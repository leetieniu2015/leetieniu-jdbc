package org.leetieniu.jdbc.pool;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;

import org.leetieniu.jdbc.exception.JdbcTempleteException;

/**
 * @package org.leetieniu.jdbc.pool  
 * @author leetieniu
 * @description 默认数据库连接池实现
 * @date 2016年5月6日 下午7:16:01    
 * @version V1.0
 */
public class DefaultDataBaseConnectionPool implements DataBaseConnectionPool {
	
	/** 工作池中的连接 */
	private final LinkedList<Connection> workingPool = new LinkedList<Connection>();
	/** 初始化后的连接池*/
	private final LinkedList<Connection> stashPool = new LinkedList<Connection>();
	
	/** 连接池最大限制数 */
	private static final int MAX_POOL_SIZE = 20;
	/** 连接池默认的数量 */
	private static final int DEFAULT_POOL_SIZE = 5;
	/** 连接池最小的数量*/
	private static final int MIN_POOL_SIZE = 1;
	
	public DefaultDataBaseConnectionPool(String driverClassName, String url, 
			String userName, String password) {
		this(DEFAULT_POOL_SIZE, driverClassName, url, userName, password);
	}
	
	/** 存放已经加载过的驱动 */
	private static final Set<String> DRIVERS = new HashSet<String>();
	
	/**
	 * @param initialSize 连接池个数
	 * @param driverClassName 驱动名称
	 * @param url 数据库地址
	 * @param userName 用户名
	 * @param password 密码
	 */
	public DefaultDataBaseConnectionPool(int initialSize, String driverClassName, String url, 
			String userName, String password) {
		
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
		
		initialSize = initialSize > MAX_POOL_SIZE ? MAX_POOL_SIZE : initialSize < MIN_POOL_SIZE ? MIN_POOL_SIZE : initialSize;
		
		for(int i = 0; i < initialSize; i ++) {
			try {
				workingPool.addLast(DriverManager.getConnection(url, userName, password));
			} catch (SQLException ex) {
				throw new JdbcTempleteException(ex);
			}
		}
		stashPool.addAll(workingPool);
	}
	
	@Override
	public void releaseConnection(Connection connection) {
		if(connection != null) {
			synchronized (workingPool) {
				// 连接释放后需要进行统治, 这样其他消费者能够感知到连接池中已经归还了一个链接
				workingPool.addLast(connection);
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

	@Override
	public void shutdown() {
		for(Connection connection : stashPool) {
			if(connection != null) {
				try {
					connection.close();
				} catch (SQLException e) {
					
				}
			}
		}
		workingPool.clear();
		stashPool.clear();
		//CONNECTIONS.clear();
	}

	@Override
	public int getPoolSize() {
		return workingPool.size();
	}
}
