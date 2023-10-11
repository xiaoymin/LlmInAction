package com.github.xiaoymin.llm.command;

import com.github.xiaoymin.llm.compoents.TxtChunk;
import com.github.xiaoymin.llm.compoents.VectorStorage;
import com.github.xiaoymin.llm.domain.llm.ChunkResult;
import com.github.xiaoymin.llm.domain.llm.EmbeddingResult;
import com.github.xiaoymin.llm.llm.ZhipuAI;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;

import java.util.List;

/**
 * @author <a href="xiaoymin@foxmail.com">xiaoymin@foxmail.com</a>
 * 2023/10/06 12:50
 * @since llm_chat_java_hello
 */
@Slf4j
@AllArgsConstructor
@ShellComponent
public class AddTxtCommand {

    final TxtChunk txtChunk;
    final VectorStorage vectorStorage;
    final ZhipuAI zhipuAI;

    @ShellMethod(value = "add local txt data")
    public String add(String doc){
        log.info("start add doc.");
        // 加载
        List<ChunkResult> chunkResults= txtChunk.chunk(doc);
        // embedding
        List<EmbeddingResult> embeddingResults=zhipuAI.embedding(chunkResults);
        // store vector
        String collection= vectorStorage.getCollectionName();
        vectorStorage.store(collection,embeddingResults);
        log.info("finished");
        return "finished docId:{}"+doc;
    }
}
