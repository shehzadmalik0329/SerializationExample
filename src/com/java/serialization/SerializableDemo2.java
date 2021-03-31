package com.java.serialization;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

class Dog implements Serializable{
	Cat c = new Cat();
}

class Cat implements Serializable{
	Rat r = new Rat();
}

class Rat implements Serializable{
	int j = 20;
}

public class SerializableDemo2 {

	public static void main(String[] args) throws IOException, ClassNotFoundException {
		Dog d1 = new Dog();
		System.out.println("Serialization Started");
		FileOutputStream fos = new FileOutputStream("pqr.ser");
		ObjectOutputStream oos = new ObjectOutputStream(fos);
		oos.writeObject(d1);
		System.out.println("Serialization Ended");
		
		System.out.println("DeSerialization started");
		FileInputStream fis = new FileInputStream("pqr.ser");
		ObjectInputStream ois = new ObjectInputStream(fis);
		Dog d2 = (Dog) ois.readObject();
		System.out.println("DeSerialization ended");
		
		System.out.println(d1.c.r.j+"........"+d2.c.r.j);
		oos.close();
		ois.close();

	}

}
