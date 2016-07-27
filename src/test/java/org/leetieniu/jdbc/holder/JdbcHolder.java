package org.leetieniu.jdbc.holder;

import org.leetieniu.jdbc.datasouce.SimpleDataSource;
import org.leetieniu.jdbc.factory.DataSourceFactory;
import org.leetieniu.jdbc.factory.JdbcTempleteFactory;
import org.leetieniu.jdbc.templete.DefaultJdbcTemplete;

/**
 * Jdbc持有者
 * @author leetieniu
 */
public class JdbcHolder {
	
	private JdbcHolder() {}
	
	/** 连接池默认的数量 */
	private static final int DEFAULT_POOL_SIZE = 30;
	/** 默认超时时间 毫秒 */
	private static final int DEFUALT_TIMEOUT = 10 * 1000;
	
	/** 执行周期 6小时 */
	private static final int DEFUALT_VALIMILLS = 6 * 60 * 60 * 1000;
	
	/**
	 * 获取简单数据源
	 * @return SimpleDataSource
	 */
	public static SimpleDataSource getSimpleDataSource() {
		String driverClassName = null;
		String url = null;
		String userName = null;
		String password = null;
		final SimpleDataSource simpleDataSource = DataSourceFactory.
				getSimpleDataSource(DEFAULT_POOL_SIZE, driverClassName, url, 
						userName, password, DEFUALT_TIMEOUT, DEFUALT_VALIMILLS);
		return simpleDataSource;
	}
	
	/**
	 * 获取DefaultJdbcTemplete
	 * @return DefaultJdbcTemplete
	 */
	public static DefaultJdbcTemplete getDefaultJdbcTemplete() {
		return JdbcTempleteFactory.getDefaultJdbcTemplete(getSimpleDataSource());
	}
	
}
