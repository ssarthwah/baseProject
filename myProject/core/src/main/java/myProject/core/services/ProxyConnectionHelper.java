/**
 * Package Section
 */
package myProject.core.services;


/**
 * @desc - This Service is used to set PROXY, if required by Environment to access external service
 * 
 *
 */
public interface ProxyConnectionHelper {
	
	public void setProxy();
	
	public void clearProxy();	
}
