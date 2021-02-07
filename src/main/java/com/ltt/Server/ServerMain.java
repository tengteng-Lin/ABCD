package com.ltt.Server;

import com.alibaba.fastjson.JSONObject;
import com.ltt.Model.Statistics.Statistics;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.MMapDirectory;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;
import org.springframework.web.servlet.ModelAndView;
import com.ltt.Model.*;


import com.ltt.Utils.GlobalVariances;
import org.tartarus.snowball.ext.LithuanianStemmer;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Service
public class ServerMain {

    private ConcurrentMap <String, Session> sessionMap;
    //Singleton
    private static ServerMain instance = new ServerMain();

    public static ServerMain getInstance(){
        return instance;
    }

    private Directory indexDir;

    {
        try
        {
            indexDir = MMapDirectory.open(Paths.get(GlobalVariances.indexDir));
        } catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    private ServerMain(){
        sessionMap = new ConcurrentHashMap<>() ;
    }



    public String newSession() throws IOException, ParseException {
        String sessionId = UUID.randomUUID().toString();
        //TODO   add sessionId to sessionMap
        sessionMap.put(sessionId, new Session(sessionId, indexDir));
        return sessionId;
    }

    public List<Block> getResultList(String sessionId, String keyword, String[] organizations, String[] repostories, String[] licenses, String[] ins) throws IOException, ParseException {
        return sessionMap.get(sessionId).getResultList(keyword, organizations, repostories, licenses, ins);

    }

    public List<String> schemaFilter(String sessionId, int dataset_local_id, String[] data, int type){

        if(type==0) return sessionMap.get(sessionId).schemaFilter(dataset_local_id,data,type);
        else return sessionMap.get(sessionId).schemaFilter(dataset_local_id,data,type);


    }

    public List<String> dataFilter(String sessionId, int dataset_local_id, JSONObject jsonObject){

        return sessionMap.get(sessionId).dataFilter(dataset_local_id,(List<Integer>) jsonObject.get("class"),(List<Integer>) jsonObject.get("property"));
    }


//    public ModelAndView getMetadata(String sessionId,Integer doc_id,Integer dataset_local_id) throws IOException {
//        ModelAndView result = new ModelAndView();
//        //TODO
//
//        System.out.println(sessionMap.get(sessionId).sessionId);
//        Metadata tmp = sessionMap.get(sessionId).getMetadata(dataset_local_id);
//        List<Resource> resources = sessionMap.get(sessionId).getResource(dataset_local_id);
//        HashMap<String,String> extras = sessionMap.get(sessionId).getExtra(dataset_local_id);
//
//        result.setViewName("index.html");
//        result.addObject("metadata",tmp);
//        result.addObject("resource",resources);
//        result.addObject("extra",extras);
//
//
//
////        result.addObject("title",tmp.getTitle());//暂时是null，无数据
//
//        return result;
//    }



    public ModelAndView getSummaryOne(String sessionId,Integer local_id){
        ModelAndView result = new ModelAndView();
        //TODO
        return result;
    }

    public List<List<JSONObject>> getIllustrativeSnippet(String session_Id, Integer dataset_local_id){
        return sessionMap.get(session_Id).getIllustrativeSnippet();
    }

    public List<JSONObject> getPattern(String session_id,Integer dataset_local_id){
        List<JSONObject> patternList = sessionMap.get(session_id).getPattern();

        return patternList;
    }

    public List<JSONObject> getEDPPattern(String session_id,Integer dataset_local_id){
        List<JSONObject> edpList = sessionMap.get(session_id).getEDP();
        System.out.println(edpList);


        return edpList;
    }

    public List<JSONObject> getLPPattern(String session_id,Integer dataset_local_id){
        List<JSONObject> lpList = sessionMap.get(session_id).getLP();

        System.out.println(lpList);


        return lpList;
    }


    public List<List<List<JSONObject>>>  getTripleRank(String session_id, Integer dataset_local_id){

        return sessionMap.get(session_id).getTripleRank();
    }

    public List<JSONObject> getROCKER(String session_id, Integer dataset_local_id){

        return sessionMap.get(session_id).getROCKER();

    }

    public List<List<JSONObject>> getHITS(String session_id, Integer dataset_local_id){


        return sessionMap.get(session_id).getHITS();

    }


    public List<JSONObject> getExpLOD(String session_id, Integer dataset_local_id){

        return sessionMap.get(session_id).getExoLOD2();

    }

    public List<JSONObject> getNamespace(String session_id,Integer dataset_local_id){
        List<JSONObject> namespaces = sessionMap.get(session_id).getNamespace();

        return namespaces;

    }

    public List<JSONObject> getPageRank(String session_id, Integer dataset_local_id){


        return sessionMap.get(session_id).getPageRank();

    }

    public ModelAndView getTopK(String session_id,Integer dataset_local_id){
        ModelAndView modelAndView = new ModelAndView();
        List<List<JSONObject>> topk = sessionMap.get(session_id).getTopK(dataset_local_id);
        modelAndView.addObject("topk",topk);
        modelAndView.setViewName("B-dataPatterns.html");

        return modelAndView;

    }

    public List<List<JSONObject>> getSnippetTopK(String session_id, Integer dataset_local_id){


        return sessionMap.get(session_id).getSnippetTopK();

    }

    public ModelAndView getSchemaFilter(String session_id,Integer dataset_local_id){
        ModelAndView modelAndView = new ModelAndView();
        List<List<JSONObject>> res = sessionMap.get(session_id).getSchemaFilter(dataset_local_id);
        modelAndView.addObject("class",res.get(0));
        modelAndView.addObject("property",res.get(1));
        modelAndView.setViewName("schema-filter.html");

        return modelAndView;

    }


    public ModelAndView getDataFilter(String session_id,Integer dataset_local_id){
        ModelAndView modelAndView = new ModelAndView();
        List<List<JSONObject>> res = sessionMap.get(session_id).getSchemaFilter(dataset_local_id);
        modelAndView.addObject("class",res.get(0));
        modelAndView.addObject("property",res.get(1));
        modelAndView.setViewName("data-filter.html");

        return modelAndView;

    }


    /** ABCD **/
    public ModelAndView getAOverView(String session_id,Integer dataset_local_id){
        Statistics statistics = sessionMap.get(session_id).getStatistics(dataset_local_id);
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.addObject("statistics",statistics);
        modelAndView.addObject("session_id",session_id);
        modelAndView.addObject("dataset_local_id",dataset_local_id);
        modelAndView.setViewName("A-overview.html");

        return modelAndView;

    }

    public List<JSONObject> getExploreClass(String session_id,int dataset_local_id){
        return sessionMap.get(session_id).getExploreClass();
    }

    public List<JSONObject> getExploreProperty(String session_id,int dataset_local_id){
        return sessionMap.get(session_id).getExploreProperty();
    }

    public JSONObject getBasicInfo(String session_id,int dataset_local_id){

        return sessionMap.get(session_id).getBasicinfo(dataset_local_id);
    }

    public List<JSONObject> getResource(String session_id,int dataset_local_id){
        return sessionMap.get(session_id).getResource(dataset_local_id);
    }

    public List<JSONObject> getExtra(String session_id,int dataset_local_id){
        return sessionMap.get(session_id).getExtra(dataset_local_id);
    }




}
