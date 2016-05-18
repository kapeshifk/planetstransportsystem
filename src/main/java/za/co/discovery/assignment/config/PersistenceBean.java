package za.co.discovery.assignment.config;

import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.annotation.PersistenceExceptionTranslationPostProcessor;
import org.springframework.orm.hibernate5.HibernateTransactionManager;
import org.springframework.orm.hibernate5.LocalSessionFactoryBean;

import javax.sql.DataSource;
import java.util.Properties;

//@Configuration
//@EnableTransactionManagement
public class PersistenceBean {

    public LocalSessionFactoryBean sessionFactory(DataSource dataSource, @Qualifier("hibernateProperties") Properties properties) {
        LocalSessionFactoryBean sessionFactory = new LocalSessionFactoryBean();
        sessionFactory.setDataSource(dataSource);
        sessionFactory.setPackagesToScan("za.co.discovery.assignment.entity");
        sessionFactory.setHibernateProperties(properties);

        return sessionFactory;
    }

    public HibernateTransactionManager transactionManager(final SessionFactory sessionFactory) {
        HibernateTransactionManager txManager = new HibernateTransactionManager();
        txManager.setSessionFactory(sessionFactory);

        return txManager;
    }

    public PersistenceExceptionTranslationPostProcessor exceptionTranslation() {
        return new PersistenceExceptionTranslationPostProcessor();
    }

    public Properties properties() {
        Properties properties = new Properties();
        properties.setProperty("hibernate.dialect", "org.hibernate.dialect.DerbyTenSevenDialect");
        properties.setProperty("hibernate.show_sql", "false");
        properties.setProperty("hibernate.hbm2ddl.auto", "create");

        return properties;
    }
}

