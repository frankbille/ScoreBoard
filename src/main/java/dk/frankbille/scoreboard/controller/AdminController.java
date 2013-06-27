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

import com.googlecode.objectify.Key;
import com.googlecode.objectify.ObjectifyService;
import com.googlecode.objectify.Ref;
import dk.frankbille.scoreboard.domain.*;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.IOException;
import java.util.*;

@Controller
@RequestMapping("/admin")
public class AdminController {

    @RequestMapping(method = RequestMethod.GET, value = "/loadbigdata")
    @ResponseBody
    public String loadBigData() {
        createTestData(100, 5000, 10000);

        return "LOADED";
    }

    @RequestMapping(method = RequestMethod.GET, value = "/loadsmalldata")
    @ResponseBody
    public String loadSmallData() {
        createTestData(5, 35, 500);

        return "LOADED";
    }

    @RequestMapping(method = RequestMethod.POST, value = "/import")
    @ResponseBody
    public String importOldData(@RequestParam("xmlDumpFile") MultipartFile xmlDumpFile) throws ParserConfigurationException, SAXException, IOException {
        SAXParserFactory factory = SAXParserFactory.newInstance();
        SAXParser parser = factory.newSAXParser();
        parser.parse(xmlDumpFile.getInputStream(), new XmlDumpParser());

        return "IMPORTED";
    }

    private void createTestData(int numberOfLeagues, int numberOfPlayers, int numberOfGames) {
        List<League> leagues = new ArrayList<>();
        for (int i = 1; i <= numberOfLeagues; i++) {
            League league = new League();
            league.setName("League " + i);
            league.setActive(true);
            ObjectifyService.ofy().save().entity(league).now();
            leagues.add(league);
        }

        List<Player> players = new ArrayList<>();
        for (int i = 1; i <= numberOfPlayers; i++) {
            Player p = new Player();
            p.setName("Player " + i);
            ObjectifyService.ofy().save().entity(p).now();
            players.add(p);
        }

        for (int i = 1; i <= numberOfGames; i++) {
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
    }

    private List<Player> getRandomPlayers(List<Player> allPlayers, int numberOfPlayers) {
        List<Player> allPlayersShuffled = new ArrayList<>(allPlayers);
        Collections.shuffle(allPlayersShuffled);
        return allPlayersShuffled.subList(0, numberOfPlayers);
    }

    private League getRandomLeague(List<League> allLeagues) {
        return allLeagues.get(new Random().nextInt(allLeagues.size()));
    }

    private static class XmlDumpParser extends DefaultHandler {
        private static final DateTimeFormatter DATE_FORMAT = ISODateTimeFormat.date();
        private String currentTable = null;
        private Object currentObject = null;
        private String fieldName = null;
        private StringBuilder fieldValue = new StringBuilder();
        private Map<Long, Team> teams = new HashMap<>();

        @Override
        public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
            if ("table_data".equalsIgnoreCase(qName)) {
                String tableName = attributes.getValue("name");
                if (!"DATABASECHANGELOGLOCK".equalsIgnoreCase(tableName)
                        && !"DATABASECHANGELOG".equalsIgnoreCase(tableName)) {
                    currentTable = tableName;
                } else {
                    currentTable = null;
                }
            } else if ("row".equalsIgnoreCase(qName)) {
                if ("game".equalsIgnoreCase(currentTable)) {
                    currentObject = new Game();
                } else if ("game_team".equalsIgnoreCase(currentTable)) {
                    currentObject = new GameTeam();
                } else if ("team".equalsIgnoreCase(currentTable)) {
                    currentObject = new Team();
                } else if ("team_players".equalsIgnoreCase(currentTable)) {
                    currentObject = new Team();
                } else if ("player".equalsIgnoreCase(currentTable)) {
                    currentObject = new Player();
                } else if ("league".equalsIgnoreCase(currentTable)) {
                    currentObject = new League();
                } else {
                    currentObject = null;
                }
            } else if ("field".equalsIgnoreCase(qName)) {
                if (currentObject != null) {
                    fieldName = attributes.getValue("name");
                    fieldValue = new StringBuilder();
                } else {
                    fieldName = null;
                }
            }
        }

        @Override
        public void endElement(String uri, String localName, String qName) throws SAXException {
            if ("field".equalsIgnoreCase(qName)) {
                if (currentObject instanceof Game) {
                    Game game = (Game) currentObject;
                    if ("id".equalsIgnoreCase(fieldName)) {
                        game.setId(getLong());
                    } else if ("game_date".equalsIgnoreCase(fieldName)) {
                        game.setDate(getDate());
                    } else if ("team1_id".equalsIgnoreCase(fieldName)) {
                        game.setGameTeam1(Ref.create(Key.create(GameTeam.class, getLong())));
                    } else if ("team2_id".equalsIgnoreCase(fieldName)) {
                        game.setGameTeam2(Ref.create(Key.create(GameTeam.class, getLong())));
                    } else if ("league_id".equalsIgnoreCase(fieldName)) {
                        game.setLeague(Ref.create(Key.create(League.class, getLong())));
                    }
                } else if (currentObject instanceof GameTeam) {
                    GameTeam gameTeam = (GameTeam) currentObject;
                    if ("id".equalsIgnoreCase(fieldName)) {
                        gameTeam.setId(getLong());
                    } else if ("score".equalsIgnoreCase(fieldName)) {
                        gameTeam.setScore(getInt());
                    } else if ("team_id".equalsIgnoreCase(fieldName)) {
                        gameTeam.setTeam(Ref.create(Key.create(Team.class, getLong())));
                    }
                } else if (currentObject instanceof Team) {
                    Team team = (Team) currentObject;
                    if ("team".equalsIgnoreCase(currentTable)) {
                        if ("id".equalsIgnoreCase(fieldName)) {
                            team.setId(getLong());
                            teams.put(team.getId(), team);
                        } else if ("name".equalsIgnoreCase(fieldName)) {
                            team.setName(getString());
                        }
                    } else if ("team_players".equalsIgnoreCase(currentTable)) {
                        if ("team_id".equalsIgnoreCase(fieldName)) {
                            team.setId(getLong());
                        } else if ("player_id".equalsIgnoreCase(fieldName)) {
                            Team teamEntity = teams.get(team.getId());
                            teamEntity.addPlayer(Ref.create(Key.create(Player.class, getLong())));
                        }
                    }
                } else if (currentObject instanceof Player) {
                    Player player = (Player) currentObject;
                    if ("id".equalsIgnoreCase(fieldName)) {
                        player.setId(getLong());
                    } else if ("name".equalsIgnoreCase(fieldName)) {
                        player.setName(getString());
                    } else if ("full_name".equalsIgnoreCase(fieldName)) {
                        player.setFullName(getString());
                    } else if ("group_name".equalsIgnoreCase(fieldName)) {
                        player.setGroupName(getString());
                    }
                } else if (currentObject instanceof League) {
                    League league = (League) currentObject;
                    if ("id".equalsIgnoreCase(fieldName)) {
                        league.setId(getLong());
                    } else if ("name".equalsIgnoreCase(fieldName)) {
                        league.setName(getString());
                    } else if ("active".equalsIgnoreCase(fieldName)) {
                        league.setActive(getBoolean());
                    }
                }
            } else if ("row".equalsIgnoreCase(qName)) {
                if (currentObject != null) {
                    if (!(currentObject instanceof Team)) {
                        ObjectifyService.ofy().save().entity(currentObject);
                        currentObject = null;
                    }
                }
            } else if ("database".equalsIgnoreCase(qName)) {
                for (Team team : teams.values()) {
                    ObjectifyService.ofy().save().entity(team);
                }
            }
        }

        @Override
        public void characters(char[] ch, int start, int length) throws SAXException {
            if (fieldName != null) {
                fieldValue.append(ch, start, length);
            }
        }

        private long getLong() {
            return Long.parseLong(fieldValue.toString());
        }

        private int getInt() {
            return Integer.parseInt(fieldValue.toString());
        }

        private String getString() {
            return fieldValue.toString();
        }

        private boolean getBoolean() {
            return getInt() == 1;
        }

        private Date getDate() {
            return DATE_FORMAT.parseDateTime(fieldValue.toString()).toDate();
        }
    }
}
