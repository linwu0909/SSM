package com.taotao.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import javax.annotation.Resource;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;
import org.springframework.stereotype.Service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.taotao.common.pojo.EasyUIDataGridResult;
import com.taotao.common.pojo.TaotaoResult;
import com.taotao.common.util.IDUtils;
import com.taotao.common.util.JsonUtils;
import com.taotao.manager.jedis.JedisClient;
import com.taotao.mapper.TbItemDescMapper;
import com.taotao.mapper.TbItemMapper;
import com.taotao.pojo.TbItem;
import com.taotao.pojo.TbItemDesc;
import com.taotao.pojo.TbItemExample;
import com.taotao.pojo.TbItemExample.Criteria;
import com.taotao.service.ItemService;

@Service
public class ItemServiceImpl implements ItemService {
	@Autowired
	private TbItemMapper mapper;
	@Autowired
	private TbItemDescMapper descmapper;
	@Autowired
	private JedisClient client;

	@Autowired
	private JmsTemplate jmstemplate;

	@Resource(name = "topicDestination")
	private Destination destination;

	@Value("${ITEM_INFO_KEY}")
	private String ITEM_INFO_KEY;

	@Value("${ITEM_INFO_KEY_EXPIRE}")
	private Integer ITEM_INFO_KEY_EXPIRE;

	@Override
	public EasyUIDataGridResult getItemList(Integer page, Integer rows) {
		// 1.设置分页的信息 使用pagehelper
		if (page == null)
			page = 1;
		if (rows == null)
			rows = 30;
		PageHelper.startPage(page, rows);
		// 2.注入mapper
		// 3.创建example 对象 不需要设置查询条件
		TbItemExample example = new TbItemExample();
		// 4.根据mapper调用查询所有数据的方法
		List<TbItem> list = mapper.selectByExample(example);
		// 5.获取分页的信息
		PageInfo<TbItem> info = new PageInfo<>(list);
		// 6.封装到EasyUIDataGridResult
		EasyUIDataGridResult result = new EasyUIDataGridResult();
		result.setTotal((int) info.getTotal());
		result.setRows(info.getList());
		// 7.返回
		return result;
	}

	@Override
	public TaotaoResult saveItem(TbItem item, String desc) {
		// 生成商品的id
		final long itemId = IDUtils.genItemId();
		// 1.补全item 的其他属性
		item.setId(itemId);
		item.setCreated(new Date());
		// 1-正常，2-下架，3-删除',
		item.setStatus((byte) 1);
		item.setUpdated(item.getCreated());
		// 2.插入到item表 商品的基本信息表
		mapper.insertSelective(item);
		// 3.补全商品描述中的属性
		TbItemDesc desc2 = new TbItemDesc();
		desc2.setItemDesc(desc);
		desc2.setItemId(itemId);
		desc2.setCreated(item.getCreated());
		desc2.setUpdated(item.getCreated());
		// 4.插入商品描述数据
		// 注入tbitemdesc的mapper
		descmapper.insertSelective(desc2);

		// 添加发送消息的业务逻辑
		jmstemplate.send(destination, new MessageCreator() {

			@Override
			public Message createMessage(Session session) throws JMSException {
				// 发送的消息
				return session.createTextMessage(itemId + "");
			}
		});
		// 5.返回taotaoresult
		return TaotaoResult.ok();
	}

	@Override
	public TbItem getItemById(Long itemId) {

		// 添加缓存

		// 1.从缓存中获取数据，如果有直接返回
		try {
			String jsonstr = client.get(ITEM_INFO_KEY + ":" + itemId + ":BASE");

			if (StringUtils.isNotBlank(jsonstr)) {
				// 重新设置商品的有效期
				client.expire(ITEM_INFO_KEY + ":" + itemId + ":BASE", ITEM_INFO_KEY_EXPIRE);
				return JsonUtils.jsonToPojo(jsonstr, TbItem.class);

			}
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		// 2如果没有数据

		// 注入mapper
		// 调用方法
		TbItem tbItem = mapper.selectByPrimaryKey(itemId);
		// 返回tbitem

		// 3.添加缓存到redis数据库中
		// 注入jedisclient
		// ITEM_INFO:123456:BASE
		// ITEM_INFO:123456:DESC
		try {
			client.set(ITEM_INFO_KEY + ":" + itemId + ":BASE", JsonUtils.objectToJson(tbItem));
			// 设置缓存的有效期
			client.expire(ITEM_INFO_KEY + ":" + itemId + ":BASE", ITEM_INFO_KEY_EXPIRE);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return tbItem;
	}

	@Override
	public TbItemDesc getItemDescById(Long itemId) {
		// 添加缓存

		// 1.从缓存中获取数据，如果有直接返回
		try {
			String jsonstr = client.get(ITEM_INFO_KEY + ":" + itemId + ":DESC");

			if (StringUtils.isNotBlank(jsonstr)) {
				// 重新设置商品的有效期
				System.out.println("有缓存");
				client.expire(ITEM_INFO_KEY + ":" + itemId + ":DESC", ITEM_INFO_KEY_EXPIRE);
				return JsonUtils.jsonToPojo(jsonstr, TbItemDesc.class);

			}
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		//如果没有查到数据 从数据库中查询
		TbItemDesc itemdesc = descmapper.selectByPrimaryKey(itemId);
		//添加缓存
		// 3.添加缓存到redis数据库中
		// 注入jedisclient
		// ITEM_INFO:123456:BASE
		// ITEM_INFO:123456:DESC
		try {
			client.set(ITEM_INFO_KEY + ":" + itemId + ":DESC", JsonUtils.objectToJson(itemdesc));
			// 设置缓存的有效期
			client.expire(ITEM_INFO_KEY + ":" + itemId + ":DESC", ITEM_INFO_KEY_EXPIRE);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return itemdesc;
	}

}
