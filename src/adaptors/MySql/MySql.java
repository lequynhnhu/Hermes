package adaptors.MySql;

import adaptors.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;

import pool.Pool;

import com.mysql.jdbc.Statement;

import configuration.Configuration;
import core.Hermes;

public class MySql extends Adaptor {

  private static HashMap<String, String> typesMapping = null;

  //used for save jointures
  public int save(HashMap<String, Object> attributes_values, String tableName) {
    Connection connexion = null;
    Integer id = 0;
    Pool pool = Pool.getInstance();
    ResultSet rs = null;
    try {
      connexion = pool.getConnexion();
      PreparedStatement statement = connexion.prepareStatement(SqlBuilder.insert(attributes_values,tableName), Statement.RETURN_GENERATED_KEYS);
      statement.execute();
      rs = statement.getGeneratedKeys();
      if (rs.next()) {
        id = rs.getInt(1);
      }
    } catch (SQLException e) {
      e.printStackTrace();
      return -1;
    } finally {
      pool.release(connexion);
      try {
        if (rs != null) {
          rs.close();
        }
      } catch (SQLException e) {
        e.printStackTrace();
      }
    }
    return id;
  }
  public int save(HashMap<String, Object> attributes_values, Class<? extends Hermes> model) {
    Connection connexion = null;
    Integer id = 0;
    Pool pool = Pool.getInstance();
    ResultSet rs = null;
    try {
      connexion = pool.getConnexion();
      PreparedStatement statement = connexion.prepareStatement(SqlBuilder.insert(attributes_values, model), Statement.RETURN_GENERATED_KEYS);
      statement.execute();
      rs = statement.getGeneratedKeys();
      if (rs.next()) {
        id = rs.getInt(1);
      }
    } catch (SQLException e) {
      e.printStackTrace();
      return -1;
    } finally {
      pool.release(connexion);
      try {
        if (rs != null) {
          rs.close();
        }
      } catch (SQLException e) {
        e.printStackTrace();
      }
    }
    return id;
  }

  public boolean update(String tableName, HashMap<String, Object> attributes_values, int id) {
    Connection connexion = null;
    Pool pool = Pool.getInstance();
    ResultSet rs = null;
    try {
      connexion = pool.getConnexion();
      PreparedStatement statement = connexion.prepareStatement(SqlBuilder.update(attributes_values, id, tableName));
      statement.execute();
    } catch (SQLException e) {
      e.printStackTrace();
      return false;
    } finally {
      pool.release(connexion);
      try {
        if (rs != null) {
          rs.close();
        }
      } catch (SQLException e) {
        e.printStackTrace();
      }
    }
    return true;
  }

  public boolean delete(String tableName, int id) {
    Connection connexion = null;
    Pool pool = Pool.getInstance();
    boolean deleted = false;
    String sql = "delete from " + tableName + " where id =" + id;


    try {
      connexion = pool.getConnexion();
      PreparedStatement statement = connexion.prepareStatement(sql);
      statement.execute();
      deleted = true;
    } catch (Exception e) {
      e.printStackTrace();
    } finally {
      pool.release(connexion);
    }
    return deleted;
  }

  public boolean delete(String tableName, String whereClause) {
    Connection connexion = null;
    Pool pool = Pool.getInstance();
    boolean deleted = false;
    String sql = "delete from " + tableName + " where " + whereClause;


    try {
      connexion = pool.getConnexion();
      PreparedStatement statement = connexion.prepareStatement(sql);
      statement.execute();
      deleted = true;
    } catch (Exception e) {
      e.printStackTrace();
    } finally {
      pool.release(connexion);
    }
    return deleted;
  }

  public Hermes find(int id, Class<? extends Hermes> model) {
    Connection connexion = null;
    Pool pool = Pool.getInstance();

    try {
      String sql = SqlBuilder.select("*", "id = " + id, model.newInstance());
      connexion = pool.getConnexion();
      ResultSet rs = connexion.prepareStatement(sql).executeQuery();
      return Record.toObject(rs, model);

    } catch (Exception e) {
      e.printStackTrace();
    } finally {
      pool.release(connexion);

    }
    return null;

  }

  public ResultSet find(String select_clause, String where_clause, Hermes model) {
    Connection connexion = null;
    Pool pool = Pool.getInstance();
    try {
      connexion = pool.getConnexion();
      PreparedStatement statement = connexion.prepareStatement(SqlBuilder.select(select_clause, where_clause, model));
      return statement.executeQuery();

    } catch (Exception e) {
      e.printStackTrace();
      return null;
    } finally {
      pool.release(connexion);
    }
  }

  public String javaToSql(String javaType) {
    if (typesMapping == null) {
      setTypesMapping();
    }
    return typesMapping.get(javaType);
  }

  // Private methods
  private static void setTypesMapping() {
    typesMapping = new HashMap<String, String>();
    typesMapping.put("int", "int");
    typesMapping.put("Integer", "int");
    typesMapping.put("String", "varchar(" + Configuration.SqlConverterConfig.varcharLength + ")");
  }
}
