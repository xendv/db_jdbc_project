package db.jbdc;

import db.jbdc.dao.QueryManager;
import db.jbdc.entities.Organization;
import db.jbdc.initializers.DBFlywayInitializer;
import db.jbdc.initializers.JDBCSettingsProvider;
import org.jetbrains.annotations.NotNull;

import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.SQLException;

public class MainApplication {
    public static final @NotNull
    JDBCSettingsProvider JDBC_SETTINGS = JDBCSettingsProvider.DEFAULT;

    public static void main (String ... args){
        try (Connection connection = DriverManager.getConnection(JDBC_SETTINGS.url(), JDBC_SETTINGS.login(), JDBC_SETTINGS.password())) {
            DBFlywayInitializer.initDBFlyway();

            // QueryManager check

            System.out.println("\n get10ByProductsQuantity");
            final QueryManager queryManager = new QueryManager(connection);
            queryManager.get10ByProductsQuantity().forEach(System.out::println);

            System.out.println("\n getAvgPriceByPeriod");
            Date start = java.sql.Date.valueOf("1999-01-01");
            Date end = java.sql.Date.valueOf("2000-01-01");
            int product_id = 111;
            System.out.println("Interval start = " + start.toString() + ", Interval end = " + end.toString() +
                    ", product_id = " + product_id);
            System.out.println(queryManager.getAvgPriceByPeriod(product_id, start, end));

            System.out.println("\n getProductsAndOrgInPeriod");
            queryManager.getProductsAndOrgInPeriod(start, end).forEach(
                    entryHashMap -> {
                            String product = "";
                            Organization organization = (Organization) entryHashMap.keySet().toArray()[0];
                            if (entryHashMap.get(organization) != null)
                                product = entryHashMap.get(organization).toString();
                            else product = " shipped no products during this period";
                            System.out.println(organization + " " + product);
                    });
        }
        catch (SQLException exception) {
            System.err.println(exception.getMessage());
        }
    }
}
