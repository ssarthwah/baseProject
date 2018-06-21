package myProject.core;

public interface Constants {
	
	String NULL = null;
	String PIPE_DELIMITER = "\\|";


	// FOR PRODUCT LOCATOR
	String DATANODE  = "/data";
	String STR_PRODUCTLOCATOR_PRODUCTS ="productlocator/productcategory/new-products";
	String STR_PRODUCT_ID="productid";
	String STR_PRODUCT_NAME="productname";


	//FOR STATE DROPDOWN
	String STR_STATES ="USStates/states";
	String STR_STATE_ID="stateId";
	String STR_STATE_NAME="stateName";


	//CONSTANTS for EMAIL FUNCTIONALITY
	String STR_USER_NAME = "${user_name}";
	String STR_JCR_DATA= "jcr:data";
	String STR_MEMBER_NME="${member_name}";

	//CONSTANTS for YesMail FUNCTIONALITY
	String NEWSLETTER_SUCCESS_MESSAGE="Congratulations! You are now subscribed to our newsletters!";
	String NEWSLETTER_ALREADYSUBSCRIBED_MESSAGE="You are already Subscribed !!!";
	String STR_COLON = ":";
	String STR_BASIC = "Basic ";
	String COMMA_DELIMITER = ",";	


	//FOR SUBJECT DROPDOWN
	String STR_SUBJECTS_IDEA ="subjectidea";
	String STR_SUBJECT_IDEA_ID="subjectideaid";
	String STR_SUBJECT_IDEA_NAME="subjectideaname";


	//FOR PRODUCT CATEGORY DROPDOWN
	String STR_PRODUCTS_CATEGORY ="productCategory";
	String STR_PRODUCTS_CATEGORY_ID="productCategoryId";
	String STR_PRODUCTS_CATEGORY_NAME="productCategoryName";		

	//FOR PRODUCT BRAND DROPDOWN
	String STR_PRODUCTS_BRAND ="productBrand";
	String STR_PRODUCTS_BRAND_ID="productBrandId";
	String STR_PRODUCTS_BRAND_NAME="productBrandName";		

	//FOR PROVINCE DROPDOWN
	String STR_PROVINCE ="province";
	String STR_PROVINCE_ID="provinceId";
	String STR_PROVINCE_NAME="provinceName";		

	//FOR COUNTRY DROPDOWN
	String STR_COUNTRY ="countries";
	String STR_COUNTRY_ID="countryId";
	String STR_COUNTRY_NAME="countryName";

	//FOR SUBJECT DROPDOWN
	String STR_SUBJECT ="subjects";
	String STR_SUBJECT_ID="subjectId";
	String STR_SUBJECT_NAME="subjectName";

	//FOR SOCIAL ICONS IMAGE PATH
	String STR_FACEBOOK="<i class=\"icon-facebook\"></i>";
	String STR_LINKDIN="<i class=\"icon-linkedin\"></i>";
	String STR_PINTREST="<i class=\"icon-pinterest\"></i>";
	String STR_VINE="<i class=\"icon-vine\"></i>";
	String STR_YOUTUBE="<i class=\"icon-youtube\"></i>";
	String STR_GOOGLEALERTS="<i class=\"icon-notifications\"></i>";
	String STR_GOOGLPLUS="<i class=\"icon-googleplus\"></i>";
	String STR_TWITTER="<i class=\"icon-twitter\"></i>";
	String STR_TUMBLER="<i class=\"icon-tumblr\"></i>";
	String STR_SNAPCHAT="<i class=\"icon-snapchat\"></i>";
	String STR_INSTAGRAM="<i class=\"icon-instagram\"></i>";


	//FOR PRODUCTS						
	String UPC_FILE_KEY = "upcjson.file.path";
	String TXT_UPDATE_FILE = "update.products.file.path";
	String GLOSSARY_UPDATE_FILE = "glossary.csv.file.path";


	//event2 WEBSERVICE
	String DATEFORMAT_yyyyMMdd = "yyyyMMdd";
	String TIMEFORMAT = "hh:mm:ss";
	String DATEFORMAT_MM_dd_yyyy="MM/dd/yyyy";

	// Social wall
	String TWITTER_CONSUMER_KEY = "twitterConsumerKey";
	String TWITTER_CONSUMER_SECRET = "twitterConsumerSecret";
	String TWITTER_ACCESS_TOKEN_KEY = "twitterAccessTokenKey";
	String TWITTER_ACCESS_SECRET = "twitterAccessSecret";
	String TWITTER_CQ_NODES_PATH = "twitterPostsCQNodePath";
	String TWITTER_USER_NAME = "twitterUserName";
	String INSTA_POSTS_URL = "instagramPostsUrl";
	String INSTA_CQ_NODES_PATH = "instagramPostsCQNodePath";
	String LINKEDIN_CLIENT_ID = "linkedInClientId";
	String LINKEDIN_CLIENT_SECRET = "linkedInClientSecret";
	String LINKEDIN_ACCESS_TOKEN = "linkedInAccessToken";
	String LINKEDIN_CQ_NODES_PATH = "linkedInPostsCQNodePath";
	String LINKEDIN_COMPANY_ID = "linkedInCompanyId";
	String LINKEDIN_COMPANY_UPDATE_URL = "linkedInCompanyUpdatesUrl";

	//Cloudinay Contact Us
	String CLOUDINARY_NAME = "cloudinaryName";
	String CLOUDINARY_API_KEY = "cloudinaryAPIKey";
	String CLOUDINARY_API_SECRET_KEY = "cloudinaryAPISecretKey";

	//Financial Alert Post URL
	String FINANCIAL_ALERT_POST_URL = "financialAlertPostUrl";



}
