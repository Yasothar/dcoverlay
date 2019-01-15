/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.overlay.dcapp;

import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 *
 * @author user
 */
public class LocalFile {
    int id;
    String uuid;
    String name;
    Node locatedNode;
    ArrayList<Comment> comments;
    Map<String, Double> rateMap = new ConcurrentHashMap<String, Double>();

    public Map<String, Double> getRateMap() {
        return rateMap;
    }

    public void setRateMap(Map<String, Double> rateMap) {
        this.rateMap = rateMap;
    }
    
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Node getLocatedNode() {
        return locatedNode;
    }

    public void setLocatedNode(Node locatedNode) {
        this.locatedNode = locatedNode;
    }

    public ArrayList<Comment> getComments() {
        return comments;
    }

    public void setComments(ArrayList<Comment> comments) {
        this.comments = comments;
    }
    
    
}
