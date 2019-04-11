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
		int clients = 1000; // number of parallel connections
		int requests = 10000; // total number of requests == number of keys saved
		double writesPerSeconds = 0; 
		double readsPerSeconds = 0;
		final String field = "f$)\\\"<\\\"|9M!  ~&5d'?j\\\"V\\x7f\\\"2.&7&!>z?S!-V5&I%96j8,>$C9 1v/La0/644f'?t6K}()(1$r=::$]-'3(8-t7]%5A}0Oo'0>!F18";
		
		Key key = new Key("key", field, 5);
		Client [] client = new Client[clients];
		Thread [] thread = new Thread[clients];
		
		// create threads
		for (int i = 0; i < clients; i++) {
			
			client[i] =  new Client(i, key, (int)(requests/clients), (int)(i * requests/clients), hostname, port);
			
			thread[i] = new Thread(client[i]);
			thread[i].start();
		}
		
		// when we invoke the join() method on a thread, the calling thread goes into a waiting state.
		for (int i = 0; i < clients; i++) {
			try {
				thread[i].join();
			} catch (InterruptedException e) {
				e.printStackTrace();
				log.info(e.getMessage());
			}
		}
		
		// get requests/seconds
		for (int i = 0; i < clients; i++) {
			
			writesPerSeconds += client[i].getWritesPerSeconds();
			readsPerSeconds += client[i].getReadsPerSeconds();
		}
		
		Jedis jedis = new Jedis(hostname, port); // open connection

		printStatistics(clients, jedis.info("Memory"), requests, writesPerSeconds, readsPerSeconds);
		
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
		System.out.printf("%.3f requests/sec %n%n", writesPerSeconds);
		System.out.printf("READ:%n");
		System.out.printf("%d requests in %.3f sec %n", requests, requests / readsPerSeconds);
		System.out.printf("%.3f requests/sec %n", readsPerSeconds);
	}
	
	public static void printStatisticsToFile(int clients, String memoryInfo, int requests, double writeTime,
			double readTime) {

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
			
			pw.printf("DADOS INICIAIS:%n");
			pw.printf("%d conexoes em paralelo %n", clients);
			pw.printf("%sb utilizados de memoria%n%n", memoryInfo);
			pw.printf("ESCRITA:%n");
			pw.printf("%d dados escritos em %.3f s %n", requests, writeTime);
			pw.printf("%.3f requisicoes/segundo %n%n", requests / writeTime);
			pw.println("LEITURA:");
			pw.printf("%d dados lidos em %.3f s %n", requests, readTime);
			pw.printf("%.3f requisicoes/segundo %n", requests / readTime);
			
			System.out.println("\nSalvando informacoes no arquivo " + fileName);
			
			pw.close();
			
		} catch (IOException ioe) {
			System.out.println(ioe.getMessage());
		}
	}

}
