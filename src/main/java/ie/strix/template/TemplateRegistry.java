package ie.strix.template;


import java.io.CharArrayWriter;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;

import org.apache.velocity.app.Velocity;
import org.apache.velocity.exception.ResourceNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Singleton which acts as a simplified interface to Velocity
 * (or whatever) templating engine is used.
 * 
 * <p>
 * Velocity merge() methods return just 'Exception'. Therefore
 * these methods mimic this behavior even though it's not 
 * ideal.
 * 
 * @author Joe Desbonnet
 */
public class TemplateRegistry {

	private static Logger log = LoggerFactory.getLogger(TemplateRegistry.class);
	private static TemplateRegistry templateRegistry = new TemplateRegistry();
	
	private boolean initialized = false;
	private String templatePath;
	
	private TemplateRegistry ()
	{
	}
	
	public static TemplateRegistry getInstance ()
	{
		return templateRegistry;
	}
	
	public void init (String templatePath)
	throws TemplateException
	{
	
	/*
	 * Initialize Velocity
	 */
	 try
	 {
	     Velocity.setProperty(Velocity.FILE_RESOURCE_LOADER_PATH, templatePath);
	     Velocity.setProperty(Velocity.ENCODING_DEFAULT,"UTF-8");
	     Velocity.setProperty("runtime.log.logsystem.class", "org.apache.velocity.runtime.log.NullLogSystem");
	     // Allow relative paths in VM include/parse directives
	     Velocity.setProperty("eventhandler.include.class", "org.apache.velocity.app.event.implement.IncludeRelativePath");
	     Velocity.init();
	     this.templatePath = templatePath;
	     initialized = true; 
	 }
	 catch (Exception e)
	 {
	     throw new TemplateException ("Error initializing velocity: " + e.toString());
	 }
				
	 initialized = true;
	}
	
	public boolean isInitialized ()
	{
		return initialized;
	}
	
	/**
	 * Return a template or null if no template found.
	 * 
	 * @param templateName
	 * @return
	 * @throws AppException
	 */
	public Template getTemplate (String templateName)
	throws TemplateException
	{
		if (! isInitialized())
		{
			throw new TemplateException ("Templating system not initialized");
		}
		
		try
		{
			return new Template (Velocity.getTemplate(templateName,"UTF-8"));
		}
		catch (ResourceNotFoundException e)
		{
			throw new TemplateException ("template not found: " + e);
		}
		//rather annoyingly Velocity.getTemplate() returns a general Exception!
		catch (Exception e) {	
			throw new TemplateException (e);
		}
		
	}
	
	public static String mergeToString (String templateName, Context context)
	throws TemplateException
	{
		CharArrayWriter out = new CharArrayWriter ();
		merge (templateName,context,out);
		return new String (out.toCharArray());
	}
	
	public static void merge (String templateName, Context context, Writer wout)
	throws TemplateException
	{
		Template template = getInstance().getTemplate(templateName);
		if (template == null) {
			throw new TemplateException ("template file not found, template " + templateName);
		}
		template.merge(context, wout);
	}
	
	
	public static void merge (String templateName, Context context, OutputStream out, String charEnc)
	throws TemplateException
	{
		Writer wout;
		
		try {
			wout = new OutputStreamWriter(out,charEnc);
		} catch (UnsupportedEncodingException e) {
			throw new TemplateException("unsupported character encoding " + charEnc);
		}
		
		getInstance().getTemplate(templateName).merge(context, wout);
		
	}
	
	/**
	 * @return Returns the templatePath.
	 */
	public String getTemplatePath() {
		return templatePath;
	}
}
