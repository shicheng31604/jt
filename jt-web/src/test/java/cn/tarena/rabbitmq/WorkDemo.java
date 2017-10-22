package cn.tarena.rabbitmq;

import java.io.IOException;

import org.junit.Before;
import org.junit.Test;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.QueueingConsumer;

/**
 * 测试Work-Queue模式
 * 这个模式特点：队列的信息只能被一个consumer所消费
 * 即多个consumer之间是竞争关系
 * @author ysq
 *
 */
public class WorkDemo {

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
	//工作队列模式的生成者
	@Test
	public void work_producer() throws Exception{
		System.out.println("生产者启动");
		Channel channel=connection.createChannel();
		String QUEUE_NAME="Q2";
		channel.queueDeclare(QUEUE_NAME, false, false,false,null);
		for(int i=0;i<100;i++){
			String message=i+"";
			channel.basicPublish("",QUEUE_NAME,null,message.getBytes());
		}
		
		while(true);
	}
	@Test
	public void work_consumer_1() throws Exception{
		System.out.println("消费者1启动");
		Channel channel=connection.createChannel();
		String QUEUE_NAME="Q2";
		channel.queueDeclare(QUEUE_NAME, false, false, false,null);
		//这里的1表示：当消费者收到一条消息后，需要返回ack确认信息，队列收到ack确认信息后
		//在发送下一条消息
		//如果是3，表示允许消费者最多有3条信息不反馈ack。即如果不反馈的ack数量>3,
		//则Consumer不能再从队列里取消息
		channel.basicQos(1);
		
		QueueingConsumer consumer=new QueueingConsumer(channel);
		//第二个参数，true表示Consumer接收消费后，自动反馈ack信息
		//稍后我们会手动控制，当Consumer正确消费消息后，返回ack
		channel.basicConsume(QUEUE_NAME, false,consumer);
		while(true){
			QueueingConsumer.Delivery delivery=consumer.nextDelivery();
			//获取并消费消息
			String message=new String(delivery.getBody());
			Thread.sleep(1000);//一秒消费一条数据
			System.out.println("消费者1收到消息:"+message);
			//①参：表示返回的ack状态码
			//②参：表示消费完消息后反馈ack
			channel.basicAck(delivery.getEnvelope().getDeliveryTag(),false);
		}
		
	}
	
	@Test
	public void work_consumer_2() throws Exception{
		System.out.println("消费者2启动");
		Channel channel=connection.createChannel();
		String QUEUE_NAME="Q2";
		channel.queueDeclare(QUEUE_NAME, false, false, false,null);
		//这里的1表示：当消费者收到一条消息后，需要返回ack确认信息，队列收到ack确认信息后
		//在发送下一条消息
		//如果是3，表示允许消费者最多有3条信息不反馈ack。即如果不反馈的ack数量>3,
		//则Consumer不能再从队列里取消息
		channel.basicQos(1);
		
		QueueingConsumer consumer=new QueueingConsumer(channel);
		//第二个参数，true表示Consumer接收消费后，自动反馈ack信息
		//稍后我们会手动控制，当Consumer正确消费消息后，返回ack
		channel.basicConsume(QUEUE_NAME, false,consumer);
		while(true){
			QueueingConsumer.Delivery delivery=consumer.nextDelivery();
			//获取并消费消息
			String message=new String(delivery.getBody());
			Thread.sleep(400);//一秒消费一条数据
			System.out.println("消费者2收到消息:"+message);
			//①参：表示返回的ack状态码
			//②参：表示消费完消息后反馈ack
			channel.basicAck(delivery.getEnvelope().getDeliveryTag(),false);
		}
		
	}
	
}
