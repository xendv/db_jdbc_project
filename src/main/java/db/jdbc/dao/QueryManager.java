package db.jdbc.dao;

import db.jdbc.entities.Organization;
import db.jdbc.entities.Product;
import org.jetbrains.annotations.NotNull;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

// класс с отчетами
public class QueryManager {
    // Выбрать первые 10 поставщиков по количеству поставленного товара
    private final String get10ByProductsQuantity = "SELECT itn, name, payment_account FROM organization" +
            " JOIN invoice ON invoice.sender_org_itn=organization.itn" +
            " JOIN invoice_item ON invoice_item.invoice_id = invoice.id" +
            " GROUP BY itn ORDER BY SUM(quantity) DESC" +
            " LIMIT 10";

    // Выбрать поставщиков с суммой поставленного товара выше указанного количества
    // (товар и его количество должны допускать множественное указание).
    // Реализован вариант 'OR'
    private final String getGetOrganisationsWithProductsTemplate = "SELECT itn, name, payment_account, product_code," +
            " SUM(invoice_item.quantity) AS full_quantity FROM organization" +
            " INNER JOIN invoice ON invoice.sender_org_itn=organization.itn" +
            " INNER JOIN invoice_item ON invoice_item.invoice_id = invoice.id" +
            " WHERE ";

    // За каждый день для каждого товара рассчитать количество и сумму полученного товара
    // в указанном периоде, посчитать итоги за период
    private final String getQuantityAndSumByPeriod = "SELECT product.internal_code, product.name," +
            " SUM(invoice_item.quantity) AS full_quantity," +
            " SUM(invoice_item.quantity * invoice_item.price) AS total_sum FROM product" +
            " JOIN invoice_item ON invoice_item.product_code = product.internal_code" +
            " JOIN invoice ON invoice.date >= ? AND invoice.date <= ?" +
            " AND invoice.id = invoice_item.invoice_id" +
            " GROUP BY internal_code" +
            " ORDER BY internal_code";

    // Рассчитать среднюю цену полученного товара за период
    private final String getAvgPriceByPeriod = "SELECT AVG(price) as avg_price FROM invoice" +
            " INNER JOIN invoice_item ON invoice_id = invoice.id" +
            " WHERE date > ? AND date < ?" +
            " AND product_code = ?";

    // Вывести список товаров, поставленных организациями за период.
    // Если организация товары не поставляла, то она все равно должна быть отражена в списке.
    private final String getProductsAndOrgInPeriod = "SELECT * FROM organization" +
            " LEFT OUTER JOIN invoice ON (invoice.sender_org_itn = organization.itn" +
            " AND date >= ? AND date <= ?)" +
            " LEFT JOIN invoice_item ON (invoice_item.invoice_id = invoice.id)" +
            " ORDER BY itn, date, invoice_id, product_code";

    private final @NotNull Connection connection;

    public QueryManager(@NotNull Connection connection) {
        this.connection = connection;
    }

    // Выбрать первые 10 поставщиков по количеству поставленного товара
    @NotNull
    public List<Organization> get10ByProductsQuantity() {
        final List<Organization> result = new ArrayList<>(10);
        try(PreparedStatement preparedStatement = connection.prepareStatement(get10ByProductsQuantity)){
            try (var resultSet = preparedStatement.executeQuery()) {
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

    // Выбрать поставщиков с суммой поставленного товара выше указанного количества
    // (товар и его количество должны допускать множественное указание).
    // Реализован вариант 'OR'
    @NotNull
    public List<Organization> getOrganisationsWithProductsOR(Map<Product, Integer> productMap) {
        ArrayList<Organization> result = new ArrayList<>();

        // making query
        StringBuilder query = new StringBuilder();
        query.append(getGetOrganisationsWithProductsTemplate);
        productMap.forEach(
                (product, indicators) -> {
                    if (query.length() != getGetOrganisationsWithProductsTemplate.length()) query.append(" OR ");
                    query.append("invoice_item.product_code = ?");
                });
        query.append(" GROUP BY itn, product_code");

        try (PreparedStatement preparedStatement = connection.prepareStatement(query.toString())) {
            // filling preparedStatement
            int fieldIndex = 1;
            for(Map.Entry<Product, Integer> entry : productMap.entrySet()) {
                Product key = entry.getKey();
                preparedStatement.setInt(fieldIndex++, key.getInternalCode());
            }

            //executing prepared statement
            try (var resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    Organization tempOrganization = new Organization
                            (resultSet.getInt("itn"),
                                    resultSet.getString("name"),
                                    resultSet.getString("payment_account"));
                    int productId = resultSet.getInt("product_code");
                    int productFullQuantity = resultSet.getInt("full_quantity");
                    Product tempProduct =  productMap.keySet().stream().filter(product ->
                            productId == product.getInternalCode()).findFirst().orElse(null);
                    if (productFullQuantity >= productMap.get(tempProduct) && !result.contains(tempOrganization))
                        result.add(tempOrganization);
                }
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return result;

    }

    // За каждый день для каждого товара рассчитать количество и сумму полученного товара
    // в указанном периоде, посчитать итоги за период
    @NotNull
    public Map<Product,Map<Integer, Integer>> getQuantityAndSumByPeriod(Date start, Date end){
        HashMap<Product,Map<Integer, Integer>> result = new HashMap<>();
        try (PreparedStatement preparedStatement = connection.prepareStatement(getQuantityAndSumByPeriod)){
            preparedStatement.setDate(1,start);
            preparedStatement.setDate(2,end);
            try (var resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    Map<Integer, Integer> indicatorsHashMap = Map.of(
                           resultSet.getInt("full_quantity"),
                            resultSet.getInt("total_sum")
                    );
                    result.put(new Product(resultSet.getInt("internal_code"),
                            resultSet.getString("name")), indicatorsHashMap);
                }
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return result;
    }
    // Human-readable version
    public String getQuantityAndSumByPeriodHReadable(Date start, Date end){
        StringBuilder stringBuilder = new StringBuilder();
        getQuantityAndSumByPeriod(start, end).forEach(
                (product, indicators) -> {
                    String productResult = product + " total quantity = " + indicators.keySet().toArray()[0] +
                            " total cost = " + indicators.values().toArray()[0] + "\n";
                    stringBuilder.append(productResult);
                });
        return stringBuilder.toString();
    }

    // Рассчитать среднюю цену полученного товара за период
    //@NotNull
    public double getAvgPriceByPeriod(int innerCode, Date start, Date end){
        double result;
        try (PreparedStatement preparedStatement = connection.prepareStatement(getAvgPriceByPeriod)){
            preparedStatement.setDate(1,start);
            preparedStatement.setDate(2,end);
            preparedStatement.setInt(3,innerCode);
            try (var resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    result = resultSet.getDouble("avg_price");
                    return result;
                }
                throw new IllegalStateException("No product with inner_code " + innerCode + "found in this period");
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        throw new IllegalStateException("No product with inner_code " + innerCode + "found in this period");
    }

    // Вывести список товаров, поставленных организациями за период.
    // Если организация товары не поставляла, то она все равно должна быть отражена в списке.
    @NotNull
    public ArrayList<HashMap<Organization,Product>> getProductsAndOrgInPeriod(Date start, Date end) {
        ArrayList<HashMap<Organization,Product>> result = new ArrayList<>();
        try (PreparedStatement preparedStatement = connection.prepareStatement(getProductsAndOrgInPeriod)) {
            preparedStatement.setDate(1,start);
            preparedStatement.setDate(2,end);
            try (var resultSet = preparedStatement.executeQuery()) {
                ProductDAO productDao = new ProductDAO(connection);
                while (resultSet.next()) {
                    Organization tempOrganization = new Organization(resultSet.getInt("itn"), resultSet.getString("name"), resultSet.getString("payment_account"));
                    HashMap<Organization, Product> oPhm = new HashMap<>();
                    if (resultSet.getObject("product_code") != null){
                        oPhm.put(tempOrganization, productDao.get(resultSet.getInt("product_code")));
                    }
                    else oPhm.put(tempOrganization, null);
                    result.add(oPhm);
                }
                return result;
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return result;
    }
    // Human-readable version
    @NotNull
    public String getProductsAndOrgInPeriodHReadable(Date start, Date end) {
        StringBuilder stringBuilder = new StringBuilder();
        getProductsAndOrgInPeriod(start, end).forEach(
                entryHashMap -> {
                    String resultPart;
                    Organization organization = (Organization) entryHashMap.keySet().toArray()[0];
                    if (entryHashMap.get(organization) != null)
                        resultPart = entryHashMap.get(organization).toString();
                    else resultPart = " shipped no products during this period";
                    resultPart = organization + " " + resultPart + "\n";
                    stringBuilder.append(resultPart);
                });
        return stringBuilder.toString();
    }
}
