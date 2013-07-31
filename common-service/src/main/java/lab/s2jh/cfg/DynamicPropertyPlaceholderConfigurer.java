package lab.s2jh.cfg;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Properties;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowCallbackHandler;

/**
 * 基于数据库加载动态配置参数
 */
public class DynamicPropertyPlaceholderConfigurer extends PropertyPlaceholderConfigurer {

    private final static Logger logger = LoggerFactory.getLogger(DynamicPropertyPlaceholderConfigurer.class);

    private DataSource dataSource;
    private String nameColumn;
    private String valueColumn;
    private String tableName;

    private static Properties propertiesContainer = new Properties();

    @Override
    protected void loadProperties(final Properties props) throws IOException {
        super.loadProperties(props);
        propertiesContainer.putAll(props);

        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
        String sql = String.format("select %s, %s from %s", nameColumn, valueColumn, tableName);
        logger.info("Reading configuration properties from database");

        try {
            jdbcTemplate.query(sql, new RowCallbackHandler() {
                public void processRow(ResultSet rs) throws SQLException {
                    String name = rs.getString(nameColumn);
                    String value = rs.getString(valueColumn);
                    if (null == name || null == value) {
                        throw new SQLException("Configuration database contains empty data. Name='" + name
                                + "' Value='" + value + "'");
                    }
                    props.setProperty(name, value);
                }
            });
        } catch (DataAccessException e) {
            logger.warn("Database configuration data read error: {}", e.getMessage());
        }

        if (props.size() == 0) {
            logger.warn("The configuration database could not be reached or does not contain any properties in '"
                    + tableName + "'");
        }

    }

    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public void setNameColumn(String nameColumn) {
        this.nameColumn = nameColumn;
    }

    public void setValueColumn(String valueColumn) {
        this.valueColumn = valueColumn;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }
}
