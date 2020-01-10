package com.springboot.common.utils;

import com.google.common.collect.Lists;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TermRangeQuery;
import org.wltea.analyzer.lucene.IKAnalyzer;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;

/**
 * 根据传入实体获取查询方法
 * @Description
 * @Author hangaoming
 * @Time 2020/1/10 13:47
 **/
public class QueryUtil {

    public static <T> List<Query> getQuery(T resultParams) throws Exception {
        List<Query> list = Lists.newArrayList();
        java.lang.reflect.Field[] filed = resultParams.getClass().getDeclaredFields();
        for (Field field : filed) {
            String fieldName = field.getName();
            String name = "get" + fieldName.replaceFirst(fieldName.substring(0,1), fieldName.substring(0, 1).toUpperCase());

            Method method = resultParams.getClass().getMethod(name);
            String[] params = String.valueOf(method.invoke(resultParams)).split(",");
            Query query = null;
            if(params.length == 1){
                // 创建查询解析器
                QueryParser queryParser = new QueryParser(field.getName(), new IKAnalyzer());
                query = queryParser.parse(params[0]);

            }else if(params.length == 2){
                // 创建查询解析器
                query = TermRangeQuery.newStringRange(field.getName(), params[0], params[1], true, true);
            }
            list.add(query);
        }
        return list;
    }

    public static void main(String[] args) throws Exception {
        ShenQingResultEntity shenQingResultEntity = new ShenQingResultEntity();
        shenQingResultEntity.setId("100,101");
        shenQingResultEntity.setName("韩高明");
        shenQingResultEntity.setAddress("河南省周口市");

        getQuery(shenQingResultEntity);
    }
}
