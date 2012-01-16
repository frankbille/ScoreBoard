package dk.frankbille.scoreboard;

import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.spring.injection.annot.SpringBean;

import dk.frankbille.scoreboard.service.ScoreBoardService;

public class HomePage extends WebPage {
	private static final long serialVersionUID = 1L;

	@SpringBean
	private ScoreBoardService scoreBoardService;

    public HomePage(final PageParameters parameters) {
    	scoreBoardService.test();
    }
}
