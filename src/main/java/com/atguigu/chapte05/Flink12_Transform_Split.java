package com.atguigu.chapte05;

import com.atguigu.bean.WaterSensor;
import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.streaming.api.collector.selector.OutputSelector;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.datastream.SplitStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;

import java.util.Arrays;

/**
 * @author chujian
 * @create 2021-03-20 20:56
 */
public class Flink12_Transform_Split {
    public static void main(String[] args) throws Exception {

        //TODO 获取处理执行环境
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setParallelism(1);


        SingleOutputStreamOperator<WaterSensor> sensorDS = env.readTextFile("D:\\idea\\wokspace\\flink0421\\input\\sensor-data.log")
                .map(new MapFunction<String, WaterSensor>() {

                    @Override
                    public WaterSensor map(String s) throws Exception {
                        String[] datas = s.split(",");

                        return new WaterSensor(datas[0], Long.valueOf(datas[1], Integer.valueOf(datas[2])));
                    }
                });


        //split
        SplitStream<WaterSensor> splitSS = sensorDS.split(new OutputSelector<WaterSensor>() {
                                                              @Override
                                                              public Iterable<String> select(WaterSensor value) {

                                                                  if (value.getVc() < 50) {

                                                                      return Arrays.asList("normal");
                                                                  } else if (value.getVc() < 80) {
                                                                      return Arrays.asList("warn");
                                                                  } else {
                                                                      return Arrays.asList("alarm");
                                                                  }
                                                              }
                                                          }
        );

        env.execute();


    }
}
