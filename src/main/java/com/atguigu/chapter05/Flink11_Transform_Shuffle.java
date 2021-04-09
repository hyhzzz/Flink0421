package com.atguigu.chapter05;

import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;

/**
 * @author chujian
 * @create 2021-03-20 20:56
 */
public class Flink11_Transform_Shuffle {
    public static void main(String[] args) throws Exception {

        //TODO 获取处理执行环境
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setParallelism(1);

        DataStreamSource<String> inputDS = env.readTextFile("input/sensor-data.log");

        inputDS.print("inpit");
        DataStream<String> resultDS = inputDS.shuffle();
        inputDS.print("shuffle");

        env.execute();
    }
}
