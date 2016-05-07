package org.leetieniu.jdbc.templete;

import java.util.List;

/**
 * @package org.leetieniu.jdbc.templete  
 * @author leetieniu
 * @description Jdbc调用模板接口
 * @date 2016年5月6日 下午7:15:07    
 * @version V1.0
 */
public interface JdbcTemplete {
	
	/**
	 * @description 获取指定对象
	 * @date 2016年5月4日 下午5:52:56  
	 * @param sql
	 * @param param 参数
	 * @param clazz 返回对象class
	 * @return T
	 */
	public <T> T queryForObject(String sql, Object[] param, Class<T> clazz);
	
	/**
	 * @description 查询制定对象列表
	 * @date 2016年5月4日 下午8:50:16  
	 * @param sql
	 * @param param 参数
	 * @param clazz 返回对象class
	 * @return T
	 */
	public <T> List<T> queryForList(String sql, Object[] param , Class<T> clazz) ;
	
	/**
	 * @description 插入
	 * @date 2016年5月7日 下午10:17:32  
	 * @param sql
	 * @param param 参数
	 * @return 大于0成功否则失败
	 */
	public int insert(String sql, Object[] param);
	
	/**
	 * @description 更新
	 * @date 2016年5月7日 下午10:17:40  
	 * @param sql
	 * @param param 参数
	 * @return 大于0成功否则失败
	 */
	public int update(String sql, Object[] param);
	
	/**
	 * @description 删除
	 * @date 2016年5月7日 下午10:17:44  
	 * @param sql
	 * @param param 参数
	 * @return 大于0成功否则失败
	 */
	public int delete(String sql, Object[] param);
	
}
