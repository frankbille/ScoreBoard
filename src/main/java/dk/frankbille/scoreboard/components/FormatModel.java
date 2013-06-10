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
