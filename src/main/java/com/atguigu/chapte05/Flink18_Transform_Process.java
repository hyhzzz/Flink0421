package com.atguigu.chapte05;

import com.atguigu.bean.WaterSensor;
import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.api.java.functions.KeySelector;
import org.apache.flink.api.java.tuple.Tuple3;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.datastream.KeyedStream;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.KeyedProcessFunction;
import org.apache.flink.util.Collector;

/**
 * @author chujian
 * @create 2021-03-20 20:56
 */
public class Flink18_Transform_Process {
    public static void main(String[] args) throws Exception {

        //TODO 获取处理执行环境
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setParallelism(2);

        DataStreamSource<String> inputDS = env.readTextFile("D:\\idea\\wokspace\\flink0421\\input\\sensor-data.log");


        SingleOutputStreamOperator<WaterSensor> sensorDS = inputDS.map(new Flink06_Transform_Map.MyMapFunction());


        KeyedStream<Tuple3<String, Long, Integer>, String> sensorKS = sensorDS
                .map(new MapFunction<WaterSensor, Tuple3<String, Long, Integer>>() {
                    @Override
                    public Tuple3<String, Long, Integer> map(WaterSensor value) throws Exception {
                        return new Tuple3<>(value.getId(), value.getTs(), value.getVc());
                    }
                })
                .keyBy(r -> r.f0);
        //TODO process
        // 可以获取到一些环境信息
        sensorKS.process(new KeyedProcessFunction<String, Tuple3<String, Long, Integer>, String>() {
            /**
             * 处理数据的方法，来一条处理一条
             * @param value  一条数据
             * @param  上下文
             * @param  采集器
             * @throws Exception
             */
            @Override
            public void processElement(Tuple3<String, Long, Integer> value, Context ctx, Collector<String> out) throws Exception {
                out.collect("当前key=" + ctx.getCurrentKey()+"数据="+value);
            }
        }).print("process");

        env.execute();
    }


    public static class MyKeySelector implements KeySelector<WaterSensor, String> {

        @Override
        public String getKey(WaterSensor value) throws Exception {
            return value.getId();
        }
    }
}
