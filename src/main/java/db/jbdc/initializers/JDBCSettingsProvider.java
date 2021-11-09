package db.jbdc.initializers;

import org.jetbrains.annotations.NotNull;

public final class JDBCSettingsProvider {

    public static final @NotNull
    JDBCSettingsProvider DEFAULT = new JDBCSettingsProvider(
            "127.0.0.1",
            "5432",
            "shipments_db",
            "postgres",
            ""
    );

    private static final @NotNull String PREFIX = "jdbc:postgresql";

    private final @NotNull String host;
    private final @NotNull String port;
    private final @NotNull String dbName;
    private final @NotNull String login;
    private final @NotNull String password;

    private JDBCSettingsProvider(@NotNull String host,
                                 @NotNull String port,
                                 @NotNull String dbName,
                                 @NotNull String login,
                                 @NotNull String password) {
        this.host = host;
        this.port = port;
        this.dbName = dbName;
        this.login = login;
        this.password = password;
    }

    public @NotNull String url() {
        return PREFIX
                + "://" + host + ':' + port
                + '/' + dbName;
    }

    public @NotNull String login() {
        return login;
    }

    public @NotNull String password() {
        return password;
    }
}
