package tud.ggpserver.util;

public class TextBox extends HtmlForm{
	private String value;

	public TextBox(String value, String id) {
		super();
		this.value = value;
		this.id = id;
	}

	

	public TextBox(String id) {
		super();
		this.id = id;
		this.value = "";
	}

	

	public String getValue() {
		return value;
	}



	public void setValue(String value) {
		this.value = value;
	}



	@Override
	public String getHtml() {
		return "<input type=\"text\" name=\"" + id + "\" size=\"10\" value=\"" + value + "\" />";
	}

}
