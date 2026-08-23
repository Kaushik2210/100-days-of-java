import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class FileIODemo {

    public static void main(String[] args) throws IOException {
        File file = File.createTempFile("day34-notes", ".txt");
        file.deleteOnExit(); // clean up after the JVM exits, since this is just a demo

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            writer.write("First line");
            writer.newLine();
            writer.write("Second line");
        } // writer.close() happens automatically, even if an exception is thrown

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
            }
        }
    }
}
