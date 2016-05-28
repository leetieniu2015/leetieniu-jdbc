package org.leetieniu.jdbc.datasouce;

import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.logging.Logger;

import javax.sql.DataSource;

import org.leetieniu.jdbc.exception.JdbcTempleteException;
import org.leetieniu.jdbc.pool.DataBaseConnectionPool;
import org.leetieniu.jdbc.pool.DefaultDataBaseConnectionPool;

/**
 * @package org.leetieniu.jdbc.datasouce  
 * @author leetieniu
 * @description 简单数据源实现
 * @date 2016年5月6日 下午7:10:19    
 * @version V1.0
 */
public class SimpleDataSource implements DataSource {
	
	/**
	 * @param initialSize 连接池个数
	 * @param driverClassName 驱动名称
	 * @param url 数据库地址
	 * @param userName 用户名
	 * @param password 密码
	 * @param timeout 超时时间
	 */
	public SimpleDataSource(int initialSize, String driverClassName, String url, 
			String userName, String password, int timeout) {
		this.initialSize = initialSize;
		this.driverClassName = driverClassName;
		this.url = url;
		this.userName = userName;
		this.password = password;
		this.timeout = timeout;
		this.pool = new DefaultDataBaseConnectionPool(
				initialSize, driverClassName, url, userName, password);
	}
	
	/** 连接池个数*/
	private int initialSize;
	/** 驱动名称  */
	private String driverClassName;
	/** 数据库地址 */
	private String url;
	/** 用户名 */
	private String userName;
	/** 密码 */
	private String password;
	/** 超时时间 */
	private int timeout;
	/** 连接池 */
	private DataBaseConnectionPool pool;
	
	@Override
	public PrintWriter getLogWriter() throws SQLException {
		throw new SQLException("SimepleDataSource is no logger");
	}

	@Override
	public void setLogWriter(PrintWriter out) throws SQLException {
		throw new SQLException("SimepleDataSource is no logger");
	}

	@Override
	public void setLoginTimeout(int seconds) throws SQLException {
		this.timeout = seconds;
	}

	@Override
	public int getLoginTimeout() throws SQLException {
		return timeout;
	}

	public Logger getParentLogger() throws SQLFeatureNotSupportedException {
		throw new SQLFeatureNotSupportedException("SimepleDataSource is no parent logger");
	}

	@Override
	public <T> T unwrap(Class<T> iface) throws SQLException {
		throw new SQLException("SimepleDataSource is not a wrapper.");
	}

	@Override
	public boolean isWrapperFor(Class<?> iface) throws SQLException {
		return false;
	}

	@Override
	public Connection getConnection() throws SQLException {
		Connection conn = null;
		try {
			conn = pool.fecthConnection(timeout);
		} catch (InterruptedException e) {
			throw new JdbcTempleteException(e);
		}
		return conn;
	}

	@Override
	public Connection getConnection(String username, String password)
			throws SQLException {
		return null;
	}
	
	/**
	 * @description 释放连接 到连接池 
	 * @date 2016年5月28日 下午11:27:14  
	 * @param connection 连接
	 */
	public void releaseConnection(Connection connection) {
		pool.releaseConnection(connection);
	}
	
	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getDriverClassName() {
		return driverClassName;
	}

	public void setDriverClassName(String driverClassName) {
		this.driverClassName = driverClassName;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public int getInitialSize() {
		return initialSize;
	}

	public void setInitialSize(int initialSize) {
		this.initialSize = initialSize;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((driverClassName == null) ? 0 : driverClassName.hashCode());
		result = prime * result
				+ ((password == null) ? 0 : password.hashCode());
		result = prime * result + ((url == null) ? 0 : url.hashCode());
		result = prime * result
				+ ((userName == null) ? 0 : userName.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		SimpleDataSource other = (SimpleDataSource) obj;
		if (driverClassName == null) {
			if (other.driverClassName != null)
				return false;
		} else if (!driverClassName.equals(other.driverClassName))
			return false;
		if (password == null) {
			if (other.password != null)
				return false;
		} else if (!password.equals(other.password))
			return false;
		if (url == null) {
			if (other.url != null)
				return false;
		} else if (!url.equals(other.url))
			return false;
		if (userName == null) {
			if (other.userName != null)
				return false;
		} else if (!userName.equals(other.userName))
			return false;
		return true;
	}
}
