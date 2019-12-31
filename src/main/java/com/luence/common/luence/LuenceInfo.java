package com.luence.common.luence;

import com.alibaba.fastjson.JSONObject;
import com.luence.common.entity.SystemParams;
import com.luence.common.utils.FileUtil;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.nio.file.Paths;
import java.util.List;

/**
 * @Description
 * @Author hangaoming
 * @Time 2019/12/30 13:04
 **/
@Component
public class LuenceInfo {

    @Autowired
    private SystemParams systemParams;

    public Object indexCreate(List<Field> list){
        // 创建文档对象
        Document document = new Document();

        JSONObject jsonObject = new JSONObject();

        // 添加字段信息
        if(CollectionUtils.isEmpty(list)){
            jsonObject.put("status", "500");
            jsonObject.put("info", "域(Field)不能为空");
            return jsonObject;
        }
        list.stream().forEach(field -> document.add(field));

        try {
            // 删除旧索引信息，或创建索引地址
            boolean isTure = FileUtil.deleteFile(systemParams.getPath());
            if (isTure) {
                // 创建索引目录对象
                Directory directory = FSDirectory.open(Paths.get(systemParams.getPath()));

                // 创建分词器
                Analyzer analyzer = new StandardAnalyzer();

                // 创建配置对象
                IndexWriterConfig indexWriterConfig = new IndexWriterConfig(analyzer);

                //设置打开索引库读写的方式：CREATE->覆盖原来索引，APPEND->追加到原来的索引。
                indexWriterConfig.setOpenMode(IndexWriterConfig.OpenMode.CREATE);

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
            }
        } catch(Exception e){
            e.printStackTrace();
        }
        jsonObject.put("status", "510");
        jsonObject.put("info", "未知异常,断点调试");
        return jsonObject;
    }
}
