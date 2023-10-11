package com.github.xiaoymin.llm.llm;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.http.ContentType;
import cn.hutool.http.Header;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.xiaoymin.llm.config.LLmProperties;
import com.github.xiaoymin.llm.domain.llm.ChunkResult;
import com.github.xiaoymin.llm.domain.llm.EmbeddingResult;
import com.github.xiaoymin.llm.domain.llm.ZhipuChatCompletion;
import com.github.xiaoymin.llm.domain.llm.ZhipuResult;
import com.github.xiaoymin.llm.listener.ConsoleEventSourceListener;
import com.github.xiaoymin.llm.utils.LLMUtils;
import com.google.gson.Gson;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import okhttp3.sse.EventSource;
import okhttp3.sse.EventSources;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * @author <a href="xiaoymin@foxmail.com">xiaoymin@foxmail.com</a>
 * 2023/10/06 13:35
 * @since llm_chat_java_hello
 */
@Component
@AllArgsConstructor
@Slf4j
public class ZhipuAI {

    final LLmProperties lLmProperties;

    final Gson GSON=new Gson();

    public String getApiKey(){
        String apiKey= lLmProperties.getZpKey();
        if (StrUtil.isBlank(apiKey)){
            apiKey=System.getenv("CHAT2CMD_KEY_ZP");
        }
        return apiKey;
    }

    public void chat(String prompt){
        try {
            OkHttpClient.Builder builder = new OkHttpClient.Builder()
                    .connectTimeout(20000, TimeUnit.MILLISECONDS)
                    .readTimeout(20000, TimeUnit.MILLISECONDS)
                    .writeTimeout(20000, TimeUnit.MILLISECONDS)
                    .addInterceptor(new ZhipuHeaderInterceptor(this.getApiKey()));
            OkHttpClient okHttpClient = builder.build();

            ZhipuChatCompletion zhipuChatCompletion=new ZhipuChatCompletion();
            zhipuChatCompletion.addPrompt(prompt);
            // 采样温度，控制输出的随机性，必须为正数
            // 值越大，会使输出更随机，更具创造性；值越小，输出会更加稳定或确定
            zhipuChatCompletion.setTemperature(0.7f);
            zhipuChatCompletion.setTop_p(0.7f);

            EventSource.Factory factory = EventSources.createFactory(okHttpClient);
            ObjectMapper mapper = new ObjectMapper();
            String requestBody = mapper.writeValueAsString(zhipuChatCompletion);
            Request request = new Request.Builder()
                    .url("https://open.bigmodel.cn/api/paas/v3/model-api/chatglm_std/sse-invoke")
                    .post(RequestBody.create(MediaType.parse(ContentType.JSON.getValue()), requestBody))
                    .build();
            CountDownLatch countDownLatch=new CountDownLatch(1);
            // 创建事件,控制台输出
            EventSource eventSource = factory.newEventSource(request, new ConsoleEventSourceListener(countDownLatch));
            countDownLatch.await();

        } catch (Exception e) {
            log.error("llm-chat异常：{}", e.getMessage());
        }
    }

    /**
     * 获取句子的向量
     * @param sentence 句子
     * @return 向量
     */
    public double[] sentence(String sentence){
        ChunkResult chunkResult=new ChunkResult();
        chunkResult.setContent(sentence);
        chunkResult.setChunkId(RandomUtil.randomInt());
        EmbeddingResult embeddingResult=this.embedding(chunkResult);
        return embeddingResult.getEmbedding();
    }

    /**
     * 批量
     * @param chunkResults 批量文本
     * @return 向量
     */
    public List<EmbeddingResult> embedding(List<ChunkResult> chunkResults){
        log.info("start embedding,size:{}",CollectionUtil.size(chunkResults));
        if (CollectionUtil.isEmpty(chunkResults)){
            return new ArrayList<>();
        }
        List<EmbeddingResult> embeddingResults=new ArrayList<>();
        for (ChunkResult chunkResult:chunkResults){
            embeddingResults.add(this.embedding(chunkResult));
        }
        return embeddingResults;
    }

    public EmbeddingResult embedding(ChunkResult chunkResult){
        String apiKey= this.getApiKey();
        //log.info("zp-key:{}",apiKey);
        OkHttpClient.Builder builder = new OkHttpClient.Builder()
                .connectTimeout(20000, TimeUnit.MILLISECONDS)
                .readTimeout(20000, TimeUnit.MILLISECONDS)
                .writeTimeout(20000, TimeUnit.MILLISECONDS)
                .addInterceptor(new ZhipuHeaderInterceptor(apiKey));
        OkHttpClient okHttpClient = builder.build();
        EmbeddingResult embedRequest=new EmbeddingResult();
        embedRequest.setPrompt(chunkResult.getContent());
        embedRequest.setRequestId(Objects.toString(chunkResult.getChunkId()));
        // 智谱embedding
        Request request = new Request.Builder()
                .url("https://open.bigmodel.cn/api/paas/v3/model-api/text_embedding/invoke")
                .post(RequestBody.create(MediaType.parse(ContentType.JSON.getValue()), GSON.toJson(embedRequest)))
                .build();
        try {
            Response response= okHttpClient.newCall(request).execute();
            String result=response.body().string();
            ZhipuResult zhipuResult= GSON.fromJson(result, ZhipuResult.class);
            EmbeddingResult ret= zhipuResult.getData();
            ret.setPrompt(embedRequest.getPrompt());
            ret.setRequestId(embedRequest.getRequestId());
            return  ret;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    @AllArgsConstructor
    private static class ZhipuHeaderInterceptor implements Interceptor {

        final String apiKey;

        @NotNull
        @Override
        public Response intercept(@NotNull Chain chain) throws IOException {
            Request original = chain.request();
            String authorization=LLMUtils.gen(apiKey,60);
            //log.info("authorization:{}",authorization);
            Request request = original.newBuilder()
                    .header(Header.AUTHORIZATION.getValue(), authorization)
                    .header(Header.CONTENT_TYPE.getValue(), ContentType.JSON.getValue())
                    .method(original.method(), original.body())
                    .build();
            return chain.proceed(request);
        }
    }
}
