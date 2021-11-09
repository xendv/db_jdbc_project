package db.jbdc.entities;

import lombok.Data;
import org.jetbrains.annotations.NotNull;

@Data
public class InvoiceItem {
    @NotNull
    int invoice_id;
    @NotNull
    int product_code;
    @NotNull
    int price;
    @NotNull
    int quantity;

    @Override
    public @NotNull String toString() {
        return "InvoiceItem{" +
                "invoice_id=" + invoice_id +
                ", product_code='" + product_code + '\'' +
                ", price='" + price + '\'' +
                ", quantity='" + quantity + '\'' +
                '}';
    }
}
