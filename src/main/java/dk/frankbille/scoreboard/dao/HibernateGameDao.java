package dk.frankbille.scoreboard.dao;

import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import dk.frankbille.scoreboard.domain.Player;

@Repository
@Transactional(propagation = Propagation.MANDATORY)
public class HibernateGameDao extends HibernateDaoSupport implements GameDao {

	@Autowired
	public HibernateGameDao(SessionFactory sessionFactory) {
		setSessionFactory(sessionFactory);
	}

	@Override
	public void test() {
		Player player = new Player();
		player.setName("Frank");
		getHibernateTemplate().save(player);
	}

}
