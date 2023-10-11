package com.github.xiaoymin.llm.domain.llm;

import lombok.Data;

/**
 * @author <a href="xiaoymin@foxmail.com">xiaoymin@foxmail.com</a>
 * 2023/10/06 14:11
 * @since llm_chat_java_hello
 */
@Data
public class ZhipuResult {
    private int code;
    private String msg;
    private boolean success;
    private EmbeddingResult data;
}
