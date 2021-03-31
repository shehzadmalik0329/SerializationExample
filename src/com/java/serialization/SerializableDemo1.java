package com.java.serialization;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

class Student implements Serializable{
	
	int id = 10;
	String name = "shehzad";
}

class Employee implements Serializable{
	transient int empId = 123;
	static transient String empName = "malik";
	final transient int age = 26;
}

public class SerializableDemo1 {

	public static void main(String[] args) throws IOException, ClassNotFoundException {
		Student s1 = new Student();
		Employee e1 = new Employee();
		
		System.out.println("Serialization started");
		FileOutputStream fos = new FileOutputStream("abc.ser");
		ObjectOutputStream oos = new ObjectOutputStream(fos);
		oos.writeObject(s1);
		oos.writeObject(e1);
		System.out.println("Serialization ended");
		
		System.out.println("DeSerialization started");
		FileInputStream fis = new FileInputStream("abc.ser");
		ObjectInputStream ois = new ObjectInputStream(fis);
		Student s2 = (Student) ois.readObject();
		Employee e2 = (Employee) ois.readObject();
		System.out.println("DeSerialization ended");
		
		System.out.println(s1.id+".................."+s2.id);
		System.out.println(s1.name+".................."+s2.name);
		
		System.out.println(e1.empId+".................."+e2.empId);
		System.out.println(e1.empName+".................."+e2.empName);
		System.out.println(e1.age+".................."+e2.age);
		
		oos.close();
		ois.close();
	}

}
