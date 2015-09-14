/*
 * ScoreBoard
 * Copyright (C) 2012-2015 Frank Bille
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

package dk.frankbille.scoreboard.ratings.trueskill;

import dk.frankbille.scoreboard.comparators.GameComparator;
import dk.frankbille.scoreboard.domain.*;
import dk.frankbille.scoreboard.domain.Player;
import dk.frankbille.scoreboard.ratings.*;
import jskills.*;
import jskills.Team;
import jskills.trueskill.FactorGraphTrueSkillCalculator;

import java.util.*;

public class TrueSkillRatingCalculator implements RatingCalculator {

    private Map<Long, Rating> players;
    private Map<TeamId, Rating> teams;
    private Map<Long, TrueSkillGameRating> games;
    private Map<String, TrueSkillPlayerRating> gamePlayers;
    private GameInfo gameInfo = GameInfo.getDefaultGameInfo();
    private SkillCalculator calculator;

    public TrueSkillRatingCalculator() {
        players = new HashMap<Long, Rating>();
        teams = new HashMap<TeamId, Rating>();
        games = new HashMap<Long, TrueSkillGameRating>();
        gamePlayers = new HashMap<String, TrueSkillPlayerRating>();

        gameInfo = GameInfo.getDefaultGameInfo();
        calculator = new FactorGraphTrueSkillCalculator();
    }

    @Override
    public void setGames(List<Game> games) {
        //Clear the current ratings
        this.players.clear();
        this.teams.clear();
        this.games.clear();
        this.gamePlayers.clear();

        //Order the games by date
        Collections.sort(games, new GameComparator() {
            @Override
            public int compare(Game o1, Game o2) {
                int compare = o1.getDate().compareTo(o2.getDate());

                if (compare == 0) {
                    compare = o1.getId().compareTo(o2.getId());
                }

                return compare;
            }
        });

        //Go through the games one-by-one
        for (Game game : games) {
            addPlayerRatings(game);
            addTeamRatings(game);
        }
    }

    private void addTeamRatings(Game game) throws RatingException {
        GameTeam winner = game.getWinnerTeam();
        GameTeam loser = game.getLoserTeam();
        jskills.Player<Integer> team1player = new jskills.Player<Integer>(1);
        Team team1 = new Team().addPlayer(team1player, getTeamTrueSkillRating(new TeamId(winner.getTeam())));
        jskills.Player<Integer> team2player = new jskills.Player<Integer>(2);
        Team team2 = new Team().addPlayer(team2player, getTeamTrueSkillRating(new TeamId(loser.getTeam())));

        Collection<ITeam> teamsCol = Team.concat(team1, team2);
        Map<IPlayer, Rating> ratings = calculator.calculateNewRatings(gameInfo, teamsCol, 1, winner.getScore()==loser.getScore() ? 1 : 2);

        //Add the rating change to the teams and game
        Rating team1Rating = ratings.get(team1player);
        Rating team2Rating = ratings.get(team2player);
        double team1Change = team1Rating.getMean() - getTeamTrueSkillRating(new TeamId(winner.getTeam())).getMean();
        double team2Change = team2Rating.getMean() - getTeamTrueSkillRating(new TeamId(loser.getTeam())).getMean();
        teams.put(new TeamId(winner.getTeam()), team1Rating);
        teams.put(new TeamId(loser.getTeam()), team2Rating);
        games.put(game.getId(), new TrueSkillGameRating(winner.getId(), team1Rating.getMean(), team1Change, loser.getId(), team2Rating.getMean(), team2Change));
    }
    private void addPlayerRatings(Game game) throws RatingException {
        //Calculate the teams before-ratings
        GameTeam winner = game.getWinnerTeam();
        GameTeam loser = game.getLoserTeam();

        List<jskills.Player<Long>> players = new ArrayList<jskills.Player<Long>>();
        Team team1 = new Team();
        Team team2 = new Team();
        for (Player player : winner.getTeam().getPlayers()) {
            Long playerId = player.getId();
            jskills.Player<Long> iPlayer = new jskills.Player<Long>(playerId);
            players.add(iPlayer);
            team1.addPlayer(iPlayer, getPlayerTrueSkillRating(playerId));
        }
        for (Player player : loser.getTeam().getPlayers()) {
            Long playerId = player.getId();
            jskills.Player<Long> iPlayer = new jskills.Player<Long>(playerId);
            players.add(iPlayer);
            team2.addPlayer(iPlayer, getPlayerTrueSkillRating(playerId));
        }

        Collection<ITeam> teamsCol = Team.concat(team1, team2);
        Map<IPlayer, Rating> newRatingsWinLose = calculator.calculateNewRatings(gameInfo, teamsCol, 1, winner.getScore()==loser.getScore() ? 1 : 2);

        //Add the rating change to the players
        for (jskills.Player player : players) {
            Long id = (Long) player.getId();
            Rating rating = newRatingsWinLose.get(player);
            double change = rating.getMean() - getPlayerTrueSkillRating(id).getMean();
            this.players.put(id, rating);
            gamePlayers.put(game.getId() + "-" + id, new TrueSkillPlayerRating(rating.getMean(), change));
        }
    }

    private Rating getPlayerTrueSkillRating(long playerId) {
        //Try to find the playerRating
        Rating player = players.get(playerId);

        if (player==null) {
            //Create a new default player
            player = gameInfo.getDefaultRating();
            players.put(playerId, player);
        }

        return player;
    }

    @Override
    public RatingInterface getPlayerRating(long playerId) {
        Rating rating = getPlayerTrueSkillRating(playerId);
        return new TrueSkillRating(rating.getMean(), rating.getStandardDeviation());
    }

    private Rating getTeamTrueSkillRating(TeamId teamId) {
        //Try to find the teamRating
        Rating team = teams.get(teamId);

        if (team==null) {
            //Create a new default team
            team = gameInfo.getDefaultRating();
            teams.put(teamId, team);
        }

        return team;
    }
    @Override
    public RatingInterface getTeamRating(TeamId teamId) {
        Rating rating = getTeamTrueSkillRating(teamId);
        return new TrueSkillRating(rating.getMean(), rating.getStandardDeviation());
    }

    @Override
    public double getDefaultRating() {
        return gameInfo.getDefaultRating().getMean();
    }

    @Override
    public RatingCalculatorType getType() {
        return RatingCalculatorType.TRUESKILL;
    }

    @Override
    public GamePlayerRatingInterface getGamePlayerRating(long gameId, long playerId) {
        return gamePlayers.get(gameId+"-"+playerId);
    }

    @Override
    public GameRatingInterface getGameRatingChange(Long id) {
        return games.get(id);
    }


}