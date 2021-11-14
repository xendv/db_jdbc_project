package db.jdbc;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import db.jdbc.dao.QueryManager;
import db.jdbc.entities.Organization;
import db.jdbc.entities.Product;
import db.jdbc.initializers.DBFlywayInitializer;
import db.jdbc.initializers.JDBCSettingsProvider;
import org.hamcrest.Matchers;
import org.jetbrains.annotations.NotNull;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.lang.reflect.Type;
import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertEquals;

public class QueryManagerTests {

    Gson gson = new Gson();

    String get10ByProductsQuantityJson = "[{itn:2222222, name:'Provider2', paymentAccount:'222333444'}," +
    "{itn:3333333, name:'Provider3', paymentAccount:'333444555'}," +
    "{itn:1111111, name:'ItemsProvider', paymentAccount:'111222333'}]";

    String getOrganisationsWithProductsJson = "[{itn=1111111, name='ItemsProvider', paymentAccount='111222333'}," +
            "{itn:3333333, name:'Provider3', paymentAccount:'333444555'}]";

    String getQuantityAndSumByPeriodString = "Product{internalCode=111, name='fork'} total quantity = 500 total cost = 44000\n" +
    "Product{internalCode=333, name='plate'} total quantity = 250 total cost = 75000\n" +
    "Product{internalCode=222, name='spoon'} total quantity = 900 total cost = 66000\n";

    double getAvgPriceByPeriodAnswer = 90.0;

    String getProductsAndOrgInPeriodString = "Organisation{itn=1111111, name='ItemsProvider', paymentAccount='111222333'} Product{internalCode=111, name='fork'}\n" +
            "Organisation{itn=1111111, name='ItemsProvider', paymentAccount='111222333'} Product{internalCode=222, name='spoon'}\n" +
            "Organisation{itn=1111111, name='ItemsProvider', paymentAccount='111222333'} Product{internalCode=111, name='fork'}\n" +
            "Organisation{itn=2222222, name='Provider2', paymentAccount='222333444'} Product{internalCode=111, name='fork'}\n" +
            "Organisation{itn=2222222, name='Provider2', paymentAccount='222333444'} Product{internalCode=222, name='spoon'}\n" +
            "Organisation{itn=2222222, name='Provider2', paymentAccount='222333444'} Product{internalCode=333, name='plate'}\n" +
            "Organisation{itn=3333333, name='Provider3', paymentAccount='333444555'}  shipped no products during this period\n";

    Product forkProduct = new Product(111, "fork");
    Product tableProduct = new Product(444, "table");
    Map<Product, Integer> prodQuantmap =  Map.of(forkProduct, 300,
            tableProduct, 20);

    Type orgListType = new TypeToken<List<Organization>>(){}.getType();

    Date start = java.sql.Date.valueOf("1999-01-01");
    Date end = java.sql.Date.valueOf("2000-01-01");

    public static final @NotNull
    JDBCSettingsProvider JDBC_SETTINGS = JDBCSettingsProvider.DEFAULT;

    @Mock
    static private Connection connection;

    private QueryManager queryManager;

    @Before
    public void init() {
        MockitoAnnotations.initMocks(this);
        initDB();
        queryManager = new QueryManager(connection);
    }

    @After
    public void after(){
        try{
            connection.close();
        }
        catch (SQLException exception) {
            System.err.println(exception.getMessage());
        }
    }

    @Test
    public void get10ByProductsQuantityTest(){
        List<Organization> answer = gson.fromJson(get10ByProductsQuantityJson, orgListType);
        assertThat(answer, Matchers.containsInAnyOrder(queryManager.get10ByProductsQuantity().toArray()));
    }

    @Test
    public void getGetOrganisationsWithProductsORTest(){
        List<Organization> answer = gson.fromJson(getOrganisationsWithProductsJson, orgListType);
        assertThat(answer, Matchers.containsInAnyOrder(queryManager.getOrganisationsWithProductsOR(prodQuantmap).toArray()));
    }

    @Test
    public void getQuantityAndSumByPeriodTest(){
        assertEquals(getQuantityAndSumByPeriodString, queryManager.getQuantityAndSumByPeriodHReadable(start,end));
    }

    @Test
    public void getAvgPriceByPeriodTest(){
        assertEquals(getAvgPriceByPeriodAnswer, queryManager.getAvgPriceByPeriod(forkProduct.getInternalCode(), start, end), 0.0);
    }

    @Test
    public void getProductsAndOrgInPeriodTest(){
        assertEquals(getProductsAndOrgInPeriodString, queryManager.getProductsAndOrgInPeriodHReadable(start,end));
    }

    private void initDB(){
        try {
            connection = DriverManager.getConnection(JDBC_SETTINGS.url(), JDBC_SETTINGS.login(), JDBC_SETTINGS.password());
            DBFlywayInitializer.initDBFlyway();
        }
        catch (SQLException exception) {
            System.err.println(exception.getMessage());
        }
    }

}
