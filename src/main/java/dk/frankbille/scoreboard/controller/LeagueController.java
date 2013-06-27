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
import dk.frankbille.scoreboard.domain.League;
import org.springframework.hateoas.Resource;
import org.springframework.hateoas.mvc.ControllerLinkBuilder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/leagues")
public class LeagueController {

    private Dao<League> leagueDao;

    @RequestMapping(method = RequestMethod.GET)
    @ResponseBody
    public List<Resource<League>> getLeagues() {
        return createResource(leagueDao.findAll());
    }

    @RequestMapping(method = RequestMethod.GET, value = "/{leagueId}")
    @ResponseBody
    public Resource<League> getLeague(@PathVariable Long leagueId) {
        return createResource(leagueDao.find(leagueId));
    }

    @RequestMapping(method = RequestMethod.POST)
    @ResponseBody
    public Resource<League> createLeague(@RequestBody League league) {
        return createResource(leagueDao.persist(league));
    }

    private List<Resource<League>> createResource(List<League> leagues) {
        List<Resource<League>> leagueResources = new ArrayList<>();
        for (League league : leagues) {
            leagueResources.add(createResource(league));
        }
        return leagueResources;
    }

    private Resource<League> createResource(League league) {
        if (league != null) {
            Resource<League> leagueResource = new Resource<>(league);
            leagueResource.add(ControllerLinkBuilder.linkTo(LeagueController.class).slash(league).withSelfRel());
            return leagueResource;
        }

        return null;
    }

    public void setLeagueDao(Dao<League> leagueDao) {
        this.leagueDao = leagueDao;
    }
}
