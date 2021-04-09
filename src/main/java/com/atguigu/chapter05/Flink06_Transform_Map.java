package com.atguigu.chapter05;

import com.atguigu.bean.WaterSensor;
import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;

/**
 * @author chujian
 * @create 2021-03-20 20:56
 */
public class Flink06_Transform_Map {
    public static void main(String[] args) throws Exception {

        //TODO 获取处理执行环境  从文件读取数据
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setParallelism(1);

        DataStreamSource<String> inputDS = env.readTextFile("input/sensor-data.log");

        //Transform ：Map转换成实体对象
        SingleOutputStreamOperator<WaterSensor> sensorDS = inputDS.map(new MyMapFunction());

        sensorDS.print();

        env.execute();
    }

    /**
     * 实现MapFunction 指定输入的类型，返回的类型
     * 重写map方法
     */
    public static class MyMapFunction implements MapFunction<String, WaterSensor> {

        @Override
        public WaterSensor map(String value) throws Exception {

            String[] datas = value.split(",");
            return new WaterSensor(datas[0], Long.valueOf(datas[1]), Integer.valueOf(datas[2]));
        }
    }
}
