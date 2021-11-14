package db.jdbc.entities;

import lombok.Data;
import org.jetbrains.annotations.NotNull;

@Data
public class InvoiceItem {
    int invoiceId;
    int productCode;
    int price;
    int quantity;

    public InvoiceItem (int invoiceId, int productCode, int price, int quantity){
        this.invoiceId = invoiceId;
        this.productCode = productCode;
        this.price = price;
        this.quantity = quantity;
    }

    @Override
    public @NotNull String toString() {
        return "InvoiceItem{" +
                "invoiceId=" + invoiceId +
                ", productCode='" + productCode + '\'' +
                ", price='" + price + '\'' +
                ", quantity='" + quantity + '\'' +
                '}';
    }
}
