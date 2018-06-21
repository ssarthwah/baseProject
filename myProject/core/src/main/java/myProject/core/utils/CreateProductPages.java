package myProject.core.utils;

import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.PathNotFoundException;
import javax.jcr.RepositoryException;
import javax.jcr.Session;

import org.apache.sling.api.resource.ResourceResolver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;
import com.day.cq.wcm.api.WCMException;

/**
 * This is a utility class to create product pages using PageManager api
 * 
 */
public class CreateProductPages  {

	private final Logger LOGGER = LoggerFactory.getLogger(this.getClass().getSimpleName());
	private static final String ABSOLUTE_PRODUCTS_PATH =""; // provide Absolute path to the Product page
	private static final String RELATIVE_PRODUCTS_PATH =""; //provide Relative path to the Product page

	public void createpages (Session session,ResourceResolver resolver) {

		try {  		
			String title="", brand="", description="";
			Node rootNode=session.getRootNode();
			if(rootNode.hasNode("data/products")) {			
				Node productNode=rootNode.getNode("data/products");
				NodeIterator productNodeIter=productNode.getNodes();
				while(productNodeIter.hasNext())
				{
					Node upcNode = (Node) productNodeIter.nextNode();
					String UPCCode=upcNode.getName();
					NodeIterator psnNode=upcNode.getNodes();
					while(psnNode.hasNext())
					{
						Node specNode=(Node)psnNode.next();
						String specNo=specNode.getName();
						if(specNode.hasProperty("productTitle")){
							title=specNode.getProperty("productTitle").getString();
						}
						if(specNode.hasProperty("brand")){
							brand=specNode.getProperty("brand").getString();
						}
						if(specNode.hasProperty("productShortText")){
							description=specNode.getProperty("productShortText").getString();
						}
						getPage(resolver, session, title, UPCCode, specNo, description, brand);
					}
				}			
			}
		} catch (PathNotFoundException e) {
			LOGGER.error("PathNotFoundException | " + this.getClass().getSimpleName(), e);	
		} catch (RepositoryException e) {
			LOGGER.error("RepositoryException | " + this.getClass().getSimpleName(), e);
		}  
	}

	public boolean getPage(ResourceResolver resourceResolver, Session session, String title, String UPCCode, String specNo, String description, String brand)
	{
		try {
			String template="/apps/myProject/templates/productTemplate"; //template path
			String templateRenderer="myProject/components/pages/productPage"; //page component for the template
			Page productPage= null ;

			// Create Page 	
			Node root = session.getRootNode();	
			Node parentNode = root.getNode(RELATIVE_PRODUCTS_PATH);

			String pageName=title.trim().replaceAll("\\s+", " ");
			pageName=pageName.toLowerCase().replaceAll("[^A-Za-z0-9 ]", "");
			pageName=pageName.replaceAll("\\s+", " ");
			pageName=pageName.replaceAll(" ", "-");
			pageName=pageName.replace("--", "-");

			PageManager pageManager = resourceResolver.adaptTo(PageManager.class);

			if(!parentNode.hasNode(pageName)) {
				productPage = pageManager.create(ABSOLUTE_PRODUCTS_PATH, pageName, template, title);			 
			} else {
				productPage=pageManager.getPage(ABSOLUTE_PRODUCTS_PATH+"/"+pageName);	
			}
			Node productNode = productPage.adaptTo(Node.class);			
			Node jcrNode = null;
			if (productPage.hasContent()) {
				jcrNode = productPage.getContentResource().adaptTo(Node.class);
			} else {                   
				jcrNode = productNode.addNode("jcr:content", "cq:PageContent");
			} 
			jcrNode.setProperty("sling:resourceType", templateRenderer);
			jcrNode.setProperty("subtitle", UPCCode);
			jcrNode.setProperty("navTitle", specNo);
			jcrNode.setProperty("jcr:title", title);
			jcrNode.setProperty("jcr:description", description);
			jcrNode.setProperty("brand", brand);				

			session.save();
			return true ;		
		} catch (RepositoryException e) {
			LOGGER.error("RepositoryException | " + this.getClass().getSimpleName(), e);
			return false;
		} catch (WCMException e) {
			LOGGER.error("WCMException | " + this.getClass().getSimpleName(), e);
			return false;
		}finally {
			if(session!=null && session.isLive()){
				session.logout();
			}
			if (resourceResolver != null && resourceResolver.isLive()) {
				resourceResolver.close();
			}
		} 
	}

}
