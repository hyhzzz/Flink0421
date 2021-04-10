package com.atguigu.chapter06;

import com.atguigu.bean.UserBehavior;
import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.streaming.api.TimeCharacteristic;
import org.apache.flink.streaming.api.datastream.KeyedStream;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.datastream.WindowedStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.timestamps.AscendingTimestampExtractor;
import org.apache.flink.streaming.api.windowing.time.Time;
import org.apache.flink.streaming.api.windowing.windows.TimeWindow;

/**
 * @author chujian
 * @create 2021-03-23 10:55
 */
public class Flink25_Case_HotItemsAnalysis {
    public static void main(String[] args) throws Exception {

        //创建执行环境
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setParallelism(1);
        env.setStreamTimeCharacteristic(TimeCharacteristic.EventTime);

        //读取数据
        SingleOutputStreamOperator<UserBehavior> userBehaviorDS = env.readTextFile("input/UserBehavior.csv")
                .map(new MapFunction<String, UserBehavior>() {
                    @Override
                    public UserBehavior map(String s) throws Exception {
                        String[] datas = s.split(",");
                        return new UserBehavior(Long.valueOf(datas[0]), Long.valueOf(datas[1]), Integer.valueOf(datas[2]), datas[3], Long.valueOf(datas[4]));
                    }
                })
                .assignTimestampsAndWatermarks(
                        new AscendingTimestampExtractor<UserBehavior>() {
                            @Override
                            public long extractAscendingTimestamp(UserBehavior element) {
                                return element.getTimestamp() * 1000L;
                            }
                        }
                );

        //处理数据
        //过滤出pv行为
        SingleOutputStreamOperator<UserBehavior> userBehaviorFilter = userBehaviorDS.filter(data -> "pv".equals(data.getBehavior()));

        //按照 统计维度 分组 ：商品
        KeyedStream<Tuple2<Long, Integer>, Long> userBehaiorKS = userBehaviorFilter.map(new MapFunction<UserBehavior, Tuple2<Long, Integer>>() {
            @Override
            public Tuple2<Long, Integer> map(UserBehavior value) throws Exception {
                return Tuple2.of(value.getItemId(), 1);
            }
        }).keyBy(data -> data.f0);
//        KeyedStream<UserBehavior, Long> userBehaiorKS = userBehaviorFilter.keyBy(data -> data.getItemId());

        //开窗 每五分钟输出最近一小时 =》滑动窗口 长度1小时 步长五分钟
//        WindowedStream<UserBehavior, Long, TimeWindow> userBehaviorWS = userBehaiorKS.timeWindow(Time.hours(1), Time.minutes(5));
        WindowedStream<Tuple2<Long, Integer>, Long, TimeWindow> userBehaviorWS = userBehaiorKS.timeWindow(Time.hours(1), Time.minutes(5));

        //求和 统计 每个商品被点击多少次
        SingleOutputStreamOperator<Tuple2<Long, Integer>> aggregate = userBehaviorWS.sum(1);

        aggregate.print();
        env.execute();
    }
}
