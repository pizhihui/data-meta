package com.archgeek.data.meta.mc;

import com.aliyun.odps.FileResource;
import com.aliyun.odps.Odps;
import com.aliyun.odps.OdpsException;
import com.aliyun.odps.Partition;
import com.aliyun.odps.PyResource;
import com.aliyun.odps.Resource;
import com.aliyun.odps.Resources;
import com.aliyun.odps.Shard;
import com.aliyun.odps.StorageTierInfo;
import com.aliyun.odps.Table;
import com.aliyun.odps.Table.ClusterInfo;
import com.aliyun.odps.Table.TableTypeConverter;
import com.aliyun.odps.TableLifecycleConfig;
import com.aliyun.odps.TableSchema;
import com.aliyun.odps.account.Account;
import com.aliyun.odps.account.AliyunAccount;
import com.aliyun.odps.rest.ResourceBuilder;
import com.aliyun.odps.rest.RestClient;
import com.aliyun.odps.rest.SimpleXmlUtils;
import com.aliyun.odps.simpleframework.xml.Attribute;
import com.aliyun.odps.simpleframework.xml.Element;
import com.aliyun.odps.simpleframework.xml.ElementList;
import com.aliyun.odps.simpleframework.xml.Root;
import com.aliyun.odps.simpleframework.xml.Text;
import com.aliyun.odps.simpleframework.xml.convert.Convert;
import com.aliyun.odps.utils.NameSpaceSchemaUtils;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * @author pizhihui
 * @date 2024-09-25 20:55
 */
public class MaxComputeClientTool {
    private static final Logger logger = LoggerFactory.getLogger(MaxComputeClientTool.class);

    private static String accessId = System.getenv("MC_KEY");
    private static String accessKey = System.getenv("MC_SECRET");

    //默认情况下，使用公网进行传输。如果需要通过内网进行数据传输，必须设置tunnelUrl变量。
    //此处取值为华东2经典网络Tunnel Endpoint。
    private static String odpsUrl = "http://service.cn-beijing.maxcompute.aliyun.com/api";
    private static String tunnelUrl = "http://dt.cn-beijing.maxcompute.aliyun.com";


    private static String project = "znzz_pipi_ads";
    private static String partition = "sale_date=201312,region=hangzhou";

    private static final String STAGE_FORMAT = "%-26s %13s  %5s  %9s  %7s  %7s  %6s";

    private MaxComputeClientTool() {

    }

    private static Odps createClient() {
        Account account = new AliyunAccount(accessId, accessKey);
        Odps odps = new Odps(account);
        odps.setEndpoint(odpsUrl);
        odps.setDefaultProject(project);
        return odps;
    }

    public static Odps getClient() {
        return Holder.INSTANCE;
    }

    private static class Holder {
        private static final Odps INSTANCE = createClient();
    }

    @Root(name = "Column", strict = false)
    static class ColumnModel {

        @Attribute(name = "Name", required = false)
        protected String columnName;

        @Attribute(name = "Value", required = false)
        protected String columnValue;
    }

    @Root(name = "Partition", strict = false)
    static class PartitionModel {

        @ElementList(entry = "Column", inline = true, required = false)
        List<ColumnModel> columns = new ArrayList<ColumnModel>();

        @Element(name = "CreationTime", required = false)
        @Convert(SimpleXmlUtils.EpochConverter.class)
        Date createdTime;

        @Element(name = "LastDDLTime", required = false)
        @Convert(SimpleXmlUtils.EpochConverter.class)
        Date lastMetaModifiedTime;

        @Element(name = "LastModifiedTime", required = false)
        @Convert(SimpleXmlUtils.EpochConverter.class)
        Date lastDataModifiedTime;

        @Element(name = "LastAccessTime", required = false)
        @Convert(SimpleXmlUtils.EpochConverter.class)
        Date lastAccessTime;

        /**
         * 分层存储相关
         */
        StorageTierInfo storageTierInfo;
    }

    @Root(name = "Partitions", strict = false)
    private static class ListPartitionsResponse {

        @ElementList(entry = "Partition", inline = true, required = false)
        private List<PartitionModel> partitions = new LinkedList<PartitionModel>();

        @Element(name = "Marker", required = false)
        @Convert(SimpleXmlUtils.EmptyStringConverter.class)
        private String marker;

        @Element(name = "MaxItems", required = false)
        private Integer maxItems;
    }

    @Root(name = "Table", strict = false)
    static class TableModel {
        @Root(name = "Schema", strict = false)
        static class Schema {

            @Text(required = false)
            String content;
        }

        @Element(name = "Name", required = false)
        @Convert(SimpleXmlUtils.EmptyStringConverter.class)
        String name;

        @Element(name = "TableId", required = false)
        @Convert(SimpleXmlUtils.EmptyStringConverter.class)
        String ID;

        @Attribute(name = "format", required = false)
        private String format;

        @Element(name = "Schema", required = false)
        private Schema schema;

        @Element(name = "Comment", required = false)
        @Convert(SimpleXmlUtils.EmptyStringConverter.class)
        String comment;

        @Element(name = "Owner", required = false)
        @Convert(SimpleXmlUtils.EmptyStringConverter.class)
        String owner;

        @Element(name = "Project", required = false)
        @Convert(SimpleXmlUtils.EmptyStringConverter.class)
        String projectName;

        @Element(name = "SchemaName", required = false)
        @Convert(SimpleXmlUtils.EmptyStringConverter.class)
        String schemaName;

        @Element(name = "TableLabel", required = false)
        @Convert(SimpleXmlUtils.EmptyStringConverter.class)
        String tableLabel;

        @Element(name = "CryptoAlgo", required = false)
        @Convert(SimpleXmlUtils.EmptyStringConverter.class)
        String cryptoAlgoName;

        @Element(name = "TableMaskInfo", required = false)
        @Convert(SimpleXmlUtils.EmptyStringConverter.class)
        String tableMaskInfo;

        @Element(name = "CreationTime", required = false)
        @Convert(SimpleXmlUtils.DateConverter.class)
        Date createdTime;

        @Element(name = "LastModifiedTime", required = false)
        @Convert(SimpleXmlUtils.DateConverter.class)
        Date lastModifiedTime;

        @Element(name = "LastAccessTime", required = false)
        @Convert(SimpleXmlUtils.DateConverter.class)
        Date lastAccessTime;

        @Element(name = "Type", required = false)
        @Convert(TableTypeConverter.class)
        TableType type;

        Date lastMetaModifiedTime;
        Date lastMajorCompactTime;
        boolean isVirtualView;
        boolean isMaterializedViewRewriteEnabled;
        boolean isMaterializedViewOutdated;
        boolean isExternalTable;
        long life = -1L;
        long hubLifecycle = -1L;
        String viewText;
        String viewExpandedText;
        long size;
        long recordNum = -1L;

        boolean isArchived;
        long physicalSize;
        long fileNum;

        boolean isTransactional;
        // reserved json string in extended info
        String reserved;
        Shard shard;

        // for external table extended info
        String storageHandler;
        String location;
        String resources;
        Map<String, String> serDeProperties;

        // for clustered info
        ClusterInfo clusterInfo;
        // for table extended labels
        List<String> tableExtendedLabels;

        Map<String, String> mvProperties;

        List<Map<String, String>> refreshHistory;

        boolean hasRowAccessPolicy;
        List<String> primaryKey;
        int acidDataRetainHours;
        StorageTierInfo storageTierInfo;
        TableLifecycleConfig tableLifecycleConfig;

        List<ColumnMaskInfo> columnMaskInfoList;

    }

    public static class ColumnMaskInfo {

        private final String name;
        private final List<String> policyNameList;

        public String getName() {
            return name;
        }

        public List<String> getPolicyNameList() {
            return policyNameList;
        }

        ColumnMaskInfo(String name, List<String> policyNameList) {
            this.name = name;
            this.policyNameList = policyNameList;
        }
    }

    public enum TableType {
        /**
         * Regular table managed by ODPS
         */
        MANAGED_TABLE,
        /**
         * Virtual view
         */
        VIRTUAL_VIEW,
        /**
         * External table
         */
        EXTERNAL_TABLE,
        /**
         * Materialized view
         */
        MATERIALIZED_VIEW
    }

    @Root(name = "Partition", strict = false)
    private static class PartitionMeta {

        @Element(name = "Schema", required = false)
        @Convert(SimpleXmlUtils.EmptyStringConverter.class)
        private String schema;
    }


    public static List<String> listTablePartitions(String project, String tablename) {

        try {
            getClient().setDefaultProject(project);

            Table t = getClient().tables().get(tablename);
            t.reload();

////            Iterator<Partition> partitionIterator = t.getPartitionIterator();
            Iterator<Partition> partitionIterator = t.getPartitionIterator(
                    null,
                    true,
                    1000L,
                    10L
            );
            while (partitionIterator.hasNext()) {
                Partition partition = partitionIterator.next();
                System.out.println(
                        "name: " + partition.getPartitionSpec()
                                + ",CreatedTime: " + partition.getCreatedTime()
                                + ",num: " + partition.getRecordNum()
                                + ",filenum: " + partition.getFileNum()
                                + ",physicalsize: " + partition.getPhysicalSize()
                                + ",lastTime: " + partition.getLastDataModifiedTime()
                                + ",lastAccessTime: " + partition.getLastMetaModifiedTime()
                                + ",size:" + partition.getSize());
            }
        } catch (Exception e) {
            logger.error("get table  " + tablename + " partiton error: ", e);
        }
        return null;
    }

    private static List<String> getPartition(String project, String tablename) throws OdpsException {
        RestClient restClient = getClient().getRestClient();

        TableModel model = new TableModel();
        model.projectName = project;
        model.name = tablename;

        String resource = ResourceBuilder.buildTableResource(model.projectName, model.name);

        Map<String, String> params = new HashMap<>();
        params.put("maxitems", "10");
        params.put("partitions", null);
        params.put("expectmarker", "true"); // since sprint-11
        params.put("reverse", null);        // reverse
//            params.put("marker", "ZHQ9MjAyNC0wOS0xOC9kcz1tb3hpbmdmZW5fMzc0");

        ListPartitionsResponse
                resp =
                restClient.request(ListPartitionsResponse.class, resource, "GET", params);

        for (PartitionModel partitionModel : resp.partitions) {
            List<String> joiner = new ArrayList<>();
            for (ColumnModel column : partitionModel.columns) {
                String columnName = column.columnName;
                String columnValue = column.columnValue;
                joiner.add(columnName + "/" + columnValue);
            }

            System.out.println(
                    "schemaName" + model.schemaName
                            + "," + partitionModel.createdTime
                            + "," + partitionModel.lastAccessTime
                            + "," + partitionModel.lastMetaModifiedTime
                            + "," + partitionModel.lastDataModifiedTime
                            + "," + String.join(",", joiner));
        }
        System.out.println("marker:::::" + resp.marker);
        params.put("marker", resp.marker);

        return null;
    }

    public static List<String> getTableInfo(String tablename) {
        try {
            Table tableRes = getClient().tables().get(tablename);
            String comment = tableRes.getComment();
            Date createdTime = tableRes.getCreatedTime();
            Date lastMetaModifiedTime = tableRes.getLastMetaModifiedTime();
            Date lastDataModifiedTime = tableRes.getLastDataModifiedTime();
            long life = tableRes.getLife();
            String owner = tableRes.getOwner();
            long size = tableRes.getSize();
//            long physicalSize = tableRes.getPhysicalSize();
            long recordNum = tableRes.getRecordNum();
            TableSchema schema = tableRes.getSchema();
            String jsonSchema = tableRes.getJsonSchema();
            String schemaName = tableRes.getSchemaName();
            System.out.println();
        } catch (Exception e) {
            logger.error("get table  " + tablename + " error: ", e);
        }
        return null;
    }

    public static void getPartitonInfo(String projectName, String tablename, String schemaName) throws OdpsException {

        String resource = ResourceBuilder.buildTableResource(projectName, tablename);
        Map<String, String> params = NameSpaceSchemaUtils.initParamsWithSchema(schemaName);
        params.put("partition", "dt='2024-09-18',ds='moxingfen_402'");

        RestClient restClient = getClient().getRestClient();

        PartitionMeta meta = restClient.request(PartitionMeta.class, resource, "GET", params);
        JsonObject tree = new JsonParser().parse(meta.schema).getAsJsonObject();
        System.out.println(tree.toString());

    }


    public static List<String> listResources(String project) throws FileNotFoundException, OdpsException {
        Odps client = getClient();
        client.setDefaultProject(project);

        Resources resources = client.resources();
//        for (Resource resource : resources) {
//            System.out.println(resource.getName());
//        }

        String source = "/Users/pizhihui/juzi/code_juzi/mc-sql/scripts/resource/test1.py";
        File file = new File(source);
        InputStream is = new FileInputStream(file);
        Resource resource = new PyResource();
        String name = file.getName();
        resource.setName(name);

        resources.update(project, (FileResource) resource, is);

        return null;
    }

    public static void main(String[] args) throws OdpsException, FileNotFoundException {

        String table = "znzz_pipi_ads.t_test_vehicle_20240911_002_mc";

        System.out.println(getTableInfo(table));
//        System.out.println(listTablePartitions("znzz_fintech_dwd", "dwd_beforeloan_model_combine_id_import_csv"));
//        System.out.println(getPartition("znzz_fintech_dwd", "dwd_beforeloan_model_combine_id_import_csv_external_v2"));
//        getPartitonInfo("znzz_fintech_dwd", "dwd_beforeloan_model_combine_id_import_csv","");

//        listResources("znzz_fintech_ads");


    }

}
