/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ltm_giuaky;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 *
 * @author COMPUTER
 */
public class LTM_GiuaKy_Server {

    /**
     * @param args the command line arguments
     */
    public static boolean checkFile(String fname) {
        try {
            FileWriter fileWriter = new FileWriter(fname);
        } catch (IOException ex) {
            System.out.println("Error writing to file named '" + fname + "' ..!!");
            return false;
        }
        return true;
    }

    public static boolean checkReadFile(String fname) {
        try {

            FileReader fileReader = new FileReader(fname);
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            bufferedReader.close();
        } catch (IOException ex) {
            System.out.println("Error reading file named '" + fname + "'");
            return false;
        }
        //      System.out.println("aaa 1" + line);
        System.out.println("đọc đúng tên file mà!");
        return true;
    }

    public static String readFile(String fname) {

        String line;
        String result = "";
        try {

            FileReader fileReader = new FileReader(fname);
            BufferedReader bufferedReader = new BufferedReader(fileReader);

            while ((line = bufferedReader.readLine()) != null) {
                result += line + "\n";
            }
            bufferedReader.close();

        } catch (IOException ex) {
            System.out.println("Error reading file named '" + fname + "'");
        }
        //      System.out.println("aaa 1" + line);
        return result;
    }

    public static boolean writeFile(String fname, String text) {
        try {
            FileWriter fileWriter = new FileWriter(fname);
            BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
            bufferedWriter.write(text);
            bufferedWriter.close();
        } catch (IOException ex) {
            System.out.println("Error writing to file named '" + fname + "' ..!!");
            return false;
        }
        return true;
    }

    public static String decrypt(final String message, final String key) {
        StringBuilder result = new StringBuilder();

        for (int i = 0, j = 0; i < message.length(); i++) {

            char c = message.charAt(i);
            if (Character.isLetter(c)) {
                if (Character.isUpperCase(c)) {
                    result.append((char) ('Z' - (25 - (c - key.toUpperCase().charAt(j))) % 26));

                } else {
                    result.append((char) ('z' - (25 - (c - key.toLowerCase().charAt(j))) % 26));
                }
            } else {
                result.append(c);
            }

            j = ++j % key.length();
        }
        return result.toString();
    }

    public static String findCharacterAppearMost(String message) {
        Map<Character, Integer> numCharMap = new HashMap<Character, Integer>();
        for (int i = 0; i < message.length(); i++) {
            char c = message.charAt(i);
            if (c == ' ') {
                continue;
            }
            if (numCharMap.containsKey(c)) {
                numCharMap.put(c, numCharMap.get(c) + 1);
            } else {
                numCharMap.put(c, 1);
            }
        }
        int max = Collections.max(numCharMap.values());
        String characterAppearMost = null;
        System.out.println("max " + max);
        Set<Map.Entry<Character, Integer>> numSet = numCharMap.entrySet();
        for (Map.Entry<Character, Integer> m : numSet) {
            if (m.getValue() == max) {
                System.out.println("The character " + m.getKey() + " appear " + m.getValue());
                characterAppearMost = m.getKey() + " " + m.getValue();
            }
            //    System.out.println("The character " + m.getKey() + " appear " + m.getValue());
        }
        return characterAppearMost;
    }

    public static void main(String[] args) throws SocketException, IOException {
        // TODO code application logic here
        // tạo socket cho server kết nối
        DatagramSocket server = new DatagramSocket(8888);
        System.out.println("Server đang chạy!");
        //tạo mãng dữ liệu, tạo nhân gửi dữ liệu
        byte[] arr;
        DatagramPacket in;
        DatagramPacket out;
        while (true) {

            //Nhận check từ client
            int check;
            arr = new byte[256];
            in = new DatagramPacket(arr, arr.length);
            server.receive(in);
            check = Integer.parseInt(new String(in.getData(), 0, in.getLength()).trim());
            //Nhận đường dẫn file từ client
            String filePath;
            arr = new byte[256];
            in = new DatagramPacket(arr, arr.length);
            server.receive(in);
            filePath = new String(in.getData(), 0, in.getLength()).trim();

            //Nhận key từ client
            String key;
            arr = new byte[256];
            in = new DatagramPacket(arr, arr.length);
            server.receive(in);
            key = new String(in.getData(), 0, in.getLength()).trim();

            //Nhận văn bản từ client
            String text;
            arr = new byte[256];
            in = new DatagramPacket(arr, arr.length);
            server.receive(in);
            text = new String(in.getData(), 0, in.getLength()).trim();

            boolean checkWriteFile = false;
            if (check == 1) {
                checkWriteFile = writeFile(filePath, text);
                //Trả lỗi write file
                arr = new byte[256];
                arr = String.valueOf(checkWriteFile).getBytes();
                out = new DatagramPacket(arr, arr.length, in.getAddress(), in.getPort());
                server.send(out);
            } else {
                checkWriteFile = checkReadFile(filePath);
                //Trả lỗi write file
                arr = new byte[256];
                arr = String.valueOf(checkWriteFile).getBytes();
                out = new DatagramPacket(arr, arr.length, in.getAddress(), in.getPort());
                server.send(out);
            }

            if (checkWriteFile) {
                String readFile = readFile(filePath);
                System.out.println("readfile:" + readFile + "    filePath: " + filePath);
                String decryptText = decrypt(readFile, key);
                //gửi kết quả sau khi đọc lại file được giải mã 
                arr = new byte[256];
                arr = decryptText.getBytes();
                out = new DatagramPacket(arr, arr.length, in.getAddress(), in.getPort());
                server.send(out);

                //gửi kết quả tìm được từ xuất hiện nhiều nhất
                System.out.println(decryptText);
                if (decryptText.equals("")) {
                    arr = new byte[256];
                    arr = decryptText.getBytes();
                    out = new DatagramPacket(arr, arr.length, in.getAddress(), in.getPort());
                    server.send(out);
                } else {
                    String characterAppearMost = findCharacterAppearMost(decryptText);
                    arr = new byte[256];
                    arr = characterAppearMost.getBytes();
                    out = new DatagramPacket(arr, arr.length, in.getAddress(), in.getPort());
                    server.send(out);
                }
            }
        }
    }
}
