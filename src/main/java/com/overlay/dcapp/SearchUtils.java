/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.overlay.dcapp;

import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author 10938
 */
public class SearchUtils {

    //private ArrayList<String> fileList;
    //private ArrayList<String> resultList = new ArrayList<>();
    private String searchString;
    Map<String, LocalFile> localFileMap = new HashMap<>();
    Map<String, LocalFile> searchResMap = new HashMap<>();

    public SearchUtils(/*ArrayList fileList*/Map<String, LocalFile> localFileMap, String searchString) {
        //this.fileList = fileList;
        this.localFileMap = localFileMap;
        this.searchString = searchString;
    }

    public boolean searchFile(String searchQuery, String stringFileName) {
        boolean available = false;

        //StringTokenizer stSearch = new StringTokenizer(stringToSearch, " ");
        StringTokenizer stPattern = new StringTokenizer(searchQuery, " ");

        //System.out.println("stPattern.countTokens()==>" + stPattern.countTokens());
        int countTokens = stPattern.countTokens();
        if (stPattern.countTokens() == 1) {
            Pattern p = Pattern.compile(searchQuery.toLowerCase());   // the pattern to search for
            Matcher m = p.matcher(stringFileName.toLowerCase());

            // now try to find at least one match
            if (m.find()) {
                //System.out.println("Found a match");
                available = true;
            } else {
                //System.out.println("Did not find a match");
            }
        } else {
            while (countTokens > 0) {
                String stPatttern = stPattern.nextToken();
                //System.out.println("pattern==>" + stPatttern);
                Pattern p = Pattern.compile(stPatttern);   // the pattern to search for
                Matcher m = p.matcher(stringFileName.toLowerCase());
                //System.out.println("stringToSearch==>" + stringFileName);
                // now try to find at least one match
                if (m.find()) {
                    //System.out.println("Found a match");
                    available = true;
                } else {
                    //System.out.println("Did not find a match");
                }
                countTokens--;
            }
        }

        return available;
    }

    public Map<String, LocalFile> searchList() {
//        LocalUtils l = new LocalUtils();
//        Iterator it = fileList.iterator();
//        SearchUtils s = new SearchUtils(fileList, searchString);
//        while (it.hasNext()) {
//            String fileName = it.next().toString();
//            if (s.searchFile(searchString, fileName)) {
//                resultList.add(fileName);
//            }
//        }
        LocalUtils l = new LocalUtils();
        Map<String, LocalFile> map = localFileMap;

        map.entrySet().stream().forEach((entry) -> {
            SearchUtils s = new SearchUtils(localFileMap, searchString);
            String key = entry.getKey();
            LocalFile fileName = entry.getValue();
            if (s.searchFile(searchString, fileName.getName())) {
                searchResMap.put(key, fileName);
            }
        });
        if (!searchResMap.isEmpty()) {
            System.out.println("sysout> search success in local node..");
        } else {
            System.out.println("sysout> search failed in local node..");
        }
        return searchResMap;
    }

    public static void main(String[] args) {
//        System.out.println("Greetings Human");
//        ArrayList fileList = new ArrayList();
//        fileList.add("Lord of the Rings");
//        fileList.add("Lord of Buddha");
//        fileList.add("Tomb Raider");
//        SearchUtils s = new SearchUtils(fileList, "Raider");
//        System.out.println("s==>" + s.searchList());

        Map<String, LocalFile> map = new HashMap<>();

        LocalFile f1 = new LocalFile();
        f1.setName("Lord fo the rings");
        LocalFile f2 = new LocalFile();
        f2.setName("Lord of the game");
        map.put("1", f1);
        map.put("2", f2);
        SearchUtils s = new SearchUtils(map, "Lord");
        System.out.println("s==>" + s.searchList());
    }
}
