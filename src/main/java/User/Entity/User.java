package User.Entity;

import jakarta.persistence.Entity;
import lombok.Data;

@Data
@Entity
public class User {

    private String Username;

    private int age;

    private char gender;

    private String area;
}
