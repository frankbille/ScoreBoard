package dk.frankbille.scoreboard.components;

import java.text.Format;

import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.IModel;

public class FormatModel extends AbstractReadOnlyModel<String> {
	private static final long serialVersionUID = 1L;

	private final Format format;

	private final IModel<Object> objectModel;

	@SuppressWarnings("unchecked")
	public FormatModel(Format format, final Object object) {
		this.format = format;
		if (object instanceof IModel<?>) {
			objectModel = (IModel<Object>) object;
		} else {
			this.objectModel = new AbstractReadOnlyModel<Object>() {
				private static final long serialVersionUID = 1L;

				@Override
				public Object getObject() {
					return object;
				}
			};
		}
	}

	@Override
	public String getObject() {
		Object object = objectModel.getObject();
		return format.format(object);
	}

}
