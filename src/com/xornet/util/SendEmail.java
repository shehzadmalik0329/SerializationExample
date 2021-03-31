package com.xornet.util;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.*;
import javax.mail.internet.*;
import org.apache.log4j.Logger;

public class SendEmail
{
	 static Logger log = Logger.getLogger(SendEmail.class);
	 
	 public static void main(String [] args) throws IOException{
		 sendMail("Email Sending Feature Testing","Hi,<br/><br/><span style='color:black;font-family:'Times New Roman', Times, serif;font-size:9px;'>This is a test email , please ignore it.<br/></span><br/>Regards,<br/>Xornet Team");
	 }
	 
	 
	 public static void sendMail(String moduleName,String msg) throws IOException
	 {
		
	   Date dNow = new Date();
	   SimpleDateFormat ft = new SimpleDateFormat("dd MMMM yyyy");
	   SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
	   String propFileName 			= "emails.properties";
	   Properties configFile		= new java.util.Properties();
	   configFile.load(Thread.currentThread().getContextClassLoader().getResourceAsStream(propFileName));
	   String to 					= configFile.getProperty("emailsTo");
	   String from 					= configFile.getProperty("emailsFrom");
       String host 					= configFile.getProperty("emailsHost");
       String emailsServer 			= configFile.getProperty("emailsSMTP");
       String emailsAttachFileName  = df.format(dNow)+".csv";
       String emailsAttachFilePath  = configFile.getProperty("emailsAttachFilePath")+emailsAttachFileName;
       String emailsSentSuccessMsg  = configFile.getProperty("emailsSentSuccessMsg");
       String emailsSubject 		= moduleName+" : "+ft.format(dNow);
       String emailsBody 			= msg;
       
       log.info(to);
       log.info(from);
       log.info(host);
       log.info(emailsServer);
       log.info(emailsSubject);
       log.info("Message from Cron Run => "+emailsBody);
       log.info(emailsAttachFilePath);
     
      // Get system properties
      Properties properties = System.getProperties();

      // Setup mail server
      properties.setProperty(emailsServer, host);

      // Get the default Session object.
      Session session = Session.getDefaultInstance(properties);

      try{
         // Create a default MimeMessage object.
         MimeMessage message = new MimeMessage(session);

         // Set From: header field of the header.
         message.setFrom(new InternetAddress(from));

         // Set To: header field of the header.
         message.addRecipient(Message.RecipientType.TO,new InternetAddress(to));
         
         // Set Subject: header field
         message.setSubject(emailsSubject);
         
         // Create the message part 
         BodyPart messageBodyPart = new MimeBodyPart();

         // Send the actual HTML message, as big as you like
         messageBodyPart.setContent(emailsBody,"text/html" );
         
         // Create a multipar message
         Multipart multipart = new MimeMultipart();

         // Set text message part
         multipart.addBodyPart(messageBodyPart);

        

         //Part Two for sending attachment
        // Part two is attachment
         if("Xornet to SPARSH Temp Card details Upload Status".equals(moduleName)){
        	 messageBodyPart = new MimeBodyPart();
             String filePath = emailsAttachFilePath;
             String fileName = emailsAttachFileName;       
             DataSource source = new FileDataSource(filePath);
             messageBodyPart.setDataHandler(new DataHandler(source));
             messageBodyPart.setFileName(fileName);
             multipart.addBodyPart(messageBodyPart);
         }
        
         //end part two 
         
         // Send the complete message parts
         message.setContent(multipart );
         
         
         // Send message
         try{
        	 Transport.send(message);
             log.info(emailsSentSuccessMsg);
         }catch (MessagingException ex){
             System.err.println("Cannot send email. " + ex);
             log.info("Unable to Send email");
         }
        
      }catch (MessagingException mex) {
         mex.printStackTrace();
      }
   }
}