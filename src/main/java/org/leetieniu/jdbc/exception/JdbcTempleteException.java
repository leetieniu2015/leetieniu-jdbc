package org.leetieniu.jdbc.exception;

/**
 * @package org.leetieniu.jdbc.exception  
 * @author leetieniu
 * @description 数据库连接异常, 把所有数据库异常都封装成此异常类
 * @date 2016年5月6日 下午7:16:47    
 * @version V1.0
 */
public class JdbcTempleteException extends RuntimeException {

	private static final long serialVersionUID = 4376516742904096837L;

	public JdbcTempleteException() {
		super();
	}

	public JdbcTempleteException(String message) {
		super(message);
	}

	public JdbcTempleteException(Throwable cause) {
		super(cause);
	}

	public JdbcTempleteException(String message, Throwable cause) {
		super(message, cause);
	}
}
