package com.ltt;

import com.zaxxer.hikari.HikariDataSource;
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
    public static HikariDataSource primaryDataSource;
    public static HikariDataSource secondDataSource;

    public static void main(String[] args) {
        ApplicationContext applicationContext = SpringApplication.run(DemoApplication.class, args);
        primaryDataSource = (HikariDataSource) applicationContext.getBean("primaryDataSource",DataSource.class);
        secondDataSource = (HikariDataSource) applicationContext.getBean("secondaryDataSource",DataSource.class);

    }

}
