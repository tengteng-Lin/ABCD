package com.ltt.Snippet;



import com.ltt.Model.Triple.SnippetProperty;
import com.ltt.Model.Triple.SnippetTriple;
import com.ltt.Model.Triple.SnippetTypeClass;
import com.ltt.Utils.GlobalVariances;
import com.ltt.Utils.JdbcUtil;
import com.ltt.Utils.StringUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

public class KSDSnippet {
    Connection connection = JdbcUtil.getConnection(GlobalVariances.REMOTE);
    public int dataset;
    private static int MAX_SIZE = 20; /** 设置输出的snippet规模限制*/

    public KSDSnippet(int dataset){
        connection = JdbcUtil.getConnection(GlobalVariances.REMOTE);
        this.dataset = dataset;
    }

    public void close(){
        try {
            connection.close();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public Set<SnippetTriple> findSnippet(List<String>keywords){
        createTriples();
        setWeight(keywords);
        getSnippet(keywords);
        return result;
    }

    int T; /**triple总数 */
    int C = 0; /**s-type-class的总数 */
    private List<SnippetTriple> tpHeap = null;
    private Map<Integer, SnippetProperty> pmap= new HashMap<>();
    private Map<Integer, SnippetTypeClass> cmap = new HashMap<>();
    private Map<Integer, Integer> entout = new HashMap<>();
    private Map<Integer, Integer> entin = new HashMap<>();
    private Set<Integer> literal = new HashSet<>();//literal的id集合
    int typeID;/**用来存对应http://www.w3.org/1999/02/22-rdf-syntax-ns#type的predicate的id，直接查表*/
    int maxin = 0, maxout = 0; //记录数据集中entity的最大出入度
    double mo, mi;//maxin和maxout的log值和
    boolean[] iscovered = null;//记录关键词有没有被覆盖

    public Set<SnippetTriple> result = new HashSet<>();/**记录当前结果*/
    private Map<Integer, String> labelID;
    private Map<Integer,String> uriID;

    public void createTriples(){//读数据库，生成triple
        tpHeap = new ArrayList<>();
        labelID = new HashMap<>();//id -> label
        uriID = new HashMap<>();//id -> uri
        String selectTYPE = "select type_id from dataset_info3 where dataset_local_id = " + dataset;
        String selectLabel = "select id,label,is_literal,uri from uri_label_id? where dataset_local_id = ?";
        String selectTriple = "select subject,predicate,object from triple? where dataset_local_id = ?";
        try {
            PreparedStatement selectStatement = connection.prepareStatement(selectTYPE);
            ResultSet resultSet = selectStatement.executeQuery();
            if (resultSet.next())
            {
                typeID = resultSet.getInt("type_id");
            }
            /**找到typeID*/

            selectStatement = connection.prepareStatement(selectLabel);
            if (dataset <= 311){
                selectStatement.setInt(1, 2);
                selectStatement.setInt(2, dataset);
            }else {
                selectStatement.setInt(1, 3);
                selectStatement.setInt(2, (dataset-311));
            }
            resultSet = selectStatement.executeQuery();
            while (resultSet.next()){
                int id = resultSet.getInt("id");
                String label = resultSet.getString("label");
                String uri = resultSet.getString("uri");
                boolean litr = resultSet.getBoolean("is_literal");
                labelID.put(id, label);
                uriID.put(id,uri);
                if (litr){ //是literal
                    literal.add(id);
                }
            }
            /**labelID建完*/
            if (typeID != 0){
                SnippetProperty prop = new SnippetProperty();
                prop.setName(labelID.get(typeID));
                pmap.put(typeID, prop);
            }
            selectStatement = connection.prepareStatement(selectTriple);
            if (dataset <= 311){
                selectStatement.setInt(1, 2);
                selectStatement.setInt(2, dataset);
            }else {
                selectStatement.setInt(1, 3);
                selectStatement.setInt(2, (dataset-311));
            }
            resultSet = selectStatement.executeQuery();
            while(resultSet.next()){
                int s = resultSet.getInt("subject");
                int p = resultSet.getInt("predicate");
                int o = resultSet.getInt("object");
                SnippetTriple t = new SnippetTriple();
                t.s_id = s;
                if (!entout.containsKey(s)){
                    entout.put(s, 1);
                }
                else entout.put(s, entout.get(s)+1);/**出度+1 */

                //换行
                t.subject = StringUtil.strBreakForSPO(labelID.get(s));

                t.p_id = p;
                if (p == typeID){ //S-TYPE-Class
                    C++;
                    t.predicate = "type";
                    t.o_id = o;
                    String cls = labelID.get(o);
                    t.object = cls;
                    if (!cmap.containsKey(o)){/**加入cMap */
                        SnippetTypeClass tempClass = new SnippetTypeClass();
                        tempClass.setName(cls);
                        tempClass.add();
                        cmap.put(o, tempClass);
                    }
                    else{//数量+1
                        SnippetTypeClass tempClass = cmap.get(o);
                        tempClass.add();
                        cmap.put(o, tempClass);
                    }
                }
                else{ //S-非TYPE的property-value(entity/literal)
                    String prop = labelID.get(p);
                    t.predicate = prop;
                    if (!pmap.containsKey(p)){//加入pMap
                        SnippetProperty prp = new SnippetProperty();
                        prp.setName(prop);
                        prp.add();
                        pmap.put(p, prp);
                    }
                    else{//数量+1
                        SnippetProperty prp = pmap.get(p);
                        prp.add();
                        pmap.put(p, prp);
                    }
                    t.o_id = o;
//                    t.object = labelID.get(o);
                    //换行
                    t.object = StringUtil.strBreakForSPO(labelID.get(o));

                    if (!literal.contains(o)){//不是literal
                        if(!entin.containsKey(o)){
                            entin.put(o, 1);
                        }
                        else entin.put(o, entin.get(o)+1);
                    }
                }
                tpHeap.add(t);
            }
//            connection.close();
        }catch (Exception e){
            e.printStackTrace();
        }
        T = tpHeap.size();
        mo = 0;
        mi = 0;
        for (int i: entout.values()){
            if (maxout < i) maxout = i;
            mo += Math.log((double)i + 1);
        }
        for (int i: entin.values()){
            if ((maxin < i))maxin = i;
            mi += Math.log((double)i + 1);
        }
    }

    public void setWeight(List<String> keywords){
        /** 注意给triple加权，要分词
         * entity, SnippetProperty, class, literal */
        double keyWeight = (double)1/keywords.size();
        iscovered = new boolean[keywords.size()];
        int count;
        for (SnippetTriple iter: tpHeap){/**一轮循环处理一个triple */
            count = 0;
            String labels = StringUtil.splitLabel(iter.subject+" "+iter.predicate+" "+iter.object);
            for (String s: keywords){
                if(labels.contains(s)){
                    count++;
                }
            }
            iter.kweight = count * keyWeight;/**覆盖关键词的权重 */
            int o = iter.o_id;
            int p = iter.p_id;
            if (cmap.containsKey(o)){
                iter.cweight = cmap.get(o).getCount()/C;
            }
            if (pmap.containsKey(p)){
                iter.pweight = pmap.get(p).getCount()/T;
            } /**覆盖class和property的权重 */

            int s = iter.s_id;/**S一定是entity */
            double dw1 = Math.log((double)entout.get(s)+1)/mo;
            double dw2 = 0;
            if (entin.containsKey(s)){
                dw2 = Math.log((double)entin.get(s)+1)/mi;
            }
            if (entin.containsKey(o)){/**说明O是entity */
                if (entout.containsKey(o)){
                    dw1 += Math.log((double)entout.get(o)+1)/mo;
                }
                dw2 += Math.log((double)entin.get(o)+1)/mi;
            }
            iter.oweight = dw1;
            iter.iweight = dw2;
            iter.setW();
        }
    }

    public Set<SnippetTriple> getSnippet(List<String> keywords){
        double keyWeight = (double)1/keywords.size();
        Set<String> word;
        for (int i = 1; i <= MAX_SIZE; i++){
            if (tpHeap.size() == 0)break;
            Collections.sort(tpHeap);
            SnippetTriple t = tpHeap.get(0);
            result.add(t);
            tpHeap.remove(0);
            word = new HashSet<>();
            /**然后根据选中的来改权重 */
            if (t.kweight > 0){
                String labels = StringUtil.splitLabel(t.subject+" "+t.predicate+" "+t.object);
                for (int j = 0; j < keywords.size(); j++){
                    if (!iscovered[j] && labels.contains(keywords.get(j))){
                        word.add(keywords.get(j));/**新增的覆盖的keywords */
                        iscovered[j] = true;
                    }
                }
            }
            for (SnippetTriple iter: tpHeap){
                double kw = iter.kweight;
                if (kw > 0){
                    int count = 0;
                    String templabels = StringUtil.splitLabel(iter.subject+" "+iter.predicate+" "+iter.object);
                    for (String s: word){
                        if(templabels.contains(s)){
                            count++;
                        }
                    }
                    iter.kweight = kw - count * keyWeight;/**覆盖关键词的权重 */
                }
                if (iter.cweight > 0 && iter.o_id == t.o_id)iter.cweight = 0;
                if (iter.pweight > 0 && iter.p_id == t.p_id)iter.pweight = 0;
                iter.setW();
            }
        }
        return result;
    }

    private List<SnippetTriple> readSnippetFromDB(String suffix)
    {
        String sniString = "";
        String table_name = "dataset_snippet_result_" + suffix;
        String sql = String.format("SELECT * FROM %s WHERE dataset_local_id=%d;", table_name, dataset);
        try
        {
            PreparedStatement st = connection.prepareStatement(sql);
            ResultSet rs = st.executeQuery();
            while(rs.next())
            {
                sniString = rs.getString("snippet");
            }
            rs.close(); st.close();
        } catch (SQLException throwables)
        {
            throwables.printStackTrace();
        }
        Integer index = sniString.indexOf(";");
        sniString = sniString.substring(index + 1);
        String[] snis = sniString.split(",");
        List<SnippetTriple> res = new ArrayList<>(); res.clear();
        for(String sni : snis)
        {
            String[] sn = sni.split(" ");
            if(sn.length < 3)
            {
                continue;
            }

            int sub_type=1;
            int pre_type=1;
            int obj_type=2;
            if(Integer.parseInt(sn[2]) == 2){//type
                pre_type=0;
                sub_type=0;
                obj_type=0;
            }

            if(literal.contains(Integer.parseInt(sn[1]))){
                obj_type=1;//literal
            }

            res.add( new SnippetTriple(
                    StringUtil.strBreakForSPO(labelID.get(Integer.parseInt(sn[0]))),
                    StringUtil.strBreakForSPO(labelID.get(Integer.parseInt(sn[2]))),
                    StringUtil.strBreakForSPO(labelID.get(Integer.parseInt(sn[1]))),
                    StringUtil.strBreakForSPO(uriID.get(Integer.parseInt(sn[2]))),

                    sub_type,pre_type,obj_type



            ) );
        }
        return res;
    }

    //TODO
    public Map<String, List<SnippetTriple>> getAllTypeSnippet()
    {
        Map<String, List<SnippetTriple>> res = new HashMap<>(); res.clear();
        res.put("KSD", new ArrayList<>(result));

        res.put("new_1000s", readSnippetFromDB("new_1000s"));
        try {
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return res;
    }

    public List<SnippetTriple> getQuerySnippet(){
        return new ArrayList<>(result);
    }

    public List<SnippetTriple> getDbSnippet(){
        labelID = new HashMap<>();//id -> label
        uriID = new HashMap<>();//id -> uri

        String selectLabel = "select id,label,is_literal,uri from uri_label_id? where dataset_local_id = ?";

        try {
            PreparedStatement selectStatement = connection.prepareStatement(selectLabel);
//            ResultSet resultSet = selectStatement.executeQuery();

            if (dataset <= 311){
                selectStatement.setInt(1, 2);
                selectStatement.setInt(2, dataset);
            }else {
                selectStatement.setInt(1, 3);
                selectStatement.setInt(2, (dataset-311));
            }
            ResultSet resultSet = selectStatement.executeQuery();
            while (resultSet.next()){
                int id = resultSet.getInt("id");
                String label = resultSet.getString("label");
                String uri = resultSet.getString("uri");
                boolean litr = resultSet.getBoolean("is_literal");
                labelID.put(id, label);
                uriID.put(id,uri);
                if (litr){ //是literal
                    literal.add(id);
                }
            }
            /**labelID建完*/

//            connection.close();
        }catch (Exception e){
            e.printStackTrace();
        }
        return readSnippetFromDB("new_1000s");
    }



}
