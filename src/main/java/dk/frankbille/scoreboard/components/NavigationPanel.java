package dk.frankbille.scoreboard.components;

import java.util.ArrayList;
import java.util.List;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;

public abstract class NavigationPanel<T> extends Panel {
	private static final long serialVersionUID = 1L;

	public NavigationPanel(String id, final PaginationModel<T> paginationModel) {
		super(id);
		
		setOutputMarkupId(true);
		
		add(new AjaxLink<Void>("previousLink") {
			private static final long serialVersionUID = 1L;

			@Override
			public void onClick(AjaxRequestTarget target) {
				pageChanged(target, paginationModel.previousPage());
			}
			
			@Override
			public boolean isVisible() {
				return paginationModel.isPreviousPossible();
			}
		});
		
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
				item.add(pageLink);
				
				pageLink.add(new Label("pageLabel", item.getModel()));
			}
		});
		
		add(new AjaxLink<Void>("nextLink") {
			private static final long serialVersionUID = 1L;

			@Override
			public void onClick(AjaxRequestTarget target) {
				pageChanged(target, paginationModel.nextPage());
			}
			
			@Override
			public boolean isVisible() {
				return paginationModel.isNextPossible();
			}
		});
	}
	
	private void pageChanged(AjaxRequestTarget target, int selectedPage) {
		target.add(this);
		onPageChanged(target, selectedPage);
	}
	
	protected abstract void onPageChanged(AjaxRequestTarget target, int selectedPage);

}
