package com.atguigu.chapter05;

import org.apache.flink.api.common.functions.FlatMapFunction;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.util.Collector;

public class Flink25_Case_PVByFlatmap {
    public static void main(String[] args) throws Exception {

        // 0.创建执行环境
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setParallelism(1);

        // 1.从文件读取数据、转换成 bean对象
        env
                .readTextFile("input/UserBehavior.csv")
                .flatMap(new FlatMapFunction<String, Tuple2<String, Integer>>() {
                    @Override
                    public void flatMap(String value, Collector<Tuple2<String, Integer>> collector) throws Exception {
                        String[] datas = value.split(",");
                        if ("pv".equals(datas[3])) {
                            collector.collect(Tuple2.of("pv", 1));
                        }
                    }
                })
                .keyBy(0)
                .sum(1)
                .print("pv by flatmap");


        env.execute();
    }

}
