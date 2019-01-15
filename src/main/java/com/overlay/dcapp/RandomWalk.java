/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.overlay.dcapp;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author 10938
 */
public class RandomWalk {

    private ArrayList<Node> neighbourList;
    //private int hop;
    private Node thisNode;

    public RandomWalk(ArrayList neighbourList, Node thisNode) {
        this.neighbourList = neighbourList;
        //this.hop = hop;
        this.thisNode = thisNode;
    }

    private Node getRandomNode() {
        Random rand = new Random();
        int n = rand.nextInt(2) + 0;
        return neighbourList.get(n);
    }

    public String randomWalk(SearchNodeInfoBean s) {
        String result = "";
        Node nextNode;

        if (!neighbourList.isEmpty()) {
            if (neighbourList.size() == 1) {
                nextNode = neighbourList.get(0);
            } else {
                nextNode = getRandomNode();
            }
            System.out.println("sysout> Next node ip:: " + nextNode.getIp() + " port:: " + nextNode.getPort());
            try {
                Message m = new Message(nextNode);
                System.out.println("sysout> Random walk initiated...");
                DatagramSocket ds = new DatagramSocket();
                byte[] buf = new byte[1000];
                DatagramPacket dp = new DatagramPacket(buf, buf.length);

                InetAddress hostAddress = InetAddress.getByName(nextNode.getIp());

                String outString = m.msgSearch(s);
                System.out.println("sysout>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
                System.out.println("sysout> msg out [" + outString+"]");
                System.out.println("sysout>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
                buf = outString.getBytes();
                DatagramPacket out = new DatagramPacket(buf, buf.length, hostAddress, nextNode.getPort());
                ds.send(out);
                ds.receive(dp);
                System.out.println("sysout><<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<");
                System.out.println("sysout> msg in [" + new String(dp.getData(), 0, dp.getLength())+"]");
                System.out.println("sysout><<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<");
                //String rcvd = "rcvd from " + dp.getAddress() + ", " + dp.getPort() + ": "
                //        + new String(dp.getData(), 0, dp.getLength());
                result = new String(dp.getData(), 0, dp.getLength());
                //System.out.println(rcvd);

            } catch (SocketException ex) {
                Logger.getLogger(RandomWalk.class.getName()).log(Level.SEVERE, null, ex);
            } catch (UnknownHostException ex) {
                Logger.getLogger(RandomWalk.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IOException ex) {
                Logger.getLogger(RandomWalk.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else {
            System.out.println("sysout> No neighbours to random walk...");
        }
        return result;
    }
}
