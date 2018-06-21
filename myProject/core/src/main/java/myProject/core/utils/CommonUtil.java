/**
 * Package Section
 */
package myProject.core.utils;

/**
 * Import Section
 */
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Iterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.io.IOUtils;
import org.apache.sling.commons.json.JSONArray;
import org.apache.sling.commons.json.JSONException;
import org.apache.sling.commons.json.JSONObject;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.ServiceReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;

import myProject.core.Constants;
import myProject.core.services.ProxyConnectionHelper;
import myProject.core.services.StandardConfigService;


public class CommonUtil {

	private static Logger logger = LoggerFactory.getLogger(CommonUtil.class);
	
	public static boolean HTMLEscapeFlag = false;
	public static boolean HTMLSpecialCharEscapeFlag = false;
	
	private static final Pattern tabPattern = Pattern.compile("\t");
	private static final Pattern newLinePattern = Pattern.compile("\n");
	private static final Pattern returnPattern = Pattern.compile("\r");
	
	/**
	 * @desc - function to check if string is not null or empty
	 * @param str - String variable
	 * @return boolean value
	 */
	public final static boolean isNotNullOrEmpty(String str){
		if(Constants.NULL != str && str.trim().length() !=0 ){
			return true;
		}
		return false;
	}

	public final static String getJsonString(JSONObject jsonObject, String key){
		if(jsonObject !=null && jsonObject.has(key) && !jsonObject.isNull(key)){
			try {
				return jsonObject.get(key).toString();
			} catch (JSONException e) {
				logger.error("JSONException **", e);
			}
		}
		return "";

	}

	public final static int getJsonInt(JSONObject jsonObject, String key){
		if(jsonObject !=null && jsonObject.has(key) && !jsonObject.isNull(key)){
			try {
				return jsonObject.getInt(key);
			} catch (JSONException e) {
				logger.error("JSONException **", e);
			}
		}
		return 0;

	}

	public static void safeClose(InputStream is) {
		if (is != null) {
			try {
				is.close();
			} catch (IOException e) {
				logger.error("IOException **", e);
			}
		}
	}
	
	/**
	 * Parsing an object
	 * @param object
	 * @return
	 * @throws JSONException
	 */
	public final static JSONObject traverseJsonObjectAndEliminateHTMLChar(JSONObject object) throws JSONException 
	{
		JSONObject displayableJsonObj = new JSONObject();

		Iterator<String> keysItr = object.keys();
		while(keysItr.hasNext()) 
		{
			String key = keysItr.next();
			Object value = object.get(key);

			if(value instanceof JSONArray) 
			{
				value = traverseJsonArrayAndEliminateHTMLChar((JSONArray) value);
			}
			else if(value instanceof JSONObject) 
			{
				value = traverseJsonObjectAndEliminateHTMLChar((JSONObject) value);
			}
			else if(value instanceof String)
			{
				if(!HTMLSpecialCharEscapeFlag)
				{
					value = replaceHTMLSpecialChar((String)value);
				}
			}

			displayableJsonObj.put(key, value);
		}
		return displayableJsonObj;
	}

	/**
	 * 
	 * @param array
	 * @return
	 * @throws JSONException
	 */
	private final static JSONArray traverseJsonArrayAndEliminateHTMLChar(JSONArray array) throws JSONException
	{
		JSONArray displayableJsonArray = new JSONArray();
		for(int i = 0; i < array.length(); i++) 
		{
			Object value = array.get(i);
			if(value instanceof JSONArray) 
			{
				value = traverseJsonArrayAndEliminateHTMLChar((JSONArray) value);
			}
			else if(value instanceof JSONObject) 
			{
				value = traverseJsonObjectAndEliminateHTMLChar((JSONObject) value);
			}
			else if(value instanceof String)
			{
				if(!HTMLSpecialCharEscapeFlag)
				{
					value = replaceHTMLSpecialChar((String)value);
				}
			}
			displayableJsonArray.put(value);
		}
		return displayableJsonArray;
	}

	/**
	 * 
	 * @param contentValue
	 * @return
	 */
	public final static String replaceHTMLSpecialChar(String contentValue)
	{
		String contetnStringAfterHtmlUnescape =null;
		try
		{
			//remove html special char
			contetnStringAfterHtmlUnescape = contentValue.replaceAll(" *&(\\w+); *", "");

			//remove html tag
			if(!HTMLEscapeFlag)
			{
				contetnStringAfterHtmlUnescape = contetnStringAfterHtmlUnescape.replaceAll("\\<.*?\\>", "");
				
				//adjustment for double quotation in string
				contetnStringAfterHtmlUnescape = contetnStringAfterHtmlUnescape.replaceAll("\\\"", "");
			}
			
		}
		catch(PatternSyntaxException ex)
		{
			//TODO::
			logger.error("Regex syntax is worng, Caused by : " + ex.getCause());
		}
		return contetnStringAfterHtmlUnescape;
	}
	
	/**
	 * 
	 * @param url
	 * @return
	 */
	public static String getXMLStringFromURL(String url)
	{
		String xmlString = "";
		BufferedReader br = null;
		HttpURLConnection conn = null;
		
		//Getting the values from osgi configuration
		try 
		{
			BundleContext bundleContext = FrameworkUtil.getBundle(CommonUtil.class).getBundleContext();
			ServiceReference serviceReferenceEnvConfig = bundleContext.getServiceReference(StandardConfigService.class.getName());
			StandardConfigService configService = (StandardConfigService) bundleContext.getService(serviceReferenceEnvConfig);
			
			ServiceReference serviceReferenceProcyHelper = bundleContext.getServiceReference(ProxyConnectionHelper.class.getName());
			ProxyConnectionHelper proxyConnectionHelper = (ProxyConnectionHelper) bundleContext.getService(serviceReferenceProcyHelper);
			
			URL urlObject = new URL(url);
			conn = (HttpURLConnection) urlObject.openConnection();
			conn.setRequestMethod("GET");
			conn.setRequestProperty("Accept", "application/json");
			
			if (null != configService) 
			{
				if (null != configService.getProperty("useProxy") &&
							configService.getProperty("useProxy").equalsIgnoreCase("true")) 
				{
					proxyConnectionHelper.setProxy();
				}
				else
				{
					proxyConnectionHelper.clearProxy();
				}
			} 
			else
			{
				logger.error("HSPropertiesUtil instance is null");
			}

			if (conn.getResponseCode() != 200) 
			{
				throw new RuntimeException("Failed : HTTP error code : " + conn.getResponseCode());
			}
			else 
			{
				/* get data from input stream */
				br = new BufferedReader(new InputStreamReader((conn.getInputStream())));

				/* get xml in the response, you received from above stream */
				xmlString = IOUtils.toString(br);
			}
		}
		catch (MalformedURLException e)
		{
			logger.error("MalformedString exception in " , e);
		}
		catch (IOException e) 
		{
			logger.error("IOException exception in " , e);
		}
		finally
		{
			try 
			{
				if (br != null) 
				{
					br.close();
				}
				
				if (conn != null) 
				{
					conn.disconnect();
				}
			} 
			catch (IOException ex) 
			{
				logger.error("Connection can not be closed, Caused by : " + ex.getCause());
			}
		}
		return xmlString;
	}
	
	/**
	 * 
	 * @param xml
	 * @return
	 */
	public static JSONObject getJsonFromXMLString(String xml)
	{
		JSONObject jsonObj = null;
		try
		{
			Matcher matcher = null;
			if(null!=xml){
				String xmlStringWithoutLineFeed = xml.trim();
				matcher = returnPattern.matcher(xmlStringWithoutLineFeed);
				xmlStringWithoutLineFeed = matcher.replaceAll("");
				matcher = tabPattern.matcher(xmlStringWithoutLineFeed);
				xmlStringWithoutLineFeed = matcher.replaceAll("");
				matcher = newLinePattern.matcher(xmlStringWithoutLineFeed);				
				xmlStringWithoutLineFeed = matcher.replaceAll("");			
				
				jsonObj = XMLPayloadParser.getInstance().parseXMLPayloadToJSON(xmlStringWithoutLineFeed, false);
			}
		    
			if(null != jsonObj)
			{
				jsonObj = traverseJsonObjectAndEliminateHTMLChar(jsonObj);
			}
		}
		catch (SAXException sae) 
		{
			logger.error("SAX ParseException occurred, Caused by : " + sae.getCause());
		}
		catch (ParserConfigurationException pce) 
		{
			logger.error("File does not exist, Caused by : " + pce.getCause());
		}
		catch(FileNotFoundException ex)
		{
			logger.error("File does not exist, Caused by : " + ex.getCause());
		}
		catch(SecurityException ex)
		{
			logger.error("Can't read a file, Caused by : " + ex.getCause());
		}
		catch(IOException ex)
		{
			logger.error("I/O erroe occurred, Caused by : " + ex.getCause());
		}
		catch(JSONException ex)
		{
			logger.error("Can't parse to JSON format, Caused by : " + ex.getCause());
		}
		return jsonObj;	    
	}
	
	public static String getXMLStringFromAuthorizedURL(String url, String username, String password)
	{
		String xmlString = "";
		BufferedReader br = null;
		HttpURLConnection conn = null;
		
		//Getting the values from osgi configuration
		try 
		{
			BundleContext bundleContext = FrameworkUtil.getBundle(CommonUtil.class).getBundleContext();
			ServiceReference serviceReferenceEnvConfig = bundleContext.getServiceReference(StandardConfigService.class.getName());
			StandardConfigService configService = (StandardConfigService) bundleContext.getService(serviceReferenceEnvConfig);
			
			ServiceReference serviceReferenceProcyHelper = bundleContext.getServiceReference(ProxyConnectionHelper.class.getName());
			ProxyConnectionHelper proxyConnectionHelper = (ProxyConnectionHelper) bundleContext.getService(serviceReferenceProcyHelper);			
			
			String encoding = new String(
					 org.apache.commons.codec.binary.Base64.encodeBase64   
					    (org.apache.commons.codec.binary.StringUtils.getBytesUtf8(username + ":" +password))
					  );

			URL urlObject = new URL(url);
			conn = (HttpURLConnection) urlObject.openConnection();
			conn.setRequestMethod("GET");
			conn.setDoOutput(true);
			conn.setRequestProperty("Authorization", "Basic " + encoding);
			
			if (null != configService) 
			{
				if (null != configService.getProperty("useProxy") &&
							configService.getProperty("useProxy").equalsIgnoreCase("true")) 
				{
					proxyConnectionHelper.setProxy();
				}
				else
				{
					proxyConnectionHelper.clearProxy();
				}
			} 
			else
			{
				logger.error("HSPropertiesUtil instance is null");
			}

			if (conn.getResponseCode() != 200) 
			{
				throw new RuntimeException("Failed : HTTP error code : " + conn.getResponseCode());
			}
			else 
			{
				/* get data from input stream */
				br = new BufferedReader(new InputStreamReader((conn.getInputStream())));

				/* get xml in the response, you received from above stream */
				xmlString = IOUtils.toString(br);
			}
		}
		catch (MalformedURLException e)
		{
			logger.error("MalformedString exception in " , e);
		}
		catch (IOException e) 
		{
			logger.error("IOException exception in " , e);
		}
		finally
		{
			try 
			{
				if (br != null) 
				{
					br.close();
				}
				
				if (conn != null) 
				{
					conn.disconnect();
				}
			} 
			catch (IOException ex) 
			{
				logger.error("Connection can not be closed, Caused by : " + ex.getCause());
			}
		}
		return xmlString;
	}
}
