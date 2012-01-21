package dk.frankbille.scoreboard.dao;

import java.util.List;

import org.hibernate.SessionFactory;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.transform.DistinctRootEntityResultTransformer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import dk.frankbille.scoreboard.domain.Game;
import dk.frankbille.scoreboard.domain.GameTeam;

@Repository
@Transactional(propagation = Propagation.MANDATORY)
public class HibernateGameDao extends HibernateDaoSupport implements GameDao {

	@Autowired
	public HibernateGameDao(SessionFactory sessionFactory) {
		setSessionFactory(sessionFactory);
	}

	@Override
	public void saveGame(Game game) {
		for (GameTeam gameTeam : game.getTeams()) {
			getHibernateTemplate().saveOrUpdate(gameTeam.getTeam());
		}
		getHibernateTemplate().saveOrUpdate(game);
	}

	@Override
	public List<Game> getAllGames() {
		DetachedCriteria c = DetachedCriteria.forClass(Game.class);
		c.addOrder(Order.desc("date"));
		c.addOrder(Order.desc("id"));
//		c.setFetchMode("teams.team.players", FetchMode.JOIN);
		c.setResultTransformer(DistinctRootEntityResultTransformer.INSTANCE);
		@SuppressWarnings("unchecked")
		List<Game> result = getHibernateTemplate().findByCriteria(c);
		return result;
	}

}
