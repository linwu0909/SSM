package com.taotao.search.test;

import org.apache.solr.client.solrj.impl.CloudSolrServer;
import org.apache.solr.common.SolrInputDocument;
import org.junit.Test;

public class SolrCloudTest {
	//
	@Test
	public void testAdd() throws Exception{
		//1.创建solrserver   集群的实现类
		//指定zookeeper集群的节点列表字符串
		CloudSolrServer cloudSolrServer = new CloudSolrServer("192.168.25.154:2181,192.168.25.154:2182,192.168.25.154:2183");
		//2.设置默认的搜索的collection  默认的索引库（不是core所对应的，是指整个collection索引集合）
		cloudSolrServer.setDefaultCollection("collection2");
		//3.创建solrinputdocumenet对象
		SolrInputDocument document = new SolrInputDocument();
		//4.添加域到文档
		document.addField("id", "testcloudid");
		document.addField("item_title", "今天鸟语花香，容易睡觉");
		//5.将文档提交到索引库中
		cloudSolrServer.add(document);
		//6.提交
		cloudSolrServer.commit();
	}
}
