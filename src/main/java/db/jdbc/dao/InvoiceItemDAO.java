package db.jdbc.dao;

import db.jdbc.entities.InvoiceItem;
import org.jetbrains.annotations.NotNull;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class InvoiceItemDAO extends DAO<InvoiceItem>{

    final String saveInvoiceItemQuery = "INSERT INTO invoice_item(invoice_id, product_code, price, quantity) VALUES(?,?,?,?)";
    final String updateInvoiceItemQuery = "UPDATE invoice_item SET quantity = ? AND price = ? WHERE invoice_id = ? AND product_code = ?";

    public InvoiceItemDAO(@NotNull Connection connection) {
        super(connection);
        tableName = "invoice_item";
        primaryKey = "id";
    }

    @NotNull
    @Override
    public InvoiceItem get(int id) {
        try (PreparedStatement preparedStatement = getByIdPreparedStatement()) {
            preparedStatement.setInt(1, id);
            try (var resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    return new InvoiceItem(resultSet.getInt("invoice_id"), resultSet.getInt("product_code"), resultSet.getInt("price"),resultSet.getInt("quantity"));
                }
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        throw new IllegalStateException("Record with id " + id + " not found");
    }

    @NotNull
    @Override
    public List<InvoiceItem> all() {
        final List<InvoiceItem> result = new ArrayList<>();
        try (PreparedStatement preparedStatement = getAllPreparedStatement()) {
            try (var resultSet = preparedStatement.executeQuery()) {
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
        try (PreparedStatement preparedStatement = getPrepareStatement (saveInvoiceItemQuery)) {
            preparedStatement.setInt(1, entity.getInvoiceId());
            preparedStatement.setInt(2, entity.getProductCode());
            preparedStatement.setInt(3, entity.getPrice());
            preparedStatement.setInt(4, entity.getProductCode());
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    @Override
    public void update(@NotNull InvoiceItem entity) {
        try(PreparedStatement preparedStatement = getPrepareStatement(updateInvoiceItemQuery)) {
            int fieldIndex = 1;
            preparedStatement.setInt(fieldIndex++, entity.getQuantity());
            preparedStatement.setInt(fieldIndex++, entity.getPrice());
            preparedStatement.setInt(fieldIndex++, entity.getInvoiceId());
            preparedStatement.setInt(fieldIndex, entity.getProductCode());
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    @Override
    public void delete(@NotNull InvoiceItem entity) {
        try(PreparedStatement preparedStatement = deleteDyIdPreparedStatement()) {
            preparedStatement.setInt(1, entity.getInvoiceId());
            preparedStatement.setInt(2, entity.getProductCode());
            if (preparedStatement.executeUpdate() == 0) {
                throw new IllegalStateException("Record with invoice = " + entity.getInvoiceId() + " and product_code =" + entity.getProductCode()+ " not found");
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }
}
