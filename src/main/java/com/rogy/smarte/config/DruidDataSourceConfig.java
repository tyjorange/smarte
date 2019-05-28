package com.rogy.smarte.config;

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.spring.boot.autoconfigure.DruidDataSourceBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.Properties;

@Configuration
public class DruidDataSourceConfig {
    @Value("${spring.datasource.initialSize}")
    private Integer initialSize;
    @Value("${spring.datasource.maxActive}")
    private Integer maxActive;
    @Value("${spring.datasource.maxWait}")
    private Integer maxWait;
    @Value("${spring.datasource.timeBetweenEvictionRunsMillis}")
    private Integer timeBetweenEvictionRunsMillis;
    @Value("${spring.datasource.minEvictableIdleTimeMillis}")
    private Integer minEvictableIdleTimeMillis;
    @Value("${spring.datasource.minIdle}")
    private Integer minIdle;
    @Value("${spring.datasource.validationQuery}")
    private String validationQuery;
    @Value("${spring.datasource.testWhileIdle}")
    private Boolean testWhileIdle;
    @Value("${spring.datasource.testOnBorrow}")
    private Boolean testOnBorrow;
    @Value("${spring.datasource.testOnReturn}")
    private Boolean testOnReturn;
    @Value("${spring.datasource.poolPreparedStatements}")
    private Boolean poolPreparedStatements;
    @Value("${spring.datasource.filters}")
    private String filters;
    @Value("${spring.datasource.slowSqlMillis}")
    private String slowSqlMillis;
    @Value("${spring.datasource.logSlowSql}")
    private String logSlowSql;
    @Value("${spring.datasource.mergeSql}")
    private String mergeSql;

    @Bean(name = "DataSource_1", initMethod = "init", destroyMethod = "close")
    @Primary
    @ConfigurationProperties(prefix = "spring.datasource.db1")
    public DruidDataSource getDataSourceDB1() throws SQLException {
        return this.getDataSource();
    }

    @Bean(name = "DataSource_2", initMethod = "init", destroyMethod = "close")
    @ConfigurationProperties(prefix = "spring.datasource.db2")
    public DruidDataSource getDataSourceDB2() throws SQLException {
        return this.getDataSource();
    }

    /**
     * 设置连接池参数并返回
     *
     * @return 连接池对象
     * @throws SQLException e
     */
    private DruidDataSource getDataSource() throws SQLException {
        DruidDataSource druidDataSource = DruidDataSourceBuilder.create().build();
        druidDataSource.setInitialSize(initialSize);
        druidDataSource.setMaxActive(maxActive);
        druidDataSource.setMaxWait(maxWait);
        druidDataSource.setTimeBetweenEvictionRunsMillis(timeBetweenEvictionRunsMillis);
        druidDataSource.setMinEvictableIdleTimeMillis(minEvictableIdleTimeMillis);
        druidDataSource.setMinIdle(minIdle);
        druidDataSource.setValidationQuery(validationQuery);
        druidDataSource.setTestWhileIdle(testWhileIdle);
        druidDataSource.setTestOnBorrow(testOnBorrow);
        druidDataSource.setTestOnReturn(testOnReturn);
        druidDataSource.setPoolPreparedStatements(poolPreparedStatements);
        druidDataSource.setFilters(filters);
        Properties properties = new Properties();
        properties.put("druid.stat.slowSqlMillis", slowSqlMillis);
        properties.put("druid.stat.logSlowSql", logSlowSql);
        properties.put("druid.stat.mergeSql", mergeSql);
        druidDataSource.setConnectProperties(properties);
        return druidDataSource;
    }
}
