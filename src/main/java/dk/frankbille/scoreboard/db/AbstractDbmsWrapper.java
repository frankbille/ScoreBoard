package dk.frankbille.scoreboard.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * PLEASE NOTE! This file is licensed under Apache Software License V2
 *
 * @author Frank Bille
 */
abstract class AbstractDbmsWrapper implements DbmsWrapper {

	@Override
	public int getDatabaseVersion(Connection connection, String tableName, String columnName) throws SQLException {
		int version = 0;

		PreparedStatement preparedStatement = connection.prepareStatement("SELECT " + columnName + " FROM " + tableName);
		ResultSet result = preparedStatement.executeQuery();
		if (result.next()) {
			version = result.getInt(columnName);
		}
		result.close();
		preparedStatement.close();

		return version;
	}

	@Override
	public void setDatabaseVersion(Connection connection, String tableName, String columnName, int oldVersion, int newVersion) throws SQLException {
		PreparedStatement preparedStatement = connection.prepareStatement("DELETE FROM " + tableName + " WHERE " + columnName + " = ?");
		preparedStatement.setInt(1, oldVersion);
		preparedStatement.executeUpdate();
		preparedStatement.close();

		preparedStatement = connection.prepareStatement("INSERT INTO " + tableName + " (" + columnName + ") VALUES (?)");
		preparedStatement.setInt(1, newVersion);
		preparedStatement.executeUpdate();
		preparedStatement.close();
	}

}