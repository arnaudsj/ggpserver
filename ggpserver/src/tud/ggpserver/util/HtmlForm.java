package tud.ggpserver.util;

public abstract class HtmlForm {
	protected String id;
	protected String onChange = " onChange=\"form.submit()\" ";
	public abstract String getHtml();
	
	public String getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = String.valueOf(id);
	}
	
	
}
