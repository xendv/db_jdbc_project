package db.jbdc.entities;

import lombok.Data;
import org.jetbrains.annotations.NotNull;

@Data
public class Organization {
    @NotNull
    int itn;
    @NotNull
    String name;
    @NotNull
    String payment_account;

    @Override
    public @NotNull String toString() {
        return "Organisation{" +
                "itn=" + itn +
                ", name='" + name + '\'' +
                ", payment_account='" + payment_account + '\'' +
                '}';
    }
}
