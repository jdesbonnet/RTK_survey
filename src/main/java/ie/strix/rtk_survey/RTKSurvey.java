package ie.strix.rtk_survey;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ie.strix.template.Context;
import ie.strix.template.TemplateException;
import ie.strix.template.TemplateRegistry;


/**
 * This is a core class that is loaded when the application starts. It performs 
 * some application-boot time initialization.
 * 
 * It can be used by the application to get software version etc.
 * 
 * @author joe
 *
 */
public class RTKSurvey implements ServletContextListener {

	private static final Logger log = LoggerFactory.getLogger(RTKSurvey.class);
	
	/**
	 * Software version (now retrieved from metadata files created by maven pom.xml)
	 * and set at application boot time.
	 */
	public static String VERSION;
		
	private static Properties configuration = new Properties();
	private static File confFile;
	private static long confLoadedTime=0L;
	
	/**
	 * The path to the application root in OS file system.
	 */
	private static File appRoot;
	
	@Override
	public void contextDestroyed(ServletContextEvent arg0) {
	}

	/**
	 * Called by the servlet container when the application starts. We do app initialization here.
	 */
	@Override
	public void contextInitialized(ServletContextEvent event) {
		System.err.println ("*** STARTING TIRTBX contextPath=" + event.getServletContext().getContextPath());
		
		appRoot = new File(event.getServletContext().getRealPath("/"));

		// Retrieve software version from pom.xml (also written in pom.properties)
		File pomPropertiesFile = new File (appRoot, "META-INF/maven/ie.strix/tirtbx/pom.properties");
		Properties pomProperties = new Properties ();
		try {
			pomProperties.load(new FileInputStream(pomPropertiesFile));
		} catch (IOException e) {
		}	
		VERSION = pomProperties.getProperty("version");
		log.info("VERSION=" + VERSION);
		
		TemplateRegistry templates = TemplateRegistry.getInstance();
		
		/*
		 * Initialize our own templating wrapper
		 */
		if (! templates.isInitialized()) {
			
			File templateRoot = new File(appRoot, "WEB-INF/classes/templates");
			log.info("templateRoot=" + templateRoot.getPath());
			if ( ! templateRoot.exists()) {
				log.error("template root directory at " + templateRoot.getPath() + " not found");
			}
			try {
				templates.init(templateRoot.getPath());
			} catch (TemplateException e) {
				log.error("Error initializing template system: " + e.getMessage());
				e.printStackTrace();
				//throw new ServletException(e.toString());
			}
		}
	}
	
	public static File getAppRoot () {
		return appRoot;
	}
	
	
	public static void loadConfigurationProperties () {
		log.info ("loading configuration");

		Properties properties = new Properties() ;
		try {
			properties.load(new FileReader(confFile));
		} catch (Exception e1) {
			log.error ("unable to load config.properties at " + confFile.getPath());
		}
		configuration = properties;
		confLoadedTime = System.currentTimeMillis();
		
		for (Object key : properties.keySet()) {
			log.info ("property[" + key + "]=" 
					+ properties.getProperty(key.toString()));
		}
	}
	
	public static String getConfiguration (String key) {
		
		if (confFile == null) {
			log.warn("no confFile");
			return null;
		}
		
		// If configuration file has been edited, reload
		if (confFile.lastModified() > confLoadedTime) {
			loadConfigurationProperties();
		}
		// TODO: should values be trim() before returning?
		return (String)configuration.get(key);
	}
	
	
	public static String getConfiguration (String key, String defaultValue) {
		String value = getConfiguration(key);
		return value == null ? defaultValue : value;
	}
	
	/**
	 * Return the value of a boolean configuration property. Returns true if 
	 * set to "true", false for all other values or if not present.
	 * @param key
	 * @return
	 */
	public static boolean getConfigurationBoolean (String key) {
		return "true".equals(getConfiguration(key));
	}
	
	/**
	 * Return the value of a boolean configuration property and specify a 
	 * default value if the configuration is missing. Returns true if 
	 * set to "true", false if missing or for all other values.
	 * @param key
	 * @param defaultValue 
	 * @return
	 */
	public static boolean getConfigurationBoolean (String key, boolean defaultValue) {
		if (getConfiguration(key) == null) {
			return defaultValue;
		}
		return getConfigurationBoolean(key);
	}
	
	/**
	 * Return the value of a integer configuration property. 
	 * @param key
	 * @return
	 */
	public static int getConfigurationInt (String key) {
		return Integer.parseInt(getConfiguration(key));
	}
	
	/**
	 * Return the value of a integer configuration property and specify a 
	 * default value if the configuration is missing. 
	 * @param key
	 * @param defaultValue 
	 * @return
	 */
	public static int getConfigurationInt (String key, int defaultValue) {
		if (getConfiguration(key) == null) {
			return defaultValue;
		}
		return getConfigurationInt(key);
	}
	
	
	public static String getFullContextPath(HttpServletRequest request) {
		return (request.isSecure() ? "https://" : "http://") 
				+ request.getServerName()
				+ (request.getServerPort() != 80 ? (":" + request.getServerPort()) : "") 
				+ request.getContextPath();
	}
	
	
	/**
	 * Setup global context variables in user interface.
	 * 
	 * This was in JSP fragment _header_template_init.jsp. Made separate to
	 * _header.jsp because also want to include it in error.jsp (which 
	 * can't include _header.jsp).
	 * 
	 * @param context
	 * @param request
	 * @param response
	 */
	public static void initializeVelocityContext(Context context, User user,
			HttpServletRequest request) {

		// NB: everything here must be public. Never put private credentials in
		// template context.
		
		// The database used
		//context.put("DATASOURCE",datasource);
			

		String serverName = request.getServerName();

		
		
		// context.put("ASSETS","./include");
		context.put("INCLUDE", "./include");
		context.put("VERSION", VERSION);
		context.put("APP_ROOT", getAppRoot());
		
		//FormatUtil fmt = new FormatUtil();
		//context.put("fmt", fmt);
		
		
		context.put("user", user);
		context.put("contextPath", request.getServletContext().getContextPath());

		// TODO: I don't think this is ever used:
		context.put("SYSTEM_ROOT_URL", getFullContextPath(request));
		context.put("fullContextPath", getFullContextPath(request));

		context.put("API", getFullContextPath(request) + "/api/v0");

		
		String contextPath = request.getContextPath();
		
		// UI includes
		String jQueryVersion = getConfiguration("ui.jquery_version", "3.3.1"); // was 1.11.1
		String bootstrapVersion = getConfiguration("ui.bootstrap_version","3.3.7-1");
		String faVersion = getConfiguration("ui.fa_version","4.7.0");
		String jsBarcodeVersion = getConfiguration("ui.jsBarcode_version","3.9.0");
		String leafletVersion = getConfiguration("ui.leaflet_version","1.4.0");
		String chartJsVersion = getConfiguration("ui.chartjs_version","2.8.0");
		String smoothieVersion = getConfiguration("ui.smoothie_version","1.24");

		// Configurable CDN or local hosting of UI JS,CSS include files
		if (getConfigurationBoolean("ui.use_cdn", false)) {
			// CDN
			log.info("loading UI assets from CDN");
			context.put("JQUERY_SCRIPT", "//code.jquery.com/jquery-" + jQueryVersion + ".min.js");
			context.put("BOOTSTRAP_ROOT", "https://maxcdn.bootstrapcdn.com/bootstrap/" + bootstrapVersion);
			context.put("FONT_AWESOME_ROOT", "//maxcdn.bootstrapcdn.com/font-awesome/" + faVersion);
			context.put("LEAFLET_SCRIPT", "//cdnjs.cloudflare.com/ajax/libs/leaflet/1.4.0/leaflet.js");
			context.put("LEAFLET_CSS", "//cdnjs.cloudflare.com/ajax/libs/leaflet/1.4.0/leaflet.css");
			context.put("CHARTJS_SCRIPT", "//cdnjs.cloudflare.com/ajax/libs/Chart.js/" + chartJsVersion + "/Chart.js");
			context.put("CHARTJS_CSS", "//cdnjs.cloudflare.com/ajax/libs/Chart.js/" + chartJsVersion + "/Chart.css");
		} else {			
			// Local hosted 
			log.info("loading UI assets from local");
			context.put("JQUERY_SCRIPT",contextPath+"/webjars/jquery/" + jQueryVersion + "/jquery.min.js");
			context.put("BOOTSTRAP_ROOT",contextPath+"/webjars/bootstrap/" + bootstrapVersion);
			context.put("FONT_AWESOME_ROOT",contextPath+"/webjars/font-awesome/" + faVersion);
			context.put("JS_BARCODE_SCRIPT",contextPath+"/webjars/JsBarcode/" + jsBarcodeVersion + "/dist/JsBarcode.all.min.js");
			context.put("LEAFLET_SCRIPT",contextPath+"/webjars/leaflet/" + leafletVersion + "/leaflet.js");
			context.put("LEAFLET_CSS",contextPath+"/webjars/leaflet/" + leafletVersion + "/leaflet.css");
			context.put("CHARTJS_SCRIPT", contextPath+"/webjars/chartjs/" + chartJsVersion + "/Chart.js");
			context.put("CHARTJS_CSS", contextPath+"/webjars/chartjs/" + chartJsVersion + "/Chart.css");
			context.put("SMOOTHIE_SCRIPT", contextPath+"/webjars/smoothie/" + smoothieVersion + "/smoothie.js");

		}
	}
	
	
}
