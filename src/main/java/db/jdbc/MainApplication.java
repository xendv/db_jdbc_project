package db.jdbc;

import db.jdbc.dao.QueryManager;
import db.jdbc.entities.Product;
import db.jdbc.initializers.DBFlywayInitializer;
import db.jdbc.initializers.JDBCSettingsProvider;
import org.jetbrains.annotations.NotNull;

import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Map;

public class MainApplication {
    public static final @NotNull
    JDBCSettingsProvider JDBC_SETTINGS = JDBCSettingsProvider.DEFAULT;

    public static void main (String ... args){
        try (Connection connection = DriverManager.getConnection(JDBC_SETTINGS.url(), JDBC_SETTINGS.login(), JDBC_SETTINGS.password())) {
            DBFlywayInitializer.initDBFlyway();

            // QueryManager check

            final QueryManager queryManager = new QueryManager(connection);
            Date start = java.sql.Date.valueOf("1999-01-01");
            Date end = java.sql.Date.valueOf("2000-01-01");

            System.out.println("\n get10ByProductsQuantity");
            queryManager.get10ByProductsQuantity().forEach(System.out::println);

            System.out.println("\n getOrganisationsWithProductsOR");
            queryManager.getOrganisationsWithProductsOR(
                    Map.of(new Product(111, "fork"), 300,
                            new Product(444, "table"), 20)
            ).forEach(System.out::println);

            System.out.println("\n getQuantityAndSumByPeriod");
            System.out.println(queryManager.getQuantityAndSumByPeriodHReadable(start, end));

            System.out.println(" getAvgPriceByPeriod");
            int product_id = 111;
            System.out.println("Interval start = " + start.toString() + ", Interval end = " + end.toString() +
                    ", product_id = " + product_id);
            System.out.println(queryManager.getAvgPriceByPeriod(product_id, start, end));

            System.out.println("\n getProductsAndOrgInPeriod");
            System.out.println(queryManager.getProductsAndOrgInPeriodHReadable(start, end));
        }
        catch (SQLException exception) {
            System.err.println(exception.getMessage());
        }
    }
}
