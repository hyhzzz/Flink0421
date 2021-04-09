package com.atguigu.chapter05;

import com.atguigu.bean.WaterSensor;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;

import java.util.Arrays;

/**
 * @author chujian
 * @create 2021-03-20 20:30
 */
public class Flink02_Source_Collection {

    public static void main(String[] args) throws Exception {

        //TODO 获取处理执行环境  从集合读取数据
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        //设置并行度
//        env.setParallelism(1);

        //source：读取数据
        DataStreamSource<WaterSensor> sensorDS = env.fromCollection(
                Arrays.asList(
                        new WaterSensor("sensor_1", 1232323L, 41),
                        new WaterSensor("sensor_2", 12442342L, 42),
                        new WaterSensor("sensor_3", 1323323L, 43)
                )

        );

        //打印
        sensorDS.print();
        env.execute();

    }
}
