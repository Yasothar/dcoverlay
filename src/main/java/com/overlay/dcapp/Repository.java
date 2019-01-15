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
public class Repository {

    Map<String, LocalFile> searchResultMap = new ConcurrentHashMap();
    ArrayList commentResultList = new ArrayList();
    Map<String, Comment> commentMgrMap = new ConcurrentHashMap<>();
    Map<String, LocalFile> localFileMap = new ConcurrentHashMap<>();
    
}
