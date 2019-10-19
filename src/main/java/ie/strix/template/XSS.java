package ie.strix.template;

/**
 * Sanitize strings supplied as inputs to web forms for potential XSS 
 * (cross-site scripting) attack code. Inputed strings should never
 * be echoed back to user in HTML with applying this filter.
 * 
 * @author Joe Desbonnet
 *
 */
public class XSS {

	public static String clean (String in) {
		if (in == null) {
			return "";
		}
		return in.replaceAll("<", "&lt;").replaceAll(">", "&gt;").replaceAll("&","&amp;").replaceAll("\"", "&quot;");
	}
}
