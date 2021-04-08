package com.atguigu.chapte05;

import com.atguigu.bean.OrderEvent;
import com.atguigu.bean.TxEvent;
import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.streaming.api.datastream.ConnectedStreams;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.co.CoProcessFunction;
import org.apache.flink.util.Collector;

import java.util.HashMap;
import java.util.Map;

/**
 * 不同省份 不同广告的点击量实时统计
 *
 * @author cjp
 * @version 1.0
 * @date 2020/9/18 16:32
 */
public class Flink29_Case_AdClickAnalysis {
    public static void main(String[] args) throws Exception {
        // 0 执行环境
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setParallelism(1);


        //读取数据 转成bean对象
        SingleOutputStreamOperator<OrderEvent> orderDS = env.readTextFile("input/OrderLog.csv")
                .map(new MapFunction<String, OrderEvent>() {
                    @Override
                    public OrderEvent map(String value) throws Exception {
                        String[] datas = value.split(",");
                        return new OrderEvent(
                                Long.valueOf(datas[0]),
                                datas[1],
                                datas[2],
                                Long.valueOf(datas[3])
                        );
                    }
                });

        SingleOutputStreamOperator<TxEvent> TxDS = env.readTextFile("input/ReceiptLog.csv")
                .map(new MapFunction<String, TxEvent>() {
                    @Override
                    public TxEvent map(String value) throws Exception {
                        String[] datas = value.split(",");
                        return new TxEvent(
                                datas[0],
                                datas[1],
                                Long.valueOf(datas[2])
                        );
                    }
                });

        //实时对账
        //两条流 connect 起来 ，通过交易吗txid 做一个匹配 匹配上就是对账成功
        // 对于同一笔交易来说 的业务系统和交易系统的数据 哪个先来是不一定的
        ConnectedStreams<OrderEvent, TxEvent> orderTxCS = orderDS.connect(TxDS);
        //使用process进行处理
        SingleOutputStreamOperator<String> resultDS = orderTxCS.process(new CoProcessFunction<OrderEvent, TxEvent, String>() {
            //用来存放交易系统的数据
            private Map<String, TxEvent> txMap = new HashMap<>();
            //用来存放 业务系统的数据
            private Map<String, OrderEvent> orderMap = new HashMap<>();

            /**
             * 处理业务系统的数据，来一条处理一条
             * @param value
             * @param ctx
             * @param out
             * @throws Exception
             */
            @Override
            public void processElement1(OrderEvent value, Context ctx, Collector<String> out) throws Exception {
                //进入这个方法，说明来的数据是业务系统的数据
                //判断 交易数据来了没有
                //通过交易吗查询 保存的交易数据 =》如果不为空说明交易数据已经来了
                TxEvent txEvent = txMap.get(value.getTxId());
                if (txEvent == null) {
                    //说交易数据没来
                    orderMap.put(value.getTxId(), value);
                } else {
                    //说明交易数据来了
                    out.collect("订单" + value.getOrderId() + "对账成功");
                }
            }

            /**
             * 处理交易系统的数据 来一条处理一条
             * @param value
             * @param ctx
             * @param out
             * @throws Exception
             */
            @Override
            public void processElement2(TxEvent value, Context ctx, Collector<String> out) throws Exception {
                OrderEvent orderEvent = this.orderMap.get(value.getTxId());
                if (orderEvent == null) {
                    //说明业务数据没来 = >吧自己临时保存起来\
                    txMap.put(value.getTxId(), value);
                } else {
                    //说明业务数据来了 => 对账成功
                    out.collect("订单" + orderEvent.getOrderId() + "对账成功");
                    //对账成功，将保存的数据删除
                    txMap.remove(value.getTxId());
                }
            }
        });

        resultDS.print();
        env.execute();
    }


}
