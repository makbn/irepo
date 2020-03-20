package io.github.makbn.irepo.config;

import com.zaxxer.hikari.HikariDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.env.Environment;
import org.springframework.orm.hibernate5.HibernateTransactionManager;
import org.springframework.orm.hibernate5.LocalSessionFactoryBean;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;


import javax.sql.DataSource;
import java.util.Properties;

@Configuration
@EnableTransactionManagement
public class HibernateConfig {

    private final Environment environment;

    @Autowired
    public HibernateConfig(Environment environment) {
        this.environment = environment;
    }

    @Primary
    @Bean
    public LocalSessionFactoryBean sessionFactory() {
        LocalSessionFactoryBean sessionFactory = new LocalSessionFactoryBean();
        sessionFactory.setDataSource(dataSource());
        sessionFactory.setPackagesToScan("io.github.makbn.irepo.model");
        sessionFactory.setHibernateProperties(hibernateProperties());
        return sessionFactory;
    }

    @Bean
    public DataSource dataSource() {
        HikariDataSource ds = new HikariDataSource();
        ds.setDriverClassName("com.mysql.cj.jdbc.Driver");
        ds.setJdbcUrl(
                "jdbc:mysql://"+environment.getProperty("irepo_db_host")+":3306/"+environment.getProperty("irepo_db_name")+"?useSSL=false"
                );
        ds.setUsername(environment.getProperty("irepo_db_username"));
        ds.setPassword(environment.getProperty("irepo_db_password"));
        ds.setIdleTimeout(15000);
        ds.setAllowPoolSuspension(false);
        ds.setLeakDetectionThreshold(90000);
        ds.setMinimumIdle(10);
        ds.setPoolName("irepo_db_pool");
        ds.setMaxLifetime(100000);
        ds.setMaximumPoolSize(100);

        return ds;
    }

    @Bean
    public PlatformTransactionManager hibernateTransactionManager() {
        HibernateTransactionManager transactionManager
                = new HibernateTransactionManager();
        transactionManager.setSessionFactory(sessionFactory().getObject());
        return transactionManager;
    }

    @Qualifier
    @Bean
    public LocalContainerEntityManagerFactoryBean entityManagerFactory() {

        HibernateJpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
        vendorAdapter.setGenerateDdl(true);

        LocalContainerEntityManagerFactoryBean factory = new LocalContainerEntityManagerFactoryBean();
        factory.setJpaVendorAdapter(vendorAdapter);
        factory.setPackagesToScan("io.github.makbn.irepo.model");
        factory.setDataSource(dataSource());
        return factory;
    }

    private final Properties hibernateProperties() {
        Properties hibernateProperties = new Properties();
        hibernateProperties.setProperty(
                "hibernate.hbm2ddl.auto", "update");
        hibernateProperties.setProperty(
                "hibernate.dialect", "org.hibernate.dialect.MySQLDialect");
        hibernateProperties.setProperty(
                "hibernate.show_sql", "false");
        hibernateProperties.setProperty(
                "hibernate.connection.autocommit", "false");
        hibernateProperties.setProperty(
                "hibernate.format_sql", "false");
        hibernateProperties.setProperty(
                "hibernate.temp.use_jdbc_metadata_default", "false");
        hibernateProperties.setProperty(
                "hibernate.globally_quoted_identifiers", "true");
        hibernateProperties.setProperty(
                "hibernate.hikari.maximumPoolSize", "100");
        hibernateProperties.setProperty(
                "hibernate.hikari.dataSource.idleTimeout", "15000");
        hibernateProperties.setProperty(
                "hibernate.hikari.dataSource.minimumIdle", "10");
        hibernateProperties.setProperty(
                "hibernate.hikari.dataSource.connectionTimeout", "3000");
        hibernateProperties.setProperty(
                "hibernate.hikari.dataSource.maxLifetime", "100000");
        hibernateProperties.setProperty(
                "hibernate.hikari.dataSource.leakDetectionThreshold", "90000");
        hibernateProperties.setProperty(
                "hibernate.hikari.dataSource.cachePrepStmts", "true");
        hibernateProperties.setProperty(
                "hibernate.hikari.dataSource.prepStmtCacheSize", "250");
        hibernateProperties.setProperty(
                "hibernate.hikari.dataSource.prepStmtCacheSqlLimit", "2048");
        return hibernateProperties;
    }
}
