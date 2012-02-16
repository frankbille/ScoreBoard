package dk.frankbille.scoreboard.db;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.core.io.Resource;
import org.springframework.util.Assert;

/**
 * Manages the database schema, by versioning the database and using evolution
 * SQL scripts to bring the database up to the correct version. It is meant to
 * be used together with Spring, to easily handle the database part before any
 * application level actions take place.
 * <p>
 * The schema evolution files must be named like this: NUMBER.sql, f.ex. 1.sql,
 * 2.sql, 10.sql, 00001.sql etc. The files will then be sorted by the numeric
 * value of the file name and executed in that order. The number value will also
 * server as a version number for to which level of evolution the database has
 * been upgraded to.
 * <p>
 * PLEASE NOTE! This file is licensed under Apache Software License V2
 *
 * @author Frank Bille
 */
public class DatabaseManager implements InitializingBean {
	private static final Logger logger = LoggerFactory.getLogger(DatabaseManager.class);

	public static enum DBMS {
		HSQLDB,
		MYSQL
	}

	private static final Map<DBMS, DbmsWrapper> DBMS_WRAPPERS = new HashMap<DBMS, DbmsWrapper>();

	static {
		DBMS_WRAPPERS.put(DBMS.MYSQL, new MysqlDbmsWrapper());
		DBMS_WRAPPERS.put(DBMS.HSQLDB, new HsqldbDbmsWrapper());
	}

	private DataSource dataSource;

	private DBMS dbms;

	private List<Resource> sqlFiles;

	private String versionTableName = "database_version";

	private String versionTableColumnName = "version";

	@Override
	public void afterPropertiesSet() throws Exception {
		Assert.notNull(dataSource, "Property 'dataSource' is required");
		Assert.notNull(dbms, "Property 'dbms' is required");
		Assert.notNull(sqlFiles, "Property 'sqlFiles' is required");

		Connection connection = dataSource.getConnection();

		ensureVersionTableExists(connection);

		sortSqlFiles();

		int databaseVersion = getDatabaseVersion(connection);

		int newDatabaseVersion = updateDatabase(connection, databaseVersion);

		if (newDatabaseVersion > databaseVersion) {
			setDatabaseVersion(connection, databaseVersion, newDatabaseVersion);
		} else {
			logger.info("No update of database required");
		}

		connection.close();
	}

	private int updateDatabase(Connection connection, int databaseVersion) throws SQLException, IOException {
		for (Resource resource : sqlFiles) {
			int fileVersion = getNumericName(resource);
			if (fileVersion > databaseVersion) {
				logger.info("Applying database version "+fileVersion);

				InputStream inputStream = resource.getInputStream();
				String resourceContents = IOUtils.toString(inputStream);
				executeSql(connection, resourceContents);

				databaseVersion = fileVersion;
			}
		}
		return databaseVersion;
	}

	private void sortSqlFiles() {
		Collections.sort(sqlFiles, new Comparator<Resource>() {
			@Override
			public int compare(Resource o1, Resource o2) {
				int compare = 0;

				if (o1 != null && o2 != null) {
					compare = new Integer(getNumericName(o1)).compareTo(getNumericName(o2));
				} else if (o1 != null) {
					compare = -1;
				} else if (o2 != null) {
					compare = 1;
				}

				return compare;
			}
		});
	}

	private int getDatabaseVersion(Connection connection) throws SQLException {
		DbmsWrapper dbmsWrapper = DBMS_WRAPPERS.get(dbms);
		return dbmsWrapper.getDatabaseVersion(connection, versionTableName, versionTableColumnName);
	}

	private void setDatabaseVersion(Connection connection, int oldVersion, int newVersion) throws SQLException {
		DbmsWrapper dbmsWrapper = DBMS_WRAPPERS.get(dbms);
		dbmsWrapper.setDatabaseVersion(connection, versionTableName, versionTableColumnName, oldVersion, newVersion);
	}

	private void ensureVersionTableExists(Connection connection) throws SQLException {
		if (false == versionTableExists(connection)) {
			createVersionTable(connection);
		}
	}

	private boolean versionTableExists(Connection connection) throws SQLException {
		boolean exists = false;
		DatabaseMetaData metaData = connection.getMetaData();
		ResultSet tables = metaData.getTables(null, null, versionTableName, null);
		exists = tables.next();
		tables.close();
		return exists;
	}

	private void createVersionTable(Connection connection) throws SQLException {
		DbmsWrapper dbmsWrapper = DBMS_WRAPPERS.get(dbms);
		dbmsWrapper.createVersionTable(connection, versionTableName, versionTableColumnName);
	}

	private void executeSql(Connection connection, String sql) throws SQLException {
		Statement statement = connection.createStatement();
		statement.executeUpdate(sql);
		statement.close();
	}

	private int getNumericName(Resource resource) {
		String filename = resource.getFilename();
		filename = filename.replace(".sql", "");
		return Integer.parseInt(filename);
	}

	public DataSource getDataSource() {
		return dataSource;
	}

	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
	}

	public DBMS getDbms() {
		return dbms;
	}

	public void setDbms(DBMS dbms) {
		this.dbms = dbms;
	}

	public Resource[] getSqlFiles() {
		return sqlFiles != null ? sqlFiles.toArray(new Resource[sqlFiles.size()]) : null;
	}

	public void setSqlFiles(Resource[] sqlFiles) {
		this.sqlFiles = Arrays.asList(sqlFiles);
	}

	public String getVersionTableName() {
		return versionTableName;
	}

	public void setVersionTableName(String versionTableName) {
		this.versionTableName = versionTableName;
	}

	public String getVersionTableColumnName() {
		return versionTableColumnName;
	}

	public void setVersionTableColumnName(String versionTableColumnName) {
		this.versionTableColumnName = versionTableColumnName;
	}

}
