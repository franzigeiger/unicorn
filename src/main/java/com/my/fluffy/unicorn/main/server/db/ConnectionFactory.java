package com.my.fluffy.unicorn.main.server.db;

import org.apache.commons.dbcp2.BasicDataSource;

import javax.sql.DataSource;
import java.sql.*;

class ConnectionFactory {
    private static final String driver = "jdbc:postgresql://";
    private static final String driverClass = "org.postgresql.Driver";
    private static final String host = "localhost:5432/";
    private static final String database = "unicorn";
    private static final String schema = "election";
    private static final String username = "postgres";
    private static final String password = "root";

    private static BasicDataSource dataSource = null;

    static Connection create() throws SQLException, ClassNotFoundException {
        if (dataSource == null) createDataSource();
        return dataSource.getConnection();
    }

    private static void createDataSource() throws ClassNotFoundException {
        Class.forName(driverClass);

        dataSource = new BasicDataSource();
        dataSource.setDriverClassName(driverClass);
        dataSource.setUsername(username);
        dataSource.setPassword(password);
        dataSource.setUrl(driver + host + database + "?currentSchema=" + schema);
    }
}
