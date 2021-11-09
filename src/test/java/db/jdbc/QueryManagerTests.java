package db.jdbc;

import db.jbdc.dao.InvoiceDAO;
import db.jbdc.dao.InvoiceItemDAO;
import db.jbdc.dao.OrganizationDAO;
import db.jbdc.dao.ProductDAO;
import org.junit.Before;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class QueryManagerTests {

    // no tests yet :(

    @Mock
    static private Connection connection;

    @Mock
    private Statement statement;

    @Mock
    private ResultSet resultSet;

    InvoiceDAO invoiceDAO;
    InvoiceItemDAO invoiceItemDAO;
    OrganizationDAO organizationDAO;
    ProductDAO productDAO;

    @Before
    public void init() throws SQLException {
        MockitoAnnotations.initMocks(this);
        Mockito.when(connection.createStatement()).thenReturn(statement);
    }


}
