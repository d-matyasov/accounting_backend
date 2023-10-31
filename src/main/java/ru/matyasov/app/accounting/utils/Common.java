package ru.matyasov.app.accounting.utils;

import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Component
public class Common {

    public Common() {
    }

    public List<Integer> stringToIntegerList(String string) {

        List<Integer> resultList = Arrays.stream(string.split(",")).map(Integer::parseInt).toList();

        return resultList;

    }
}
