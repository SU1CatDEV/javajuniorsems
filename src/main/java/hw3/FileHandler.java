package hw3;

import com.fasterxml.jackson.databind.SerializationFeature;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class FileHandler {
    public static void savePersonsToFile(String fileName, List<Person> persons) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(fileName))) {
            oos.writeObject(persons);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static List<Person> loadPersonsFromFile(String fileName) {
        List<Person> tasks = new ArrayList<>();

        File file = new File(fileName);
        if (file.exists()) {
            try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
                tasks = (List<Person>) ois.readObject();
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }
        }

        return tasks;
    }
}
