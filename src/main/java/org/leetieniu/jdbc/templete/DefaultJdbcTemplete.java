package org.leetieniu.jdbc.templete;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.leetieniu.jdbc.datasouce.SimpleDataSource;
import org.leetieniu.jdbc.exception.JdbcTempleteException;

/**
 * @package org.leetieniu.jdbc.templete  
 * @author leetieniu
 * @description 默认JdbcTemplete实现
 * @date 2016年5月6日 下午7:15:39    
 * @version V1.0
 */
public class DefaultJdbcTemplete implements JdbcTemplete {
	
	/**
	 * @param dataSource 数据源
	 */
	public DefaultJdbcTemplete(SimpleDataSource dataSource) {
		this.dataSource = dataSource;
	}
	
	/** 数据源 */
	private SimpleDataSource dataSource;
	
	public SimpleDataSource getDataSource() {
		return dataSource;
	}

	public void setDataSource(SimpleDataSource dataSource) {
		this.dataSource = dataSource;
	}

	@Override
	public <T> T queryForObject(String sql, Object[] param , final Class<T> clazz) {
		T t = excute(sql, param ,  new PreparedStatementJdbcHandler<T> () {
			@Override
			public T handle(PreparedStatement pstmt) throws SQLException,
					InstantiationException, IllegalAccessException {
				T t = query(pstmt, clazz, new ResultSetHandler<T, T> () {
					@Override
					public T handle(ResultSet resultSet, Class<T> clazz)
							throws SQLException, InstantiationException,
							IllegalAccessException {
						return getObject(resultSet, clazz);
					}
				});
				return t;
			}
		});
		return t;
	}
	
	@Override
	public <T> List<T> queryForList(String sql, Object[] param , final Class<T> clazz) {
		List<T> list = excute(sql, param , new PreparedStatementJdbcHandler<List<T>> () {

			@Override
			public List<T> handle(PreparedStatement pstmt) throws SQLException,
					InstantiationException, IllegalAccessException {
				List<T> list = query(pstmt, clazz, new ResultSetHandler<List<T>, T> () {
					@Override
					public List<T> handle(ResultSet resultSet, Class<T> clazz)
							throws SQLException, InstantiationException,
							IllegalAccessException {
						return getObjectList(resultSet, clazz);
					}
				});
				return list;
			}
		});
		return list;
	}
	
	/**
	 * @description 查询模板
	 * @date 2016年5月5日 上午10:30:55  
	 * @param pstmt
	 * @param clazz
	 * @param hander
	 * @return
	 * @throws SQLException
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 */
	private <T,D> D query(PreparedStatement pstmt, Class<T> clazz, ResultSetHandler<D, T> hander) throws SQLException, InstantiationException, IllegalAccessException {
		ResultSet resultSet = null;
		D d = null;
		try {
			resultSet = pstmt.executeQuery();
			d = hander.handle(resultSet, clazz);
		} catch (SQLException ex) {
			throw ex;
		} catch (InstantiationException ex) {
			throw ex;
		} catch (IllegalAccessException ex) {
			throw ex;
		}finally {
			try {
				if (resultSet != null) {
					resultSet.close();
					resultSet = null;
				}
			} catch (SQLException ex) {
				throw new JdbcTempleteException(ex);
			}
		}
		return d;
	}
	
	/**
	 * @description 执行模板
	 * @date 2016年5月5日 上午10:06:07  
	 * @param sql 
	 * @param param 参数
	 * @param clazz 类型
	 * @param handler 行为
	 * @return D
	 */
	private <T,D> D excute(String sql, Object[] param , PreparedStatementJdbcHandler<D> handler) {
		
		Connection conn = null;
		PreparedStatement pstmt = null;
		D d = null;
		try {
			conn = dataSource.getConnection();
			pstmt = conn.prepareStatement(sql);
			if(null != param) {
				for (int i = 0; i < param.length; i++) {
					pstmt.setObject(i + 1, param[i]);
			    }
			}
			
			d = handler.handle(pstmt);
			/*resultSet = pstmt.executeQuery();
			d = handler.handle(resultSet, clazz);*/
			
		} catch (SQLException ex) {
			throw new JdbcTempleteException(ex);
		} catch (InstantiationException ex) {
			throw new JdbcTempleteException(ex);
		} catch (IllegalAccessException ex) {
			throw new JdbcTempleteException(ex);
		} catch (Exception ex) {
			throw new JdbcTempleteException(ex);
		} finally {
			try {
				if (pstmt != null) {
					pstmt.close();
					pstmt = null;
				}
			} catch (SQLException ex) {
				throw new JdbcTempleteException(ex);
			}
			dataSource.getPool().releaseConnection(conn);
		}
		return d;
	}
	
	private final static int FIRST = 0;
	
	/**
	 * @description 获取结果集对象
	 * @date 2016年5月4日 下午8:18:51  
	 * @param resultSet
	 * @param clazz
	 * @return T
	 * @throws SQLException
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 */
	private <T> T getObject(ResultSet resultSet, Class<T> clazz) throws SQLException, InstantiationException, IllegalAccessException  {
		if(resultSet.getRow() > 1) {
			throw new JdbcTempleteException("结果集大于1个");
		}
		List<T> list = getObjectList(resultSet, clazz);
		if(list.size() < 1) {
			return null;
		}
		return list.get(FIRST);
	}
	
	/**
	 * @description 获取结果集的对象列表
	 * @date 2016年5月4日 下午8:18:27  
	 * @param resultSet
	 * @param clazz
	 * @return List<T>
	 * @throws SQLException
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 */
	private <T> List<T> getObjectList(ResultSet resultSet, Class<T> clazz) 
			throws SQLException, InstantiationException, IllegalAccessException {
		final List<T> result = new ArrayList<T>();
		
		final ResultSetMetaData resultSetMetaData = resultSet.getMetaData();
		int columnCount = resultSetMetaData.getColumnCount();
		/*Map<String, Integer> map = new HashMap<String, Integer>();
		
		for(int i = 1; i <= columnCount; i ++ ) {
			map.put(resultSetMetaData.getColumnName(i), 
					resultSetMetaData.getColumnType(i));
		}*/
		
		Field[] fields = clazz.getDeclaredFields();
		
		if (resultSet != null) {
			while (resultSet.next()) {
				T t = clazz.newInstance();
				for(Field field : fields) {
					field.setAccessible(true);
					for(int j = 1; j <= columnCount; j ++ ) {
						// 如果 field 名称等与 column 名称
						if(field.getName().equalsIgnoreCase(resultSetMetaData.getColumnName(j))) {
							
							setFieldValue(t, field, resultSetMetaData.getColumnType(j), 
									resultSet , j);
							break;
						}
					}
				}
				result.add(t);
			}
		}
		return result;
	}
	
	/**
	 * @package com.creditease.system.util.jdbc  
	 * @author leetieniu
	 * @description PreparedStatementJdbcHandler
	 * @date 2016年5月6日 上午9:57:42    
	 * @version V1.0 
	 * @param <D> 返回值
	 */
	interface PreparedStatementJdbcHandler<D> {
		
		/**
		 * @description 模板方法
		 * @date 2016年5月5日 上午10:03:26  
		 * @param pstmt 
		 * @return
		 * @throws SQLException
		 * @throws InstantiationException
		 * @throws IllegalAccessException
		 */
		public D handle(PreparedStatement pstmt) throws SQLException, InstantiationException, IllegalAccessException;
	}
	
	/**
	 * @package com.creditease.system.app.dao  
	 * @author leetieniu
	 * @description ResultSetHandler
	 * @date 2016年5月5日 上午10:16:18    
	 * @version V1.0 
	 * @param <D> 返回值
	 * @param <T> 参数类型
	 */
	interface ResultSetHandler<D, T> {
		
		/**
		 * @description 模板方法
		 * @date 2016年5月5日 上午10:16:07  
		 * @param resultSet
		 * @param clazz
		 * @return
		 * @throws SQLException
		 * @throws InstantiationException
		 * @throws IllegalAccessException
		 */
		public D handle(ResultSet resultSet, Class<T> clazz) throws SQLException, InstantiationException, IllegalAccessException;
	}
	
	private static final String SIMPLENAME_INTEGER = "Integer";
	private static final String SIMPLENAME_LONG = "Long";
	private static final String SIMPLENAME_STRING = "String";
	private static final String SIMPLENAME_DATE = "Date";
	private static final String SIMPLENAME_DOUBLE = "Double";
	
	/**
	 * @description 给
	 * @date 2016年5月5日 上午10:04:03  
	 * @param obj 实例
	 * @param field 字段
	 * @param sqlType 对应的java.sql.Types 的值
	 * @param resultSet 结果集
	 * @param columnIndex 列数
	 * @throws IllegalArgumentException
	 * @throws IllegalAccessException
	 * @throws SQLException
	 * @throws InstantiationException
	 */
	private void setFieldValue(Object obj, Field field, int sqlType, ResultSet resultSet, int columnIndex) 
			throws IllegalArgumentException, IllegalAccessException, SQLException, InstantiationException {
		
		final String simpleName = field.getType().getSimpleName();
		
		if(resultSet.getObject(columnIndex) != null) {
			if(SIMPLENAME_INTEGER.equals(simpleName)) {
				field.set(obj, resultSet.getInt(columnIndex));
			} else if(SIMPLENAME_LONG.equals(simpleName)) {
				field.set(obj, resultSet.getLong(columnIndex));
			} else if(SIMPLENAME_STRING.equals(simpleName)) {
				field.set(obj, resultSet.getString(columnIndex));
			} else if(SIMPLENAME_DATE.equals(simpleName)) {
				//field.set(obj, new Date(resultSet.getTimestamp(columnIndex).getTime()));
				field.set(obj, resultSet.getTimestamp(columnIndex));
			} else if(SIMPLENAME_DOUBLE.equals(simpleName)) {
				field.set(obj, resultSet.getDouble(columnIndex));
			}
		}
		
		/*if(sqlType == Types.CHAR ||
				sqlType == Types.VARCHAR ||
				sqlType == Types.LONGVARCHAR
			) {
			field.set(obj, resultSet.getString(columnIndex));
		} else if(sqlType == Types.NUMERIC ||
				sqlType == Types.DECIMAL) {
			if(fieldType instanceof Integer) {
				
			}
			field.set(obj, resultSet.getBigDecimal(columnIndex));
		} */
		
	}
	
	/**
	 * @description 执行更新
	 * @date 2016年5月7日 下午10:16:55  
	 * @param sql
	 * @param param 参数
	 * @return 大于0成功否则失败
	 */
	private int exceuteUpdate(String sql, Object[] param) {
		return excute(sql, param , new PreparedStatementJdbcHandler<Integer> () {
			@Override
			public Integer handle(PreparedStatement pstmt) throws SQLException,
					InstantiationException, IllegalAccessException {
				return pstmt.executeUpdate();
			}
		});
	}
	
	@Override
	public int insert(String sql, Object[] param) {
		return exceuteUpdate(sql, param);
	}

	@Override
	public int update(String sql, Object[] param) {
		return exceuteUpdate(sql, param);
	}

	@Override
	public int delete(String sql, Object[] param) {
		return exceuteUpdate(sql, param);
	}
}
