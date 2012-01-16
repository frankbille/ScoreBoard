package dk.frankbille.scoreboard.dao;

import java.util.List;

import org.hibernate.SessionFactory;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import dk.frankbille.scoreboard.domain.Player;

@Repository
@Transactional(propagation=Propagation.MANDATORY)
public class HibernatePlayerDao extends HibernateDaoSupport implements PlayerDao {

	@Autowired
	public HibernatePlayerDao(SessionFactory sessionFactory) {
		setSessionFactory(sessionFactory);
	}

	@Transactional(readOnly=true)
	@Override
	public List<Player> getAllPlayers() {
		DetachedCriteria c = DetachedCriteria.forClass(Player.class);
		c.addOrder(Order.asc("name"));
		@SuppressWarnings("unchecked")
		List<Player> result = getHibernateTemplate().findByCriteria(c);
		return result;
	}

	@Override
	public void savePlayer(Player player) {
		getHibernateTemplate().save(player);
	}

}
