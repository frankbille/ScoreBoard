package dk.frankbille.scoreboard.components;

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.Component;
import org.apache.wicket.behavior.Behavior;
import org.apache.wicket.markup.html.IHeaderResponse;
import org.apache.wicket.model.IModel;

public class TooltipBehavior extends Behavior {
	private static final long serialVersionUID = 1L;

	public static enum Placement {
		TOP,
		BOTTOM,
		LEFT,
		RIGHT
	}

	private final IModel<? extends CharSequence> titleModel;
	private final Placement placement;

	public TooltipBehavior(IModel<? extends CharSequence> titleModel) {
		this(titleModel, Placement.LEFT);
	}

	public TooltipBehavior(IModel<? extends CharSequence> titleModel, Placement placement) {
		this.titleModel = titleModel;
		this.placement = placement;
	}

	@Override
	public void bind(Component component) {
		component.add(AttributeModifier.replace("rel", "tooltip-"+getPlacement()));
		component.add(AttributeModifier.replace("title", titleModel));
	}

	@Override
	public void renderHead(Component component, IHeaderResponse response) {
		response.renderOnDomReadyJavaScript("$(\"*[rel=tooltip-"+getPlacement()+"]\").tooltip({placement:\""+getPlacement()+"\"});");
	}

	private String getPlacement() {
		return placement.name().toLowerCase();
	}

}
