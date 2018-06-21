package myProject.core.utils;

import java.util.ArrayList;

import javax.mail.internet.InternetAddress;

import org.apache.commons.mail.HtmlEmail;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.commons.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.day.cq.mailer.MessageGateway;
import com.day.cq.mailer.MessageGatewayService;

public class ContactUsMailHandler {

	private static final Logger logger = LoggerFactory.getLogger(ContactUsMailHandler.class);
	
	
	public void sendContactUsMail(SlingHttpServletRequest  request,String useremail,MessageGatewayService messageGatewayService,String finalMessage)
	{
		
		
		String toAddress=useremail;
		String fromAddress="no-reply@xyz.com";
		String message=finalMessage;
		try {
            
			
            
			ArrayList<InternetAddress> emailRecipients = new ArrayList<InternetAddress>();
			
			HtmlEmail email = new HtmlEmail();
			
			emailRecipients.add(new InternetAddress(toAddress));
			email.setCharset("UTF-8");
			email.setFrom(fromAddress);
			email.setTo(emailRecipients);
			email.setSubject("Thank you for contacting The Hershey Company.");
			email.setHtmlMsg(message);
          
			MessageGateway<HtmlEmail> messageGateway = messageGatewayService.getGateway(HtmlEmail.class);
			
			messageGateway.send(email);
            
			emailRecipients.clear();                     

		} catch (Exception e) {
			
			logger.error("ContactUsServlet*="+e);
		} 
		
		
	

	}

	public void sendConsumerData(SlingHttpServletRequest request,
			String clientmail, MessageGatewayService messageGatewayService,
			String consumerDataBody, JSONObject consumerData) {
		// TODO Auto-generated method stub
		
		String toAddress=clientmail;
		String fromAddress="no-reply@thehersheycompany.com";
		//String message=consumerDataBody;
		try {
			
			
           consumerDataBody = consumerDataBody.replace("${birthdate}",consumerData.getString("birth_date") );
           
           consumerDataBody = consumerDataBody.replace("${subject}",consumerData.getString("selectSubject") );
           
			
			consumerDataBody = consumerDataBody.replace("${email}", consumerData.getString("email"));
			
			
			consumerDataBody = consumerDataBody.replace("${First_Name}", consumerData.getString("firstName"));
			
			
			consumerDataBody = consumerDataBody.replace("${Last_Name}", consumerData.getString("lastName"));
			
			
			consumerDataBody = consumerDataBody.replace("${Gender}", consumerData.getString("gender"));
			
			
			consumerDataBody = consumerDataBody.replace("${Address_Line_1}", consumerData.getString("streetAddress1"));
			
			
			consumerDataBody = consumerDataBody.replace("${Address_Line_2}", consumerData.getString("streetAddress2"));
			
			
			consumerDataBody = consumerDataBody.replace("${City}", consumerData.getString("city"));
			
			
			consumerDataBody = consumerDataBody.replace("${State}", consumerData.getString("stateProvince"));
			
			
			consumerDataBody = consumerDataBody.replace("${Country}", consumerData.getString("countryName"));
			
			
			consumerDataBody = consumerDataBody.replace("${Zip}", consumerData.getString("postalcode"));
			
			
			consumerDataBody = consumerDataBody.replace("${Daytime_Phone}", consumerData.getString("daytimePhone"));
			
			
			consumerDataBody = consumerDataBody.replace("${Product}", consumerData.getString("product"));
			
			consumerDataBody = consumerDataBody.replace("${UPC_Code}", consumerData.getString("upcCode"));
			
			
			consumerDataBody = consumerDataBody.replace("${Manufacturer_Code}", consumerData.getString("manufacturerCode"));
			
			
			consumerDataBody = consumerDataBody.replace("${Date_of_Purchase}", consumerData.getString("dateOfPurchase"));
			
			
			consumerDataBody = consumerDataBody.replace("${Store_Exists}", consumerData.getString("storeExist"));
			
			
			consumerDataBody = consumerDataBody.replace("${Store_Name}", consumerData.getString("storeName"));
			
			
			consumerDataBody = consumerDataBody.replace("${Store_Address_Line_1}", consumerData.getString("storeAddress1"));
			
			
			consumerDataBody = consumerDataBody.replace("${Store_Address_Line_2}", consumerData.getString("storeAddress2"));
			
			
			consumerDataBody = consumerDataBody.replace("${Store_City}", consumerData.getString("storeCity"));
			
			
			consumerDataBody = consumerDataBody.replace("${Store_State}", consumerData.getString("storestateProvince"));
			
			
			consumerDataBody = consumerDataBody.replace("${Store_Country}", consumerData.getString("storeCountry"));
			
			consumerDataBody = consumerDataBody.replace("${Tried_to_Find_Information_on_Website}", consumerData.getString("didYouFind"));
			
			consumerDataBody = consumerDataBody.replace("${timeStamp}", consumerData.getString("TimeStamp"));
			
			consumerDataBody = consumerDataBody.replace("${language}", consumerData.getString("selectedLanguage"));
			
			consumerDataBody = consumerDataBody.replace("${user_agent}", consumerData.getString("userAgent"));
			
			consumerDataBody = consumerDataBody.replace("${followup_url}", consumerData.getString("followUpUrl"));
			
			consumerDataBody = consumerDataBody.replace("${referrer_url}", consumerData.getString("referralUrl"));
			
			consumerDataBody = consumerDataBody.replace("${comments}", consumerData.getString("comments"));
			
			
			
			consumerDataBody = consumerDataBody.replace("${Locations}", consumerData.getString("imageLocations"));
			
			ArrayList<InternetAddress> emailRecipients = new ArrayList<InternetAddress>();
			
			HtmlEmail email = new HtmlEmail();
			
			emailRecipients.add(new InternetAddress(toAddress));
			email.setCharset("UTF-8");
			email.setFrom(fromAddress);
			email.setTo(emailRecipients);
			email.setSubject(consumerData.get("selectSubject").toString());
			email.setHtmlMsg(consumerDataBody);
          
			MessageGateway<HtmlEmail> messageGateway = messageGatewayService.getGateway(HtmlEmail.class);
			
			logger.error("BeforeEmailSent**");
			messageGateway.send(email);
			logger.error("EmailSent**");
            
			emailRecipients.clear();                     

		} catch (Exception e) {
			
			logger.error("ContactUsServlet="+e.getCause());
			
		} 
		
	}
}
