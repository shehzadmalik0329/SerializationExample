package com.java.fileprocessing;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class MoveFile {

	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
//		Path temp = Files.move 
//		        (Paths.get("D:\\JavaMoveFile\\abc.ser"),
//		        		Paths.get("D:\\workspace\\Serialization\\abc.ser") 
//		        ); 
//		  
//		        if(temp != null) 
//		        { 
//		            System.out.println("File renamed and moved successfully"); 
//		        } 
//		        else
//		        { 
//		            System.out.println("Failed to move the file"); 
//		        } 
		        
		 Path filePath = Paths.get("D:\\workspace\\Serialization\\abc.ser");
		 if(filePath.toFile().exists()) {
			 System.out.println("Files present");
		 }
		 else {
			 System.out.println("Not present");
		 }

	}

}
