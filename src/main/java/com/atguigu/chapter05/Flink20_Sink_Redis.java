package com.atguigu.chapter05;

import com.atguigu.bean.WaterSensor;
import org.apache.flink.api.java.functions.KeySelector;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.connectors.redis.RedisSink;
import org.apache.flink.streaming.connectors.redis.common.config.FlinkJedisPoolConfig;
import org.apache.flink.streaming.connectors.redis.common.mapper.RedisCommand;
import org.apache.flink.streaming.connectors.redis.common.mapper.RedisCommandDescription;
import org.apache.flink.streaming.connectors.redis.common.mapper.RedisMapper;

/**
 * @author chujian
 * @create 2021-03-20 20:56
 */
public class Flink20_Sink_Redis {
    public static void main(String[] args) throws Exception {

        //TODO 获取处理执行环境
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setParallelism(2);

        DataStreamSource<String> inputDS = env.readTextFile("input/sensor-data.log");


        // TODO 数据Sink 到Redis
        FlinkJedisPoolConfig jedisConfig = new FlinkJedisPoolConfig.Builder()
                .setHost("hadoop102")
                .setPort(6379)
                .build();
        inputDS.addSink(
                new RedisSink<String>(jedisConfig,
                        new RedisMapper<String>() {
                            //redis的命令 :key是最外层的key
                            @Override
                            public RedisCommandDescription getCommandDescription() {
                                return new RedisCommandDescription(RedisCommand.HSET, "sensor0421");
                            }

                            //hash类型：这个指定的是hash的key
                            @Override
                            public String getKeyFromData(String data) {
                                String[] datas = data.split(",");
                                return datas[1];//把什么当做key
                            }
                            //hash类型：这个指定的是hash的value
                            @Override
                            public String getValueFromData(String data) {
                                String[] datas = data.split(",");
                                return datas[2];
                            }
                        })
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
