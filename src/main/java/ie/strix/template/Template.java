package ie.strix.template;

import java.io.Writer;

import org.apache.velocity.VelocityContext;

/**
 * A facade for VelocityTemplate
 * 
 * @author Joe
 *
 */
public class Template {
	
	private org.apache.velocity.Template velocityTemplate;
	
	public Template ()
	{
	}
	public Template (org.apache.velocity.Template velocityTemplate)
	{
		this.velocityTemplate = velocityTemplate;
	}
	
	/**
	 * Merge context with template to produce 
	 * output on 'out'. 
	 * 
	 * @param context
	 * @param out
	 * @throws Exception
	 */
	/*
	public void merge (Context context, OutputStream out)
	throws Exception 
	{
		//PrintWriter wout = new PrintWriter (out);
		Writer wout = new OutputStreamWriter(out,"UTF-8");
		merge (context, wout);
		wout.close();
	}
	*/
	
	/**
	 * Merge context with template to produce 
	 * output on 'out'. 
	 * 
	 * @param context
	 * @param out
	 * @throws Exception
	 */
	public void merge (Context context, Writer wout)
	throws TemplateException
	{
		VelocityContext vcontext = context.getVelocityContext();
		if (vcontext == null) {
			vcontext = new VelocityContext();
		}
		velocityTemplate.merge(vcontext, wout);
	}
	

}
