package com.ltt.Server;

//import com.sun.org.apache.xpath.internal.operations.Mod;
import com.alibaba.fastjson.JSONObject;
import org.apache.lucene.queryparser.classic.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import com.ltt.Model.ReturnOne;

import javax.sql.DataSource;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

/**
 * mapping
 */
@Controller
public class HelloController {
//    @Value("${name}")
//    private String name;

    @RequestMapping("/hello")
    public String hello(String asd){
        System.out.println(asd);
        return "index";
    }

    @GetMapping("/first")
    public String index(){
        return "first";
    }

//    @RequestMapping("/getMetadata")
//    public ModelAndView getMetadata(String local_id,String session_id, String id) throws IOException {
//        System.out.println(id);
//        return ServerMain.getInstance().getMetadata(session_id,Integer.parseInt(id),Integer.parseInt(local_id));
//    }

//    @RequestMapping("getSchemaStatistics")
//    public ModelAndView getSchemaStatistics(String session_id, Integer local_id){
//        return ServerMain.getInstance().getSchemaStatistics(session_id,local_id);
//    }

    @RequestMapping("getSummaryOne")
    public ModelAndView getSummaryOne(String session_id, Integer local_id){
        return ServerMain.getInstance().getSummaryOne(session_id,local_id);
    }

    //only json,without view
    @ResponseBody
    @RequestMapping(value = "/search",method = RequestMethod.GET)
    public ModelAndView getResultList(@RequestParam String keyword) throws IOException, ParseException {

        String sessionId = ServerMain.getInstance().newSession();
        return ServerMain.getInstance().getAOverView(sessionId,Integer.parseInt(keyword));


    }

    //only json,without view
    @ResponseBody
    @RequestMapping(value = "/dataFilter",method = RequestMethod.GET)
    public List<String> dataFilter(@RequestParam String session_id, @RequestParam String dataset_local_id, @RequestParam String data) throws IOException, ParseException {


        JSONObject jsonObject = JSONObject.parseObject(data);

        System.out.println(jsonObject);

//        return ServerMain.getInstance().dataFilter(session_id,Integer.parseInt(dataset_local_id),data);
        return ServerMain.getInstance().dataFilter(session_id,Integer.parseInt(dataset_local_id),jsonObject);
    }

    @ResponseBody
    @RequestMapping(value = "/schemaFilter",method = RequestMethod.GET)
    public List<String> schemaFilter(@RequestParam String session_id, @RequestParam String dataset_local_id, @RequestParam(value = "data[]") String[] data, @RequestParam int type) throws IOException, ParseException {
        return ServerMain.getInstance().schemaFilter(session_id,Integer.parseInt(dataset_local_id),data,type);
    }

    @ResponseBody
    @RequestMapping(value = "/getIllustrative",method = RequestMethod.GET)
    public List<List<JSONObject>> getIllustrativeSnippet(@RequestParam String session_id, @RequestParam String dataset_local_id){
        return ServerMain.getInstance().getIllustrativeSnippet(session_id,Integer.parseInt(dataset_local_id));
    }


    @ResponseBody
    @RequestMapping(value = "/getPAnDA",method = RequestMethod.GET)
    public List<JSONObject> getPattern(@RequestParam String session_id, @RequestParam String dataset_local_id){
        System.out.println(session_id);
        return ServerMain.getInstance().getPattern(session_id,Integer.parseInt(dataset_local_id));
    }


    @ResponseBody
    @RequestMapping(value = "/getEDP",method = RequestMethod.GET)
    public List<JSONObject> getEDPPattern(@RequestParam String session_id,@RequestParam String dataset_local_id){

        return ServerMain.getInstance().getEDPPattern(session_id,Integer.parseInt(dataset_local_id));
    }

    @ResponseBody
    @RequestMapping(value = "/getLP",method = RequestMethod.GET)
    public List<JSONObject> getLPPattern(@RequestParam String session_id,@RequestParam String dataset_local_id){

        return ServerMain.getInstance().getLPPattern(session_id,Integer.parseInt(dataset_local_id));
    }


    @ResponseBody
    @RequestMapping(value = "/getTripleRank",method = RequestMethod.GET)
    public List<List<List<JSONObject>>> getTripleRank(@RequestParam String session_id, @RequestParam String dataset_local_id){
            return ServerMain.getInstance().getTripleRank(session_id,Integer.parseInt(dataset_local_id));

    }

    @ResponseBody
    @RequestMapping(value = "/getPageRank",method = RequestMethod.GET)
    public List<JSONObject> getPageRank(@RequestParam String session_id, @RequestParam String dataset_local_id){
        return ServerMain.getInstance().getPageRank(session_id,Integer.parseInt(dataset_local_id));
    }

    @ResponseBody
    @RequestMapping(value = "/getTopK",method = RequestMethod.GET)
    public List<List<JSONObject>> getTopK(@RequestParam String session_id, @RequestParam String dataset_local_id){
        return ServerMain.getInstance().getSnippetTopK(session_id,Integer.parseInt(dataset_local_id));
    }

    @ResponseBody
    @RequestMapping(value = "/getROCKER",method = RequestMethod.GET)
    public List<JSONObject> getROCKER(@RequestParam String session_id, @RequestParam String dataset_local_id){
        return ServerMain.getInstance().getROCKER(session_id,Integer.parseInt(dataset_local_id));
    }

    @ResponseBody
    @RequestMapping(value = "/getTopEntity",method = RequestMethod.GET)
    public List<List<JSONObject>> getTopEntity(@RequestParam String session_id, @RequestParam String dataset_local_id){
        return ServerMain.getInstance().getSnippetTopK(session_id,Integer.parseInt(dataset_local_id));

    }

    @ResponseBody
    @RequestMapping(value = "/getHITS",method = RequestMethod.GET)
    public List<List<JSONObject>> getHITS(@RequestParam String session_id, @RequestParam String dataset_local_id){
        return ServerMain.getInstance().getHITS(session_id,Integer.parseInt(dataset_local_id));

    }

    @RequestMapping(value = "/getSchemaFilter",method = RequestMethod.GET)
    public ModelAndView getSchemaFilter(@RequestParam String session_id,@RequestParam String dataset_local_id){
        return ServerMain.getInstance().getSchemaFilter(session_id,Integer.parseInt(dataset_local_id));

    }

    @ResponseBody
    @RequestMapping(value = "/getExploreClass",method = RequestMethod.GET)
    public List<JSONObject> getExploreClass(@RequestParam String session_id, @RequestParam String dataset_local_id){
        return ServerMain.getInstance().getExploreClass(session_id,Integer.parseInt(dataset_local_id));

    }

    @ResponseBody
    @RequestMapping(value = "/getExploreProperty",method = RequestMethod.GET)
    public List<JSONObject> getExploreProperty(@RequestParam String session_id, @RequestParam String dataset_local_id){
        return ServerMain.getInstance().getExploreProperty(session_id,Integer.parseInt(dataset_local_id));

    }


    @ResponseBody
    @RequestMapping(value = "/getExpLOD",method = RequestMethod.GET)
    public List<JSONObject> getExpLOD(@RequestParam String session_id, @RequestParam String dataset_local_id){
        return ServerMain.getInstance().getExpLOD(session_id,Integer.parseInt(dataset_local_id));

    }

//    @RequestMapping(value = "/getNamespace",method = RequestMethod.GET)
//    public ModelAndView getNamespace(@RequestParam String session_id,@RequestParam String dataset_local_id){
//        return ServerMain.getInstance().getNamespace(session_id,Integer.parseInt(dataset_local_id));
//
//    }

    @RequestMapping(value = "/getDataFilter",method = RequestMethod.GET)
    public ModelAndView getDataFilter(@RequestParam String session_id,@RequestParam String dataset_local_id){
        return ServerMain.getInstance().getDataFilter(session_id,Integer.parseInt(dataset_local_id));

    }



    /** ABCD **/
    @RequestMapping(value = "/getAoverview",method = RequestMethod.GET)
    public ModelAndView getAoverview(@RequestParam String session_id,@RequestParam String dataset_local_id) throws SQLException {
        return ServerMain.getInstance().getAOverView(session_id,Integer.parseInt(dataset_local_id));
    }

    //only json,without view
    @ResponseBody
    @RequestMapping(value = "/getnamespace",method = RequestMethod.GET)
    public List<JSONObject> getNamespace(@RequestParam String session_id, @RequestParam String dataset_local_id) throws IOException, ParseException {

        List<JSONObject> result = ServerMain.getInstance().getNamespace(session_id,Integer.parseInt(dataset_local_id));

        return result;
    }

    @ResponseBody
    @RequestMapping(value = "/getBasicInfo",method = RequestMethod.GET)
    public JSONObject getBasicInfo(@RequestParam String session_id, @RequestParam String dataset_local_id){
        JSONObject result = ServerMain.getInstance().getBasicInfo(session_id,Integer.parseInt(dataset_local_id));
        return result;
    }

    @ResponseBody
    @RequestMapping(value = "/getresource",method = RequestMethod.GET)
    public List<JSONObject> getResource(@RequestParam String session_id, @RequestParam String dataset_local_id){
        List<JSONObject> result = ServerMain.getInstance().getResource(session_id,Integer.parseInt(dataset_local_id));
        return result;
    }

    @ResponseBody
    @RequestMapping(value = "/getextra",method = RequestMethod.GET)
    public List<JSONObject> getExtra(@RequestParam String session_id, @RequestParam String dataset_local_id){
        List<JSONObject> result = ServerMain.getInstance().getExtra(session_id,Integer.parseInt(dataset_local_id));
        return result;
    }













}
