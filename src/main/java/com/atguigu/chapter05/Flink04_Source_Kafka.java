package com.atguigu.chapter05;

import org.apache.flink.api.common.serialization.SimpleStringSchema;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaConsumer011;

import java.util.Properties;

/**
 * @author chujian
 * @create 2021-03-20 20:56
 */
public class Flink04_Source_Kafka {
    public static void main(String[] args) throws Exception {

        //TODO 获取处理执行环境  无界流 从Kafka读取数据
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setParallelism(1);

        Properties properties = new Properties();
        properties.setProperty("bootstrap.servers", "hadoop102:9092");
        properties.setProperty("group.id", "consumer-group");
        properties.setProperty("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        properties.setProperty("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        properties.setProperty("auto.offset.reset", "latest");

        DataStreamSource<String> kafkaDS = env.addSource(
                new FlinkKafkaConsumer011<String>(
                        "sensor0421",
                        new SimpleStringSchema(),
                        properties
                )
        );
        kafkaDS.print();

        env.execute();
    }
}
