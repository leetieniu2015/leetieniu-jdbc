package org.leetieniu.jdbc.factory;

import java.util.HashMap;
import java.util.Map;

import javax.sql.DataSource;

import org.leetieniu.jdbc.config.DataBaseConfig;
import org.leetieniu.jdbc.datasouce.SimpleDataSource;

/**
 * @package org.leetieniu.jdbc.factory  
 * @author leetieniu
 * @description dataSource工厂类
 * @date 2016年5月28日 下午10:49:28    
 * @version V1.0
 */
public class DataSourceFactory {
	
	private final static Map<DataBaseConfig, DataSource> DATASOURCES = new HashMap<DataBaseConfig, DataSource>();
	
	/** 连接池默认的数量 */
	private static final int DEFAULT_POOL_SIZE = 20;
	/** 默认超时时间 */
	private static final int DEFUALT_TIMEOUT = 10;
	
	/**
	 * @see https://github.com/leetieniu2015/leetieniu-jdbc/tree/master
	 * @description 获取简单连接池
	 * @date 2016年5月28日 下午11:00:19  
	 * @param driverClassName 驱动名称
	 * @param url 数据库地址
	 * @param userName 用户名
	 * @param password 密码
	 * @return SimpleDataSource
	 */
	public static SimpleDataSource getSimpleDataSource( String driverClassName, String url, 
			String userName, String password) {
		return getSimpleDataSource(DEFAULT_POOL_SIZE, driverClassName, url, 
				userName, password, DEFUALT_TIMEOUT);
	}
	
	/**
	 * @see https://github.com/leetieniu2015/leetieniu-jdbc/tree/master
	 * @description 获取简单连接池
	 * @date 2016年5月28日 下午11:00:46  
	 * @param initialSize 连接池个数
	 * @param driverClassName 驱动名称
	 * @param url 数据库地址
	 * @param userName 用户名
	 * @param password 密码
	 * @param timeout 超时时间
	 * @return SimpleDataSource
	 */
	public static SimpleDataSource getSimpleDataSource(int initialSize, String driverClassName, String url, 
			String userName, String password, int timeout) {
		final DataBaseConfig config = new DataBaseConfig(driverClassName, url, userName, password);
		
		if(!DATASOURCES.containsKey(config)) {
			synchronized (DATASOURCES) {
				if(!DATASOURCES.containsKey(config)) {
					final SimpleDataSource dateSource = new SimpleDataSource(
							initialSize, driverClassName, url, userName, password, timeout);
					DATASOURCES.put(config, dateSource);
				}
			}
		}
		return (SimpleDataSource)DATASOURCES.get(config);
	}
	
}
