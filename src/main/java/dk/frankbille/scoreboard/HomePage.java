package dk.frankbille.scoreboard;

import java.util.List;

import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.IAjaxCallDecorator;
import org.apache.wicket.ajax.calldecorator.AjaxCallDecorator;
import org.apache.wicket.ajax.form.AjaxFormSubmitBehavior;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.spring.injection.annot.SpringBean;

import dk.frankbille.scoreboard.domain.Player;
import dk.frankbille.scoreboard.service.ScoreBoardService;

public class HomePage extends WebPage {
	private static final long serialVersionUID = 1L;

	@SpringBean
	private ScoreBoardService scoreBoardService;

    public HomePage(final PageParameters parameters) {
    	final IModel<List<Player>> playerListModel = new LoadableDetachableModel<List<Player>>() {
			private static final long serialVersionUID = 1L;

			@Override
			protected List<Player> load() {
				return scoreBoardService.getAllPlayers();
			}
		};

		final WebMarkupContainer playerListContainer = new WebMarkupContainer("playerListContainer") {
			private static final long serialVersionUID = 1L;

			@Override
			public boolean isVisible() {
				return false == playerListModel.getObject().isEmpty();
			}
		};
		playerListContainer.setOutputMarkupId(true);
		playerListContainer.setOutputMarkupPlaceholderTag(true);
		add(playerListContainer);

		playerListContainer.add(new ListView<Player>("players", playerListModel) {
			private static final long serialVersionUID = 1L;

			@Override
			protected void populateItem(ListItem<Player> item) {
				item.add(new Label("name", item.getModelObject().getName()));
			}
		});

		IModel<String> nameModel = new Model<String>() {
			private static final long serialVersionUID = 1L;

			@Override
			public void setObject(String name) {
				scoreBoardService.createNewPlayer(name);
			}
		};

    	final TextField<String> nameField = new TextField<String>("name", nameModel);
    	nameField.setOutputMarkupId(true);

    	Form<Void> form = new Form<Void>("form");
    	form.add(new AjaxFormSubmitBehavior("onsubmit") {
			private static final long serialVersionUID = 1L;

			@Override
			protected void onSubmit(AjaxRequestTarget target) {
				target.add(playerListContainer);
				target.add(nameField);
				target.focusComponent(nameField);
			}

			@Override
			protected void onError(AjaxRequestTarget target) {
			}

			@Override
			protected IAjaxCallDecorator getAjaxCallDecorator() {
				return new AjaxCallDecorator() {
					private static final long serialVersionUID = 1L;

					@Override
					public CharSequence decorateScript(Component c, CharSequence script) {
						return new StringBuilder(script).append("; return false;");
					}
				};
			}
		});
    	add(form);
    	form.add(nameField);
    }
}
