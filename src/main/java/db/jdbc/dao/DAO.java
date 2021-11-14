package db.jdbc.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public abstract class DAO<E> implements IDAO<E>{
        protected String selectAllTemplate = "SELECT * FROM ";
        protected String deleteTemplate = "DELETE FROM ";
        protected String sql;

        private final Connection connection;

        public DAO(Connection connection){
                this.connection = connection;
        }

        public String tableName;
        public String primaryKey;

        public PreparedStatement getAllPreparedStatement() {
                sql = selectAllTemplate + tableName;
                return getPrepareStatement(sql);
        }

        public PreparedStatement getByIdPreparedStatement() {
                sql = selectAllTemplate + tableName + " WHERE " + primaryKey + " = ?";
                return getPrepareStatement(sql);
        }

        public PreparedStatement deleteDyIdPreparedStatement() {
                sql = deleteTemplate + tableName + " WHERE " + primaryKey + " = ?";
                return getPrepareStatement(sql);
        }

        // Получение экземпляра PrepareStatement
        public PreparedStatement getPrepareStatement(String sql) {
                PreparedStatement ps = null;
                try {
                        ps = connection.prepareStatement(sql);
                } catch (SQLException e) {
                        e.printStackTrace();
                }
                return ps;
        }
}
