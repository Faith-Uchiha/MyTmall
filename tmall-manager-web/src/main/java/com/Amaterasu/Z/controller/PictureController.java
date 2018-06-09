package com.Amaterasu.Z.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.Amaterasu.Z.utils.FastDFSClient;
import com.Amaterasu.Z.utils.JsonUtils;

@Controller
public class PictureController {

	@Value("${IMAGE_SERVER_URL}")
	private String IMAGE_SERVER_URL;
	/**
	 * 图片上传
	 * @param uploadFile 传过来的图片
	 * @return 成功，返回{"error":0,"url":"http://"} 失败，返回{"error":1,message:""}
	 * @throws Exception 
	 */
	@RequestMapping(value="/pic/upload",produces=MediaType.TEXT_PLAIN_VALUE+";charset=utf-8")
	@ResponseBody
	public String pictureUpload(MultipartFile uploadFile) {
		
		try {
			//通过FastDFSClient客户端访问文件服务器
			FastDFSClient fastDFSClient = new FastDFSClient("C:\\计科学习\\JavaPrj\\UsingEclipse\\mavenprj\\tmallrepo\\tmall-manager-web\\src\\main\\resources\\conf\\client.conf");
			
			//从参数中获取文件byte[] 和扩展名extName
	//		String name = uploadFile.getName();  //这个获得的是参数名 而不是文件名
			String originalFilename = uploadFile.getOriginalFilename();
			String extName = originalFilename.substring(originalFilename.lastIndexOf(".")+1);
	//		System.out.println(name);
	//		System.out.println(originalFilename);
			
			//上传文件 返回一个访问图片的URL
			String picUrl = fastDFSClient.uploadFile(uploadFile.getBytes(), extName);
			//在前面添加服务器ip
			picUrl = IMAGE_SERVER_URL+picUrl; 
			Map map= new HashMap<>();
			map.put("error", 0);
			map.put("url", picUrl);
			return JsonUtils.objectToJson(map);
		}catch(Exception e) {
			
			e.printStackTrace();
			//出现异常就返回图片上传失败的json
			Map map = new HashMap<>();
			map.put("error", 1);
			map.put("message", "图片上传失败");
			return JsonUtils.objectToJson(map);
		}
		
	}
}
