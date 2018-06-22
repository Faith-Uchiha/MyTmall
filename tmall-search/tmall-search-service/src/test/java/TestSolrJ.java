import java.io.IOException;

import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.apache.solr.common.SolrInputDocument;
import org.junit.Test;

public class TestSolrJ {
	
	@Test
	public void testUpload() throws IOException, SolrServerException {
		String baseURL = "http://192.168.56.11:8080/solrapp/new_core";
		HttpSolrClient client = new HttpSolrClient.Builder(baseURL).build();
		
		//创建文档对象
		SolrInputDocument doc = new SolrInputDocument();
		//创建域
		doc.addField("id", "test001");
		doc.addField("item_title", "测试商品01");
		doc.addField("item_price", "199");
		//添加文档和索引
		client.add(doc);
		client.commit();
	}
	
	@Test
	public void testDelete() throws IOException, SolrServerException {
		String baseURL = "http://192.168.56.11:8080/solrapp/new_core";
		HttpSolrClient client = new HttpSolrClient.Builder(baseURL).build();
		
		client.deleteById("test001");
		client.commit();
	}
}
