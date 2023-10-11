package com.github.xiaoymin.llm.domain.llm;

import lombok.Getter;
import lombok.Setter;


/**
 * @author <a href="xiaoymin@foxmail.com">xiaoymin@foxmail.com</a>
 * 2023/10/06 13:36
 * @since llm_chat_java_hello
 */
@Getter
@Setter
public class EmbeddingResult {

    /**
     * 原始文本内容
     */
    private String prompt;
    /**
     * embedding的处理结果，返回向量化表征的数组，数组长度为1024
     */
    private double[] embedding;
    /**
     * 用户在客户端请求时提交的任务编号或者平台生成的任务编号
     */
    private String requestId;
    /**
     * 智谱AI开放平台生成的任务订单号，调用请求结果接口时请使用此订单号
     */
    private String taskId;
    /**
     * 处理状态，PROCESSING（处理中），SUCCESS（成功），FAIL（失败）
     * 注：处理中状态需通过查询获取结果
     */
    private String taskStatus;
}
