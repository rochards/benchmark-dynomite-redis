package com.rochards.statistics;

public class Timer {
	
	
	private double writeTime; // seconds
	private double readTime; // seconds
	
	public Timer() {
		this.writeTime = 0;
		this.readTime = 0;
	}
	
	public synchronized void writeTime(long writeTime) {
		this.writeTime += writeTime;
	}
	
	public synchronized void readTime(long readTime) {
		this.readTime += readTime;
	}
	
	public double getWriteTime() {
		return this.writeTime / 1000.0; // to seconds
	}
	
	public double getReadTime() {
		return this.readTime / 1000.0; // to seconds
	}
}
