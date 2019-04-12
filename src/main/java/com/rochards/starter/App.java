package com.rochards.starter;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.rochards.clients.Client;
import com.rochards.keys.Key;

import redis.clients.jedis.Jedis;

public class App {
	public static void main(String[] args) {

		final Logger log = LoggerFactory.getLogger(App.class);
		
		String hostname = "127.0.0.1"; // server hostname
		int port = 6379; // server port
		int clients = 4000; // number of parallel connections
		int requests = 1000000; // total number of requests == number of keys saved
		
		long startTime = 0;
		long endTime = 0;
		double writesPerSeconds = 0; 
		double readsPerSeconds = 0;
		final String field = "f$)\\\"<\\\"|9M!  ~&5d'?j\\\"V\\x7f\\\"2.&7&!>z?S!-V5&I%96j8,>$C9 1v/La0/644f'?t6K}()(1$r=::$]-'3(8-t7]%5A}0Oo'0>!F18";
		Key key = new Key("key", field, 5);
		
		Client [] client = new Client[clients];
		
		/* == CREATE CLIENTS == */
		for (int i = 0; i < clients; i++) {
			client[i] =  new Client(i, (int)(requests/clients), (int)(i * requests/clients), hostname, port);
		}
		
		/* == START WRITE == */
		startTime = System.currentTimeMillis();
		for (int i = 0; i < clients; i++) {
			//client[i].hset(key);
			client[i].set("key", "value");
		}
		
		int count = 0;
		while (count < clients) {
			if(!client[count].isWriteDone()) {
				count = 0;
			}
			else {
				count++;
			}
		}
		endTime = System.currentTimeMillis();	
		writesPerSeconds = requests / ((endTime - startTime)/1000.0);
		
		
		/* == START READ == */
		startTime = System.currentTimeMillis();
		for (int i = 0; i < clients; i++) {
			//client[i].hgetAll(key.getName());
			client[i].get("key");
		}
		//
		count = 0;
		while (count < clients) {
			if(!client[count].isReadDone()) {
				count = 0;
			}
			else {
				count++;
			}
		}
		endTime = System.currentTimeMillis();	
		readsPerSeconds = requests / ((endTime - startTime)/1000.0);
		
		Jedis jedis = new Jedis(hostname, port); // open connection

		printStatistics(clients, jedis.info("Memory"), requests, writesPerSeconds, readsPerSeconds);
		printStatisticsToFile(clients, jedis.info("Memory"), requests, writesPerSeconds, readsPerSeconds);
		
		jedis.close();
		
		log.info("done");
	}

	public static void printStatistics(int clients, String memoryInfo, int requests, double writesPerSeconds,
			double readsPerSeconds) {

		Pattern pattern = Pattern.compile("(?<=used_memory_human:)(.*)(?=)");
		Matcher matcher = pattern.matcher(memoryInfo);

		if (matcher.find()) {
			memoryInfo = matcher.group(1);
		}

		System.out.printf("INFO:%n");
		System.out.printf("%d parallel clients %n", clients);
		System.out.printf("%sb used memory%n%n", memoryInfo);
		System.out.printf("WRITE:%n");
		System.out.printf("%d requests in %.3f sec %n", requests, requests / writesPerSeconds);
		System.out.printf("%.3f requests/sec %n", writesPerSeconds);
		System.out.printf("%.3f ms is the average time to perform a write request %n%n", 1000 / writesPerSeconds);
		System.out.printf("READ:%n");
		System.out.printf("%d requests in %.3f sec %n", requests, requests / readsPerSeconds);
		System.out.printf("%.3f requests/sec %n", readsPerSeconds);
		System.out.printf("%.3f ms is the average time to perform a read request %n%n", 1000 / readsPerSeconds);
	}
	
	public static void printStatisticsToFile(int clients, String memoryInfo, int requests, double writesPerSeconds,
			double readsPerSeconds) {

		String fileName = "workload_" + clients + "_clients_" + requests + "_requests.txt";
		
		Pattern pattern = Pattern.compile("(?<=used_memory_human:)(.*)(?=)");
		Matcher matcher = pattern.matcher(memoryInfo);

		if (matcher.find()) {
			memoryInfo = matcher.group(1);
		}
		
		FileWriter fw; //= //new FileWriter(fileName);
		PrintWriter pw;
		
		try {
			
			fw = new FileWriter(fileName);
			pw = new PrintWriter(fw);
			
			pw.printf("INFO:%n");
			pw.printf("%d parallel clients %n", clients);
			pw.printf("%sb used memory%n%n", memoryInfo);
			pw.printf("WRITE:%n");
			pw.printf("%d requests in %.3f sec %n", requests, requests / writesPerSeconds);
			pw.printf("%.3f requests/sec %n", writesPerSeconds);
			pw.printf("%.3f ms is the average time to perform a write request %n%n", 1000 / writesPerSeconds);
			pw.printf("READ:%n");
			pw.printf("%d requests in %.3f sec %n", requests, requests / readsPerSeconds);
			pw.printf("%.3f requests/sec %n", readsPerSeconds);
			pw.printf("%.3f ms is the average time to perform a read request %n%n", 1000 / readsPerSeconds);
			
			System.out.println("\nSalvando informacoes no arquivo " + fileName);
			
			pw.close();
			
		} catch (IOException ioe) {
			System.out.println(ioe.getMessage());
		}
	}

}
