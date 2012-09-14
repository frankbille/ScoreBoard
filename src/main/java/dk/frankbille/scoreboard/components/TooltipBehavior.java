package dk.frankbille.scoreboard.components;

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.Component;
import org.apache.wicket.behavior.Behavior;
import org.apache.wicket.markup.html.IHeaderResponse;
import org.apache.wicket.model.IModel;

public class TooltipBehavior extends Behavior {
	private static final long serialVersionUID = 1L;

	private IModel<? extends CharSequence> titleModel; 
	
	public TooltipBehavior(IModel<? extends CharSequence> titleModel) {
		this.titleModel = titleModel;
	}

	@Override
	public void bind(Component component) {
		component.add(AttributeModifier.replace("rel", "tooltip-left"));
		component.add(AttributeModifier.replace("title", titleModel));
	}
	
	@Override
	public void renderHead(Component component, IHeaderResponse response) {
		response.renderOnDomReadyJavaScript("$(\"*[rel=tooltip-left]\").tooltip({placement:\"left\"});");
	}
	
}
