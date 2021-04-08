package com.atguigu.chapte05;

import com.atguigu.bean.WaterSensor;
import org.apache.flink.api.common.serialization.SimpleStringSchema;
import org.apache.flink.api.java.functions.KeySelector;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaProducer011;

/**
 * @author chujian
 * @create 2021-03-20 20:56
 */
public class Flink19_Sink_Kakfa {
    public static void main(String[] args) throws Exception {

        //TODO 获取处理执行环境
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setParallelism(2);

        DataStreamSource<String> inputDS = env.readTextFile("D:\\idea\\wokspace\\flink0421\\input\\sensor-data.log");


        // 数据sink到kafka
        // datastream调用addsink 注意不是env来调用
        inputDS.addSink(
                new FlinkKafkaProducer011<String>(
                        "hadoop102:9092",
                        "sensor0421",
                        new SimpleStringSchema())
        );
        env.execute();
    }


    public static class MyKeySelector implements KeySelector<WaterSensor, String> {

        @Override
        public String getKey(WaterSensor value) throws Exception {
            return value.getId();
        }
    }
}
