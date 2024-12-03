package hw3;

import jakarta.persistence.*;

import java.util.List;

public class DBHandler {
    private static final EntityManagerFactory ENTITY_MANAGER_FACTORY = Persistence.createEntityManagerFactory("hw3");

    public static void addPerson(String inName, byte inAge) {
        EntityManager em = ENTITY_MANAGER_FACTORY.createEntityManager();
        EntityTransaction et = null;
        try {
            et = em.getTransaction();
            et.begin();
            Person person = new Person();
            person.setFullName(inName);
            person.setAge(inAge);
            em.persist(person);
            et.commit();
        } catch (Exception ex) {
            if (et != null) {
                et.rollback();
            }
            ex.printStackTrace();
        } finally {
            em.close();
        }
    }

    public static List<Person> getAllPersons() {
        EntityManager em = ENTITY_MANAGER_FACTORY.createEntityManager();
        String stringQuery = "SELECT p FROM Person p WHERE p.id IS NOT NULL";

        TypedQuery<Person> tq = em.createQuery(stringQuery, Person.class);
        List<Person> persons = null;
        try {
            persons = tq.getResultList();
        } catch (NoResultException ex) {
            ex.printStackTrace();
        } finally {
            em.close();
        }

        return persons;
    }

    public static void displayAllPersons() {
        DBHandler.getAllPersons().forEach(person -> System.out.println(person.getId() + ": " +person.getFullName() + ", " + person.getAge()));
    }

    public static void changeName(int id, String inName) {
        EntityManager em = ENTITY_MANAGER_FACTORY.createEntityManager();
        EntityTransaction et = null;
        Person person = null;
        try {
            et = em.getTransaction();
            et.begin();
            person = em.find(Person.class, id);
            person.setFullName(inName);
            em.persist(person);
            et.commit();
        } catch (Exception ex) {
            if (et != null) {
                et.rollback();
            }
            ex.printStackTrace();
        } finally {
            em.close();
        }
    }

    public static void deletePerson(int id) {
        EntityManager em = ENTITY_MANAGER_FACTORY.createEntityManager();
        EntityTransaction et = null;
        Person person = null;
        try {
            et = em.getTransaction();
            et.begin();
            person = em.find(Person.class, id);
            em.remove(person);
            et.commit();
        } catch (Exception ex) {
            if (et != null) {
                et.rollback();
            }
            ex.printStackTrace();
        } finally {
            em.close();
        }
    }

    public static void deleteAllAndLoad(List<Person> persons) {
        EntityManager em = ENTITY_MANAGER_FACTORY.createEntityManager();
        EntityTransaction et = null;
        try {
            et = em.getTransaction();
            et.begin();
            Query deleteAllQuery = em.createQuery("DELETE FROM Person");
            deleteAllQuery.executeUpdate();
            persons.forEach(person -> DBHandler.addPerson(person.getFullName(), person.getAge()));
            et.commit();
        } catch (Exception ex) {
            if (et != null) {
                et.rollback();
            }
            ex.printStackTrace();
        } finally {
            em.close();
        }
    }
}
