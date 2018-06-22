package com.Amaterasu.Z.search.service.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.apache.solr.common.SolrInputDocument;
import org.apache.solr.common.params.SolrParams;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.Amaterasu.Z.pojo.ResponseResult;
import com.Amaterasu.Z.pojo.SearchItem;
import com.Amaterasu.Z.pojo.SearchResult;
import com.Amaterasu.Z.search.mapper.SearchItemMapper;
import com.Amaterasu.Z.search.service.SearchService;

@Service
public class SearchServiceImpl implements SearchService {

	@Autowired
	private SearchItemMapper searchItemMapper;
	
	//单例模式导入连接solr的客户端，否则一个请求new一次client 性能不好
	@Autowired
	private HttpSolrClient solrClient;
	
//	@Value("${SOLR_SERVER}")
//	private String SOLR_SERVER;
	
	
	/**
	 * 向索引库导入商品数据 
	 * 成功：返回200状态码
	 * 失败：返回500
	 */
	@Override
	public ResponseResult importItems() {

		//查询出所有商品数据
		List<SearchItem> list = searchItemMapper.getSearchItemList();
		
		//利用solrj写入索引库
//		HttpSolrClient client = getSolrClient(SOLR_SERVER);
		
		try {
			for (SearchItem item : list) {

				SolrInputDocument doc = new SolrInputDocument();
				doc.addField("id", item.getId());
				doc.addField("item_title", item.getTitle());
				doc.addField("item_price", item.getPrice());
				doc.addField("item_sell_point", item.getSell_point());
				doc.addField("item_image", item.getImage());
				doc.addField("item_category_name", item.getCategory_name());
				solrClient.add(doc);
			}
			//别忘了提交
			solrClient.commit();
			//成功
			return ResponseResult.ok();
		} catch (SolrServerException | IOException e) {
			e.printStackTrace();
			//失败
			return ResponseResult.build(500, "添加索引库失败！请检查！");
		}
		
	}
	
	/**
	 * 搜索商品
	 */
	@Override
	public SearchResult search(String keyword, Integer page, Integer rows) {

		SolrQuery query = new SolrQuery();
		
		//1.设置查询条件
		//设置默认域
		query.set("df","item_keywords");
		query.set("q", keyword); //查询条件
		//设置分页
		if(page<=0) page=1;
		query.setStart((page-1)*rows);
		query.setRows(rows);
		
		//设置高亮
		query.setHighlight(true);
		query.addHighlightField("item_title");
		query.setHighlightSimplePre("<span style='color:red'>");
		query.setHighlightSimplePost("</span>");
		
		//2.执行查询
		QueryResponse response = null;
		try {
			response = solrClient.query(query);
		} catch (SolrServerException e) {
			System.out.println("搜索服务中的search方法出现SolrServerException，请检查！");
			e.printStackTrace();
		} catch (IOException e) {
			System.out.println("搜索服务中的search方法出现IOException，请检查！");
			e.printStackTrace();
		}
		
		//3.获取查询结果
		SolrDocumentList docList = null;
		if(response!=null)
			docList = response.getResults();
		
		//整合结果
		SearchResult result = new SearchResult();
		//查询结果总条数
		long numFound = docList.getNumFound();
		result.setRecordCount(numFound);
		
		List<SearchItem> itemList = new ArrayList<>();
		Map<String, Map<String, List<String>>> highlighting = response.getHighlighting();
		for(SolrDocument doc:docList) {
			//获取SearchItem
			SearchItem item = new SearchItem();
			item.setId(new Long(doc.get("id").toString()));
			item.setPrice((Long)doc.get("item_price"));
			item.setSell_point((String) doc.get("item_sell_point"));
			item.setImage((String) doc.get("item_image"));
			item.setCategory_name((String) doc.get("item_category_name"));
			
			Map<String, List<String>> map = highlighting.get(doc.get("id"));
			List<String> list = map.get("item_title"); //取高亮域的结果
			
			//如果高亮结果不为空则 显示高亮 为空则显示原本的标题
			if(list!=null&&list.size()>0) {
				item.setTitle(list.get(0));
			}else {
				item.setTitle((String) doc.get("item_title"));
			}
			itemList.add(item);
		}
		
		result.setItemList(itemList);
		
		//设置页数
		Integer pages = (int) Math.ceil(numFound*1.0/rows);
		result.setTotalPages(pages);
		return result;
	}
	
//	private HttpSolrClient getSolrClient(String url) {
//		return new HttpSolrClient.Builder(url).build();
//	}

}
