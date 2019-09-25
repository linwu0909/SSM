package com.taotao.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.taotao.common.pojo.TaotaoResult;
import com.taotao.search.service.SearchService;

@Controller
public class ImportAllItems {
	@Autowired
	private SearchService service;
	/**
	 * 导入所有的商品的数据到索引库中
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("/index/importAll")
	@ResponseBody
	public TaotaoResult importAll() throws Exception{
		//1.引入服务
		//2.注入服务
		//3.调用方法
		return service.importAllSearchItems();
	}
}
