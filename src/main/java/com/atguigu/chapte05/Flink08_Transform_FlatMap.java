package com.atguigu.chapte05;

import org.apache.flink.api.common.functions.FlatMapFunction;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.util.Collector;

import java.util.Arrays;
import java.util.List;

/**
 * @author chujian
 * @create 2021-03-20 20:56
 */
public class Flink08_Transform_FlatMap {
    public static void main(String[] args) throws Exception {

        //TODO 获取处理执行环境
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setParallelism(2);

        DataStreamSource<List<Integer>> inputDS = env.fromCollection(
                Arrays.asList(
                        Arrays.asList(1, 2, 3, 4, 5),
                        Arrays.asList(5, 6, 7, 8, 9)
                )
        );

        //  Transform  FlatMap

        inputDS.flatMap(new FlatMapFunction<List<Integer>, Integer>() {
            @Override
            public void flatMap(List<Integer> value, Collector<Integer> out) throws Exception {

                for (Integer number : value) {
                    out.collect(number+10);
                }
            }
        }).print();
        env.execute();
    }
}
