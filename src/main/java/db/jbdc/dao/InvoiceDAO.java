package db.jbdc.dao;

import db.jbdc.entities.Invoice;
import org.jetbrains.annotations.NotNull;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class InvoiceDAO implements IDAO<Invoice> {
    private final @NotNull
    Connection connection;

    public InvoiceDAO(@NotNull Connection connection) {
        this.connection = connection;
    }

    @NotNull
    @Override
    public Invoice get(int id) {
        try (var statement = connection.createStatement()) {
            try (var resultSet = statement.executeQuery("SELECT id, date, sender_org_itn FROM invoice WHERE id = " + id)) {
                if (resultSet.next()) {
                    return new Invoice(resultSet.getInt("id"), resultSet.getDate("date"), resultSet.getInt("sender_org_itn"));
                }
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        throw new IllegalStateException("Record with id " + id + "not found");
    }

    @NotNull
    @Override
    public List<Invoice> all() {
        final List<Invoice> result = new ArrayList<>();
        try (var statement = connection.createStatement()) {
            try (var resultSet = statement.executeQuery("SELECT * FROM invoice")) {
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
        try (PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO invoice(id, date, sender_org_itn) VALUES(?,?,?)")) {
            preparedStatement.setInt(1, entity.getId());
            preparedStatement.setDate(2, entity.getDate());
            preparedStatement.setInt(3, entity.getSender_org_itn());
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    @Override
    public void update(@NotNull Invoice entity) {
        try(PreparedStatement preparedStatement = connection.prepareStatement("UPDATE invoice SET date = ?, sender_org_itn = ? WHERE id = ?")) {
            int fieldIndex = 1;
            preparedStatement.setDate(fieldIndex++, entity.getDate());
            preparedStatement.setInt(fieldIndex++, entity.getSender_org_itn());
            preparedStatement.setInt(fieldIndex++, entity.getId());
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    @Override
    public void delete(@NotNull Invoice entity) {
        try(PreparedStatement preparedStatement = connection.prepareStatement("DELETE FROM invoice WHERE id = ?")) {
            preparedStatement.setInt(1, entity.getId());
            if (preparedStatement.executeUpdate() == 0) {
                throw new IllegalStateException("Record with id = " + entity.getId() + " not found");
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }
}
