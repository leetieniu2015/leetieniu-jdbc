package org.leetieniu.jdbc.pool;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * 数据库连接池接口
 * @author leetieniu
 */
public interface DataBaseConnectionPool {
	
	/**
	 * 在mills 内获取连接,获取不到将会返回null
	 * @param mills 超时时间
	 * @return 连接
	 * @throws InterruptedException 超时时抛出异常
	 */
	public Connection fecthConnection(long mills) throws InterruptedException;
	
	/**
	 * 释放连接  到连接池  
	 * @param connection 连接
	 */
	public void releaseConnection(Connection connection);
	
	/**
	 * 建立一个新连接
	 * @return Connection 连接
	 * @throws SQLException 创建错误时抛出异常
	 */
	public Connection createNewConnection() throws SQLException;
	
	/**
	 * 关闭连接池  
	 */
	public void shutdown();
	
	/**
	 * 丢弃连接
	 * @param connection 连接
	 */
	public void remove(Connection connection);
	
	/**
	 * 获取等待线程的任务数量
	 * @return 连接
	 */
	public int getPoolSize();
	
	/**
	 * 校验所有连接
	 */
	public void validAllConnection();
}
