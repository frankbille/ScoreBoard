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

package dk.frankbille.scoreboard.dao;

import com.googlecode.objectify.Key;
import com.googlecode.objectify.ObjectifyService;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public abstract class AbstractDao<T> implements Dao<T> {

    private final Class<T> clazz;

    public AbstractDao(Class<T> clazz) {
        this.clazz = clazz;
    }

    @Override
    public T find(Long id) {
        return ObjectifyService.ofy().load().key(Key.create(clazz, id)).now();
    }

    @Override
    public T find(String id) {
        return ObjectifyService.ofy().load().key(Key.create(clazz, id)).now();
    }

    @Override
    public T find(Key<T> key) {
        return ObjectifyService.ofy().load().key(key).now();
    }

    @Override
    public Key<T> key(T t) {
        return Key.create(t);
    }

    @Override
    public List<T> findAll() {
        return ObjectifyService.ofy().load().type(clazz).list();
    }

    @Override
    public List<T> findAll(List<Key<T>> keys) {
        if (keys == null) {
            return null;
        }
        final Map<Key<T>, T> map = ObjectifyService.ofy().load().keys(keys);
        final List<T> list = new ArrayList<>();
        for (final T t : map.values()) {
            list.add(t);
        }
        return list;
    }

    @Override
    public List<Key<T>> key(List<T> list) {
        if (list == null) {
            return null;
        }
        final List<Key<T>> keys = new ArrayList<>(list.size());
        for (final T t : list) {
            final Key<T> key = key(t);
            keys.add(key);
        }
        return keys;
    }

    @Override
    public T persist(T t) {
        ObjectifyService.ofy().save().entity(t).now();
        return t;
    }

    @Override
    public void delete(T t) {
        ObjectifyService.ofy().delete().entity(t).now();
    }

}
