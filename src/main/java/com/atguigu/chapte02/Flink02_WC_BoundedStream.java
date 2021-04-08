package com.atguigu.chapte02;

import org.apache.flink.api.common.functions.FlatMapFunction;
import org.apache.flink.api.java.tuple.Tuple;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.datastream.KeyedStream;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.util.Collector;

/**
 * @author chujian
 * @create 2021-03-20 8:26
 */
public class Flink02_WC_BoundedStream {
    public static void main(String[] args) throws Exception {
        //TODO 有界流处理  文件
        //创建执行环境
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();

        //读取数据
        DataStreamSource<String> fileDS = env.readTextFile("input/word.txt");

        //处理数据
        //切分、转换成二元祖
        SingleOutputStreamOperator<Tuple2<String, Integer>> wordAndOneTuple = fileDS.flatMap(new MyFlattMapFunction());

        //按照wird分组
        KeyedStream<Tuple2<String, Integer>, Tuple> wordAndOneKS = wordAndOneTuple.keyBy(0);

        //按照分组聚合
        SingleOutputStreamOperator<Tuple2<String, Integer>> result = wordAndOneKS.sum(1);

        //输出
        result.print();

        //执行任务
        env.execute();
    }

    public static class MyFlattMapFunction implements FlatMapFunction<String, Tuple2<String, Integer>> {

        @Override
        public void flatMap(String value, Collector<Tuple2<String, Integer>> collector) throws Exception {
            String[] words = value.split(" ");
            //转换成二元组
            for (String word : words) {
                // collect 往下游发送数据
                Tuple2<String, Integer> tuple = new Tuple2<>(word, 1);
                collector.collect(tuple);
            }
        }
    }
}
