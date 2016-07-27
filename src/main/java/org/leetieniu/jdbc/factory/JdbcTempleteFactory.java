package org.leetieniu.jdbc.factory;

import java.util.HashMap;
import java.util.Map;

import org.leetieniu.jdbc.datasouce.SimpleDataSource;
import org.leetieniu.jdbc.templete.DefaultJdbcTemplete;
import org.leetieniu.jdbc.templete.JdbcTemplete;

/**
 * JdbcTemplete工厂类
 * @see https://github.com/leetieniu2015/leetieniu-jdbc/tree/master
 * @author leetieniu
 */
public class JdbcTempleteFactory {
	
	private final static Map<SimpleDataSource, JdbcTemplete> JDBCTEMPLETES = new HashMap<SimpleDataSource, JdbcTemplete>();
	
	/**
	 * 获取默认jdbc模板
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
