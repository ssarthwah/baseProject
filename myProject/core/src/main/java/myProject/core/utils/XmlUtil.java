package myProject.core.utils;


import java.io.File;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

public class XmlUtil {
	/**
	 * Logger variable
	 */
	private static final Logger LOGGER = LoggerFactory.getLogger(XmlUtil.class);

	public static NodeList getAllNodes(){
	  
	  NodeList nList=null;

    try {

	File fXmlFile = new File("filename.xml");
	DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
	DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
	Document doc = dBuilder.parse(fXmlFile);
			
	doc.getDocumentElement().normalize();

	nList = doc.getElementsByTagName("Image");
	} catch (Exception e) {
    	LOGGER.debug("Exception  is occured at XMLUtil"+e);
    }
    return  nList;
  }

}