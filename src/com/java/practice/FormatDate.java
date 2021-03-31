package com.java.practice;


import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.TimeZone;

public class FormatDate {

	public static void main(String[] args) {
		DateTimeFormatter dtf = DateTimeFormatter.ofPattern("ddMMyyyy");
		LocalDateTime ldt = LocalDateTime.now();
		Date date = new Date();
		System.out.println("Now:"+ldt);
		System.out.println("Format:"+dtf.format(ldt));
		
		DateFormat df = new SimpleDateFormat("HH:mm:ss");
		df.setTimeZone(TimeZone.getTimeZone("Asia/Kolkata"));
	    //String dateStr = zt.format(sdf);
		System.out.println("Now:"+date);
		System.out.println("Format:"+df.format(date));
	}

}
