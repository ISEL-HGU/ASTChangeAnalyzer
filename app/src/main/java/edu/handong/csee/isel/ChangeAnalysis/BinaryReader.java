package edu.handong.csee.isel.ChangeAnalysis;

import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class BinaryReader {
    private String combinePath;

    public BinaryReader(String combinePath) {
        this.combinePath = combinePath;
    }

    public void getHashMap() {
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
            if (!files.endsWith(".chg")) continue;
            try {
                FileInputStream fileIn = new FileInputStream(combinePath + "/" + files);
                ObjectInputStream objectIn = new ObjectInputStream(fileIn);

                ChangeAnalyzer binaryChangeAnalyzer = (ChangeAnalyzer)objectIn.readObject();

                HashMap<String, HashMap<String, ArrayList<String>>> coreMap = binaryChangeAnalyzer.getCoreMap();

                objectIn.close();
                fileIn.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
