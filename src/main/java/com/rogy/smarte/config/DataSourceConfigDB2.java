package com.rogy.smarte.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.orm.jpa.JpaProperties;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;

@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(
        entityManagerFactoryRef = "EntityManagerFactory_2",
        transactionManagerRef = "TransactionManager_2",
        basePackages = {"com.rogy.smarte.repository.db2"}) //设置Repository所在位置
public class DataSourceConfigDB2 {
    @Autowired
    private JpaProperties jpaProperties;


    @Autowired
    @Qualifier("DataSource_2")
    private DataSource DataSource_2;

    /**
     * 我们通过LocalContainerEntityManagerFactoryBean来获取EntityManagerFactory实例
     *
     * @return
     */
    @Bean(name = "EntityManagerFactoryBean_2")
    public LocalContainerEntityManagerFactoryBean entityManagerFactoryBean(EntityManagerFactoryBuilder builder) {
        return builder
                .dataSource(DataSource_2)
                .properties(jpaProperties.getProperties())
                .packages("com.rogy.smarte.entity.db2") //设置实体类所在位置
                .persistenceUnit("persistenceUnit_2")
                .build();
        //.getObject();//不要在这里直接获取EntityManagerFactory
    }

    /**
     * EntityManagerFactory类似于Hibernate的SessionFactory,mybatis的SqlSessionFactory
     * 总之,在执行操作之前,我们总要获取一个EntityManager,这就类似于Hibernate的Session,
     * mybatis的sqlSession.
     *
     * @param builder
     * @return
     */
    @Bean(name = "EntityManagerFactory_2")
    public EntityManagerFactory entityManagerFactory(EntityManagerFactoryBuilder builder) {
        return this.entityManagerFactoryBean(builder).getObject();
    }

    /**
     * 配置事物管理器
     *
     * @return
     */
    @Bean(name = "TransactionManager_2")
    public PlatformTransactionManager writeTransactionManager(EntityManagerFactoryBuilder builder) {
        return new JpaTransactionManager(this.entityManagerFactory(builder));
    }
}
