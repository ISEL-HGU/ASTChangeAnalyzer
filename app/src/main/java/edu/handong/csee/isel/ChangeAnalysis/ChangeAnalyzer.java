package edu.handong.csee.isel.ChangeAnalysis;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class ChangeAnalyzer {
	// 이 아이는 나중에
    public void hunkAnalyzer() {

    }

    public void fileAnalyzer() {

    }

    public String computeSHA256Hash(String hashString) {
        MessageDigest md;
        try {
            md = MessageDigest.getInstance("SHA-256");
            md.update(hashString.getBytes());
            byte bytes[] = md.digest();
            StringBuffer sb = new StringBuffer();
            for(byte b : bytes){
                sb.append(Integer.toString((b&0xff) + 0x100, 16).substring(1));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return "";
    }
}
