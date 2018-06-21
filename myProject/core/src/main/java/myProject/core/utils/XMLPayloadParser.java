package myProject.core.utils;

import java.io.IOException;
import java.io.StringReader;
import java.util.Collection;
import java.util.Iterator;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.sling.commons.json.JSONException;
import org.apache.sling.commons.json.JSONObject;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * @author 
 */
public class XMLPayloadParser 
{

	private static XMLPayloadParser instance = null;

	public static XMLPayloadParser getInstance() 
	{
		if (instance == null) {
			instance = new XMLPayloadParser();
		}
		return instance;
	}

	public XMLPayloadParser() 
	{
		super();
	}

	/**
	 * Converts the XML represented by String "xmlStr" into JSON. 
	 */
	public JSONObject parseXMLPayloadToJSON(String xmlStr, 
											boolean parseNamespace) throws SAXException, 
																		   IOException,
																		   ParserConfigurationException,
																		   JSONException 
	{
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = factory.newDocumentBuilder();
		InputSource source = new InputSource(new StringReader(xmlStr));
		Document root = builder.parse(source);
		Element element = root.getDocumentElement();
		element.normalize();
		JSONObject jsonObject = parseElement(element, parseNamespace);
		return jsonObject;
	}

	private JSONObject parseElement(Element element,
									boolean parseNamespace) throws JSONException 
	{
		JSONObject childJsonObject = doParse(element, parseNamespace);
		String elementName = getNodeName(element, parseNamespace);
		JSONObject responseJsonObject = new JSONObject();
		addToJson(elementName, childJsonObject, responseJsonObject);
		return responseJsonObject;
	}

	private JSONObject doParse(Element element, 
							   boolean parseNamespace) throws JSONException
	{
		JSONObject jsonObject = new JSONObject();
		// Parse element text.
		parseText(element, jsonObject);
		// Parse element attributes.
		parseAttributes(element, jsonObject, parseNamespace);
		// Parse child elements.
		parseChildren(element, jsonObject, parseNamespace);
		if(jsonObject.length()==0)
		{
			jsonObject.put(element.getNodeName(),"");
		}
		return jsonObject;
	}

	private void parseText(Element element, 
						   JSONObject jsonObject) throws JSONException 
	{
		NodeList childNodes = element.getChildNodes();
		boolean keyFlag = element.getAttributes().getLength() > 0 ? true:false;
		for (int i = 0; i < childNodes.getLength(); i++) {
			Node childNode = (Node) childNodes.item(i);
			if (childNode.getNodeType() == Node.TEXT_NODE || childNode.getNodeType() == Node.CDATA_SECTION_NODE) {
				String elementValue = childNode.getNodeValue().trim();
				String elementName = childNode.getParentNode().getNodeName().trim();
				if (elementValue != null && hasValue(elementValue)) 
				{
					if(keyFlag)
					{
						addToJson("content", elementValue, jsonObject);
					}
					else
					{
						addToJson(elementName, elementValue, jsonObject);
					}
				}
			}
		}
	}

	private void parseAttributes(Element element, 
								 JSONObject jsonObject,
								 boolean parseNamespace) throws JSONException 
	{
		NamedNodeMap attributeMap = element.getAttributes();
		for (int i = 0; i < attributeMap.getLength(); i++) {
			Node attribute = (Node) attributeMap.item(i);
			String attributeName = getNodeName(attribute, parseNamespace);
			
			if (attributeName != null) {
				String attributeValue = attribute.getNodeValue().trim();
				addToJson((attributeName), attributeValue, jsonObject);
			}
		}
	}

	private void parseChildren(Element element, 
							   JSONObject jsonObject, 
							   boolean parseNamespace) throws JSONException 
	{
		NodeList childNodes = element.getChildNodes();
		for (int i = 0; i < childNodes.getLength(); i++) {
			Node childNode = (Node) childNodes.item(i);
			if (childNode.getNodeType() == Node.ELEMENT_NODE) {
				String childName = getNodeName(childNode, parseNamespace);
				JSONObject childJsonObject = doParse((Element) childNode, parseNamespace);

				int attributeCount = childJsonObject.length();
				if(attributeCount == 1)
				{
					String attributeName = null;
					Object attributeValue = null;
					Iterator itr = childJsonObject.keys();
					while(itr.hasNext()) 
					{
						attributeName = (String)itr.next();
						attributeValue = childJsonObject.get(attributeName.toString());
					}
					if(childName.equals(attributeName))
					{
						addToJson(childName, attributeValue, jsonObject);
					}
					else
					{
						addToJson(childName, childJsonObject, jsonObject);
					}
				}
				else
				{
					addToJson(childName, childJsonObject, jsonObject);
				}
			}
		}
	}

	private String getNodeName(Node childNode,
							   boolean parseNamespace)
	{
		String nodeName = null;
		if (parseNamespace) {
			nodeName = childNode.getNodeName();
		} else {
			String[] childNodeNameParts = childNode.getNodeName().split(":");
			if (childNodeNameParts.length == 1) {
				// Plain attribute declation.
				// e.g. attribute="value"
				nodeName = childNodeNameParts[0];
			} else {
				// If attribute is namespace URI declaration then ignore it.
				// e.g. xmlns:wss="http://www.boomi.com/connector/wss"
				if (!(childNodeNameParts[0].equalsIgnoreCase("xmlns"))) {
					nodeName = childNodeNameParts[1];
				}
			}
		}
		return nodeName;
	}

	private void addToJson(String key, 
						   Object value,
						   JSONObject jsonObject) throws JSONException
	{
		if (canAddValue(key, value)) {
			boolean isNewEntry = jsonObject.isNull(key);
			if (isNewEntry) {
				jsonObject.put(key, value);
			} else {
				jsonObject.accumulate(key, value);
			}
		}
	}

	private boolean canAddValue(String key, 
							    Object value) throws JSONException 
	{
		boolean canAddValue = true;
		if (key == null) {
			canAddValue = false;
		} else if (value == null) {
			canAddValue = false;
		} else if (value instanceof JSONObject) {
			JSONObject valueJson = (JSONObject) value;
			if (valueJson.length() == 0) {
				canAddValue = false;
			}
		} else if (value instanceof Collection) {
			Collection valueColl = (Collection) value;
			if (valueColl.isEmpty()) {
				canAddValue = false;
			}
		}
		return canAddValue;
	}

	private boolean hasValue(String value)
	{
		boolean hasValue = true;
		if (value.matches("^\\s*$|^\\n*$")) {
			hasValue = false;
		}
		return hasValue;
	}

}
