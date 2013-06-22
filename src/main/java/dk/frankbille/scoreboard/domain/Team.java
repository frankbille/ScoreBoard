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

import java.util.ArrayList;
import java.util.List;

@Entity
public class Team implements Identifiable<Long> {

    @Id
    private Long id;
    private String name;
    @Load
    private List<Ref<Player>> playerRefs;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Player> getPlayers() {
        List<Player> players = new ArrayList<>();
        for (Ref<Player> playerRef : playerRefs) {
            players.add(playerRef.get());
        }
        return players;
    }

    public void setPlayers(List<Player> players) {
        playerRefs = new ArrayList<>();
        for (Player player : players) {
            playerRefs.add(Ref.create(player));
        }
    }

}
