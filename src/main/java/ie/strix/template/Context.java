package ie.strix.template;

import javax.servlet.http.*;

import org.apache.velocity.VelocityContext;

/**
 * @author Joe
 * A facade for the Velocity Template class.
 */
public class Context {
	
	private VelocityContext velocityContext;
	
	public Context ()
	{
		this.velocityContext = new VelocityContext();
	}
	public Context (HttpServletRequest request, HttpServletResponse response)
	{
		this();
		put ("request",request);
		put ("response", response);
	}
	
	public final void put (String name, Object o)
	{
		velocityContext.put(name,o);
	}
	
	public final Object get (String name)
	{
		return velocityContext.get(name);
	}
	
	public final void remove (String key) {
		velocityContext.remove(key);
	}
	
	public final VelocityContext getVelocityContext ()
	{
		return velocityContext;
	}
	
	
}
