/*
 * ScoreBoard
 * Copyright (C) 2012-2013 Frank Bille
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package dk.frankbille.scoreboard.components;

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.datetime.PatternDateConverter;
import org.apache.wicket.datetime.markup.html.form.DateTextField;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.OnDomReadyHeaderItem;
import org.apache.wicket.model.IModel;

import java.util.Date;

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
        response.render(OnDomReadyHeaderItem.forScript(b));
    }

}
