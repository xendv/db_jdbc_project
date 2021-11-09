package db.jbdc.dao;

import db.jbdc.entities.Organization;
import db.jbdc.entities.Product;
import org.jetbrains.annotations.NotNull;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

// класс с отчетами
public class QueryManager {
    private final @NotNull Connection connection;

    public QueryManager(@NotNull Connection connection) {
        this.connection = connection;
    }

    // Выбрать первые 10 поставщиков по количеству поставленного товара
    @NotNull
    public List<Organization> get10ByProductsQuantity() {
        final List<Organization> result = new ArrayList<>(10);
        try (var statement = connection.createStatement()) {
            String query ="SELECT itn, name, payment_account, SUM(quantity) as quantityOfProducts\n" +
                    "FROM organization JOIN invoice\n" +
                    "ON invoice.sender_org_itn=organization.itn\n" +
                    "JOIN invoice_item ON invoice_item.invoice_id = invoice.id\n" +
                    "group by itn order by quantityOfProducts DESC\n" +
                    "LIMIT 10";
            try (var resultSet = statement.executeQuery(query)) {
                while (resultSet.next()) {
                    result.add(new Organization(resultSet.getInt("itn"), resultSet.getString("name"), resultSet.getString("payment_account")));
                }
                return result;
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return result;
    }

    // Рассчитать среднюю цену полученного товара за период
    @NotNull
    public double getAvgPriceByPeriod(int inner_code, Date start, Date end){
        double result = 0;
        String query = "SELECT ROUND(AVG(price),2) as avg_price FROM invoice \n" +
                "INNER JOIN invoice_item ON invoice_id = invoice.id\n" +
                "Where date - ?>0 AND date - ?<0\n" +
                "AND product_code = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(query)){
            preparedStatement.setDate(1,start);
            preparedStatement.setDate(2,end);
            preparedStatement.setInt(3,inner_code);
            try (var resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    result = resultSet.getDouble("avg_price");
                    return result;
                }
                throw new IllegalStateException("No product with inner_code " + inner_code + "found in this period");
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        throw new IllegalStateException("No product with inner_code " + inner_code + "found in this period");
    }

    // Вывести список товаров, поставленных организациями за период.
    // Если организация товары не поставляла, то она все равно должна быть отражена в списке.
    @NotNull
    public ArrayList<HashMap<Organization,Product>> getProductsAndOrgInPeriod(Date start, Date end) {
        ArrayList<HashMap<Organization,Product>> result = new ArrayList<>();
        String query ="SELECT * FROM organization\n" +
                "LEFT OUTER JOIN invoice ON\n" +
                "(invoice.sender_org_itn = organization.itn\n" +
                "AND date >= ? AND date <= ?)\n" +
                "LEFT JOIN invoice_item ON\n" +
                "(invoice_item.invoice_id = invoice.id)\n" +
                "ORDER BY \n" +
                "itn, date, invoice_id, product_code";
        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setDate(1,start);
            preparedStatement.setDate(2,end);
            try (var resultSet = preparedStatement.executeQuery()) {
                ProductDAO productDao = new ProductDAO(connection);
                while (resultSet.next()) {
                    Organization tempOrganization = new Organization(resultSet.getInt("itn"), resultSet.getString("name"), resultSet.getString("payment_account"));
                    if (resultSet.getObject("product_code") != null){
                        result.add(new HashMap(){{ put(tempOrganization, productDao.get(resultSet.getInt("product_code")));}});
                    }
                    else result.add(new HashMap(){{ put(tempOrganization, null);}});
                }
                return result;
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return result;
    }
}
