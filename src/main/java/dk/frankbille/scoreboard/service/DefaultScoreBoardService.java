package dk.frankbille.scoreboard.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import dk.frankbille.scoreboard.dao.GameDao;

@Repository
@Transactional(propagation = Propagation.REQUIRED)
public class DefaultScoreBoardService implements ScoreBoardService {

	private GameDao gameDao;

	@Autowired
	public DefaultScoreBoardService(GameDao gameDao) {
		this.gameDao = gameDao;
	}

	@Override
	public void test() {
		gameDao.test();
	}

}
