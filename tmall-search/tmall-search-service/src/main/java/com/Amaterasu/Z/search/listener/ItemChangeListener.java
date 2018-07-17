package com.Amaterasu.Z.search.listener;

import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;

import org.springframework.beans.factory.annotation.Autowired;

import com.Amaterasu.Z.search.service.impl.SearchServiceImpl;

public class ItemChangeListener implements MessageListener {
	
	@Autowired
	private SearchServiceImpl searchServiceImpl;

	@Override
	public void onMessage(Message message) {
		try {
			TextMessage textMessage = null;
			Long itemId = null; 
			//取商品id
			if (message instanceof TextMessage) {
				textMessage = (TextMessage) message;
				String recv = textMessage.getText();
				if(recv==null) {
					System.out.println("收到商品id为空！");
					return;
				}else {
					itemId = Long.parseLong(recv);
				}
				
			}
			//向索引库添加文档
			searchServiceImpl.addDocument(itemId);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
