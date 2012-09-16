package dk.frankbille.scoreboard.league;

import java.util.List;

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.spring.injection.annot.SpringBean;

import dk.frankbille.scoreboard.BasePage;
import dk.frankbille.scoreboard.components.menu.MenuItemType;
import dk.frankbille.scoreboard.domain.League;
import dk.frankbille.scoreboard.security.SecureExecutionBookmarkablePageLink;
import dk.frankbille.scoreboard.security.SecureRenderingBookmarkablePageLink;
import dk.frankbille.scoreboard.service.ScoreBoardService;

public class LeagueListPage extends BasePage {
	private static final long serialVersionUID = 1L;

	@SpringBean
	private ScoreBoardService scoreBoardService;

	public LeagueListPage() {
		add(new SecureRenderingBookmarkablePageLink<League>("addNewLeagueLink", LeagueEditPage.class));
		
		IModel<List<League>> leagueListModel = new LoadableDetachableModel<List<League>>() {
			private static final long serialVersionUID = 1L;

			@Override
			protected List<League> load() {
				return scoreBoardService.getAllLeagues();
			}
		};

		add(new ListView<League>("leagues", leagueListModel) {
			private static final long serialVersionUID = 1L;

			@Override
			protected void populateItem(ListItem<League> item) {
				PageParameters pp = new PageParameters();
				pp.set(0, item.getModelObject().getId());
				Link<League> link = new SecureExecutionBookmarkablePageLink<League>("leagueLink", LeagueEditPage.class, pp);
				link.add(new Label("name", new PropertyModel<String>(item.getModel(), "name")));
				item.add(link);
			}
		});
	}
	
	@Override
	public MenuItemType getMenuItemType() {
		return MenuItemType.LEAGUES;
	}

}
