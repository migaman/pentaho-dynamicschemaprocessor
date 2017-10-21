package org.migaman.pentaho.example.dsp;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.log4j.Logger;

public final class DbPoolUtil {

	private static final Logger LOGGER = Logger.getLogger(DbPoolUtil.class);
	private static final String LITERAL_CLOSE = "Cannot close database object";

	private DbPoolUtil() {
	}

	public static void closeAll(ResultSet rs, PreparedStatement stmt, Connection conn) {
		closeResultset(rs);
		closeStatement(stmt);
		closeConnection(conn);
	}

	private static void closeResultset(ResultSet rs) {
		try {
			if (rs != null) {
				rs.close();
			}
		}
		catch (SQLException e) {
			LOGGER.error(LITERAL_CLOSE + e.getMessage(), e);
		}
	}

	private static void closeStatement(PreparedStatement stmt) {
		try {
			if (stmt != null) {
				stmt.close();
			}
		}
		catch (SQLException e) {
			LOGGER.error(LITERAL_CLOSE + e.getMessage(), e);
		}
	}

	private static void closeConnection(Connection conn) {
		try {
			if (conn != null) {
				conn.close();
			}
		}
		catch (SQLException e) {
			LOGGER.error(LITERAL_CLOSE + e.getMessage(), e);
		}
	}
}
