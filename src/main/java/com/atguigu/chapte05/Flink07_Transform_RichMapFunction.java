package com.atguigu.chapte05;

import com.atguigu.bean.WaterSensor;
import org.apache.flink.api.common.functions.RichMapFunction;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;

/**
 * @author chujian
 * @create 2021-03-20 20:56
 */
public class Flink07_Transform_RichMapFunction {
    public static void main(String[] args) throws Exception {

        //TODO 获取处理执行环境  从文件读取数据
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setParallelism(2);

//        DataStreamSource<String> inputDS = env.readTextFile("D:\\idea\\wokspace\\flink0421\\input\\sensor-data.log");
        DataStreamSource<String> inputDS = env.socketTextStream("hadoop102", 8888);

        //Transform ：Map转换成实体对象
        SingleOutputStreamOperator<WaterSensor> sensorDS = inputDS.map(new MyRichFunction());

        sensorDS.print();

        env.execute();
    }

    /**
     * 继承RichMapFunction 指定输入的类型，返回的类型
     * 重写map方法
     * 提供了 open close方法
     */
    public static class MyRichFunction extends RichMapFunction<String, WaterSensor> {

        @Override
        public WaterSensor map(String value) throws Exception {

            String[] datas = value.split(",");
            return new WaterSensor(datas[0], Long.valueOf(datas[1]), Integer.valueOf(datas[2]));
        }

        @Override
        public void open(Configuration parameters) throws Exception {
            System.out.println("open...");
        }

        @Override
        public void close() throws Exception {
            System.out.println("close...");
        }
    }
}
