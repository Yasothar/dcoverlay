/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.overlay.dcapp;

import com.google.gson.Gson;
import java.io.IOException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import java.util.Scanner;
import java.util.StringTokenizer;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author 10938
 */
public class OverlayMain implements Runnable {

    ArrayList<Node> neighbourList = new ArrayList<Node>();
    LocalUtils localUtils = new LocalUtils(); //utils methods for the app
    //ArrayList<String> fileList = new ArrayList<String>(); //list of uploaded files

    //Map<String, LocalFile> localFileMap = new ConcurrentHashMap<>();
    Map<String, ArrayList> commentMgrMap = new ConcurrentHashMap<>();
    //Map<String, String> searchResultMap = new ConcurrentHashMap();
//    public OverlayMain(ArrayList<Node> neighbourList, ArrayList<String> fileList){
//        
//    }

    Node thisNode;
    Node bsNode;
    Repository r = new Repository();
    LamportTS lTs;
    int hops;

    public Node getThisNode() {
        return thisNode;
    }

//    public OverlayMain(Map<String, String> searchResultMap){
//        this.searchResultMap = searchResultMap;
//    }
    public OverlayMain(Node n, Repository r, LamportTS l, Node bsNode, int hops) {
        this.thisNode = n;
        this.r = r;
        this.lTs = l;
        this.bsNode = bsNode;
        this.hops = hops;
    }

    @Override
    public void run() {

        OutboundComUtils out;
        StringTokenizer st = null;
        Scanner sc = new Scanner(System.in);
        Gson g = new Gson();
        while (true) {
            try {
                String userInput = readInputsViaCli(localUtils);
                switch (userInput) {
                    case "0":
                        System.out.println("sysout> Exiting system.");
                        System.exit(0);
                        break;
                    case "1":
                        String bsResponse = "";
                        String length = "";
                        String command = "";
                        System.out.println("sysout> Initiating connections to bootstrap server...");
                        Message m = new Message(thisNode);
                        out = new OutboundComUtils(m, bsNode);
                        try {
                            bsResponse = out.joinBs();
                        } catch (UnknownHostException ex) {
                            Logger.getLogger(OverlayMain.class.getName()).log(Level.SEVERE, null, ex);
                        } catch (IOException ex) {
                            Logger.getLogger(OverlayMain.class.getName()).log(Level.SEVERE, null, ex);
                        }
                        st = new StringTokenizer(bsResponse, " ");
                        length = st.nextToken();
                        command = st.nextToken();
                        int param3 = Integer.parseInt(st.nextToken());
                        if (command.equals("REGOK")) {
                            //System.out.println("sysout> " + length);
                            //System.out.println("sysout> " + command);
                            //System.out.println("sysout> " + param3);

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
                        break;
                    case "2":
                        String input = "y";
                        while (!input.equals("n")) {
                            System.out.println("sysout> Insert a file name...");
                            System.out.print("sysin > ");
                            sc = new Scanner(System.in);
                            String inputFileName = "";
                            inputFileName = sc.nextLine();
                            //fileList.add(inputFileName);
                            UUID uuid = UUID.randomUUID();
                            String tempUuid = uuid.toString();
                            LocalFile f = new LocalFile();
                            ArrayList<Comment> tempCmtLst = new ArrayList();
                            f.setName(inputFileName);
                            f.setLocatedNode(thisNode);
                            f.setUuid(tempUuid);
                            f.setComments(tempCmtLst);
                            f.setRateMap(new ConcurrentHashMap<String, Double>());
                            r.localFileMap.put(tempUuid, f);
                            System.out.println("sysout> '" + inputFileName + "' inserted...");
                            System.out.println("sysout> Add another file (y - yes, n - no)");
                            System.out.print("sysin > ");
                            input = sc.nextLine();
                        }
                        break;
                    case "3":
                        System.out.println("sysout> Listing all filenames...");
                        if (r.localFileMap.isEmpty()) {
                            System.out.println("sysout> No files found...");
                        } else {
                            Map<String, LocalFile> map = r.localFileMap;
                            for (Map.Entry<String, LocalFile> entry : map.entrySet()) {
                                String key = entry.getKey();
                                LocalFile f = entry.getValue();

                                ArrayList<Comment> tempCmtLst = f.getComments();
                                //System.out.println("tempCmtLst.size()==>" + tempCmtLst.size());

                                Double doubleSum = f.getRateMap().values().stream().mapToDouble(Double::doubleValue).sum();
                                //System.out.println("doubleSum==>" + doubleSum);
                                Double rateAvg = doubleSum / f.getRateMap().values().size();
                                if (rateAvg.isNaN()) {
                                    rateAvg = 0.0;
                                }
                                //System.out.println("avg==>" + doubleSum / f.getRateMap().values().size());
                                System.out.println("key - " + key + " - file - " + f.name + " - rating - " + rateAvg);
                                if (!tempCmtLst.isEmpty()) {
                                    System.out.println("\t%%%%%%%%%%%%%%%%%%%%comments-start%%%%%%%%%%%%%%%%%%%%%%%%%%");
                                    Iterator it = tempCmtLst.iterator();
                                    while (it.hasNext()) {
                                        Comment tempComment = (Comment) it.next();
                                        System.out.println("\t[" + tempComment.getNode().getIp() + "::" + tempComment.getNode().getPort() + "::" + tempComment.getNode().getUsername() + "] commented :: " + tempComment.getComment() + " \t\tlamportTS :: " + tempComment.getLamportTs());
                                    }
                                    System.out.println("\t%%%%%%%%%%%%%%%%%%%%comments-end%%%%%%%%%%%%%%%%%%%%%%%%%%%%");
                                }

                            }
                        }
                        break;
                    case "4":
                        String result = "";
                        System.out.println("sysout> Initiating file search...");
                        System.out.println("sysout> Enter filename to search...");
                        System.out.print("sysin > ");
                        sc = new Scanner(System.in);
                        String searchString = sc.nextLine();
                        if (!neighbourList.isEmpty()) {
                            SearchNodeInfoBean s = new SearchNodeInfoBean();
                            s.setIp(thisNode.getIp());
                            s.setPort(thisNode.getPort());
                            s.setSearchKey(searchString);
                            s.setUserName(thisNode.getUsername());
                            s.setHops(hops);
                            g = new Gson();
                            String jsonMsg = g.toJson(s);
                            System.out.println("sysout> " + jsonMsg);
                            //System.out.println("sysout> Encoded initiator details- " + localUtils.encodeDecodeString("E", jsonMsg));
                            RandomWalk r = new RandomWalk(neighbourList, thisNode);
                            result = r.randomWalk(s);
                            //System.out.println("class::OverlayMain->result - " + result);
//                            if (!result.equals("")) {
//                                st = new StringTokenizer(result, " ");
//                                length = st.nextToken();
//                                command = st.nextToken();
//                                String noOfFiles = st.nextToken();
//                                String ip = st.nextToken();
//                                String port = st.nextToken();
//                                String fileList = st.nextToken();
//                                LocalUtils l = new LocalUtils();
//                                System.out.println("sysout> Files found ::" + l.encodeDecodeString("D", fileList));
//
//                                int iNoOfFiles = Integer.parseInt(noOfFiles);
//                                System.out.println("class::OverlayMain->iNoOfFiles - " + iNoOfFiles);
//
//
//                                if (command.equals("SERFAIL")) {
//                                    System.out.println("inside -- if (command.equals(\"SERFAIL\"))");
//                                    System.out.println("sysout> Unable to locate any files");
//                                }
//                            } else {
//                                System.out.println("sysout> Unable to locate any files");
//                            }
                        }else{
                            System.out.println("No neighbours to search...");
                        }
                        break;
                    case "5":
                        System.out.println("Comment on a File");
                        System.out.println("######################");
                        System.out.println("Listing searched files");

                        Map<String, LocalFile> map = r.searchResultMap;
                        //System.out.println("searchResultMap " + map.size());

                        if (map.size() > 0) {
                            int i = 0;
                            for (Map.Entry<String, LocalFile> entry : map.entrySet()) {
                                String key = entry.getKey();
                                LocalFile fileName = entry.getValue();

                                Map<String, Double> tempMap = fileName.getRateMap();
                                for (Map.Entry<String, Double> entry1 : tempMap.entrySet()) {
                                    System.out.println(entry1.getKey() + " : " + entry1.getValue());
                                }
                                Double doubleSum = tempMap.values().stream().mapToDouble(Double::doubleValue).sum();
                                //System.out.println("doubleSum==>" + doubleSum);
                                Double rateAvg = doubleSum / tempMap.values().size();
                                //System.out.println("avg==>" + doubleSum / tempMap.values().size());
                                //System.out.println(i++ + "-" + key + " " + fileName.getName() + " rating  - " + rateAvg + " *");
                                if (rateAvg.isNaN()) {
                                    rateAvg = 0.0;
                                }
                                System.out.println(i++ + " - " + "key - " + key + " - file - " + fileName.getName() + " - rating - " + rateAvg);
                            }
                        } else {
                            System.out.println("sysout> You should search to file before comment/rate");
                            break;
                        }

                        System.out.println("######################");

                        String comment = "";
                        String strFileId = "";
                        String strRate = "";
                        int fileId;
                        Double rating = 0.0;

                        System.out.println("sysout> Select a file name id");
                        System.out.print("sysin > ");
                        sc = new Scanner(System.in);
                        strFileId = sc.nextLine();

                        System.out.println("sysout> Add a comment");
                        System.out.print("sysin > ");
                        sc = new Scanner(System.in);
                        comment = sc.nextLine();

                        System.out.println("sysout> Add a rating (1-5)");
                        System.out.print("sysin > ");
                        sc = new Scanner(System.in);
                        strRate = sc.nextLine();
                        rating = Double.parseDouble(strRate);
                        //System.out.println("==>" + localUtils.validateInput(strFileId));
                        //System.out.println("==>" + localUtils.isNumber(strRate));

                        if (localUtils.isNumber(strRate)) {
                            LocalFile tmpFile = map.get(strFileId);

                            Double tempDoubleRate = Double.parseDouble(strRate);
                            if (tempDoubleRate < 0 || tempDoubleRate > 5) {
                                System.out.println("sysout> Rating should be between 1 to 5");
                                break;
                            } else if (tmpFile != null) {
                                System.out.println("sysout> File id OK, Proceeding operation..");
                                Comment c = new Comment();
                                CommentComObj co = new CommentComObj();

                                c.setComment(comment);
                                c.setNode(thisNode);
                                c.setRate(rating);
                                c.setLamportTs(lTs.sendTs());

                                co.setComment(c);
                                co.setRate((rating));
                                String tempInt = String.valueOf(thisNode.getPort());
                                co.setRateHash("" + (thisNode.getIp() + tempInt + thisNode.getUsername()).hashCode());
                                co.setFile(tmpFile);
                                co.setFromNode(thisNode);
                                m = new Message(thisNode);
                                out = new OutboundComUtils(m, bsNode);
                                try {
                                    out.commentOnFile(co);
                                } catch (UnknownHostException ex) {
                                    Logger.getLogger(OverlayMain.class.getName()).log(Level.SEVERE, null, ex);
                                } catch (IOException ex) {
                                    Logger.getLogger(OverlayMain.class.getName()).log(Level.SEVERE, null, ex);
                                }
                            } else {
                                System.out.println("sysout> Invalid file ID");
                            }
                        } else {
                            System.out.println("sysout> Input Error, Please Try Again");
                        }
                        break;
                    case "6":
                        System.out.println("sysout> Listing neighbours...");
                        if (neighbourList.isEmpty()) {
                            System.out.println("sysout> Neighbour list empty...");
                        } else {
                            neighbourList.forEach(System.out::println);
                        }
                        break;
                    case "7":
                        System.out.println("sysout> Functionality yet not available..");
                        break;
                    default:
                        System.out.println("sysout> Invalid user input..");
                }
                Thread.sleep(1000);
            } catch (InterruptedException ex) {
                Logger.getLogger(OverlayMain.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    public String readInputsViaCli(LocalUtils l) {
        String result = "";
        System.out.println("###############################");
        System.out.println("1 - Connect to bootstrap server");
        System.out.println("2 - Upload a file");
        System.out.println("3 - List available files");
        System.out.println("4 - Search a file");
        System.out.println("5 - Comment/rate a file");
        System.out.println("6 - List neigbours");
        System.out.println("0 - Quit");
        System.out.println("Please a make a selection..");
        System.out.println("###############################");
        System.out.print("sysin > ");
        Scanner sc = new Scanner(System.in);
        String input = sc.nextLine();
        if (l.validateInput(input)) {
            result = input;
        } else {
            result = "ERR_INPUT";
        }
        return result;
    }
}
