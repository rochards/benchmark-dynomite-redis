package com.rochards.clients;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.rochards.keys.Key;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.exceptions.JedisConnectionException;

public class Client implements Runnable {

	private int id;
	private Key key;
	private int requests;
	private int keyStart;
	private String hostname;
	private int port;
	private long writeTime;
	private long readTime;
	private Jedis jedis;
	
	private final Logger log = LoggerFactory.getLogger(Client.class);
	
	/**
	   * Create a instance of Client
	   * @param id This is the identifier of client
	   * @param key  This is a Key object
	   * @param requests This is the total number of requests this client do
	   */
	public Client(int id, Key key, int requests) {
		this(id, key, requests, 0, "127.0.0.1", 6379);
	}
	
	/**
	   * Create a instance of Client
	   * @param id This is the identifier of client
	   * @param key  This is a Key object
	   * @param requests This is the total number of requests that this client do
	   * @param keyStart This is the initial key to start record
	   * @param hostname This is the Server hostname (default 127.0.0.1)
	   * @param port This is the Server port (default 6379)
	   */
	public Client(int id, Key key, int requests, int keyStart, String hostname, int port) {
		
		this.id = id;
		this.key = key;
		this.requests = requests;
		this.keyStart = keyStart;
		this.hostname = hostname;
		this.port = port;
		this.writeTime = 0;
		this.readTime = 0;
	}
	
	@Override
	public void run() {
		try {
			
			jedis = new Jedis(hostname, port); // open connection
			
			log.info("Client " + this.id + " started write");
			this.hset();
		
			log.info("Client " + this.id + " started read");	
			this.randomKey();
			
			jedis.close(); // close connection
			
			log.info("Client " + this.id + " done");
			
		} catch (JedisConnectionException jce) {
			log.info(jce.getMessage());
		}
	}
	
	private void hset() {
		
		long startTime;
		long endTime;
		
		startTime = System.currentTimeMillis();
		for (int i = keyStart; i < keyStart + requests; i++) {
			for (Map.Entry<String, String> entry : key.getFields().entrySet()) {
				jedis.hset(key.getName() + i, entry.getKey(), entry.getValue());
			}
		}
		endTime = System.currentTimeMillis();
		
		this.writeTime = endTime - startTime;
	}
	
	private void randomKey() {
		
		long startTime;
		long endTime;
		
		startTime = System.currentTimeMillis();
		for (int i = 0; i < requests; i++) {
			String randomKey = jedis.randomKey();
		}
		endTime = System.currentTimeMillis();
		
		this.readTime = endTime - startTime;
		log.info("" + this.readTime);
	}
	
	/**
	 * @return write time in milliseconds
	 * */
	public long getWriteTime() {
		return this.writeTime; // millis
	}
	
	/**
	 * @return read time in milliseconds
	 * */
	public long getReadTime() {
		return this.readTime; // millis
	}
	
	/**
	 * @return writes/seconds
	 * */
	public double getWritesPerSeconds() {
		return this.requests / (this.writeTime / 1000.0); 
	}
	
	/**
	 * @return reads/seconds
	 * */
	public double getReadsPerSeconds() {
		return this.requests / (this.readTime / 1000.0);
	}
}
