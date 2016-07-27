package org.leetieniu.jdbc.templete;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.leetieniu.jdbc.datasouce.SimpleDataSource;
import org.leetieniu.jdbc.exception.JdbcTempleteException;

/**
 * 默认JdbcTemplete实现
 * @author leetieniu
 */
public class DefaultJdbcTemplete implements JdbcTemplete {
	
	public DefaultJdbcTemplete() {}
	
	/**
	 * @param dataSource 数据源
	 */
	public DefaultJdbcTemplete(SimpleDataSource dataSource) {
		this.dataSource = dataSource;
	}
	
	/** 数据源 */
	private SimpleDataSource dataSource;

	public void setDataSource(SimpleDataSource dataSource) {
		this.dataSource = dataSource;
	}
	
	/**
	 * 查询模板
	 * @param pstmt
	 * @param clazz
	 * @param handler
	 * @return D
	 * @throws SQLException
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 */
	private <T,D> D query(PreparedStatement pstmt, Class<T> clazz, ResultSetHandler<D, T> handler) throws SQLException, InstantiationException, IllegalAccessException {
		ResultSet resultSet = null;
		D d = null;
		try {
			resultSet = pstmt.executeQuery();
			d = handler.handle(resultSet, clazz);
		} catch (SQLException ex) {
			throw ex;
		} catch (InstantiationException ex) {
			throw ex;
		} catch (IllegalAccessException ex) {
			throw ex;
		} finally {
			try {
				if (resultSet != null) {
					resultSet.close();
				}
			} catch (SQLException ex) {
				throw ex;
			}
		}
		return d;
	}
	
	/**
	 * 执行方法  连接自己管理
	 * @param conn
	 * @param sql
	 * @param param
	 * @param handler
	 * @return D
	 */
	private <T,D> D excute(Connection conn ,String sql, Object[] param , PreparedStatementJdbcHandler<D> handler) {
		PreparedStatement pstmt = null;
		D d = null;
		try {
			pstmt = conn.prepareStatement(sql);
			if(null != param) {
				for (int i = 0; i < param.length; i++) {
					pstmt.setObject(i + 1, param[i]);
			    }
			}
			d = handler.handle(pstmt);
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
				}
			} catch (SQLException ex) {
				//throw new JdbcTempleteException(ex);
			}
		}
		return d;
	}
	
	private final static int FIRST = 0;
	
	/**
	 * 获取结果集对象
	 * @param resultSet
	 * @param clazz
	 * @return T
	 * @throws SQLException
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 */
	private <T> T getObject(ResultSet resultSet, Class<T> clazz) throws SQLException, InstantiationException, IllegalAccessException  {
		resultSet.last();
		if(resultSet.getRow() > 1) {
			throw new JdbcTempleteException("结果集大于1个");
		}
		resultSet.beforeFirst();
		List<T> list = getObjectList(resultSet, clazz);
		if(list.size() < 1) {
			return null;
		}
		return list.get(FIRST);
	}
	
	/**
	 * 获取结果集的对象列表
	 * @param resultSet
	 * @param clazz
	 * @return List<T>
	 * @throws SQLException
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 */
	private <T> List<T> getObjectList(ResultSet resultSet, Class<T> clazz) 
			throws SQLException, InstantiationException, IllegalAccessException {
		resultSet.last();
		int row = resultSet.getRow() + 1;
		resultSet.beforeFirst();
		
		List<T> result = null;
		
		final ResultSetMetaData resultSetMetaData = resultSet.getMetaData();
		int columnCount = resultSetMetaData.getColumnCount();
		
		Field[] fields = clazz.getDeclaredFields();
		
		//TODO 算法待优化
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
				
				if(result == null) {
					result = new ArrayList<T>(row);
				}
				result.add(t);
			}
		}
		if(result == null) {
			result = Collections.emptyList();
		}
		return result;
	}
	
	/**
	 * PreparedStatementJdbcHandler
	 * @author leetieniu
	 * @param <D>
	 */
	interface PreparedStatementJdbcHandler<D> {
		
		/**
		 * 模板方法
		 * @param pstmt
		 * @return
		 * @throws SQLException
		 * @throws InstantiationException
		 * @throws IllegalAccessException
		 */
		public D handle(PreparedStatement pstmt) throws SQLException, InstantiationException, IllegalAccessException;
	}
	
	/**
	 * ResultSetHandler
	 * @author leetieniu
	 * @param <D> 返回值
	 * @param <T> 参数类型
	 */
	interface ResultSetHandler<D, T> {
		
		/**
		 * 模板方法
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
	private static final String SIMPLENAME_BASE_INT = "int";
	private static final String SIMPLENAME_LONG = "Long";
	private static final String SIMPLENAME_BASE_LONG = "long";
	private static final String SIMPLENAME_STRING = "String";
	private static final String SIMPLENAME_DATE = "Date";
	private static final String SIMPLENAME_DOUBLE = "Double";
	private static final String SIMPLENAME_BASE_DOUBLE = "double";
	
	/**
	 * 设置bean的值
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
			if(SIMPLENAME_INTEGER.equals(simpleName) || SIMPLENAME_BASE_INT.equals(simpleName)) {
				field.set(obj, resultSet.getInt(columnIndex));
			} else if(SIMPLENAME_LONG.equals(simpleName) || SIMPLENAME_BASE_LONG.equals(simpleName)) {
				field.set(obj, resultSet.getLong(columnIndex));
			} else if(SIMPLENAME_STRING.equals(simpleName)) {
				field.set(obj, resultSet.getString(columnIndex));
			} else if(SIMPLENAME_DATE.equals(simpleName)) {
				//field.set(obj, new Date(resultSet.getTimestamp(columnIndex).getTime()));
				field.set(obj, resultSet.getTimestamp(columnIndex));
			} else if(SIMPLENAME_DOUBLE.equals(simpleName) || SIMPLENAME_BASE_DOUBLE.equals(simpleName)) {
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
	 * 执行更新
	 * @param sql
	 * @param param 参数
	 * @return 大于0成功否则失败
	 */
	private int exceuteUpdate(String sql, Object[] param) {
		Connection conn = null;
		try {
			conn = dataSource.getConnection();
			return exceuteUpdate(conn, sql, param);
		} catch (SQLException ex) {
			throw new JdbcTempleteException(ex);
		} finally {
			dataSource.releaseConnection(conn);
		}
	}
	
	/**
	 * 执行更新
	 * @param conn
	 * @param sql
	 * @param param 参数
	 * @return 大于0成功否则失败
	 */
	private int exceuteUpdate(Connection conn, String sql, Object[] param) {
		return excute(conn, sql, param , new PreparedStatementJdbcHandler<Integer> () {
			@Override
			public Integer handle(PreparedStatement pstmt) throws SQLException,
					InstantiationException, IllegalAccessException {
				return pstmt.executeUpdate();
			}
		});
	}
	
	@Override
	public int insert(Connection conn, String sql, Object[] param) {
		return exceuteUpdate(conn, sql, param);
	}
	
	@Override
	public int insert(String sql, Object[] param) {
		return exceuteUpdate(sql, param);
	}
	
	@Override
	public int update(Connection conn, String sql, Object[] param) {
		return exceuteUpdate(conn, sql, param);
	}
	
	@Override
	public int update(String sql, Object[] param) {
		return exceuteUpdate(sql, param);
	}
	
	@Override
	public int delete(Connection conn, String sql, Object[] param) {
		return exceuteUpdate(conn, sql, param);
	}
	
	@Override
	public int delete(String sql, Object[] param) {
		return exceuteUpdate(sql, param);
	}
	
	@Override
	public <T> T queryForObject(String sql, Object[] param , final Class<T> clazz) {
		Connection conn = null;
		try {
			conn = dataSource.getConnection();
			return queryForObject(conn, sql, param , clazz);
		} catch (SQLException ex) {
			throw new JdbcTempleteException(ex);
		} finally {
			dataSource.releaseConnection(conn);
		}
	}
	
	@Override
	public <T> T queryForObject(Connection conn, String sql, Object[] param , final Class<T> clazz) {
		T t = excute(conn, sql, param ,  new PreparedStatementJdbcHandler<T> () {
			@Override
			public T handle(PreparedStatement pstmt) throws SQLException,
					InstantiationException, IllegalAccessException {
				T t = query(pstmt, clazz, new ResultSetHandler<T, T> () {
					@Override
					public T handle(ResultSet resultSet, Class<T> clazz)
							throws SQLException, InstantiationException,
							IllegalAccessException {
						T t = getObject(resultSet, clazz);
						return t;
					}
				});
				return t;
			}
		});
		return t;
	}
	
	@Override
	public <T> List<T> queryForList(String sql, Object[] param , final Class<T> clazz) {
		Connection conn = null;
		try {
			conn = dataSource.getConnection();
			return queryForList(conn, sql, param , clazz);
		} catch (SQLException ex) {
			throw new JdbcTempleteException(ex);
		} finally {
			dataSource.releaseConnection(conn);
		}
	}
	
	@Override
	public <T> List<T> queryForList(Connection conn, String sql, Object[] param , final Class<T> clazz) {
		List<T> list = excute(conn, sql, param , new PreparedStatementJdbcHandler<List<T>> () {

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
	 * 批量更新, 普通的Statement
	 * @param conn
	 * @param sqls 批量的sql
	 * @return 每条sql 成功执行数
	 */
	private int[] executeBatchByStatement(Connection conn , List<String> sqls) {
		Statement stm = null;
		try {
			stm = conn.createStatement();
			
			if(sqls.size() > 0) {
				for(String sql : sqls) {
					stm.addBatch(sql);
				}
			}
			return stm.executeBatch();
		} catch (SQLException ex) {
			throw new JdbcTempleteException(ex);
		} catch (Exception ex) {
			throw new JdbcTempleteException(ex);
		} finally {
			try {
				if (stm != null) {
					stm.close();
				}
			} catch (SQLException ex) {
				//throw new JdbcTempleteException(ex);
			}
		}
	}
	
	/**
	 * 批量更新 PreparedStatement
	 * @param conn
	 * @param sql 同一条sql
	 * @param params 多条的参数
	 * @return 每条sql 成功执行数
	 */
	private int[] executeBatchByPreparedStatement(Connection conn , String sql, List<Object[]> params) {
		PreparedStatement pstmt = null;
		try {
			pstmt = conn.prepareStatement(sql);
			
			if(null != params) {
				for(Object[] param : params) {
					for (int i = 0; i < param.length; i++) {
						pstmt.setObject(i + 1, param[i]);
				    }
					pstmt.addBatch();
				}
			}
			return pstmt.executeBatch();
		} catch (SQLException ex) {
			throw new JdbcTempleteException(ex);
		} catch (Exception ex) {
			throw new JdbcTempleteException(ex);
		} finally {
			try {
				if (pstmt != null) {
					pstmt.close();
				}
			} catch (SQLException ex) {
				//throw new JdbcTempleteException(ex);
			}
		}
	}
	
	@Override
	public int[] executeBatch(String sql, List<Object[]> params) {
		Connection conn = null;
		try {
			conn = dataSource.getConnection();
			return executeBatchByPreparedStatement(conn , sql, params);
		} catch (SQLException ex) {
			throw new JdbcTempleteException(ex);
		} finally {
			dataSource.releaseConnection(conn);
		}
	}
	
	@Override
	public int[] executeBatch(Connection conn , String sql, List<Object[]> params) {
		return executeBatchByPreparedStatement(conn , sql, params);
	}
	
	@Override
	public int[] executeBatch(List<String> sqls) {
		Connection conn = null;
		try {
			conn = dataSource.getConnection();
			return executeBatch(conn, sqls);
		} catch (SQLException ex) {
			throw new JdbcTempleteException(ex);
		} finally {
			dataSource.releaseConnection(conn);
		}
	}
	
	@Override
	public int[] executeBatch(Connection conn, List<String> sqls) {
		return executeBatchByStatement(conn, sqls);
	}
}
