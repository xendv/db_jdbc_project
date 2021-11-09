package db.jbdc.entities;

import lombok.Data;
import org.jetbrains.annotations.NotNull;

import java.sql.Date;
import java.util.ArrayList;

@Data
public class Invoice {
    @NotNull
    int id;
    @NotNull
    Date date;
    @NotNull
    int sender_org_itn;

    @Override
    public @NotNull String toString() {
        return "Invoice{" +
                "id=" + id +
                ", date='" + date + '\'' +
                ", sender_org_itn='" + sender_org_itn + '\'' +
                '}';
    }
}
