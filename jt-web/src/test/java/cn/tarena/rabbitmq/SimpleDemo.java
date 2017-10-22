package cn.tarena.rabbitmq;

import java.io.IOException;

import org.junit.Test;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.Consumer;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;
import com.rabbitmq.client.AMQP.BasicProperties;

/**
 * 用于测试MQ的简单模式
 * 生产者生产消息——队列——消费者消费消息
 * @author ysq
 *
 */
public class SimpleDemo {
	
	//生产者代码
	@Test
	public void simple_producer() throws Exception{
		ConnectionFactory factory=new ConnectionFactory();
		factory.setHost("192.168.234.131");
		//5672是MQ的连接端口，注意，不能写15672,因为是MQ控制台的访问端口
		factory.setPort(5672);
		factory.setUsername("jtadmin");
		factory.setPassword("123456");
		factory.setVirtualHost("/jt");
		
		Connection connection=factory.newConnection();
		//获取Channel通道，作用是通过Channel声明队列，交换机，发布信息
		Channel channel=connection.createChannel();
		
		String QUEUE_NAME="Q1";
		//①参：队列名
		//②参：durable 消息是否需要持久化，false表示不需要。
		//③参：如果此参数是true,表示创建此队列的客户端断开连接后，对应的队列被移除，并且此队列不能被共用
		//④参：true表示：如果此队列没有消费者，队列被自动移除
		//⑤参：额外的扩展参数，一般为null即可
		channel.queueDeclare(QUEUE_NAME, false,false,false,null);
		
		String message="hellomq";
		//basicPublish()是用于向队列里生成消息的
		//①参：交换机的名字
		//②参：队列名，补充：如果用到交换机，此参数要写路由key
		//③参：额外的扩展参数，一般为null即可
		//④参：消息体的字节数组
		for(int i=0;i<100;i++){
		channel.basicPublish("",QUEUE_NAME,null,message.getBytes());
		}
		
		while(true);
	}
	
	@Test
	public void simple_consumer() throws Exception{
		ConnectionFactory factory=new ConnectionFactory();
		factory.setHost("192.168.234.131");
		//5672是MQ的连接端口，注意，不能写15672,因为是MQ控制台的访问端口
		factory.setPort(5672);
		factory.setUsername("jtadmin");
		factory.setPassword("123456");
		factory.setVirtualHost("/jt");
		
		Connection connection=factory.newConnection();
		//获取Channel通道，作用是通过Channel声明队列，交换机，发布信息
		Channel channel=connection.createChannel();
		
		//队列名要和生产队列名一致
		String QUEUE_NAME="Q1";
		
		channel.queueDeclare(QUEUE_NAME, false,false,false,null);
		
		Consumer consumer=new DefaultConsumer(channel){
			
			//消费者消费消息时，会进入到此方法
			//数据会传到 byte[] body 这个对象里
			@Override
			public void handleDelivery(String consumerTag, Envelope envelope, BasicProperties properties, byte[] body)
					throws IOException {
				String message=new String(body);
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				System.out.println("消费者获取消息："+message);
				
			}
		};
		//①参：队列名
		//②参：true表示 当消费者收到消费后，自动发送ack确认信息。false表示手动发送ack
		//③参：consumer对象
		channel.basicConsume(QUEUE_NAME,true,consumer);
		
		while(true);//保持消费者线程一直开启

		
	}

}
