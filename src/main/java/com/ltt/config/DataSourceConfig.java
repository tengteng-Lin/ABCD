package com.ltt.config;


import com.zaxxer.hikari.HikariDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;


import javax.sql.DataSource;


@Configuration
@PropertySource("application.properties")
public class DataSourceConfig {
    private Logger logger = LoggerFactory.getLogger(DataSourceConfig.class);


    @Bean
    @Primary
    @ConfigurationProperties("spring.datasource.primary")
    public DataSourceProperties firstDataSourceProperties() {
        return new DataSourceProperties();
    }


    @Bean("primaryDataSource")
    @Qualifier("primaryDataSource")
    @Primary
//    @ConfigurationProperties(prefix = "spring.datasource.primary")
    public HikariDataSource primaryDataSource(){
        logger.info("remote DataSource init！");
        HikariDataSource ss = (HikariDataSource) firstDataSourceProperties().initializeDataSourceBuilder().build();
        ss.setMaximumPoolSize(200);
        System.out.println(ss.getMaximumPoolSize());
        ss.setIdleTimeout(60000);
        ss.setConnectionTimeout(30000);
        ss.setMaxLifetime(200000);
        ss.setMinimumIdle(10);
//        ss.setLeakDetectionThreshold(80000);
//        System.out.println(ss.getMaximumPoolSize());
        return ss;
    }

    @Bean
    @Primary
    @ConfigurationProperties("spring.datasource.secondary")
    public DataSourceProperties secondDataSourceProperties() {
        return new DataSourceProperties();
    }

    @Bean("secondaryDataSource")
    @Qualifier("secondaryDataSource")
//    @ConfigurationProperties(prefix = "spring.datasource.secondary")
    public HikariDataSource secondaryDataSource(){
        logger.info("local DataSource init!");
        HikariDataSource aa = (HikariDataSource) secondDataSourceProperties().initializeDataSourceBuilder().build();
        aa.setMaximumPoolSize(200);
        aa.setIdleTimeout(60000);
        aa.setConnectionTimeout(30000);
        aa.setMaxLifetime(200000);
        aa.setMinimumIdle(10);
//        aa.setLeakDetectionThreshold(5000);
        return aa;
//        return DataSourceBuilder.create().build();
    }


}
