import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.Amaterasu.Z.utils.JedisClient;

import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisCluster;
import redis.clients.jedis.JedisPool;

public class TestJedis {

	//单机版
	@Test
	public void testJedis() {
		Jedis jedis = new Jedis("192.168.56.11",6379);
		jedis.set("test12", "my first jedis test!");
		String result = jedis.get("test12");
		System.out.println(result);
		jedis.close();
	}
	
	//连接池
	@Test
	public void testJedisPool() {
		JedisPool jedisPool = new JedisPool("192.168.56.11",6379);
		Jedis jedis = jedisPool.getResource();
		jedis.set("test22", "test jedis pool!");
		String result = jedis.get("test22");
		System.out.println(result);
		jedis.close();
	}
	
	//集群
	@Test
	public void testCluster() throws IOException {
		   
		Set<HostAndPort> nodes = new HashSet<>();
		nodes.add(new HostAndPort("192.168.56.11",7001));
		nodes.add(new HostAndPort("192.168.56.11",7002));
		nodes.add(new HostAndPort("192.168.56.11",7003));
		nodes.add(new HostAndPort("192.168.56.11",7004));
		nodes.add(new HostAndPort("192.168.56.11",7005));
		nodes.add(new HostAndPort("192.168.56.11",7006));
		JedisCluster jedisCluster = new JedisCluster(nodes);
		jedisCluster.set("test3", "test Jedis Cluster!");
		String result = jedisCluster.get("test3");
		System.out.println(result);
		jedisCluster.close();
	}
	
	//封装jedis常用操作，并编写了单机版和集群版的实现类
	@Test
	public void testJedisClient() {
		ApplicationContext context = new ClassPathXmlApplicationContext("classpath:spring/applicationContext-redis.xml");
		JedisClient jedisClient = context.getBean(JedisClient.class);
//		if(jedisClient==null)
//			System.out.println("jedis Client is null"); 
//		else
//			System.out.println("not null");
		
		//一开始总是空指针  原因：jedisCluster没有注入。然后竟然是因为没有配置Spring去哪扫描注解
		jedisClient.set("testIntegration", "Integration of SingleModel");
		String result = jedisClient.get("testIntegration");
		System.out.println(result);
	}
}
