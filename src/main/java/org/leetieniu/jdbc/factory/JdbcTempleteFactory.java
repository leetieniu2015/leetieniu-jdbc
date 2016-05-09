package org.leetieniu.jdbc.factory;

import java.util.HashMap;
import java.util.Map;

import org.leetieniu.jdbc.config.DataBaseConfig;
import org.leetieniu.jdbc.datasouce.SimpleDataSource;
import org.leetieniu.jdbc.templete.DefaultJdbcTemplete;
import org.leetieniu.jdbc.templete.JdbcTemplete;


/**
 * @see https://github.com/leetieniu2015/leetieniu-jdbc/tree/master
 * @package org.leetieniu.jdbc.factory  
 * @author leetieniu
 * @description JdbcTemplete工厂类
 * @date 2016年5月6日 下午7:16:37    
 * @version V1.0
 */
public class JdbcTempleteFactory {
	
	private final static Map<DataBaseConfig, JdbcTemplete> JDBCTEMPLETES = new HashMap<DataBaseConfig, JdbcTemplete>();
	
	/** 连接池默认的数量 */
	private static final int DEFAULT_POOL_SIZE = 5;
	/** 默认超时时间 */
	private static final int DEFUALT_TIMEOUT = 10;
	
	/**
	 * @see https://github.com/leetieniu2015/leetieniu-jdbc/tree/master
	 * @description 获取默认JdbcTemplete
	 * @date 2016年5月7日 下午10:03:53  
	 * @param driverClassName 驱动名称
	 * @param url 数据库地址
	 * @param userName 用户名
	 * @param password 密码
	 * @return DefaultJdbcTemplete
	 */
	public static DefaultJdbcTemplete getDefaultJdbcTemplete(String driverClassName, String url, 
			String userName, String password) {
		return getDefaultJdbcTemplete(DEFAULT_POOL_SIZE, driverClassName, url, 
				userName, password, DEFUALT_TIMEOUT);
	}
	
	/**
	 * @see https://github.com/leetieniu2015/leetieniu-jdbc/tree/master
	 * @description 获取默认JdbcTemplete
	 * @date 2016年5月7日 下午10:08:33  
	 * @param initialSize 连接池个数
	 * @param driverClassName 驱动名称
	 * @param url 数据库地址
	 * @param userName 用户名
	 * @param password 密码
	 * @param timeout 超时时间
	 * @return DefaultJdbcTemplete
	 */
	public static DefaultJdbcTemplete getDefaultJdbcTemplete(int initialSize, String driverClassName, String url, 
			String userName, String password, int timeout) {
		final DataBaseConfig config = new DataBaseConfig(driverClassName, url, userName, password);
		
		if(!JDBCTEMPLETES.containsKey(config)) {
			synchronized (JDBCTEMPLETES) {
				if(!JDBCTEMPLETES.containsKey(config)) {
					final SimpleDataSource dateSource = new SimpleDataSource(
							initialSize, driverClassName, url, userName, password, timeout);
					final DefaultJdbcTemplete defaultJdbcTemplete = new DefaultJdbcTemplete(dateSource);
					JDBCTEMPLETES.put(config, defaultJdbcTemplete);
				}
			}
		}
		return (DefaultJdbcTemplete)JDBCTEMPLETES.get(config);
	}
	
}
