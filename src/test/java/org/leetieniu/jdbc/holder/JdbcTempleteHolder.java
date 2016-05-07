package org.leetieniu.jdbc.holder;

import org.leetieniu.jdbc.factory.JdbcTempleteFactory;
import org.leetieniu.jdbc.templete.DefaultJdbcTemplete;

/**
 * @package org.leetieniu.jdbc.holder  
 * @author leetieniu
 * @description JdbcTemplete 拥有者
 * @date 2016年5月7日 下午10:20:19    
 * @version V1.0
 */
public class JdbcTempleteHolder {
	
	public static DefaultJdbcTemplete getDefaultJdbcTemplete() {
		
		final String driverClassName = "com.mysql.jdbc.Driver";
		//final String url = "jdbc:mysql://115.28.219.63/qdm160915374_db?characterEncoding=utf8&useSSL=false";
		final String url = "jdbc:mysql://115.28.219.63/qdm160915374_db?characterEncoding=utf-8&useSSL=false";
		final String userName = "qdm160915374";
		final String password = "19910325";
		return JdbcTempleteFactory.getDefaultJdbcTemplete(1,
				driverClassName, url, userName, password, 10);
	}
}
