package com.atguigu.chapter05;

import org.apache.flink.api.common.functions.FilterFunction;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;

import java.util.Arrays;

/**
 * @author chujian
 * @create 2021-03-20 20:56
 */
public class Flink09_Transform_filter {
    public static void main(String[] args) throws Exception {

        //TODO 获取处理执行环境
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setParallelism(2);

        DataStreamSource<Integer> inputDS = env.fromCollection(Arrays.asList(1, 2, 4, 5, 6));

        //  Transform  filter => 为true 保留
        inputDS.filter(new MyFilterFunction()).print();

        env.execute();
    }

    public static class MyFilterFunction implements FilterFunction<Integer> {

        @Override
        public boolean filter(Integer value) throws Exception {
            return value % 2 == 0;
        }
    }
}
