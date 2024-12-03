package hw2;

import java.lang.reflect.Method;

public class Main {
    public static void main(String[] args) throws ClassNotFoundException {
        Class<?> stringClass = Class.forName("java.lang.String");

        Method[] methods = stringClass.getMethods();

        for (Method method : methods) {
            System.out.println("Метод: " + method.getName());
        }
    }
}
