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

package dk.frankbille.scoreboard.controller;

import com.googlecode.objectify.ObjectifyService;
import dk.frankbille.scoreboard.domain.*;
import org.joda.time.DateTime;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

@Controller
@RequestMapping("/admin")
public class AdminController {

    @RequestMapping(method = RequestMethod.GET, value = "/loadtestdata")
    @ResponseBody
    public String loadTestData() {
        List<League> leagues = new ArrayList<>();
        for (int i = 1; i <= 100; i++) {
            League league = new League();
            league.setName("League " + i);
            league.setActive(true);
            ObjectifyService.ofy().save().entity(league).now();
            leagues.add(league);
        }

        List<Player> players = new ArrayList<>();
        for (int i = 1; i <= 5000; i++) {
            Player p = new Player();
            p.setName("Player " + i);
            ObjectifyService.ofy().save().entity(p).now();
            players.add(p);
        }

        for (int i = 1; i <= 10000; i++) {
            Team team1 = new Team();
            team1.setPlayers(getRandomPlayers(players, 2));
            ObjectifyService.ofy().save().entity(team1).now();

            Team team2 = new Team();
            team2.setPlayers(getRandomPlayers(players, 2));
            ObjectifyService.ofy().save().entity(team2).now();

            GameTeam gameTeam1 = new GameTeam();
            gameTeam1.setTeam(team1);
            gameTeam1.setScore(10);
            ObjectifyService.ofy().save().entity(gameTeam1).now();

            GameTeam gameTeam2 = new GameTeam();
            gameTeam2.setTeam(team2);
            gameTeam2.setScore(2);
            ObjectifyService.ofy().save().entity(gameTeam2).now();

            DateTime date = new DateTime().minusDays(new Random().nextInt(365 * 2));

            Game game = new Game();
            game.setDate(date.toDate());
            game.setGameTeam1(gameTeam1);
            game.setGameTeam2(gameTeam2);
            game.setLeague(getRandomLeague(leagues));
            ObjectifyService.ofy().save().entity(game).now();
        }

        return "LOADED";
    }

    private List<Player> getRandomPlayers(List<Player> allPlayers, int numberOfPlayers) {
        List<Player> allPlayersShuffled = new ArrayList<>(allPlayers);
        Collections.shuffle(allPlayersShuffled);
        return allPlayersShuffled.subList(0, numberOfPlayers);
    }

    private League getRandomLeague(List<League> allLeagues) {
        return allLeagues.get(new Random().nextInt(allLeagues.size()));
    }

}
