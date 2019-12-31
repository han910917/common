package com.luence.common;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.luence.common.entity.SystemParams;
import com.luence.common.luence.LuenceInfo;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest
class CommonApplicationTests {

    @Test
    void contextLoads() {
        List<Field> list = Lists.newArrayList();
        Field filedName = new TextField("name", "这个是luence测试使用的", Field.Store.YES);
        Field filedPrice = new StringField("price", "125.25", Field.Store.YES);
        Field filedAddress = new TextField("address", "广东省深圳市罗湖区仙湖植物园", Field.Store.YES);
        list.add(filedName);
        list.add(filedPrice);
        list.add(filedAddress);
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

}
