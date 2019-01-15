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

/**
 *
 * @author user
 */
public class OutboundComUtils {

    DatagramSocket s;
    byte[] buf = new byte[65536];
    DatagramPacket dp = new DatagramPacket(buf, buf.length);
    Message message = null;
    Node bsNode = null;

    public OutboundComUtils(Message message, Node bsNode) {
        this.message = message;
        this.bsNode = bsNode;
    }

    public String joinBs() throws SocketException, UnknownHostException, IOException {
        s = new DatagramSocket();
        byte[] buf = new byte[65536];
        DatagramPacket dp = new DatagramPacket(buf, buf.length);
        InetAddress hostAddress = InetAddress.getByName(bsNode.getIp());

        String outMessage = message.msgRegister();
        System.out.println("sysout>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
        System.out.println("sysout> msg out ==> [" + outMessage + "]");
        System.out.println("sysout>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
        buf = outMessage.getBytes();
        DatagramPacket out = new DatagramPacket(buf, buf.length, hostAddress, bsNode.getPort());
        s.send(out);
        s.receive(dp);

        String msgRcvd = " from ip::" + dp.getAddress() + "::port::" + dp.getPort() + "::msg::["
                + new String(dp.getData(), 0, dp.getLength());
        System.out.println("sysout<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<");
        System.out.println("sysout> msg in ==>[" + msgRcvd + "]");
        System.out.println("sysout<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<");
        return new String(dp.getData(), 0, dp.getLength());
    }

    public String leaveBs() throws SocketException, UnknownHostException, IOException {
        s = new DatagramSocket();
        byte[] buf = new byte[65536];
        DatagramPacket dp = new DatagramPacket(buf, buf.length);
        InetAddress hostAddress = InetAddress.getByName("localhost");

        String outMessage = message.msgUnregister();
        System.out.println("sysout>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
        System.out.println("sysout> msg out ==> [" + outMessage + "]");
        System.out.println("sysout>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
        buf = outMessage.getBytes();
        DatagramPacket out = new DatagramPacket(buf, buf.length, hostAddress, 55555);
        s.send(out);
        s.receive(dp);
        String msgRcvd = " from ip::" + dp.getAddress() + "::port::" + dp.getPort() + "::msg::["
                + new String(dp.getData(), 0, dp.getLength()) + "]";
        System.out.println("sysout<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<");
        System.out.println("sysout> msg in ==>[" + msgRcvd);
        System.out.println("sysout<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<");
        System.out.println(msgRcvd);

        return new String(dp.getData(), 0, dp.getLength());
    }

    public String commentOnFile(CommentComObj cco) throws SocketException, UnknownHostException, IOException {
        s = new DatagramSocket();
        byte[] buf = new byte[65536];
        DatagramPacket dp = new DatagramPacket(buf, buf.length);
        InetAddress hostAddress = InetAddress.getByName(cco.getFile().getLocatedNode().getIp());

        String outMessage = message.msgComment(cco);
        System.out.println("sysout>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
        System.out.println("sysout> msg in ==> [" + outMessage + "]");
        System.out.println("sysout>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
        buf = outMessage.getBytes();
        DatagramPacket out = new DatagramPacket(buf, buf.length, hostAddress, cco.getFile().getLocatedNode().getPort());
        s.send(out);
        s.receive(dp);
        String msgRcvd = " from ip::" + dp.getAddress() + "::port::" + dp.getPort() + "::msg::["
                + new String(dp.getData(), 0, dp.getLength()) + "]";
        System.out.println("sysout<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<");
        System.out.println("sysout> msg out ==> [" + msgRcvd);
        System.out.println("sysout<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<");
        //System.out.println(dRcvd);

        return new String(dp.getData(), 0, dp.getLength());
    }

    public String unRegister() throws SocketException, UnknownHostException, IOException {
        s = new DatagramSocket();
        byte[] buf = new byte[1000];
        DatagramPacket dp = new DatagramPacket(buf, buf.length);
        InetAddress hostAddress = InetAddress.getByName("localhost");

        String outMessage = message.msgUnregister();
        buf = outMessage.getBytes();
        System.out.println("sysout>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
        System.out.println("sysout> msg in ==> [" + outMessage + "]");
        System.out.println("sysout>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
        DatagramPacket out = new DatagramPacket(buf, buf.length, hostAddress, 55555);
        s.send(out);
        s.receive(dp);
        String msgRcvd = " from ip::" + dp.getAddress() + "::port::" + dp.getPort() + "::msg::["
                + new String(dp.getData(), 0, dp.getLength()) + "]";
        System.out.println("sysout<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<");
        System.out.println("sysout> msg out ==> [" + msgRcvd);
        System.out.println("sysout<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<");

        return new String(dp.getData(), 0, dp.getLength());
    }

    public String unRegisterFailedNode(Node n) throws SocketException, UnknownHostException, IOException {
        s = new DatagramSocket();
        byte[] buf = new byte[1000];
        DatagramPacket dp = new DatagramPacket(buf, buf.length);
        InetAddress hostAddress = InetAddress.getByName("localhost");

        String outMessage = message.msgUnregisterOnFailure(n);
        System.out.println("sysout>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
        System.out.println("sysout> msg in ==> [" + outMessage + "]");
        System.out.println("sysout>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
        buf = outMessage.getBytes();
        DatagramPacket out = new DatagramPacket(buf, buf.length, hostAddress, 55555);
        s.send(out);
        s.receive(dp);
        String msgRcvd = " from ip::" + dp.getAddress() + "::port::" + dp.getPort() + "::msg::["
                + new String(dp.getData(), 0, dp.getLength()) + "]";
        System.out.println("sysout<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<");
        System.out.println("sysout> msg out ==> [" + msgRcvd);
        System.out.println("sysout<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<");
        return new String(dp.getData(), 0, dp.getLength());
    }
}
