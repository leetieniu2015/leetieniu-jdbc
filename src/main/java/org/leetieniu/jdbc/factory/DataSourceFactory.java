package org.leetieniu.jdbc.factory;

import java.util.HashMap;
import java.util.Map;

import javax.sql.DataSource;

import org.leetieniu.jdbc.config.DataBaseConfig;
import org.leetieniu.jdbc.datasouce.SimpleDataSource;


/**
 * dataSource工厂类
 * @author leetieniu
 * @see https://github.com/leetieniu2015/leetieniu-jdbc/tree/master
 */
public class DataSourceFactory {
	
	private final static Map<DataBaseConfig, DataSource> DATASOURCES = new HashMap<DataBaseConfig, DataSource>();
	
	/**
	 * 获取简单连接池
	 * @param initialSize 连接池个数
	 * @param driverClassName 驱动名称
	 * @param url 数据库地址
	 * @param userName 用户名
	 * @param password 密码
	 * @param timeout 超时时间
	 * @return SimpleDataSource
	 */
	public static SimpleDataSource getSimpleDataSource(int initialSize, String driverClassName, String url, 
			String userName, String password, int timeout, int validateTime) {
		final DataBaseConfig config = new DataBaseConfig(driverClassName, url, userName, password);
		
		if(!DATASOURCES.containsKey(config)) {
			synchronized (DATASOURCES) {
				if(!DATASOURCES.containsKey(config)) {
					final SimpleDataSource dateSource = new SimpleDataSource(
							initialSize, driverClassName, url, userName, password, timeout);
					//dateSource.setTestOnBorrow(true);
					//dateSource.setTestOnReturn(true);
					dateSource.setTimeBetweenValidMillis(validateTime);
					DATASOURCES.put(config, dateSource);
				}
			}
		}
		return (SimpleDataSource)DATASOURCES.get(config);
	}
	
}
