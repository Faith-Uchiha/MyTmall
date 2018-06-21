package com.Amaterasu.Z.jedisclient.impl;

import org.springframework.beans.factory.annotation.Autowired;

import com.Amaterasu.Z.utils.JedisClient;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

/**
 * 单机版JedisClient实现类
 * @author Amaterasu.Z
 *
 */
public class JedisClientPool implements JedisClient {

	@Autowired
	private JedisPool jedisPool;
	
	@Override
	public String set(String key, String value) {
		Jedis jedis = jedisPool.getResource();
		String result = jedis.set(key, value);
		jedis.close(); //别忘了归还
		return result;
	}

	@Override
	public String get(String key) {
		Jedis jedis = jedisPool.getResource();
		String result = jedis.get(key);
		jedis.close(); //别忘了归还
		return result;
	}

	@Override
	public Boolean exists(String key) {
		Jedis jedis = jedisPool.getResource();
		Boolean result = jedis.exists(key);
		jedis.close(); //别忘了归还
		return result;
	}

	@Override
	public Long expire(String key, int seconds) {
		Jedis jedis = jedisPool.getResource();
		Long expire = jedis.expire(key, seconds);
		jedis.close(); //别忘了归还
		return expire;
	}

	@Override
	public Long ttl(String key) {
		Jedis jedis = jedisPool.getResource();
		Long ttl = jedis.ttl(key);
		jedis.close(); //别忘了归还
		return ttl;
	}

	@Override
	public Long incr(String key) {
		Jedis jedis = jedisPool.getResource();
		Long incr = jedis.incr(key);
		jedis.close(); //别忘了归还
		return incr;
	}

	@Override
	public Long hset(String key, String field, String value) {
		Jedis jedis = jedisPool.getResource();
		Long result = jedis.hset(key, field, value);
		jedis.close(); //别忘了归还
		return result;
	}

	@Override
	public String hget(String key, String field) {
		Jedis jedis = jedisPool.getResource();
		String result = jedis.hget(key, field);
		jedis.close(); //别忘了归还
		return result;
	}

	@Override
	public Long hdel(String key, String... field) {
		Jedis jedis = jedisPool.getResource();
		Long result = jedis.hdel(key,field);
		jedis.close(); //别忘了归还
		return result;
	}

}
