package com.taotao.search.service;

import com.taotao.common.pojo.SearchResult;
import com.taotao.common.pojo.TaotaoResult;

public interface SearchService {
	//导入所有的商品数据到索引库中
	public TaotaoResult importAllSearchItems() throws Exception;
	//根据搜索的条件搜索的结果
	/**
	 * 
	 * @param queryString  查询的主条件
	 * @param page  查询的当前的页码
	 * @param rows  每页显示的行数 这个在controller中写死
	 * @return
	 * @throws Exception
	 */
	public SearchResult search(String queryString ,Integer page,Integer rows) throws Exception;
	/**
	 * 更新索引库
	 * @param itemId
	 * @return
	 * @throws Exception
	 */
	public TaotaoResult updateSearchItemById(Long itemId) throws Exception;
}
