package db.jbdc.dao;

import db.jbdc.entities.Organization;
import org.jetbrains.annotations.NotNull;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class OrganizationDAO implements IDAO<Organization> {
    private final @NotNull
    Connection connection;

    public OrganizationDAO(@NotNull Connection connection) {
        this.connection = connection;
    }

    @NotNull
    @Override
    public Organization get(int id) {
        try (var statement = connection.createStatement()) {
            try (var resultSet = statement.executeQuery("SELECT itn, name, payment_account FROM organization WHERE itn = " + id)) {
                if (resultSet.next()) {
                    return new Organization(resultSet.getInt("itn"), resultSet.getString("name"), resultSet.getString("payment_account"));
                }
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        throw new IllegalStateException("Record with id " + id + "not found");
    }

    @NotNull
    @Override
    public List<Organization> all() {
        final List<Organization> result = new ArrayList<>();
        try (var statement = connection.createStatement()) {
            try (var resultSet = statement.executeQuery("SELECT * FROM organization")) {
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

    @Override
    public void save(@NotNull Organization entity) {
        try (PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO organization(itn,name,payment_account) VALUES(?,?,?)")) {
            preparedStatement.setInt(1, entity.getItn());
            preparedStatement.setString(2, entity.getName());
            preparedStatement.setString(3, entity.getPayment_account());
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    @Override
    public void update(@NotNull Organization entity) {
        try(PreparedStatement preparedStatement = connection.prepareStatement("UPDATE organization SET name = ?, payment_account = ? WHERE itn = ?")) {
            int fieldIndex = 1;
            preparedStatement.setString(fieldIndex++, entity.getName());
            preparedStatement.setString(fieldIndex++, entity.getPayment_account());
            preparedStatement.setInt(fieldIndex, entity.getItn());
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    @Override
    public void delete(@NotNull Organization entity) {
        try(PreparedStatement preparedStatement = connection.prepareStatement("DELETE FROM organization WHERE itn = ?")) {
            preparedStatement.setInt(1, entity.getItn());
            if (preparedStatement.executeUpdate() == 0) {
                throw new IllegalStateException("Record with id = " + entity.getItn() + " not found");
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }
}
