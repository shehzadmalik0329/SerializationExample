package com.java.fileprocessing;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Vector;

class Employee {
	String name;
	int id;
	
	public Employee(String name, int id) {
		this.name = name;
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public int getId() {
		return id;
	}

	@Override
	public String toString() {
		return "Employee [name=" + name + ", id=" + id + "]";
	}
}

public class ProcessFile {

	public static void main(String[] args) {
		
		
		/*
		 * Vector<String> files = new Vector();
		 * files.add("Fusion_Employee_Extract_20191022060631.csv");
		 * files.add("Fusion_Employee_Extract_20191022030504.csv");
		 * files.add("Fusion_Employee_Extract_20191021090658.csv");
		 * System.out.println("Before Sort:"+files); Collections.sort(files);
		 * System.out.println("After Sort:"+files);
		 */
		  
		  LocalDate localdate = LocalDate.now();
		  
		  System.out.println(localdate);
		  
		  Date inputDate=new Date();
		  System.out.println(inputDate);
		  LocalDate oneDayBefore =
		  inputDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
		  
		  System.out.println(oneDayBefore);
		 
		
//		Employee emp1 = new Employee("Fusion_Employee_Extract_20191021060631.csv",1);
//		Employee emp2 = new Employee("Fusion_Employee_Extract_20191022030504.csv",1);
//		Employee emp3 = new Employee("Fusion_Employee_Extract_20191024090658.csv",1);
//		
//		Vector<Employee> employees = new Vector();
//		employees.add(emp3);
//		employees.add(emp1);
//		employees.add(emp2);
//		
//		System.out.println("Before Sort");
//		System.out.println(employees);
//		Collections.sort(employees, new Comparator<Employee>() {
//
//			@Override
//			public int compare(Employee emp1, Employee emp2) {
//				// TODO Auto-generated method stub
//				return -emp1.getName().compareTo(emp2.getName());
//			}
//			
//		});
//		System.out.println("After Sort");
//		System.out.println(employees);
	}

}
