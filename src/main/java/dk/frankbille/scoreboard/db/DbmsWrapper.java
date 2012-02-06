package dk.frankbille.scoreboard.db;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * DBMS helper, for interacting with the version table in the database. Because
 * SQL dialects can be different, an implementation should be provided for each
 * database vendor.
 * <p>
 * PLEASE NOTE! This file is licensed under Apache Software License V2
 *
 * @author Frank Bille
 */
interface DbmsWrapper {

	void createVersionTable(Connection connection, String tableName, String columnName) throws SQLException;

	int getDatabaseVersion(Connection connection, String tableName, String columnName) throws SQLException;

	void setDatabaseVersion(Connection connection, String tableName, String columnName, int oldVersion, int newVersion) throws SQLException;

}
