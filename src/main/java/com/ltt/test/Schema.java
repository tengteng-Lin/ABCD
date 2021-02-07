package com.ltt.test;

import com.ltt.Utils.FileModel;
import com.ltt.Utils.SQLUtil;
import com.alibaba.fastjson.JSONObject;
import org.apache.lucene.analysis.core.WhitespaceAnalyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.*;

import com.ltt.Model.InstanceFeature;
import com.ltt.Utils.GlobalVariances;
import com.ltt.Utils.JdbcUtil;
import org.apache.lucene.index.*;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.store.MMapDirectory;

import java.nio.file.Paths;
import java.sql.*;
import java.util.*;

public class Schema {
    public static Map<Integer, String> id2uri;   //
    Set<Integer> literal;//literal的id集合
    Set<Integer> mess;//乱码的id集合
    private long literalLength;
    private int literalCount;
    HashMap<String, Integer> vocabularyCount;
    int subClassID;
    int typeID = -1;

    Map<Integer,Set<Integer>> class2entity;
    Map<Integer,Set<Integer>> property2entity;



    Connection connection_remote;
    Connection connection_local;

    String namespacePath = "D:\\java.com.ltt.Index\\Namespace\\";

//    //建索引
//    Directory dir;
//    IndexWriterConfig java.com.ltt.config;
//    IndexWriter indexWriter;

    public Schema() {

        connection_remote = JdbcUtil.getConnection(GlobalVariances.REMOTE);
        connection_local = JdbcUtil.getConnection(GlobalVariances.LOCAL);
        id2uri = new HashMap<>();//id -> uri
        literal = new HashSet<>();
        mess = new HashSet<>();
        class2entity = new HashMap<>();
        property2entity = new HashMap<>();
        literalLength = 0;
        literalCount = 0;
        vocabularyCount = new HashMap<>();
        subClassID = -1;


    }



    public void getFilterForSchema(int table_id,int dataset_local_id){


        getID2URI(table_id,dataset_local_id);
        try{
            FileModel.CreateFolder("D:\\java.com.ltt.Index\\Filter\\"+table_id+"\\Class\\"+dataset_local_id);
            Directory dir_class = MMapDirectory.open(Paths.get("D:\\Filter\\"+table_id+"\\Class\\"+dataset_local_id));//会变的，一个dataset一个文件夹，因为是一个group一个document！！

            FileModel.CreateFolder("D:\\java.com.ltt.Index\\Filter\\"+table_id+"\\Property\\"+dataset_local_id);
            Directory dir_property = MMapDirectory.open(Paths.get("D:\\Filter\\"+table_id+"\\Property\\"+dataset_local_id));

            IndexWriterConfig config = new IndexWriterConfig(new StandardAnalyzer());
            config.setOpenMode(IndexWriterConfig.OpenMode.CREATE);
            config.setMaxBufferedDocs(100);
            IndexWriterConfig config2 = new IndexWriterConfig(new StandardAnalyzer());
            config2.setOpenMode(IndexWriterConfig.OpenMode.CREATE);
            config2.setMaxBufferedDocs(100);

            IndexWriter class_indexWriter = new IndexWriter(dir_class, config);
            IndexWriter property_indexWriter = new IndexWriter(dir_property, config2);

            String searchTriple = String.format("SELECT * FROM triple%d WHERE dataset_local_id=%d",table_id,dataset_local_id);
            PreparedStatement searchPst = connection_remote.prepareStatement(searchTriple);

            ResultSet searchRst = searchPst.executeQuery();
            while(searchRst.next()){
                int subject = searchRst.getInt("subject");
                int predicate = searchRst.getInt("predicate");
                int object = searchRst.getInt("object");

                if(predicate == typeID){
                    if(class2entity.containsKey(object)){
                        Set<Integer>tmp = class2entity.get(object);
                        tmp.add(subject);
                        class2entity.replace(object,tmp);
                    }else{
                        Set<Integer> tmp = new HashSet<>();
                        tmp.add(subject);
                        class2entity.put(object,tmp);
                    }
                }

                if(property2entity.containsKey(predicate)){
                    Set<Integer> tmp = property2entity.get(predicate);
                    tmp.add(subject);
                    property2entity.replace(predicate,tmp);
                }else{
                    Set<Integer> tmp = new HashSet<>();
                    tmp.add(subject);
                    property2entity.put(predicate,tmp);
                }
            }

            System.out.println("class\tentity");
            for(Integer cls : class2entity.keySet()){
                Document doc1 = new Document();
                Set<Integer> entity = class2entity.get(cls);
                String entityField = "";
                for(Integer integer:entity){
                    entityField+=String.valueOf(integer)+" ";
                }

                System.out.println(cls+"\t"+entityField.trim());

//                doc1.add(new TextField("entity",entityField.trim(), Field.Store.YES));
//                doc1.add(new TextField("class", String.valueOf(cls), Field.Store.YES));
//                class_indexWriter.addDocument(doc1);
            }
//            class_indexWriter.commit();

            System.out.println("property\tentity");
            for(Integer pro:property2entity.keySet()){
                Document doc2 = new Document();
                Set<Integer> entity = property2entity.get(pro);
                String propertyField = "";
                for(Integer integer:entity){
                    propertyField+=String.valueOf(integer)+" ";
                }
                System.out.println(String.valueOf(pro)+"\t"+propertyField.trim());
//                doc2.add(new TextField("entity",propertyField.trim(), Field.Store.YES));
//                doc2.add(new TextField("property", String.valueOf(pro), Field.Store.YES));
//                property_indexWriter.addDocument(doc2);

            }
//            property_indexWriter.commit();

        }catch(Exception e){
            e.printStackTrace();
        }

    }



    public void getExpLOD(int table_id, int dataset_local_id) {


        HashMap<String, Integer> features2id = new HashMap<>();
        features2id.clear();
        HashMap<Integer, Set<Integer>> idx2instances = new HashMap<>();
        idx2instances.clear();

//        typeID = 4;
        getID2URI(table_id, dataset_local_id);
//        Connection connection = JdbcUtil.getConnection(GlobalVariances.LOCAL);
        String sql = String.format("SELECT * FROM triple%d WHERE dataset_local_id=%d ORDER BY subject", table_id, dataset_local_id);  //ORDER BY 是必须的，因为不是subject先排序，是一个个三元组这么顺下来的
        try {
            FileModel.CreateFolder("D:\\java.com.ltt.Index\\ExpLOD\\"+dataset_local_id);
            Directory dir = MMapDirectory.open(Paths.get("D:\\java.com.ltt.Index\\ExpLOD\\"+dataset_local_id));//会变的，一个dataset一个文件夹，因为是一个group一个document！！
            IndexWriterConfig config = new IndexWriterConfig(new WhitespaceAnalyzer());
            config.setOpenMode(IndexWriterConfig.OpenMode.CREATE);
            config.setMaxBufferedDocs(100);
            IndexWriter indexWriter = new IndexWriter(dir, config);


            Statement pst = connection_remote.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);
            ResultSet rst = pst.executeQuery(sql);

            Set<Integer> onePredicate = new HashSet<>();
            onePredicate.clear();
            Set<Integer> oneClass = new HashSet<>();
            oneClass.clear();

            int idx = 0;//InstanceBlock的标号
            int lastSubject = -8, predicate = -8, object = -8;
            while (rst.next()) {
                lastSubject = rst.getInt("subject");
                object = rst.getInt("object");
                if(mess.contains(lastSubject) || mess.contains(object)) continue;


                onePredicate.add(rst.getInt("predicate"));
                if (rst.getInt("predicate") == typeID) {
                    oneClass.add(object);
                }
                break;
            }

            while (rst.next()) {
                int subject = rst.getInt("subject");
                predicate = rst.getInt("predicate");
                object = rst.getInt("object");

                //只要subject或object有一个是乱码，那么整个triple就不要了
                if(mess.contains(subject) || mess.contains(object)) continue;


                if (subject != lastSubject) {
                    InstanceFeature instanceFeature = new InstanceFeature(onePredicate, oneClass);
                    if (features2id.containsKey(instanceFeature.toString())) {
                        int idx_one = features2id.get(instanceFeature.toString());
                        Set<Integer> needAddInstance = idx2instances.get(idx_one);
                        needAddInstance.add(lastSubject);
                        idx2instances.put(idx_one, needAddInstance);
                    } else {
                        Set<Integer> ins = new HashSet<>();
                        ins.add(lastSubject);
                        idx2instances.put(idx, ins);
                        features2id.put(instanceFeature.toString(), idx++);
                    }

                    onePredicate.clear();
                    onePredicate.add(predicate);
                    oneClass.clear();
                    if (predicate == typeID) {
                        oneClass.add(object);
                    }

                } else {
                    onePredicate.add(predicate);
                    if (predicate == typeID) {
                        oneClass.add(object);
                    }
                }
                lastSubject = subject;
            }


            InstanceFeature instanceFeature1 = new InstanceFeature(onePredicate, oneClass);
            if (features2id.containsKey(instanceFeature1.toString())) {

                int idx_one = features2id.get(instanceFeature1.toString());
                Set<Integer> needAddInstance = idx2instances.get(idx_one);

                needAddInstance.add(lastSubject);
                idx2instances.put(idx_one, needAddInstance);

            } else {

                Set<Integer> ins = new HashSet<>();
                ins.add(lastSubject);
                idx2instances.put(idx, ins);

                features2id.put(instanceFeature1.toString(), idx++);

            }

/**开始处理BL标签，建索引**/
            for (String instanceFeature:features2id.keySet()){
                Document doc = new Document();

                int idx_for = features2id.get(instanceFeature);
                int pos = instanceFeature.indexOf("#");


                String properties = instanceFeature.substring(1,pos-1);

                doc.add(new StringField("property",getStrForPreAndObj("P/",properties,table_id,dataset_local_id), Field.Store.YES));

                String classes = instanceFeature.substring(pos+2,instanceFeature.length()-1);

                doc.add(new StringField("class",getStrForPreAndObj("C/",classes,table_id,dataset_local_id), Field.Store.YES));
                doc.add(new IntPoint("idx",idx_for));


                Set<Integer> instances = idx2instances.get(idx_for);

                String instanceID ="";
                Integer example =-2;
                for(Integer integer : instances){
                    instanceID += integer+" ";
                    example=integer;

                }
                //instanceStr也需要处理
                doc.add(new TextField("instanceID",instanceID, Field.Store.YES));
                doc.add(new StringField("instanceStr",getStrForPreAndObj("I/",String.valueOf(example),table_id,dataset_local_id), Field.Store.YES));


//                System.out.println(getStrForPreAndObj("P/",properties,table_id,dataset_local_id)+"\t"+getStrForPreAndObj("C/",classes,table_id,dataset_local_id)+"\t"+getStrForPreAndObj("I/",String.valueOf(example),table_id,dataset_local_id));



                indexWriter.addDocument(doc);


            }
            indexWriter.commit();
            indexWriter.close();




            rst.close();
            pst.close();

        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    //TODO   需要改    去Index搜索 |  根据id搜到字符串和label，再根据字符串去搜prefix
    public String getStrForPreAndObj(String prefix,String str,int table_id,int dataset_local_id) {
        String[] strArray = str.trim().split(", ");
        String result = "";
        if("".equals(str)){
            return result;
        }

        try{
            Directory directory = FSDirectory.open(Paths.get(namespacePath+table_id+"\\"+dataset_local_id));//打开索引文件夹
            IndexReader reader = DirectoryReader.open(directory);//读取目录

            for(String ss : strArray){
                Integer ii = Integer.parseInt(ss); //某个uri的id，未排除乱码
                List<String> tt = SQLUtil.getURIAndLabelForId(table_id,dataset_local_id,ii);
                String uri = tt.get(0);
                String label = tt.get(1);

//                System.out.println(prefix+ii);

                //需要用Lucene来搜索
                String uriPre = uri.replace(label,"");
//                System.out.println("uriPre:"+uriPre);

                IndexSearcher searcher = new IndexSearcher(reader);//索引搜索工具

                //怎么会搜不到？因为对于object，只对类建了索引，其他的无
                //搜不到，全都搜不到？？？？？？？
                TermQuery query = new TermQuery(new Term("vocabulary",uriPre));
//                QueryParser parser = new QueryParser("vocabulary",new StandardAnalyzer());
//                Query query = parser.parse(uriPre);//这里是要求整个匹配的

                TopDocs topDocs = searcher.search(query,1);
//                System.out.println(topDocs.scoreDocs.length);

                String prefixSearch = "";
//                if(topDocs.scoreDocs.length==0){
////                    System.out.println(reader.document(topDocs.scoreDocs[0].doc).get("prefix"));
//                    prefixSearch = uriPre;
//                }else{
                    prefixSearch = reader.document(topDocs.scoreDocs[0].doc).get("prefix");
//                }



                result+=prefix+prefixSearch+":"+label+" *** ";
            }



        }catch (Exception e){
            e.printStackTrace();
        }


        return result.substring(0,result.length()-5);

    }

    public void getDegree2(int table_id, int dataset_local_id) {



        getID2URI(table_id, dataset_local_id);

        List<LinkedHashMap<String, Integer>> result = new ArrayList<>();

        LinkedHashMap<Integer, JSONObject> inDegreeResult = new LinkedHashMap<>();  //range->count
        LinkedHashMap<Integer, JSONObject> outDegreeResult = new LinkedHashMap<>();
//        HashMap<Integer,String> inDegreeExample = new HashMap<>();
//        HashMap<Integer,String> outDegreeExample = new HashMap<>();

        LinkedHashMap<Integer, Integer> inDegreeCount = new LinkedHashMap<Integer, Integer>();
        LinkedHashMap<Integer, Integer> outDegreeCount = new LinkedHashMap<Integer, Integer>();

        Connection connection = JdbcUtil.getConnection(GlobalVariances.REMOTE);
        String sql = String.format("SELECT * FROM triple%d WHERE dataset_local_id=%d", table_id, dataset_local_id);
        try {
            FileModel.CreateFolder("D:\\java.com.ltt.Index\\LineDegree\\In\\"+table_id+"\\"+dataset_local_id);
            Directory dir_in = MMapDirectory.open(Paths.get("D:\\java.com.ltt.Index\\LineDegree\\In\\"+table_id+"\\"+dataset_local_id));
            FileModel.CreateFolder("D:\\java.com.ltt.Index\\LineDegree\\Out\\"+table_id+"\\"+dataset_local_id);
            Directory dir_out = MMapDirectory.open(Paths.get("D:\\java.com.ltt.Index\\LineDegree\\Out\\"+table_id+"\\"+dataset_local_id));


            IndexWriterConfig config = new IndexWriterConfig(new StandardAnalyzer());
            config.setOpenMode(IndexWriterConfig.OpenMode.CREATE);
            config.setMaxBufferedDocs(100);
            IndexWriterConfig config2 = new IndexWriterConfig(new StandardAnalyzer());
            config2.setOpenMode(IndexWriterConfig.OpenMode.CREATE);
            config2.setMaxBufferedDocs(100);

            IndexWriter in_indexWriter = new IndexWriter(dir_in, config);
            IndexWriter out_indexWriter = new IndexWriter(dir_out, config2);

            Statement pst = connection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);
            ResultSet rst = pst.executeQuery(sql);

            while (rst.next()) {
//                System.out.println("java.com.ltt.test");

                int subject = rst.getInt("subject");
                int predicate = rst.getInt("predicate");
                int object = rst.getInt("object");

                if (predicate != typeID) {
                    if (outDegreeCount.containsKey(subject)) {
                        outDegreeCount.put(subject, outDegreeCount.get(subject) + 1);
                    } else {
                        outDegreeCount.put(subject, 1);
                    }

                    if (!literal.contains(object)) {
                        if (inDegreeCount.containsKey(object)) {
                            inDegreeCount.put(object, inDegreeCount.get(object) + 1);
                        } else {
                            inDegreeCount.put(object, 1);
                        }
                    }
                }


            }
            rst.close();

            LinkedHashMap<Integer, Integer> inDegreeCount2 = sortHashMap(inDegreeCount);//根据value排序的
            LinkedHashMap<Integer, Integer> outDegreeCount2 = sortHashMap(outDegreeCount);

            String exampleInEntities = "";
            int inInitial = getHead(inDegreeCount2).getValue();
            int inValue = 0;
            for (Integer entityID : inDegreeCount2.keySet()) {
                Integer entityCount = inDegreeCount2.get(entityID);

                if (entityCount != inInitial) {
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("value",inValue);
                    jsonObject.put("example",exampleInEntities.trim());
                    inDegreeResult.put(inInitial, jsonObject);
//                    inDegreeExample.put(inInitial,exampleInEntities);
                    inInitial = entityCount;
                    inValue = 1;
                    exampleInEntities = entityID +" ";
                } else {
                    inValue++;
                    exampleInEntities+=entityID+" ";
                }
            }
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("value",inValue);
            jsonObject.put("example",exampleInEntities.trim());
            inDegreeResult.put(inInitial, jsonObject);


            String exampleOutEntities = "";
            int outInitial = getHead(outDegreeCount2).getValue();
            int outValue = 0;
            for (Integer entityID : outDegreeCount2.keySet()) {
                Integer entityCount = outDegreeCount2.get(entityID);
//                System.out.println("entityCount:"+entityCount);
//                System.out.println("outInitial:"+outInitial);

                if (entityCount != outInitial) {
                    JSONObject jsonObject1 = new JSONObject();
                    jsonObject1.put("value",outValue);
                    jsonObject1.put("example",exampleOutEntities.trim());
                    outDegreeResult.put(outInitial, jsonObject1);

                    outInitial = entityCount;
                    outValue = 1;
                    exampleOutEntities= entityID +" ";
                } else {
                    outValue++;
                    exampleOutEntities+=entityID+" ";
                }
            }
            JSONObject jsonObject1 = new JSONObject();
            jsonObject1.put("value",outValue);
            jsonObject1.put("example",exampleOutEntities.trim());
            outDegreeResult.put(outInitial, jsonObject1);

            /**直接写入索引**/
            for (Integer cc : inDegreeResult.keySet()) {
                JSONObject jsonObject2 = inDegreeResult.get(cc);
                int value = jsonObject2.getInteger("value");
                String example =  jsonObject2.getString("example");

                Document doc = new Document();

                doc.add(new TextField("count", String.valueOf(cc), Field.Store.YES));
                doc.add(new TextField("value", String.valueOf(value), Field.Store.YES));
                doc.add(new TextField("example",example, Field.Store.YES));

//                System.out.println(cc+"\t"+value+"\t"+example);
                in_indexWriter.addDocument(doc);
            }

            in_indexWriter.commit();

            for (Integer cc : outDegreeResult.keySet()) {
                JSONObject jsonObject2 = outDegreeResult.get(cc);
                int value = jsonObject2.getInteger("value");
                String example =  jsonObject2.getString("example");

                Document doc = new Document();

                doc.add(new TextField("count", String.valueOf(cc), Field.Store.YES));
                doc.add(new TextField("value", String.valueOf(value), Field.Store.YES));
                doc.add(new TextField("example",example, Field.Store.YES));

                out_indexWriter.addDocument(doc);


            }

            out_indexWriter.commit();

            in_indexWriter.close();
            out_indexWriter.close();


            rst.close();
            pst.close();



//            /** 写入 in **/
//            //sql
//            String write_sql = String.format("INSERT INTO in_degree%d(dataset_local_id,in_range,count)values(?,?,?);", table_id);
//            //预编译
//            PreparedStatement stt = connection.prepareStatement(write_sql); //预编译SQL，减少sql执行
//
//            for (Integer cc : inDegreeResult.keySet()) {
//                //传参
////                System.out.println("cc:"+cc);
//                stt.setInt(1, dataset_local_id);
//                stt.setInt(2, cc);
//                stt.setInt(3, inDegreeResult.get(cc));
//                //执行
//                stt.executeUpdate();
//
//            }
//
//            /** 写入 out **/
//            //sql
//            String write_sql_out = String.format("INSERT INTO out_degree%d(dataset_local_id,out_range,count)values(?,?,?);", table_id);
//            //预编译
//            PreparedStatement stt_out = connection.prepareStatement(write_sql_out); //预编译SQL，减少sql执行
//
//            for (Integer cc : outDegreeResult.keySet()) {
//                //传参
////                System.out.println("cc:"+cc);
//                stt_out.setInt(1, dataset_local_id);
//                stt_out.setInt(2, cc);
//                stt_out.setInt(3, outDegreeResult.get(cc));
//                //执行
//                stt_out.executeUpdate();
//
//            }

            connection.close();


        } catch (Exception e) {
            e.printStackTrace();
        }


    }

//    public void getDegree(int table_id,int dataset_local_id){
//        getID2URI(table_id,dataset_local_id);
//
//        List<LinkedHashMap<String,Integer>> result = new ArrayList<>();
//
//        LinkedHashMap<String,Integer> inDegreeResult = new LinkedHashMap<>();
//        LinkedHashMap<String,Integer> outDegreeResult = new LinkedHashMap<>();
//
//        LinkedHashMap<Integer,Integer> inDegreeCount = new LinkedHashMap<Integer,Integer>();
//        LinkedHashMap<Integer,Integer> outDegreeCount = new LinkedHashMap<Integer,Integer>();
//
//        Connection connection = JdbcUtil.getConnection();
//        String sql = String.format("SELECT * FROM triple%d WHERE dataset_local_id=%d",table_id,dataset_local_id);
//        try{
//            Statement pst = connection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_UPDATABLE);
//            ResultSet rst = pst.executeQuery(sql);
//
//            rst.last();
//            System.out.println(rst.getRow());
//            rst.beforeFirst();
//
//            while(rst.next()){
////                System.out.println("java.com.ltt.test");
//
//                int subject = rst.getInt("subject");
//                int predicate = rst.getInt("predicate");
//                int object = rst.getInt("object");
//
//                if(predicate!=typeID){
//                    if(outDegreeCount.containsKey(subject)){
//                        outDegreeCount.put(subject,outDegreeCount.get(subject)+1);
//                    }else{
//                        outDegreeCount.put(subject,1);
//                    }
//
//                    if(!literal.contains(object)){
//                        if(inDegreeCount.containsKey(object)){
//                            inDegreeCount.put(object,inDegreeCount.get(object)+1);
//                        }else{
//                            inDegreeCount.put(object,1);
//                        }
//                    }
//                }
//
//
//            }
//
//            rst.close();
//            pst.close();
//
//            LinkedHashMap<Integer,Integer>inDegreeCount2 = sortHashMap(inDegreeCount);
//            LinkedHashMap<Integer,Integer>outDegreeCount2 = sortHashMap(outDegreeCount);
//
//            int inDegreeMIN = getHead(inDegreeCount2).getValue();
////            System.out.println("inDegreeMIN:"+inDegreeMIN);
//            int inDegreeMax = getTailByReflection(inDegreeCount2).getValue();
////            System.out.println("inDegreeMAX:"+inDegreeMax);
//
//            int outDegreeMIN = getHead(outDegreeCount2).getValue();
//            int outDegreeMax = getTailByReflection(outDegreeCount2).getValue();
//
////            System.out.println((int)Math.ceil((float)3/10));
//            int inDegreeRange = (int)Math.ceil(((float)inDegreeMax - inDegreeMIN) / 10);
////            System.out.println("inDegreeRange:"+inDegreeRange);
//            int outDegreeRange = (int)Math.ceil((float)(outDegreeMax - outDegreeMIN) / 10);
////            System.out.println("outDegreeRange:"+outDegreeRange);
//
//
//
//            int upperbandForInDegree =inDegreeMIN +inDegreeRange;
//            int lowerbandForInDegree = inDegreeMIN;
//            int countForIndegree = 0;
//
//            int upperbandForOutDegree = outDegreeMIN + outDegreeRange;
//            int lowerbandForOutDegree = outDegreeMIN;
//            int countForOutDegree = 0;
//
//            for(Integer key : inDegreeCount2.keySet()){
//                Integer value = inDegreeCount2.get(key);
////                System.out.println("value:"+value);
//
//                if(value < upperbandForInDegree){
//                    countForIndegree += 1;
//
//                }else{
//                    while(value >= upperbandForInDegree){
//                        inDegreeResult.put(String.valueOf(lowerbandForInDegree)+"~"+String.valueOf(upperbandForInDegree),countForIndegree);
//                        countForIndegree = 0;
//
//                        lowerbandForInDegree = upperbandForInDegree+1;
//                        upperbandForInDegree = lowerbandForInDegree+inDegreeRange;
//
//                        if (value < upperbandForInDegree) {
//                            countForIndegree += 1;
//                            break;
//                        }
//
////                        System.out.println("upperbandForOutDegree:"+upperbandForOutDegree);
//                    }
//
//
//                }
//
//            }
//
//            inDegreeResult.put(String.valueOf(lowerbandForInDegree)+"~"+String.valueOf(upperbandForInDegree),countForIndegree);
//
//
//
//
//
//
//            for(Integer key : outDegreeCount2.keySet()){
//                Integer value = outDegreeCount2.get(key);
////                System.out.println("value:"+value);
//
//                if(value < upperbandForOutDegree){
//                    countForOutDegree += 1;
//
//                }else{
//                    while(value >= upperbandForOutDegree){
//                        outDegreeResult.put(String.valueOf(lowerbandForOutDegree)+"~"+String.valueOf(upperbandForOutDegree),countForOutDegree);
//                        countForOutDegree = 0;
//
//                        lowerbandForOutDegree = upperbandForOutDegree+1;
//                        upperbandForOutDegree = lowerbandForOutDegree+outDegreeRange;
//
//                        if (value < upperbandForOutDegree) {
//                            countForOutDegree += 1;
//                            break;
//                        }
//
////                        System.out.println("upperbandForOutDegree:"+upperbandForOutDegree);
//                    }
//
//
//                }
//
//            }
//
//            outDegreeResult.put(String.valueOf(lowerbandForOutDegree)+"~"+String.valueOf(upperbandForOutDegree),countForOutDegree);
//
//
//
//            /** 写入 in **/
//            //sql
//            String write_sql = String.format("INSERT INTO in_degree%d(dataset_local_id,in_range,count)values(?,?,?);",table_id);
//            //预编译
//            PreparedStatement stt = connection.prepareStatement(write_sql); //预编译SQL，减少sql执行
//
//            for(String cc : inDegreeResult.keySet()){
//                //传参
////                System.out.println("cc:"+cc);
//                stt.setInt(1,dataset_local_id);
//                stt.setString(2,cc);
//                stt.setInt(3,inDegreeResult.get(cc));
//                //执行
//                stt.executeUpdate();
//
//            }
//
//            /** 写入 in **/
//            //sql
//            String write_sql_out = String.format("INSERT INTO out_degree%d(dataset_local_id,out_range,count)values(?,?,?);",table_id);
//            //预编译
//            PreparedStatement stt_out = connection.prepareStatement(write_sql_out); //预编译SQL，减少sql执行
//
//            for(String cc : outDegreeResult.keySet()){
//                //传参
////                System.out.println("cc:"+cc);
//                stt_out.setInt(1,dataset_local_id);
//                stt_out.setString(2,cc);
//                stt_out.setInt(3,outDegreeResult.get(cc));
//                //执行
//                stt_out.executeUpdate();
//
//            }
//
//            connection.close();
//
//        }catch(Exception e){
//            e.printStackTrace();
//
//        }
//
////        result.add(inDegreeResult);
////        result.add(outDegreeResult);
////
////        return result;
//
//
//    }

    public void getID2URI(int tableid, int dataset_local_id) {
        vocabularyCount.clear();
        id2uri.clear();
        literal.clear();
        mess.clear();
        subClassID = -1;


        String selectLabel = String.format("select * from uri_label_id%d where dataset_local_id = %d", tableid, dataset_local_id);

        try {
            PreparedStatement selectStatement = connection_remote.prepareStatement(selectLabel);
            ResultSet resultSet = selectStatement.executeQuery();


            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                String label = resultSet.getString("label");

                String uri = resultSet.getString("uri");

                boolean litr = resultSet.getBoolean("is_literal");

                if ("type".equals(label)) {
                    typeID = id;
                }

                id2uri.put(id, uri);
                if (litr) { //是literal
                    literal.add(id);
                }

//                if(!litr && !(uri.startsWith("http"))){
//
//                    mess.add(id);
//                }
            }
            /**labelID建完*/
            resultSet.close();
            selectStatement.close();

//            connection.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void cal2(int tableid, int dataset_local_id) {


        literalCount = 0;
        literalLength = 0;

        int maxDepth = 0;

        getID2URI(tableid, dataset_local_id);


        String sql = String.format("SELECT * FROM triple%d WHERE dataset_local_id=%d", tableid, dataset_local_id);
        try {
            Statement pst = connection_remote.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,
                    ResultSet.CONCUR_UPDATABLE);
            ResultSet rst = pst.executeQuery(sql);//79个确实全取出来了

            while (rst.next()) {
//                System.out.println("subjectID:"+rst.getInt("subject"));
                String subject = id2uri.get(rst.getInt("subject"));
                String predicate = id2uri.get(rst.getInt("predicate"));
                String object = id2uri.get(rst.getInt("object"));

//                System.out.println("objectID:"+rst.getInt("object"));

//                System.out.println(subject);

                addadd(subject);
                addadd(predicate);


//                System.out.println(literal.contains(rst.getInt("object")));

                if (literal.contains(rst.getInt("object"))) {
//                    System.out.println("qwqwqwqwq");
                    literalLength += object.length();
                    literalCount++;
                } else {
                    addadd(object);
                }

//                System.out.println(literalLength);
            }

//            System.out.println("aaaaa");

//            if(subClassID!=-1){
//
//            }

            List<Map.Entry<String, Integer>> list = new ArrayList<Map.Entry<String, Integer>>(vocabularyCount.entrySet());
            list.sort(new Comparator<Map.Entry<String, Integer>>() {
                @Override
                public int compare(Map.Entry<String, Integer> o1, Map.Entry<String, Integer> o2) {
                    return o2.getValue().compareTo(o1.getValue());
                }
            });


            int literalResult = 0;
            if (literalCount != 0) literalResult = Math.round(literalLength / literalCount);
//            System.out.println();
//            System.out.println(list.get(0).getKey()+":"+list.get(0).getValue());

            String sql_write = String.format("UPDATE statistics%d SET literal_length=%d, max_vocabulary=%s WHERE dataset_local_id=%d", tableid, literalResult, list.get(0).getKey());
            PreparedStatement pst_sql_write = connection_remote.prepareStatement(sql_write);
            pst_sql_write.execute();


        } catch (Exception e) {

        }

    }

    public void addadd(String subject) {
        if (subject.contains(":")) {
            String sub = subject.substring(subject.indexOf(":") + 3, subject.indexOf("/", subject.indexOf(":") + 3));

            if (!vocabularyCount.containsKey(sub)) {
                vocabularyCount.put(sub, 1);
            } else {
                vocabularyCount.put(sub, vocabularyCount.get(sub) + 1);
            }
        }

    }


    public void writeToDatabase(int table_id, int dataset_local_id) { // 存肯定也是存id   Integer

        int type = -1, classDefined = -1;   //TODO   classDefined还需确定！！！！！！！！！！！！！！！！！！！！
        int label = -1, sameAs = -1;

        HashMap<Integer, Integer> classCount = new HashMap<>();
        classCount.clear();
        HashMap<Integer, Integer> propertyCount = new HashMap<>();
        propertyCount.clear();

        HashMap<Integer, Integer> inDegree = new HashMap<>();
        inDegree.clear();
        HashMap<Integer, Integer> outDegree = new HashMap<>();
        outDegree.clear();

        Set<Integer> subjects = new HashSet<>();
        Set<Integer> objects = new HashSet<>();

        //TODO!!!!!!!!1！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！
        //显示未用，真的没有用吗？？？？？？？？？？？？？？？？？？？？？？？？？？？？？？？？？？？？？？？？？？？
        int tripleCount = 0, entityCount = 0, literalCount = 0, typedSubjectCount = 0, labelledSubjectCount = 0, sameAsCount = 0, averageTypedStringLength = 0;
        int propertyUsageCountPerSubject = 0, propertyUsageCountPerObject = 0, classDefinedCount = 0, classUsage = 0, propertyUsage = 0, predicateCount = 0;
        int subjectCount = 0;

        /** average typed string length **/
        //TODO!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!需要有datatype！！！！！！！！！！！！！！！！


        if (connection_remote == null) connection_remote = JdbcUtil.getConnection(GlobalVariances.REMOTE);

        //TODO 写法对吗？？？？？？？？？？？？？？？？？？？
        String sql_type = String.format("SELECT * FROM uri_label_id%d WHERE dataset_local_id=%d;", table_id, dataset_local_id);
//        String sql_label = String.format("SELECT * FROM uri_label_id%d WHERE dataset_local_id=%d;",table_id,dataset_local_id);

        try {
            PreparedStatement pst_typeAndLabel = connection_remote.prepareStatement(sql_type);
            ResultSet rst_typeAndLabel = pst_typeAndLabel.executeQuery();

            while (rst_typeAndLabel.next()) {
                String str_typeAndLabel = rst_typeAndLabel.getString("label");
                int id = rst_typeAndLabel.getInt("id");
                int is_literal = rst_typeAndLabel.getInt("is_literal");

                if ("label".equals(str_typeAndLabel)) {
                    label = id;
                } else if ("type".equals(str_typeAndLabel)) {
                    type = id;
                } else if ("sameAs".equals(str_typeAndLabel) || "seeAlso".equals(str_typeAndLabel)) {
                    sameAs = id;
                } else if ("Class".equals(str_typeAndLabel)) {
                    classDefined = id;//TODO  需确认写法！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！
                }
                //TODO    CLASS DEFINED!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!

                if (is_literal == 1) literal.add(id);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        if (type == -1) {
            //TODO
        }
        if (label == -1) {
            //TODO
        }


        String sql = String.format("SELECT * FROM triple%d WHERE dataset_local_id=%d;", table_id, dataset_local_id);

        try {
            PreparedStatement st = connection_remote.prepareStatement(sql);
            ResultSet rs = st.executeQuery();
            int subjectUpdate = -1;
            while (rs.next())//
            {

                Integer subject = rs.getInt("subject");
                Integer predicate = rs.getInt("predicate");
                Integer object = rs.getInt("object");

                subjects.add(subject);
                objects.add(object);

                /** propertyCount Pie **/
                if (propertyCount.containsKey(predicate)) {
                    propertyCount.put(predicate, propertyCount.get(predicate) + 1);
                } else {
                    propertyCount.put(predicate, 1);
                }

//                if(outDegree.containsKey(subject)){
//                    outDegree.put(subject,outDegree.get(subject)+1);
//                }else{
//                    outDegree.put(subject,1);
//                }

                if (subject != subjectUpdate) { //不对！！！！！因为不一定是连续存的！！！！！！！！！！！！！！！！！！！
                    subjectCount++;
                    subjectUpdate = subject;
                }


                /**tripleCount**/
                tripleCount++;//TODO  真的加上了吗？？？？？？？？？？？？？？？？？？？？？？？？？？？？？？？？


                if (predicate == type) {
                    /**typedSubjectCount**/
                    typedSubjectCount++;

                    /**classCount Pie**/
                    if (classCount.containsKey(object)) {
                        classCount.put(object, classCount.get(object) + 1);
                    } else {
                        classCount.put(object, 1);
                    }
                }


                if (predicate == label) {
                    /** labelledSubjectCount **/
                    labelledSubjectCount++;
                } else if (predicate == sameAs) {
                    /** sameAsCount **/
                    sameAsCount++;
                }


                /** entityCount **/
                entityCount += 2;//subject和predicate一定是entity，重点是这里是要放个数还是种类数！！！！！！！！！！！！！
                if (literal.contains(object)) {

                    /** literalCount **/
                    literalCount++;
                } else {
//                    if(inDegree.containsKey(object)){
//                        inDegree.put(object,inDegree.get(object)+1);
//                    }else{
//                        inDegree.put(object,1);
//                    }
                    entityCount++;
                }
                //TODO  需确认是否没有存 空  的情况！！！！！！！！！！！！！！！！！！！

            }

            /** class usage **/
            classUsage = classCount.size();

            /** property usage **/
            propertyUsage = propertyCount.size();

            try {
                /** property usage count per subject **/
                propertyUsageCountPerSubject = tripleCount / subjects.size();//TODO 注意取整！！！！！！！！！！！！！！！！！！！
                /** property usage count per object **/
                propertyUsageCountPerObject = tripleCount / objects.size();
            } catch (ArithmeticException e) {
                e.printStackTrace();
                System.out.println("除数为零！");
            }


            /** 写入 class count pie **/
            //sql
            String write_sql_class = String.format("INSERT INTO class_count%d(dataset_local_id, class_id, count)values(?,?,?)", table_id);
            //预编译
            st = connection_remote.prepareStatement(write_sql_class); //预编译SQL，减少sql执行

            for (Integer cc : classCount.keySet()) {
                //传参
                st.setInt(1, dataset_local_id);
                st.setInt(2, cc);
                st.setInt(3, classCount.get(cc));
                //执行
                st.execute();
            }

            /** 写入 property count pie **/
            String write_sql_property = String.format("INSERT INTO property_count%d(dataset_local_id, property_id, count)values(?,?,?)", table_id);
            PreparedStatement pst_write_sql_property = connection_remote.prepareStatement(write_sql_property);
            for (Integer pc : propertyCount.keySet()) {

                pst_write_sql_property.setInt(1, dataset_local_id);
                pst_write_sql_property.setInt(2, pc);
                pst_write_sql_property.setInt(3, propertyCount.get(pc));
                pst_write_sql_property.execute();
            }
            pst_write_sql_property.close();

            /** 写入 statistics count **/
            String write_sql_statistics = String.format("INSERT INTO statistics%d(dataset_local_id,triple_count,entity_count,literal_count," +
                    "typed_subject_count,labelled_subject_count,sameAs_count,average_typed_string_length,property_usage_count_per_subject," +
                    "property_usage_count_per_object,class_defined_count,class_usage,property_usage)values(?,?,?,?,?,?,?,?,?,?,?,?,?)", table_id);
            PreparedStatement pst_write_sql_statistics = connection_remote.prepareStatement(write_sql_statistics);
            pst_write_sql_statistics.setInt(1, dataset_local_id);
            pst_write_sql_statistics.setInt(2, tripleCount);
            pst_write_sql_statistics.setInt(3, entityCount);
            pst_write_sql_statistics.setInt(4, literalCount);
            pst_write_sql_statistics.setInt(5, typedSubjectCount);
            pst_write_sql_statistics.setInt(6, labelledSubjectCount);
            pst_write_sql_statistics.setInt(7, sameAsCount);
            pst_write_sql_statistics.setInt(8, averageTypedStringLength);
            pst_write_sql_statistics.setInt(9, propertyUsageCountPerSubject);
            pst_write_sql_statistics.setInt(10, propertyUsageCountPerObject);
            pst_write_sql_statistics.setInt(11, classDefinedCount); //TODO!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
            pst_write_sql_statistics.setInt(12, classUsage); //TODO!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
            pst_write_sql_statistics.setInt(13, propertyUsage); //TODO!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!

            pst_write_sql_statistics.execute();


            //TODO    indegree和outdegree需要排序！！！！！！！！！计算数量


            rs.close();
            st.close();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

    }

    public static LinkedHashMap<Integer, Integer> sortHashMap(HashMap<Integer, Integer> map) {
        //從HashMap中恢復entry集合，得到全部的鍵值對集合
        Set<Map.Entry<Integer, Integer>> entey = map.entrySet();
        //將Set集合轉為List集合，為了實用工具類的排序方法
        List<Map.Entry<Integer, Integer>> list = new ArrayList<Map.Entry<Integer, Integer>>(entey);
        //使用Collections工具類對list進行排序
        Collections.sort(list, new Comparator<Map.Entry<Integer, Integer>>() {
            @Override
            public int compare(Map.Entry<Integer, Integer> o1, Map.Entry<Integer, Integer> o2) {
                //按照age倒敘排列
                return o1.getValue() - o2.getValue();
            }
        });
        //創建一個HashMap的子類LinkedHashMap集合
        LinkedHashMap<Integer, Integer> linkedHashMap = new LinkedHashMap<Integer, Integer>();
        //將list中的數據存入LinkedHashMap中
        for (Map.Entry<Integer, Integer> entry : list) {
            linkedHashMap.put(entry.getKey(), entry.getValue());
        }
        return linkedHashMap;
    }

    public <K, V> Map.Entry<K, V> getHead(LinkedHashMap<K, V> map) {
        return map.entrySet().iterator().next();
    }

    public <K, V> Map.Entry<K, V> getTailByReflection(LinkedHashMap<K, V> map) {
        Iterator<Map.Entry<K, V>> iterator = map.entrySet().iterator();
        Map.Entry<K, V> tail = null;
        while (iterator.hasNext()) {
            tail = iterator.next();
//            System.out.println(tail.getKey());
        }
        return tail;
    }


}
