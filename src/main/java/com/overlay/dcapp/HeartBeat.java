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
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author 10938
 */
public class HeartBeat implements Runnable {

    ArrayList<Node> neighbourList;
    LocalUtils l = new LocalUtils();
    Node thisNode = l.getLocalIpPort();
    Node bsNode;

    public HeartBeat(ArrayList<Node> neighBourList, Node bsNode) {
        this.neighbourList = neighBourList;
        this.bsNode = bsNode;
    }

    public String echoMsg(Node n) throws SocketException, UnknownHostException, IOException {
        DatagramSocket s;
        byte[] buf = new byte[1000];
        DatagramPacket dp = new DatagramPacket(buf, buf.length);
        s = new DatagramSocket();
        s.setSoTimeout(1000);
        InetAddress hostAddress = InetAddress.getByName(n.getIp());

        String outMessage = "ECHO " + n.getIp() + " " + n.getPort();
        outMessage = outMessage.length() + " " + outMessage;
        buf = outMessage.getBytes();
        DatagramPacket out = new DatagramPacket(buf, buf.length, hostAddress, n.getPort());
        s.send(out);
        s.receive(dp);
        String msgRcvd = "echomsg> msg received>>>>> " + dp.getAddress() + ", " + dp.getPort() + ": "
                + new String(dp.getData(), 0, dp.getLength());
        System.out.println(msgRcvd);
        return new String(dp.getData(), 0, dp.getLength());
    }

    @Override
    public void run() {

        while (true) {
            if (!neighbourList.isEmpty()) {
                System.out.println(neighbourList.size() + " Neighbour available");
                for (int i = 0; i < neighbourList.size(); i++) {
                    try {
                        String rsp = echoMsg(neighbourList.get(i));
                        StringTokenizer st = new StringTokenizer(rsp, " ");
                        String length = st.nextToken();
                        String command = st.nextToken();
                        if (!command.equals("ECHOOK")) {
                            //initiate neighbour getting process
                            System.out.println("attn::issue in acessing neighbour node!!!");
                            Thread.sleep(1000);
                        } else {
                            System.out.println("ECHO to " + neighbourList.get(i).getPort() + " ok..");
                            Thread.sleep(1000);
                        }
                    } catch (Exception ex) {
                        try {
                            System.out.println("sysmsg > communication failure");
                            System.out.println("sysmsg > Initiating new neighbour request");
                            //neighbourList.clear();
                            /*unregiser from bs node*/
                            System.out.println("neighbourList.get(i) info ==>" + neighbourList.get(i).getIp() + neighbourList.get(i).getPort());
                            neighbourList.get(i);
                            OutboundComUtils out;
                            Message m = new Message(thisNode);
                            out = new OutboundComUtils(m, bsNode);
                            String resp = out.unRegisterFailedNode(neighbourList.get(i));
                            System.out.println("resp - unregister resp" + resp);
                            /**/
 /*clear local neighbour list*/
                            neighbourList.clear();
                            /**/
 /*remove this node before re-register*/
                            System.out.println("sysmsg > communication failure");
                            System.out.println("sysmsg > Initiating new neighbour request");
                            //neighbourList.clear();
                            /*unregiser from bs node*/
                            System.out.println("neighbourList.get(i) info ==>" + neighbourList.get(i).getIp() + neighbourList.get(i).getPort());
                            neighbourList.get(i);
                            OutboundComUtils out1;
                            out = new OutboundComUtils(m, bsNode);
                            resp = out.unRegister();
                            System.out.println("unregisyer this node " + resp);

                            /**/
                            String bsResponse = "";
                            String length = "";
                            String command = "";
                            System.out.println("sysout> Initiating connections to bootstrap server...");

                            try {
                                bsResponse = out.joinBs();
                            } catch (UnknownHostException ex1) {
                                Logger.getLogger(OverlayMain.class.getName()).log(Level.SEVERE, null, ex);
                            } catch (IOException ex2) {
                                Logger.getLogger(OverlayMain.class.getName()).log(Level.SEVERE, null, ex);
                            }
                            StringTokenizer st;
                            st = new StringTokenizer(bsResponse, " ");
                            length = st.nextToken();
                            command = st.nextToken();
                            int param3 = Integer.parseInt(st.nextToken());
                            if (command.equals("REGOK")) {
                                System.out.println("sysout> " + length);
                                System.out.println("sysout> " + command);
                                System.out.println("sysout> " + param3);

                                if (param3 == 9998) {
                                    System.out.println("sysout> Port, Username Combination Already Registered");
                                }
                                if (param3 == 9997) {
                                    System.out.println("sysout> Username Already Registered");
                                }
                                if (param3 == 1) {
                                    String ip1 = st.nextToken();
                                    String port1 = st.nextToken();
                                    neighbourList.add(new Node(ip1, Integer.parseInt(port1), ""));
                                }
                                if (param3 == 2) {
                                    String ip1 = st.nextToken();
                                    String port1 = st.nextToken();
                                    String ip2 = st.nextToken();
                                    String port2 = st.nextToken();
                                    neighbourList.add(new Node(ip1, Integer.parseInt(port1), ""));
                                    neighbourList.add(new Node(ip2, Integer.parseInt(port2), ""));
                                }
                                if (!neighbourList.isEmpty()) {
                                    neighbourList.forEach(System.out::println);
                                } else {
                                    System.out.println("sysout> This is the first node");
                                }
                            } else {
                                System.out.println("sysout> An error occurred..");
                            }
                        } catch (UnknownHostException ex1) {
                            Logger.getLogger(HeartBeat.class.getName()).log(Level.SEVERE, null, ex1);
                        } catch (IOException ex1) {
                            Logger.getLogger(HeartBeat.class.getName()).log(Level.SEVERE, null, ex1);
                        }
                    }
                }
            } else {
                System.out.println("No neighbours YET!");
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException ex) {
                    Logger.getLogger(com.overlay.dcapp.HeartBeat.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }
}
