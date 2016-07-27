package org.leetieniu.jdbc.templete;

import java.sql.Connection;
import java.util.List;

/**
 * Jdbc调用模板接口
 * @author leetieniu
 */
public interface JdbcTemplete {
	
	/**
	 * 获取指定对象 连接自己管理
	 * @param conn
	 * @param sql
	 * @param param 参数
	 * @param clazz 返回对象class
	 * @return T
	 */
	public <T> T queryForObject(Connection conn, String sql, Object[] param, Class<T> clazz);
	
	/**
	 * 获取指定对象
	 * @param sql
	 * @param param 参数
	 * @param clazz 返回对象class
	 * @return T
	 */
	public <T> T queryForObject(String sql, Object[] param, Class<T> clazz);
	
	/**
	 * 查询制定对象列表 连接自己管理
	 * @param conn
	 * @param sql
	 * @param param  参数
	 * @param clazz 返回对象class
	 * @return List<T>
	 */
	public <T> List<T> queryForList(Connection conn, String sql, Object[] param , Class<T> clazz) ;
	
	/**
	 * 查询制定对象列表
	 * @param sql
	 * @param param 参数
	 * @param clazz 返回对象class
	 * @return List<T>
	 */
	public <T> List<T> queryForList(String sql, Object[] param , Class<T> clazz) ;
	
	/**
	 * 插入, 连接自己管理
	 * @param conn 连接
	 * @param sql
	 * @param param 参数
	 * @return 大于0成功否则失败
	 */
	public int insert(Connection conn, String sql, Object[] param);
	
	/**
	 * 插入
	 * @param sql
	 * @param param 参数
	 * @return 大于0成功否则失败
	 */
	public int insert(String sql, Object[] param);
	
	/**
	 * 更新, 连接自己管理
	 * @param conn 连接
	 * @param sql
	 * @param param 参数
	 * @return 大于0成功否则失败
	 */
	public int update(Connection conn, String sql, Object[] param);
	
	/**
	 * 更新
	 * @param sql
	 * @param param 参数
	 * @return 大于0成功否则失败
	 */
	public int update(String sql, Object[] param);
	
	/**
	 * 删除, 连接自己管理
	 * @param conn 连接
	 * @param sql
	 * @param param 参数
	 * @return 大于0成功否则失败
	 */
	public int delete(Connection conn, String sql, Object[] param);
	
	/**
	 * 删除
	 * @param sql
	 * @param param 参数
	 * @return 大于0成功否则失败
	 */
	public int delete(String sql, Object[] param);
	
	/**
	 * 预编译的sql , 同一条sql 不同参数的批量更新
	 * @param sql 同一条sql
	 * @param params 不同参数 列表 
	 * @return 每条sql 成功执行数
	 */
	public int[] executeBatch(String sql, List<Object[]> params);
	
	/**
	 * 预编译的sql , 同一条sql 不同参数的批量更新
	 * @param conn
	 * @param sql 同一条sql
	 * @param params 不同参数 列表 
	 * @return 每条sql 成功执行数
	 */
	public int[] executeBatch(Connection conn, String sql, List<Object[]> params);
	
	/**
	 * 包含批中每个命令的一个元素的更新计数所组成的数组。数组的元素根据将命令添加到批中的顺序排序。
	 * @param sqls 批量的sql
	 * @return 每条sql 成功执行数
	 */
	public int[] executeBatch(List<String> sqls);
	
	/**
	 * 包含批中每个命令的一个元素的更新计数所组成的数组。数组的元素根据将命令添加到批中的顺序排序。
	 * @param conn
	 * @param sqls 批量的sql
	 * @return 每条sql 成功执行数
	 */
	public int[] executeBatch(Connection conn, List<String> sqls);
	
}
