package tud.ggpserver.util;

import java.util.List;

public class DropDownMenu extends HtmlForm {
	private List<String> options;
	private String selected;


	public String getSelected() {
		return selected;
	}

	public void setSelected(String selected) {
		this.selected = selected;
	}

	public DropDownMenu(List<String> options, String id) {
		super();
		this.options = options;
		this.id = id;
		this.selected = "";
	}

	public DropDownMenu(List<String> options, String selected, String id) {
		super();
		this.options = options;
		this.selected = selected;
		this.id = id;
	}

	@Override
	public String getHtml() {
		String html = "<select name=\"" + id + "\"" + onChange + ">";
		html += getOptions();
		return html + "</select>";
	}
	
	private String getOptions() {
		String html = "";
		for (String option : options) {
			if (option.equals(selected))
				html += "<option selected=\"selected\">" + option + "</option>\n";
			else
				html += "<option>" + option + "</option>\n";
		}
		
		return html;
	}
	
}
