package com.xornet.util;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Vector;
import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.ChannelSftp.LsEntry;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.xornet.util.MySqlDBConnection;


import org.apache.log4j.Logger;

public class CopySrcSparshCsv {
	
	private static Logger log=Logger.getLogger(CopySrcSparshCsv.class.getName());
	
	private CopySrcSparshCsv(){  
		log.info("CopySrcSparshCSV constructor invoked");
	}
	public static void main (String[] args) throws IOException{
		try {
			copyFile("","");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static Map<String, String> copyFile(String jobFileName, String jobFilePath) throws Exception{
		SimpleDateFormat dtNow = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

		log.info(" ***** copyFile Started At: "+ dtNow.format(new Date()));
		log.info(" ***** Inside copyFile trying to connect to remote system ...  *******");
		String propFileName = "config.properties";
		Properties configFile = new java.util.Properties();
		configFile.load(Thread.currentThread().getContextClassLoader().getResourceAsStream(propFileName));

		String remoteHost = configFile.getProperty("remote_host");  
		String remoteUser = configFile.getProperty("remote_user"); 
		String remotePassword = configFile.getProperty("remote_password");
		//String remoteIdentityFile = configFile.getProperty("remote_identityfile");
		int remotePort = Integer.parseInt((configFile.getProperty("remote_port")));
		String remoteDir = configFile.getProperty("remote_dir");
		String remoteFile = "";

		Session     session     = null;
		Channel     channel     = null;
		ChannelSftp channelSftp = null;

		Map<String, String> filesMap = new LinkedHashMap<String, String>();

		try{
			JSch jsch = new JSch();
			//jsch.addIdentity(remoteIdentityFile);

			session = jsch.getSession(remoteUser,remoteHost,remotePort);
			Properties config = new java.util.Properties();
			config.put("StrictHostKeyChecking", "no");
			session.setPassword(remotePassword);
			session.setConfig(config);
			session.connect();
			channel = session.openChannel("sftp");
			channel.connect();
			channelSftp = (ChannelSftp)channel;
			log.info(" ***** Inside copyFile connected to remote system ...  *******");
			channelSftp.cd(remoteDir);
			log.info(" ***** Changed Directory to ...  *******"+ remoteDir);
		   } catch (JSchException ex){

			throw new RuntimeException("Could not connect to Ramco FTP server. Error :- " + ex.getMessage());
		   }
		   try{

			@SuppressWarnings("unchecked")
			// Iterating through the files and get the job specific csv
			Vector<LsEntry> entries = channelSftp.ls("*.*");
			Vector<LsEntry> newEntries = new Vector<LsEntry>();

			log.info(" ***** Iterating through the files and getting the job specific csv ...  *******");
			for (LsEntry entry : entries) {
				if(entry.getFilename().toLowerCase().startsWith(jobFileName.toLowerCase()) && entry.getFilename().toLowerCase().endsWith(".csv")) {
					newEntries.add(entry);
				}
			}

			log.info(" ***** Getting the latest file...  *******");

			Collections.sort(newEntries, new Comparator<LsEntry>() {
				@Override
				public int compare(LsEntry file1, LsEntry file2) {
					if(file1.getAttrs().getMTime() > file2.getAttrs().getMTime())
						return 1;
					else if(file1.getAttrs().getMTime() < file2.getAttrs().getMTime())
						return -1;
					else
						return 0;
				}
			});
			
			if (!newEntries.isEmpty()){
				Iterator<LsEntry> itr = newEntries.iterator();
		        while(itr.hasNext()){
		        	processFileIntoMap(itr.next(), filesMap, remoteDir, jobFileName, channelSftp, jobFilePath);
		        }
		   }else{
				log.info(" ***** No file present with Naming convention: " + jobFileName + ".csv in remote directory: " + remoteDir + "Check with Sparsh");
				filesMap.put(remoteFile, "NO_FILE_PRESENT");
			}
			channel.disconnect();
			session.disconnect();
			log.info(" ***** copied file from: "+ remoteDir + " named: " + remoteFile + " and placed at: " + jobFilePath);
			log.info(" ***** copyFile Finished At: "+ dtNow.format(new Date()));

		}catch(Exception ex){
			log.error("Exception in CopySrcSparshCsv.copyFile(): " + ex.getMessage());
			throw ex;
		}
		return filesMap;
	}
	
	private static Map<String, String> processFileIntoMap(LsEntry firstFile, Map<String, String> filesMap, String remoteDir, String jobFileName, ChannelSftp channelSftp, String jobFilePath) throws Exception {
		
			String remoteFile = firstFile.getFilename();
			log.info(remoteFile + " is the latest job specific file");

			if (firstFile.getAttrs().getSize()==0){
				log.info(remoteFile + ".csv in remote directory: " + remoteDir + " has no data. Check with Sparsh");
				filesMap.put(remoteFile, "NO_DATA");
			}
			else{
				Boolean isProcessed = chkFileIsProcessed(jobFileName, remoteFile);
				if(isProcessed.equals(true)){
					filesMap.put(remoteFile, "ALREADY_PROCESSED");
					
				} else{

					byte[] buffer = new byte[1024];
					BufferedInputStream bis = new BufferedInputStream(channelSftp.get(remoteFile));
					File processeFile = new File(jobFilePath + remoteFile);
					OutputStream os = new FileOutputStream(processeFile);
					BufferedOutputStream bos = new BufferedOutputStream(os);
					int readCount;
					while( (readCount = bis.read(buffer)) > 0) {
						bos.write(buffer, 0, readCount);
					}
					bis.close();
					bos.close();
					log.info(remoteFile + " job specific file is downloaded");
					filesMap.put(processeFile.toString(), "FILE_PRESENT");
					channelSftp.rm(remoteFile);
				}
			}
		return filesMap;
	}
	
	public static void jobCompleted(String empFileName, Connection con, String status) throws SQLException
	{
		log.info("Inside jobCompleted..once the file is uploaded successfully insert the details with file:"+empFileName+" status:"+status);
		java.sql.Statement statement = null;

		try{
			
			statement = con.createStatement();
			String query = "INSERT INTO csv_upload_daily_job (file_type, file_name, file_status, file_date) "
					+ " VALUES('EMP', '"+ empFileName + "', '"+ status + "', NOW())";

			statement.executeUpdate(query);
		} catch (SQLException e) {
			log.error("SQL Exception occured in jobStarted : Unable to insert values in data_migration_daily_log table in MySql"+e.getStackTrace());
			throw e;
		} finally {
			if(statement != null){
				statement.close();
			}
		}
	}

	private static Boolean chkFileIsProcessed(String fileType, String empFileName) throws SQLException, IOException{

		log.info("Inside chkEmpFileIsProcessed..checking if the File is already Processed");
		PreparedStatement preparedStatement =null;
		ResultSet rs =null;
		Boolean isProcessed = false;
		Connection con = null;

		try {
			con = MySqlDBConnection.getConnection();
			if(con == null){
				throw new RuntimeException("Could not connect to Xornet MySQL DB. Error :- Connection failed");
			}

			String query ="select file_type, file_status from csv_upload_daily_job" +
					" where file_name =? and file_status='PROCESSED'";

			preparedStatement = con.prepareStatement(query);			
			preparedStatement.setString(1, empFileName);
			rs=preparedStatement.executeQuery();

			if(rs.next()){
				log.info("File is already Processed!!!");
				isProcessed = true;
			}
			return isProcessed;

		}catch (SQLException e) {
			log.error("SQL Exception occurred in chkEmpFileIsProcessed: unable to get records "+e.getMessage());
			throw e;
		}finally{
			try {	
				if (preparedStatement != null) {
					preparedStatement.close();
				}
				if (rs != null) {
					rs.close();
				}
			} catch (SQLException e) {
				log.error("SQL Exception in chkEmpFileIsProcessed while closing preparedstmt and resultset:: "+e.getMessage());
				throw e;
			}
		}
	}
}
