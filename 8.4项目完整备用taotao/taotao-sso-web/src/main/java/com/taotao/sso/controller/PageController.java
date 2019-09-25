package com.taotao.sso.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * 跳转页面使用的controller
 * @title PageController.java
 * <p>description</p>
 * <p>company: www.itheima.com</p>
 * @author ljh 
 * @version 1.0
 */
@Controller
public class PageController {
	
	@RequestMapping("/page/{page}")
	public String showPage(@PathVariable String page,String redirect,Model model){
		System.out.println(redirect);
		model.addAttribute("redirect", redirect);
		return page;
	}
}
