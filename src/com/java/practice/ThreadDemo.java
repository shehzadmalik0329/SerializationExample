package com.java.practice;

import java.time.LocalTime;

public class ThreadDemo {

	public static void main(String[] args) {
		
		for(int i=0; i<10; i++) {
			try {
				LocalTime now = LocalTime.now();
				System.out.println(now);
				Thread.sleep(60000);
				LocalTime now1 = LocalTime.now();
				System.out.println(now1);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			
		}

	}

}
