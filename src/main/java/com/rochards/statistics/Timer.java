package com.rochards.statistics;

public class Timer {
	
	
	private double writeTime; // seconds
	private double readTime; // seconds
	
	public Timer() {
		this.writeTime = 0;
		this.readTime = 0;
	}
	
	public synchronized double writeTime(double writeTime) {
		this.writeTime += writeTime;
		return this.writeTime;
	}
	
	public synchronized double readTime(double readTime) {
		this.readTime += readTime;
		return this.readTime;
	}
}
