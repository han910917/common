# common包含各个第三方jar的开发
--- 191231 ---  
1、添加FileUtil工具包  
2、添加实体类  
3、修改动态获取配置文件失败问题  
4、索引的简单查询（未完 ik分词器不兼容luence7）


--- 200102 ---  
1、修改包名  
2、修改pom，打包成可共用的jar

--- 200105 ----  
1、移除实体类  
2、修改创建索引的方法  
3、修改fileUtil文件，去掉每次创建索引时删除旧索引 

--- 200107 ---  
1、新增term查询、通配符查询、模糊查询、数字范围查询

--- 200108 ---  
1、新增组合查询  
2、新增基于词条和Query对象的索引删除  
说明：不提供删除全部的接口，数据量大的话，索引难建   

--- 200110 ---  
1、普通查询添加索引地址参数  

--- 200114 ---  
1、修改QueryUtil，排除实体类为空  