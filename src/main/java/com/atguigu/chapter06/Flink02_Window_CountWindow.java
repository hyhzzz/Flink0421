package com.atguigu.chapter06;

import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.datastream.KeyedStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;

/**
 * @author chujian
 * @create 2021-03-22 8:15
 */
public class Flink02_Window_CountWindow {
    public static void main(String[] args) throws Exception {

        // 0.创建执行环境
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setParallelism(1);

        //读数据
        DataStreamSource<String> socketDS = env.socketTextStream("localhost", 9999);


        KeyedStream<Tuple2<String, Integer>, String> dataKS = socketDS.map(new MapFunction<String, Tuple2<String, Integer>>() {
            @Override
            public Tuple2<String, Integer> map(String s) throws Exception {
                return Tuple2.of(s, 1);
            }
        })
                .keyBy(r -> r.f0);

        // TODO CountWindow
        // 根据本组数据条数 =》因为是keyby之后开的窗
        dataKS
//                .countWindow(3)// 滚动窗口
                .countWindow(3,2)//滑动窗口
                .sum(1).print();


        env.execute();

    }
}
