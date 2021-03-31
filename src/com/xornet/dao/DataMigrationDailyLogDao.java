package com.xornet.dao;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.log4j.Logger;

import com.mysql.jdbc.Statement;

public class DataMigrationDailyLogDao
{
	 static Logger log = Logger.getLogger(DataMigrationDailyLogDao.class);
	 public static final String STATUS_STARTED ="Started";
	 private static final String STATUS_FAILED = "Failed";
	 private static final String STATUS_COMPLETED_WITH_ERRORS = "Completed with Errors";
	 private static final String STATUS_COMPLETED = "Completed";
	 
	 public static void main(String [] args) throws IOException{
		   
	 }
	 
	 public static long jobStarted(String jobName,Connection con) throws SQLException
	 {
		 java.sql.Statement statement = null;
		 ResultSet rs = null;
		 Long key =0l;
		 
			try{
				
				statement = con.createStatement();
				String query = "INSERT INTO data_migration_daily_log (job_name,start_time,status) "
						+ " VALUES('"+jobName+"',now(),'"+STATUS_STARTED+"')";

				statement.executeUpdate(query,Statement.RETURN_GENERATED_KEYS);
				
				rs = statement.getGeneratedKeys();
				if (rs != null && rs.next()) {
				    key = rs.getLong(1);
				}
					
				
			} catch (SQLException e) {
				log.error("SQL Exception occured in jobStarted : Unable to insert values in data_migration_daily_log table in MySql"+e.getStackTrace());
				throw e;
			} finally {
				if(statement != null){
					statement.close();
				}
				
			}
			return key;
			
   }
	 
	 public static void jobCompleted(long jobId,int processedCount,int addedCount,int udpatedCount,int errorCount,String errorMsg,Connection con) throws SQLException
	 {
		  String status = STATUS_COMPLETED;
			
			try{
				if(errorCount != 0){
					status = STATUS_COMPLETED_WITH_ERRORS;
				}else{
					errorMsg = null;
				}
				updateLog(jobId, status, processedCount, addedCount, udpatedCount, errorCount, errorMsg,con);
					
			} catch (SQLException e) {
				log.error("SQL Exception occured in jobCompleted: Unable to update data_migration_daily_log table on job completion"+e.getMessage());
				throw e;
			} 
			
   }
	 
	 public static void jobFailed(long jobId,String errorMsg,Connection con) throws SQLException
	 {
		  try{
				
				updateLog(jobId, STATUS_FAILED, 0, 0, 0, 0, errorMsg,con);
					
			} catch (SQLException e) {
				log.error("SQL Exception occured in jobFailed : Unable to update data_migration_daily_log table on job failure "+e.getMessage());
				throw e;
			} 
			
   }
	 
	 private static void updateLog(long jobId,String status,int processedCount,int addedCount,int udpatedCount,int errorCount,String errorMsg,Connection con) throws SQLException
	 {

		 PreparedStatement preparedStatement = null;
		 	
			try{
				String query = "update data_migration_daily_log "
						+ "	set end_time=now(),	status =?,processed_count=?,added_count=?,updated_count=?,error_count=?,error_message=?"
						+ " where id=?";

				preparedStatement = con.prepareStatement(query);			
				preparedStatement.setString(1, status);
				preparedStatement.setInt(2, processedCount);
				preparedStatement.setInt(3, addedCount);
				preparedStatement.setInt(4, udpatedCount);
				preparedStatement.setInt(5, errorCount);
				preparedStatement.setString(6, errorMsg);
				preparedStatement.setLong(7, jobId);				
				preparedStatement.executeUpdate();
					
			} catch (SQLException e) {
				log.error("SQL Exception occured in updateLog: Unable to update values in data_migration_daily_log table in MySql"+e.getMessage());
				throw e;
			} finally {
				if(preparedStatement != null){
					preparedStatement.close();
				}
				
			}
			
   }
	 
	
}