package com.archgeek.data.meta.holo;

import com.aliyun.odps.jdbc.utils.JdbcColumn;
import com.archgeek.data.meta.exception.MetaRuntimeException;
import com.archgeek.data.meta.jdbc.DataSourceUtils;
import com.archgeek.data.meta.jdbc.JdbcConfig;
import com.archgeek.data.meta.jdbc.bean.JdbcColumnBean;
import com.archgeek.data.meta.jdbc.bean.JdbcIndexBean;
import com.archgeek.data.meta.jdbc.bean.JdbcIndexBean.IndexType;
import com.archgeek.data.meta.jdbc.bean.JdbcSchemaBean;
import com.archgeek.data.meta.jdbc.bean.JdbcTableBean;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import static com.archgeek.data.meta.jdbc.JdbcConfig.JDBC_DRIVER_NAME;
import static com.archgeek.data.meta.jdbc.JdbcConfig.JDBC_URL;
import static com.archgeek.data.meta.jdbc.bean.JdbcIndexBean.IndexType.PRIMARY_KEY;

/**
 * @author pizhihui
 * @date 2024-10-28 21:04
 */
public class HologresOperations {

    private static final Logger LOG = LoggerFactory.getLogger(HologresOperations.class);

    private DataSource dataSource;

    public HologresOperations(DataSource dataSource) {
        this.dataSource = dataSource;
    }


    public void loadCatalog(String catalog) {
        try (Connection connection = this.dataSource.getConnection();) {
            DatabaseMetaData metaData = connection.getMetaData();
            String catalog1 = connection.getCatalog();
            System.out.println();
        } catch (Exception e) {

        }
    }

    /**
     * @return
     */
    public List<JdbcSchemaBean> listSchemas(String catalog) {
        List<JdbcSchemaBean> schemaBeanList = new ArrayList<>();

        try (Connection connection = this.dataSource.getConnection()) {
            if (null == catalog || catalog.isEmpty()) {
                catalog = connection.getCatalog();
            }
            DatabaseMetaData metaData = connection.getMetaData();
//            ResultSet resultSet = metaData.getCatalogs();
//            ResultSet resultSet = metaData.getSchemas();
            ResultSet resultSet = metaData.getSchemas(catalog, null);
            while (resultSet.next()) {
                String schemaName = resultSet.getString(1);
                System.out.println(schemaName);
                if (!isSystemDatabase(schemaName)) {
                    String name = resultSet.getString(1);
                    String comment = getSchemaComment(schemaName, connection);
                    schemaBeanList.add(new JdbcSchemaBean(name, comment));
                }
            }
            return schemaBeanList;


        } catch (SQLException se) {
            throw new MetaRuntimeException(se, se.getMessage());
        }
    }

    /**
     * @param schema
     * @return
     */
    public List<JdbcTableBean> listTables(String schema) {
        final List<JdbcTableBean> tableBeanList = Lists.newArrayList();

        try (Connection connection = this.dataSource.getConnection()) {
            final DatabaseMetaData metaData = connection.getMetaData();
            String catalogName = connection.getCatalog();
            // SYSTEM TABLE
            // TABLE
            try (ResultSet tables = metaData.getTables(catalogName, schema, null, new String[]{"TABLE"})) {
                while (tables.next()) {
                    if (Objects.equals(tables.getString("TABLE_SCHEM"), schema)) {
                        String name = tables.getString("TABLE_NAME");
                        String comment = tables.getString("REMARKS");
                        tableBeanList.add(new JdbcTableBean(name, comment));
                    }
                }
            }
            LOG.info("Finished listing tables size {} for database name {} ", tableBeanList.size(), schema);
            return tableBeanList;
        } catch (SQLException se) {
            throw new MetaRuntimeException(se, se.getMessage());
        }
    }

    protected List<JdbcIndexBean> listIndexes(String databaseName, String tableName)
            throws SQLException {

        Connection connection = this.dataSource.getConnection();

        String catalog = connection.getCatalog();


        DatabaseMetaData metaData = connection.getMetaData();
        List<JdbcIndexBean> indexes = new ArrayList<>();

        // Get primary key information
        ResultSet primaryKeys = metaData.getPrimaryKeys(catalog, databaseName, tableName);
        List<JdbcIndexBean> jdbcIndexBeans = new ArrayList<>();
        while (primaryKeys.next()) {
            jdbcIndexBeans.add(
                    new JdbcIndexBean(
                            PRIMARY_KEY,
                            primaryKeys.getString("COLUMN_NAME"),
                            primaryKeys.getString("PK_NAME"),
                            primaryKeys.getInt("KEY_SEQ")));
        }

        Set<String> primaryIndexNames =
                jdbcIndexBeans.stream().map(JdbcIndexBean::getIdxName).collect(Collectors.toSet());

        // Get unique key information
        ResultSet indexInfo = metaData.getIndexInfo(catalog, databaseName, tableName, false, false);
        while (indexInfo.next()) {
            String indexName = indexInfo.getString("INDEX_NAME");
            // The primary key is also the unique key, so we need to filter the primary key here.
            if (!indexInfo.getBoolean("NON_UNIQUE") && !primaryIndexNames.contains(indexName)) {
                jdbcIndexBeans.add(
                        new JdbcIndexBean(
                                IndexType.UNIQUE_KEY,
                                indexInfo.getString("COLUMN_NAME"),
                                indexName,
                                indexInfo.getInt("ORDINAL_POSITION")));
            }
        }

        // Assemble into Index
        Map<IndexType, List<JdbcIndexBean>> indexBeanGroupByIndexType =
                jdbcIndexBeans.stream().collect(Collectors.groupingBy(JdbcIndexBean::getIndexType));

        for (Map.Entry<IndexType, List<JdbcIndexBean>> entry :
                indexBeanGroupByIndexType.entrySet()) {
            // Group by index Name
            Map<String, List<JdbcIndexBean>> indexBeanGroupByName =
                    entry.getValue().stream().collect(Collectors.groupingBy(JdbcIndexBean::getIdxName));
            for (Map.Entry<String, List<JdbcIndexBean>> indexEntry : indexBeanGroupByName.entrySet()) {
                List<String> colNames =
                        indexEntry.getValue().stream()
                                .sorted(Comparator.comparingInt(JdbcIndexBean::getOrder))
                                .map(JdbcIndexBean::getColName)
                                .collect(Collectors.toList());
                String[][] colStrArrays = colNames.stream().map(colName -> new String[]{colName}).toArray(String[][]::new);
                if (entry.getKey() == PRIMARY_KEY) {
                    indexes.add(new JdbcIndexBean(PRIMARY_KEY, indexEntry.getKey(), colStrArrays));
                } else {
                    indexes.add(new JdbcIndexBean(IndexType.UNIQUE_KEY, indexEntry.getKey(), colStrArrays));
                }
            }
        }
        return indexes;
    }


    /**
     *
     * @param databaseName
     * @param tableName
     * @return
     * @throws SQLException
     */
    public List<JdbcColumnBean> listColumns(String databaseName, String tableName) throws SQLException {
        Connection connection = this.dataSource.getConnection();
        final DatabaseMetaData metaData = connection.getMetaData();
        String catalog = connection.getCatalog();
        ResultSet columns = metaData.getColumns(catalog, databaseName, tableName, null);

        List<JdbcColumnBean> resultList = new ArrayList<>();

        while (columns.next()) {
            if (Objects.equals(columns.getString("TABLE_NAME"), tableName)
                    && Objects.equals(columns.getString("TABLE_SCHEM"), databaseName)) {
//                builder = getBasicJdbcColumnInfo(columns);

                String columnName = columns.getString("COLUMN_NAME");
                String columnSize = columns.getString("COLUMN_SIZE");
                String typeName = columns.getString("TYPE_NAME");

                String decimalDigits = columns.getString("DECIMAL_DIGITS");
                String columnDef = columns.getString("COLUMN_DEF");
                boolean nullable = columns.getBoolean("NULLABLE");
                String comment = columns.getString("REMARKS");
                String isGeneratedcolumn = columns.getString("IS_GENERATEDCOLUMN");

                JdbcColumnBean jdbcColumnBean = new JdbcColumnBean(columnName, comment, typeName, nullable, columnDef);
                resultList.add(jdbcColumnBean);

                System.out.println("xxxxx: " + columnName + "..." + comment + "...." + typeName + "....." + nullable + "...." + columnSize + "...." + decimalDigits + "..." + columnDef + "..." + isGeneratedcolumn);

            }
        }
        return resultList;
    }


    public static final String PG_QUOTE = "\"";
    public static final String NEW_LINE = "\n";
    public static final String SPACE = " ";
    public static final String TABLE_COMMENT = "COMMENT ON TABLE ";
    public static final String IS = " IS '";
    public static final String COLUMN_COMMENT = "COMMENT ON COLUMN ";

    /**
     * @return
     */
    public String generateCreateTableSql(String tableName,
                                         List<JdbcColumnBean> columns,
                                         String comment,
                                         List<JdbcIndexBean> indexes) {

        StringBuilder sqlBuilder = new StringBuilder();
        sqlBuilder
                .append("CREATE TABLE ")
                .append(PG_QUOTE)
                .append(tableName)
                .append(PG_QUOTE)
                .append(" (")
                .append(NEW_LINE);

        // Add columns
        for (int i = 0; i < columns.size(); i++) {
            JdbcColumnBean column = columns.get(i);
            sqlBuilder.append("    ").append(PG_QUOTE).append(column.getColName()).append(PG_QUOTE);

            appendColumnDefinition(column, sqlBuilder);
            // Add a comma for the next column, unless it's the last one
            if (i < columns.size() - 1) {
                sqlBuilder.append(",").append(NEW_LINE);
            }
        }
        appendIndexesSql(indexes, sqlBuilder);
        sqlBuilder.append(NEW_LINE).append(")");
        sqlBuilder.append(";");


        // Add table comment if specified
        if (null != comment && !comment.isEmpty()) {
            sqlBuilder
                    .append(NEW_LINE)
                    .append(TABLE_COMMENT)
                    .append(PG_QUOTE)
                    .append(tableName)
                    .append(PG_QUOTE)
                    .append(IS)
                    .append(comment)
                    .append("';");
        }

        columns.stream().filter(jdbcColumn -> null != jdbcColumn.getColComment() && !jdbcColumn.getColComment().isEmpty())
                                .forEach(
                        jdbcColumn ->
                                sqlBuilder
                                        .append(NEW_LINE)
                                        .append(COLUMN_COMMENT)
                                        .append(PG_QUOTE)
                                        .append(tableName)
                                        .append(PG_QUOTE)
                                        .append(".")
                                        .append(PG_QUOTE)
                                        .append(jdbcColumn.getColName())
                                        .append(PG_QUOTE)
                                        .append(IS)
                                        .append(jdbcColumn.getColComment())
                                        .append("';"));

        // Return the generated SQL statement
        String result = sqlBuilder.toString();

        LOG.info("Generated create table:{} sql: {}", tableName, result);
        return result;
    }

    private void appendColumnDefinition(JdbcColumnBean column, StringBuilder sqlBuilder) {
        // Add data type
        sqlBuilder.append(SPACE).append(column.getColType()).append(SPACE);

        // Add NOT NULL if the column is marked as such
        if (column.isNullable()) {
            sqlBuilder.append("NULL ");
        } else {
            sqlBuilder.append("NOT NULL ");
        }
        // Add DEFAULT value if specified
            sqlBuilder
                    .append("DEFAULT ")
                    .append(column.getColDefaultValue())
                    .append(SPACE);
    }

    static void appendIndexesSql(List<JdbcIndexBean> indexes, StringBuilder sqlBuilder) {
        for (JdbcIndexBean index : indexes) {
            String fieldStr = getIndexFieldStr(index.getFieldNames());
            sqlBuilder.append(",").append(NEW_LINE);
            switch (index.getIndexType()) {
                case PRIMARY_KEY:
                    if (StringUtils.isNotEmpty(index.getIdxName())) {
                        sqlBuilder.append("CONSTRAINT ").append(PG_QUOTE).append(index.getIdxName()).append(PG_QUOTE);
                    }
                    sqlBuilder.append(" PRIMARY KEY (").append(fieldStr).append(")");
                    break;
                case UNIQUE_KEY:
                    if (StringUtils.isNotEmpty(index.getIdxName())) {
                        sqlBuilder.append("CONSTRAINT ").append(PG_QUOTE).append(index.getIdxName()).append(PG_QUOTE);
                    }
                    sqlBuilder.append(" UNIQUE (").append(fieldStr).append(")");
                    break;
                default:
                    throw new IllegalArgumentException("PostgreSQL doesn't support index : " + index.getIndexType());
            }
        }
    }
    protected static String getIndexFieldStr(String[][] fieldNames) {
        return Arrays.stream(fieldNames)
                .map(
                        colNames -> {
                            if (colNames.length > 1) {
                                throw new IllegalArgumentException(
                                        "Index does not support complex fields in PostgreSQL");
                            }
                            return PG_QUOTE + colNames[0] + PG_QUOTE;
                        })
                .collect(Collectors.joining(", "));
    }

    private String getShowSchemaCommentSql(String schema) {
        return String.format(
                "SELECT obj_description(n.oid, 'pg_namespace') AS comment\n"
                        + "FROM pg_catalog.pg_namespace n\n"
                        + "WHERE n.nspname = '%s';\n",
                schema);
    }

    private String getSchemaComment(String schema, Connection connection) throws SQLException {
        try (PreparedStatement preparedStatement =
                     connection.prepareStatement(getShowSchemaCommentSql(schema))) {
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getString("comment");
                }
            }
        }
        return null;
    }


    protected boolean isSystemDatabase(String dbName) {
        return createSysDatabaseNameSet().contains(dbName.toLowerCase(Locale.ROOT));
    }

    protected Set<String> createSysDatabaseNameSet() {
        return ImmutableSet.of("pg_toast", "pg_catalog", "information_schema", "hologres_sample", "hologres_statistic", "hologres_streaming_mv", "hologres");
    }


    public static void main(String[] args) throws SQLException {
        String HOLO_ENDPOINT = "hgpostcn-cn-71j3z8fbh03j-cn-beijing.hologres.aliyuncs.com";
        int HOLO_PORT = 80;
        String HOLO_DEFAULT_DB = "smartpipi";
//        String user = System.getenv("HOLO_USER"); // "BASIC$pipi";//System.getenv("HOLO_USER");
//        String password = System.getenv("HOLO_PWD"); // "kk5VBj15POdVNGL7";//System.getenv("HOLO_PWD");

        String user = "BASIC$pipi"; // "BASIC$pipi";//System.getenv("HOLO_USER");
        String password = "kk5VBj15POdVNGL7"; // "kk5VBj15POdVNGL7";//System.getenv("HOLO_PWD");


        // jdbc:postgresql://hgpostcn-cn-71j3z8fbh03j-cn-beijing.hologres.aliyuncs.com:80/smartpipi
        String url = String.format("jdbc:postgresql://%s:%d/%s?user=%s&password=%s", HOLO_ENDPOINT, HOLO_PORT, HOLO_DEFAULT_DB, user, password);

        Map<String, String> params = new HashMap<>();
        params.put(JDBC_DRIVER_NAME, "org.postgresql.Driver");
        params.put(JDBC_URL, url);
        JdbcConfig jdbcConfig = new JdbcConfig(params);
        DataSource holoDs = DataSourceUtils.createDataSource(jdbcConfig);
        HologresOperations hologresOperations = new HologresOperations(holoDs);

        // catalog info
        hologresOperations.loadCatalog("smartpipi");

        List<JdbcSchemaBean> schemas = hologresOperations.listSchemas("smartpipi");
        System.out.println("schema======================" + Arrays.toString(schemas.toArray()));

        System.out.println("**************************");

        for (JdbcSchemaBean schema : schemas) {
            List<JdbcTableBean> tableBeanList = hologresOperations.listTables(schema.getSchemaName());
            System.out.println("table====================" + Arrays.toString(tableBeanList.toArray()));


            for (JdbcTableBean jdbcTableBean : tableBeanList) {
                List<JdbcIndexBean> indexes = hologresOperations.listIndexes(schema.getSchemaName(), jdbcTableBean.getTableName());
                System.out.println("index::::::::" + Arrays.toString(indexes.toArray()));

                List<JdbcColumnBean> columnBeanList = hologresOperations.listColumns(schema.getSchemaName(), jdbcTableBean.getTableName());


                String createTableSql = hologresOperations.generateCreateTableSql(jdbcTableBean.getTableName(), columnBeanList, jdbcTableBean.getTableComment(), indexes);
                System.out.println(createTableSql);

            }

        }

    }

}
