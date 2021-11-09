package db.jbdc.entities;

import lombok.Data;
import org.jetbrains.annotations.NotNull;

@Data
public class Product {
    @NotNull
    int internal_code;
    @NotNull
    String name;

    @Override
    public @NotNull String toString() {
        return "Product{" +
                "internal_code=" + internal_code +
                ", name='" + name + '\'' +
                '}';
    }
}
