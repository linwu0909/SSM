package com.taotao.sso.service;

import com.taotao.common.pojo.TaotaoResult;

/**
 * 登录的接口
 * @title UserLoginServcie.java
 * <p>description</p>
 * <p>company: www.itheima.com</p>
 * @author ljh 
 * @version 1.0
 */
public interface UserLoginService {
	/**
	 * 根据用户名和密码登录
	 * @param username
	 * @param password
	 * @return
	 * taotaoresult 登录成功 返回200 并且包含一个token数据
	 *登录失败：返回400
	 */
	public TaotaoResult login(String username,String password);
	/**
	 * 根据token获取用户的信息
	 * @param token
	 * @return  TaotaoResult 应该包含用户的信息
	 */
	public TaotaoResult getUserByToken(String token);
}
