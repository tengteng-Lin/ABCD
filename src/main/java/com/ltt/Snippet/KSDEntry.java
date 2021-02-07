package com.ltt.Snippet;



import com.ltt.Model.Triple.SnippetTriple;
import com.ltt.Utils.GlobalVariances;

import java.util.*;

public class KSDEntry
{
    private Map<Integer, Map<String, List<SnippetTriple>>> results;
    private Map<Integer,List<SnippetTriple>> querySnippet;
//    private List<SnippetTriple> querySnippet;
    //<dataset_id,<query,……>>

    private void getSnippetResults(List<String> keywords, List<Integer> datasets){
        results = new HashMap<>(); results.clear();
        querySnippet = new HashMap<>(); querySnippet.clear();

        List<KSDSnippet> finderList = new ArrayList<>();
        List<CustomedThread> threadList = new ArrayList<>();
        List<Long>startTime = new ArrayList<>();
        for (Integer dataset: datasets){
            KSDSnippet finder = new KSDSnippet(dataset);
            finderList.add(finder);
            CustomedThread thread = new CustomedThread(finder, dataset, keywords);
            thread.start();
            startTime.add(System.currentTimeMillis());
            threadList.add(thread);
        }
        try
        {
            for (int i = 0; i < threadList.size(); i++){
                long usedTime = System.currentTimeMillis() - startTime.get(i);
                CustomedThread thread = threadList.get(i);
                if (usedTime <= GlobalVariances.TIMEOUT)
                    thread.join(GlobalVariances.TIMEOUT - usedTime);
                if (thread.isAlive())thread.interrupt();
            }
        } catch (InterruptedException e)
        {
            e.printStackTrace();
        }
        for (KSDSnippet iter: finderList) {
            saveResult(iter);
        }
    }

    private void saveResult(KSDSnippet finder){
        List<SnippetTriple> list = new ArrayList<>(); list.clear();
        for(SnippetTriple tri : finder.result)
        {
            list.add(tri);
        }
        querySnippet.put(finder.dataset,finder.getQuerySnippet());



        //TODO
//        results.put(finder.dataset, finder.getAllTypeSnippet());
    }

    public Map<Integer, Map<String, List<SnippetTriple>>> getResults()
    {
        return results;
    }

    private static class CustomedThread extends Thread{
        KSDSnippet finder;
        int datasetId;
        List<String> keywords;
        Set<SnippetTriple> result;
        public CustomedThread(KSDSnippet finder, int datasetId, List<String> keywords) {
            super();
            this.finder = finder;
            this.datasetId = datasetId;
            this.keywords = keywords;
        }
        @Override
        public void run(){
            try {
                result = finder.findSnippet(keywords);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public Map<Integer, Map<String, List<SnippetTriple>>> batchGetSnippet(List<String> keywords, List<Integer> datasets)
    {
        getSnippetResults(keywords, datasets);
        return getResults();
    }

    public Map<Integer, List<SnippetTriple>> getQuerySnippet(List<String> keywords, List<Integer> datasets){
        getSnippetResults(keywords, datasets);
        return querySnippet;
    }

    public List<SnippetTriple> getDbSnippet(Integer local_id){
        KSDSnippet finder = new KSDSnippet(local_id);
        return finder.getDbSnippet();

//        readSnippetFromDB("new_1000s")

    }

//    public
}
