package info.preva1l.fadlc.persistence;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@AllArgsConstructor
@RequiredArgsConstructor
public enum DatabaseType {
    SQLITE("sqlite", "SQLite", "org.sqlite.JDBC"),
    MYSQL("mysql", "MySQL", "com.mysql.cj.jdbc.Driver"),
    MARIADB("mariadb", "MariaDB", "org.mariadb.jdbc.Driver"),
    MONGO("mongodb", "MongoDB"),
    ;

    private final String id;
    private final String friendlyName;
    private String driverClass;

    public boolean isLocal() {
        return this == DatabaseType.SQLITE;
    }
}