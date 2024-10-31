CREATE DATABASE IF NOT EXISTS `data_meta_db` CHARACTER SET 'utf8';

CREATE TABLE `data_meta_db`.`t_meta_catalog`
(
    `f_catalog_id`      BIGINT(20) UNSIGNED NOT NULL COMMENT 'catalog id',
    `f_catalog_name`    VARCHAR(128)        NOT NULL COMMENT 'catalog name',
    `f_catalog_comment` VARCHAR(256)                 DEFAULT '' COMMENT 'catalog comment',
    `f_type`            VARCHAR(64)         NOT NULL COMMENT 'catalog type, RELATIONAL|FILESET|MESSAGE',
    `f_provider`        VARCHAR(64)         NOT NULL COMMENT 'catalog provider, JDBC_POSTGRESQL|JDBC_MYSQL|JDBC_STARROCKS|MAXCOMPUTE|HIVE',
    `f_properties`      MEDIUMTEXT                   DEFAULT NULL COMMENT 'catalog properties',
    `f_create_time`    datetime            NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `f_modify_time`    datetime           NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`f_catalog_id`)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_bin COMMENT 'metadata-catalog';


CREATE TABLE `data_meta_db`.`t_meta_schema`
(
    `schema_id`      BIGINT(20) UNSIGNED NOT NULL COMMENT 'schema id',
    `schema_name`    VARCHAR(128)        NOT NULL COMMENT 'schema name',
    `schema_comment` VARCHAR(256)                 DEFAULT '' COMMENT 'schema comment',

    `catalog_id`     BIGINT(20) UNSIGNED NOT NULL COMMENT 'catalog id',

    `create_time`   datetime            NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `modify_time`   timestamp           NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`schema_id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_bin COMMENT 'metadata-schema';


CREATE TABLE `data_meta_db`.`t_meta_table`
(
    `f_table_id`    BIGINT(20) UNSIGNED NOT NULL COMMENT 'table id',
    `f_table_name`  VARCHAR(128)        NOT NULL COMMENT 'table name',

    `catalog_id`   BIGINT(20) UNSIGNED NOT NULL COMMENT 'catalog id',
    `schema_id`    BIGINT(20) UNSIGNED NOT NULL COMMENT 'schema id',

    `Fcreate_time` datetime            NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `Fmodify_time` timestamp           NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`f_table_id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_bin COMMENT 'metadata-table';



CREATE TABLE `data_meta_db`.`t_meta_column`
(
    `id`                    BIGINT(20) UNSIGNED NOT NULL AUTO_INCREMENT COMMENT 'auto increment id',

    `Fcolumn_id`            BIGINT(20) UNSIGNED NOT NULL COMMENT 'column id',
    `Fcolumn_name`          VARCHAR(128)        NOT NULL COMMENT 'column name',


    `column_name`           VARCHAR(128)        NOT NULL COMMENT 'column name',
    `column_type`           TEXT                NOT NULL COMMENT 'column type',
    `column_comment`        VARCHAR(256)                 DEFAULT '' COMMENT 'column comment',
    `column_nullable`       TINYINT(1)          NOT NULL DEFAULT 1 COMMENT 'column nullable, 0 is not nullable, 1 is nullable',
    `column_auto_increment` TINYINT(1)          NOT NULL DEFAULT 0 COMMENT 'column auto increment, 0 is not auto increment, 1 is auto increment',
    `column_default_value`  TEXT                         DEFAULT NULL COMMENT 'column default value',


    `catalog_id`            BIGINT(20) UNSIGNED NOT NULL COMMENT 'catalog id',
    `schema_id`             BIGINT(20) UNSIGNED NOT NULL COMMENT 'schema id',
    `table_id`              BIGINT(20) UNSIGNED NOT NULL COMMENT 'table id',


    `Fcreate_time`          datetime            NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `Fmodify_time`          timestamp           NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_bin COMMENT 'metadata-column';



-- 分区表-记录分区字段

CREATE TABLE `data_meta_db`.`t_meta_partition_keys`
(

    `f_table_id`    BIGINT(20) UNSIGNED NOT NULL COMMENT 'table id',

    `f_pkey_name`          VARCHAR(128)        NOT NULL COMMENT 'partition name,eg: dt',
    `f_pkey_type`          VARCHAR(128)        NOT NULL COMMENT 'partition type, eg: string',
    `f_pkey_comment`          VARCHAR(128)        NOT NULL COMMENT 'partition comment, eg: dt partition col',

    `f_create_time`          datetime            NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'create time',
    `f_modify_time`          timestamp           NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'modify time',
    PRIMARY KEY (`f_table_id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_bin COMMENT 'metadata-partition-keys';

-- 分区表-记录表的每个分区的所有分区信息

CREATE TABLE `data_meta_db`.`t_meta_partition`
(
    `fid`                    BIGINT(20) UNSIGNED NOT NULL AUTO_INCREMENT COMMENT 'auto increment id',

    `f_part_id`            BIGINT(20) UNSIGNED NOT NULL COMMENT 'partition id, auto assign',
    `f_part_name`          VARCHAR(128)        NOT NULL COMMENT 'partition name, eg: dt=2024-10-24',

    `f_table_id`              BIGINT(20) UNSIGNED NOT NULL COMMENT 'relation table id',

    `f_create_time`          datetime            NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `f_modify_time`          timestamp           NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`fid`),
    UNIQUE KEY `uk_pid_tid_ptn` (`f_table_id`,`f_part_name`),
    KEY key_ptid (`f_part_id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_bin COMMENT 'metadata-partition';


-- 分区表-记录表的每个分区的参数信息,如分区大小/分区实际大小/分区包含文件的数量/分区行数/分区ddl时间

CREATE TABLE `data_meta_db`.`t_meta_partition_params`
(
    `fid`                    BIGINT(20) UNSIGNED NOT NULL AUTO_INCREMENT COMMENT 'auto increment id',

    `f_part_id`            BIGINT(20) UNSIGNED NOT NULL COMMENT 'column id',

    `f_params_key`            BIGINT(20) UNSIGNED NOT NULL COMMENT 'column id',
    `f_params_value`            BIGINT(20) UNSIGNED NOT NULL COMMENT 'column id',

    `f_create_time`          datetime            NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `f_modify_time`          timestamp           NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`fid`),
    KEY key_ptid (`f_part_id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_bin COMMENT 'metadata-partition-keys';

