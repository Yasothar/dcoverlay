/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.overlay.dcapp;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 *
 * @author user
 */
public class SearchNodeInfoBean {

    private String ip;
    private int port;
    private String userName;
    private String searchKey;
    private String searchResult;
    Map<String, LocalFile> searchResultMap = new ConcurrentHashMap<>();
    private int hops;
    private String locatedIp;
    private int locatedPort;

    public Map getMap() {
        return searchResultMap;
    }

    public void setMap(Map<String, LocalFile> searchResultMap) {
        this.searchResultMap = searchResultMap;
    }

    public String getLocatedIp() {
        return locatedIp;
    }

    public void setLocatedIp(String locatedIp) {
        this.locatedIp = locatedIp;
    }

    public int getLocatedPort() {
        return locatedPort;
    }

    public void setLocatedPort(int locatedPort) {
        this.locatedPort = locatedPort;
    }

    public int getHops() {
        return hops;
    }

    public void setHops(int hops) {
        this.hops = hops;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getSearchKey() {
        return searchKey;
    }

    public void setSearchKey(String searchKey) {
        this.searchKey = searchKey;
    }

    public String getSearchResult() {
        return searchResult;
    }

    public void setSearchResult(String searchResult) {
        this.searchResult = searchResult;
    }
}
