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
import org.apache.wicket.Component;
import org.apache.wicket.behavior.Behavior;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.OnDomReadyHeaderItem;
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
        response.render(OnDomReadyHeaderItem.forScript("$(\"*[rel=popover-top]\").popover({placement:\"top\", trigger:\"hover\", html:true});"));
    }

}
