package com.atguigu.chapter06;

import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.datastream.KeyedStream;
import org.apache.flink.streaming.api.datastream.WindowedStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.windowing.time.Time;
import org.apache.flink.streaming.api.windowing.windows.TimeWindow;

/**
 * @author chujian
 * @create 2021-03-22 8:15
 */
public class Flink01_Window_TimeWidow {
    public static void main(String[] args) throws Exception {

        // 0.创建执行环境
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setParallelism(1);

        //读数据
        DataStreamSource<String> socketDS = env.socketTextStream("localhost", 9999);


        //TODO 开窗
        // datastream可以直接调用开窗的方法，但是都带all
        // 这种情况下 所有数据 不分组 都在窗口
//        socketDS.windowAll();
//        socketDS.connect();
//        socketDS.timeWindowAll();

        KeyedStream<Tuple2<String, Integer>, String> dataKS = socketDS.map(new MapFunction<String, Tuple2<String, Integer>>() {
            @Override
            public Tuple2<String, Integer> map(String s) throws Exception {
                return Tuple2.of(s, 1);
            }
        })
                .keyBy(r -> r.f0);

        WindowedStream<Tuple2<String, Integer>, String, TimeWindow> dataWS = dataKS
                .timeWindow(Time.seconds(5)); //滚动窗口
//                .window(TumblingAlignedProcessingTimeWindows.of(Time.seconds(5))); //和上面的那个效果一样
//                .timeWindow(Time.seconds(5), Time.seconds(2));//滑动窗口 第一个是窗口长度，第二个是滑动步长
//                .window(ProcessingTimeSessionWindows.withGap(Time.seconds(3))); //会话窗口
        dataWS.sum(1).print();
        env.execute();

    }
}
