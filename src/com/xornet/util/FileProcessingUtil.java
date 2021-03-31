package com.xornet.util;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class FileProcessingUtil {
	
	public static void moveFile(String source, String destination) throws IOException {
		
		Path targetFilePath = Files.move 
		        (Paths.get(source),
		        		Paths.get(destination) 
		        ); 
		  
		        if(targetFilePath != null) 
		        { 
		            System.out.println("File renamed and moved successfully"); 
		        } 
		        else
		        { 
		            System.out.println("Failed to move the file"); 
		        } 
		
	}

}
