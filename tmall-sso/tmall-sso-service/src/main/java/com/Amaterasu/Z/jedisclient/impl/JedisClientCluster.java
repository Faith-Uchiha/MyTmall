package com.Amaterasu.Z.jedisclient.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.Amaterasu.Z.utils.JedisClient;

import redis.clients.jedis.JedisCluster;

/**
 * 集群版JedisClient实现类
 * @author Amaterasu.Z
 *
 */

public class JedisClientCluster implements JedisClient {

	//利用spring注入对象 就不用构造方法初始化
	@Autowired
	private JedisCluster jedisCluster;
	
	@Override
	public String set(String key, String value) {
		
		return jedisCluster.set(key, value);
	}

	@Override
	public String get(String key) {
		return jedisCluster.get(key);
	}

	@Override
	public Boolean exists(String key) {
		return jedisCluster.exists(key);
	}

	@Override
	public Long expire(String key, int seconds) {
		return jedisCluster.expire(key, seconds);
	}

	@Override
	public Long ttl(String key) {
		return jedisCluster.ttl(key);
	}

	@Override
	public Long incr(String key) {
		return jedisCluster.incr(key);
	}

	@Override
	public Long hset(String key, String field, String value) {
		return jedisCluster.hset(key, field, value);
	}

	@Override
	public String hget(String key, String field) {
		return jedisCluster.hget(key, field);
	}

	@Override
	public Long hdel(String key, String... field) {
		return jedisCluster.hdel(key, field);
	}

}
