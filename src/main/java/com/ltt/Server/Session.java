package com.ltt.Server;



import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
//import jdk.nashorn.internal.ir.BlockLexicalContext;
import com.ltt.DemoApplication;
import com.ltt.Model.Statistics.Statistics;
import com.ltt.Utils.SpringContextUtil;
import com.ltt.config.DataSourceConfig;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.*;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import com.ltt.Model.*;
import com.ltt.Model.Triple.SnippetTriple;
import com.ltt.Model.Triple.TripleName;
import com.ltt.Snippet.KSDEntry;
import com.ltt.Utils.GlobalVariances;
import com.ltt.Utils.JdbcUtil;
import com.ltt.Utils.SQLUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Repository;

import javax.annotation.PostConstruct;
import javax.sql.DataSource;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Paths;
import java.sql.*;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

@Component
public class Session {
    //TODO   全局一个connection！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！
    private Directory indexDir;
    private IndexReader reader;
    private IndexSearcher searcher;
    private Analyzer analyzer;
    private KSDEntry ksdEntry;

    public String sessionId;

    private Logger logger = LoggerFactory.getLogger(Session.class);

    /** overview **/
    private List<JSONObject> namespaces;
    private AtomicBoolean boolnamespace = new AtomicBoolean(false);


    /** data patterns **/
    private List<JSONObject> simplePatterns;
    private AtomicBoolean boolsimplePatterns = new AtomicBoolean(false);

    private List<JSONObject> edpPatterns;
    private AtomicBoolean booledpPatterns = new AtomicBoolean(false);

    private List<JSONObject> lpPatterns;
    private AtomicBoolean boollpPatterns = new AtomicBoolean(false);

    private List<JSONObject> expLOD;
    private AtomicBoolean boolexpLOD = new AtomicBoolean(false);


    /** data samples **/
    private List<List<JSONObject>> topKList;
    private AtomicBoolean booltopKList = new AtomicBoolean(false);
    private List<List<JSONObject>> illustrativeList;
    private AtomicBoolean boolillustrativeList = new AtomicBoolean(false);

    private List<List<List<JSONObject>>> tripleRankList;
    private AtomicBoolean booltripleRank = new AtomicBoolean(false);

    private List<JSONObject> pageRankList;
    private AtomicBoolean  boolpageRank = new AtomicBoolean(false);

    private List<JSONObject> rockerList;
    private AtomicBoolean boolrockerList = new AtomicBoolean(false);

    private List<List<JSONObject>> hitsList;
    private AtomicBoolean boolhits = new AtomicBoolean(false);


    /** explore **/
    private List<JSONObject> exploreClasses;
    private AtomicBoolean boolexploreClasses = new AtomicBoolean(false);
//    private boolean boolexploreClasses = false;
    private List<JSONObject> exploreProperties;
    private AtomicBoolean boolexploreProperties = new AtomicBoolean(false);


    /** metdata **/
    private JSONObject basicinfo;
    private AtomicBoolean boolbasicinfo = new AtomicBoolean(false);
    private List<JSONObject> resources;
    private AtomicBoolean boolresources = new AtomicBoolean(false);
    private List<JSONObject> extras;
    private AtomicBoolean boolextras = new AtomicBoolean(false);


    class BasicInfoThread extends Thread{
        private int table_id;
        private int dataset_local_id;

        public BasicInfoThread(int table_id, int dataset_local_id) {
            this.table_id = table_id;
            this.dataset_local_id = dataset_local_id;
            basicinfo = new JSONObject();
        }

        @Override
        public void run() {
            synchronized (boolbasicinfo){
                try{
                    Connection connection = DemoApplication.primaryDataSource.getConnection();
                    String sql = String.format("SELECT * FROM dataset%d WHERE local_id=%d;",table_id,dataset_local_id);
                    try{
                        PreparedStatement pst = connection.prepareStatement(sql);
                        ResultSet rst = pst.executeQuery();

                        while(rst.next()){
                            basicinfo.put("author",rst.getString("author"));
                            basicinfo.put("author_email",rst.getString("author_email"));
                            basicinfo.put("metadata_created",rst.getString("metadata_created").substring(0,10));
                            basicinfo.put("maintainer",rst.getString("maintainer"));
                            basicinfo.put("maintainer_email",rst.getString("maintainer_email"));
                            basicinfo.put("name",rst.getString("name"));
                            basicinfo.put("metadata_modified",rst.getString("metadata_modified").substring(0,10));
                            basicinfo.put("notes",rst.getString("notes"));
                            basicinfo.put("org_title",rst.getString("org_title"));
                            basicinfo.put("title",rst.getString("title"));
                            basicinfo.put("state",rst.getString("state"));
                            basicinfo.put("num_tags",rst.getString("num_tags"));
                            basicinfo.put("creator_user_id",rst.getString("creator_user_id"));
                            basicinfo.put("org_approval_status",rst.getString("org_approval_status"));
                            basicinfo.put("org_created",rst.getString("org_created"));
                            basicinfo.put("org_state",rst.getString("org_state"));
                            basicinfo.put("org_description",rst.getString("org_description"));
                            basicinfo.put("revision_id",rst.getString("revision_id"));
                            basicinfo.put("org_image_url",rst.getString("org_image_url"));
                            basicinfo.put("version",rst.getString("version"));
                            basicinfo.put("org_revision_id",rst.getString("org_revision_id"));
                            basicinfo.put("license_title",rst.getString("license_title"));
                            basicinfo.put("license_id",rst.getString("license_id"));


                        }

                        rst.close();pst.close();

                    }catch(Exception e){
                        e.printStackTrace();
                    }
                    boolbasicinfo.set(true);
                    boolbasicinfo.notify();
                    logger.info("basic info end!");



                }catch(Exception e){
                    e.printStackTrace();
                }
            }
        }
    }

    class ResourceThread extends Thread{
        private int table_id;
        private int dataset_local_id;

        public ResourceThread(int table_id, int dataset_local_id) {
            this.table_id = table_id;
            this.dataset_local_id = dataset_local_id;
            resources = new ArrayList<>();
        }

        @Override
        public void run() {
            synchronized (boolresources){

                String sql = String.format("SELECT * FROM resource%d WHERE dataset_local_id=%d;",table_id,dataset_local_id);
                try{
                    Connection connection = DemoApplication.primaryDataSource.getConnection();
                    PreparedStatement pst = connection.prepareStatement(sql);
                    ResultSet rst = pst.executeQuery();

                    while(rst.next()){
                        JSONObject jsonObject = new JSONObject();
                        jsonObject.put("mimetype",rst.getString("mimetype"));
                        jsonObject.put("cache_url",rst.getString("cache_url"));
                        jsonObject.put("hash",rst.getString("hash"));
                        jsonObject.put("description",rst.getString("description"));
                        jsonObject.put("name",rst.getString("name"));
                        jsonObject.put("format",rst.getString("format"));
                        jsonObject.put("url",rst.getString("url"));
                        jsonObject.put("cache_last_updated",rst.getString("cache_last_updated"));
                        jsonObject.put("package_id",rst.getString("package_id"));
                        jsonObject.put("created",rst.getString("created"));
                        jsonObject.put("state",rst.getString("state"));
                        jsonObject.put("mimetype_inner",rst.getString("mimetype_inner"));
                        jsonObject.put("last_modified",rst.getString("last_modified"));
                        jsonObject.put("position",rst.getString("position"));
                        jsonObject.put("revision_id",rst.getString("revision_id"));
                        jsonObject.put("url_type",rst.getString("url_type"));
                        jsonObject.put("resource_type",rst.getString("resource_type"));
                        jsonObject.put("downloaded",rst.getString("downloaded"));
                        jsonObject.put("data_source",rst.getString("data_source"));


                        resources.add(jsonObject);


                    }

                }catch(Exception e){
                    e.printStackTrace();
                }
                boolresources.set(true);
                boolresources.notify();
            }

        }
    }

    class ExtraThread extends Thread{
        private int table_id;
        private int dataset_local_id;

        public ExtraThread(int table_id, int dataset_local_id) {
            this.table_id = table_id;
            this.dataset_local_id = dataset_local_id;
            extras = new ArrayList<>();
        }

        @Override
        public void run() {
            synchronized (boolextras){
                try{
                    Connection connection = DemoApplication.primaryDataSource.getConnection();
                    String sql = String.format("SELECT * FROM extra%d WHERE dataset_local_id=%d;",table_id,dataset_local_id);
                    try{
                        PreparedStatement pst = connection.prepareStatement(sql);
                        ResultSet rst = pst.executeQuery();


                        while(rst.next()){

                            String key = rst.getString("key");
                            if(!"links".equals(key.substring(0,5))){
                                JSONObject jsonObject = new JSONObject();
                                jsonObject.put("id",key);
                                jsonObject.put("value",rst.getString("value"));
                                extras.add(jsonObject);
                            }

                        }

                        rst.close();pst.close();

                    }catch(Exception e){
                        e.printStackTrace();
                    }
                    boolextras.set(true);
                    boolextras.notify();



                }catch (Exception e){
                    e.printStackTrace();
                }

            }
        }
    }




    class SimplePatternThread extends Thread {
        private int table_id;
        private int dataset_local_id;

        public SimplePatternThread(int table_id, int dataset_local_id) {
            this.table_id = table_id;
            this.dataset_local_id = dataset_local_id;
            simplePatterns = new ArrayList<>();
        }

        @Override
        public void run() {
            int table_id=2;
            if(dataset_local_id>312) table_id=3;



            //访问数据库或许迁移至单独类
//            List<JSONObject> result = new ArrayList<>();


            synchronized (boolsimplePatterns){
                try
                {
                    Connection connection = DemoApplication.primaryDataSource.getConnection();
                    String sql = String.format("SELECT * FROM panda_result2 WHERE dataset_local_id=%d;", dataset_local_id);
                    PreparedStatement st = connection.prepareStatement(sql);
                    ResultSet rs = st.executeQuery();
                    while(rs.next())//只能取出来一条
                    {


                        String str = rs.getString("knowledge_pattern");
                        String[] strPatterns = str.split(";");//76 2 (5)     69 74 2 (4)
                        for(int i=0;i<strPatterns.length;i++){
                            String onePattern = strPatterns[i];
//                        System.out.println("onePattern:"+onePattern);
                            JSONObject oneJSON = new JSONObject();
                            List<JSONObject> oneResult = new ArrayList<>();
                            int pos = onePattern.indexOf("(");  //onePattern:76 2 (5)


                            String []before = onePattern.substring(0,pos).trim().split(" ");
                            for(String oneId:before){
                                JSONObject pattern = new JSONObject();
//                            System.out.println("oneID:"+oneId);

                                //需要getid对应的string
                                String name = SQLUtil.getLabelForId(table_id,dataset_local_id,Integer.parseInt(oneId));
//                            System.out.println(name);
                                pattern.put("name",name);
                                pattern.put("type",0);
//                        System.out.println(pattern);

                                oneResult.add(pattern);

                            }

                            int count = Integer.parseInt(onePattern.substring(pos+1,onePattern.length()-1));

                            oneJSON.put("children",oneResult);
                            oneJSON.put("name","pattern"+i);
                            oneJSON.put("count",count);

                            simplePatterns.add(oneJSON);

                        }

                        Collections.sort(simplePatterns, new Comparator<JSONObject>() {

                            @Override
                            public int compare(JSONObject o1, JSONObject o2) {


                                return o2.getLong("count").compareTo(o1.getLong("count"));
                            }
                        });


                    }



                    rs.close(); st.close();
                } catch (SQLException throwables)
                {
                    throwables.printStackTrace();
                }
                boolsimplePatterns.set(true);
                boolsimplePatterns.notify();
            }

        }
    }

    class EDPThread extends Thread {
        private int table_id;
        private int dataset_local_id;

        public EDPThread(int table_id, int dataset_local_id) {
            this.table_id = table_id;
            this.dataset_local_id = dataset_local_id;
            edpPatterns = new ArrayList<>();
        }

        @Override
        public void run() {
            int tableid = 2;

            if(dataset_local_id>311){
                dataset_local_id-=311;
                tableid=3;
            }
            String indexReadPath = "D:\\Index\\EDPIndex202010\\";

            synchronized (booledpPatterns){
                try {
                    Directory directory = FSDirectory.open(Paths.get(indexReadPath+dataset_local_id));//打开索引文件夹
                    IndexReader reader = DirectoryReader.open(directory);//读取目录
                    int edpSum=0;
                    for(int i=0;i<reader.maxDoc();i++){
                        edpSum+=Integer.parseInt(reader.document(i).get("count"));
                    }

                    //一个pattern一个document
                    for(int i=0;i<reader.maxDoc();i++){//从0开始从1开始？？？？
                        JSONObject onePattern = new JSONObject();
                        List<JSONObject> children = new ArrayList<>();

                        Document doc = reader.document(i);
                        //只有一项和空length都为1
                        String strOutProperty = doc.get("outProperty");
                        if(strOutProperty.length()!=0){
                            String [] outProperty = strOutProperty.trim().split(" ");
                            for(int j=0;j<outProperty.length;j++){
                                JSONObject jsonObject = new JSONObject();
//                        System.out.println(outProperty[j]);
                                String name = SQLUtil.getLabelForId(tableid,dataset_local_id,Integer.parseInt(outProperty[j]));
                                jsonObject.put("name",name);

                                jsonObject.put("type",0);
                                jsonObject.put("inOrOut",1);

                                children.add(jsonObject);

                            }
                        }

                        String strInProperty = doc.get("inProperty");
                        if(strInProperty.length()!=0){
                            String [] inProperty = strInProperty.trim().split(" "); //很可能没有进入的或出去的

                            for(int j=0;j<inProperty.length;j++){

                                JSONObject jsonObject = new JSONObject();
                                jsonObject.put("name",SQLUtil.getLabelForId(tableid,dataset_local_id,Integer.parseInt(inProperty[j])));
                                jsonObject.put("type",0);
                                jsonObject.put("inOrOut",0);

                                children.add(jsonObject);

                            }
                        }

                        String strClasses = doc.get("classes");
                        if(strClasses.length()!=0){
                            String [] classes = strClasses.trim().split(" ");
                            for(int j=0;j<classes.length;j++){
                                JSONObject jsonObject = new JSONObject();
                                jsonObject.put("name",SQLUtil.getLabelForId(tableid,dataset_local_id,Integer.parseInt(classes[j])));
                                jsonObject.put("type",1);
                                jsonObject.put("inOrOut",1);

                                children.add(jsonObject);

                            }

                        }
                        onePattern.put("children",children);

                        String count = doc.get("count");
//                System.out.println("count:"+count);

                        onePattern.put("count",(double)Integer.parseInt(count)/edpSum);
                        onePattern.put("name","pattern"+i);

                        edpPatterns.add(onePattern);
                    }

                    Collections.sort(edpPatterns, new Comparator<JSONObject>() {

                        @Override
                        public int compare(JSONObject o1, JSONObject o2) {
//                    int count1 = Integer.parseInt(o1.get("count").toString());
//                    int count2 = Integer.parseInt(o2.get("count").toString());

                            return o2.getDouble("count").compareTo(o1.getDouble("count"));
                        }
                    });


                    reader.close();//关闭资源
                    directory.close();//关闭连接
                }catch(Exception e)    {
                    e.printStackTrace();
                }
                System.out.println(edpPatterns);
                logger.info("EDPPattern end!");
                booledpPatterns.set(true);
                booledpPatterns.notify();

            }
        }
    }

    class LPThread extends Thread{
        private int table_id;
        private int dataset_local_id;

        public LPThread(int table_id, int dataset_local_id) {
            this.table_id = table_id;
            this.dataset_local_id = dataset_local_id;
            lpPatterns = new ArrayList<>();
        }

        @Override
        public void run() {
            int tableid = 2;

            if(dataset_local_id>311){
                dataset_local_id-=311;
                tableid=3;
            }
            String indexReadPath = "D:\\Index\\LPIndex202010\\";
            synchronized (boollpPatterns){

                List<JSONObject> fromResult = new ArrayList<>();
                List<JSONObject> toResult = new ArrayList<>();



                try {
                    Directory directory = FSDirectory.open(Paths.get(indexReadPath+dataset_local_id));//打开索引文件夹
                    IndexReader reader = DirectoryReader.open(directory);//读取目录

                    int lpSum=0;
                    for(int i=0;i<reader.maxDoc();i++){
                        lpSum+=Integer.parseInt(reader.document(i).get("count"));
                    }
                    //一个pattern一个document
                    for(int i=0;i<reader.maxDoc();i++){//从0开始从1开始？？？？
                        JSONObject resultOne = new JSONObject();



                        Document doc = reader.document(i);

                        int propertyID = Integer.parseInt(doc.get("propertyID"));
                        String count = doc.get("count");

//                onePattern.put("name",count);

                        JSONObject onePattern = new JSONObject();
                        List<JSONObject> children = new ArrayList<>();

                        String strFromInProperty = doc.get("fromInProperty");
                        if(strFromInProperty.length()!=0){
                            String [] fromInProperty = strFromInProperty.trim().split(" ");
                            for(int j=0;j<fromInProperty.length;j++){

                                JSONObject jsonObject = new JSONObject();
                                jsonObject.put("name",SQLUtil.getLabelForId(tableid,dataset_local_id,Integer.parseInt(fromInProperty[j])));
                                jsonObject.put("type",0);
                                jsonObject.put("inOrOut",0);

                                children.add(jsonObject);
                            }
                        }

                        String strFromOutProperty = doc.get("fromOutProperty");
                        if(strFromOutProperty.length()!=0){
                            String [] fromOutProperty = strFromOutProperty.trim().split(" ");
                            for(int j=0;j<fromOutProperty.length;j++){
                                if(Integer.parseInt(fromOutProperty[j])==propertyID) continue;
                                JSONObject jsonObject = new JSONObject();
                                jsonObject.put("name",SQLUtil.getLabelForId(tableid,dataset_local_id,Integer.parseInt(fromOutProperty[j])));
                                jsonObject.put("type",0);
                                jsonObject.put("inOrOut",1);

                                children.add(jsonObject);

                            }
                        }

                        String strFromClasses = doc.get("fromClasses");
                        if(strFromClasses.length()!=0){
                            String [] fromClasses = strFromClasses.trim().split(" ");
                            for(int j=0;j<fromClasses.length;j++){
                                JSONObject jsonObject = new JSONObject();
                                jsonObject.put("name",SQLUtil.getLabelForId(tableid,dataset_local_id,Integer.parseInt(fromClasses[j])));
                                jsonObject.put("type",1);
                                jsonObject.put("inOrOut",0);

                                children.add(jsonObject);

                            }

                        }
                        onePattern.put("children",children);
                        onePattern.put("name","pattern"+i);

                        fromResult.add(onePattern);


                        JSONObject twoPattern = new JSONObject();
                        List<JSONObject> children2 = new ArrayList<>();

                        String strToInProperty = doc.get("toInProperty");
                        if(strToInProperty.length()!=0){
                            String [] toInProperty = strToInProperty.trim().split(" ");
                            for(int j=0;j<toInProperty.length;j++){
                                if(Integer.parseInt(toInProperty[j])==propertyID) continue;
                                JSONObject jsonObject = new JSONObject();
                                jsonObject.put("name",SQLUtil.getLabelForId(tableid,dataset_local_id,Integer.parseInt(toInProperty[j])));
                                jsonObject.put("type",0);
                                jsonObject.put("inOrOut",0);

                                children2.add(jsonObject);

                            }
                        }

                        String strToOutProperty = doc.get("toOutProperty");
                        if(strToOutProperty.length()!=0){
                            String [] toOutProperty = strToOutProperty.trim().split(" ");
                            for(int j=0;j<toOutProperty.length;j++){
                                JSONObject jsonObject = new JSONObject();
                                jsonObject.put("name",SQLUtil.getLabelForId(tableid,dataset_local_id,Integer.parseInt(toOutProperty[j])));
                                jsonObject.put("type",0);
                                jsonObject.put("inOrOut",1);

                                children2.add(jsonObject);

                            }
                        }

                        String strToClasses = doc.get("toClasses");
                        if(strToClasses.length()!=0){
                            String [] toClasses = strToClasses.trim().split(" ");
                            for(int j=0;j<toClasses.length;j++){
                                JSONObject jsonObject = new JSONObject();
                                jsonObject.put("name",SQLUtil.getLabelForId(tableid,dataset_local_id,Integer.parseInt(toClasses[j])));
                                jsonObject.put("type",1);
                                jsonObject.put("inOrOut",0);

                                children2.add(jsonObject);

                            }

                        }

                        twoPattern.put("children",children2);
                        twoPattern.put("name","pattern"+i);
//                toResult.add(twoPattern);

                        resultOne.put("onePattern",onePattern);
                        resultOne.put("twoPattern",twoPattern);
                        resultOne.put("count",(double)Integer.parseInt(count)/lpSum);
//                resultOne.put("name","pattern"+i);
//                System.out.println(i);
                        resultOne.put("property",SQLUtil.getLabelForId(tableid,dataset_local_id,propertyID));

                        lpPatterns.add(resultOne);

                    }

                    Collections.sort(lpPatterns, new Comparator<JSONObject>() {

                        @Override
                        public int compare(JSONObject o1, JSONObject o2) {
                            return o2.getDouble("count").compareTo(o1.getDouble("count"));
                        }
                    });
                    reader.close();//关闭资源
                    directory.close();//关闭连接
                }catch(Exception e)    {
                    e.printStackTrace();
                }
                boollpPatterns.set(true);
                logger.info("LPPattern end!");
                boollpPatterns.notify();
            }

        }
    }

    class ExpLODThread extends Thread{
        private int dataset_local_id;
        private int tableid;

        public ExpLODThread(int tableid,int dataset_local_id) {
            this.tableid=tableid;
            this.dataset_local_id = dataset_local_id;
            expLOD = new ArrayList<>();
        }

        @Override
        public void run() {

            String indexReadPath = "D:\\Index\\ExpLOD\\";

            synchronized (boolexpLOD){
                try {
                    Directory directory = FSDirectory.open(Paths.get(indexReadPath+tableid+"\\"+dataset_local_id));//打开索引文件夹
                    IndexReader reader = DirectoryReader.open(directory);//读取目录

                    //一个instance组即一个feature组是一个document
                    for(int i=0;i<reader.maxDoc();i++) {
                        JSONObject oneExpLOD = new JSONObject();

                        Document doc = reader.document(i);

                        String instanceStr = doc.get("instanceStr");
                        String property = doc.get("property");//是字符串连接，不是id
                        String classes = doc.get("class");
                        oneExpLOD.put("name",instanceStr);
                        List<JSONObject> childrens = new ArrayList<>();

                        String[] propertyArr = property.split(" \\*\\*\\* ");
                        String[] classArr = classes.split("\\*\\*\\*");

                        for(String ss:propertyArr){
                            JSONObject jsonObject = new JSONObject();
                            jsonObject.put("name",ss);


                            if("P/rdf:type".equals(ss) && classArr.length>0){ //只能有一个type！！！！！！！！！！！！！！！！！！！！！！！！！
                                List<JSONObject> tt = new ArrayList<>();
                                for(String sss:classArr){
                                    JSONObject xiao = new JSONObject();
                                    xiao.put("name",sss);
                                    tt.add(xiao);
                                }
                                jsonObject.put("children",tt);
                            }

                            childrens.add(jsonObject);
                        }

                        oneExpLOD.put("children",childrens);

                        expLOD.add(oneExpLOD);
                    }

                    reader.close();//关闭资源
                    directory.close();//关闭连接
                }catch(Exception e)    {
                    e.printStackTrace();
                }
                boolexpLOD.set(true);
                boolexpLOD.notify();
            }

        }
    }

    class namespaceThread extends Thread {
        private int table_id;
        private int dataset_local_id;

        public namespaceThread(int table_id, int dataset_local_id) {
            this.table_id = table_id;
            this.dataset_local_id = dataset_local_id;
        }

        @Override
        public void run() {
            namespaces = new ArrayList<>();

            /** test**/
            synchronized (boolnamespace){
                try{
                    Directory directory = FSDirectory.open(Paths.get("D:\\Index\\Namespace\\"+table_id+"\\"+dataset_local_id));//打开索引文件夹
                    IndexReader reader = DirectoryReader.open(directory);//读取目录

//            IndexSearcher searcher = new IndexSearcher(reader);
//            QueryParser parser = new QueryParser("content",new StandardAnalyzer());
//            Query query = parser.parse("aaa");
//            TopDocs topDocs = searcher.search(query,new ArrayList<Integer>(),new Sort(new SortField()))
                    //n就设置为reader.max
//            TopDocs topDocs = searcher.search(new MatchAllDocsQuery(),)


                    for(int i=0;i<reader.maxDoc();i++){
                        Document doc = reader.document(i);

                        JSONObject jsonObject = new JSONObject();

                        jsonObject.put("vocab",doc.get("vocabulary"));
                        jsonObject.put("prefix",doc.get("prefix"));

                        namespaces.add(jsonObject);
                    }

                    reader.close();
                    directory.close();

                }catch (Exception e){
                    e.printStackTrace();
                }

                boolnamespace.set(true);
                boolnamespace.notify();
            }


        }
    }

    class TopKThread extends Thread{
        private int dataset_local_id;
        private int tableid;

        public TopKThread(int tableid,int dataset_local_id) {
            this.dataset_local_id = dataset_local_id;
            this.tableid=tableid;
            topKList = new ArrayList<>();
        }

        @Override
        public void run() {

            synchronized (booltopKList){
                List<JSONObject> ins = new ArrayList<>();
                List<JSONObject> outs = new ArrayList<>();




                String sql_in = String.format("SELECT * FROM indegree_count%d WHERE dataset_local_id=%d ORDER BY indegree_count DESC LIMIT 20",tableid,dataset_local_id);
                String sql_out = String.format("SELECT * FROM outdegree_count%d WHERE dataset_local_id=%d ORDER BY outdegree_count DESC LIMIT 20",tableid,dataset_local_id);
                //limit 100

                try{
                    Connection connection = DemoApplication.secondDataSource.getConnection();
                    PreparedStatement pst = connection.prepareStatement(sql_in);
                    ResultSet rst_in = pst.executeQuery();

                    while(rst_in.next()){
                        JSONObject jsonObject = new JSONObject();

                        jsonObject.put("entity",rst_in.getString("entity_id"));
                        jsonObject.put("count",rst_in.getInt("indegree_count"));

                        ins.add(jsonObject);
                    }
                   topKList.add(ins);

                    pst = connection.prepareStatement(sql_out);
                    ResultSet rst_pro = pst.executeQuery();
                    while(rst_pro.next()){
                        JSONObject jsonObject = new JSONObject();

                        jsonObject.put("entity",rst_pro.getString("entity_id"));
                        jsonObject.put("count",rst_pro.getString("outdegree_count"));

                        outs.add(jsonObject);
                    }
                    topKList.add(outs);


                    rst_in.close();rst_pro.close();
                    pst.close();
                    connection.close();


                }catch (Exception e){
                    e.printStackTrace();
                }
                booltopKList.set(true);
                booltopKList.notify();

            }
        }
    }

    class hitsThread extends Thread{
        private int dataset_local_id;
        private int tableid;

        public hitsThread(int tableid,int dataset_local_id) {
            this.tableid = tableid;
            this.dataset_local_id = dataset_local_id;
            hitsList = new ArrayList<>();
        }

        @Override
        public void run() {

            synchronized (boolhits){
                List<JSONObject> res1 = new ArrayList<>();
                List<JSONObject> res2 = new ArrayList<>();

                String sqlhub = String.format("SELECT * FROM hits%d WHERE dataset_local_id=%d AND hub is not null ORDER BY hub DESC LIMIT 20",tableid,dataset_local_id);
                String sqlauthority = String.format("SELECT * FROM hits%d WHERE dataset_local_id=%d AND authority is not null ORDER BY authority DESC LIMIT 20",tableid,dataset_local_id);
                //limit 100

                try{
                    Connection connection = DemoApplication.secondDataSource.getConnection();
                    PreparedStatement pst = connection.prepareStatement(sqlhub);
                    ResultSet rsthub = pst.executeQuery();

                    while(rsthub.next()){
                        JSONObject jsonObject = new JSONObject();

                        jsonObject.put("entity",rsthub.getString("entity"));
                        jsonObject.put("hub",rsthub.getString("hub"));

                        res1.add(jsonObject);
                    }
                    hitsList.add(res1);
                    rsthub.close();

                    pst = connection.prepareStatement(sqlauthority);
                    ResultSet rstau = pst.executeQuery();

                    while(rstau.next()){
                        JSONObject jsonObject = new JSONObject();

                        jsonObject.put("entity",rstau.getString("entity"));
                        jsonObject.put("authority",rstau.getString("authority"));

                        res2.add(jsonObject);
                    }
                    hitsList.add(res2);
                    rstau.close();

                    pst.close();

                }catch (Exception e){
                    e.printStackTrace();
                }
                boolhits.set(true);
                boolhits.notify();

            }
        }
    }

    class tripleRankThread extends Thread{
        private int dataset_local_id;
        private int tableid;

        public tripleRankThread(int tableid,int dataset_local_id) {
            this.tableid = tableid;
            this.dataset_local_id = dataset_local_id;
            tripleRankList = new ArrayList<>();
        }

        @Override
        public void run() {


            synchronized (booltripleRank){
                String indexReadPath = "D:\\Index\\TripleRankResult\\"+dataset_local_id+"\\";

                try {
                    File file = new File(indexReadPath);
                    if(file.exists()){

                        String []str = file.list();//获取该目录下所有文件
                        for(int i=1;i<=str.length;i++){
                            System.out.println(i);
                            List<List<JSONObject>> oneRsult = new ArrayList<>();

                            List<JSONObject> entities = new ArrayList<>();
                            List<JSONObject> relations = new ArrayList<>();

                            File entity = new File(indexReadPath+String.valueOf(i)+"\\entity.txt");
                            String tempString = "";
                            BufferedReader reader = new BufferedReader(new FileReader(entity));
                            while ((tempString = reader.readLine()) != null) {
                                JSONObject entityJ = new JSONObject();
//                        System.out.println(tempString);
                                int pos = tempString.trim().indexOf('\t');

                                String entity_id = SQLUtil.getURIForId(tableid,dataset_local_id,Integer.parseInt(tempString.substring(0,pos)));
                                String entity_score = tempString.substring(pos+1);
                                entityJ.put("entity",entity_id);
                                entityJ.put("score",entity_score);

                                entities.add(entityJ);

                            }

                            File relation = new File(indexReadPath+String.valueOf(i)+"\\relation.txt");

                            reader = new BufferedReader(new FileReader(relation));
                            while ((tempString = reader.readLine()) != null) {
                                JSONObject relationJ = new JSONObject();
//                        System.out.println(tempString);
                                int pos = tempString.trim().indexOf('\t');

                                String relation_id = SQLUtil.getURIForId(tableid,dataset_local_id,Integer.parseInt(tempString.substring(0,pos)));
                                String entity_score = tempString.substring(pos+1);
                                relationJ.put("relation",relation_id);
                                relationJ.put("score",entity_score);

                                relations.add(relationJ);

                            }

                            oneRsult.add(entities);oneRsult.add(relations);
                            tripleRankList.add(oneRsult);

                            reader.close();
                        }

                    }else{
                        //不存在就是没有这个dataset

                    }

                }catch(Exception e)    {
                    e.printStackTrace();
                }
                booltripleRank.set(true);
                booltripleRank.notify();

            }
        }
    }

    class pageRankThread extends Thread{
        private int dataset_local_id;
        private int tableid;

        public pageRankThread(int tableid,int dataset_local_id) {
            this.dataset_local_id = dataset_local_id;
            this.tableid = tableid;
            pageRankList = new ArrayList<>();
        }

        @Override
        public void run() {

            synchronized (boolpageRank){
                String sql = String.format("SELECT * FROM pagerank%d WHERE dataset_local_id=%d LIMIT 20",tableid,dataset_local_id);
                //limit 100

                try{
                    Connection connection = DemoApplication.secondDataSource.getConnection();
                    PreparedStatement pst = connection.prepareStatement(sql);
                    ResultSet rst = pst.executeQuery();

                    while(rst.next()){
                        JSONObject jsonObject = new JSONObject();

                        jsonObject.put("entity",rst.getString("entity_id"));
                        jsonObject.put("score",rst.getString("score"));

                        pageRankList.add(jsonObject);
                    }

                    rst.close();
                    pst.close();


                }catch (Exception e){
                    e.printStackTrace();
                }

                boolpageRank.set(true);
                boolpageRank.notify();

            }
        }
    }

    class rockerThread extends Thread{
        private int dataset_local_id;
        private int tableid;

        public rockerThread(int tableid,int dataset_local_id) {
            this.dataset_local_id = dataset_local_id;
            this.tableid = tableid;
            rockerList = new ArrayList<>();
        }

        @Override
        public void run() {

            synchronized (boolrockerList){

                String indexReadPath = "D:\\Index\\ROCKER\\"+tableid+"\\";

                try {
                    File file = new File(indexReadPath);
                    if (file.exists()) {


                        String[] str = file.list();//获取该目录下所有文件,一个文件对应一个class
                        for (int i = 1; i <= str.length; i++) {
                            JSONObject oneClass = new JSONObject();

                            File oneClassFile = new File(indexReadPath+ i +".txt");

                            String tempString = "";
                            BufferedReader reader = new BufferedReader(new FileReader(oneClassFile));
                            String classID = reader.readLine();
                            String classStr = SQLUtil.getPrefixAndLabelForId(tableid,dataset_local_id,Integer.parseInt(classID));
                            oneClass.put("class",classStr);

                            List<JSONObject> list = new ArrayList<>();
                            while ((tempString = reader.readLine()) != null){

                                int colonPos = tempString.indexOf(":");
                                String[] properties = tempString.substring(0,colonPos).split("\t");
                                String score = tempString.substring(colonPos+1);

                                JSONObject oneLine = new JSONObject();
                                oneLine.put("oneProperty",SQLUtil.getPrefixAndLabelForId(tableid,dataset_local_id,Integer.parseInt(properties[0])));
                                if(properties.length>1) {
                                    List<String> propertiesStr = new ArrayList<>();
                                    for(int j=1;j<properties.length;j++){
                                        propertiesStr.add(SQLUtil.getPrefixAndLabelForId(tableid,dataset_local_id,Integer.parseInt(properties[j])));
                                    }
                                    oneLine.put("property",propertiesStr);
                                }
                                else oneLine.put("property",null);

                                oneLine.put("score",score);
                                oneLine.put("count",properties.length);
                                list.add(oneLine);


                            }
                            oneClass.put("list",list);
                            rockerList.add(oneClass);
                            reader.close();
                        }

                    }else{
                        //不存在就是没有这个dataset

                    }

                }catch(Exception e)    {
                    e.printStackTrace();
                }
                boolrockerList.set(true);
                boolrockerList.notify();
                logger.info("ROCKER end!");

            }
        }
    }

    class illustrativeThread extends Thread{
        private int dataset_local_id;


        public illustrativeThread(int dataset_local_id) {
            this.dataset_local_id = dataset_local_id;

            illustrativeList = new ArrayList<>();
        }

        @Override
        public void run() {
            synchronized (boolillustrativeList){
                if(ksdEntry==null){
                    ksdEntry = new KSDEntry();//每一个getSnippet都需要这一句
                }

                List<SnippetTriple> illustrativeSnippet = ksdEntry.getDbSnippet(dataset_local_id);

                Set<JSONObject> nodes = new HashSet<>();
                Set<JSONObject> links = new HashSet<>();


                for(SnippetTriple kv : illustrativeSnippet)
                {
                    TripleName tmp =  kv.toTripleName();
                    JSONObject sub = new JSONObject();
                    sub.put("name",tmp.getSub());
                    sub.put("type",tmp.getSub_type());
                    nodes.add(sub);


                    JSONObject ob = new JSONObject();
                    ob.put("name",tmp.getObj());
                    ob.put("type",tmp.getObj_type());
                    nodes.add(ob);

                    JSONObject pre = new JSONObject();
                    pre.put("source",tmp.getSub());
                    pre.put("target",tmp.getObj());
                    pre.put("relation",tmp.getPre());
                    pre.put("uri",tmp.getPre_uri());
                    pre.put("type",tmp.getPre_type());
                    links.add(pre);
                    //TODO   直接在这里更新成D3

                }
                illustrativeList.add(new ArrayList<>(nodes));
                illustrativeList.add(new ArrayList<>(links));

                boolillustrativeList.set(true);
                boolillustrativeList.notify();

            }
        }
    }

    class explorePropertiesThread extends Thread{
        private int dataset_local_id;
        private int tableid;

        public explorePropertiesThread( int tableid,int dataset_local_id) {
            this.dataset_local_id = dataset_local_id;
            this.tableid = tableid;
            exploreProperties = new ArrayList<>();
        }

        @Override
        public void run() {
            synchronized (boolexploreProperties){
                try{
                    String indexReadPathProperty = "D:\\Index\\Filter\\"+tableid+"\\property\\";
                    Directory directoryProperty = FSDirectory.open(Paths.get(indexReadPathProperty+String.valueOf(dataset_local_id)));//打开索引文件夹
                    IndexReader reader = DirectoryReader.open(directoryProperty);//读取目录


                    for(int i=0;i<reader.maxDoc();i++){//从0开始从1开始？？？？

                        //一个group一个doucument
                        Document doc = reader.document(i);
                        String property = doc.get("property");

                        JSONObject jsonObject = new JSONObject();
                        jsonObject.put("property",SQLUtil.getPrefixAndLabelForId(tableid,dataset_local_id,Integer.parseInt(property)));
                        jsonObject.put("id",Integer.parseInt(property));

                        exploreProperties.add(jsonObject);
                    }

                    reader.close();
                    directoryProperty.close();
                }catch(Exception e){
                    e.printStackTrace();
                }
                boolexploreProperties.set(true);
                boolexploreProperties.notify();
            }
        }
    }

    class exploreClassesThread extends Thread{
        private int dataset_local_id;
        private int tableid;

        public exploreClassesThread(int tableid,int dataset_local_id) {
            this.dataset_local_id = dataset_local_id;
            this.tableid = tableid;
            exploreClasses = new ArrayList<>();
        }

        @Override
        public void run() {
            synchronized (this){
                try{

                    String indexReadPathClass = "D:\\Index\\Filter\\"+tableid+"\\class\\";

                    try {

                        Directory directoryClass = FSDirectory.open(Paths.get(indexReadPathClass+String.valueOf(dataset_local_id)));//打开索引文件夹
                        IndexReader reader = DirectoryReader.open(directoryClass);




                        for(int i=0;i<reader.maxDoc();i++){//从0开始从1开始？？？？

                            //一个group一个doucument
                            Document doc = reader.document(i);
                            String property = doc.get("class");

                            JSONObject jsonObject = new JSONObject();
                            jsonObject.put("class",SQLUtil.getPrefixAndLabelForId(tableid,dataset_local_id,Integer.parseInt(property)));
                            jsonObject.put("id",Integer.parseInt(property));

                            exploreClasses.add(jsonObject);
                        }


                        reader.close();//关闭资源

                        directoryClass.close();
                    }catch(Exception e)    {
                        e.printStackTrace();
                    }


                }catch(Exception e){
                    e.printStackTrace();
                }
                boolexploreClasses.set(true);
                boolexploreClasses.notify();
                logger.info("exploreClass end!");
            }
        }
    }


    public Session() {
    }

    public Session(String sessionId, Directory indexDir){
        this.sessionId = sessionId;
        this.indexDir = indexDir;
        try {

            this.reader = DirectoryReader.open(indexDir);
            this.searcher = new IndexSearcher(reader);

        } catch (IOException e) {
            e.printStackTrace();
        }

        analyzer = GlobalVariances.globalAnalyzer();

    }



    public List<JSONObject> getExploreClass(){
        synchronized (boolexploreClasses){
            try{
                while(!boolexploreClasses.get()){
                    exploreClasses.wait();
                }

            }catch(Exception e){
                e.printStackTrace();
            }
        }
//        synchronized (this.boolexploreClasses)

        return exploreClasses;
    }

    public List<JSONObject> getExploreProperty(){
        synchronized (boolexploreProperties){
            try{
                while(!boolexploreProperties.get()){
                    exploreProperties.wait();
                }

            }catch(Exception e){
                e.printStackTrace();
            }
        }
        return exploreProperties;
    }


    public List<Block> getResultList(String keyword,String[] organizations,String[] repostories,String[] licenses,String[] ins)throws ParseException, IOException{
        QueryParser parser_repo = new QueryParser("data_source",analyzer);
        String repos = repostories[0];
        for(int i=1;i<repostories.length;i++){
            repos+=" OR ";
            repos+=repostories[i];
        }
        Query repoQuery = parser_repo.parse(repos);

        QueryParser parser_organ = new QueryParser("org_des",analyzer);
        String organs = organizations[0];
        for(int i=1;i<organizations.length;i++){
            organs+=" OR ";
            organs+=organizations[i];
        }
        Query organQuery = parser_organ.parse(organs);

        BooleanQuery.Builder builder= new BooleanQuery.Builder();
        builder.add(repoQuery, BooleanClause.Occur.MUST);
        builder.add(organQuery, BooleanClause.Occur.MUST);

        BooleanQuery finalQuery = builder.build();


        TermQuery testQuery = new TermQuery(new Term("notes",keyword));

        TopDocs hits = searcher.search(testQuery,10);
//        System.out.println(hits.scoreDocs.length);

        List<Block> resultBlock = new ArrayList<>();
        for(ScoreDoc scoreDoc: hits.scoreDocs){
            Document doc = searcher.doc(scoreDoc.doc);   //可以设定返回的字段

//            System.out.println(doc.get("id"));
            resultBlock.add(new Block(scoreDoc.doc,doc.get("title"),doc.get("data_source"),doc.get("format"),doc.get("metadata_modified").substring(0,10),doc.get("local_id")));

        }

        return resultBlock;
    }


    public JSONObject getBasicinfo(Integer dataset_local_id)  {
        synchronized (boolbasicinfo){
            try{
                while(!boolbasicinfo.get()){
                    basicinfo.wait();
                }

            }catch (Exception e){
                e.printStackTrace();
            }
        }
        return basicinfo;

    }

    public List<JSONObject> getResource(Integer dataset_local_id){
        synchronized (boolresources){
            try{
                while(!boolresources.get()){
                    resources.wait();
                }

            }catch (Exception e){
                e.printStackTrace();
            }
        }
        logger.info("resource end!");
        return resources;

    }

    public List<JSONObject> getExtra(Integer dataset_local_id){

        synchronized (boolextras){
            try{
                while(!boolextras.get()){
                    extras.wait();
                }

            }catch (Exception e){
                e.printStackTrace();
            }
        }
        return extras;

    }

    public List<List<JSONObject>> getIllustrativeSnippet(){
        synchronized (boolillustrativeList){
            try{
                while(!boolillustrativeList.get()){
                    illustrativeList.wait();
                }

            }catch(InterruptedException e){
                e.printStackTrace();
            }
        }
        return illustrativeList;

    }

    public List<JSONObject> getPattern(){
        synchronized (boolsimplePatterns){
            try{
                while(!boolsimplePatterns.get()){
                    simplePatterns.wait();
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }



        return simplePatterns;

    }

    public  List<JSONObject> getEDP() {
        synchronized (booledpPatterns){
            try{
                while(!booledpPatterns.get()){
                    edpPatterns.wait();
                }
            }catch(Exception e){
                e.printStackTrace();
            }
        }

        return edpPatterns;

    }

    public  List<JSONObject> getLP() {
        synchronized (boollpPatterns){
            try{
                while(!boollpPatterns.get()){
                    lpPatterns.wait();
                }

            }catch(Exception e){
                e.printStackTrace();
            }
        }

        return lpPatterns;
    }



    public void getTopKClassAndProperty(Integer dataset_local_id){
        int tableid = 2;
        if(dataset_local_id>311){
            tableid = 3;
            dataset_local_id-=311;
        }
        Connection connection = JdbcUtil.getConnection(GlobalVariances.REMOTE);

        String sql = String.format("SELECT * FROM class_count%d WHERE dataset_local_id=%d ORDER BY count DESC limit 10;",tableid,dataset_local_id);
        try{
            PreparedStatement pst = connection.prepareStatement(sql);
            ResultSet rst = pst.executeQuery();

            if(rst.next()){


            }

            rst.close();
            pst.close();
            connection.close();



        }catch (Exception e){
            e.printStackTrace();
        }




    }

    public List<List<List<JSONObject>>> getTripleRank(){
        synchronized (booltripleRank){
            try{
                while(!booltripleRank.get()){
                    tripleRankList.wait();
                }
            }catch (InterruptedException e){
                e.printStackTrace();
            }
        }
        return tripleRankList;


    }

    public List<JSONObject> getPageRank(){
        synchronized (boolpageRank){
            try{
                while(!boolpageRank.get()){
                    pageRankList.wait();
                }
            }catch (InterruptedException e){
                e.printStackTrace();
            }
        }
        return pageRankList;

    }




    public List<List<JSONObject>> getTopK(Integer dataset_local_id){
        List<List<JSONObject>> result = new ArrayList<>();

        List<JSONObject> classes = new ArrayList<>();
        List<JSONObject> properties = new ArrayList<>();

        int tableid = 2;

        if(dataset_local_id>311){
            dataset_local_id-=311;
            tableid=3;
        }

        Connection connection = JdbcUtil.getConnection(GlobalVariances.LOCAL);
        String sql_class = String.format("SELECT * FROM class_count%d WHERE dataset_local_id=%d ORDER BY count DESC LIMIT 20",tableid,dataset_local_id);
        String sql_property = String.format("SELECT * FROM property_count%d WHERE dataset_local_id=%d ORDER BY count DESC LIMIT 20",tableid,dataset_local_id);
        //limit 100

        try{
            PreparedStatement pst = connection.prepareStatement(sql_class);
            ResultSet rst_class = pst.executeQuery();

            while(rst_class.next()){
                JSONObject jsonObject = new JSONObject();

                jsonObject.put("entity",SQLUtil.getURIForId(tableid,dataset_local_id,rst_class.getInt("class_id")));
                jsonObject.put("count",rst_class.getString("count"));

                classes.add(jsonObject);
            }
            result.add(classes);

            pst = connection.prepareStatement(sql_property);
            ResultSet rst_pro = pst.executeQuery();
            while(rst_pro.next()){
                JSONObject jsonObject = new JSONObject();

                jsonObject.put("property",SQLUtil.getURIForId(tableid,dataset_local_id,rst_pro.getInt("property_id")));
                jsonObject.put("count",rst_pro.getString("count"));

                properties.add(jsonObject);
            }
            result.add(properties);


            rst_class.close();rst_pro.close();
            pst.close();
            connection.close();


        }catch (Exception e){
            e.printStackTrace();
        }


        return result;

    }

    public List<List<JSONObject>> getSnippetTopK(){
       synchronized (booltopKList){
           try{
               while(!booltopKList.get()){
                   booltopKList.wait();
               }
           }catch (Exception e){
               e.printStackTrace();
           }
       }
       return topKList;
    }

    public List<List<JSONObject>> getSchemaFilter(Integer dataset_local_id){
        List<List<JSONObject>> result = new ArrayList<>();

        List<JSONObject> res1 = new ArrayList<>();
        List<JSONObject> res2 = new ArrayList<>();

        int tableid = 2;

        if(dataset_local_id>311){
            dataset_local_id-=311;
            tableid=3;
        }


        return result;

    }


    //ROCKER
    public List<JSONObject> getROCKER(){
        synchronized (boolrockerList){
            try{
                while(!boolrockerList.get()){
                    rockerList.wait();
                }
            }catch(InterruptedException e){
                e.printStackTrace();
            }
        }
        return rockerList;
    }

    public List<List<JSONObject>> getHITS(){

        synchronized (boolhits){
            try{
                while(!boolhits.get()){
                    hitsList.wait();
                }
            }catch (InterruptedException e){
                e.printStackTrace();
            }
        }

        return hitsList;

    }

    public List<String> schemaFilter(int dataset_local_id,String[] data,int type){

        List<String> result = new ArrayList<>();

        String oneIndex="",twoIndex="",oneStr="",twoStr="";




        int tableid = 2;

        if(dataset_local_id>311){
            dataset_local_id-=311;
            tableid=3;
        }

//        String java.com.ltt.test = "";
//        for(String one:data){
//            java.com.ltt.test+=one+" "; //TODO   不能这么搜，搜出来不准确    不能拼接，要分别搞
//        }
        //先得找到对应的entity

        if(type==0){
            oneIndex = "D:\\Index\\Filter\\"+tableid+"\\class\\";
            twoIndex = "D:\\Index\\Filter\\"+tableid+"\\property\\";
            oneStr="class";
            twoStr="property";
        }else {
            twoIndex = "D:\\Index\\Filter\\"+tableid+"\\class\\";
            oneIndex = "D:\\Index\\Filter\\"+tableid+"\\property\\";
            twoStr="class";
            oneStr="property";
        }

        try {

            Set<String> resultPP = new HashSet<>();
            //先去查entity
            for(String ss:data){
                Set<String> middleEntity = getEntity(ss,oneStr,"entity",oneIndex+dataset_local_id); //TODO  索引目录打开了很多次！

                for(String sss : middleEntity){
                    Set<String> pp = getEntity(sss,"entity",twoStr,twoIndex+dataset_local_id);

                    if(resultPP.size()==0) resultPP = pp;
                    else resultPP.retainAll(pp);

                }
            }
            //resultPP 是结果，需要去查prefix+label
            for(String ssss:resultPP){
                List<String> uriANDlabel = SQLUtil.getURIAndLabelForId(tableid,dataset_local_id,Integer.parseInt(ssss));
                String sa = getPrefix(uriANDlabel.get(0).replace(uriANDlabel.get(1),""),tableid,dataset_local_id)+":"+uriANDlabel.get(1);

                result.add(sa);

            }

        }catch(Exception e)    {
            e.printStackTrace();
        }


        return result;


    }

    public List<String> dataFilter(int dataset_local_id,List<Integer> classes,List<Integer> property){

        List<String> result = new ArrayList<>();

        int tableid = 2;

        if(dataset_local_id>311){
            dataset_local_id-=311;
            tableid=3;
        }

        String classIndex = "D:\\java.com.ltt.Index\\Filter\\"+tableid+"\\class\\";
        String propertyIndex = "D:\\java.com.ltt.Index\\Filter\\"+tableid+"\\property\\";



        try {
            Set<String> middleResult = new HashSet<>();
            for(Integer cls:classes){
                Set<String> middleEntity = getEntity(String.valueOf(cls),"class","entity",classIndex+dataset_local_id);

                if(middleResult.size()==0) middleResult = middleEntity;
                else middleResult.retainAll(middleEntity);

            }

            for(Integer cls:property){
                Set<String> middleEntity = getEntity(String.valueOf(cls),"property","entity",propertyIndex+dataset_local_id);

                if(middleResult.size()==0) middleResult = middleEntity;
                else middleResult.retainAll(middleEntity);

            }


            //resultPP 是结果，需要去查prefix+label
            for(String ssss:middleResult){   //ssss:67 75[//TODO   怎么会？？？？？？？？？？？？？？
                System.out.println("ssss:"+ssss);
                List<String> uriANDlabel = SQLUtil.getURIAndLabelForId(tableid,dataset_local_id,Integer.parseInt(ssss));
                if(uriANDlabel.get(1)!=null && uriANDlabel.get(0)!=null){
                    String sa = getPrefix(uriANDlabel.get(0).replace(uriANDlabel.get(1),""),tableid,dataset_local_id)+":"+uriANDlabel.get(1);
                    System.out.println("sa:"+sa);
                    result.add(sa);
                }


            }

        }catch(Exception e)    {
            e.printStackTrace();
        }


        return result;


    }


    private String getPrefix(String uri,int table_id,int dataset_local_id){
        String result = "";

        try{
            Directory directoryOne = FSDirectory.open(Paths.get("D:\\java.com.ltt.Index\\Namespace\\"+table_id+"\\"+dataset_local_id));//索引目录对象
            IndexReader reader = DirectoryReader.open(directoryOne);//索引读取工具
            IndexSearcher searcher = new IndexSearcher(reader);//索引搜索工具

            TermQuery query = new TermQuery(new Term("vocabulary",uri));

            TopDocs topDocs = searcher.search(query,100);
            // 获取总条数
            System.out.println("本次搜索共找到" + topDocs.totalHits + "条数据");
            // 获取得分文档对象（ScoreDoc）数组.SocreDoc中包含：文档的编号、文档的得分
            ScoreDoc[] scoreDocs = topDocs.scoreDocs;
            for (ScoreDoc scoreDoc : scoreDocs) {
                // 取出文档编号
                int docID = scoreDoc.doc;
                // 根据编号去找文档
                Document doc = reader.document(docID);
                result = doc.get("prefix");
                System.out.println(result);

            }
            directoryOne.close();
            reader.close();
        }catch(Exception e){
            e.printStackTrace();
        }


        return result;
    }

    //需要每一个entity独立去查property
    private Set<String> getEntity(String ss,String field1,String field2,String indexPath){



        Set<String> entities = new HashSet<>();
        try{
            Directory directoryOne = FSDirectory.open(Paths.get(indexPath));//索引目录对象
            IndexReader reader = DirectoryReader.open(directoryOne);//索引读取工具
            IndexSearcher searcher = new IndexSearcher(reader);//索引搜索工具

            QueryParser parser = new QueryParser(field1,new StandardAnalyzer());

            Query query = parser.parse(ss.trim());

            TopDocs topDocs = searcher.search(query,100);
            // 获取总条数
            System.out.println("本次搜索共找到" + topDocs.totalHits + "条数据");
            // 获取得分文档对象（ScoreDoc）数组.SocreDoc中包含：文档的编号、文档的得分
            ScoreDoc[] scoreDocs = topDocs.scoreDocs;
            for (ScoreDoc scoreDoc : scoreDocs) {
                // 取出文档编号
                int docID = scoreDoc.doc;
                // 根据编号去找文档
                Document doc = reader.document(docID);
                String ids = doc.get(field2);
                //搜出来是entity的话，应该单个加入
                String id[] = ids.trim().split(" ");
                for(String ii:id){
                    entities.add(ii);
                }

            }
            directoryOne.close();
            reader.close();
        }catch(Exception e){
            e.printStackTrace();
        }
        return entities;

    }


    public List<JSONObject> getNamespace(){

        synchronized (boolnamespace){
            try{
                while(!boolnamespace.get()){
                    namespaces.wait();
                }
            }catch(Exception e){
                e.printStackTrace();
            }
        }
        return namespaces;




    }

    public List<JSONObject> getExoLOD2(){
        synchronized (boolexpLOD){
            try{
                while(!boolexpLOD.get()){
                    expLOD.wait();
                }
            }catch(Exception e){
                e.printStackTrace();
            }
        }

        return expLOD;

    }

    private int getClassCount(String type,int tableid,int dataset_local_id){
        String classSum_sql = String.format("SELECT sum(count) FROM %s_count%d WHERE dataset_local_id=%d",type,tableid,dataset_local_id);

        try{
            Connection conn_remote = DemoApplication.secondDataSource.getConnection();
            Statement class_pst = conn_remote.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,
                    ResultSet.CONCUR_UPDATABLE);
            ResultSet class_rst = class_pst.executeQuery(classSum_sql);


            while (class_rst.next()){
                return class_rst.getInt(1);

            }


        }catch (Exception e){
            e.printStackTrace();
        }
        return -1;


    }

    /** ABCD **/
    public Statistics getStatistics(Integer dataset_local_id){
        Statistics statistics = new Statistics();
        List<JSONObject> jsonClass = new ArrayList<>();
        List<JSONObject> jsonProperty = new ArrayList<>();



        int tableid = 2;
        if(dataset_local_id>311){
            tableid = 3;
            dataset_local_id-=311;
        }



        String class_sql = String.format("SELECT * FROM class_count%d WHERE dataset_local_id=%d ORDER BY count DESC",tableid,dataset_local_id);
        String property_sql = String.format("SELECT * FROM property_count%d WHERE dataset_local_id=%d ORDER BY count DESC",tableid,dataset_local_id);


        int classSum = getClassCount("class",tableid,dataset_local_id);
        int propertySum = getClassCount("property",tableid,dataset_local_id);

        try{
            Connection conn_remote = DemoApplication.secondDataSource.getConnection();
            Statement class_pst = conn_remote.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,
                    ResultSet.CONCUR_UPDATABLE);
            ResultSet class_rst = class_pst.executeQuery(class_sql);

            int classSumSelect = 10;
            int classClock =0;

            int class_id=0;
            while (class_rst.next()){
                classClock++;
                class_id = class_rst.getInt("class_id");
                List<String> tmp = SQLUtil.getURIAndLabelForId(tableid,dataset_local_id,class_id);


                JSONObject jsonObject = new JSONObject();
                jsonObject.put("name",tmp.get(1));
                jsonObject.put("uri",tmp.get(0));
                jsonObject.put("value",(double)class_rst.getInt("count")/classSum);
                jsonClass.add(jsonObject);

                if(classClock>classSumSelect){
                    break;
                }


            }
            System.out.println(jsonClass);
            statistics.setClassDis(jsonClass);



            Statement property_pst = conn_remote.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,
                    ResultSet.CONCUR_UPDATABLE);
            ResultSet property_rst = property_pst.executeQuery(property_sql);
            int propertySumSelect =10;
            if(propertySumSelect>GlobalVariances.MAX_CLASS_PROPERTY){
                propertySumSelect = (int)Math.round(propertySumSelect * 0.2);
            }else if(propertySumSelect<GlobalVariances.MAX_CLASS_PROPERTY && propertySumSelect>GlobalVariances.MIN_CLASS_PROPERTY){
                propertySumSelect = (int)Math.round(propertySumSelect * 0.6);
            }

            int propertyClock = 0;
            int property_id=0;

            while(property_rst.next()){
                propertyClock++;

                property_id = property_rst.getInt("property_id");

                List<String> tmp = SQLUtil.getURIAndLabelForId(tableid,dataset_local_id,property_id);


                JSONObject jsonObject = new JSONObject();
                jsonObject.put("name",tmp.get(1));
                jsonObject.put("uri",tmp.get(0));
                jsonObject.put("value",(double)property_rst.getInt("count")/propertySum);
                jsonProperty.add(jsonObject);

                if(propertyClock>propertySumSelect)break;
            }
            statistics.setPropertyDis(jsonProperty);

        }catch (Exception e){
            e.printStackTrace();
        }

        Thread edpThread = new EDPThread(tableid,dataset_local_id);
        edpThread.start();

        Thread lpThread = new LPThread(tableid,dataset_local_id);
        lpThread.start();

        Thread simple = new SimplePatternThread(tableid,dataset_local_id);
        simple.start();





        Thread basicinfoTh = new BasicInfoThread(tableid,dataset_local_id);
        basicinfoTh.start();

        Thread resourceTh = new ResourceThread(tableid,dataset_local_id);
        resourceTh.start();

        Thread extraTh = new ExtraThread(tableid,dataset_local_id);
        extraTh.start();


        return statistics;

    }




}

