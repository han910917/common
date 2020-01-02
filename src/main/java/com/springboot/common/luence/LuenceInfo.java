package com.springboot.common.luence;

import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.springboot.common.entity.SystemParams;
import com.springboot.common.utils.FileUtil;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.wltea.analyzer.lucene.IKAnalyzer;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * @Description
 * @Author hangaoming
 * @Time 2019/12/30 13:04
 **/
@Component
public class LuenceInfo {

    @Autowired
    private SystemParams systemParams;

    /**
     * 创建索引
     * @author HanGaoMing
     * @Time 2019/12/31 15:18
     * @param list
     * @return java.lang.Object
     */
    public Object indexCreate(List<Document> list){
        JSONObject jsonObject = new JSONObject();

        // 添加字段信息
        if(CollectionUtils.isEmpty(list)){
            jsonObject.put("status", "500");
            jsonObject.put("info", "域(Field)不能为空");
            return jsonObject;
        }

        try {
            // 删除旧索引信息，或创建索引地址
            boolean isTure = FileUtil.deleteFile(systemParams.getPath());
            if (isTure) {
                // 创建索引目录对象
                Directory directory = FSDirectory.open(Paths.get(systemParams.getPath()));

                // 创建IK分词器
                Analyzer analyzer = new IKAnalyzer();

                // 创建配置对象
                IndexWriterConfig indexWriterConfig = new IndexWriterConfig(analyzer);

                //设置打开索引库读写的方式：CREATE->覆盖原来索引，APPEND->追加到原来的索引。
                indexWriterConfig.setOpenMode(IndexWriterConfig.OpenMode.CREATE);

                // 创建索引的写出工具类
                IndexWriter indexWriter = new IndexWriter(directory, indexWriterConfig);

                list.stream().forEach(doc -> {
                    try {
                        // 添加文档
                        indexWriter.addDocument(doc);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });

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

    public List<Map<String, Object>> indexSearch(String key) throws Exception {
        List<Map<String, Object>> list = Lists.newArrayList();
        // 索引目录对象
        Directory directory = FSDirectory.open(Paths.get(systemParams.getPath()));
        // 索引读取工具
        IndexReader indexReader = DirectoryReader.open(directory);
        // 索引搜索工具
        IndexSearcher indexSearcher = new IndexSearcher(indexReader);

        // 创建查询解析器
        QueryParser queryParser = new QueryParser("address", new IKAnalyzer());
        Query query = queryParser.parse(key);

        // 搜索数据
        TopDocs topDocs = indexSearcher.search(query, 10);
        // 获取得分文档对象
        ScoreDoc[] scoreDocs = topDocs.scoreDocs;
        Arrays.stream(scoreDocs).forEach(scoreDoc -> {
            Map<String, Object> map = Maps.newHashMap();
            int docId = scoreDoc.doc;
            try {
                Document document = indexReader.document(docId);
                map.put("name", document.get("name"));
                map.put("price", document.get("price"));
                map.put("address", document.get("address"));
                list.add(map);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        return list;
    }
}
