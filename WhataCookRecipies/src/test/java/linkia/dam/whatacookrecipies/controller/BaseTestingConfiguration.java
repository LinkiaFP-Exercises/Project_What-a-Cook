package linkia.dam.whatacookrecipies.controller;

import linkia.dam.whatacookrecipies.model.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import static linkia.dam.whatacookrecipies.utilities.ServiceUtil.isNotNullAndStartWithD;

@ExtendWith(SpringExtension.class)
public class BaseTestingConfiguration {

    protected String pathVariable, valuePathVariable;
    protected final String deleted = "deleted";
    protected int page, size, amount = 36;

    protected int getNumberLastElements() {
        return amount % size == 0 ? size : amount % size;
    }

    protected <T extends NamedEntity> T getExpectedDto(boolean desc, List<T> listToSort) {
        List<T> sortedList = new ArrayList<>(listToSort);

        sortedList.sort((a, b) -> desc ? b.getName().compareTo(a.getName()) : a.getName().compareTo(b.getName()));

        int startIndex = page * size;
        if (startIndex >= sortedList.size()) {
            throw new IndexOutOfBoundsException("Start index is out of bounds");
        }

        return sortedList.get(startIndex);
    }

}
