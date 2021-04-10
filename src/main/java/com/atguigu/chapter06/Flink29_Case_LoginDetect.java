package com.atguigu.chapter06;

import com.atguigu.bean.LoginEvent;
import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.api.common.state.ValueState;
import org.apache.flink.api.common.state.ValueStateDescriptor;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.TimeCharacteristic;
import org.apache.flink.streaming.api.datastream.KeyedStream;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.KeyedProcessFunction;
import org.apache.flink.streaming.api.functions.timestamps.BoundedOutOfOrdernessTimestampExtractor;
import org.apache.flink.streaming.api.windowing.time.Time;
import org.apache.flink.util.Collector;

/**
 * @author chujian
 * @create 2021-03-23 14:01
 */
public class Flink29_Case_LoginDetect {
    public static void main(String[] args) throws Exception {

        //创建执行环境
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();

        env.setParallelism(1);
        env.setStreamTimeCharacteristic(TimeCharacteristic.EventTime);

        //读取数据
        SingleOutputStreamOperator<LoginEvent> loginDS = env.readTextFile("input/LoginLog.csv")
                .map(new MapFunction<String, LoginEvent>() {
                    @Override
                    public LoginEvent map(String s) throws Exception {
                        String[] datas = s.split(",");
                        return new LoginEvent(
                                Long.valueOf(datas[0]),
                                datas[1],
                                datas[2],
                                Long.valueOf(datas[3])


                        );
                    }
                })
                .assignTimestampsAndWatermarks(new BoundedOutOfOrdernessTimestampExtractor<LoginEvent>(Time.seconds(10)) {
                    @Override
                    public long extractTimestamp(LoginEvent loginEvent) {
                        return loginEvent.getEventTime() * 1000L;
                    }
                });


        //处理数据
        //过滤
        SingleOutputStreamOperator<LoginEvent> filterDS = loginDS.filter(data -> "fail".equals(data.getEventType()));

        //按照统计维度分组
        KeyedStream<LoginEvent, Long> loginKS = filterDS.keyBy(data -> data.getUserId());

        SingleOutputStreamOperator<String> resultDS = loginKS.process(new loginFailDetect());

        resultDS.print();

        env.execute();
    }

    public static class loginFailDetect extends KeyedProcessFunction<Long, LoginEvent, String> {

        private ValueState<LoginEvent> failState;

        @Override
        public void open(Configuration parameters) throws Exception {
            failState = getRuntimeContext().getState(new ValueStateDescriptor<LoginEvent>("FileSystem", LoginEvent.class));
        }

        @Override
        public void processElement(LoginEvent value, Context ctx, Collector<String> out) throws Exception {
            //判断当前是第几次失败

            if (failState.value() == null) {
                //说明当前数据是第一次失败 =》 保存起来
                failState.update(value);
            } else {
                if (value.getEventTime() - failState.value().getEventTime() < 2) {
                    //说明当前数据不是第一次失败 =》 告警
                    out.collect("用户" + value.getUserId() + "在2s内登录失败2次，可能为恶意登录");
                }
                //不管是否到警告条件，都要吧当前的失败保存起来，后面还可以继续判断
                failState.update(value);
            }
        }
    }
}
