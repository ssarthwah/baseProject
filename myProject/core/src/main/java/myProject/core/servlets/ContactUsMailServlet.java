package myProject.core.servlets;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.jcr.Node;
import javax.jcr.Session;
import javax.servlet.Servlet;
import javax.servlet.ServletException;

import org.apache.commons.mail.HtmlEmail;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Properties;
import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.Service;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;
import org.apache.sling.commons.json.JSONObject;
import org.apache.sling.jcr.api.SlingRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.day.cq.mailer.MessageGateway;
import com.day.cq.mailer.MessageGatewayService;

import myProject.core.services.StandardConfigService;
import myProject.core.utils.ContactUsMailHandler;


@Service(Servlet.class)
@Component(immediate = true, metatype = false)
@Properties({
		@Property(name = "sling.servlet.paths", value = { "/bin/services/ContactUs" }),
		@Property(name = "sling.servlet.methods", value = { "POST" }) })
public class ContactUsMailServlet extends SlingAllMethodsServlet {

	private static final Logger logger = LoggerFactory.getLogger(ContactUsMailServlet.class);
	@Reference
	private MessageGatewayService messageGatewayService;
	
	@Reference
	private  StandardConfigService environmentConfigService;
	
	@Reference
	public SlingRepository repository;
	protected void execute(SlingHttpServletRequest request,
			SlingHttpServletResponse response) throws ServletException,
			IOException {
		
        logger.error("ClientURl****"+environmentConfigService.getProperty("contactUsClientURL"));
        
		
		//sendMail(request);
		consumerData(request,environmentConfigService.getProperty("contactUsClientURL"));
		
	}
	
	private void sendMail(SlingHttpServletRequest request) {
		// TODO Auto-generated method stub
		Session session = null;
		InputStream is = null;
		String finalMsg=null;
		BufferedInputStream bis = null;
		try
		{
			session = repository.loginAdministrative(null);

			String templateReference = "etc/designs/myProject/notifications/contactUs.txt/jcr:content";

			Node root = session.getRootNode();
			
			Node jcrContent = root.getNode(templateReference);
			is = jcrContent.getProperty("jcr:data").getBinary().getStream();
			bis = new BufferedInputStream(is);
			ByteArrayOutputStream buf = new ByteArrayOutputStream();
			int resultNumber = bis.read();
			while (resultNumber != -1) {
				byte b = (byte) resultNumber;
				buf.write(b);
				resultNumber = bis.read();
			}   
		
		finalMsg= buf.toString();
		
		
			
		}
		catch(Exception e)
		{
			logger.error("ContactUsServlet="+e);
		}
		String email=request.getParameter("email");
		
		ContactUsMailHandler contactUsMailHandler=new ContactUsMailHandler();
		contactUsMailHandler.sendContactUsMail(request,email,this.messageGatewayService,finalMsg);
		MessageGateway<HtmlEmail> messageGateway = this.messageGatewayService.getGateway(HtmlEmail.class);
		
		
	}
	
	private void consumerData(SlingHttpServletRequest request, String consumerDataUrl)
	{
	
		Session session = null;
		InputStream is = null;
		String consumerDataBody=null;
		BufferedInputStream bis = null;
		try
		{
			session = repository.loginAdministrative(null);

			String templateReference = "etc/designs/myProject/notifications/consumerData.txt/jcr:content";

			Node root = session.getRootNode();
			
			Node jcrContent = root.getNode(templateReference);
			is = jcrContent.getProperty("jcr:data").getBinary().getStream();
			bis = new BufferedInputStream(is);
			ByteArrayOutputStream buf = new ByteArrayOutputStream();
			int resultNumber = bis.read();
			while (resultNumber != -1) {
				byte b = (byte) resultNumber;
				buf.write(b);
				resultNumber = bis.read();
			}  
			
			consumerDataBody= buf.toString();
			
			
			
		
			Date now = new Date();
			SimpleDateFormat dateFormatter = new SimpleDateFormat("y-M-d 'at' h:m:s a");
	    dateFormatter = new SimpleDateFormat("dd/MM/YYYY hh:mm:ss a");
	   
	    String timeStamp=dateFormatter.format(now);
	    
	    String imageLocations=request.getParameter("image1")+" "+request.getParameter("image2")+" "+request.getParameter("image3");
	   
	    String storeState="";
		String storeExist="";
		
		if(request.getParameter("storestateProvince").trim().length()==0)
		{
			storeState=request.getParameter("countryStateValue");
		}
		else
		{
			storeState=request.getParameter("storestateProvince");
		}
		
		if(request.getParameter("storeName").trim().length()==0)
		{
			
			storeExist="";
		}
		else
		{
			storeExist="S";
		}
		JSONObject consumerData=new JSONObject();
		String selectdeLanguagaeCode=languageCode(request.getParameter("selectedLanguage"));
		
		consumerData.put("selectedLanguage",selectdeLanguagaeCode);
		consumerData.put("birth_date",request.getParameter("birth_date"));
		consumerData.put("countryName",request.getParameter("countryName"));
		consumerData.put("selectSubject",new String(request.getParameter("selectSubject").getBytes("ISO-8859-1"), Charset.forName("UTF-8")));
		consumerData.put("gender",new String(request.getParameter("gender").getBytes("ISO-8859-1"), Charset.forName("UTF-8")));
		consumerData.put("firstName",new String(request.getParameter("firstName").getBytes("ISO-8859-1"), Charset.forName("UTF-8")));
		consumerData.put("lastName",new String(request.getParameter("lastName").getBytes("ISO-8859-1"), Charset.forName("UTF-8")));
		consumerData.put("email",new String(request.getParameter("email").getBytes("ISO-8859-1"), Charset.forName("UTF-8")));
		consumerData.put("daytimePhone",new String(request.getParameter("daytimePhone").getBytes("ISO-8859-1"), Charset.forName("UTF-8")));
		consumerData.put("streetAddress1",new String(request.getParameter("streetAddress1").getBytes("ISO-8859-1"), Charset.forName("UTF-8")));
		consumerData.put("streetAddress2",new String(request.getParameter("streetAddress2").getBytes("ISO-8859-1"), Charset.forName("UTF-8")));
		consumerData.put("city",new String(request.getParameter("city").getBytes("ISO-8859-1"), Charset.forName("UTF-8")));
		consumerData.put("stateProvince",new String(request.getParameter("stateProvince").getBytes("ISO-8859-1"), Charset.forName("UTF-8")));
		consumerData.put("postalcode",new String(request.getParameter("postalcode").getBytes("ISO-8859-1"), Charset.forName("UTF-8")));
		consumerData.put("product",new String(request.getParameter("product").getBytes("ISO-8859-1"), Charset.forName("UTF-8")));
		consumerData.put("dateOfPurchase",new String(request.getParameter("dateOfPurchase").getBytes("ISO-8859-1"), Charset.forName("UTF-8")));
		consumerData.put("upcCode",new String(request.getParameter("upcCode").getBytes("ISO-8859-1"), Charset.forName("UTF-8")));
		consumerData.put("manufacturerCode",new String(request.getParameter("manufacturerCode").getBytes("ISO-8859-1"), Charset.forName("UTF-8")));
		consumerData.put("storeName",new String(request.getParameter("storeName").getBytes("ISO-8859-1"), Charset.forName("UTF-8")));
		consumerData.put("storeCountry",new String(request.getParameter("storeCountry").getBytes("ISO-8859-1"), Charset.forName("UTF-8")));
		consumerData.put("storeCity",new String(request.getParameter("storeCity").getBytes("ISO-8859-1"), Charset.forName("UTF-8")));
		consumerData.put("storeAddress1",new String(request.getParameter("storeAddress1").getBytes("ISO-8859-1"), Charset.forName("UTF-8")));
		consumerData.put("storeAddress2",new String(request.getParameter("storeAddress2").getBytes("ISO-8859-1"), Charset.forName("UTF-8")));
		consumerData.put("storestateProvince",storeState);
		consumerData.put("storeExist",storeExist);
		
		consumerData.put("comments",new String(request.getParameter("comments").getBytes("ISO-8859-1"), Charset.forName("UTF-8")));
		
		
		consumerData.put("didYouFind",new String(request.getParameter("didYouFind").getBytes("ISO-8859-1"), Charset.forName("UTF-8")));
		consumerData.put("imageLocations",imageLocations);
		consumerData.put("userAgent",request.getParameter("userAgent"));
		consumerData.put("TimeStamp",timeStamp);
		consumerData.put("followUpUrl",request.getParameter("followUpUrl"));
		consumerData.put("referralUrl",request.getParameter("followUpUrl"));
		ContactUsMailHandler contactUsMailHandler=new ContactUsMailHandler();
		contactUsMailHandler.sendConsumerData(request,consumerDataUrl,this.messageGatewayService,consumerDataBody, consumerData);
		}
		catch(Exception e)
		{
			logger.error("ContactUsServlet=*"+e);
			
		}
	}
	
private String languageCode(String language)
{

	if(language.equalsIgnoreCase("english"))
	{
		return "en";
	}
	
	else if(language.equalsIgnoreCase("francias"))
	{
		return "fr";
	}
	else if(language.equalsIgnoreCase("espanol"))
	{
		return "es";
	}
	else if(language.equalsIgnoreCase("portugues"))
	{
		return "pt";
	}
	else if(language.equalsIgnoreCase("japaneas"))
	{
		return "ja";
	}
	else if(language.equalsIgnoreCase("chinese"))
	{
		return "zh";
	}
	else if(language.equalsIgnoreCase("rusian"))
	{
		return "ko";
	}
	
	else
	{
		return "en";
	}
}


}
