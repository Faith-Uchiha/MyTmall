package com.Amaterasu.Z.service.impl;

import java.util.Date;
import java.util.List;

import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;
import javax.jms.TextMessage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;
import org.springframework.stereotype.Service;

import com.Amaterasu.Z.mapper.TbItemDescMapper;
import com.Amaterasu.Z.mapper.TbItemMapper;
import com.Amaterasu.Z.pojo.EasyUIDataGridResult;
import com.Amaterasu.Z.pojo.ResponseResult;
import com.Amaterasu.Z.pojo.TbItem;
import com.Amaterasu.Z.pojo.TbItemDesc;
import com.Amaterasu.Z.pojo.TbItemExample;
import com.Amaterasu.Z.pojo.TbItemExample.Criteria;
import com.Amaterasu.Z.service.ItemService;
import com.Amaterasu.Z.utils.IDUtils;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;

@Service
public class ItemServiceImpl implements ItemService {
	
	@Autowired
	private TbItemMapper itemMapper;
	
	@Autowired
	private TbItemDescMapper itemDescMapper;
	
	//同步索引库需要
	@Autowired
	private JmsTemplate jmsTemplate;
	
	@Autowired
	private Destination topicDestination;
	
	public TbItem selectItemById(long id){
		
		TbItemExample example = new TbItemExample();
		 Criteria criteria = example.createCriteria();
		 criteria.andIdEqualTo(id);
		 
		List<TbItem> itemList = itemMapper.selectByExample(example);
		
		if(itemList!=null&&itemList.size()>0)
			return itemList.get(0);
		
		return null;
	}

	@Override
	public EasyUIDataGridResult<TbItem> selectItemListByPage(Integer page, Integer rows) {
		
		//设置分页
		PageHelper.startPage(page, rows);
		TbItemExample example = new TbItemExample();
		//查询
		List<TbItem> list = itemMapper.selectByExample(example);
		EasyUIDataGridResult<TbItem> result = new EasyUIDataGridResult<>();
		//设置返回前端的数据
		result.setRows(list);
		PageInfo<TbItem> pageInfo = new PageInfo<>(list);
		result.setTotal(pageInfo.getTotal());
		return result;
	}

	@Override
	public ResponseResult insertItem(TbItem item, String desc) {
		//数据库中商品ID不是自增，要手动添加
		long id = IDUtils.genItemId();
		item.setId(id);
		item.setStatus((byte) 1);
		Date created = new Date();
		item.setCreated(created);
		item.setUpdated(created);
		itemMapper.insert(item);
		
		//插入第二张表 Tb_Item_Desc
		TbItemDesc tbItemDesc = new TbItemDesc();
		tbItemDesc.setCreated(created);
		tbItemDesc.setUpdated(created);
		tbItemDesc.setItemDesc(desc);
		tbItemDesc.setItemId(id);
		itemDescMapper.insert(tbItemDesc);

		//发送一个商品添加消息  
		jmsTemplate.send(topicDestination, new MessageCreator() {
			
			@Override
			public Message createMessage(Session session) throws JMSException {
				TextMessage textMessage = session.createTextMessage(item.getId() + "");
				return textMessage;
			}
		});
		return ResponseResult.ok();
	}	
}
