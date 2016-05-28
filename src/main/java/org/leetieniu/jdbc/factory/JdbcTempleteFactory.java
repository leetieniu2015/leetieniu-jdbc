package org.leetieniu.jdbc.factory;

import java.util.HashMap;
import java.util.Map;

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
	
	private final static Map<SimpleDataSource, JdbcTemplete> JDBCTEMPLETES = new HashMap<SimpleDataSource, JdbcTemplete>();
	
	/**
	 * @description 获取默认jdbc模板
	 * @date 2016年5月28日 下午11:09:54  
	 * @param simpleDataSource 简单数据源
	 * @return DefaultJdbcTemplete
	 */
	public static DefaultJdbcTemplete getDefaultJdbcTemplete(SimpleDataSource simpleDataSource) {
		
		if(!JDBCTEMPLETES.containsKey(simpleDataSource)) {
			synchronized (JDBCTEMPLETES) {
				if(!JDBCTEMPLETES.containsKey(simpleDataSource)) {
					final DefaultJdbcTemplete defaultJdbcTemplete = new DefaultJdbcTemplete(simpleDataSource);
					JDBCTEMPLETES.put(simpleDataSource, defaultJdbcTemplete);
				}
			}
		}
		return (DefaultJdbcTemplete)JDBCTEMPLETES.get(simpleDataSource);
	}
}
