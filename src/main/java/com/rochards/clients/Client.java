package com.rochards.clients;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.rochards.keys.Key;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.exceptions.JedisConnectionException;

public class Client {

	private int id;
	private int requests;
	private int keyStart;
	private String hostname;
	private int port;
	private long writeTime;
	private boolean writeDone;
	private long readTime;
	private boolean readDone;
	private Jedis jedis;
	
	private final Logger log = LoggerFactory.getLogger(Client.class);
	
	/**
	   * Create a instance of Client
	   * @param id This is the identifier of client
	   * @param key  This is a Key object
	   * @param requests This is the total number of requests this client do
	   */
	public Client(int id, int requests) {
		this(id, requests, 0, "127.0.0.1", 6379);
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
	public Client(int id, int requests, int keyStart, String hostname, int port) {
		
		this.id = id;
		this.requests = requests;
		this.keyStart = keyStart;
		this.hostname = hostname;
		this.port = port;
		this.writeTime = 0;
		this.readTime = 0;
	}
	
	/**
	 * write a simple key and value n times you defined in requests for this client. Ex: key(i) => key0 key1 key2 ... keyn
	 * @param key 
	 * @param value
	 * */
	public void set(String key, String value) {
	
		this.writeDone = false;
		
		new Thread(()->{
			try {
				
				long startTime;
				long endTime;
				
				jedis = new Jedis(hostname, port); // open connection
				
				log.info("Client " + this.id + " started write");
				
				startTime = System.currentTimeMillis();
				for (int i = keyStart; i < keyStart + requests; i++) {
					jedis.set(key + i, value);
				}
				endTime = System.currentTimeMillis();
				this.writeTime = endTime - startTime;
				
				jedis.close(); // close connection
				
			} catch (JedisConnectionException jce) {
				log.info("Client" + this.id + " "  + jce.getMessage());
			} finally {
				this.writeDone = true;
			}
		}).start();
	}
	
	/**
	 * Just read a simple key and value. It iterates over all keys. Ex: key(i) => key0 key1 key2 ... keyn
	 * @param key
	 * */
	public void get(String key) {
		
		this.readDone = false;
		
		new Thread(()-> {
			
			try {
				
				long startTime;
				long endTime;
				
				jedis = new Jedis(hostname, port); // open connection
				
				log.info("Client " + this.id + " started read");
				
				startTime = System.currentTimeMillis();
				for (int i = keyStart; i < requests + keyStart; i++) {
					String value = jedis.get(key + i);
				}
				endTime = System.currentTimeMillis();
				this.readTime = endTime - startTime;
				
				jedis.close(); // close connection
				
			} catch (JedisConnectionException jce) {
				log.info("Client " + this.id + " "  + jce.getMessage());
			} finally {
				this.readDone = true;
			}
		}).start();
	}
	
	/**
	 * write a simple hash key
	 * @param Key object. Writes this key n times you defined in requests for this client. Ex: key(i) => key0 key1 key2 ... keyn
	 * */
	public void hset(Key key) {
		
		this.writeDone = false;
		
		new Thread(()-> {
			try {

				long startTime;
				long endTime;
				
				jedis = new Jedis(hostname, port); // open connection
				
				log.info("Client " + this.id + " started write");
				
				startTime = System.currentTimeMillis();
				for (int i = keyStart; i < keyStart + requests; i++) {
					for (Map.Entry<String, String> entry : key.getFields().entrySet()) {
						jedis.hset(key.getName() + i, entry.getKey(), entry.getValue());
					}
				}
				endTime = System.currentTimeMillis();
				this.writeTime = endTime - startTime;
				
				jedis.close(); // close connection
				
			} catch (JedisConnectionException jce) {
				log.info("Client" + this.id + " "  + jce.getMessage());
			} finally {
				this.writeDone = true;
			}
		}).start();
	}
	
	/**
	 * Just read a simple hash key. It iterates over all keys. Ex: key(i) => key0 key1 key2 ... keyn
	 * @param key
	 * */
	public void hgetAll(String key) {
		
		this.readDone = false;
		
		new Thread(()-> {
			
			try {
				
				long startTime;
				long endTime;
				
				jedis = new Jedis(hostname, port); // open connection
				
				log.info("Client " + this.id + " started read");
				
				startTime = System.currentTimeMillis();
				for (int i = keyStart; i < requests + keyStart; i++) {
					Map<String, String> value = jedis.hgetAll(key + i);
				}
				endTime = System.currentTimeMillis();
				this.readTime = endTime - startTime;
				
				jedis.close(); // close connection
				
			} catch (JedisConnectionException jce) {
				log.info("Client" + this.id + " "  + jce.getMessage());
			} finally {
				this.readDone = true;
			}
		}).start();
	}
	
	/**
	 * @return id
	 * */
	public int getId() {
		return this.id;
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
	
	/**
	 * @return return true if client has done write
	 * */
	public boolean isWriteDone() {
		return this.writeDone;
	}
	
	/**
	 * @return return true if client has done read
	 * */
	public boolean isReadDone() {
		return this.readDone;
	}
}
