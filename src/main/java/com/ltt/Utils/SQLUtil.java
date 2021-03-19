package com.ltt.Utils;

import com.ltt.DemoApplication;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class SQLUtil {
    private Connection connection;



    public SQLUtil() {
//        try {
        connection = JdbcUtil.getConnection(GlobalVariances.REMOTE);
//        } catch (SQLException throwables) {
//            throwables.printStackTrace();
//        }

    }

    public  List<String> getURIAndLabelForId(int table_id, int dataset_local_id, int id) {

        String uri = "";
        String label = "";
        List<String> result = new ArrayList<>();
        String sql = String.format("SELECT * FROM uri_label_id%d WHERE dataset_local_id=%d AND id = %d", table_id, dataset_local_id, id);
        try {
//            Connection connection = JdbcUtil2.getConnection(GlobalVariances.REMOTE);
            PreparedStatement pst = connection.prepareStatement(sql);
            ResultSet rst = pst.executeQuery();

            if (rst.next()) {
                uri = rst.getString("uri");

                label = rst.getString("label");

                result.add(uri);
                result.add(label);

            }
            rst.close();
            pst.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    public String getURIForId(int table_id, int dataset_local_id, int id) {
//        Connection connection = JdbcUtil2.getConnection(GlobalVariances.REMOTE);
        String uri = "";
        String sql = String.format("SELECT * FROM uri_label_id%d WHERE dataset_local_id=%d AND id = %d", table_id, dataset_local_id, id);
        try {
            PreparedStatement pst = connection.prepareStatement(sql);
            ResultSet rst = pst.executeQuery();
//        System.out.println(rst.);
            if (rst.next()) {
                uri = rst.getString("uri");
//                if(!uri.substring(0,4).equals("http")) uri="";

            }

            rst.close();
            pst.close();

        } catch (Exception e) {
            e.printStackTrace();
        }

        return uri;


    }

    public  String getPrefixAndLabelForId(int table_id, int dataset_local_id, int id) {

        String result = "";
        String sql = String.format("SELECT * FROM namespace%d WHERE dataset_local_id=%d AND id = %d", table_id, dataset_local_id, id);
        try {
            Connection connection = DemoApplication.secondDataSource.getConnection();
            PreparedStatement pst = connection.prepareStatement(sql);
            ResultSet rst = pst.executeQuery();
//        System.out.println(rst.);
            if (rst.next()) {
                result = rst.getString("prefix") + ":" + rst.getString("label");
//            System.out.println("label:"+label);
            }

            rst.close();
            pst.close();
            connection.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;


    }

    public  String getLabelForId(int table_id, int dataset_local_id, int id) {
        String label = "";
        try {
//            Connection connection = DemoApplication.primaryDataSource.getConnection();

            String sql = String.format("SELECT * FROM uri_label_id%d WHERE dataset_local_id=%d AND id = %d", table_id, dataset_local_id, id);

            PreparedStatement pst = connection.prepareStatement(sql);
            ResultSet rst = pst.executeQuery();
            if (rst.next()) {
                label = rst.getString("label");
            }

            rst.close();
            pst.close();

        } catch (Exception e) {
            e.printStackTrace();
        }

        return label;
    }
}
