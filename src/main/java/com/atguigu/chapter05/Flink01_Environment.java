package com.atguigu.chapter05;

import org.apache.flink.api.java.ExecutionEnvironment;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;

/**
 * @author chujian
 * @create 2021-03-20 20:30
 */
public class Flink01_Environment {

    public static void main(String[] args) {

        //TODO 获取批处理 执行环境
        ExecutionEnvironment benv = ExecutionEnvironment.getExecutionEnvironment();
        //TODO 获取处理执行环境
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();

    }
}
