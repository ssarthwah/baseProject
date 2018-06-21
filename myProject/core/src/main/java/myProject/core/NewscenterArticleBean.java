/**
 * 
 */
package myProject.core;

import java.util.Calendar;

/**
 * @author 
 *
 */
public class NewscenterArticleBean implements Comparable<NewscenterArticleBean> {

	private String blogHeading ;
	private String blogText ;
	private String blogImage ;
	private String blogUrl ;
	private String blogPageTitle;
	private String blogTags;
	private Calendar createdDate;


	public NewscenterArticleBean() {
		super();
	}
	/**
	 * @return the blogHeading
	 */
	public String getBlogHeading() {
		return blogHeading;
	}
	/**
	 * @param blogHeading the blogHeading to set
	 */
	public void setBlogHeading(String blogHeading) {
		this.blogHeading = blogHeading;
	}
	/**
	 * @return the blogText
	 */
	public String getBlogText() {
		return blogText;
	}
	/**
	 * @param blogText the blogText to set
	 */
	public void setBlogText(String blogText) {
		this.blogText = blogText;
	}
	/**
	 * @return the blogImage
	 */
	public String getBlogImage() {
		return blogImage;
	}
	/**
	 * @param blogImage the blogImage to set
	 */
	public void setBlogImage(String blogImage) {
		this.blogImage = blogImage;
	}
	/**
	 * @return the blogUrl
	 */
	public String getBlogUrl() {
		return blogUrl;
	}
	/**
	 * @param blogUrl the blogUrl to set
	 */
	public void setBlogUrl(String blogUrl) {
		this.blogUrl = blogUrl;
	}
	/**
	 * @return the blogPageTitle
	 */
	public String getBlogPageTitle() {
		return blogPageTitle;
	}
	/**
	 * @param blogPageTitle the blogPageTitle to set
	 */
	public void setBlogPageTitle(String blogPageTitle) {
		this.blogPageTitle = blogPageTitle;
	}
	/**
	 * @return the blogTags
	 */
	public String getBlogTags() {
		return blogTags;
	}
	/**
	 * @param blogTags the blogTags to set
	 */
	public void setBlogTags(String blogTags) {
		this.blogTags = blogTags;
	}
	/**
	 * @return the createdDate
	 */
	public Calendar getCreatedDate() {
		return createdDate;
	}
	/**
	 * @param createdDate the createdDate to set
	 */
	public void setCreatedDate(Calendar createdDate) {
		this.createdDate = createdDate;
	}	

	public int compareTo(NewscenterArticleBean newscenterobject) {

		int flag=-1;
		NewscenterArticleBean newscenterArticleBean = (NewscenterArticleBean)newscenterobject;		
		Calendar date1 = newscenterArticleBean.getCreatedDate();
		Calendar date2 = this.getCreatedDate();

		if(date1.compareTo(date2) > 0)
		{
			flag = 1;
		}

		return flag;
	}	
}
