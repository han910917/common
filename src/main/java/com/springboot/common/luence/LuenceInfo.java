package com.springboot.common.luence;

import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.springboot.common.utils.FileUtil;
import com.springboot.common.utils.QueryUtil;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.*;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.*;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.springframework.util.CollectionUtils;
import org.wltea.analyzer.lucene.IKAnalyzer;

import java.io.IOException;
import java.lang.reflect.Method;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * @Description
 * @Author hangaoming
 * @Time 2019/12/30 13:04
 **/
public class LuenceInfo {

    /**
     * 创建索引
     * @author HanGaoMing
     * @Time 2019/12/31 15:18
     * @param list
     * @return java.lang.Object
     */
    public static Object indexCreate(List<Document> list, String path){
        JSONObject jsonObject = new JSONObject();

        // 添加字段信息
        if(CollectionUtils.isEmpty(list)){
            jsonObject.put("status", "500");
            jsonObject.put("info", "域(Field)不能为空");
            return jsonObject;
        }

        try {
            // 删除旧索引信息，或创建索引地址
            boolean isTure = FileUtil.deleteFile(path);
            if (isTure) {
                // 创建索引目录对象
                Directory directory = FSDirectory.open(Paths.get(path));

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

    /**
     * 普通查询(待优化)
     * @author HanGaoMing
     * @Time 2020/1/7 18:03
     * @return java.util.List<java.util.Map<java.lang.String,java.lang.Object>>
     */
    public static <T> List<Map<String, Object>> indexSearch(String path, T params, T result) throws Exception {
        List<Map<String, Object>> list = Lists.newArrayList();
        Map<String, Object> paramsMap = Maps.newHashMap();
        String key = null;
        try {
            java.lang.reflect.Field[] filed = params.getClass().getDeclaredFields();
            String name = filed[0].getName();
            Method m = params.getClass().getMethod("get" + name.replaceFirst(name.substring(0, 1), name.substring(0, 1).toUpperCase()));
            paramsMap = (Map<String, Object>) m.invoke(params);
        } catch (Exception e) {
            e.printStackTrace();
        }

        List<Query> lists = QueryUtil.getQuery(result);

        // 索引目录对象
        Directory directory = FSDirectory.open(Paths.get(path));
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
                map.put("id", document.get("id"));
                map.put("address", document.get("address"));
                list.add(map);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        return list;
    }

    /**
     * Term(词条)查询(待优化)
     * 词条是做小收缩单元，是不可分割的。所以没有使用分词器进行分词在查询。
     * @author HanGaoMing
     * @Time 2020/1/7 18:06
     * @param 
     * @return java.util.List<java.util.Map<java.lang.String,java.lang.Object>>
     */
    public static List<Map<String, Object>> indexTeamSearch(String key, String path) throws Exception {
        // 1、创建索引目录对象
        Directory directory = FSDirectory.open(Paths.get(path));
        // 2、索引读取工具
        IndexReader indexReader = DirectoryReader.open(directory);
        // 3、索引搜索工具
        IndexSearcher indexSearcher = new IndexSearcher(indexReader);
        // 4、创建查询对象
        Query query = new TermQuery(new Term("address", key));
        // 5、搜索数据
        TopDocs topDocs = indexSearcher.search(query, 10);
        // 6、获取总条数
        long num = topDocs.totalHits;
        // 7、获取得分文档的对象
        ScoreDoc[] scoreDocs = topDocs.scoreDocs;
        // 8、取出文档编号
        List<Map<String, Object>> list = Lists.newArrayList();
        Arrays.stream(scoreDocs).forEach(scoreDoc -> {
            Map<String, Object> map = Maps.newHashMap();
            int docId = scoreDoc.doc;

            try {
                // 9、根据编号去找文档
                Document document = indexReader.document(docId);
                map.put("name", document.get("name"));
                map.put("id", document.get("id"));
                map.put("address", document.get("address"));
                list.add(map);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        return list;
    }

    /**
     * 通配符(WildcardQuery)查询
     * @author HanGaoMing
     * @Time 2020/1/7 18:47
     * @param
     * @return java.util.List<java.util.Map<java.lang.String,java.lang.Object>>
     */
    public static List<Map<String, Object>> indexWildcardQuery(String key, String path) throws Exception {
        // 1、创建索引目录对象
        Directory directory = FSDirectory.open(Paths.get(path));
        // 2、索引读取工具
        IndexReader indexReader = DirectoryReader.open(directory);
        // 3、索引搜索工具
        IndexSearcher indexSearcher = new IndexSearcher(indexReader);
        // 4、创建查询对象
        Query query = new WildcardQuery(new Term("address", key));
        // 5、搜索数据
        TopDocs topDocs = indexSearcher.search(query, 10);
        // 6、获取得分文档的对象
        ScoreDoc[] scoreDocs = topDocs.scoreDocs;
        List<Map<String, Object>> list = Lists.newArrayList();
         Arrays.stream(scoreDocs).forEach(scoreDoc -> {
             Map<String, Object> map = Maps.newHashMap();
            // 7、取出文档编号
             int docId = scoreDoc.doc;
            // 8、根据编号去找文档
             try {
                 Document document = indexReader.document(docId);
                 map.put("name", document.get("name"));
                 map.put("id", document.get("id"));
                 map.put("address", document.get("address"));
                 list.add(map);
             } catch (IOException e) {
                 e.printStackTrace();
             }
         });

        return list;
    }

    /**
     * 模糊查询(FuzzyQuery)
     * @author HanGaoMing
     * @Time 2020/1/7 19:19
     * @param key
     * @param path
     * @return java.util.List<java.util.Map<java.lang.String,java.lang.Object>>
     */
    public static List<Map<String, Object>> indexFuzzyQuery(String key, String path) throws Exception{
        Directory directory = FSDirectory.open(Paths.get(path));

        IndexReader indexReader = DirectoryReader.open(directory);

        IndexSearcher indexSearcher = new IndexSearcher(indexReader);

        Query query = new FuzzyQuery(new Term("address", key), 1);

        TopDocs topDocs = indexSearcher.search(query, 10);

        ScoreDoc[] scoreDocs = topDocs.scoreDocs;

        List<Map<String, Object>> list = Lists.newArrayList();
        Arrays.stream(scoreDocs).forEach(scoreDoc -> {
            Map<String, Object> map = Maps.newHashMap();
            int docId = scoreDoc.doc;
            try {
                Document document = indexReader.document(docId);
                map.put("name", document.get("name"));
                map.put("id", document.get("id"));
                map.put("address", document.get("address"));
                list.add(map);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        return list;
    }

    /**
     * 数值范围查询（NumericRangeQuery）
     * newStringRange(String field, String lowerTerm, String upperTerm, boolean includeLower, boolean includeUpper)
     * includeLower和includeUpper表示是否包含，例：includeLower为true，includeUpper为false时，查询范围为【lowerTerm，upperTerm）
     * @author HanGaoMing
     * @Time 2020/1/7 19:45
     * @param start
     * @param end
     * @param path
     * @return java.util.List<java.util.Map<java.lang.String,java.lang.Object>>
     */
    public static List<Map<String, Object>> indexNumericRangeQuery(Integer start, Integer end, String path) throws Exception {
        Directory directory = FSDirectory.open(Paths.get(path));

        IndexReader indexReader = DirectoryReader.open(directory);

        IndexSearcher indexSearcher = new IndexSearcher(indexReader);

        Query query = TermRangeQuery.newStringRange("id", start.toString(), end.toString(), true, false);

        TopDocs topDocs = indexSearcher.search(query, 10);
        ScoreDoc[] scoreDocs = topDocs.scoreDocs;

        List<Map<String, Object>> list = Lists.newArrayList();
        Arrays.stream(scoreDocs).forEach(scoreDoc -> {
            Map<String, Object> map = Maps.newHashMap();
            int docId = scoreDoc.doc;
            try {
                Document document = indexReader.document(docId);
                map.put("name", document.get("name"));
                map.put("id", document.get("id"));
                map.put("address", document.get("address"));
                list.add(map);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        return list;
    }

    /**
     * 组合查询
     * @author HanGaoMing
     * @Time 2020/1/8 10:53
     * @param key
     * @param start
     * @param end
     * @param path
     * @return java.util.List<java.util.Map<java.lang.String,java.lang.Object>>
     */
    public static List<Map<String, Object>> indexBooleanQuery(String key, Integer start, Integer end, String path) throws Exception{
        Directory directory = FSDirectory.open(Paths.get(path));
        IndexReader indexReader = DirectoryReader.open(directory);

        IndexSearcher indexSearcher = new IndexSearcher(indexReader);

        Query query = new TermQuery(new Term("address", key));
        Query query1= TermRangeQuery.newStringRange("id", start.toString(), end.toString(), true, false);

        BooleanQuery.Builder builder = new BooleanQuery.Builder();
        builder.add(query, BooleanClause.Occur.SHOULD);
        builder.add(query1, BooleanClause.Occur.SHOULD);

        BooleanQuery booleanQuery = builder.build();
        TopDocs topDocs = indexSearcher.search(booleanQuery, 10);
        ScoreDoc[] scoreDocs = topDocs.scoreDocs;

        List<Map<String, Object>> list = Lists.newArrayList();
        Arrays.stream(scoreDocs).forEach(scoreDoc -> {
            Map<String, Object> map = Maps.newHashMap();
            int docId = scoreDoc.doc;
            try {
                Document document = indexReader.document(docId);
                map.put("name", document.get("name"));
                map.put("id", document.get("id"));
                map.put("address", document.get("address"));
                list.add(map);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        return list;
    }

    /**
     * 修改指定id的索引
     * @author HanGaoMing
     * @Time 2020/1/8 17:50
     * @param text
     * @param id
     * @return void
     */
    public static void updateIndex(String text, String id, String path) throws Exception{
        Directory directory = FSDirectory.open(Paths.get(path));
        IndexWriterConfig indexWriterConfig = new IndexWriterConfig(new IKAnalyzer());
        IndexWriter indexWriter = new IndexWriter(directory, indexWriterConfig);
        Document document = new Document();
        document.add(new StringField("id", id, Field.Store.YES));
        document.add(new StringField("name", "郑州大学", Field.Store.YES));
        document.add(new TextField("address", text, Field.Store.YES));

        indexWriter.updateDocument(new Term("id", id), document);
        indexWriter.commit();
        indexWriter.close();
    }

    /**
     * 删除索引基于词条
     * @author HanGaoMing
     * @Time 2020/1/8 18:10
     * @param id
     * @param path
     * @return void
     */
    public static void deleteTermIndex(String id, String path) throws Exception{
        Directory directory = FSDirectory.open(Paths.get(path));
        IndexWriterConfig config = new IndexWriterConfig();
        IndexWriter indexWriter = new IndexWriter(directory, config);

        indexWriter.deleteDocuments(new Term("id", id));
        indexWriter.commit();
        indexWriter.close();
    }

    /**
     * 删除索引基于Query对象
     * @author HanGaoMing
     * @Time 2020/1/8 18:10
     * @param id
     * @param path
     * @return void
     */
    public static void deleteQueryIndex(String id, String path) throws Exception{
        Directory directory = FSDirectory.open(Paths.get(path));
        IndexWriterConfig config = new IndexWriterConfig();
        IndexWriter indexWriter = new IndexWriter(directory, config);

        Query query = TermRangeQuery.newStringRange("id", id, id, true, true);
        indexWriter.deleteDocuments(query);
        indexWriter.commit();
        indexWriter.close();
    }

}
