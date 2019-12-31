package com.luence.common.luence;

import com.alibaba.fastjson.JSONObject;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.util.CollectionUtils;

import java.io.File;
import java.util.List;

/**
 * @Description
 * @Author hangaoming
 * @Time 2019/12/30 13:04
 **/
@PropertySource(value = {"classpath:common.properties"}, encoding = "utf-8")
public class LuenceInfo {

    @Value("${system.path}")
    private static String path;

    public static Object indexCreate(List<Field> list){
        // 创建文档对象
        Document document = new Document();

        JSONObject jsonObject = new JSONObject();

        // 添加字段信息
        if(CollectionUtils.isEmpty(list)){
            jsonObject.put("status", "500");
            jsonObject.put("info", "域(Field不能为空");
            return jsonObject;
        }
        list.stream().forEach(field -> document.add(field));

        try {
            // 创建索引目录对象
            Directory directory = FSDirectory.open(new File(path));

            // 创建分词器
            Analyzer analyzer = new StandardAnalyzer();

            // 创建配置对象
            IndexWriterConfig indexWriterConfig = new IndexWriterConfig(Version.LATEST, analyzer);

            // 创建索引的写出工具类
            IndexWriter indexWriter = new IndexWriter(directory, indexWriterConfig);

            // 添加文档
            indexWriter.addDocument(document);

            // 提交
            indexWriter.commit();
            // 关闭
            indexWriter.close();

            jsonObject.put("status", "200");
            jsonObject.put("info", "创建索引成功");
            return jsonObject;
        } catch (Exception e) {
            e.printStackTrace();
        }
        jsonObject.put("status", "510");
        jsonObject.put("info", "未知异常,断点调试");
        return jsonObject;
    }
}
