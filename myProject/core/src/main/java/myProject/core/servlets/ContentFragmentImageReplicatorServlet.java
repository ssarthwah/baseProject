package myProject.core.servlets;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.jcr.Node;
import javax.jcr.PathNotFoundException;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.servlet.ServletException;

import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Properties;
import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.Service;
import org.apache.jackrabbit.JcrConstants;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.day.cq.replication.ReplicationActionType;
import com.day.cq.replication.ReplicationException;
import com.day.cq.replication.Replicator;


/**
 * This is a sample servlet which publishes the referenced images in the content fragments. Currently AEM doesn't provides any functionality to publish Images referenced inside content fragments.
 * 
 */


@Component(immediate = true, metatype = false, label = "Sample Fragment Replicator Servlet")
@Service
@Properties({
	@Property(name = "sling.servlet.paths", value = "/bin/servlet/fragmentactivator"),
	@Property(name = "sling.servlet.methods", value = "POST")
})
public class ContentFragmentImageReplicatorServlet extends SlingAllMethodsServlet {
	
	private static final long serialVersionUID = 1L;
	private static final String ORIGINAL_RENDITION_NODE_PATH = "/" + JcrConstants.JCR_CONTENT + "/renditions/original/" + JcrConstants.JCR_CONTENT; //get node till jcr:content of original rendition
	private static final Logger logger = LoggerFactory.getLogger(ContentFragmentImageReplicatorServlet.class);

	@Reference
	Replicator replicator;

	@Override
	protected void doPost(SlingHttpServletRequest request, SlingHttpServletResponse response) throws ServletException, IOException {
		ResourceResolver resourceResolver = request.getResourceResolver();
		String path = request.getParameter("path");  //path of the content fragment
		String content="";
		logger.info("Inside doPost method** ");
		Session session = null;
		
		try {			
			Resource resource = resourceResolver.getResource(path);
			if(resource != null){
				String pathToContent = path + ORIGINAL_RENDITION_NODE_PATH;
				session = resourceResolver.adaptTo(Session.class);
				Node node = session.getNode(pathToContent);
				content = node.getProperty(JcrConstants.JCR_DATA).getString();
				List<String> imageList=getImageSrc(content);
				if (imageList != null && !imageList.isEmpty()) { 
					activateAssets(imageList, resourceResolver, session, replicator);
				}
			}
		} catch (PathNotFoundException e) {
			logger.error("PathNotFoundException** "+e.getMessage());
		} catch (RepositoryException e) {
			logger.error("RepositoryException** "+e.getMessage());
		} finally{
			if (resourceResolver != null && resourceResolver.isLive()) {
				resourceResolver.close();
			}
			if(session != null && session.isLive()){
				session.logout();
			}
		}
	}
	
	//method used to fetch all images from content fragment data.
		public List<String> getImageSrc(String file) throws IOException {
			List<String> imageList = new ArrayList<String>();
			Document doc = Jsoup.parse(file);
			Elements media = doc.select("[src]");
			for (Element src : media) {
				if (src.tagName().equals("img")) {
					
					//check if image is a DAM image based on path
					if(src.attr("src").startsWith("/content")){
						imageList.add(src.attr("src"));
					}				
				}
			}
			return imageList;
		}

		//this method activates images from DAM using Replicator api
		public void activateAssets(List<String> imageList, ResourceResolver resolver, Session session, Replicator replicator){
			for(int i = 0; i < imageList.size(); i++) {	
				try {
					String imagePath=imageList.get(i); 
					Resource resource = resolver.getResource(imagePath);
					if(resource != null){
						replicator.replicate(session, ReplicationActionType.ACTIVATE, imagePath);
						logger.info("Activated Image Path**  "+imagePath);
					}
				} catch (ReplicationException e) {
					logger.error("ReplicationException** "+e.getMessage());	
				}				
			}
		}
}
