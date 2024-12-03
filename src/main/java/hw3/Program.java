package hw3;

import java.util.Scanner;

import static hw3.DBHandler.*;
import static hw3.FileHandler.loadPersonsFromFile;
import static hw3.FileHandler.savePersonsToFile;

public class Program {

    private static final String FILE_BIN = "persons.bin";

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        // getPersons()
        // loadPersonsFromFile()
        // if not equal, choose
        // if db, do nothing, exit condition will handle it
        // if from file, clear db and write file contents

        if (!getAllPersons().equals(loadPersonsFromFile(FILE_BIN))) {
            System.out.println("Local (1) and database data (2) do not match, which would you like to keep?");
            String keepChoice = scanner.nextLine();
            switch (keepChoice) {
                case "1":
                    deleteAllAndLoad(loadPersonsFromFile(FILE_BIN));
                    break;
                case "2":
                    savePersonsToFile(FILE_BIN, getAllPersons());
                    break;
                default:
                    System.out.println("Invalid input, cannot continue without syncing, now exiting.");
                    System.exit(1);
            }
        }

        while (true) {
            System.out.println("Current persons:");
            displayAllPersons();
            System.out.println("Enter action: ");
            System.out.println("1. Add new person");
            System.out.println("2. Change person full name");
            System.out.println("3. Delete person");
            System.out.println("4. Exit");

            String choice = scanner.nextLine();

            switch (choice) {
                case "1":
                    System.out.println("Enter the name of the person: ");
                    String addName = scanner.nextLine();
                    System.out.println("Enter the age of the person: ");
                    byte addAge = scanner.nextByte();
                    scanner.nextLine();
                    addPerson(addName, addAge);
                    break;
                case "2":
                    System.out.println("Enter the ID of the person: ");
                    int changeId = scanner.nextInt();
                    scanner.nextLine();
                    System.out.println("Enter the new name: ");
                    String newName = scanner.nextLine();
                    changeName(changeId, newName);
                    break;
                case "3":
                    System.out.println("Enter the ID of the person: ");
                    int delId = scanner.nextInt();
                    deletePerson(delId);
                    break;
                case "4":
                    scanner.close();
                    FileHandler.savePersonsToFile(FILE_BIN, getAllPersons());
                    System.exit(0);
                default:
                    System.out.println("Invalid operation, try again");
                    break;
            }
        }
    }
}
