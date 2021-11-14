package db.jdbc;

import db.jdbc.dao.InvoiceDAO;
import db.jdbc.dao.OrganizationDAO;
import db.jdbc.entities.Invoice;
import db.jdbc.entities.Organization;
import db.jdbc.initializers.DBFlywayInitializer;
import db.jdbc.initializers.JDBCSettingsProvider;
import org.hamcrest.Matchers;
import org.jetbrains.annotations.NotNull;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.MockitoAnnotations;

import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.List;

import static org.junit.Assert.*;

public class DAOTests {

    Organization selectedOrganization = new Organization(3333333, "Provider3", "333444555");

    Invoice selectedInvoice1 = new Invoice(3, Date.valueOf("1999-03-20"), 2222222);
    Invoice selectedInvoice12 = new Invoice(3, Date.valueOf("2233-10-23"), 2222222);
    Invoice selectedInvoice2 = new Invoice(5, Date.valueOf("2001-05-01"), 2222222);

    List<Invoice> allInvoices = List.of(
            new Invoice(1, Date.valueOf("1999-01-08"), 1111111),
            new Invoice(2, Date.valueOf("1999-03-20"), 1111111),
            selectedInvoice1,
            new Invoice(4, Date.valueOf("2000-04-04"), 3333333));

    List<Invoice> allInvoicesSub = List.of(
            new Invoice(1, Date.valueOf("1999-01-08"), 1111111),
            new Invoice(2, Date.valueOf("1999-03-20"), 1111111),
            selectedInvoice12,
            new Invoice(4, Date.valueOf("2000-04-04"), 3333333));

    List<Invoice> allInvoicesMinusOne = List.of(
            new Invoice(1, Date.valueOf("1999-01-08"), 1111111),
            new Invoice(2, Date.valueOf("1999-03-20"), 1111111),
            new Invoice(4, Date.valueOf("2000-04-04"), 3333333));

    public static final @NotNull
    JDBCSettingsProvider JDBC_SETTINGS = JDBCSettingsProvider.DEFAULT;

    private Connection connection;

    @Before
    public void init() {
        MockitoAnnotations.initMocks(this);
        initDB();
        //Mockito.when(connection.createStatement()).thenReturn(statement);
    }

    @After
    public void after(){
        try{
            this.connection.close();
        }
        catch (SQLException exception) {
            System.err.println(exception.getMessage());
        }
    }

    @Test
    public void getInvoiceById(){
        InvoiceDAO invoiceDAO = new InvoiceDAO (connection);
        Invoice invoice = invoiceDAO.get(2);
        assertEquals(invoice, allInvoices.get(1));
    }

    @Test
    public void getOrganizationByItn(){
        OrganizationDAO organizationDAO = new OrganizationDAO (connection);
        Organization organization = organizationDAO.get(3333333);
        assertEquals(organization, selectedOrganization);
    }

    @Test
    public void getAllInvoices(){
        InvoiceDAO invoiceDAO = new InvoiceDAO (connection);
        assertArrayEquals(allInvoices.toArray(), invoiceDAO.all().toArray());
    }


    @Test
    public void deleteInvoice(){
        InvoiceDAO invoiceDAO = new InvoiceDAO (connection);
        invoiceDAO.delete(selectedInvoice1);
        List<Invoice> invoices = invoiceDAO.all();
        assertEquals(3, invoices.size());
        assertArrayEquals(allInvoicesMinusOne.toArray(), invoices.toArray());
    }

    @Test(expected =  IllegalStateException.class)
    public void deleteNotExistingInvoice(){
        InvoiceDAO invoiceDAO = new InvoiceDAO (connection);
        invoiceDAO.delete(selectedInvoice2);
    }

    @Test
    public void updateInvoice(){
        InvoiceDAO invoiceDAO = new InvoiceDAO (connection);
        invoiceDAO.update(selectedInvoice12);
        List <Invoice> invoices =List.copyOf(invoiceDAO.all());
        assertEquals(allInvoicesSub.size(), invoices.size());
        assertThat(invoices, Matchers.containsInAnyOrder(invoices.toArray()));
    }

    @Test
    public void saveInvoice(){
        InvoiceDAO invoiceDAO = new InvoiceDAO (connection);
        invoiceDAO.save(selectedInvoice2);
        List <Invoice> invoices =List.copyOf(invoiceDAO.all());
        assertEquals(allInvoices.size() + 1, invoices.size());
        assertThat(invoices.toArray(), Matchers.hasItemInArray(selectedInvoice2));
    }

    private void initDB(){
        try {
            this.connection = DriverManager.getConnection(JDBC_SETTINGS.url(), JDBC_SETTINGS.login(), JDBC_SETTINGS.password());
            DBFlywayInitializer.initDBFlyway();
        }
        catch (SQLException exception) {
            System.err.println(exception.getMessage());
        }
    }
}
