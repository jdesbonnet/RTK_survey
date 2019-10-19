package ie.strix.template;

@SuppressWarnings("serial")
public class TemplateException extends Exception {

	public TemplateException (String message) {
		super(message);
	}
	public TemplateException (Throwable e) {
		super(e);
	}
}
