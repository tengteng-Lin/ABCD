package com.ltt.Pattern;

import com.alibaba.fastjson.JSONObject;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import static com.ltt.Utils.SQLUtil.getLabelForId;


public class Search {
    public static void main(String args[]){
        System.out.println(getLP(1));
    }

    public static List<JSONObject> getEDP(Integer dataset_local_id) {
        List<JSONObject> result = new ArrayList<>();

        int tableid = 2;

        if(dataset_local_id>311){
            dataset_local_id-=311;
            tableid=3;
        }
        String indexReadPath = "D:\\DashBoardIndex\\EDPIndex202010\\";

        try {
            Directory directory = FSDirectory.open(Paths.get(indexReadPath+dataset_local_id.toString()));//打开索引文件夹
            IndexReader reader = DirectoryReader.open(directory);//读取目录

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
                        String name = getLabelForId(tableid,dataset_local_id,Integer.parseInt(outProperty[j]));
                        jsonObject.put("name:",name);

                        jsonObject.put("type",0);
                        jsonObject.put("inOrOut",1);

                        children.add(jsonObject);

                    }
                    if(outProperty.length>0){

                    }
                }

//                System.out.println(doc.get("outProperty"));
                String strInProperty = doc.get("inProperty");
                if(strInProperty.length()!=0){
                    String [] inProperty = strInProperty.trim().split(" "); //很可能没有进入的或出去的

                        for(int j=0;j<inProperty.length;j++){

                            JSONObject jsonObject = new JSONObject();
                            jsonObject.put("name",getLabelForId(tableid,dataset_local_id,Integer.parseInt(inProperty[j])));
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
                        jsonObject.put("name",getLabelForId(tableid,dataset_local_id,Integer.parseInt(classes[j])));
                        jsonObject.put("type",1);
                        jsonObject.put("inOrOut",0);

                        children.add(jsonObject);
                        onePattern.put("children",children);
                    }

                }

                String count = doc.get("count");
//                System.out.println("count:"+count);

                onePattern.put("name",count);

                result.add(onePattern);
            }



//            IndexSearcher search = new IndexSearcher(reader);//初始化查询组件

            reader.close();//关闭资源
            directory.close();//关闭连接
    }catch(Exception e)    {
        e.printStackTrace();
    }

    return result;
}


    public static List<JSONObject> getLP(Integer dataset_local_id) {
        List<JSONObject> result = new ArrayList<>();
        List<JSONObject> fromResult = new ArrayList<>();
        List<JSONObject> toResult = new ArrayList<>();

        int tableid = 2;

        if(dataset_local_id>311){
            dataset_local_id-=311;
            tableid=3;
        }
        String indexReadPath = "D:\\DashBoardIndex\\LPIndex202010\\";

        try {
            Directory directory = FSDirectory.open(Paths.get(indexReadPath+dataset_local_id.toString()));//打开索引文件夹
            IndexReader reader = DirectoryReader.open(directory);//读取目录
//            System.out.println(reader.maxDoc());
            //一个pattern一个document
            for(int i=0;i<reader.maxDoc();i++){//从0开始从1开始？？？？
                JSONObject resultOne = new JSONObject();



                Document doc = reader.document(i);

//                String [] toInProperty = doc.get("ToInProperty").trim().split(" ");
//                String [] toOutProperty = doc.get("ToOutProperty").trim().split(" ");
//
//                String [] toClasses = doc.get("toClasses").trim().split(" ");

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
                        jsonObject.put("name",getLabelForId(tableid,dataset_local_id,Integer.parseInt(fromInProperty[j])));
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
                        jsonObject.put("name",getLabelForId(tableid,dataset_local_id,Integer.parseInt(fromOutProperty[j])));
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
                        jsonObject.put("name",getLabelForId(tableid,dataset_local_id,Integer.parseInt(fromClasses[j])));
                        jsonObject.put("type",1);
                        jsonObject.put("inOrOut",0);

                        children.add(jsonObject);
                        onePattern.put("children",children);
                    }

                }

                fromResult.add(onePattern);


                JSONObject twoPattern = new JSONObject();
                List<JSONObject> children2 = new ArrayList<>();

                String strToInProperty = doc.get("toInProperty");
                if(strToInProperty.length()!=0){
                    String [] toInProperty = strToInProperty.trim().split(" ");
                    for(int j=0;j<toInProperty.length;j++){
                        if(Integer.parseInt(toInProperty[j])==propertyID) continue;
                        JSONObject jsonObject = new JSONObject();
                        jsonObject.put("name",getLabelForId(tableid,dataset_local_id,Integer.parseInt(toInProperty[j])));
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
                        jsonObject.put("name",getLabelForId(tableid,dataset_local_id,Integer.parseInt(toOutProperty[j])));
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
                        jsonObject.put("name",getLabelForId(tableid,dataset_local_id,Integer.parseInt(toClasses[j])));
                        jsonObject.put("type",1);
                        jsonObject.put("inOrOut",0);

                        children2.add(jsonObject);
                        twoPattern.put("children",children2);
                    }

                }

                toResult.add(twoPattern);

                resultOne.put("onePattern",onePattern);
                resultOne.put("twoPattern",twoPattern);
                resultOne.put("count",count);
                resultOne.put("property",  getLabelForId(tableid,dataset_local_id,propertyID));

                result.add(resultOne);

            }

            reader.close();//关闭资源
            directory.close();//关闭连接
        }catch(Exception e)    {
            e.printStackTrace();
        }


        return result;
    }


}
