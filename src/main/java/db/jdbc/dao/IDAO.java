package db.jdbc.dao;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public interface IDAO<T> {
    @NotNull
    T get(int id);

    @NotNull
    List<T> all();

    void save(@NotNull T entity);

    void update(@NotNull T entity);

    void delete(@NotNull T entity);
}
