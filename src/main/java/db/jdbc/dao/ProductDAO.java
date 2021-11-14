package db.jdbc.dao;

import db.jdbc.entities.Product;
import org.jetbrains.annotations.NotNull;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ProductDAO extends DAO<Product>{

    final String saveProductQuery = "INSERT INTO product(internal_code,name) VALUES(?,?)";
    final String updateProductQuery = "UPDATE product SET name = ? WHERE internal_code = ?";

    public ProductDAO(@NotNull Connection connection) {
        super(connection);
        tableName = "product";
        primaryKey = "internal_code";
    }

    @NotNull
    @Override
    public Product get(int id) {
        try (PreparedStatement preparedStatement = getByIdPreparedStatement()){
            preparedStatement.setInt(1, id);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    return new Product(resultSet.getInt("internal_code"), resultSet.getString("name"));
                }
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        throw new IllegalStateException("Record with id " + id + " not found");
    }

    @NotNull
    @Override
    public List<Product> all() {
        final List<Product> result = new ArrayList<>();
        try(PreparedStatement preparedStatement = getAllPreparedStatement()) {
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    result.add(new Product(resultSet.getInt("internal_code"), resultSet.getString("name")));
                }
                return result;
            }
        }
        catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return result;
    }

    @Override
    public void save(@NotNull Product entity) {
        try(PreparedStatement preparedStatement = getPrepareStatement(saveProductQuery)){
            preparedStatement.setInt(1, entity.getInternalCode());
            preparedStatement.setString(2, entity.getName());
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    @Override
    public void update(@NotNull Product entity) {
        try(PreparedStatement preparedStatement = getPrepareStatement(updateProductQuery)) {
            int fieldIndex = 1;
            preparedStatement.setString(fieldIndex++, entity.getName());
            preparedStatement.setInt(fieldIndex, entity.getInternalCode());
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    @Override
    public void delete(@NotNull Product entity) {
        try(PreparedStatement preparedStatement = deleteDyIdPreparedStatement()) {
            preparedStatement.setInt(1, entity.getInternalCode());
            if (preparedStatement.executeUpdate() == 0) {
                throw new IllegalStateException("Record with id = " + entity.getInternalCode() + " not found");
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }
}
