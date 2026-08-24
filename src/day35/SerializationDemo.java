import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

public class SerializationDemo {

    public static void main(String[] args) throws IOException, ClassNotFoundException {
        File file = File.createTempFile("day35-person", ".ser");
        file.deleteOnExit();

        Person original = new Person("Asha", 30);

        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(file))) {
            out.writeObject(original);
        }

        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(file))) {
            Person restored = (Person) in.readObject(); // requires a cast
            System.out.println(restored.name + ", " + restored.age);
            System.out.println("restored == original: " + (restored == original));
        }

        File sessionFile = File.createTempFile("day35-session", ".ser");
        sessionFile.deleteOnExit();

        Session session = new Session("asha", "secret-token-123");

        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(sessionFile))) {
            out.writeObject(session);
        }

        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(sessionFile))) {
            Session restoredSession = (Session) in.readObject();
            System.out.println("username = " + restoredSession.username);
            System.out.println("authToken = " + restoredSession.authToken); // null -- transient, never serialized
        }
    }
}

class Session implements Serializable {
    private static final long serialVersionUID = 1L;

    String username;
    transient String authToken; // never written to the serialized bytes

    Session(String username, String authToken) {
        this.username = username;
        this.authToken = authToken;
    }
}

class Person implements Serializable {
    private static final long serialVersionUID = 1L;

    String name;
    int age;

    Person(String name, int age) {
        this.name = name;
        this.age = age;
    }
}
