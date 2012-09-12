package dk.frankbille.scoreboard.components;

import java.util.Date;

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.datetime.PatternDateConverter;
import org.apache.wicket.datetime.markup.html.form.DateTextField;
import org.apache.wicket.markup.html.IHeaderResponse;
import org.apache.wicket.model.IModel;

/**
 * Date text field with date picker enabled.
 */
public class DateField extends DateTextField {
	private static final long serialVersionUID = 1L;

	public DateField(String id, IModel<Date> model) {
		super(id, model, new PatternDateConverter("yyyy-MM-dd", true));
		
		add(AttributeModifier.replace("rel", "datepicker"));
	}
	
	@Override
	public void renderHead(IHeaderResponse response) {
		StringBuilder b = new StringBuilder();
		b.append("$(\"*[rel=datepicker]\").datepicker({");
		b.append("format: 'yyyy-mm-dd'");
		b.append("});");
		response.renderOnDomReadyJavaScript(b.toString());
	}

}
