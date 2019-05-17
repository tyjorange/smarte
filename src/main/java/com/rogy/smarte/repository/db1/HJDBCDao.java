package com.rogy.smarte.repository.db1;
//
//import org.hibernate.Session;
//import org.hibernate.internal.SessionFactoryImpl;
//import org.springframework.stereotype.Repository;
//
//import javax.persistence.EntityManager;
//import javax.persistence.PersistenceContext;
//import java.sql.Connection;
//import java.sql.ResultSet;
//import java.sql.SQLException;
//import java.sql.Statement;
//
////import org.hibernate.jpa.HibernateEntityManager;
//
//@Repository
//public class HJDBCDao {
//
//	@PersistenceContext
//	private EntityManager entityManager;
//
//	/**
//	 * 获取一个JDBC连接。
//	 * @return JDBC连接。
//	 * @throws SQLException
//	 */
//	public Connection getJDBCConnection() throws SQLException {
//		Session session;
////		HibernateEntityManager hEntityManager = (HibernateEntityManager)entityManager;
////		session = hEntityManager.getSession();
//		session = (Session)entityManager.getDelegate();
//		SessionFactoryImpl sessionFactory = (SessionFactoryImpl) session.getSessionFactory();
//		return sessionFactory.getConnectionProvider().getConnection();
//	}
//
//	/**
//	 * 执行SQL。
//	 * @param sql SQL语句。
//	 * @return 见JDBC-execute的返回说明。
//	 * @throws SQLException
//	 */
//	public boolean execute(String sql) throws SQLException {
//		return execute(sql, true);
//	}
//	/**
//	 * 执行SQL。
//	 * @param sql SQL语句。
//	 * @param autoCommit
//	 * @return 见JDBC-execute的返回说明。
//	 * @throws SQLException
//	 */
//	public boolean execute(String sql, boolean autoCommit) throws SQLException {
//		Connection connection = getJDBCConnection();
//		connection.setAutoCommit(autoCommit);
//		Statement statement = connection.createStatement();
//		return statement.execute(sql);
//	}
//
//	/**
//	 * 执行SQL。
//	 * @param sql SQL语句。
//	 * @return 见JDBC-executeQuery的返回说明。
//	 * @throws SQLException
//	 */
//	public ResultSet executeQuery(String sql) throws SQLException {
//		return executeQuery(sql, true);
//	}
//	/**
//	 * 执行SQL。
//	 * @param sql SQL语句。
//	 * @param autoCommit
//	 * @return 见JDBC-executeQuery的返回说明。
//	 * @throws SQLException
//	 */
//	public ResultSet executeQuery(String sql, boolean autoCommit) throws SQLException {
//		Connection connection = getJDBCConnection();
//		connection.setAutoCommit(autoCommit);
//		Statement statement = connection.createStatement();
//		return statement.executeQuery(sql);
//	}
//
//}
