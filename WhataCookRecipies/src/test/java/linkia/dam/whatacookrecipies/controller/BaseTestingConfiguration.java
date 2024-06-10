package linkia.dam.whatacookrecipies.controller;

import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
public class BaseTestingConfiguration {

    protected String pathVariable, valuePathVariable;
    protected final String deleted = "deleted";
    protected int amount;

}
