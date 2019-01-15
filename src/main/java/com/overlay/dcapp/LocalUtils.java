/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.overlay.dcapp;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Base64;
import java.util.Properties;

/**
 *
 * @author 10938
 */
public class LocalUtils {

    public boolean validateInput(String i) {

        try {
            Integer.parseInt(i);
            return true;
        } catch (NumberFormatException nfe) {
        }
        return false;
    }

    public boolean isNumber(String amount) {
        try {
            Double.parseDouble(amount);
            return true;
        } catch (NumberFormatException e) {

        }
        return false;
    }

    public Node getLocalIpPort() {
        Properties prop = new Properties();
        InputStream input = null;
        Node localNode = null;
        try {
            input = new FileInputStream("src/main/resources/app.properties");
            // load a properties file
            prop.load(input);
            // get the property value and print it out
            String localIp = prop.getProperty("localIp");
            int localPort = Integer.parseInt(prop.getProperty("localPort"));
            String userName = prop.getProperty("userName");
            int ttl = Integer.parseInt(prop.getProperty("ttl"));

            System.out.print("sysout>Local System Details :: ip::" + localIp);
            System.out.print("\tport::" + localPort);
            System.out.println("\tuser::" + userName);
            //System.out.println(ttl);
            localNode = new Node(localIp, localPort, userName);

        } catch (IOException ex) {
            ex.printStackTrace();
        } finally {
            if (input != null) {
                try {
                    input.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return localNode;
    }

    public Node getBsIpPort() {
        Properties prop = new Properties();
        InputStream input = null;
        Node bsNode = null;
        try {
            input = new FileInputStream("src/main/resources/app.properties");
            // load a properties file
            prop.load(input);
            // get the property value and print it out
            String bsIp = prop.getProperty("bsIp");
            int bsPort = Integer.parseInt(prop.getProperty("bsPort"));

            System.out.println("");

            //System.out.println(ttl);
            bsNode = new Node(bsIp, bsPort, "bs");
            bsNode.toString();
        } catch (IOException ex) {
            ex.printStackTrace();
        } finally {
            if (input != null) {
                try {
                    input.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return bsNode;
    }

    public int getHops() {
        Properties prop = new Properties();
        InputStream input = null;
        int hops = 0;
        try {
            input = new FileInputStream("src/main/resources/app.properties");
            // load a properties file
            prop.load(input);

            // get the property value and print it out
            hops = Integer.parseInt(prop.getProperty("hops"));
        } catch (IOException ex) {
            ex.printStackTrace();
        } finally {
            if (input != null) {
                try {
                    input.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return hops;
    }

    //Mode should be "E" for Encode, "D" for decode
    public String encodeDecodeString(String mode, String input) {
        String result = "";
        if (mode.equals("E")) {
            result = Base64.getEncoder().encodeToString(input.getBytes());
        } else {
            byte[] decodedBytes = Base64.getDecoder().decode(input);
            result = new String(decodedBytes);
        }
        return result;
    }

    public static void main(String[] args) {
        LocalUtils l = new LocalUtils();
        //l.getLocalIpPort();
//        System.out.println("::" + l.encodeDecodeString("E", "Test"));
//        System.out.println("::" + l.encodeDecodeString("D", "VGVzdA=="));
        System.out.println("==>" + l.validateInput("1"));
        System.out.println("==>" + l.isNumber("100"));
    }
}
