package myProject.core.schedulers;


import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.PathNotFoundException;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.Value;
import javax.jcr.ValueFormatException;

import org.apache.felix.scr.annotations.Activate;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.Service;
import org.apache.sling.api.resource.LoginException;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.ServiceReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.day.cq.tagging.Tag;
import com.day.cq.tagging.TagManager;

import myProject.core.CorporateTagBean;
import myProject.core.NewscenterArticleBean;
import myProject.core.utils.CommonUtil;


@Component(immediate = true, label = "Newscenter Articles Service/Job", description = "Newscenter Articles Service/Job", metatype = true)
@Service(Runnable.class)
@Property(name="scheduler.concurrent", propertyPrivate=true, boolValue=false)
public class NewscenterArticlesService implements Runnable{


	@Property(longValue = 86400, description="Time span between two consecutive runs in seconds(For e.g. '1 day = 86400 sec')") //Job runs after 24 hours
	static final String SCHEDULER_PERIOD = "scheduler.period";

	@Property(boolValue = true , description="Switch to ON or OFF") //Job runs after 24 hours
	static final String SCHEDULER_SWITCH = "scheduler.switch";

	private boolean schedulerSwitch;
	private Long schedulerPeriod;

	private final Logger LOGGER = LoggerFactory.getLogger(NewscenterArticlesService.class);

	@Activate
	public void activate(final Map<String, Object> props){
		this.schedulerPeriod = (Long)props.get(SCHEDULER_PERIOD);
		this.schedulerSwitch=(Boolean)props.get(SCHEDULER_SWITCH);
	}

	String ARTICLE_PAGE_PATH = "content/corporate/en_us/news-center/blog";
	String ARTICLE_TAGS_PATH = "etc/tags/myproject/BlogCategory";

	public NewscenterArticlesService() {}

	public void run(){
		if(this.schedulerSwitch){
			getNewscenterArticles();
			getBlogTags();
		}
	}

	public List<NewscenterArticleBean> getNewscenterArticles() {

		Session session=null;
		NewscenterArticleBean newscenterArticleBean= new NewscenterArticleBean();

		List<NewscenterArticleBean> articleBeanList=new ArrayList<NewscenterArticleBean>();
		ResourceResolver resolver=null;

		try{
			BundleContext bundleContext = FrameworkUtil.getBundle(NewscenterArticlesService.class).getBundleContext();
			ServiceReference serviceReference = bundleContext.getServiceReference(ResourceResolverFactory.class.getName());
			ResourceResolverFactory factory = (ResourceResolverFactory) bundleContext.getService(serviceReference);
			resolver = factory.getAdministrativeResourceResolver(null);
			session = resolver.adaptTo(Session.class);			
			Node rootNode=session.getRootNode();
			Node contentNode=null,imageNode=null,rteNode=null;
			if(rootNode.hasNode(ARTICLE_PAGE_PATH)){
				Node unwrappedNode=rootNode.getNode(ARTICLE_PAGE_PATH);

				if(unwrappedNode.hasNodes()) {
					NodeIterator itr=unwrappedNode.getNodes();
					while(itr.hasNext()){
						Node pageNode=itr.nextNode();
						String blogTitle="",blogImage="",blogDesc="",blogURL="",bannerTitle="";
						Calendar createdDate= Calendar.getInstance();

						if(pageNode.hasNode("jcr:content")){

							Node mainNode=pageNode.getNode("jcr:content");
							if(pageNode.hasNode("jcr:content/artilclePageBanner")){
								contentNode =pageNode.getNode("jcr:content/artilclePageBanner");
							}
							if(contentNode!=null && contentNode.hasNode("image")){
								imageNode=contentNode.getNode("image");
							}
							if(pageNode.hasNode("jcr:content/articleRTETop")){
								rteNode=pageNode.getNode("jcr:content/articleRTETop");
							}
							blogURL= pageNode.getPath();
							newscenterArticleBean.setBlogUrl(blogURL);
							if(contentNode!=null && contentNode.hasProperty("bannerText")){
								bannerTitle=contentNode.getProperty("bannerText").getString();
								if(CommonUtil.isNotNullOrEmpty(bannerTitle)){
									String[] hIndex= bannerTitle.split("</h1>");
									bannerTitle=hIndex[0];	
									bannerTitle = bannerTitle.replaceAll("\\<.*?\\>", "");
									newscenterArticleBean.setBlogHeading(bannerTitle);										 		    						  
								}
							}
							if(pageNode.hasProperty("jcr:created")){
								createdDate=pageNode.getProperty("jcr:created").getDate();								
									newscenterArticleBean.setCreatedDate(createdDate);	
							}
							if(!mainNode.hasProperty("isBlogPage")){
								mainNode.setProperty("isBlogPage", "true");									 
							}
							if(mainNode.hasProperty("jcr:title")){
								blogTitle=mainNode.getProperty("jcr:title").getString();
								if(CommonUtil.isNotNullOrEmpty(blogTitle)){
									newscenterArticleBean.setBlogPageTitle(blogTitle);										 			    						  
								}
							}
							if(imageNode!=null && imageNode.hasProperty("fileReference")){
								blogImage=imageNode.getProperty("fileReference").getString();
								if(CommonUtil.isNotNullOrEmpty(blogImage)){
									newscenterArticleBean.setBlogImage(blogImage);										 			    						  
								}
							}
							if(mainNode.hasProperty("cq:tags")){

								String finalString="";
								Value[] valuesTag=mainNode.getProperty("cq:tags").getValues();
								for(int i = 0;i<valuesTag.length;i++){
									String str=	valuesTag[i].toString();
									TagManager tagManager=resolver.adaptTo(TagManager.class);
									Tag tag=tagManager.resolve(str);
									String tagTitle=tag.getTitle();
									if(i<valuesTag.length-1){
										finalString+=tagTitle+",";
									}else{
										finalString+=tagTitle ; 
									}
								}

								newscenterArticleBean.setBlogTags(finalString);
							}
							if(rteNode!=null && rteNode.hasProperty("text")){
								blogDesc=rteNode.getProperty("text").getString();
								if(CommonUtil.isNotNullOrEmpty(blogDesc)){	
									int pIndex= blogDesc.indexOf("</p>");											
									blogDesc=blogDesc.substring(0, pIndex);
									blogDesc = blogDesc.replaceAll("\\<.*?\\>", "");
									newscenterArticleBean.setBlogText(blogDesc);										 			    						  
								}
							}	
							articleBeanList.add(newscenterArticleBean);	
							newscenterArticleBean= new NewscenterArticleBean();
						}							  

					}

				}				   
			}	
			session.save();			
			Collections.sort(articleBeanList);
			return articleBeanList;			

		} catch (PathNotFoundException e) {
			LOGGER.error("PathNotFoundException **", e);
		} catch (ValueFormatException e) {
			LOGGER.error("ValueFormatException **", e);
		} catch (RepositoryException e) {
			LOGGER.error("RepositoryException **", e);
		} catch (LoginException e) {
			LOGGER.error("LoginException **", e);
		}  
		finally {
			if (session != null && session.isLive()) {
				session.logout();
			}
			if(resolver != null && resolver.isLive()){
				resolver.close();
			}

		}
		return null;
	}	

	public  CorporateTagBean getBlogTags() {

		Session session=null;
		Set<String> corporateTagSet= new HashSet<String>();
		CorporateTagBean corporateTagBean = new CorporateTagBean();
		ResourceResolver resolver=null;
		try{ 
			BundleContext bundleContext = FrameworkUtil.getBundle(NewscenterArticlesService.class).getBundleContext();
			ServiceReference serviceReference = bundleContext.getServiceReference(ResourceResolverFactory.class.getName());
			ResourceResolverFactory factory = (ResourceResolverFactory) bundleContext.getService(serviceReference);
			resolver = factory.getAdministrativeResourceResolver(null);
			session = resolver.adaptTo(Session.class);			
			Node rootNode=session.getRootNode();


			if(rootNode.hasNode(ARTICLE_TAGS_PATH)){

				Node tagNode=rootNode.getNode(ARTICLE_TAGS_PATH);

				if(tagNode.hasNodes()) {
					NodeIterator itr=tagNode.getNodes();
					while(itr.hasNext()){
						Node tagNamenode=itr.nextNode();
						String tagTitle="";
						if(tagNamenode.hasProperty("jcr:title")){
							tagTitle=tagNamenode.getProperty("jcr:title").getString();
							corporateTagSet.add(tagTitle);
						}	

					}

					corporateTagBean.setBlogTags(corporateTagSet);
				}

			}

			return corporateTagBean;
		} catch (LoginException e) {
			LOGGER.error("RepositoryException **", e);
		} catch (RepositoryException e) {
			LOGGER.error("RepositoryException **", e);
		} 
		finally {
			if (session != null && session.isLive()) {
				session.logout();
			}
			if(resolver != null && resolver.isLive()){
				resolver.close();
			}
		}

		return null;

	}

}


