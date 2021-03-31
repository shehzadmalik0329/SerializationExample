package com.xornet.controller;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.LineNumberReader;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import org.apache.log4j.Logger;
import com.xornet.dao.DataMigrationDailyLogDao;
import com.xornet.util.CopySrcSparshCsv;
import com.xornet.util.CopySrcSparshCsv2;
import com.xornet.util.MySqlDBConnection;
import com.xornet.util.SendEmail;

public class SparshToXornetEmpUploadCsv2 {
	private static Logger log = Logger.getLogger(SparshToXornetEmpUploadCsv2.class);
	private static final String JOB_NAME = "SPARSH to XORNET Employee Upload";
	private static final String EMAIL_SUBJECT = JOB_NAME+" Status";
	private static final String STATUS_FAILED = " : Failed";
	private static final String STATUS_COMPLETED = " : Completed";
	private static final Properties configFile = new java.util.Properties();
	private static final String propFileName = "config.properties";
	private static int totalNoOfRecords = 0;
	private static int successRecords = 0;
	private static Map<String, String> empFileMap = new HashMap<String, String>();

	private SparshToXornetEmpUploadCsv2(){
		log.info("SparshToXornetEmpUploadCsv constructor invoked");
	}
	public static void main(String[] args) throws IOException{
		try{
			log.info("SparshToXornetEmpUploadCsv main method invoked");
			addEmployeeDataFromSparshToXornet();
			
		} catch(Exception ex){
			ex.printStackTrace();
			log.error(EMAIL_SUBJECT+STATUS_FAILED +ex.getMessage());
			SendEmail.sendMail(EMAIL_SUBJECT+STATUS_FAILED,"Job failed due to unexpected error. Exception :" + ex.getMessage());
			return;
		}
	}
	
	private static void addEmployeeDataFromSparshToXornet() throws Exception {
		log.info("addEmployeeDataFromSparshToXornet method invoked");
		Connection con = null;
		PreparedStatement preparedStatementMysql = null;
		ResultSet rs = null;
		java.sql.Statement statement = null;
		SimpleDateFormat ft = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		StringBuffer sbErrorMsg = new StringBuffer("");
		long jobId = 0;
		log.info("\n********* Sparsh to Xornet Employee Data Upload : Started at "+ft.format(new Date())+"***************\n");

		try {
			con = MySqlDBConnection.getConnection();
			if(con == null){
				throw new RuntimeException("Could not connect to Xornet MySQL DB. Error :- Connection failed");
			}
			con.setAutoCommit(false);
			jobId = DataMigrationDailyLogDao.jobStarted(JOB_NAME, con);
			
			try {
				empFileMap = fileCopyStatus(con, jobId);
			} catch (Exception ex) {
				log.error(" Exception in addEmployeeDataFromSparshToXornet :: "+ex.getMessage());
				sbErrorMsg.append("Exception in addEmployeeDataFromSparshToXornet ::  "+ex.getMessage() +"\n");
				DataMigrationDailyLogDao.jobFailed(jobId, ex.getMessage(),con);
				throw ex;
			}
			
			//Transfer CSV data to database table
			for(Map.Entry<String, String> entry : empFileMap.entrySet()) {
				String tempName = entry.getKey().replace("\\", "/");
				int tempIndex = tempName.lastIndexOf("/");
				String empfileName = tempName.substring(tempIndex+1);
				
				clearPreviousData(con); //Truncate the earlier data

				try{
					String loadQuery = 
							"LOAD DATA LOCAL INFILE '"+tempName+"' INTO TABLE temp_table FIELDS ENCLOSED BY '\"' TERMINATED BY ','" + 
									" LINES TERMINATED BY '\r\n' IGNORE 1 LINES " +
									"(@empcode, @firstname, @middlename, @lastname, @empname, @designation, @designationdesc, @department, @location, @active,"+
									"@birthday, @birthdate, @birthmonth, @emailid, @supemployeecode, @grade, @fullgrade, @enddate, @joindate, @gender, @placeofwork, @username, @effective_from, @addressline1, @addressline2, @state, @city, @zip, @country, @hometelephone, @worktelephone, @homeemail, @assignment, @globaltransfer)"+
									"set upload_id=null, empcode=@empcode, firstname=@firstname, middlename=@middlename, lastname=@lastname, empname=@empname, designation=@designation, department=@department,"+
									"location=@location, active=@active, birthday=@birthday, birth_day=@birthdate, birth_month=@birthmonth, emailid=@emailid, sup_employee_code=@supemployeecode,"+
									"grade=@grade, full_grade=@fullgrade, End_Date=@enddate, JoinDate=@joindate, Gender=@gender, creation_date=NOW(),"+
									"designation_description=@designationdesc, place_of_work=@placeofwork, assignment=@assignment, global_transfer=@globaltransfer";

					log.info(loadQuery);
					preparedStatementMysql = con.prepareStatement(loadQuery);
					preparedStatementMysql.executeQuery();

				}catch (SQLException e) {
					log.error("SQL Exception in addEmployeeDataFromSparshToXornet :: "+e.getMessage());
					sbErrorMsg.append("SQL Exception in addEmployeeDataFromSparshToXornet ::  "+e.getMessage() +"\n");
				}
				finally{
					if (preparedStatementMysql != null) {
						preparedStatementMysql.close();
					}
				}

				if (sbErrorMsg.toString().equals("")){
					totalNoOfRecords = csvFileCount(tempName);
					successRecords = getNoOfRecords(con);
					CopySrcSparshCsv.jobCompleted(empfileName, con, "PROCESSED");
					String errorMsg = (sbErrorMsg.length()>500? sbErrorMsg.substring(0,500):sbErrorMsg.toString()) ;
					DataMigrationDailyLogDao.jobCompleted(jobId, totalNoOfRecords, successRecords, 0, 0, errorMsg,con);
				}
				sendStatusEmail(sbErrorMsg, totalNoOfRecords, successRecords);
				con.commit();
			}
			
		} 
		catch(SQLException ex){
			log.error("SQLException in addEmployeeDataFromSparshToXornet :: "+ex.getMessage());
			DataMigrationDailyLogDao.jobFailed(jobId, ex.getMessage(),con);
			con.rollback();
			throw ex;
		}
		catch(IOException ex){
			log.error("IOException in addEmployeeDataFromSparshToXornet :: "+ex.getMessage());
			DataMigrationDailyLogDao.jobFailed(jobId, ex.getMessage(),con);
			con.rollback();
			throw ex;
		}
		finally{
			if(rs!=null){
				rs.close();
			}
			if(statement !=null){
				statement.close();
			}
			if (con != null) {
				con.close();
			}
		}  	
		log.info("\n********* Sparsh to Xornet Employee Data Upload : Finished at "+ft.format(new Date())+"***************\n");
	}

	public static void clearPreviousData(Connection con) throws IOException,SQLException{
		PreparedStatement preparedStatement =null;
		int noOfRecords = getNoOfRecords(con);
		if(noOfRecords>=1){
			try {
				preparedStatement = con.prepareStatement("DELETE FROM temp_table");
				preparedStatement.executeUpdate();

				log.info("Record Deleted from temp_table");

			} catch (SQLException e) {
				log.error("SQL Exception occurred in clearPreviousData : unable to delete previous records"+e.getMessage());
				throw e;
			}finally{
				try {
					if (preparedStatement != null) {
						preparedStatement.close();
					}
				} catch (SQLException e) {
					log.error("SQL Exception in clearPreviousData while closing prepared stmt:: "+e.getMessage());
					throw e;
				}

			}  	
		}else{
			log.info("No previous records exist in temp_table,hence no DELETE performed");
		}

	}

	public static int getNoOfRecords(Connection con) throws IOException, SQLException{
		PreparedStatement preparedStatement =null;
		ResultSet rs =null;
		int recCount = 0;

		try {
			preparedStatement =con.prepareStatement("select count(*) from temp_table");
			rs=preparedStatement.executeQuery();

			if(rs.next()){
				recCount=rs.getInt(1);
			}
		}catch (SQLException e) {
			log.error("SQL Exception occurred in getNoOfRecords: unable to get count of records "+e.getMessage());
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
				log.error("SQL Exception in getNoOfRecords while closing preparedstmt and resultset:: "+e.getMessage());
				throw e;
			}
		}
		return recCount;
	}

	private static void sendStatusEmail(StringBuffer sbErrorMsg, int totalRecords, int successRecords) throws IOException{

		String emailSubject ="";
		String empUploadMsg ="";

		if (!sbErrorMsg.toString().equals("")){
			emailSubject =EMAIL_SUBJECT+STATUS_FAILED;
			empUploadMsg ="Hi,<br/><br/>"+
					"SPARSH to XORNET Employee Data Upload status::<br/><br/>"+ 
					"CSV Upload NOT Completed"+"<br/><br/><br/>";
			empUploadMsg += "Error Details:::<br/><br/>"+
					sbErrorMsg;
		}else{
			emailSubject =EMAIL_SUBJECT+STATUS_COMPLETED;
			empUploadMsg ="Hi,<br/><br/>"+
					"SPARSH to XORNET Employee Data Upload status::<br/><br/>"+ 
					"Total no of records processed : "+totalRecords+"<br/><br/><br/>" +
					 "No of records added to Xornet temp table : "+successRecords+"<br/><br/><br/>" +
					"CSV Upload Completed"+"<br/><br/><br/>";
		}
		empUploadMsg += "Regards,<br/>Xornet Team";

		try {
			SendEmail.sendMail(emailSubject,empUploadMsg);
		} catch (IOException e) {
			log.error("Unable to Send emails "+e.getMessage());
			throw e;
		}

	}	
	
	private static Map<String, String> fileCopyStatus(Connection con, long jobId) throws Exception{
		configFile.load(Thread.currentThread().getContextClassLoader().getResourceAsStream(propFileName));
		
		// Local directory to receive file
		String localDir = configFile.getProperty("emp_local_dir");
		String filePrefix = configFile.getProperty("emp_file_prefix");

		//Pass the file name convention and the local path
		Map<String, String> filesMap = CopySrcSparshCsv2.copyFile(filePrefix, localDir);
		filesMap.entrySet().forEach(entry -> {
			try {
				processFileData(entry, con , jobId, filesMap);
			} catch (Exception e) {
				log.error("Error in processing SparshToXornetEmpUploadCsv " + e.getMessage());
			}
			
		});
		
		//No files to process
		if(filesMap.isEmpty()) {
			if (con != null) {
				con.close();
			}
			System.exit(0);
		}
		
		return filesMap;

	}
	
	private static void processFileData(Entry<String, String> entry, Connection con, long jobId, Map<String, String> filesMap) throws Exception {
		String message = entry.getValue();
		String empFile = entry.getKey();
		String localMsg = "";
		if(message.equals("NO_FILE_PRESENT")) {
			localMsg= " Employee data upload job : Sparsh csv file is missing at Ramco server. ";
			generateDataMigrationLog(entry, con, jobId, filesMap, message, localMsg);
		}
		else if(message.equals("NO_DATA")) {
				localMsg= " Employee data upload job : Latest Sparsh csv file " + empFile+ "  found at Ramco server  has no data . ";
				generateDataMigrationLog(entry, con, jobId, filesMap, message, localMsg);
		}
		else if(message.equals("ALREADY_PROCESSED")) {
				localMsg= " Employee data upload job : No New csv file found. Latest Sparsh source csv file " + empFile + " found at Ramco server was already processed. ";
				generateDataMigrationLog(entry, con, jobId, filesMap, message, localMsg);
		}
	}
	
	private static void generateDataMigrationLog(Entry<String, String> entry, Connection con, long jobId,
			Map<String, String> filesMap, String message, String localMsg) throws SQLException, IOException {
		String empFile = entry.getKey();
		try {
			DataMigrationDailyLogDao.jobFailed(jobId, localMsg, con);
			CopySrcSparshCsv.jobCompleted(empFile, con, message);
			SendEmail.sendMail(EMAIL_SUBJECT+STATUS_FAILED, localMsg + empFile 
					+ " <br><br>Check log for more details..");
			} catch (IOException e) {
			log.error("Error in SparshToXornetEmpUploadCsv.copyFileStatus-->"+message+": " + e.getMessage());
			DataMigrationDailyLogDao.jobFailed(jobId, localMsg, con);
			CopySrcSparshCsv.jobCompleted(empFile, con, message);
			throw e;
		} finally {
			filesMap.remove(empFile);
		}
	}
	
	private static int csvFileCount(String fileName){
		File file =new File(fileName);
		int lineCount = 0;
		try{
    		if(file.exists()){
    		    FileReader fr = new FileReader(file);
    		    LineNumberReader lnr = new LineNumberReader(fr);
	            while (lnr.readLine() != null){
	            	lineCount++;
	            }
	            lnr.close();
	            lineCount--;
    		}
		} catch(IOException e){
			log.error("Error in SparshToXornetEmpUploadCsv.csvFileCount: " + e.getMessage());
		}
		return lineCount;
	}
}


