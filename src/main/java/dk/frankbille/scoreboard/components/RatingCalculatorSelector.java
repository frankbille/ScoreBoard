/*
 * ScoreBoard
 * Copyright (C) 2012-2015 Frank Bille
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

import dk.frankbille.scoreboard.domain.RatingCalculatorType;
import org.apache.wicket.behavior.AttributeAppender;
import org.apache.wicket.extensions.markup.html.form.select.IOptionRenderer;
import org.apache.wicket.extensions.markup.html.form.select.Select;
import org.apache.wicket.extensions.markup.html.form.select.SelectOptions;
import org.apache.wicket.markup.html.form.ILabelProvider;
import org.apache.wicket.markup.html.panel.GenericPanel;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;

import java.util.Arrays;
import java.util.List;

public class RatingCalculatorSelector extends GenericPanel<RatingCalculatorType> {

    public RatingCalculatorSelector(String id, IModel<RatingCalculatorType> model) {
        super(id, model);

        setRenderBodyOnly(true);

        Select<RatingCalculatorType> select = new Select<RatingCalculatorType>("select", model);
        select.add(AttributeAppender.replace("class", new AbstractReadOnlyModel<String>() {
            @Override
            public String getObject() {
                return RatingCalculatorSelector.this.getMarkupAttributes().getString("class", "");
            }
        }));
        add(select);

        List<RatingCalculatorType> groupNames = Arrays.asList(RatingCalculatorType.values());

        select.add(new SelectOptions<RatingCalculatorType>("selectOptions", groupNames, new IOptionRenderer<RatingCalculatorType>() {
            @Override
            public String getDisplayValue(RatingCalculatorType type) {
                return type.getLongName();
            }

            @Override
            public IModel<RatingCalculatorType> getModel(RatingCalculatorType type) {
                return new Model<RatingCalculatorType>(type);
            }

        }));
    }
}
