package dk.frankbille.scoreboard.components;

import org.apache.wicket.behavior.AttributeAppender;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.IModel;

public class RowColorModifier extends AttributeAppender {
	private static final long serialVersionUID = 1L;

	public static RowColorModifier create(final ListItem<?> listItem) {
		return new RowColorModifier(new AbstractReadOnlyModel<String>() {
			private static final long serialVersionUID = 1L;

			@Override
			public String getObject() {
				return listItem.getIndex() % 2 == 0 ? "odd" : "even";
			}
		});
	}

	private RowColorModifier(IModel<?> appendModel) {
		super("class", appendModel, " ");
	}

}
