package cn.tarena.rabbitmq;

import org.junit.Before;
import org.junit.Test;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.QueueingConsumer;

/**
 * 测试路由模式
 * 特点是根据路由key去决定消息的消费
 * @author ysq
 *
 */
public class RoutingDemo {
	
	private Connection connection;
	


@Before 
public void initConnection() throws Exception{
	ConnectionFactory factory=new ConnectionFactory();
	factory.setHost("192.168.234.131");
	//5672是MQ的连接端口，注意，不能写15672,因为是MQ控制台的访问端口
	factory.setPort(5672);
	factory.setUsername("jtadmin");
	factory.setPassword("123456");
	factory.setVirtualHost("/jt");
	
	connection=factory.newConnection();

}
//路由模式的生产者
@Test
public void routing_producer() throws Exception{
	Channel channel=connection.createChannel();
	String EXCHANGER_NAME="E2";
	//声明交换机
	//①参：交换机的名字
	//②参：定义类型，fanout-发布订阅模式  direct-路由模式  topic-主题模式
	channel.exchangeDeclare(EXCHANGER_NAME,"direct");
	for(int i=0;i<100;i++){
		String message=i+"";
		
		String Routing_Key="1706";
		//指定路由key
		channel.basicPublish(EXCHANGER_NAME,Routing_Key,null,message.getBytes());
	}
	while(true);
	
}
@Test
public void routing_consumer_1() throws Exception{
	Channel channel=connection.createChannel();
	//要找对需要的交换机名字
	String EXCHANGER_NAME="E2";
	//为消费者1分配的队列名
	String QUEUE_NAME="QC3";
	//声明交换机
	channel.exchangeDeclare(EXCHANGER_NAME,"direct");
	//声明消费者1的队列
	channel.queueDeclare(QUEUE_NAME, false,false,false,null);
	
	//注意：别忘把交换机和队列绑定
	//①参：队列名
	//②参：交换机名 注意顺序
	//③参：路由key
	String routing_Key="1706";
	channel.queueBind(QUEUE_NAME,EXCHANGER_NAME,routing_Key);
	
	channel.basicQos(1);
	
	QueueingConsumer consumer=new QueueingConsumer(channel);
	channel.basicConsume(QUEUE_NAME,false,consumer);
	
	while(true){
		QueueingConsumer.Delivery delivery=consumer.nextDelivery();
		String message=new String(delivery.getBody());
		System.out.println("消费者1:"+message);
		channel.basicAck(delivery.getEnvelope().getDeliveryTag(),false);
	}
	
}
@Test
public void routing_consumer_2() throws Exception{
	Channel channel=connection.createChannel();
	//要找对需要的交换机名字
	String EXCHANGER_NAME="E2";
	//为消费者1分配的队列名
	String QUEUE_NAME="QC4";
	//声明交换机
	channel.exchangeDeclare(EXCHANGER_NAME,"direct");
	//声明消费者1的队列
	channel.queueDeclare(QUEUE_NAME, false,false,false,null);
	
	String routing_Key="1705";
	//注意：别忘把交换机和队列绑定
	channel.queueBind(QUEUE_NAME,EXCHANGER_NAME,routing_Key);
	
	channel.basicQos(1);
	
	QueueingConsumer consumer=new QueueingConsumer(channel);
	channel.basicConsume(QUEUE_NAME,false,consumer);
	
	while(true){
		QueueingConsumer.Delivery delivery=consumer.nextDelivery();
		String message=new String(delivery.getBody());
		System.out.println("消费者2:"+message);
		channel.basicAck(delivery.getEnvelope().getDeliveryTag(),false);
	}
	
}
}
