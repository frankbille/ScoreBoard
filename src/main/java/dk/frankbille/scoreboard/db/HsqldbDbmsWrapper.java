package dk.frankbille.scoreboard.db;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * PLEASE NOTE! This file is licensed under Apache Software License V2
 *
 * @author Frank Bille
 */
class HsqldbDbmsWrapper extends AbstractDbmsWrapper implements DbmsWrapper {

	@Override
	public void createVersionTable(Connection connection, String tableName, String columnName) throws SQLException {
		Statement statement = connection.createStatement();
		statement.executeUpdate("CREATE TABLE "+tableName+" ("+columnName+" VARCHAR(32) NOT NULL PRIMARY KEY)");
		statement.close();
	}

}
