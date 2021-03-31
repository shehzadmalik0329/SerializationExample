package com.java.practice;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class StreamExmple01 {

	public static void main(String[] args) {
		String[] employees = {"Shehzad", "Pramod", "Rajaram", "Sajal"};
		List<String> names = Arrays.asList(employees);

		List<String> threeName = names.stream().limit(3).collect(Collectors.toList());
		
		System.out.println(threeName);
		
		List<List<String>> nestedList = new ArrayList();
		nestedList.add(threeName);
		
		System.out.println(nestedList);
		
		List<String> flattenList = new ArrayList();
		nestedList.forEach(flattenList::addAll);
		System.out.println(flattenList);
		

	}

}
