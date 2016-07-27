package org.leetieniu.jdbc.datasouce;

import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Logger;

import javax.sql.DataSource;

import org.leetieniu.jdbc.exception.JdbcTempleteException;
import org.leetieniu.jdbc.pool.DataBaseConnectionPool;
import org.leetieniu.jdbc.pool.DefaultDataBaseConnectionPool;


/**
 * 简单数据源实现
 * @author leetieniu
 */
public class SimpleDataSource implements DataSource {
	
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
	
	/**
	 * 构造函数
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
	
	@Override
	public Connection getConnection() throws SQLException {
		Connection conn = null;
		try {
			conn = pool.fecthConnection(timeout);
			if(testOnBorrow && !conn.isValid((int)(timeout / 1000))) {
				pool.remove(conn);
				conn = pool.createNewConnection();
			}
		} catch (InterruptedException ex) {
			throw new JdbcTempleteException(ex);
		} catch (SQLException ex) {
			throw new JdbcTempleteException(ex);
		} 
		return conn;
	}
	
	/**
	 * 释放连接 到连接池 
	 * @param connection 连接
	 */
	public void releaseConnection(Connection conn) {
		try {
			if(testOnReturn && !conn.isValid((int)(timeout / 1000))) {
				pool.remove(conn);
				conn = pool.createNewConnection();
			}
			pool.releaseConnection(conn);
		} catch (SQLException ex) {
			throw new JdbcTempleteException(ex);
		} 
	}
	
	private boolean testOnBorrow = true;
	
	/**
	 * 默认true.指明在从池中租借对象时是否要进行校验, 如果对象校验失败, 则remove之后新建一个。
	 * @param testOnBorrow
	 */
	public void setTestOnBorrow(boolean testOnBorrow) {
		this.testOnBorrow = testOnBorrow;
	}
	
	private boolean testOnReturn = false; 
	
	/**
	 * 默认false.指明在将对象归还给连接池前是否需要校验, 如果对象校验失败, 则remove之后新建一个。
	 * @param testOnReturn
	 */
	public void setTestOnReturn(boolean testOnReturn) {
		this.testOnReturn = testOnReturn;
	}
	
	/**
	 * 校验线程运行时的休眠毫秒数, 大于0执行
	 * @param mills 毫秒
	 */
	public void setTimeBetweenValidMillis(long mills) {
		if(mills > 0) {
			startValidTimer(mills, mills);
		}
	}
	
	/** 定时任务执行器 */
	private Timer timer;
	
	/**
	 * 校验连接 任务 执行
	 * @param delay 延迟时间
	 * @param period 执行周期
	 */
	private void startValidTimer(long delay, long period) {
		timer = new Timer("Connection Valid Timer", true);
		timer.scheduleAtFixedRate(new TimerTask() {
			@Override
			public void run() {
				pool.validAllConnection();
			}
		}, delay, period);
	}
	
	/**
	 * 校验连接 任务 取消
	 */
	public void cancelValidTimer() {
		timer.cancel();
	}
	
	@Override
	public Connection getConnection(String username, String password)
			throws SQLException {
		return null;
	}
	
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
	
	public String getPassword() {
		return password;
	}

	public String getUserName() {
		return userName;
	}

	public String getDriverClassName() {
		return driverClassName;
	}

	public String getUrl() {
		return url;
	}

	public int getInitialSize() {
		return initialSize;
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
