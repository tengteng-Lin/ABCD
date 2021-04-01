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

import com.ltt.Utils.GlobalVariances;

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
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.PostConstruct;
import javax.sql.DataSource;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.nio.file.Paths;
import java.sql.*;
import java.text.DecimalFormat;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

import static java.lang.Integer.min;

@Component
public class Session {
    //TODO   全局一个connection！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！
    private Directory indexDir;
    private IndexReader reader;
    private IndexSearcher searcher;
    private Analyzer analyzer;
    private SQLUtil sqlUtil;
//    private KSDEntry ksdEntry;

    public String sessionId;

    private Logger logger = LoggerFactory.getLogger(Session.class);

    /** overview **/



    /** data patterns **/
    private List<JSONObject> simplePatterns;
    private final AtomicBoolean  boolsimplePatterns = new AtomicBoolean(false);

    private List<JSONObject> edpPatterns;
    private final AtomicBoolean booledpPatterns = new AtomicBoolean(false);

    private List<JSONObject> lpPatterns;
    private final AtomicBoolean boollpPatterns = new AtomicBoolean(false);

    private List<JSONObject> expLOD;
    private final AtomicBoolean boolexpLOD = new AtomicBoolean(false);


    /** data samples **/

    private List<List<JSONObject>> illustrativeList;
    private final AtomicBoolean boolillustrativeList = new AtomicBoolean(false);



    /** metdata **/
    private JSONObject basicinfo;
    private final AtomicBoolean boolbasicinfo = new AtomicBoolean(false);
    private List<JSONObject> resources;
    private final AtomicBoolean boolresources = new AtomicBoolean(false);
    private List<JSONObject> extras;
    private final AtomicBoolean boolextras = new AtomicBoolean(false);

    private List<List<JSONObject>> classAndProperties;
    private final AtomicBoolean boolclassAndProperties = new AtomicBoolean(false);

    class ClassAndPropertyThread extends Thread{
        private int table_id;
        private int database_dataset_local_id;
        private int dataset_local_id;

        public ClassAndPropertyThread(int table_id, int database_dataset_local_id,int dataset_local_id) {
            this.table_id = table_id;
            this.database_dataset_local_id = database_dataset_local_id;
            this.dataset_local_id = dataset_local_id;
            classAndProperties = new ArrayList<>();
        }

        @Override
        public void run() {
            String class_sql = String.format("SELECT * FROM class_count%d WHERE dataset_local_id=%d ORDER BY count DESC",table_id,database_dataset_local_id);
            String property_sql = String.format("SELECT * FROM property_count%d WHERE dataset_local_id=%d ORDER BY count DESC",table_id,database_dataset_local_id);


            int classSum = getClassCount("class",table_id,database_dataset_local_id);
            int propertySum = getClassCount("property",table_id,database_dataset_local_id);

            synchronized (boolclassAndProperties){
                try{
//                    logger.info("classANDproperty start!");
                    List<JSONObject> jsonClass = new ArrayList<>();
                    List<JSONObject> jsonProperty = new ArrayList<>();

                    Connection conn_remote = DemoApplication.secondDataSource.getConnection();
                    Statement class_pst = conn_remote.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,
                            ResultSet.CONCUR_UPDATABLE);
                    ResultSet class_rst = class_pst.executeQuery(class_sql);

                    int classSumSelect = 50;
                    int classClock =0;

                    int class_id=0;
                    while (class_rst.next()){
                        classClock++;
                        class_id = class_rst.getInt("class_id");
                        List<String> tmp = sqlUtil.getURIAndLabelForId(table_id,database_dataset_local_id,class_id);


                        JSONObject jsonObject = new JSONObject();
                        jsonObject.put("name",tmp.get(1));
                        jsonObject.put("uri",tmp.get(0));
                        jsonObject.put("value",new BigDecimal(new DecimalFormat("#.00000").format((double)class_rst.getInt("count")*100/classSum), new MathContext(0,
                                RoundingMode.HALF_UP)).toEngineeringString()+"%");
                        jsonClass.add(jsonObject);

                        if(classClock>classSumSelect){
                            break;
                        }


                    }

                    Statement property_pst = conn_remote.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,
                            ResultSet.CONCUR_UPDATABLE);
                    ResultSet property_rst = property_pst.executeQuery(property_sql);
                    int propertySumSelect =50;
//                    if(propertySumSelect>GlobalVariances.MAX_CLASS_PROPERTY){
//                        propertySumSelect = (int)Math.round(propertySumSelect * 0.2);
//                    }else if(propertySumSelect<GlobalVariances.MAX_CLASS_PROPERTY && propertySumSelect>GlobalVariances.MIN_CLASS_PROPERTY){
//                        propertySumSelect = (int)Math.round(propertySumSelect * 0.6);
//                    }

                    int propertyClock = 0;
                    int property_id=0;

                    while(property_rst.next()){
                        propertyClock++;

                        property_id = property_rst.getInt("property_id");

                        List<String> tmp = sqlUtil.getURIAndLabelForId(table_id,database_dataset_local_id,property_id);


                        JSONObject jsonObject = new JSONObject();
                        jsonObject.put("name",tmp.get(1));
                        jsonObject.put("uri",tmp.get(0));


                        jsonObject.put("value",new BigDecimal(new DecimalFormat("#.00000").format((double)property_rst.getInt("count")*100/propertySum), new MathContext(0,
                                RoundingMode.HALF_UP)).toEngineeringString()+"%");
                        jsonProperty.add(jsonObject);

                        if(propertyClock>propertySumSelect)break;
                    }

                    classAndProperties.add(jsonClass);
                    classAndProperties.add(jsonProperty);
                    boolclassAndProperties.set(true);
                    boolclassAndProperties.notify();
//                    logger.info("classANDproperty end!");

                }catch(Exception e){
                    e.printStackTrace();
                }
            }
        }
    }

    class BasicInfoThread extends Thread{
        private int table_id;
        private int database_dataset_local_id;
        private int dataset_local_id;

        public BasicInfoThread(int table_id, int database_dataset_local_id,int dataset_local_id) {
            this.table_id = table_id;
            this.database_dataset_local_id = database_dataset_local_id;
            this.dataset_local_id = dataset_local_id;
            basicinfo = new JSONObject();
        }

        @Override
        public void run() {
            synchronized (boolbasicinfo){
                try{
                    Connection connection = DemoApplication.primaryDataSource.getConnection();
                    String sql = String.format("SELECT * FROM dataset%d WHERE local_id=%d;",table_id,database_dataset_local_id);
                    try{
                        PreparedStatement pst = connection.prepareStatement(sql);
                        ResultSet rst = pst.executeQuery();

                        while(rst.next()){
                            basicinfo.put("author",rst.getString("author")==null?"":rst.getString("author"));
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

                        rst.close();pst.close();connection.close();

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

                    rst.close();pst.close();connection.close();

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
                    if(table_id==2){
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

                            rst.close();pst.close();connection.close();

                        }catch(Exception e){
                            e.printStackTrace();
                        }
                        boolextras.set(true);
                        boolextras.notify();
                    }else{
                        extras.add(new JSONObject());
                        boolextras.set(true);
                        boolextras.notify();
                    }



                }catch (Exception e){
                    e.printStackTrace();
                }

            }
        }
    }


    class SimplePatternThread extends Thread {
        private int table_id;
        private int dataset_local_id;
        private int database_id;

        public SimplePatternThread(int table_id, int database_id,int dataset_local_id) {
            this.table_id = table_id;
            this.dataset_local_id = dataset_local_id;
            this.database_id = database_id;
            simplePatterns = new ArrayList<>();
        }

        @Override
        public void run() {
//            int table_id=2;
//            if(dataset_local_id>311) table_id=3;



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


                            String []before = onePattern.substring(0,pos==-1?0:pos).trim().split(" ");
                            for(String oneId:before){
                                JSONObject pattern = new JSONObject();
//                            System.out.println("oneID:"+oneId);

                                //需要getid对应的string
                                String name = sqlUtil.getLabelForId(table_id,database_id,Integer.parseInt(oneId));
//                            System.out.println(name);
                                pattern.put("name",name);
                                pattern.put("type",0);
//                        System.out.println(pattern);

                                oneResult.add(pattern);

                            }

                            int count = Integer.parseInt(onePattern.substring(pos+1,onePattern.length()-1));

                            oneJSON.put("children",oneResult);
//                            oneJSON.put("name","pattern"+i);
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

                    for(int i=0;i<simplePatterns.size();i++){
                        simplePatterns.get(i).put("name","pattern_"+i);
                    }



                    rs.close(); st.close();connection.close();
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

        public EDPThread(int dataset_local_id) {
            this.table_id = table_id;
            this.dataset_local_id = dataset_local_id;
            edpPatterns = new ArrayList<>();
        }

        @Override
        public void run() {
            int table_id = 2;

            int database_dataset_local_id = dataset_local_id;
            if(dataset_local_id>311){
                database_dataset_local_id-=311;
                table_id=3;
            }
            synchronized (booledpPatterns){
                try {
                    Directory directory = FSDirectory.open(Paths.get(GlobalVariances.edpDir+dataset_local_id));//打开索引文件夹
                    IndexReader reader = DirectoryReader.open(directory);//读取目录
                    IndexSearcher searcherForEDP = new IndexSearcher(reader);
//                    logger.info(String.valueOf(reader.maxDoc()));

                    //TODO sort
                    SortField sortField = new SortField("count",SortField.Type.INT,true);
                    Sort sort = new Sort(sortField);
                    TopDocs tds = searcherForEDP.search(new MatchAllDocsQuery(),500000,sort);
//                    logger.info(String.valueOf(tds.scoreDocs.length));

                    List<JSONObject> result = new ArrayList<>();
                    //一个pattern一个document
                    for(int i=0;i<min(50,tds.scoreDocs.length);i++){//从0开始从1开始？？？？
                        JSONObject onePattern = new JSONObject();
                        List<JSONObject> children = new ArrayList<>();

                        Document doc = searcherForEDP.doc(tds.scoreDocs[i].doc);
                        //只有一项和空length都为1
                        String strOutProperty = doc.get("outProperty");
//                        logger.info("out property:"+strOutProperty);
                        if(strOutProperty.length()!=0){
                            String [] outProperty = strOutProperty.trim().split(" ");
                            for(int j=0;j<outProperty.length;j++){
                                JSONObject jsonObject = new JSONObject();
//                        System.out.println(outProperty[j]);
                                String name = sqlUtil.getLabelForId(table_id,database_dataset_local_id,Integer.parseInt(outProperty[j]));
                                jsonObject.put("name",name);

                                jsonObject.put("type",0);
                                jsonObject.put("inOrOut",1);

                                children.add(jsonObject);

                            }
                        }

                        String strInProperty = doc.get("inProperty");
//                        logger.info("in property:"+strInProperty);
                        if(strInProperty.length()!=0){
                            String [] inProperty = strInProperty.trim().split(" "); //很可能没有进入的或出去的

                            for(int j=0;j<inProperty.length;j++){

                                JSONObject jsonObject = new JSONObject();
                                jsonObject.put("name",sqlUtil.getLabelForId(table_id,database_dataset_local_id,Integer.parseInt(inProperty[j])));
                                jsonObject.put("type",0);
                                jsonObject.put("inOrOut",0);

                                children.add(jsonObject);

                            }
                        }

                        String strClasses = doc.get("classes");
//                        logger.info("classes:"+strClasses);
                        if(strClasses.length()!=0){
                            String [] classes = strClasses.trim().split(" ");
                            for(int j=0;j<classes.length;j++){
                                JSONObject jsonObject = new JSONObject();
                                jsonObject.put("name",sqlUtil.getLabelForId(table_id,database_dataset_local_id,Integer.parseInt(classes[j])));
                                jsonObject.put("type",1);
                                jsonObject.put("inOrOut",1);

                                children.add(jsonObject);

                            }

                        }
                        onePattern.put("children",children);
                        String count = doc.get("count");
                        onePattern.put("count",Integer.parseInt(count));
                        onePattern.put("name","pattern"+i);

//                        result.add(onePattern);
                        edpPatterns.add(onePattern);
                    }
//                    System.out.println(edpPatterns);








                    reader.close();//关闭资源
                    directory.close();//关闭连接
                }catch(Exception e)    {
                    e.printStackTrace();
                }


                booledpPatterns.set(true);
                booledpPatterns.notify();

            }
        }
    }

    class LPThread extends Thread{

        private int dataset_local_id;

        public LPThread( int dataset_local_id) {

            this.dataset_local_id = dataset_local_id;
            lpPatterns = new ArrayList<>();
        }

        @Override
        public void run() {
            int table_id = 2;
            int database_dataset_local_id=dataset_local_id;

            if(dataset_local_id>311){
                database_dataset_local_id-=311;
                table_id=3;
            }
            synchronized (boollpPatterns){

                List<JSONObject> fromResult = new ArrayList<>();
                List<JSONObject> toResult = new ArrayList<>();

                try {
                    Directory directory = FSDirectory.open(Paths.get(GlobalVariances.lpDir+dataset_local_id));//打开索引文件夹
                    IndexReader reader = DirectoryReader.open(directory);//读取目录
                    IndexSearcher searcherForLP = new IndexSearcher(reader);
                    //TODO sort
                    SortField sortField = new SortField("count",SortField.Type.INT,true);
                    Sort sort = new Sort(sortField);

                    TopDocs tds = searcherForLP.search(new MatchAllDocsQuery(),500000,sort);

                    List<JSONObject> result = new ArrayList<>();
                    //一个pattern一个document
                    for(int i=0;i<min(50,tds.scoreDocs.length);i++){//从0开始从1开始？？？？
                        JSONObject resultOne = new JSONObject();

                        Document doc = searcherForLP.doc(tds.scoreDocs[i].doc);

                        int propertyID = Integer.parseInt(doc.get("propertyID"));
                        String count = doc.get("count");

                        JSONObject onePattern = new JSONObject();
                        List<JSONObject> children = new ArrayList<>();

                        String strFromInProperty = doc.get("fromInProperty");
                        if(strFromInProperty.length()!=0){
                            String [] fromInProperty = strFromInProperty.trim().split(" ");
                            for(int j=0;j<fromInProperty.length;j++){

                                JSONObject jsonObject = new JSONObject();
                                jsonObject.put("name",sqlUtil.getLabelForId(table_id,database_dataset_local_id,Integer.parseInt(fromInProperty[j])));
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
                                jsonObject.put("name",sqlUtil.getLabelForId(table_id,database_dataset_local_id,Integer.parseInt(fromOutProperty[j])));
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
                                jsonObject.put("name",sqlUtil.getLabelForId(table_id,database_dataset_local_id,Integer.parseInt(fromClasses[j])));
                                jsonObject.put("type",1);
                                jsonObject.put("inOrOut",0);

                                children.add(jsonObject);

                            }

                        }
                        onePattern.put("children",children);
//                        onePattern.put("name","pattern"+i);

                        fromResult.add(onePattern);


                        JSONObject twoPattern = new JSONObject();
                        List<JSONObject> children2 = new ArrayList<>();

                        String strToInProperty = doc.get("toInProperty");
                        if(strToInProperty.length()!=0){
                            String [] toInProperty = strToInProperty.trim().split(" ");
                            for(int j=0;j<toInProperty.length;j++){
                                if(Integer.parseInt(toInProperty[j])==propertyID) continue;
                                JSONObject jsonObject = new JSONObject();
                                jsonObject.put("name",sqlUtil.getLabelForId(table_id,database_dataset_local_id,Integer.parseInt(toInProperty[j])));
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
                                jsonObject.put("name",sqlUtil.getLabelForId(table_id,database_dataset_local_id,Integer.parseInt(toOutProperty[j])));
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
                                jsonObject.put("name",sqlUtil.getLabelForId(table_id,database_dataset_local_id,Integer.parseInt(toClasses[j])));
                                jsonObject.put("type",1);
                                jsonObject.put("inOrOut",0);

                                children2.add(jsonObject);

                            }
                        }

                        twoPattern.put("children",children2);
                        resultOne.put("onePattern",onePattern);
                        resultOne.put("twoPattern",twoPattern);
                        resultOne.put("count",Integer.parseInt(count));

                        resultOne.put("property",sqlUtil.getLabelForId(table_id,dataset_local_id,propertyID));
                        resultOne.put("name","pattern"+i);

                        lpPatterns.add(resultOne);

                    }



                    reader.close();//关闭资源
                    directory.close();//关闭连接
                }
                catch(Exception e)    {
                    e.printStackTrace();
                }
                boollpPatterns.set(true);
//                logger.info("LP ends!");
                boollpPatterns.notify();
            }

        }
    }

    public List<JSONObject> getLPbyPage(int dataset_local_id,int startIndex){
        int database_dataset_local_id = dataset_local_id;
        int table_id=2;
        if(dataset_local_id>311){
            table_id=3;
            database_dataset_local_id-=311;
        }
        List<JSONObject> result = new ArrayList<>();

        List<JSONObject> fromResult = new ArrayList<>();
        List<JSONObject> toResult = new ArrayList<>();

        //一次请求50个
        try {
            Directory directory = FSDirectory.open(Paths.get(GlobalVariances.lpDir+dataset_local_id));//打开索引文件夹
            IndexReader reader = DirectoryReader.open(directory);//读取目录
            IndexSearcher searcherForLP = new IndexSearcher(reader);
            //TODO sort
            SortField sortField = new SortField("count",SortField.Type.INT,true);
            Sort sort = new Sort(sortField);

            TopDocs tds = searcherForLP.search(new MatchAllDocsQuery(),500000,sort);

            int end = 50*((startIndex/50)+1);
            //一个pattern一个document
            for(int i=startIndex;i<min(end,tds.scoreDocs.length);i++){//从0开始从1开始？？？？
                JSONObject resultOne = new JSONObject();

                Document doc = searcherForLP.doc(tds.scoreDocs[i].doc);

                int propertyID = Integer.parseInt(doc.get("propertyID"));
                String count = doc.get("count");

                JSONObject onePattern = new JSONObject();
                List<JSONObject> children = new ArrayList<>();

                String strFromInProperty = doc.get("fromInProperty");
                if(strFromInProperty.length()!=0){
                    String [] fromInProperty = strFromInProperty.trim().split(" ");
                    for(int j=0;j<fromInProperty.length;j++){

                        JSONObject jsonObject = new JSONObject();
                        jsonObject.put("name",sqlUtil.getLabelForId(table_id,database_dataset_local_id,Integer.parseInt(fromInProperty[j])));
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
                        jsonObject.put("name",sqlUtil.getLabelForId(table_id,database_dataset_local_id,Integer.parseInt(fromOutProperty[j])));
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
                        jsonObject.put("name",sqlUtil.getLabelForId(table_id,database_dataset_local_id,Integer.parseInt(fromClasses[j])));
                        jsonObject.put("type",1);
                        jsonObject.put("inOrOut",0);

                        children.add(jsonObject);

                    }

                }
                onePattern.put("children",children);
//                        onePattern.put("name","pattern"+i);

                fromResult.add(onePattern);


                JSONObject twoPattern = new JSONObject();
                List<JSONObject> children2 = new ArrayList<>();

                String strToInProperty = doc.get("toInProperty");
                if(strToInProperty.length()!=0){
                    String [] toInProperty = strToInProperty.trim().split(" ");
                    for(int j=0;j<toInProperty.length;j++){
                        if(Integer.parseInt(toInProperty[j])==propertyID) continue;
                        JSONObject jsonObject = new JSONObject();
                        jsonObject.put("name",sqlUtil.getLabelForId(table_id,database_dataset_local_id,Integer.parseInt(toInProperty[j])));
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
                        jsonObject.put("name",sqlUtil.getLabelForId(table_id,database_dataset_local_id,Integer.parseInt(toOutProperty[j])));
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
                        jsonObject.put("name",sqlUtil.getLabelForId(table_id,database_dataset_local_id,Integer.parseInt(toClasses[j])));
                        jsonObject.put("type",1);
                        jsonObject.put("inOrOut",0);

                        children2.add(jsonObject);

                    }

                }

                twoPattern.put("children",children2);
//                        twoPattern.put("name","pattern"+i);
//                toResult.add(twoPattern);

                resultOne.put("onePattern",onePattern);
                resultOne.put("twoPattern",twoPattern);
                resultOne.put("count",Integer.parseInt(count));
                resultOne.put("property",sqlUtil.getLabelForId(table_id,dataset_local_id,propertyID));
                result.add(resultOne);

            }

            for(int i=0;i<result.size();i++){
                result.get(i).put("name","pattern"+(startIndex+i));
            }

            reader.close();//关闭资源
            directory.close();//关闭连接
        }catch(Exception e)    {
            e.printStackTrace();
        }

        return result;

    }

    public List<JSONObject> getEDPbyPage(int dataset_local_id,int startIndex){
        int database_dataset_local_id = dataset_local_id;
        int table_id=2;
        if(dataset_local_id>311){
            table_id=3;
            database_dataset_local_id-=311;
        }

        List<JSONObject> result = new ArrayList<>();
        //一次请求50个
        try {
            Directory directory = FSDirectory.open(Paths.get(GlobalVariances.edpDir+dataset_local_id));//打开索引文件夹
            IndexReader reader = DirectoryReader.open(directory);//读取目录
            IndexSearcher searcherForEDP = new IndexSearcher(reader);
            //TODO sort
            SortField sortField = new SortField("count",SortField.Type.INT,true);
            Sort sort = new Sort(sortField);
            TopDocs tds = searcherForEDP.search(new MatchAllDocsQuery(),500000,sort);



            int end = 50*((startIndex/50)+1);
            //一个pattern一个document
            for(int i=startIndex;i<min(end,tds.scoreDocs.length);i++){//从0开始从1开始？？？？
                JSONObject onePattern = new JSONObject();
                List<JSONObject> children = new ArrayList<>();

                Document doc = searcherForEDP.doc(tds.scoreDocs[i].doc);
                //只有一项和空length都为1
                String strOutProperty = doc.get("outProperty");
                if(strOutProperty.length()!=0){
                    String [] outProperty = strOutProperty.trim().split(" ");
                    for(int j=0;j<outProperty.length;j++){
                        JSONObject jsonObject = new JSONObject();
//                        System.out.println(outProperty[j]);
                        String name = sqlUtil.getLabelForId(table_id,database_dataset_local_id,Integer.parseInt(outProperty[j]));
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
                        jsonObject.put("name",sqlUtil.getLabelForId(table_id,database_dataset_local_id,Integer.parseInt(inProperty[j])));
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
                        jsonObject.put("name",sqlUtil.getLabelForId(table_id,database_dataset_local_id,Integer.parseInt(classes[j])));
                        jsonObject.put("type",1);
                        jsonObject.put("inOrOut",1);

                        children.add(jsonObject);

                    }

                }
                onePattern.put("children",children);
                String count = doc.get("count");
                onePattern.put("count",Integer.parseInt(count));
                result.add(onePattern);
            }

            for(int i=0;i<result.size();i++){
                result.get(i).put("name","pattern"+(startIndex+i));
            }

            reader.close();//关闭资源
            directory.close();//关闭连接
        }catch(Exception e)    {
            e.printStackTrace();
        }


        return result;

    }

    public List<List<JSONObject>> getClassAndProperty(){
        synchronized (boolclassAndProperties){
            try{
                while(!boolclassAndProperties.get()){
//                    logger.info(String.valueOf(boolclassAndProperties.get()));
                    boolclassAndProperties.wait();
                }

            }catch (Exception e){
                e.printStackTrace();
            }
        }

//        logger.info(String.valueOf(classAndProperties));
        return classAndProperties;

    }



    public Session() {
    }

    public Session(String sessionId, Directory indexDir){
        this.sessionId = sessionId;
        this.indexDir = indexDir;
        this.sqlUtil = new SQLUtil();
        try {

            this.reader = DirectoryReader.open(indexDir);
            this.searcher = new IndexSearcher(reader);

        } catch (IOException e) {
            e.printStackTrace();
        }

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
                    boolbasicinfo.wait();
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
                    boolresources.wait();
                }

            }catch (Exception e){
                e.printStackTrace();
            }
        }

        return resources;

    }

    public List<JSONObject> getExtra(Integer dataset_local_id){

        synchronized (boolextras){
            try{
                while(!boolextras.get()){
                    boolextras.wait();
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
                    boolillustrativeList.wait();
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
                    boolsimplePatterns.wait();
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
                    booledpPatterns.wait();
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
                    boollpPatterns.wait();
                }

            }catch(Exception e){
                e.printStackTrace();
            }
        }

        return lpPatterns;
    }




    public List<JSONObject> getExoLOD2(){
        synchronized (boolexpLOD){
            try{
                while(!boolexpLOD.get()){
                    boolexpLOD.wait();
                }
            }catch(Exception e){
                e.printStackTrace();
            }
        }

        return expLOD;

    }

    private int getClassCount(String type,int tableid,int dataset_local_id){
        String classSum_sql = String.format("SELECT sum(count) FROM %s_count%d WHERE dataset_local_id=%d",type,tableid,dataset_local_id);

        int result =-1;
        try{
            Connection conn_remote = DemoApplication.secondDataSource.getConnection();
            Statement class_pst = conn_remote.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,
                    ResultSet.CONCUR_UPDATABLE);
            ResultSet class_rst = class_pst.executeQuery(classSum_sql);


            while (class_rst.next()){
                result =  class_rst.getInt(1);


            }
            class_rst.close();
            class_pst.close();
            conn_remote.close();


        }catch (Exception e){
            e.printStackTrace();
        }
//        logger.info(String.valueOf(result));
        return result;


    }

    /** ABCD **/
    public ModelAndView getStatistics(Integer dataset_local_id){
        ModelAndView modelAndView= new ModelAndView();

        JSONObject basicinfo = new JSONObject();
        List<JSONObject> resources = new ArrayList<>();
        List<JSONObject> extras =  new ArrayList<>();



        int database_id = dataset_local_id;
        int tableid = 2;
        if(dataset_local_id>311){
            tableid = 3;
            database_id-=311;
        }

        try{

            Connection connection_remote = DemoApplication.primaryDataSource.getConnection();
            String sql = String.format("SELECT * FROM dataset%d WHERE local_id=%d;",tableid,database_id);
            PreparedStatement pst = connection_remote.prepareStatement(sql);
            ResultSet rst = pst.executeQuery();

            while(rst.next()){
                basicinfo.put("author",rst.getString("author")==null?"":rst.getString("author"));
                basicinfo.put("author_email",rst.getString("author_email"));
                basicinfo.put("metadata_created",rst.getString("metadata_created").substring(0,10));
                basicinfo.put("maintainer",rst.getString("maintainer"));
                basicinfo.put("maintainer_email",rst.getString("maintainer_email"));
                basicinfo.put("name",rst.getString("name"));
//                basicinfo.put("title",rst.getString("tiy"));
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


            String sql_resource = String.format("SELECT * FROM resource%d WHERE dataset_local_id=%d;",tableid,database_id);
            PreparedStatement pstt = connection_remote.prepareStatement(sql_resource);
            ResultSet rstt = pstt.executeQuery();
            while(rstt.next()){
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("mimetype",rstt.getString("mimetype"));
                jsonObject.put("cache_url",rstt.getString("cache_url"));
                jsonObject.put("hash",rstt.getString("hash"));
                jsonObject.put("description",rstt.getString("description"));
                jsonObject.put("name",rstt.getString("name"));
                jsonObject.put("format",rstt.getString("format"));
                jsonObject.put("url",rstt.getString("url"));
                jsonObject.put("cache_last_updated",rstt.getString("cache_last_updated"));
                jsonObject.put("package_id",rstt.getString("package_id"));
                jsonObject.put("created",rstt.getString("created"));
                jsonObject.put("state",rstt.getString("state"));
                jsonObject.put("mimetype_inner",rstt.getString("mimetype_inner"));
                jsonObject.put("last_modified",rstt.getString("last_modified"));
                jsonObject.put("position",rstt.getString("position"));
                jsonObject.put("revision_id",rstt.getString("revision_id"));
                jsonObject.put("url_type",rstt.getString("url_type"));
                jsonObject.put("resource_type",rstt.getString("resource_type"));
                jsonObject.put("downloaded",rstt.getString("downloaded"));
                jsonObject.put("data_source",rstt.getString("data_source"));

                resources.add(jsonObject);

            }

            rstt.close();pstt.close();


            if(tableid==2){
                String sql_extra = String.format("SELECT * FROM extra%d WHERE dataset_local_id=%d;",tableid,database_id);
                PreparedStatement psttt = connection_remote.prepareStatement(sql_extra);
                ResultSet rsttt = psttt.executeQuery();


                while(rsttt.next()){

                    String key = rsttt.getString("key");
                    if(!"links".equals(key.substring(0,5))){
                        JSONObject jsonObject = new JSONObject();
                        jsonObject.put("key",key);
                        jsonObject.put("value",rsttt.getString("value"));
                        extras.add(jsonObject);
                    }

                }

                rsttt.close();psttt.close();
                connection_remote.close();
            }

            modelAndView.addObject("extra",extras);
            modelAndView.addObject("metadata",basicinfo);
            modelAndView.addObject("resource",resources);
            modelAndView.setViewName("A-overview.html");



        }catch (Exception e){
            e.printStackTrace();
        }

        Thread edpThread = new EDPThread(dataset_local_id);
        edpThread.start();

        Thread lpThread = new LPThread(dataset_local_id);
        lpThread.start();

        Thread simple = new SimplePatternThread(tableid,database_id,dataset_local_id);
        simple.start();

        Thread clsAndPre = new ClassAndPropertyThread(tableid,database_id,dataset_local_id);
        clsAndPre.start();





//        Thread basicinfoTh = new BasicInfoThread(tableid,database_id,dataset_local_id);
//        basicinfoTh.start();
//
//        Thread resourceTh = new ResourceThread(tableid,database_id);
//        resourceTh.start();
//
//        Thread extraTh = new ExtraThread(tableid,database_id);
//        extraTh.start();


        return modelAndView;

    }




}
