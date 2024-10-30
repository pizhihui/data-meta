package com.archgeek.data.meta.holo;

import com.google.common.collect.ImmutableList;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Objects;

/**
 * @author pizhihui
 * @date 2024-10-28 11:33
 */
public class HologresTool {

    private static String user = System.getenv("HOLO_USER"); // "BASIC$pipi";//System.getenv("HOLO_USER");
    private static String password = System.getenv("HOLO_PWD"); // "kk5VBj15POdVNGL7";//System.getenv("HOLO_PWD");

    private static final String HOLO_ENDPOINT = "hgpostcn-cn-71j3z8fbh03j-cn-beijing.hologres.aliyuncs.com";
    private static final int HOLO_PORT = 80;
    private static final String HOLO_DEFAULT_DB = "smartpipi";

    private static String driverName = "org.postgresql.Driver";

    //

    public static void main(String[] args) {

//        String user= System.getenv("ALIBABA_CLOUD_USER");
//        String password = System.getenv("ALIBABA_CLOUD_PASSWORD");
        String url = String.format("jdbc:postgresql://%s:%d/%s?user=%s&password=%s", HOLO_ENDPOINT, HOLO_PORT, HOLO_DEFAULT_DB, user, password);
        System.out.println("url:::" + url);


        // jdbc:postgresql://hgprecn-cn-ot93ru1ci001-cn-beijing-vpc-st.hologres.aliyuncs.com:80/smarttech?user=BASIC$liyiwen&password=SWvhqoVKHPJTC463OkNrxMfQslADj7En
        // jdbc:postgresql://hgpostcn-cn-71j3z8fbh03j-cn-beijing.hologres.aliyuncs.com:80/smartpipi?user=BASIC$pipi&password=k+k5VBj15PO-dVNGL7
        try {
            Class.forName(driverName);
        } catch (Exception e) {
            e.printStackTrace();
        }

        String databaseName = "ods";

        try (Connection connection = DriverManager.getConnection(url)) {

            final DatabaseMetaData metaData = connection.getMetaData();
            String catalogName = connection.getCatalog();
            System.out.println("cataloganme:" + catalogName);
            String schemaName = connection.getSchema();
            System.out.println("");

//            ResultSet resultSet = metaData.getSchemas(catalogName, schemaName);
            ResultSet resultSet = metaData.getSchemas();

            while (resultSet.next()) {
                String schemaName2 = resultSet.getString(1);
                System.out.println(resultSet.getString(1));
            }

//            ResultSet resultSet = metaData.getTables(catalogName, "ods", null, ImmutableList.of("TABLE").toArray(new String[0]));
//            while (resultSet.next()) {
//                if (Objects.equals(resultSet.getString("TABLE_SCHEM"), databaseName)) {
//                    System.out.println(resultSet.getString("TABLE_NAME"));
//
//                }
//            }


//            ResultSet columns = metaData.getColumns(catalogName, "ods", "ods_user_info", null);
//            while (columns.next()) {
//                // TODO(yunqing): check schema and catalog also
//                if (Objects.equals(columns.getString("TABLE_NAME"), "ods_user_info")) {
//                    String string = columns.getString("TYPE_NAME");
//                    String string1 = columns.getString("COLUMN_NAME");
//                    System.out.println(string + "...."  + string1);
//                }
//            }

//            try (Statement st = conn.createStatement()) {
//                String sql = "SELECT * FROM ods.ods_t_pipi limit 100";
//                try (ResultSet rs = st.executeQuery(sql)) {
//                    while (rs.next()) {
//                        //获取数据表中第一列数据值
//                        String c1 = rs.getString(1);
//                    }
//                }
//            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }

}
