package db.jdbc.entities;

import lombok.Data;
import org.jetbrains.annotations.NotNull;

import java.sql.Date;

@Data
public class Invoice {
    int id;
    @NotNull
    Date date;
    int senderOrgItn;

    public Invoice (int id, @NotNull Date date, int senderOrgItn){
        this.id = id;
        this.date = date;
        this.senderOrgItn = senderOrgItn;
    }

    @Override
    public @NotNull String toString() {
        return "Invoice{" +
                "id=" + id +
                ", date='" + date + '\'' +
                ", senderOrgItn='" + senderOrgItn + '\'' +
                '}';
    }
}
