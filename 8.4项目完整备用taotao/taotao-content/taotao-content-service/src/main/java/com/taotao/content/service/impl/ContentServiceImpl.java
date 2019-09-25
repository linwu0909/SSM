package com.taotao.content.service.impl;

import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.aspectj.weaver.AjAttribute.PrivilegedAttribute;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.taotao.common.pojo.TaotaoResult;
import com.taotao.common.util.JsonUtils;
import com.taotao.content.jedis.JedisClient;
import com.taotao.content.service.ContentService;
import com.taotao.mapper.TbContentMapper;
import com.taotao.pojo.TbContent;
import com.taotao.pojo.TbContentExample;

@Service
public class ContentServiceImpl implements ContentService {

	@Autowired
	private JedisClient client;
	
	@Autowired
	private TbContentMapper mapper;
	
	@Value("${CONTENT_KEY}")
	private String CONTENT_KEY;
	
	
	@Override
	public TaotaoResult saveContent(TbContent content) {
		//1.注入mapper
		
		//2.补全其他的属性
		content.setCreated(new Date());
		content.setUpdated(content.getCreated());
		//3.插入内容表中
		mapper.insertSelective(content);
		
		//当添加内容的时候，需要清空此内容所属的分类下的所有的缓存
		try {
			client.hdel(CONTENT_KEY, content.getCategoryId()+"");
			System.out.println("当插入时，清空缓存!!!!!!!!!!");
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return TaotaoResult.ok();
	}

	@Override
	public List<TbContent> getContentListByCatId(Long categoryId) {
		
		//添加缓存不能影响正常的业务逻辑
		
		//判断 是否redis中有数据  如果有   直接从redis中获取数据 返回
		
		try {
			String jsonstr = client.hget(CONTENT_KEY, categoryId+"");//从redis数据库中获取内容分类下的所有的内容。
			//如果存在，说明有缓存
			if(StringUtils.isNotBlank(jsonstr)){
			System.out.println("这里有缓存啦！！！！！");
				return JsonUtils.jsonToList(jsonstr, TbContent.class);
			}
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		
		
		//1.注入mapper
		//2.创建example
		TbContentExample example = new TbContentExample();
		//3.设置查询的条件
		example.createCriteria().andCategoryIdEqualTo(categoryId);//select × from tbcontent where category_id=1
		//4.执行查询
		List<TbContent> list = mapper.selectByExample(example);
		//返回
		
		
		//将数据写入到redis数据库中   
		
		// 注入jedisclient 
		// 调用方法写入redis   key - value
		try {
			System.out.println("没有缓存！！！！！！");
			client.hset(CONTENT_KEY, categoryId+"", JsonUtils.objectToJson(list));
		} catch (Exception e) {
			e.printStackTrace();
		}
				
		return list;
	}

}
