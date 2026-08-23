import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

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

        Path path = Files.createTempFile("day34-nio-notes", ".txt");
        path.toFile().deleteOnExit();

        Files.writeString(path, "First nio line\nSecond nio line"); // one call

        List<String> lines = Files.readAllLines(path);
        System.out.println(lines);

        String content = Files.readString(path);
        System.out.println(content);

        System.out.println("exists = " + Files.exists(path));
        System.out.println("size = " + Files.size(path) + " bytes");
    }
}
