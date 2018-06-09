package com.Amaterasu.Z.testPicUpload;

import org.csource.fastdfs.ClientGlobal;
import org.csource.fastdfs.StorageClient;
import org.csource.fastdfs.StorageServer;
import org.csource.fastdfs.TrackerClient;
import org.csource.fastdfs.TrackerServer;
import org.junit.Test;

import com.Amaterasu.Z.utils.FastDFSClient;

public class TestPicUpload {
	
	@Test
	public void testUpload() throws Exception{
		
		//加载全局配置文件
		ClientGlobal.init("C:\\计科学习\\JavaPrj\\UsingEclipse\\mavenprj\\tmallrepo\\tmall-manager-web\\src\\main\\resources\\conf\\client.conf");
	
		TrackerClient trackerClient = new TrackerClient();
		TrackerServer trackerServer = trackerClient.getConnection();
		StorageServer storageServer=null;
		StorageClient storageClient = new StorageClient(trackerServer,storageServer);
		//最核心的是StorageClient，通过它来上传文件
		
		String[] upload_file = storageClient.upload_file("C:\\Users\\Brilliant Coder\\Downloads\\Documents\\20141030203247_CfdwY.png", "png", null);
		for(String str:upload_file) {
			System.out.println(str);
		}
		
		/*
		 * upload_file数组内容保存
		 * group1
		   M00/00/00/wKg4C1sZ89eAUDZSAAVuz0l42QU026.png
		 */
	}
	
	@Test
	public void testFastDFSClient() throws Exception {
		FastDFSClient fastDFSClient = new FastDFSClient("C:\\计科学习\\JavaPrj\\UsingEclipse\\mavenprj\\tmallrepo\\tmall-manager-web\\src\\main\\resources\\conf\\client.conf");
	
		String str = fastDFSClient.uploadFile("C:\\Users\\Brilliant Coder\\Downloads\\Documents\\naruto.jpg", "jpg");
		System.out.println(str);
		
		//group1/M00/00/00/wKg4C1sZ-RiAX9jiAACcUOQfDJg036.jpg
	}
}
