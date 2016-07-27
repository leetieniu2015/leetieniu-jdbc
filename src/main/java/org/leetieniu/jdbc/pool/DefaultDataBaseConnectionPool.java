package org.leetieniu.jdbc.pool;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.ListIterator;
import java.util.Set;

import org.leetieniu.jdbc.exception.JdbcTempleteException;

/**
 * 默认数据库连接池实现
 * @author leetieniu
 */
public class DefaultDataBaseConnectionPool implements DataBaseConnectionPool {
	
	/** 工作池中的连接 */
	private final LinkedList<Connection> workingPool = new LinkedList<Connection>();
	/** 初始化后的连接池*/
	private final LinkedList<Connection> stashPool = new LinkedList<Connection>();
	
	/** 连接池最大限制数 */
	private static final int MAX_POOL_SIZE = 100;
	/** 连接池默认的数量 */
	private static final int DEFAULT_POOL_SIZE = 20;
	/** 连接池最小的数量*/
	private static final int MIN_POOL_SIZE = 1;
	
	/** 存放已经加载过的驱动 */
	private static final Set<String> DRIVERS = new HashSet<String>();
	
	/** 连接 */
	private String url ;
	/** 用户名 */
	private String userName; 
	/** 密码 */
	private String password;
	/** 连接数 */
	private int initialSize;

	/**
	 * 构造函数, 默认连接池个数
	 * @param driverClassName 驱动名称
	 * @param url 数据库地址
	 * @param userName 用户名
	 * @param password 密码
	 */
	public DefaultDataBaseConnectionPool(String driverClassName, String url, 
			String userName, String password) {
		this(DEFAULT_POOL_SIZE, driverClassName, url, userName, password);
	}
	
	/**
	 * 构造函数
	 * @param initialSize 连接池个数
	 * @param driverClassName 驱动名称
	 * @param url 数据库地址
	 * @param userName 用户名
	 * @param password 密码
	 */
	public DefaultDataBaseConnectionPool(int initialSize, String driverClassName, String url, 
			String userName, String password) {
		
		this.initialSize = initialSize > MAX_POOL_SIZE ? 
					MAX_POOL_SIZE : initialSize < MIN_POOL_SIZE ? 
					MIN_POOL_SIZE : initialSize;
		this.url = url;
		this.userName = userName;
		this.password = password;
		
		if(!DRIVERS.contains(driverClassName)) {
			synchronized (DRIVERS) {
				if(!DRIVERS.contains(driverClassName)) {
					try {
						Class.forName(driverClassName);
						DRIVERS.add(driverClassName);
					} catch (ClassNotFoundException ex) {
						throw new JdbcTempleteException(ex);
					}
				}
			}
		}
		
		init();
	}
	
	/**
	 * 初始化连接
	 */
	private void init() {
		for(int i = 0; i < initialSize; i ++) {
			try {
				final Connection conn = createNewConnection();
				stashPool.addLast(conn);
				workingPool.addLast(conn);
			} catch (SQLException ex) {
				shutdown();
				throw new JdbcTempleteException(ex);
			}
		}
	}
	
	@Override
	public void releaseConnection(Connection conn) {
		if(conn != null) {
			synchronized (workingPool) {
				try {
					// 释放事务
					if (!conn.getAutoCommit()) {
						conn.setAutoCommit(true);
				    }
				} catch (SQLException ex) {
					throw new JdbcTempleteException(ex);
				} 
				
				if(!stashPool.contains(conn)) {
					stashPool.addLast(conn);
				}
				
				// 连接释放后需要进行统治, 这样其他消费者能够感知到连接池中已经归还了一个链接
				workingPool.addLast(conn);
				workingPool.notifyAll();
			}
		}
	}
	
	@Override
	public Connection fecthConnection(long mills) throws InterruptedException {
		synchronized (workingPool) {
			if(mills <= 0) {
				while(workingPool.isEmpty()) {
					workingPool.wait();
				}
				return workingPool.removeFirst();
			} else {
				long future = System.currentTimeMillis() + mills;
				long remaining = mills;
				while(workingPool.isEmpty() && remaining > 0) {
					workingPool.wait(remaining);
					remaining = future - System.currentTimeMillis();
				}
				Connection result = null;
				if(!workingPool.isEmpty()) {
					result = workingPool.removeFirst();
				}
				return result;
			}
		}
	}
	
	/**
	 * 创建连接
	 * @return Connection
	 * @throws SQLException
	 */
	@Override
	public Connection createNewConnection() throws SQLException {
		return DriverManager.getConnection(this.url, this.userName, this.password);
	}
	
	@Override
	public void remove(Connection conn) {
		synchronized (workingPool) {
			try {
				conn.close();
			} catch (SQLException e) {} 
			stashPool.remove(conn);
			workingPool.remove(conn);
		}
	}
	
	@Override
	public void shutdown() {
		for(Connection conn : stashPool) {
			if(conn != null) {
				try {
					conn.close();
				} catch (SQLException e) {} 
			}
		}
		workingPool.clear();
		stashPool.clear();
	}
	
	@Override
	public int getPoolSize() {
		return workingPool.size();
	}
	
	@Override
	public void validAllConnection() {
		synchronized (workingPool) {
			
			final ListIterator<Connection> it = workingPool.listIterator();
			int expired = 0;
			while(it.hasNext()) {
				final Connection conn = it.next();
				try {
					if(!conn.isValid(10)) {
						it.remove();
						stashPool.remove(conn);
						++expired;
						conn.close();
					}
				} catch (SQLException ex) {
					ex.printStackTrace();
				}
			}
			
			for(int i = 0; i < expired; i++) {
				try {
					final Connection conn = createNewConnection();
					workingPool.addLast(conn);
					stashPool.addLast(conn);
				} catch (SQLException ex) {
					ex.printStackTrace();
				}
			}
			
			/*for(Connection conn : workingPool) {
				try {
					conn.isValid(10);
				} catch (SQLException ex) {
					ex.printStackTrace();
				}
			}*/
		}
	}
}
