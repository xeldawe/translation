package hu.davidder.translations.core.config;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.sql.DataSource;

import org.hibernate.cfg.Environment;
import org.hibernate.context.spi.CurrentTenantIdentifierResolver;
import org.hibernate.engine.jdbc.connections.spi.MultiTenantConnectionProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.PropertySource;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;

@Configuration
@PropertySource("classpath:hibernate.properties")
public class HibernateConfig {

	private static final Logger logger = LoggerFactory.getLogger(HibernateConfig.class);
	
	@Value("${markets:#{new String[0]}}")
	private String[] markets;
	@Value("${batch_size:20}")
	private String batchSize;
	@Value("${hbm2ddl.auto:update}")
	private String hbm2ddl;
	public final static String PUBLIC = "public";
	
	private static Map<String, LocalContainerEntityManagerFactoryBean> factories = new HashMap<>(); //beacuse we want to use hbm2ddl.auto with different schemas

	@Bean
	@Primary
	LocalContainerEntityManagerFactoryBean entityManagerFactory(DataSource dataSource,
			MultiTenantConnectionProvider<String> multiTenantConnectionProviderImpl,
			CurrentTenantIdentifierResolver<String> currentTenantIdentifierResolverImpl) {
		List<String> schemas = new LinkedList<>();
		schemas.add(PUBLIC);
		schemas.addAll(Arrays.asList(markets));
		LocalContainerEntityManagerFactoryBean primary = new LocalContainerEntityManagerFactoryBean();
		LocalContainerEntityManagerFactoryBean em = new LocalContainerEntityManagerFactoryBean();
		for (String schema : schemas) {
			Properties hibernateProp = new Properties();
			hibernateProp.put(Environment.MULTI_TENANT_CONNECTION_PROVIDER, multiTenantConnectionProviderImpl);
			hibernateProp.put(Environment.MULTI_TENANT_IDENTIFIER_RESOLVER, currentTenantIdentifierResolverImpl);
			hibernateProp.put("hibernate.default_schema", schema);
			hibernateProp.put("hibernate.hbm2ddl.auto", hbm2ddl);
			hibernateProp.put("hibernate.enable_lazy_load_no_trans", "true");
			hibernateProp.put("hibernate.jdbc.batch_size", batchSize);
			hibernateProp.put("hibernate.event.merge.entity_copy_observer", "allow");
			em.setDataSource(dataSource);
			em.setPackagesToScan("hu.davidder.translations*");
			em.setJpaVendorAdapter(new HibernateJpaVendorAdapter());
			em.setJpaProperties(hibernateProp);
			if (schema.equals(PUBLIC)) {
				primary = em;
			}else {
				logger.info("Init market -> {}",schema);
			}
			factories.put(schema, em);
		}
		return primary;
	}
	
	public static Connection getConnection(String schema) {
		   if(schema == null || schema.isBlank()){
			   schema = PUBLIC;
		   }
		   LocalContainerEntityManagerFactoryBean em = factories.get(schema);
		   if(em == null) {
			   em = factories.get(PUBLIC);
		   }
		   try {
			return em.getDataSource().getConnection();
		} catch (SQLException e) {
			//TODO LOGGER
			return null;
		}
		   
	}

}
