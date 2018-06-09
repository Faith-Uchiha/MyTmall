package com.Amaterasu.Z.pageHelper;

import java.util.List;

import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.Amaterasu.Z.mapper.TbItemMapper;
import com.Amaterasu.Z.pojo.TbItem;
import com.Amaterasu.Z.pojo.TbItemExample;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;

public class PageHelperTest {

	@Test
	public void testPageHelper() {
		
		
		ApplicationContext applicationContext = new ClassPathXmlApplicationContext("classpath:spring/applicationContext-dao.xml");
		//获取代理对象
		TbItemMapper itemMapper = applicationContext.getBean(TbItemMapper.class);
		//分页
		PageHelper.startPage(1, 10);
		TbItemExample example = new TbItemExample();
		//查询
		List<TbItem> itemList = itemMapper.selectByExample(example );
		PageInfo<TbItem> pageInfo = new PageInfo<>(itemList);
		System.out.println(pageInfo.getTotal());//总条数
		System.out.println(pageInfo.getPages());//总页数
		System.out.println(itemList.size());//查出了几条
	}
}
