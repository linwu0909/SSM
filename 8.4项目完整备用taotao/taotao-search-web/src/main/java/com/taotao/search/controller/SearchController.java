package com.taotao.search.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.taotao.common.pojo.SearchResult;
import com.taotao.search.service.SearchService;

@Controller
public class SearchController {
	@Value("${ITEM_ROWS}")
	private Integer ITEM_ROWS;
	
	@Autowired
	private SearchService service;
	/**
	 * 根据条件搜索商品的数据
	 * @param page
	 * @param queryString
	 * @return
	 */
	@RequestMapping("/search")
	public String search(@RequestParam(defaultValue="1") Integer page,@RequestParam(value="q") String queryString,Model model) throws Exception{
		//1.引入
		//2.注入
		//3.调用
		//处理乱码：
		queryString = new String(queryString.getBytes("iso-8859-1"),"utf-8");
		SearchResult result = service.search(queryString, page, ITEM_ROWS);
		//4.设置数据传递到jsp中
		model.addAttribute("query", queryString);
		model.addAttribute("totalPages", result.getPageCount());//总页数
		model.addAttribute("itemList", result.getItemList());
		model.addAttribute("page", page);
		return "search";
	}
}
