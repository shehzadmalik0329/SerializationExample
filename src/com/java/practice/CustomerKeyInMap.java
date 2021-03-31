package com.java.practice;

import java.util.HashMap;
import java.util.Map;

class Employee {
	
	private int id;
	private String name;
	
	
	public Employee(int id, String name) {
		this.id = id;
		this.name = name;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	
}
public class CustomerKeyInMap {

	public static void main(String[] args) {
		Employee e1 = new Employee(1, "Ravi");
		Employee e2 = new Employee(1,"Shehzad");
		Map<Employee, String> hm = new HashMap<Employee, String>();
		hm.put(e1, null);
		hm.put(e1, "A");
		
		System.out.println(hm);

	}

}
