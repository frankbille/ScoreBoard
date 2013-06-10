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

import java.util.ArrayList;
import java.util.List;

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;

public abstract class NavigationPanel<T> extends Panel {
	private static final long serialVersionUID = 1L;

	public NavigationPanel(String id, final PaginationModel<T> paginationModel) {
		super(id);
		
		setOutputMarkupId(true);
		
		AjaxLink<Void> previousLink = new AjaxLink<Void>("previousLink") {
			private static final long serialVersionUID = 1L;

			@Override
			public void onClick(AjaxRequestTarget target) {
				pageChanged(target, paginationModel.previousPage());
			}
			
			@Override
			public boolean isEnabled() {
				return paginationModel.isPreviousPossible();
			}
		};
		previousLink.add(AttributeModifier.append("class", new AbstractReadOnlyModel<String>() {
			private static final long serialVersionUID = 1L;

			@Override
			public String getObject() {
				if (paginationModel.isPreviousPossible()) {
					return null;
				}
				
				return "disabled";
			}
		}));
		add(previousLink);
		
		IModel<List<Integer>> pagesModel = new LoadableDetachableModel<List<Integer>>() {
			private static final long serialVersionUID = 1L;

			@Override
			protected List<Integer> load() {
				List<Integer> pages = new ArrayList<Integer>();
				for (int page = 1; page <= paginationModel.getMaxPage(); page++) {
					pages.add(page);
				}
				return pages;
			}
		};
		add(new ListView<Integer>("pageLinks", pagesModel) {
			private static final long serialVersionUID = 1L;

			@Override
			protected void populateItem(final ListItem<Integer> item) {
				AjaxLink<Void> pageLink = new AjaxLink<Void>("pageLink") {
					private static final long serialVersionUID = 1L;

					@Override
					public void onClick(AjaxRequestTarget target) {
						paginationModel.setPage(item.getModelObject());
						pageChanged(target, paginationModel.getPage());
					}
					
					@Override
					public boolean isEnabled() {
						return paginationModel.getPage() != item.getModelObject();
					}
				};
				pageLink.add(AttributeModifier.append("class", new AbstractReadOnlyModel<String>() {
					private static final long serialVersionUID = 1L;

					@Override
					public String getObject() {
						if (paginationModel.getPage() == item.getModelObject()) {
							return "btn-primary disabled";
						}

						return null;
					}
				}));
				item.add(pageLink);
				
				pageLink.add(new Label("pageLabel", item.getModel()));
			}
		});
		
		AjaxLink<Void> nextLink = new AjaxLink<Void>("nextLink") {
			private static final long serialVersionUID = 1L;

			@Override
			public void onClick(AjaxRequestTarget target) {
				pageChanged(target, paginationModel.nextPage());
			}
			
			@Override
			public boolean isEnabled() {
				return paginationModel.isNextPossible();
			}
		};
		nextLink.add(AttributeModifier.append("class", new AbstractReadOnlyModel<String>() {
			private static final long serialVersionUID = 1L;

			@Override
			public String getObject() {
				if (paginationModel.isNextPossible()) {
					return null;
				}
				
				return "disabled";
			}
		}));
		add(nextLink);
	}
	
	private void pageChanged(AjaxRequestTarget target, int selectedPage) {
		target.add(this);
		onPageChanged(target, selectedPage);
	}
	
	protected abstract void onPageChanged(AjaxRequestTarget target, int selectedPage);

}
