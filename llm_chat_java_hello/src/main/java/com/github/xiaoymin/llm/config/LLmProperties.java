package com.github.xiaoymin.llm.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @author <a href="xiaoymin@foxmail.com">xiaoymin@foxmail.com</a>
 * 2023/10/06 13:38
 * @since llm_chat_java_hello
 */
@Data
@Component
@ConfigurationProperties(prefix = "llm")
public class LLmProperties {
    /**
     * 智谱AI的开发密钥，https://open.bigmodel.cn/dev/api#text_embedding
     * 注册智谱AI开放平台获取
     */
    private String zpKey;
}
