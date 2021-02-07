package com.ltt.test;

import com.alibaba.fastjson.JSONObject;
import org.apache.log4j.Logger;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

import java.nio.file.Paths;
import java.util.*;

public class Entry11 {
    private static final Logger log = Logger.getLogger(Entry11.class);

    public static void main(String args[]){
        Schema schema = new Schema();
//        schema.getDegree2(2,1);
        for(int i=1;i<=311;i++){
            schema.typeID=-1;
            schema.literal.clear();
            schema.getDegree2(2,i);
            System.out.println("dataset "+i+" end!");
        }
//        for(int i=1;i<=311;i++){
//            schema.property2entity.clear();
//            schema.class2entity.clear();
//            schema.literal.clear();
//            schema.mess.clear();
//            schema.typeID=-1;
//            schema.getExpLOD(2,i);
//            System.out.println("dataset "+i+" end!");
//        }
//        schema.getFilterForSchema(2,1);
//        schema.getExpLOD(2,1);
//        for(int i=1;i<=9318;i++){
//            schema.property2entity.clear();
//            schema.class2entity.clear();
//            schema.getFilterForSchema(3,i);
//            System.out.println("dataset " + i + " end!");  //
//        }

//        schema.getFilterForSchema(2,1);
//        testSearchFilter(1);
//        testSearchExpLOD(1);


//        FileModel.writeToFile("123[]#[]123\\,\n212\n",new File("1.txt"));

//        Set<Integer> set1 = new HashSet<>();
//        Set<Integer> set2 = new HashSet<>();
//
//        set1.add(1);set2.add(1);set2.add(2);
//
//        Set<Integer> set3 = new HashSet<>();
//        Set<Integer> set4 = new HashSet<>();
//
//        set3.add(1);set4.add(1);set4.add(2);
//
//        InstanceFeature instanceFeature = new InstanceFeature(set1,set2);
//        InstanceFeature instanceFeature1 = new InstanceFeature(set3,set4);
//        System.out.println(instanceFeature.equals(instanceFeature1));
//
//        HashMap<InstanceFeature,Integer> java.com.ltt.test = new HashMap<>();
//        java.com.ltt.test.put(instanceFeature,1);
//        System.out.println(java.com.ltt.test.get(instanceFeature1));

//        schema.getDegree2(2,1);
//        List<LinkedHashMap<String,Integer>> res = schema.getDegree(2,1);
//        for(String key : res.get(0).keySet()){
//            System.out.println("key:"+key+",value:"+res.get(0).get(key));
//        }
//
//        System.out.println("====================================");
//        for(String key : res.get(1).keySet()){
//            System.out.println("key:"+key+",value:"+res.get(1).get(key));
//        }

//        schema.cal2(2,1);
//        System.out.println("=========================================================================================");
//        schema.cal2(2,2);
//        schema.writeToDatabase(2,306);
//        for(int i=1;i<=311;i++){
//            schema.getDegree2(2,i);
////            schema.writeToDatabase(2,i);
//            log.info("dataset_local_id="+i+" end! ");
//
//        }
//        testSearchFilter(1);

    }

    public static void testSearchFilter(int dataset_local_id){
        int tableid = 2;

        if(dataset_local_id>311){
            dataset_local_id-=311;
            tableid=3;
        }
        String indexReadPath = "D:\\Filter\\"+tableid+"\\Property\\";

        try {
            Directory directory = FSDirectory.open(Paths.get(indexReadPath+String.valueOf(dataset_local_id)));//打开索引文件夹
            IndexReader reader = DirectoryReader.open(directory);//读取目录


            for(int i=0;i<reader.maxDoc();i++){//从0开始从1开始？？？？


                //一个group一个doucument
                Document doc = reader.document(i);
                String property = doc.get("property");
                System.out.println(property);
//                String[] propertyArr = property.split(" *\\** ");

                String classes = doc.get("entity");
                System.out.println(classes);



            }


            reader.close();//关闭资源
            directory.close();//关闭连接
        }catch(Exception e)    {
            e.printStackTrace();
        }

    }

    public static void testSearchExpLOD(int dataset_local_id){
        int tableid = 2;

        if(dataset_local_id>311){
            dataset_local_id-=311;
            tableid=3;
        }
        String indexReadPath = "D:\\ExpLOD\\";

        try {
            Directory directory = FSDirectory.open(Paths.get(indexReadPath+String.valueOf(dataset_local_id)));//打开索引文件夹
            IndexReader reader = DirectoryReader.open(directory);//读取目录

            //一个pattern一个document
            for(int i=0;i<reader.maxDoc();i++){//从0开始从1开始？？？？
                JSONObject onePattern = new JSONObject();
                List<JSONObject> children = new ArrayList<>();

                //一个group一个doucument
                Document doc = reader.document(i);
                String property = doc.get("property");
                String[] propertyArr = property.split(" *\\** ");

                String classes = doc.get("class");
                String[] classArr = property.split(" *\\** ");

                String instanceStr = doc.get("instanceStr");



            }


            reader.close();//关闭资源
            directory.close();//关闭连接
        }catch(Exception e)    {
            e.printStackTrace();
        }

    }
}
