package myProject.core.utils;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class FinancialAlerts 
{
	private static final Logger LOGGER = LoggerFactory.getLogger(FinancialAlerts.class);

	public String createFinancialAlert(String alertsList, String emailId,String notSelectedList)
	{
		String xmlStr = "";
		try 
		{
			ArrayList<String> userInput=new ArrayList<String>();
			for(String retval: alertsList.split(","))
			{
				userInput.add(retval);
			}
			
			DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder docBuilder = docFactory.newDocumentBuilder();

			Document doc = docBuilder.newDocument();
			Element rootElement = doc.createElement("ALERT_SUBSCRIPTION");
			doc.appendChild(rootElement);

			Element company = doc.createElement("COMPANY");
			rootElement.appendChild(company);

			Attr attr = doc.createAttribute("CORPORATE_MASTER_ID");
			attr.setValue("115590");
			company.setAttributeNode(attr);

			Element members=doc.createElement("MEMBERS");
			company.appendChild(members);

			Element member=doc.createElement("MEMBER");
			members.appendChild(member);

			Element emailAddress=doc.createElement("EMAIL_ADDRESS");
			emailAddress.appendChild(doc.createTextNode(emailId));
			member.appendChild(emailAddress);

			Element alerts=doc.createElement("ALERTS");
			member.appendChild(alerts);

			for (String retval: alertsList.split(","))
			{
				Element alert=doc.createElement("ALERT");
				Attr subscription=doc.createAttribute("SUBSCRIBE"); 
				subscription.setValue("YES");
				alert.setAttributeNode(subscription);
				alert.appendChild(doc.createTextNode(retval.trim()));
				alerts.appendChild(alert);
			}

			for (String retval: notSelectedList.split(","))
			{
				Element alert=doc.createElement("ALERT");
				Attr subscription=doc.createAttribute("SUBSCRIBE"); 
				subscription.setValue("NO");
				alert.setAttributeNode(subscription);
				alert.appendChild(doc.createTextNode(retval.trim()));
				alerts.appendChild(alert);
			}      

			// get xml content as a string
			StringWriter sw = new StringWriter();
			
	        TransformerFactory tf = TransformerFactory.newInstance();
	        Transformer transformer = tf.newTransformer();
	        transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "no");
	        transformer.setOutputProperty(OutputKeys.METHOD, "xml");
	        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
	        transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");

	        transformer.transform(new DOMSource(doc), new StreamResult(sw));
	        xmlStr = sw.toString();

		} 
		catch (ParserConfigurationException pce)
		{
			LOGGER.error("ParserConfigurationException occured"+pce);
		} 
		catch (TransformerException tfe)
		{
			LOGGER.error("ParserConfigurationException occured"+tfe);
		}
		
		return xmlStr;
	}
	
	public boolean HTTPWebRequest(String targetURL, String urlParameters) 
	{
		HttpURLConnection connection = null;  
		try
		{
			URL url = new URL(targetURL);

			//	Creating connection and setting the connection properties
			connection = (HttpURLConnection)url.openConnection();
			connection.setRequestMethod("POST");
			connection.setRequestProperty("Content-Type","application/x-www-form-urlencoded");
			connection.setRequestProperty("Content-Length",Integer.toString(urlParameters.getBytes().length));
			connection.setRequestProperty("Content-Language", "en-US");  
			connection.setUseCaches(false);
			connection.setDoOutput(true);
			
			//	Posting the parameters
			DataOutputStream wr = new DataOutputStream (connection.getOutputStream());
			wr.writeBytes(urlParameters);
			wr.flush();
			wr.close();
			//	Reading from the response
			java.io.InputStream is =  connection.getInputStream();
			BufferedReader rd = new BufferedReader(new InputStreamReader(is));
			StringBuilder response = new StringBuilder();
			String line;
			while((line = rd.readLine()) != null) {
				response.append(line);
				response.append('\r');
			}
			rd.close();
			return true;
		}
		catch (Exception e) {
			LOGGER.error("In the Exception of HTTPWebRequest of Financial Alerts" , e);
			return false;
		} 
		finally {
			if(connection != null) {
				connection.disconnect(); 
			}
		}
	}
}


