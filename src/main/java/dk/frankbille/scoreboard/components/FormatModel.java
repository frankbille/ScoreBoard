package dk.frankbille.scoreboard.components;

import java.text.Format;

import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.IModel;

public class FormatModel extends AbstractReadOnlyModel<String> {
	private static final long serialVersionUID = 1L;

	private final Format format;

	private final IModel<Object> objectModel;

	public FormatModel(Format format, final Object object) {
		this.format = format;
		this.objectModel = new AbstractReadOnlyModel<Object>() {
			private static final long serialVersionUID = 1L;

			@Override
			public Object getObject() {
				return object;
			}
		};
	}

	public FormatModel(Format format, IModel<Object> objectModel) {
		this.format = format;
		this.objectModel = objectModel;
	}

	@Override
	public String getObject() {
		return format.format(objectModel.getObject());
	}

}
