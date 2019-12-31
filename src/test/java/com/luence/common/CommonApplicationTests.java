package com.luence.common;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.luence.common.luence.LuenceInfo;
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
        List<Field> list = Lists.newArrayList();
        Field filedName = new TextField("name", "这个是luence测试使用的", Field.Store.YES);
        Field filedPrice = new StringField("price", "125.25", Field.Store.YES);
        Field filedAddress = new TextField("address", "广东省深圳市罗湖区仙湖植物园", Field.Store.YES);
        Field filedName1 = new TextField("name", "这个不是luence测试使用的", Field.Store.YES);
        Field filedPrice1 = new StringField("price", "125.25", Field.Store.YES);
        Field filedAddress1 = new TextField("address", "深圳罗湖区仙湖植物园", Field.Store.YES);
        Field filedName2 = new TextField("name", "这个也不是luence测试使用的", Field.Store.YES);
        Field filedPrice2 = new StringField("price", "125.25", Field.Store.YES);
        Field filedAddress2 = new TextField("address", "深圳市罗湖区仙湖植物园", Field.Store.YES);
        list.add(filedName);
        list.add(filedPrice);
        list.add(filedAddress);
        list.add(filedName1);
        list.add(filedPrice1);
        list.add(filedAddress1);
        list.add(filedName2);
        list.add(filedPrice2);
        list.add(filedAddress2);
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
