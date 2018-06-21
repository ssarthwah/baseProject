package myProject.core.servlets;

import java.io.IOException;

import javax.servlet.Servlet;
import javax.servlet.ServletException;

import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Properties;
import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.Service;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import myProject.core.Constants;
import myProject.core.services.StandardConfigService;
import myProject.core.utils.FinancialAlerts;




@Service(Servlet.class)
@Component(immediate = true, metatype = false, label = "FinancialAlertServlet")
@Properties({
		@Property(name = "sling.servlet.paths", value = { "/bin/services/financialAlertServlet" }),
		@Property(name = "sling.servlet.methods", value = { "POST" }) })
public class FinancialAlertServlet extends SlingAllMethodsServlet 
{

	/**
	 * 
	 */
	private static final long serialVersionUID = 5124913293152403893L;
	
	private final Logger LOGGER = LoggerFactory.getLogger(FinancialAlertServlet.class);
	
	@Reference
	private StandardConfigService standardConfigService;
	
	private String financialAlertPostUrl;
	
	public FinancialAlertServlet()
	{		
		//constructor 
		
	}
	protected void execute(SlingHttpServletRequest request,
						   SlingHttpServletResponse response) throws ServletException,
						   											 IOException 
	{
		if(null != standardConfigService) 
		{
			financialAlertPostUrl = standardConfigService.getProperty(Constants.FINANCIAL_ALERT_POST_URL);
			String urlParameters ="";

			String list=request.getParameter("favorite");
			String emailId=request.getParameter("emailId");
			String notSelectedList=request.getParameter("notSelected");
			FinancialAlerts sFinancialAlerts=new FinancialAlerts();
			urlParameters = sFinancialAlerts.createFinancialAlert(list,emailId,notSelectedList);
			sFinancialAlerts.HTTPWebRequest(financialAlertPostUrl, urlParameters);
		}
		else
		{
			LOGGER.error("PropertiesUtil instance is not set properly");
		}
	}
}
