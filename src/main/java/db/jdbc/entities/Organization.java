package db.jdbc.entities;

import lombok.Data;
import org.jetbrains.annotations.NotNull;

@Data
public class Organization {
    int itn;
    @NotNull
    String name;
    @NotNull
    String paymentAccount;

    public Organization(int itn, @NotNull String name, @NotNull String paymentAccount){
        this.itn = itn;
        this.name = name;
        this.paymentAccount = paymentAccount;
    }

    @Override
    public @NotNull String toString() {
        return "Organisation{" +
                "itn=" + itn +
                ", name='" + name + '\'' +
                ", paymentAccount='" + paymentAccount + '\'' +
                '}';
    }
}
