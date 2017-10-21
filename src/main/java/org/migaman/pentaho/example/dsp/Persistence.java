package org.migaman.pentaho.example.dsp;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

public class Persistence {
	private static final String DATASOURCE_NAME = "jdbc/ExampleJNDI";

	public List<String> getTenants(String username) throws SQLException, NamingException {
		ArrayList<String> tenants = new ArrayList<>();

		DataSource ds = null;
		InitialContext ctx = new InitialContext();

		Connection conn = null;
		PreparedStatement sqlStmt = null;
		ResultSet rs = null;

		try {
			//Use existing JNDI Connection
			Context envCtx = (Context) ctx.lookup("java:comp/env");
			ds = (DataSource) envCtx.lookup(DATASOURCE_NAME);
			conn = ds.getConnection();

			String sql = "SELECT idTenant FROM t_tenant WHERE idUser = ?";
			sqlStmt = conn.prepareStatement(sql);
			sqlStmt.setString(1, username);

			rs = sqlStmt.executeQuery();
			while (rs.next()) {
				tenants.add(rs.getString("idTenant"));
			}

		}
		finally {
			DbPoolUtil.closeAll(rs, sqlStmt, conn);
		}

		return tenants;
	}

}
