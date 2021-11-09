package db.jbdc.dao;

import db.jbdc.entities.InvoiceItem;
import org.jetbrains.annotations.NotNull;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class InvoiceItemDAO implements IDAO<InvoiceItem> {
    private final @NotNull
    Connection connection;

    public InvoiceItemDAO(@NotNull Connection connection) {
        this.connection = connection;
    }

    @NotNull
    @Override
    public InvoiceItem get(int id) {
        try (var statement = connection.createStatement()) {
            try (var resultSet = statement.executeQuery("SELECT invoice_id, product_code, price, quantity FROM invoice_item WHERE product_code = " + id)) {
                if (resultSet.next()) {
                    return new InvoiceItem(resultSet.getInt("invoice_id"), resultSet.getInt("product_code"), resultSet.getInt("price"),resultSet.getInt("quantity"));
                }
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        throw new IllegalStateException("Record with id " + id + "not found");
    }

    @NotNull
    @Override
    public List<InvoiceItem> all() {
        final List<InvoiceItem> result = new ArrayList<>();
        try (var statement = connection.createStatement()) {
            try (var resultSet = statement.executeQuery("SELECT * FROM invoice_item")) {
                while (resultSet.next()) {
                    result.add(new InvoiceItem(resultSet.getInt("invoice_id"), resultSet.getInt("product_code"), resultSet.getInt("price"), resultSet.getInt("quantity")));
                }
                return result;
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return result;
    }

    @Override
    public void save(@NotNull InvoiceItem entity) {
        try (PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO invoice_item(invoice_id, product_code, price, quantity) VALUES(?,?,?,?)")) {
            preparedStatement.setInt(1, entity.getInvoice_id());
            preparedStatement.setInt(2, entity.getProduct_code());
            preparedStatement.setInt(3, entity.getPrice());
            preparedStatement.setInt(4, entity.getProduct_code());
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    @Override
    public void update(@NotNull InvoiceItem entity) {
        try(PreparedStatement preparedStatement = connection.prepareStatement("UPDATE invoice_item SET quantity = ? AND price = ? WHERE invoice_id = ? AND product_code = ?")) {
            int fieldIndex = 1;
            preparedStatement.setInt(fieldIndex++, entity.getQuantity());
            preparedStatement.setInt(fieldIndex++, entity.getPrice());
            preparedStatement.setInt(fieldIndex++, entity.getInvoice_id());
            preparedStatement.setInt(fieldIndex++, entity.getProduct_code());
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    @Override
    public void delete(@NotNull InvoiceItem entity) {
        try(PreparedStatement preparedStatement = connection.prepareStatement("DELETE FROM invoice_item WHERE invoice_id = ? AND product_code = ?")) {
            preparedStatement.setInt(1, entity.getInvoice_id());
            preparedStatement.setInt(2, entity.getProduct_code());
            if (preparedStatement.executeUpdate() == 0) {
                throw new IllegalStateException("Record with invoice = " + entity.getInvoice_id() + "and product_code =" + entity.getProduct_code()+ " not found");
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }
}
