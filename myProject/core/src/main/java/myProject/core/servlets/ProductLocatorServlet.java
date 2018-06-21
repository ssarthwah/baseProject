/**
 * Package Section
 */
package myProject.core.servlets;

/**
 * Import Section
 */
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

import javax.servlet.Servlet;

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

import myProject.core.services.StandardConfigService;


@Service(Servlet.class)
@Component(immediate = true, metatype = false, label = "Product Locator Servlet")
@Properties({
	@Property(name="sling.servlet.paths",value="/bin/services/productLocator"),
	@Property(name = "sling.servlet.methods", value = "POST")
})


/**
 * @desc - this servlet implements the functionality for Product Locator webservice
 * 
 *
 */
public class ProductLocatorServlet extends SlingAllMethodsServlet {


	/**
	 * 
	 */
	public static String sResult;

	/**
	 *  long variable that holds serialVersionUID
	 */
	private static final long serialVersionUID = 6261664949894893765L;

	/**
	 * logger variable
	 */
	private Logger LOGGER = LoggerFactory.getLogger(this.getClass());

	/**
	 * HSStandardConfigService variable
	 */
	@Reference
	StandardConfigService configService;


	/**
	 *  default constructor
	 */
	public ProductLocatorServlet() {

	}

	/**
	 * @param - request variable
	 * @param - response variable
	 */ 
	protected void execute(SlingHttpServletRequest request, SlingHttpServletResponse response) {

		LOGGER.info("Entering execute method in productLocator** " );

		String productId = request.getParameter("productId");

		LOGGER.info("Product Id is :"+ productId );

		String zipCode = request.getParameter("zip");

		LOGGER.info("Zip Code is :"+ zipCode );

		String cityName = request.getParameter("city");

		LOGGER.info("City Name is :"+ cityName );

		String stateName = request.getParameter("state");

		LOGGER.info("State Name is :"+ stateName );

		String distance =request.getParameter("distance");

		LOGGER.info("Distance is :"+ distance );	

		HttpURLConnection conn1 =  null;

		try {

			StringBuffer urlBuffer = new StringBuffer();

			String productServiceurl = configService.getProperty("productlocatorurl");
			String productServiceCustomer = configService.getProperty("productcustomer");
			String productServiceLocatorurl=productServiceurl+productServiceCustomer;

			urlBuffer.append(productServiceLocatorurl);
			// appends productid
			urlBuffer.append("&item="+productId);

			if(zipCode!=null && !zipCode.trim().isEmpty()) {
				// appends zipcode
				urlBuffer.append("&zip="+zipCode);
			}
			else {
				// appends cityname
				urlBuffer.append("&city="+URLEncoder.encode(cityName,"UTF-8"));
				// appends statename
				urlBuffer.append("&state="+stateName);
			}
			// appends radius
			urlBuffer.append("&radius="+distance);

			URL url = new URL(urlBuffer.toString());

			LOGGER.info("URL is::" + urlBuffer.toString());
			conn1 = (HttpURLConnection) url.openConnection();
			conn1.setRequestMethod("GET");
			conn1.setRequestProperty("Accept", "text/html");

			LOGGER.info("Response code is::"+conn1.getResponseCode());

			final PrintWriter out = response.getWriter();


			if (conn1.getResponseCode() != 200) {
				sResult="Invalid Zip Code or City/Province. Please try again";
				out.print(sResult);
				out.flush();
				throw new RuntimeException("Failed : HTTP error code : " + conn1.getResponseCode());

			}
			else
			{
				//Get Response		
				java.io.InputStream is =  conn1.getInputStream();
				BufferedReader rd = new BufferedReader(new InputStreamReader(is));
				StringBuilder response1 = new StringBuilder(); 
				String line;
				while((line = rd.readLine()) != null) {
					response1.append(line);
					response1.append('\r');
				}

				rd.close();
				sResult = response1.toString(); 

			}
			out.print(sResult);
			out.flush();
			out.close();

		}
		catch(Exception e) {
			LOGGER.error("Error in getting product locator details", e);
		}
		finally {
			if(conn1 !=null) {
				conn1.disconnect(); 
			}
		}
	}

}