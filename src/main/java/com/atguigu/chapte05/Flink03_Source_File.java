package com.atguigu.chapte05;

import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;

/**
 * @author chujian
 * @create 2021-03-20 20:30
 */
public class Flink03_Source_File {

    public static void main(String[] args) throws Exception {

        //TODO 获取处理执行环境 从文件读取数据
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();

        DataStreamSource<String> filesDS = env.readTextFile("D:\\idea\\wokspace\\flink0421\\input\\word.txt");

        //打印
        filesDS.print();
        env.execute();

    }
}
