package hu.davidder.translations.core.config;

import java.util.Properties;

import javax.sql.DataSource;

import org.hibernate.cfg.MultiTenancySettings;
import org.hibernate.context.spi.CurrentTenantIdentifierResolver;
import org.hibernate.engine.jdbc.connections.spi.MultiTenantConnectionProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.PropertySource;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;

/**
 * The HibernateConfig class configures the Hibernate JPA settings for the application,
 * including multi-tenancy support and batch processing.
 */
@Configuration
@PropertySource("classpath:hibernate.properties")
public class HibernateConfig {

    public static final String PUBLIC = "public";

    @Value("${batch_size:20}")
    private String batchSize;

    @Value("${hbm2ddl.auto:create}")
    private String hbm2ddl;

    /**
     * Configures and returns the entity manager factory bean.
     * 
     * @param dataSource The data source.
     * @param multiTenantConnectionProviderImpl The multi-tenant connection provider.
     * @param currentTenantIdentifierResolverImpl The current tenant identifier resolver.
     * @return The configured LocalContainerEntityManagerFactoryBean.
     */
    @Lazy
    @Bean
    @Primary
    public LocalContainerEntityManagerFactoryBean entityManagerFactory(DataSource dataSource,
            MultiTenantConnectionProvider<String> multiTenantConnectionProviderImpl,
            CurrentTenantIdentifierResolver<String> currentTenantIdentifierResolverImpl) {
        LocalContainerEntityManagerFactoryBean em = new LocalContainerEntityManagerFactoryBean();
        Properties hibernateProp = new Properties();
        hibernateProp.put(MultiTenancySettings.MULTI_TENANT_CONNECTION_PROVIDER, multiTenantConnectionProviderImpl);
        hibernateProp.put(MultiTenancySettings.MULTI_TENANT_IDENTIFIER_RESOLVER, currentTenantIdentifierResolverImpl);
//        hibernateProp.put("hibernate.default_schema", "en-th");
        hibernateProp.put("hibernate.hbm2ddl.auto", hbm2ddl);
        hibernateProp.put("hibernate.enable_lazy_load_no_trans", "true");
        hibernateProp.put("hibernate.jdbc.batch_size", batchSize);
        hibernateProp.put("hibernate.event.merge.entity_copy_observer", "allow");
        em.setDataSource(dataSource);
        em.setPackagesToScan("hu.davidder.translations*");
        em.setJpaVendorAdapter(new HibernateJpaVendorAdapter());
        em.setJpaProperties(hibernateProp);
        return em;
    }
}
