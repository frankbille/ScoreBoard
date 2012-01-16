package dk.frankbille.scoreboard.service;

import java.util.List;

import dk.frankbille.scoreboard.domain.Player;

public interface ScoreBoardService {

	Player createNewPlayer(String name);

	List<Player> getAllPlayers();

}
