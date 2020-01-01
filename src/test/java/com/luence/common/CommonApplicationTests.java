package com.luence.common;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.luence.common.luence.LuenceInfo;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.CollectionUtils;
import org.springframework.util.SocketUtils;

import java.util.List;
import java.util.Map;

@RunWith(SpringRunner.class)
@SpringBootTest
class CommonApplicationTests {

    @Test
    void indexCreate() {
        List<Document> list = Lists.newArrayList();
        Document document= new Document();
        document.add(new TextField("name", "这个是luence测试使用的", Field.Store.YES));
        document.add(new StringField("price", "125.25", Field.Store.YES));
        document.add(new TextField("address", "广东省深圳市罗湖区仙湖植物园", Field.Store.YES));
        list.add(document);
        document = new Document();
        document.add(new TextField("name", "这个不是luence测试使用的", Field.Store.YES));
        document.add(new StringField("price", "125.25", Field.Store.YES));
        document.add(new TextField("address", "东莞失市仙湖植物园", Field.Store.YES));
        list.add(document);
        document = new Document();
        document.add(new TextField("name", "这个也不是luence测试使用的", Field.Store.YES));
        document.add(new StringField("price", "125.25", Field.Store.YES));
        document.add(new TextField("address", "深圳仙湖植物园", Field.Store.YES));
        list.add(document);
        Object json = null;
        try {
            json = LuenceInfo.class.newInstance().indexCreate(list);
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        JSONObject jsonObject = JSON.parseObject(json.toString());
        System.out.println("status = " + jsonObject.get("status") + " info = " + jsonObject.get("info"));
    }

    @Test
    void indexSearch(){
        try {
            List<Map<String, Object>> list = LuenceInfo.class.newInstance().indexSearch("深圳");
            if(CollectionUtils.isEmpty(list)) System.out.println("未查询到数据");
            for (Map<String, Object> map : list) {
                System.out.println( "name = "+map.get("name") + " price = "+map.get("price") + " address = "+map.get("address") );
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
