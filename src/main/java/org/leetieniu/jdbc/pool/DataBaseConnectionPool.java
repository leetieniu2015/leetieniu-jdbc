package org.leetieniu.jdbc.pool;

import java.sql.Connection;

/**
 * @package org.leetieniu.jdbc.pool  
 * @author leetieniu
 * @description 数据库连接池接口
 * @date 2016年5月6日 下午7:16:20    
 * @version V1.0
 */
public interface DataBaseConnectionPool {
	
	/**
	 * @description 在mills 内获取连接,获取不到将会返回null
	 * @date 2016年5月4日 下午5:34:13  
	 * @param mills
	 * @return 
	 * @throws InterruptedException
	 */
	public Connection fecthConnection(long mills) throws InterruptedException;
	
	/**
	 * @description 释放连接  到连接池  
	 * @date 2016年5月4日 下午5:34:48  
	 * @param connection
	 */
	public void releaseConnection(Connection connection);
	
	/**
	 * @description 关闭连接池  
	 * @date 2016年5月4日 下午5:35:31
	 */
	public void shutdown();
	
	/**
	 * @description 获取等待线程的任务数量
	 * @date 2016年5月4日 下午5:35:39  
	 * @return
	 */
	public int getPoolSize();
}
