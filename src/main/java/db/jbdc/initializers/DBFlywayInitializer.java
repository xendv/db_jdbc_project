package db.jbdc.initializers;

import org.flywaydb.core.Flyway;
import org.gradle.internal.impldep.com.esotericsoftware.minlog.Log;
import org.jetbrains.annotations.NotNull;

public class DBFlywayInitializer {
    public static final @NotNull
    JDBCSettingsProvider JDBC_SETTINGS = JDBCSettingsProvider.DEFAULT;

    public static void initDBFlyway() {
        final Flyway flyway = Flyway
                .configure()
                .dataSource(
                        JDBC_SETTINGS.url(),
                        JDBC_SETTINGS.login(),
                        JDBC_SETTINGS.password()
                )
                .locations("database_init_scripts")
                .load();
        flyway.clean();
        flyway.migrate();
        //System.out.println("Migrations applied successfully");
        Log.debug("Migrations applied successfully");
    }
}
