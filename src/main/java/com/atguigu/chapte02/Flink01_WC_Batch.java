package com.atguigu.chapte02;

import org.apache.flink.api.common.functions.FlatMapFunction;
import org.apache.flink.api.java.ExecutionEnvironment;
import org.apache.flink.api.java.operators.AggregateOperator;
import org.apache.flink.api.java.operators.DataSource;
import org.apache.flink.api.java.operators.FlatMapOperator;
import org.apache.flink.api.java.operators.UnsortedGrouping;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.util.Collector;

/**
 * @author chujian
 * @create 2021-03-20 8:05
 */
public class Flink01_WC_Batch {

    //TODO 批处理 WordCount
    public static void main(String[] args) throws Exception {
        //创建执行环境
        ExecutionEnvironment env = ExecutionEnvironment.getExecutionEnvironment();

        //从文件读取数据 (有界)
        DataSource<String> fileDS = env.readTextFile("D:\\idea\\wokspace\\flink0421\\input\\word.txt");

        //处理数据
        //切分 转换成二元组（word,1）
        FlatMapOperator<String, Tuple2<String, Integer>> wordAndOneTuple = fileDS.flatMap(new MyFlattMapFunction());

        //按照word分组
        UnsortedGrouping<Tuple2<String, Integer>> wordAndOneGroup = wordAndOneTuple.groupBy(0);

        //按照分组聚合
        AggregateOperator<Tuple2<String, Integer>> result = wordAndOneGroup.sum(1);

        //输出数据
        result.print();

        //启动  批处理不需要

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
