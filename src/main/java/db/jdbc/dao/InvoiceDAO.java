package db.jdbc.dao;

import db.jdbc.entities.Invoice;
import org.jetbrains.annotations.NotNull;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class InvoiceDAO extends DAO<Invoice>{

    final String saveInvoiceQuery = "INSERT INTO invoice(id, date, sender_org_itn) VALUES(?,?,?)";
    final String updateInvoiceQuery = "UPDATE invoice SET date = ?, sender_org_itn = ? WHERE id = ?";

    public InvoiceDAO(@NotNull Connection connection) {
        super(connection);
        tableName = "invoice";
        primaryKey = "id";
    }

    @NotNull
    @Override
    public Invoice get(int id) {
        try(PreparedStatement preparedStatement = getByIdPreparedStatement()) {
            preparedStatement.setInt(1, id);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    return new Invoice(resultSet.getInt("id"), resultSet.getDate("date"), resultSet.getInt("sender_org_itn"));
                }
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        throw new IllegalStateException("Record with id " + id + " not found");
    }

    @NotNull
    @Override
    public List<Invoice> all() {
        final List<Invoice> result = new ArrayList<>();
        try(PreparedStatement preparedStatement = getAllPreparedStatement()){
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    result.add(new Invoice(resultSet.getInt("id"), resultSet.getDate("date"), resultSet.getInt("sender_org_itn")));
                }
                return result;
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return result;
    }

    @Override
    public void save(@NotNull Invoice entity) {
        try(PreparedStatement preparedStatement = getPrepareStatement(saveInvoiceQuery)) {
            preparedStatement.setInt(1, entity.getId());
            preparedStatement.setDate(2, entity.getDate());
            preparedStatement.setInt(3, entity.getSenderOrgItn());
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    @Override
    public void update(@NotNull Invoice entity) {
        try(PreparedStatement preparedStatement = getPrepareStatement(updateInvoiceQuery)) {
            int fieldIndex = 1;
            preparedStatement.setDate(fieldIndex++, entity.getDate());
            preparedStatement.setInt(fieldIndex++, entity.getSenderOrgItn());
            preparedStatement.setInt(fieldIndex, entity.getId());
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    @Override
    public void delete(@NotNull Invoice entity) {
        try(PreparedStatement preparedStatement = deleteDyIdPreparedStatement()) {
            preparedStatement.setInt(1, entity.getId());
            if (preparedStatement.executeUpdate() == 0) {
                throw new IllegalStateException("Record with id = " + entity.getId() + " not found");
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }
}
