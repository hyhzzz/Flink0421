package com.atguigu.chapte05;

import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;

import java.util.Arrays;

/**
 * TODO
 *
 * @author cjp
 * @version 1.0
 * @date 2020/9/16 16:55
 */
public class Flink15_Transform_union {
    public static void main(String[] args) throws Exception {
        // 0.创建执行环境
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setParallelism(1);

        // 1.从文件读取数据
        DataStreamSource<Integer> numDS1 = env.fromCollection(Arrays.asList(1, 22, 3, 4));

        // 再获取一条流
        DataStreamSource<Integer> numDS = env.fromCollection(Arrays.asList(1, 2, 33, 4));


        //TODO  Union 连接流
        // 连接流的数据要相同
        DataStream<Integer> unionDS = numDS.union(numDS1);
        unionDS.map(new MapFunction<Integer, Integer>() {
            @Override
            public Integer map(Integer value) throws Exception {

                return value *10;
            }
        }).print("union");


        env.execute();
    }
}
