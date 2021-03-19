package com.ltt.Utils;

import com.zaxxer.hikari.HikariConfig;

import javax.sql.DataSource;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class JdbcUtil {
    private static String URL_REMOTE;
    private static String JDBC_DRIVER;
    private static String USER_NAME_REMOTE;
    private static String PASSWORD_REMOTE;

    private static String URL_LOCAL;

    private static String USER_NAME_LOCAL;
    private static String PASSWORD_LOCAL;



    private static HikariConfig config_remote = new HikariConfig();
    private static DataSource ds_remote = null;

    private static HikariConfig config_local = new HikariConfig();
    private static DataSource ds_local = null;

    /*
     * 静态代码块，类初始化时加载数据库驱动
     */
    static {
        try {
            // 加载dbinfo.properties配置文件
            InputStream in = JdbcUtil.class.getClassLoader()
                    .getResourceAsStream("JavaUtil.properties");
            Properties properties = new Properties();
            properties.load(in);
//
//            // 获取驱动名称、url、用户名以及密码
            JDBC_DRIVER = properties.getProperty("JDBC_DRIVER");
            URL_REMOTE = properties.getProperty("URL_REMOTE");
            USER_NAME_REMOTE = properties.getProperty("USER_NAME_REMOTE");
            PASSWORD_REMOTE = properties.getProperty("PASSWORD_REMOTE");


            URL_LOCAL  = properties.getProperty("URL_LOCAL");
            USER_NAME_LOCAL = properties.getProperty("USER_NAME_LOCAL");
            PASSWORD_LOCAL = properties.getProperty("PASSWORD_LOCAL");
//
//
//            // 加载驱动
            Class.forName(JDBC_DRIVER);
//            Class.forName(JDBC_DRIVER);

//            config_local.setJdbcUrl(URL_LOCAL);
//            config_local.setUsername(USER_NAME_LOCAL);
//            config_local.setPassword(PASSWORD_LOCAL);
//            config_local.addDataSourceProperty("connectionTimeout", "3000"); // 连接超时：3秒
//            config_local.addDataSourceProperty("idleTimeout", "60000"); // 空闲超时：60秒
//            config_local.addDataSourceProperty("maximumPoolSize", "10"); // 最大连接数：10
//            ds_local = new HikariDataSource(config_local);

//            config_remote.setJdbcUrl(URL_REMOTE);
//            config_remote.setUsername(USER_NAME_REMOTE);
//            config_remote.setPassword(PASSWORD_REMOTE);
//            config_remote.addDataSourceProperty("connectionTimeout", "3000"); // 连接超时：3秒
//            config_remote.addDataSourceProperty("idleTimeout", "60000"); // 空闲超时：60秒
//            config_remote.addDataSourceProperty("maximumPoolSize", "35"); // 最大连接数：10
//            ds_remote = new HikariDataSource(config_remote);



        } catch (IOException e) {
            e.printStackTrace();
        }
        catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static Connection getConnection(int type)
    {
        Integer failedCnt = 0;
        while(failedCnt < 3)
        {
            try
            {
                //URL = URL.replace("needreplace", GlobalVariances.Database_name);
                if(type == GlobalVariances.REMOTE){
                    return DriverManager.getConnection(URL_REMOTE, USER_NAME_REMOTE, PASSWORD_REMOTE);
                }else{
                    return DriverManager.getConnection(URL_LOCAL, USER_NAME_LOCAL, PASSWORD_LOCAL);
                }

            } catch (SQLException e)
            {
                e.printStackTrace();
                ++ failedCnt;
                continue;
            }
        }
        return null;
    }


    public static Connection getRemoteConnection()
    {
//        try {
////            return DriverManager.getConnection(URL_REMOTE, USER_NAME_REMOTE, PASSWORD_REMOTE);
//            Connection connection = ds_remote.getConnection();
//            return connection;
//        } catch (SQLException e) {
//            // TODO Auto-generated catch block
//            System.out.println(e);
//            e.printStackTrace();
//        }
        System.out.println("Get New Remote Connection Failed");
        return null;
    }

    public static Connection getLocalConnection()
    {
//        try {
////            return DriverManager.getConnection(URL_LOCAL, USER_NAME_LOCAL, PASSWORD_LOCAL);
//            Connection connection = ds_local.getConnection();
//            return connection;
//        } catch (SQLException e) {
//            // TODO Auto-generated catch block
//            e.printStackTrace();
//        }
        System.out.println("Get New Local Connection Failed");
        return null;
    }
}