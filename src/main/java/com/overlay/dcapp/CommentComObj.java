/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.overlay.dcapp;


/**
 *
 * @author user
 */
public class CommentComObj {
    private Node fromNode;
    private Node toNode;
    private LocalFile file;
    private Comment comment;
    private String rateHash;
    private Double rate;

    public String getRateHash() {
        return rateHash;
    }

    public void setRateHash(String rateHash) {
        this.rateHash = rateHash;
    }

    public Double getRate() {
        return rate;
    }

    public void setRate(Double rate) {
        this.rate = rate;
    }
    
    

    public Node getFromNode() {
        return fromNode;
    }

    public void setFromNode(Node fromNode) {
        this.fromNode = fromNode;
    }

    public Node getToNode() {
        return toNode;
    }

    public void setToNode(Node toNode) {
        this.toNode = toNode;
    }

    public LocalFile getFile() {
        return file;
    }

    public void setFile(LocalFile file) {
        this.file = file;
    }

    public Comment getComment() {
        return comment;
    }

    public void setComment(Comment comment) {
        this.comment = comment;
    }
    
    
}
