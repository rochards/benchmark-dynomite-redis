package com.rochards.starter;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Timestamp;
import java.util.Date;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.rochards.clients.Client;
import com.rochards.keys.Key;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.exceptions.JedisConnectionException;

public class App {
	public static void main(String[] args) {

		String hostname = "127.0.0.1"; // Server hostname
		int port = 6379; // Server port
		int clients = 8; // Number of parallel connections
		int requests = 2000000; // Total number of requests

		long writeStartTime = 0; // millis
		long writeEndTime = 0; // millis
		double writeTime = 0; // seconds
		long readStartTime = 0; // millis
		long readEndTime = 0; // millis
		double readTime = 0; // seconds

		final String field = "f$)\\\"<\\\"|9M!  ~&5d'?j\\\"V\\x7f\\\"2.&7&!>z?S!-V5&I%96j8,>$C9 1v/La0/644f'?t6K}()(1$r=::$]-'3(8-t7]%5A}0Oo'0>!F18";

		Key key = new Key("key", field, 5);
		//Jedis jedis = null;
		Client client;
		
		// create threads
		for (int i = 0; i < clients; i++) {
			new Client(i, key, (int)(requests/clients), i * (int)(requests/clients) - i, hostname, port);
		}
		
		
		/*try {
			

			//jedis = new Jedis(hostname, port);

			writeStartTime = System.currentTimeMillis();
			for (int i = 0; i < requests; i++) {
				
				for (Map.Entry<String, String> entry : key.getFields().entrySet()) {
					jedis.hset(key.getName() + i, entry.getKey(), entry.getValue());
				}
			}
			writeEndTime = System.currentTimeMillis();
			writeTime = (writeEndTime - writeStartTime) / 1000.0;

			readStartTime = System.currentTimeMillis();
			for (int i = 0; i < requests; i++) {
				//Map<String, String> value = jedis.hgetAll(jedis.randomKey());
				String randomKey = jedis.randomKey();
			}
			readEndTime = System.currentTimeMillis();
			readTime = (readEndTime - readStartTime) / 1000.0; // millis to seconds

			Jedis jedis = new Jedis(hostname, port);

			printStatistics(clients, jedis.info("Memory"), requests, writeTime, readTime);
			printStatisticsToFile(clients, jedis.info("Memory"), requests, writeTime, readTime);
			//System.out.println();
			
			jedis.close();

		} catch (JedisConnectionException jce) {
			System.out.println(jce.getMessage());
		}*/
	}

	public static void printStatistics(int clients, String memoryInfo, int requests, double writeTime,
			double readTime) {

		Pattern pattern = Pattern.compile("(?<=used_memory_human:)(.*)(?=)");
		Matcher matcher = pattern.matcher(memoryInfo);

		if (matcher.find()) {
			memoryInfo = matcher.group(1);
		}

		System.out.printf("DADOS INICIAIS:%n");
		System.out.printf("%d conexoes em paralelo %n", clients);
		System.out.printf("%sb utilizados de memoria%n%n", memoryInfo);
		System.out.printf("ESCRITA:%n");
		System.out.printf("%d dados escritos em %.3f s %n", requests, writeTime);
		System.out.printf("%.3f requisicoes/segundo %n%n", requests / writeTime);
		System.out.println("LEITURA:");
		System.out.printf("%d dados lidos em %.3f s %n", requests, readTime);
		System.out.printf("%.3f requisicoes/segundo %n", requests / readTime);
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
