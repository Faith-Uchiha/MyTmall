# MyTmall
个人仿制天猫商城

## 项目架构
![image](https://github.com/Faith-Uchiha/MyTmall/blob/master/ProjectArch.png)<br>
数据量变大后可以采用Mycat对数据库进行分片处理，构成数据库集群
## 开发环境

## 软件版本
 - 详见tmall-parent的pom.xml
 
## 项目启动所需环境
 - redis，缓存
 - zookeeper，作为dubbo的注册中心
 - fastdfs，图片服务器，包括首页轮播图都需要用到
 
## 待完善功能
 - Solr集群的支持
 - 页面静态化
 - 商品详情的缓存，避免重复查询数据库

## 注意事项
前端工程front-web均为静态图片，无法显示商品详情，需要自己修改jsp，并设置跳转URL<br>
商品详情仅支持从搜索结果跳转
