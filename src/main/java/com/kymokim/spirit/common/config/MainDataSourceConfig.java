package com.kymokim.spirit.common.config;

import jakarta.persistence.EntityManagerFactory;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.*;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.beans.factory.annotation.Qualifier;

import javax.sql.DataSource;

@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(
        basePackages = {
                "com.kymokim.spirit.archive",
                "com.kymokim.spirit.common",
                "com.kymokim.spirit.drink",
                "com.kymokim.spirit.log",
                "com.kymokim.spirit.menu",
                "com.kymokim.spirit.notification",
                "com.kymokim.spirit.report",
                "com.kymokim.spirit.review",
                "com.kymokim.spirit.store"
        },
        entityManagerFactoryRef = "mainEntityManagerFactory",
        transactionManagerRef = "mainTransactionManager"
)
public class MainDataSourceConfig {

    @Primary
    @Bean
    @ConfigurationProperties("spring.datasource.main")
    public DataSourceProperties mainDataSourceProps() {
        return new DataSourceProperties();
    }

    @Primary
    @Bean(name = "mainDataSource")
    public DataSource mainDataSource(@Qualifier("mainDataSourceProps") DataSourceProperties props) {
        return props.initializeDataSourceBuilder().build();
    }

    @Primary
    @Bean(name = "mainEntityManagerFactory")
    public LocalContainerEntityManagerFactoryBean mainEntityManagerFactory(
            EntityManagerFactoryBuilder builder,
            @Qualifier("mainDataSource") DataSource dataSource) {
        return builder
                .dataSource(dataSource)
                .packages("com.kymokim.spirit.archive",
                        "com.kymokim.spirit.common",
                        "com.kymokim.spirit.drink",
                        "com.kymokim.spirit.log",
                        "com.kymokim.spirit.menu",
                        "com.kymokim.spirit.notification",
                        "com.kymokim.spirit.report",
                        "com.kymokim.spirit.review",
                        "com.kymokim.spirit.store")
                .persistenceUnit("main")
                .build();
    }

    @Primary
    @Bean(name = "mainTransactionManager")
    public PlatformTransactionManager mainTransactionManager(
            @Qualifier("mainEntityManagerFactory") EntityManagerFactory emf) {
        return new JpaTransactionManager(emf);
    }
}
