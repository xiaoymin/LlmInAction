package com.github.xiaoymin.llm.compoents;

import cn.hutool.core.io.IoUtil;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.StrUtil;
import com.github.xiaoymin.llm.domain.llm.ChunkResult;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author <a href="xiaoymin@foxmail.com">xiaoymin@foxmail.com</a>
 * 2023/10/06 12:48
 * @since llm_chat_java_hello
 */
@Slf4j
@Component
@AllArgsConstructor
public class TxtChunk {

    public List<ChunkResult> chunk(String docId){
        String path="data/"+docId+".txt";
        log.info("start chunk---> docId:{},path:{}",docId,path);
        ClassPathResource classPathResource=new ClassPathResource(path);
        try {
            String txt=IoUtil.read(classPathResource.getInputStream(), StandardCharsets.UTF_8);
            //按固定字数分割,256
            String[] lines=StrUtil.split(txt,256);
            log.info("chunk size:{}", ArrayUtil.length(lines));
            List<ChunkResult> results=new ArrayList<>();
            AtomicInteger atomicInteger=new AtomicInteger(0);
            for (String line:lines){
                ChunkResult chunkResult=new ChunkResult();
                chunkResult.setDocId(docId);
                chunkResult.setContent(line);
                chunkResult.setChunkId(atomicInteger.incrementAndGet());
                results.add(chunkResult);
            }
            return results;
        } catch (IOException e) {
            log.error(e.getMessage());
        }
        return new ArrayList<>();
    }

}
