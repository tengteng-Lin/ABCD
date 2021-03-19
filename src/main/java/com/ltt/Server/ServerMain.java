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



        return edpList;
    }

    public List<JSONObject> getLPPattern(String session_id,Integer dataset_local_id){
        List<JSONObject> lpList = sessionMap.get(session_id).getLP();



        return lpList;
    }



    public List<JSONObject> getExpLOD(String session_id, Integer dataset_local_id){

        return sessionMap.get(session_id).getExoLOD2();

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
