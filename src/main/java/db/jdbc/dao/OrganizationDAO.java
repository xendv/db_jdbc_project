package db.jdbc.dao;

import db.jdbc.entities.Organization;
import org.jetbrains.annotations.NotNull;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class OrganizationDAO extends DAO<Organization>{

    final String saveOrganizationQuery = "INSERT INTO organization(itn,name,payment_account) VALUES(?,?,?)";
    final String updateOrganizationQuery = "UPDATE organization SET name = ?, payment_account = ? WHERE itn = ?";

    public OrganizationDAO(@NotNull Connection connection) {
        super(connection);
        tableName = "organization";
        primaryKey = "itn";
    }

    @NotNull
    @Override
    public Organization get(int id) {
        try (PreparedStatement preparedStatement = getByIdPreparedStatement()) {
            preparedStatement.setInt(1, id);
            try (var resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    return new Organization(resultSet.getInt("itn"), resultSet.getString("name"), resultSet.getString("payment_account"));
                }
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        throw new IllegalStateException("Record with id " + id + " not found");
    }

    @NotNull
    @Override
    public List<Organization> all() {
        final List<Organization> result = new ArrayList<>();
        try (PreparedStatement preparedStatement = getAllPreparedStatement()) {
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

    @Override
    public void save(@NotNull Organization entity) {
        try (PreparedStatement preparedStatement = getPrepareStatement(saveOrganizationQuery)) {
            preparedStatement.setInt(1, entity.getItn());
            preparedStatement.setString(2, entity.getName());
            preparedStatement.setString(3, entity.getPaymentAccount());
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    @Override
    public void update(@NotNull Organization entity) {
        try(PreparedStatement preparedStatement = getPrepareStatement(updateOrganizationQuery)) {
            int fieldIndex = 1;
            preparedStatement.setString(fieldIndex++, entity.getName());
            preparedStatement.setString(fieldIndex++, entity.getPaymentAccount());
            preparedStatement.setInt(fieldIndex, entity.getItn());
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    @Override
    public void delete(@NotNull Organization entity) {
        try(PreparedStatement preparedStatement = deleteDyIdPreparedStatement()) {
            preparedStatement.setInt(1, entity.getItn());
            if (preparedStatement.executeUpdate() == 0) {
                throw new IllegalStateException("Record with id = " + entity.getItn() + " not found");
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }
}
