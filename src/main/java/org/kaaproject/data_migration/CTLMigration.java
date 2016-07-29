package org.kaaproject.data_migration;


import javax.sql.DataSource;

public class CTLMigration {

    private DataSource dataSource;

    public CTLMigration(DataSource dataSource) {
       this.dataSource = dataSource;
    }

    public void transform(String tableName) {

    }
}
