package org.leetieniu.jdbc;

import java.util.Date;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.leetieniu.jdbc.holder.JdbcHolder;
import org.leetieniu.jdbc.model.User;
import org.leetieniu.jdbc.templete.JdbcTemplete;

/**
 * 测试
 * @author leetieniu
 */
public class JdbcTempleteTest {
	
	private JdbcTemplete JdbcTemplete = JdbcHolder.getDefaultJdbcTemplete();
	//private JdbcTemplete JdbcTemplete2 = JdbcTempleteHolder.getDefaultJdbcTemplete();
	
	@Test
	@Ignore
	public void testQuery() {
		final String sql = "select * from user where id = ?";
		final Object[] param = {3};
		final User user = JdbcTemplete.queryForObject(sql, param, User.class);
		System.out.println(user);
	}
	
	@Test
	@Ignore
	public void testInsert() {
		final String sql = "insert into user values(?, ?, ? ,?, ?)";
		final int id = 3;
		final String userName = "leetieniu";
		final String password = "leetieniu";
		final Date createTime = new Date();
		final double rmb = 100.05;
		
		final Object[] param = {id, userName, password, createTime, rmb};
		int result = JdbcTemplete.insert(sql, param);
		Assert.assertTrue(result > 0);
	}
	
	@Test
	@Ignore
	public void testUpdate() {
		final String sql = "update user set username = ? where id = ?";
		final int id = 2;
		final String userName = "leejun";
		
		final Object[] param = {userName, id};
		int result = JdbcTemplete.update(sql, param);
		Assert.assertTrue(result > 0);
	}
	
	@Test
	@Ignore
	public void testDelete() {
		final String sql = "delete from user where id = ?";
		final int id = 3;
		final Object[] param = {id};
		int result = JdbcTemplete.update(sql, param);
		Assert.assertTrue(result > 0);
	}
}
