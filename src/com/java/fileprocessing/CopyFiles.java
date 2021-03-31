package com.java.fileprocessing;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class CopyFiles {

	public static void main(String[] args) throws IOException {

		Date date = Calendar.getInstance().getTime();  

		DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");  
		String strDate = dateFormat.format(date);  
		String directoryName = "D:\\APD\\ExcelUpload\\"+strDate+"\\";
		File directory = new File(directoryName);
		if (! directory.exists()){
			directory.mkdir();
			System.out.println("Created \"");
		}


		String attritionFileName = "Attrition.xlsx";
		Path attritionFilePath = Paths.get("D:\\APD\\ExcelUpload\\18Nov2019\\"+attritionFileName);
		Path attritionFilePathDest = Paths.get(directoryName+attritionFileName);

		String manpowerFileName = "Manpower_Trend.xlsx";
		Path manpowerFilePath = Paths.get("D:\\APD\\ExcelUpload\\18Nov2019\\"+manpowerFileName);
		Path manpowerFilePathDest = Paths.get(directoryName+manpowerFileName);

		String profitabilityFileName = "Delivery_Profitability.xlsx";
		Path profitabilityFilePath = Paths.get("D:\\APD\\ExcelUpload\\18Nov2019\\"+profitabilityFileName);
		Path profitabilityFilePathDest = Paths.get(directoryName+profitabilityFileName);

		Files.copy(attritionFilePath, attritionFilePathDest);
		Files.copy(manpowerFilePath, manpowerFilePathDest);
		Files.copy(profitabilityFilePath, profitabilityFilePathDest);
		

	}
}
