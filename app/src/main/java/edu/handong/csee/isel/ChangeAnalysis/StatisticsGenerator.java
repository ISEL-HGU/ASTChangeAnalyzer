package edu.handong.csee.isel.ChangeAnalysis;

import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.Set;

public class StatisticsGenerator {
    private String combinePath;

    public StatisticsGenerator(String combinePath) {
        this.combinePath = combinePath;
    }

    public void combine() {
        Set<String> fileList = new HashSet<>();
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(Paths.get(combinePath))) {
            for (Path path : stream) {
                if (!Files.isDirectory(path)) {
                    fileList.add(path.getFileName()
                            .toString());
                }
            }
        } catch (Exception e) { e.printStackTrace(); }
        for (String files : fileList) {
            try {
                FileInputStream fileIn = new FileInputStream(files);
                ObjectInputStream objectIn = new ObjectInputStream(fileIn);

                Object obj = objectIn.readObject();

                System.out.println("The Object has been read from the file");
                objectIn.close();

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
