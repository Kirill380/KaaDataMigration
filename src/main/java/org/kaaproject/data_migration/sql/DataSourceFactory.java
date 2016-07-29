package org.kaaproject.data_migration.sql;


import org.apache.commons.dbcp.BasicDataSource;

import javax.sql.DataSource;

public class DataSourceFactory {
    private final String USER_NAME = "sqladmin";
    private final String PASSWORD = "admin";
    private final String DB_NAME =  "kaa";

    public DataSource getDataSource(String type) {
        switch (type) {
            case "mariaDB" : return getMariaDB();
            case "postgreSQL" : return getPostgreSQL();
            default:  return null;
        }
    }

    private DataSource getPostgreSQL() {
        BasicDataSource bds = new BasicDataSource();
        bds.setDriverClassName("org.hibernate.dialect.PostgreSQL82Dialect");
        bds.setUrl("jdbc:postgresql://localhost:5432/" + DB_NAME);
        bds.setUsername(USER_NAME);
        bds.setPassword(PASSWORD);
        return bds;
    }


    private DataSource getMariaDB() {
        BasicDataSource bds = new BasicDataSource();
        bds.setDriverClassName("org.mariadb.jdbc.Driverr");
        bds.setUrl("jdbc:mysql:failover://localhost:3306/" + DB_NAME);
        bds.setUsername(USER_NAME);
        bds.setPassword(PASSWORD);
        return bds;
    }
}

