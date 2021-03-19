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
    @RequestMapping(value = "/getExpLOD",method = RequestMethod.GET)
    public List<JSONObject> getExpLOD(@RequestParam String session_id, @RequestParam String dataset_local_id){
        return ServerMain.getInstance().getExpLOD(session_id,Integer.parseInt(dataset_local_id));

    }



    /** ABCD **/
    @RequestMapping(value = "/getAoverview",method = RequestMethod.GET)
    public ModelAndView getAoverview(@RequestParam String session_id,@RequestParam String dataset_local_id) throws SQLException {
        return ServerMain.getInstance().getAOverView(session_id,Integer.parseInt(dataset_local_id));
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
