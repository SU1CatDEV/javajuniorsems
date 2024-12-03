package hw1;

import java.util.Arrays;
import java.util.List;

public class ArrayTask {

    public static void main(String[] args) {
        List<Integer> numbers = Arrays.asList(6, 8, 4, 3, 7, 2);

        double average = numbers.stream()
                .filter(num -> num % 2 == 0)
                .mapToInt(Integer::intValue)
                .average()
                .orElse(0);

        System.out.println(average);
    }

}
