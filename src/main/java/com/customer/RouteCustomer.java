package com.customer;

import com.rabbitmq.client.*;

import java.io.IOException;

public class RouteCustomer {

    // 交换器名称
    private static final String EXCHANGE = "logs";
    // 路由关键字
    private static final String[] routingKeys = new String[]{"error" ,"warning"};

    public static void main(String[] args) throws Exception {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();
        //声明交换器
        channel.exchangeDeclare(EXCHANGE, "direct");
        //获取匿名队列名称
        String queueName = channel.queueDeclare().getQueue();

        //根据路由关键字进行绑定
        for (String routingKey : routingKeys) {
            channel.queueBind(queueName, EXCHANGE, routingKey);
            System.out.println("ReceiveLogsDirect1 exchange:" + EXCHANGE + "," +
                    " queue:" + queueName + ", BindRoutingKey:" + routingKey);
        }
        System.out.println("ReceiveLogsDirect1  Waiting for messages");
        Consumer consumer = new DefaultConsumer(channel) {
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                String message = new String(body, "UTF-8");
                System.out.println("ReceiveLogsDirect1 Received '" + envelope.getRoutingKey() + "':'" + message + "'");
            }
        };
        channel.basicConsume(queueName, true, consumer);
    }
}
