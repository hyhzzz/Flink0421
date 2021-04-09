package com.atguigu.chapter05;

import com.atguigu.bean.WaterSensor;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.source.SourceFunction;

import java.util.Random;

/**
 * @author chujian
 * @create 2021-03-20 20:56
 */
public class Flink05_Source_MySource {
    public static void main(String[] args) throws Exception {

        //TODO 获取处理执行环境 source  从自义定数据源读取
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();

        DataStreamSource<WaterSensor> inputDS = env.addSource(new MySourceFunction());

        inputDS.print();

        env.execute();
    }

    /**
     * 自定义数据源：
     * 1.实现SourceFunction
     * 2.指定输出的类型
     * 3.重写两个方法  run   cancel
     */
    public static class MySourceFunction implements SourceFunction<WaterSensor> {
        //定义一个标志位 控制数据的产生
        private boolean flag = true;

        @Override
        public void run(SourceContext<WaterSensor> sourceContext) throws Exception {
            Random random = new Random();
            while (true) {
                sourceContext.collect(
                        new WaterSensor(
                                "sensor_" + random.nextInt(3),
                                System.currentTimeMillis(),
                                random.nextInt(10) + 40
                        )
                );
                Thread.sleep(2000L);
            }
        }

        @Override
        public void cancel() {
            this.flag = false;
        }
    }
}
