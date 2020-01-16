package lk.ijse.dep.pos;



import lk.ijse.dep.crypto.DEPCrypt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.JpaVendorAdapter;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.Database;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;
@PropertySource("file:${user.dir}/src/main/resources/application.properties")
@EnableTransactionManagement
@Configuration
public class JPAConfig {
    @Autowired
    private Environment environment;
    @Bean
    public LocalContainerEntityManagerFactoryBean entityManagerFactory(DataSource ds, JpaVendorAdapter jpaVendorAdapter){
        LocalContainerEntityManagerFactoryBean lcemfb = new LocalContainerEntityManagerFactoryBean();
        lcemfb.setDataSource(ds);
        lcemfb.setJpaVendorAdapter(jpaVendorAdapter);
        lcemfb.setPackagesToScan("lk.ijse.dep.pos.entity");
        return  lcemfb;
    }
    @Bean
    public DataSource dataSource(){
        DriverManagerDataSource ds = new DriverManagerDataSource();
        ds.setDriverClassName(environment.getRequiredProperty("javax.persistence.jdbc.driver"));
        ds.setUrl(environment.getRequiredProperty("javax.persistence.jdbc.url"));
        ds.setUsername(DEPCrypt.decode(environment.getRequiredProperty("javax.persistence.jdbc.user"),"dep4"));
        ds.setPassword(DEPCrypt.decode(environment.getRequiredProperty("javax.persistence.jdbc.password"),"dep4"));
        return ds;
    }
    @Bean
    public JpaVendorAdapter jpaVendorAdapter(){
        HibernateJpaVendorAdapter jpaVendorAdapter = new HibernateJpaVendorAdapter();
        jpaVendorAdapter.setDatabase(Database.MYSQL);
        jpaVendorAdapter.setDatabasePlatform(environment.getRequiredProperty("hibernate.dialect"));
        jpaVendorAdapter.setShowSql(environment.getRequiredProperty("hibernate.show_sql",Boolean.class));
        jpaVendorAdapter.setGenerateDdl(environment.getRequiredProperty("hibernate.hbm2ddl.auto").equals("update"));
        return jpaVendorAdapter;
    }

    @Bean
    public PlatformTransactionManager transactionManager(EntityManagerFactory entityManagerFactory){
        return new JpaTransactionManager(entityManagerFactory);
    }
}
