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

public class TooltipBehavior extends Behavior {
    private static final long serialVersionUID = 1L;
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
        component.add(AttributeModifier.replace("rel", "tooltip-" + getPlacement()));
        component.add(AttributeModifier.replace("title", titleModel));
    }

    @Override
    public void renderHead(Component component, IHeaderResponse response) {
        response.render(OnDomReadyHeaderItem.forScript("$(\"*[rel=tooltip-" + getPlacement() + "]\").tooltip({placement:\"" + getPlacement() + "\"});"));
    }

    private String getPlacement() {
        return placement.name().toLowerCase();
    }

    public static enum Placement {
        TOP,
        BOTTOM,
        LEFT,
        RIGHT
    }

}
