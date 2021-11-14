package db.jdbc.entities;

import lombok.Data;
import org.jetbrains.annotations.NotNull;

@Data
public class Product {
    int internalCode;
    @NotNull
    String name;

    public Product(int internalCode, @NotNull String name){
        this.internalCode = internalCode;
        this.name = name;
    }

    @Override
    public @NotNull String toString() {
        return "Product{" +
                "internalCode=" + internalCode +
                ", name='" + name + '\'' +
                '}';
    }
}
