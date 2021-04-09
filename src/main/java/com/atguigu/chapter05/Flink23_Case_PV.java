package com.atguigu.chapter05;

import com.atguigu.bean.UserBehavior;
import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.api.java.tuple.Tuple;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.streaming.api.datastream.KeyedStream;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;

/**
 * @author chujian
 * @create 2021-03-21 16:57
 */
public class Flink23_Case_PV {
    public static void main(String[] args) throws Exception {
        // 0.创建执行环境
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setParallelism(1);

        // 1.读取数据 转换成bena对象
        SingleOutputStreamOperator<UserBehavior> userBehaviorDS = env
                .readTextFile("input/UserBehavior.csv")
                .map(new MapFunction<String, UserBehavior>() {
                    @Override
                    public UserBehavior map(String value) throws Exception {
                        String[] datas = value.split(",");
                        return new UserBehavior(
                                Long.valueOf(datas[0]),
                                Long.valueOf(datas[1]),
                                Integer.valueOf(datas[2]),
                                datas[3],
                                Long.valueOf(datas[4])
                        );
                    }
                });


        //处理数据
        //过滤出 PV行为
        SingleOutputStreamOperator<UserBehavior> userBehaviorFilter = userBehaviorDS.filter(data -> "pv".equals(data.getBehavior()));
        
        //转换成二元组
        SingleOutputStreamOperator<Tuple2<String, Integer>> pvAndOneTuple2 = userBehaviorFilter.map(new MapFunction<UserBehavior, Tuple2<String, Integer>>() {
            @Override
            public Tuple2<String, Integer> map(UserBehavior userBehavior) throws Exception {

                return Tuple2.of("pv", 1);
            }
        });

        //按照第一个位置的元素分组  聚合算子只能在分组之后调用，也就是在keyedstreaming才能调用sum
        KeyedStream<Tuple2<String, Integer>, Tuple> pvAndOneKS = pvAndOneTuple2.keyBy(0);

        //求和
        SingleOutputStreamOperator<Tuple2<String, Integer>> pvDS = pvAndOneKS.sum(1);
        pvDS.print();

        env.execute();

    }
}
