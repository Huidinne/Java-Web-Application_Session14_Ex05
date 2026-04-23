package org.example.ex_05.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.orm.jpa.hibernate.HibernateTransactionManager;
import org.springframework.orm.jpa.hibernate.LocalSessionFactoryBean;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;
import java.io.IOException;
import java.util.Properties;

@Configuration
@EnableTransactionManagement
public class HibernateConfig {
    @Autowired
    private Environment env;

    @Bean
    public LocalSessionFactoryBean factoryBean(DataSource dataSource) throws IOException {
        LocalSessionFactoryBean bean = new LocalSessionFactoryBean();
        bean.setDataSource(dataSource);
        bean.setPackagesToScan("org.example.ex_05.model");

        Properties prop = new Properties();
        prop.setProperty("hibernate.show_sql", env.getProperty("spring.jpa.show-sql", "false"));
        prop.setProperty("hibernate.hbm2ddl.auto", env.getProperty("spring.jpa.hibernate.ddl-auto", "update"));
        prop.setProperty("hibernate.dialect", env.getProperty("spring.jpa.properties.hibernate.dialect", "org.hibernate.dialect.MySQLDialect"));
        prop.setProperty("hibernate.format_sql", env.getProperty("spring.jpa.properties.hibernate.format_sql", "true"));
        bean.setHibernateProperties(prop);
        return bean;
    }

    @Bean
    public HibernateTransactionManager transactionManager(LocalSessionFactoryBean localSessionFactoryBean){
        HibernateTransactionManager manager = new HibernateTransactionManager();
        manager.setSessionFactory(localSessionFactoryBean.getObject());
        return manager;
    }
}