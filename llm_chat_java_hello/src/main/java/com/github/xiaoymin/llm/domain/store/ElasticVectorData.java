package com.github.xiaoymin.llm.domain.store;

import lombok.Data;

/**
 * @author <a href="xiaoymin@foxmail.com">xiaoymin@foxmail.com</a>
 * 2023/10/06 14:40
 * @since llm_chat_java_hello
 */
@Data
public class ElasticVectorData {

    private String chunkId;
    private String content;
    private String docId;
    private double[] vector;

}
