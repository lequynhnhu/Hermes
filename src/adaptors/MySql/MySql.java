package adaptors.MySql;

import adaptors.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import pool.Pool;

import com.mysql.jdbc.Statement;

import core.Hermes;

public class MySql extends Adaptor {

	public boolean save(Hermes object) {
		return execute(SqlBuilder.build("insert", null, object), object);
	}

	public boolean update(Hermes object) {
		return execute(SqlBuilder.build("update", null, object), object);
	}

	public boolean delete(Hermes object) {
		return execute(SqlBuilder.build("delete", null, object), object);
	}

	public boolean delete(Hermes object, String conditions) {
		return execute(SqlBuilder.build("delete", conditions, object), object);
	}

	public ResultSet find(String select, String conditions, Hermes model) {
		Connection connexion = null;
		Pool pool = Pool.getInstance();
		String sql = SqlBuilder.build("select", select, conditions, model);
		try {
			connexion = pool.getConnexion();
			PreparedStatement statement = connexion.prepareStatement(sql);
			return statement.executeQuery();
		}
		catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		finally {
			pool.release(connexion);
		}
	}

	public String javaToSql(String javaType) {
		return Mapping.javaToSql(javaType);
	}

	public boolean execute(String sql, Hermes object) {
		Connection connexion = null;
		Pool pool = Pool.getInstance();
		ResultSet rs = null;
		PreparedStatement statement;
		try {
			connexion = pool.getConnexion();
			statement = connexion.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
			statement.execute();
			rs = statement.getGeneratedKeys();
			if (rs.next()) object.setId(rs.getInt(1));
		}
		catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
		finally {
			pool.release(connexion);
			try {
				if (rs != null) rs.close();
			}
			catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return true;
	}
}
