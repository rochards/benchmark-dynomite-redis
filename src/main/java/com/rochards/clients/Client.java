package com.rochards.clients;

import java.util.Map;

import com.rochards.keys.Key;
import com.rochards.statistics.Timer;

import redis.clients.jedis.Jedis;

public class Client implements Runnable {

	private int id;
	private Key key;
	private int requests;
	private int keyStart;
	private String hostname;
	private int port;
	private Jedis jedis;
	private static Timer timer;
	
	public Client(int id, Key key, int requests) {
		
		this.id = id;
		this.key = key;
		this.requests = requests;
		this.keyStart = 0;
		this.hostname = "127.0.0.1";
		this.port = 6379;
		
		timer = new Timer();
		
		new Thread(this).start();
	}
	
	public Client(int id, Key key, int requests, int keyStart, String hostname, int port) {
		
		this.id = id;
		this.key = key;
		this.requests = requests;
		this.keyStart = keyStart;
		this.hostname = hostname;
		this.port = port;
		
		timer = new Timer();
		
		new Thread(this).start();
	}
	
	public void run() {
		
		jedis = new Jedis(hostname, port);
		hset();
		randomKey();
		jedis.close();
	}
	
	private void hset() {
		
		long startTime;
		long endTime;
		
		startTime = System.currentTimeMillis();
		for (int i = keyStart; i < keyStart + requests; i++) {
			for (Map.Entry<String, String> entry : key.getFields().entrySet()) {
				jedis.hset(key.getName(), entry.getKey(), entry.getValue());
			}
		}
		endTime = System.currentTimeMillis();
		
		timer.writeTime((endTime - startTime) / 1000.0);
	}
	
	private void randomKey() {
		
		long startTime;
		long endTime;
		
		startTime = System.currentTimeMillis();
		for (int i = 0; i < requests; i++) {
			String randomKey = jedis.randomKey();
		}
		endTime = System.currentTimeMillis();
		
		timer.readTime((endTime - startTime) / 1000.0);
	}

}
