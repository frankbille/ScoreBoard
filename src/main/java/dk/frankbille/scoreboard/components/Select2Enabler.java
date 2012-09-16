package dk.frankbille.scoreboard.components;

import org.apache.wicket.Component;
import org.apache.wicket.markup.html.IHeaderResponse;

import com.vaynberg.wicket.select2.Select2ResourcesBehavior;

public class Select2Enabler extends Select2ResourcesBehavior {
	private static final long serialVersionUID = 1L;

	public void bind(Component component) {
		component.setOutputMarkupId(true);
	}
	
	@Override
	public void renderHead(Component component, IHeaderResponse response) {
		super.renderHead(component, response);
		
		response.renderOnDomReadyJavaScript("$(\"#"+component.getMarkupId()+"\").select2();");
	}
}
