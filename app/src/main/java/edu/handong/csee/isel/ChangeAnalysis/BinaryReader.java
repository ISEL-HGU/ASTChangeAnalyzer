package edu.handong.csee.isel.ChangeAnalysis;

import java.io.File;
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
    private boolean isSingle = false;

    public BinaryReader(String combinePath) {

        this.combinePath = combinePath;
        if (combinePath.endsWith(".chg")) {
            isSingle = true;
        }
    }


    public void readSingleFile() {
        try {
            FileInputStream fis = new FileInputStream(new File(this.combinePath));
            ObjectInputStream ois = new ObjectInputStream(fis);

            ChangeInfo binaryChangeInfo = (ChangeInfo)ois.readObject();
            binaryChangeInfo.getHashMap();
        } catch (Exception e) {
            e.printStackTrace();
        }



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

                ChangeInfo binaryChangeInfo = (ChangeInfo)objectIn.readObject();

                HashMap<String, ArrayList<String>> hashMap = binaryChangeInfo.getHashMap();

                objectIn.close();
                fileIn.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
