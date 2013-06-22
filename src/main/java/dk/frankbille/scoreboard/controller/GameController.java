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

import dk.frankbille.scoreboard.dao.Dao;
import dk.frankbille.scoreboard.domain.Game;
import org.springframework.hateoas.Resource;
import org.springframework.hateoas.mvc.ControllerLinkBuilder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/games")
public class GameController {

    private Dao<Game> gameDao;

    @RequestMapping(method = RequestMethod.GET)
    @ResponseBody
    public List<Resource<Game>> getGames() {
        return createResource(gameDao.findAll());
    }

    @RequestMapping(method = RequestMethod.GET, value = "/{gameId}")
    @ResponseBody
    public Resource<Game> getGame(@PathVariable Long gameId) {
        return createResource(gameDao.find(gameId));
    }

    private List<Resource<Game>> createResource(List<Game> games) {
        List<Resource<Game>> gameResources = new ArrayList<>();
        for (Game game : games) {
            gameResources.add(createResource(game));
        }
        return gameResources;
    }

    private Resource<Game> createResource(Game game) {
        if (game != null) {
            Resource<Game> gameResource = new Resource<>(game);
            gameResource.add(ControllerLinkBuilder.linkTo(GameController.class).slash(game).withSelfRel());
            return gameResource;
        }

        return null;
    }

    public void setGameDao(Dao<Game> gameDao) {
        this.gameDao = gameDao;
    }

}
