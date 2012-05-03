package dk.frankbille.scoreboard.player;

import org.apache.wicket.spring.injection.annot.SpringBean;

import dk.frankbille.scoreboard.components.InjectableDetachableModel;
import dk.frankbille.scoreboard.domain.Player;
import dk.frankbille.scoreboard.service.ScoreBoardService;

public class PlayerModel extends InjectableDetachableModel<Player> {
	private static final long serialVersionUID = 1L;

	private final Long playerId;

	@SpringBean
	private ScoreBoardService scoreBoardService;

	public PlayerModel(Long playerId) {
		this.playerId = playerId;
	}

	@Override
	protected Player load() {
		return scoreBoardService.getPlayer(playerId);
	}
}