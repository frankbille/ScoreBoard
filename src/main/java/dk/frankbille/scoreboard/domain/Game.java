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

package dk.frankbille.scoreboard.domain;

import com.googlecode.objectify.Ref;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Load;
import org.springframework.hateoas.Identifiable;

import java.util.Date;

@Entity
public class Game implements Identifiable<Long> {

    @Id
    private Long id;
    private Date date;
    @Load
    private Ref<GameTeam> gameTeam1Key;
    @Load
    private Ref<GameTeam> gameTeam2Key;
    @Load
    private Ref<League> leagueKey;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public GameTeam getGameTeam1() {
        return gameTeam1Key.get();
    }

    public void setGameTeam1(GameTeam gameTeam1) {
        this.gameTeam1Key = Ref.create(gameTeam1);
    }

    public GameTeam getGameTeam2() {
        return gameTeam2Key.get();
    }

    public void setGameTeam2(GameTeam gameTeam2) {
        this.gameTeam2Key = Ref.create(gameTeam2);
    }

    public League getLeague() {
        return leagueKey.get();
    }

    public void setLeague(League league) {
        this.leagueKey = Ref.create(league);
    }
}
