package dk.frankbille.scoreboard.dao.mybatis;

import java.util.List;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.ResultMap;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import dk.frankbille.scoreboard.domain.Game;

public interface GameMapper {

	@Insert("INSERT INTO game (date) VALUES(#{date})")
	int insertGame(Game game);

	@Update("UPDATE game SET date = #{game} WHERE id = #{id}")
	void updateGame(Game game);

	@Select("SELECT " +
			"game.id AS gameId, " +
			"game.date AS gameDate, " +
			"game_team.id AS gameTeamId, " +
			"game_team.score AS gameTeamScore, " +
			"team.id AS teamId, " +
			"team.name AS teamName, " +
			"player.id AS playerId, " +
			"player.name AS playerName " +
			"FROM " +
			"game " +
			"LEFT JOIN " +
			"game_team ON game.id=game_team.game_id " +
			"LEFT JOIN " +
			"team ON game_team.team_id=team.id " +
			"LEFT JOIN " +
			"team_players ON team.id=team_players.team_id " +
			"LEFT JOIN " +
			"player ON team_players.player_id=player.id")
	@ResultMap("GameWithDetails")
	List<Game> getAllGames();

}
