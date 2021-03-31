package com.xornet.util;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

import org.apache.log4j.Logger;

public class MySqlDBConnection {

    private static Connection connection = null;
    private static Logger log=Logger.getLogger(MySqlDBConnection.class.getName());
    private MySqlDBConnection(){  
    	log.info("MySqlDBConnection constructor invoked");
    }
    public static void main (String[] args) throws IOException{
    	getConnection();
    }
    
    public static Connection getConnection() throws IOException {
	try {
    	if (connection != null && connection.isValid(3000)) {
    		log.info("*****MySqlDBConnection : Getting old Connection *****\n"); 
    	    return connection;
        } else{
        	log.info(" ***** Trying to connect to MySql ...  *******");
        	String propFileName = "config.properties";
			Properties configFile = new java.util.Properties();
			configFile.load(Thread.currentThread().getContextClassLoader().getResourceAsStream(propFileName));
			String driver = configFile.getProperty("mysqlDriver");
	        String url = configFile.getProperty("mysqlDatabaseUrl");
	        String user = configFile.getProperty("mysqlDbuser");
	        String password = configFile.getProperty("mysqlDbpassword");
	        log.info("Driver => "+driver+"");
	        log.info("URL =>"+url+"");
	        log.info("User =>"+user+"");
	        log.info("Password =>"+password+"");
	        Class.forName(driver);
            connection = DriverManager.getConnection(url, user, password);
            if (connection != null) {
            	 log.info("*****Conneted to MySQL DBconnection *****\n");
            }else{
            	 log.info("*****Unable to connect to MySQL DB *****\n");
            }
        }
    } catch (ClassNotFoundException e) {
    	log.info("MySqlDBConnection : ClassNotFoundException occured "+e.getMessage());
    } catch (SQLException e) {
    	log.info("MySqlDBConnection : SQLException occured "+e.getMessage());
    }
    return connection;
}
    
    public static void closeConnection(){
    	try {
			connection.close();
		} catch (SQLException e) {
			log.info("Exception occured while closing connection: "+e.getMessage());
		}
    }
    
}