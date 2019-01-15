/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.overlay.dcapp;

/**
 *
 * @author 10938
 */
public class Comment {
    Node whichNode;
    String comment;
    Double rate;
    //Lamport TimeStamp for ordering of comments
    int lamportTs;

    public Node getWhichNode() {
        return whichNode;
    }

    public void setWhichNode(Node whichNode) {
        this.whichNode = whichNode;
    }

    public int getLamportTs() {
        return lamportTs;
    }

    public void setLamportTs(int lamportTs) {
        this.lamportTs = lamportTs;
    }
    
    public Node getNode() {
        return whichNode;
    }

    public void setNode(Node node) {
        this.whichNode = node;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public Double getRate() {
        return rate;
    }

    public void setRate(Double rate) {
        this.rate = rate;
    }
}
