package com.java.immutable;

import java.util.ArrayList;
import java.util.Collections;

final class User{
	private final String name;
	private final int age;

	public User(String name, int age) {
		this.name = name;
		this.age = age;
	}
	public String getName() {
		return name;
	}
	public int getAge() {
		return age;
	}
	
	
}

public class ImmutableDemo1 {

	public static void main(String[] args) {
		User u1 = new User("shehzad",27);
		System.out.println(u1);
		
		Collections.unmodifiableList(new ArrayList());
		

	}

}
