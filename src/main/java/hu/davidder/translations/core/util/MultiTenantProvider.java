package hu.davidder.translations.core.util;

import java.sql.Connection;
import java.sql.SQLException;

import javax.sql.DataSource;

import org.hibernate.engine.jdbc.connections.spi.MultiTenantConnectionProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * The MultiTenantProvider class implements the MultiTenantConnectionProvider interface
 * to provide multi-tenant support for database connections.
 */
@Component
public class MultiTenantProvider implements MultiTenantConnectionProvider<String> {

    private static final long serialVersionUID = 4397851783460807279L;

    private static final Logger logger = LoggerFactory.getLogger(MultiTenantProvider.class);
    
    private final DataSource datasource;

    /**
     * Constructor for the MultiTenantProvider.
     * 
     * @param dataSource The data source for obtaining connections.
     */
    public MultiTenantProvider(DataSource dataSource) {
        this.datasource = dataSource;
    }

    /**
     * Gets any available connection from the data source.
     * 
     * @return A connection from the data source.
     * @throws SQLException If a database access error occurs.
     */
    @Override
    public Connection getAnyConnection() throws SQLException {
        return datasource.getConnection();
    }

    /**
     * Releases the given connection back to the data source.
     * 
     * @param connection The connection to release.
     * @throws SQLException If a database access error occurs.
     */
    @Override
    public void releaseAnyConnection(Connection connection) throws SQLException {
        connection.close();
    }

    /**
     * Gets a connection for the specified tenant.
     * 
     * @param tenantIdentifier The identifier of the tenant.
     * @return A connection for the tenant.
     * @throws SQLException If a database access error occurs.
     */
    @Override
    public Connection getConnection(String tenantIdentifier) throws SQLException {
        logger.info("Get connection for tenant {}", tenantIdentifier);
        final Connection connection = getAnyConnection();
        connection.setSchema(tenantIdentifier);
        return connection;
    }

    /**
     * Releases the connection for the specified tenant back to the data source.
     * 
     * @param tenantIdentifier The identifier of the tenant.
     * @param connection The connection to release.
     * @throws SQLException If a database access error occurs.
     */
    @Override
    public void releaseConnection(String tenantIdentifier, Connection connection) throws SQLException {
        logger.info("Release connection for tenant {}", tenantIdentifier);
        connection.setSchema("public");
        releaseAnyConnection(connection);
    }

    /**
     * Indicates if aggressive release of connections is supported.
     * 
     * @return false as aggressive release is not supported.
     */
    @Override
    public boolean supportsAggressiveRelease() {
        return false;
    }

    /**
     * Checks if the specified class can be unwrapped.
     * 
     * @param aClass The class to check.
     * @return false as unwrapping is not supported.
     */
    @Override
    @SuppressWarnings("rawtypes")
    public boolean isUnwrappableAs(Class aClass) {
        return false;
    }

    /**
     * Unwraps the specified class.
     * 
     * @param aClass The class to unwrap.
     * @return null as unwrapping is not supported.
     */
    @Override
    public <T> T unwrap(Class<T> aClass) {
        return null;
    }
}
