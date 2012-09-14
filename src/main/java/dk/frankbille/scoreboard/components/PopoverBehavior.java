package dk.frankbille.scoreboard.components;

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.Component;
import org.apache.wicket.behavior.Behavior;
import org.apache.wicket.markup.html.IHeaderResponse;
import org.apache.wicket.model.IModel;

public class PopoverBehavior extends Behavior {
	private static final long serialVersionUID = 1L;

	private IModel<? extends CharSequence> titleModel;
	private IModel<? extends CharSequence> contentModel; 
	
	public PopoverBehavior(IModel<? extends CharSequence> titleModel, IModel<? extends CharSequence> contentModel) {
		this.titleModel = titleModel;
		this.contentModel = contentModel;
	}

	@Override
	public void bind(Component component) {
		component.add(AttributeModifier.replace("rel", "popover-top"));
		component.add(AttributeModifier.replace("title", titleModel));
		component.add(AttributeModifier.replace("data-content", contentModel));
	}
	
	@Override
	public void renderHead(Component component, IHeaderResponse response) {
		response.renderOnDomReadyJavaScript("$(\"*[rel=popover-top]\").popover({placement:\"top\", trigger:\"hover\"});");
	}
	
}
