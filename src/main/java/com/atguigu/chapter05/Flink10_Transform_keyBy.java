package com.atguigu.chapter05;

import com.atguigu.bean.WaterSensor;
import org.apache.flink.api.java.functions.KeySelector;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;

/**
 * @author chujian
 * @create 2021-03-20 20:56
 */
public class Flink10_Transform_keyBy {
    public static void main(String[] args) throws Exception {

        //TODO 获取处理执行环境
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setParallelism(2);

        DataStreamSource<String> inputDS = env.readTextFile("input/sensor-data.log");

        SingleOutputStreamOperator<WaterSensor> sensorDS = inputDS.map(new Flink06_Transform_Map.MyMapFunction());

        //Transform ：keyBy  分组
//        sensorDS.keyBy(0).print();

        //通过位置索引或字段名称返回 key的类型无法确定，所以会Tuple，后续使用key的时候，很麻烦
//        sensorDS.keyBy("id").print();

//        KeyedStream<WaterSensor, String> sensorKSByKeySelector = sensorDS.keyBy(new MyKeySelector());
//        KeyedStream<WaterSensor, String> waterSensorStringKeyedStream = sensorKSByKeySelector.keyBy(r -> r.getId());

        env.execute();
    }

    public static class MyKeySelector implements KeySelector<WaterSensor, String> {

        @Override
        public String getKey(WaterSensor value) throws Exception {
            return value.getId();
        }
    }
}
