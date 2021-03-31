package com.java.practice;


public class BinarySearchExample {

	public static void main(String args[])
	   {
	      int[] array = {1,2,3,4,5,6};
	      
	      int first = 0;
	      int last = array.length-1;
	      int middle = (first+last)/2;
	      
	      int search = 10;
	      
	      while(first<=last) {
	    	  if(search == array[middle]) {
	    		  System.out.println(search+" found at index "+middle);
	    		  break;
	    	  }
	    	  if(search > array[middle]) {
	    		  first = middle+1;
	    		  middle = (first+last)/2;
	    	  }
	    	  if(search < array[middle]) {
	    		  last = middle-1;
	    		  middle = (first+last)/2;
	    	  }
	      }
	      if(first > last) {
	    	  System.out.println(search+" not found.");
	      }
	   }

}
