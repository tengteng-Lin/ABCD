package com.ltt;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.ComponentScan;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.ResultSet;

//@ComponentScan("com.ltt.config")
@SpringBootApplication(exclude={DataSourceAutoConfiguration.class, HibernateJpaAutoConfiguration.class})
public class DemoApplication {

//    @Autowired
//    @Qualifier("primaryDataSource")
    public static DataSource primaryDataSource;
    public static DataSource secondDataSource;

    public static void main(String[] args) {
        ApplicationContext applicationContext = SpringApplication.run(DemoApplication.class, args);
        primaryDataSource = applicationContext.getBean("primaryDataSource",DataSource.class);
        secondDataSource = applicationContext.getBean("secondaryDataSource",DataSource.class);
//        try{
//            Connection connection = dataSource.getConnection();
//            ResultSet rs = connection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,
//                    ResultSet.CONCUR_UPDATABLE)
//                    .executeQuery("SELECT * from uri_label_id2 where dataset_local_id=1;");
//            while(rs.next()){
//                System.out.println(rs.getString("uri"));
//            }
//            if (rs.first()) {
//
//                System.out.println("Connection OK!");
//            } else {
//                System.out.println("Something is wrong");
//            }
//
//        }catch(Exception e){
//            e.printStackTrace();
//        }

    }

}
