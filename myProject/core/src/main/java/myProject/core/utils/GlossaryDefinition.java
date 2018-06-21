/**
 * 
 */
package myProject.core.utils;


import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Session;

import org.apache.felix.scr.annotations.Reference;
import org.apache.jackrabbit.commons.JcrUtils;
import org.apache.sling.api.resource.LoginException;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import myProject.core.Constants;

/**
 * utility class to read CSV file and create data nodes in the repository
 *
 */
public class GlossaryDefinition {

	@Reference
	private ResourceResolverFactory resourceResolverFactory;

	private Session session = null;
	private ResourceResolver resourceResolver;


	public GlossaryDefinition() {
		super();
	}

	private static final Logger log = LoggerFactory.getLogger(GlossaryDefinition.class);

	public Boolean createTermsNodes() {
		Map<String, Object> params = new HashMap<>();
		params.put(ResourceResolverFactory.SUBSERVICE, "system-user");

		try {
			resourceResolver = resourceResolverFactory.getServiceResourceResolver(params);
		}  catch (LoginException e) {
			e.printStackTrace();
		}

		String csvFile = Constants.GLOSSARY_UPDATE_FILE; // Path of the CSV
		BufferedReader fileReader = null;

		try {

			session = resourceResolver.adaptTo(Session.class);
			Node rootNode=session.getRootNode();
			Node dataNode=rootNode.getNode("data");	        
			Node glossNode=JcrUtils.getOrAddNode(dataNode, "glossary", "nt:unstructured");

			String term="", firstLetter="", nodeName="";
			String line = "";
			Node alphaNode=null, wordNode=null;

			// Create the file reader
			fileReader = new BufferedReader(new FileReader(csvFile));
			// Read the file header top line to skip it
			for (int i = 0; i <1; i++) {
				fileReader.readLine();
			}

			while ((line = fileReader.readLine()) != null) {

				String[] tokens = line.split(Constants.PIPE_DELIMITER,-1);

				if (tokens.length > 0) {
					term= tokens[0].trim();

					if(term != null){
						firstLetter=term.substring(0, 1);
						alphaNode=JcrUtils.getOrAddNode(glossNode, firstLetter , "nt:unstructured");

						nodeName=term.replaceAll("\\s+", "-");
						nodeName=nodeName.replaceAll(",", "");
						nodeName=nodeName.replaceAll("\\(", "").replaceAll("\\)", "");
						nodeName=nodeName.toLowerCase().replaceAll("[^A-Za-z ]", "");

						if(term.startsWith(firstLetter)){
							wordNode=JcrUtils.getOrAddNode(alphaNode, nodeName , "nt:unstructured");
							wordNode.setProperty("definition", tokens[1].trim());
							wordNode.setProperty("glossaryTerm", tokens[0].trim());
							wordNode.setProperty("nodeName", nodeName);
						}
					}
				}
			}
			fileReader.close();
			session.save();			
			return true ;
		} catch (FileNotFoundException e) {
			log.error("FileNotFoundException **", e);
		}catch (IOException e) {
			log.error("IOException **", e);
		} catch (RepositoryException e) {
			log.error("RepositoryException in node**", e);

		}finally {
			if(session!=null && session.isLive()){
				session.logout();
			}
			if (resourceResolver != null && resourceResolver.isLive()) {
				resourceResolver.close();
			}
		}		

		return false;
	}
}
