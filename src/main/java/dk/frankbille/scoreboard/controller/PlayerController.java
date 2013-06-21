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
import dk.frankbille.scoreboard.domain.Player;
import org.springframework.hateoas.Resource;
import org.springframework.hateoas.mvc.ControllerLinkBuilder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/player")
public class PlayerController {

    private Dao<Player> playerDao;

    @RequestMapping(method = RequestMethod.GET, value = "/{playerId}")
    @ResponseBody
    public Resource<Player> getPlayer(@PathVariable Long playerId) {
        return createResource(playerDao.find(playerId));
    }

    @RequestMapping(method = RequestMethod.POST)
    @ResponseBody
    public Resource<Player> createPlayer(@RequestBody Player player) {
        return createResource(playerDao.persist(player));
    }

    private Resource<Player> createResource(Player player) {
        if (player != null) {
            Resource<Player> playerResource = new Resource<>(player);
            playerResource.add(ControllerLinkBuilder.linkTo(PlayerController.class).slash(player).withSelfRel());
            return playerResource;
        }

        return null;
    }

    public void setPlayerDao(Dao<Player> playerDao) {
        this.playerDao = playerDao;
    }
}
