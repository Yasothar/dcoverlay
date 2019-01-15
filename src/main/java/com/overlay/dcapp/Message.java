/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.overlay.dcapp;

import com.google.gson.Gson;

/**
 *
 * @author user
 */
public class Message {

    private Node node;

    public Message(Node node) {
        this.node = node;
    }

    public String msgRegister() {
        String msg = " REG " + node.getIp() + " " + node.getPort() + " " + node.getUsername();
        msg = String.format("%04d", msg.length()) + msg;
        return msg;
    }

    public String msgUnregister() {
        String msg = " UNREG " + node.getIp() + " " + node.getPort() + " " + node.getUsername();
        msg = String.format("%04d", msg.length()) + msg;
        return msg;
    }

    public String msgUnregisterOnFailure(Node n) {
        String msg = " UNREG " + n.getIp() + " " + " " + n.getPort() + " " + n.getUsername();
        msg = String.format("%04d", msg.length()) + msg;
        return msg;
    }

    public String msgJoin() {
        String msg = " JOIN " + node.getIp() + " " + node.getPort();
        msg = String.format("%04d", msg.length()) + msg;
        return msg;
    }

    public String msgLeave() {
        String msg = " LEAVE " + node.getIp() + " " + " " + node.getPort();
        msg = String.format("%04d", msg.length()) + msg;
        return msg;
    }

    public String msgSearch(SearchNodeInfoBean s) {
        Gson g = new Gson();
        LocalUtils l = new LocalUtils();
        String jsonStr = g.toJson(s);
        String msg = "SER" + " " + s.getIp() + " " + s.getPort() + " " + l.encodeDecodeString("E", jsonStr);
        msg = String.format("%04d", msg.length()) + " " + msg;
        return msg;
    }

    public String msgEcho() {
        String msg = "ECHO";
        msg = msg.length() + " " + msg;
        msg = String.format("%04d", msg.length()) + msg;
        return msg;
    }

    public String msgComment(CommentComObj cco) {
        Gson g = new Gson();
        LocalUtils l = new LocalUtils();
        String jsonStr = g.toJson(cco);
        String msg = "CMT" + " " + node.getIp() + " " + node.getPort() + " " + l.encodeDecodeString("E", jsonStr);
        msg = String.format("%04d", msg.length()) + " " + msg;
        return msg;
    }

    public static void main(String[] args) {
        System.out.println("Greetings Human!");
        Node n = new Node("127.0.0.1", 5000, "Yasothar");
        Message m = new Message(n);
//        System.out.println("msg==>" + m.msgRegister());
//        System.out.println("msg==>" + m.msgJoin());
//        System.out.println("msg==>" + m.msgLeave());
//        System.out.println("msg==>" + m.msgUnregister());

//        CommentBean c = new CommentBean();
//        c.setComment("This is awesome");
//        c.setFileId("Test 1");
//        c.setRate(1.5);
//        c.setToIp("127.0.0.1");
//        c.setToPort(1000);
//        System.out.println("msg==>" + m.msgComment(c));
    }
}
