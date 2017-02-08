package cn.ijustone.crm.utils.page;

import org.springframework.beans.factory.annotation.Value;

/**
 *
 * 初始化数据库类型:ORACLE MYSQL
 *
 */
public class ConfigDBType {

    public static String DB_TYPE;

    @Value("${DB.TYPE:MySQL}")
    private String db_type;

    public void init() {
        DB_TYPE = this.db_type;
    }

}
