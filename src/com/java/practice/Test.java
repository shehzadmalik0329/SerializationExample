package com.java.practice;

class Parent{
	
	int i=10;
	public void m1() {
		System.out.println("Parent m1");
	}
	public void m2() {
		System.out.println("Parent m2");
	}
}
class Child extends Parent{
	int i=20;

	public void m1() { 
		System.out.println("Child m1"); 
		}
	 
	public void m3() {
		System.out.println("Child m3");
	}
}
public class Test {

	public static void main(String[] args) {
		Parent p1 = new Parent();
		Parent p2 = new Child();
		
		//Child c1 = new Parent();	//CE
		Child c2 = new Child();
		
		/*
		 * System.out.println(p1.i); //System.out.println(p1.c); //CE p1.m1();
		 * //p1.m3(); //CE
		 */		
		System.out.println(p2.i);
		//System.out.println(p2.c);	//CE
		p2.m1();
		//p2.m3();	//CE
		
		/*
		 * System.out.println(c2.i); System.out.println(c2.i); //CE c2.m1(); c2.m3();
		 * //CE
		 */
	}

}
