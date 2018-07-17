import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageListener;
import javax.jms.MessageProducer;
import javax.jms.Queue;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.jms.Topic;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.junit.Test;

public class testActiveMQ {
	
	//P2P消息模式，只能有一个消费者
	@Test
	public void testQueueProducer() throws Exception {
		ConnectionFactory activeMQConnectionFactory = new ActiveMQConnectionFactory("tcp://192.168.56.11:61616");
		Connection conn = activeMQConnectionFactory.createConnection();
		conn.start();
		//不开启事务
		Session session = conn.createSession(false, Session.AUTO_ACKNOWLEDGE);
		Queue queue = session.createQueue("test-queue");
		MessageProducer prod = session.createProducer(queue);
		TextMessage messa = session.createTextMessage("hello active!");
		prod.send(messa);
		
		//别忘了关闭资源  关闭producer 关闭session 关闭连接
		prod.close();
		session.close();
		conn.close();
	}
	
	@Test
	public void testQueueConsumer() throws Exception {
		ActiveMQConnectionFactory factory = new ActiveMQConnectionFactory("tcp://192.168.56.11:61616");
		Connection conn = factory.createConnection();
		conn.start();
		Session session = conn.createSession(false, Session.AUTO_ACKNOWLEDGE);
		Queue queue = session.createQueue("test-queue");
		MessageConsumer consumer = session.createConsumer(queue);
		consumer.setMessageListener(new MessageListener() {
			
			@Override
			public void onMessage(Message arg0) {

				TextMessage textMessage = (TextMessage)arg0;
				try {
					String text = textMessage.getText();
					System.out.println(text);
					
				} catch (JMSException e) {
					e.printStackTrace();
				}
			}
		});
		
		System.in.read();
		
		consumer.close();
		session.close();
		conn.close();
	}
	
	//Topic模式，发布订阅
	@Test
	public void testTopicProducer() throws Exception {
		ActiveMQConnectionFactory factory = new ActiveMQConnectionFactory("tcp://192.168.56.11:61616");
		Connection conn = factory.createConnection();
		conn.start();
		Session session = conn.createSession(false, Session.AUTO_ACKNOWLEDGE);
		
		Topic topic = session.createTopic("test-topic!");
		MessageProducer prod = session.createProducer(topic);
		TextMessage messa = session.createTextMessage("Hello MyTOPIC message!");
		
		prod.send(messa);
		prod.close();
		session.close();
		conn.close();
		
	}
	
	@Test
	public void testTopicConsumer() throws Exception {
		ActiveMQConnectionFactory factory = new ActiveMQConnectionFactory("tcp://192.168.56.11:61616");
		Connection conn = factory.createConnection();
		conn.start();
		Session session = conn.createSession(false, Session.AUTO_ACKNOWLEDGE);
		
		Topic topic = session.createTopic("test-topic!");
		MessageConsumer consumer = session.createConsumer(topic);
		TextMessage messa = session.createTextMessage("Hello MyTOPIC message!");
		
		//接收消息
		consumer.setMessageListener(new MessageListener() {
			
			@Override
			public void onMessage(Message arg0) {
				
				TextMessage meg = (TextMessage)arg0;
				try {
					String text = meg.getText();
					System.out.println(text);
				} catch (JMSException e) {
					e.printStackTrace();
				}
				
			}
		});
		System.out.println("消费端！");
		System.in.read();
		
		consumer.close();
		session.close();
		conn.close();
	}
}
