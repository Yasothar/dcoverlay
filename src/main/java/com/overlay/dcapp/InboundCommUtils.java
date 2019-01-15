package com.overlay.dcapp;

import com.google.gson.Gson;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;

public class InboundCommUtils implements Runnable {

    private Node thisNode;
    private ArrayList<Node> neibhourList;
    //private ArrayList<String> fileList;
    //Map<String, LocalFile> localFileMap = new ConcurrentHashMap<>();
    //Map<String, String> searchResultMap = new ConcurrentHashMap();
    Repository r = new Repository();
    LamportTS lTs;

    //Map<String, ArrayList> commentMgrMap = new ConcurrentHashMap<>();
    public InboundCommUtils(Node thisNode/*, Map<String, LocalFile> localFileMapArrayList fileList*/,
            ArrayList<Node> neighbourList, Repository r, LamportTS l
    ) {
        this.thisNode = thisNode;
        //this.fileList = fileList;
        //this.localFileMap = localFileMap;
        this.neibhourList = neighbourList;
        /*this.searchResultMap = searchResultMap;*/
        this.r = r;
        this.lTs = l;
    }

    public void listenPort() {

        try {
            Gson g = new Gson();
            LocalUtils l = new LocalUtils();
            SearchNodeInfoBean s = new SearchNodeInfoBean();
            int port = thisNode.getPort();
            byte[] inBuffer = new byte[65536];
            byte[] outBuffer = new byte[65536];
            DatagramSocket sk;
            sk = new DatagramSocket(port);

            System.out.println("sysout> port:: " + port + " listening for messages..");
            while (true) {
                String outMessage = "";
                DatagramPacket incoming = new DatagramPacket(inBuffer, inBuffer.length);
                sk.receive(incoming);

//                String rcvd = "recieved data " + new String(incoming.getData(), 0, incoming.getLength()) + ", from ip:: "
//                        + incoming.getAddress() + ", port:: " + incoming.getPort();
                String msgRcvd = " from ip::" + incoming.getAddress() + "::port::" + incoming.getPort() + "::msg::["
                        + new String(incoming.getData(), 0, incoming.getLength()) + "]";

                //System.out.println("sysout> " + rcvd);
                System.out.println("sysout<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<");
                System.out.println("sysout> msg in ==> [" + msgRcvd + "]");
                System.out.println("sysout<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<");

                String incomingStr = new String(incoming.getData(), 0, incoming.getLength());
                //System.out.println("debug::incomingStr==>" + incomingStr);

                StringTokenizer st = new StringTokenizer(incomingStr, " ");
                String length = st.nextToken();
                String command = st.nextToken();
                //System.out.println("length==>" + length);
                //System.out.println("command==>" + command);

                if (command.equals("SER")) {
                    String incomeIp = st.nextToken();
                    String incomePort = st.nextToken();
                    String encData = st.nextToken();
                    //System.out.println("encData==>" + encData);
                    String decData = l.encodeDecodeString("D", encData);
                    //System.out.println("decData==>" + decData);
                    s = g.fromJson(decData, SearchNodeInfoBean.class);

                    SearchUtils searchUtils = new SearchUtils(r.localFileMap, s.getSearchKey());
                    r.searchResultMap = searchUtils.searchList();
                    String fileStringForMsg = "";
                    if (!r.searchResultMap.isEmpty()) {
//                        Iterator it = searchResultList.iterator();
//                        while (it.hasNext()) {
//                            fileStringForMsg = fileStringForMsg + " " + it.next().toString();
//                        }

                        s.setLocatedIp(thisNode.getIp());
                        s.setLocatedPort(thisNode.getPort());
                        s.setSearchResult(fileStringForMsg);
                        s.setMap(r.searchResultMap);
                        String resJson = g.toJson(s);

                        outMessage = "SEROK " + r.searchResultMap.size() + " " + thisNode.getIp() + " " + thisNode.getPort() + " " + l.encodeDecodeString("E", resJson);
                        outMessage = outMessage.length() + " " + outMessage;
                        outBuffer = outMessage.getBytes();
                        System.out.println("sysout>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
                        System.out.println("sysout> msg out ==> [" + outMessage + "]");
                        System.out.println("sysout>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");

                        InetAddress hostAddress = InetAddress.getByName(s.getIp());
                        DatagramPacket out = new DatagramPacket(outBuffer, outBuffer.length, hostAddress, s.getPort());
                        sk.send(out);

//                        System.out.println("incoming.getAddress().getHostAddress()" + incoming.getAddress().getHostAddress());
//                        System.out.println("s.getIp()" + s.getIp());
//                        System.out.println("incoming.getAddress().getHostAddress() .equals(s.getIp())" + incoming.getAddress().getHostAddress().equals(s.getIp()));
                        outMessage = "ACK " + r.searchResultMap.size() + " " + thisNode.getIp() + " " + thisNode.getPort() + " " + l.encodeDecodeString("E", resJson);
                        outMessage = outMessage.length() + " " + outMessage;
                        outBuffer = outMessage.getBytes();
                        hostAddress = incoming.getAddress();
                        System.out.println("sysout>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
                        System.out.println("sysout> msg out ==> [" + outMessage + "]");
                        System.out.println("sysout>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
                        out = new DatagramPacket(outBuffer, outBuffer.length, hostAddress, incoming.getPort());
                        sk.send(out);

                    } else {
                        System.out.println("sysout> File not found - initiating random walk");
                        //respond to both initiating neighbour and the actual search outMessage = "SERFAIL" + " " + thisNode.getIp() + " " + thisNode.getPort();
                        outMessage = "ACK" + " " + thisNode.getIp() + " " + thisNode.getPort();
                        outMessage = outMessage.length() + " " + outMessage;
                        outBuffer = outMessage.getBytes();

                        InetAddress hostAddress = incoming.getAddress();
                        System.out.println("sysout>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
                        System.out.println("sysout> msg out ==> [" + outMessage + "]");
                        System.out.println("sysout>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
                        DatagramPacket out = new DatagramPacket(outBuffer, outBuffer.length, hostAddress, incoming.getPort());
                        sk.send(out);
                        RandomWalk r = new RandomWalk(neibhourList, thisNode);

                        int h = s.getHops();
                        System.out.println("Hops - " + h);
                        if (h != 0) {
                            s.setHops(--h);
                            String result = r.randomWalk(s);
                            //System.out.println("result==>" + result);
//                            if (!neibhourList.isEmpty()) {
//                                String result = r.randomWalk(s);
//                                if (!result.equals("")) {
//                                    st = new StringTokenizer(result, " ");
//                                    length = st.nextToken();
//                                    command = st.nextToken();
//                                    String noOfFiles = st.nextToken();
//                                    String frmIp = st.nextToken();
//                                    String frmPort = st.nextToken();
//                                    String fileList = st.nextToken();
//                                    System.out.println("sysout> Files found ::" + l.encodeDecodeString("D", fileList));
//
//                                    outMessage = "SEROK " + searchResultList.size() + " " + thisNode.getIp() + " " + thisNode.getPort() + " " + l.encodeDecodeString("E", fileStringForMsg);
//                                    outMessage = outMessage.length() + " " + outMessage;
//                                    InetAddress hostAddress = InetAddress.getByName(s.getIp());
//                                    buffer = outMessage.getBytes();
//                                    DatagramPacket out = new DatagramPacket(buffer, buffer.length, hostAddress, s.getPort());
//                                    sk.send(out);
//
//                                } else {
//                                    System.out.println("sysout> Unable to locate any files");
//                                }
//                            } else {
//                                //respond to both initiating neighbour and the actual search 
//                                outMessage = "SERFAIL" + " " + thisNode.getIp() + " " + thisNode.getPort();
//                                outMessage = outMessage.length() + " " + outMessage;
//                                outBuffer = outMessage.getBytes();
//
//                                InetAddress hostAddress = InetAddress.getByName(s.getIp());
//                                DatagramPacket out = new DatagramPacket(outBuffer, outBuffer.length, hostAddress, s.getPort());
//                                sk.send(out);
//
////                                outMessage = "SERFAIL " + thisNode.getIp() + " " + thisNode.getPort();
////                                outMessage = outMessage.length() + " " + outMessage;
////                                outBuffer = outMessage.getBytes();
////
////                                hostAddress = incoming.getAddress();
////                                out = new DatagramPacket(outBuffer, outBuffer.length, hostAddress, incoming.getPort());
////                                sk.send(out);
//                            }

                        } else {
                            System.out.println("sysout> Hops exhausted, terminating search");
                            outMessage = "SERFAILHOPEND " + " " + thisNode.getIp() + " " + thisNode.getPort();
                            outMessage = outMessage.length() + " " + outMessage;
                            outBuffer = outMessage.getBytes();
                            System.out.println("sysout>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
                            System.out.println("sysout> msg out ==> [" + outMessage + "]");
                            System.out.println("sysout>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");

                            hostAddress = InetAddress.getByName(s.getIp());
                            out = new DatagramPacket(outBuffer, outBuffer.length, hostAddress, s.getPort());
                            sk.send(out);
                        }
                    }
                } else if (command.equals("SEROK")) {
                    String noOfFiles = st.nextToken();
                    String incomeIp = st.nextToken();
                    String incomePort = st.nextToken();
                    String encData = st.nextToken();
                    //System.out.println("encData==>" + encData);
                    String decData = l.encodeDecodeString("D", encData);
                    //System.out.println("decData==>" + decData);
                    s = g.fromJson(decData, SearchNodeInfoBean.class);
                    //System.out.println("==>" + noOfFiles + "/" + incomeIp + "/" + incomePort + "/" + encData + "/" + decData);

                    SearchNodeInfoBean sRes = g.fromJson(decData, SearchNodeInfoBean.class);

                    //System.out.println("File found in ip::" + sRes.getIp() + "List of Files" + decData);
                    //System.out.println("====>" + sRes.getSearchResult());
                    //System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
                    r.searchResultMap = sRes.getMap();

                    //System.out.println("r.searchResultMap.size()==>" + r.searchResultMap.size());
                    Map<String, LocalFile> map = r.searchResultMap;
                    //System.out.println("map.size()==>" + map.size());
                    //=========================================================
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
//                    //==========================================================
//
//                    for (Map.Entry<String, LocalFile> entry : map.entrySet()) {
//                        String key = entry.getKey();
//                        LocalFile f = entry.getValue();
//                        System.out.println(key + "==" + f.name);
//                        System.out.println("\tComments for file***");
//
//                        ArrayList<Comment> tempCmtLst = f.getComments();
//                        //System.out.println("tempCmtLst.size()==>" + tempCmtLst.size());
//
//                        if (!tempCmtLst.isEmpty()) {
//                            //System.out.println("-------------");
//
//                            Iterator it = tempCmtLst.iterator();
//                            while (it.hasNext()) {
//                                Comment tempComment = (Comment) it.next();
//                                System.out.println("\t\t<*>" + tempComment.getNode().getIp() + ":" + tempComment.getNode().getPort() + " commented ::" + tempComment.getComment());
//                            }
//
//                        }
//                    }

                    System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
                    outMessage = "SUCCESS " + noOfFiles + " " + thisNode.getIp() + " " + thisNode.getPort() + " " + encData;
                    System.out.println("sysout>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
                    System.out.println("sysout> msg out ==> [" + outMessage + "]");
                    System.out.println("sysout>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
                    DatagramPacket out = new DatagramPacket(outMessage.getBytes(), outMessage.length(), incoming.getAddress(), incoming.getPort());
                    sk.send(out);
                } else if (command.equals("SERFAIL")) {
                    outMessage = "ACK " + thisNode.getIp() + " " + thisNode.getPort();
                    outMessage = outMessage.length() + " " + outMessage;
                    outBuffer = outMessage.getBytes();
                    InetAddress hostAddress = InetAddress.getByName(s.getIp());
                    System.out.println("sysout>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
                    System.out.println("sysout> msg out ==> [" + outMessage + "]");
                    System.out.println("sysout>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
                    DatagramPacket out = new DatagramPacket(outBuffer, outBuffer.length, hostAddress, s.getPort());
                    sk.send(out);
                } else if (command.equals("SERFAILHOPEND")) {
                    System.out.println("sysout> Hops exhausted, try again...");
                    outMessage = "ACK " + thisNode.getIp() + " " + thisNode.getPort();
                    outMessage = outMessage.length() + " " + outMessage;
                    outBuffer = outMessage.getBytes();
                    InetAddress hostAddress = InetAddress.getByName(s.getIp());
                    System.out.println("sysout>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
                    System.out.println("sysout> msg out ==> [" + outMessage + "]");
                    System.out.println("sysout>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
                    DatagramPacket out = new DatagramPacket(outBuffer, outBuffer.length, hostAddress, s.getPort());
                    sk.send(out);
                } else if (command.equals("CMT")) {
                    //System.out.println("Comment Message Received");

                    st.nextToken();
                    st.nextToken();
                    CommentComObj ccoRcved = g.fromJson(l.encodeDecodeString("D", st.nextToken()), CommentComObj.class);

                    //System.out.println("ccoRcved.getComment()==>" + ccoRcved.getComment());
                    //System.out.println("ccoRcved.getFile().getName()==>" + ccoRcved.getFile().getName());
                    //System.out.println("ccoRcved.getFromNode().getPort()==>" + ccoRcved.getFromNode().getPort());
                    //System.out.println("ccoRcved.getFile().getUuid()==>" + ccoRcved.getFile().getUuid());
                    LocalFile tempFile = r.localFileMap.get(ccoRcved.getFile().getUuid());
                    //System.out.println("ccoRcved.getFile().getUuid()==>" + tempFile.getName());
                    //System.out.println("ccoRcved.getComment().getComment()" + ccoRcved.getComment().getComment());
                    //System.out.println("ccoRcved.getComment().getLamportTs()" + ccoRcved.getComment().getLamportTs());

                    int rcvTs = ccoRcved.getComment().getLamportTs();
                    //System.out.println("before msg - rcvTs" + rcvTs);
                    ccoRcved.getComment().setLamportTs(lTs.receiveTs(rcvTs));
                    ArrayList<Comment> tempCmtList = tempFile.getComments();

                    //System.out.println("tempFile.getComments().get(0).getLamportTs() - after - msg" + tempFile.getComments().get(0).getLamportTs());
                    //System.out.println("tempFile.getComments()==>" + tempFile.getComments());
                    tempCmtList = tempFile.getComments();

                    tempCmtList.add(ccoRcved.getComment());
                    tempFile.setComments(tempCmtList);

                    //System.out.println("ccoRcved.getFile().getUuid()" + ccoRcved.getFile().getUuid());
                    //System.out.println("tempFile.getName()" + tempFile.getName());
                    //System.out.println("tempFile.getComments().size()" + tempFile.getComments().size());
                    //.put(ccoRcved.getRateHash(), ccoRcved.getRate())
                    //System.out.println("ccoRcved.getFile().getUuid()==>" + ccoRcved.getFile().getUuid());
                    //System.out.println("ccoRcved.getRateHash()==>" + ccoRcved.getRateHash());
                    //System.out.println("ccoRcved.getRate()==>" + ccoRcved.getRate());
                    Map<String, Double> a = r.localFileMap.get(ccoRcved.getFile().getUuid()).getRateMap();
                    //System.out.println("a.size()==>" + a.size());
                    a.put(ccoRcved.getRateHash(), ccoRcved.getRate());

                    tempFile.setRateMap(a);

                    r.localFileMap.replace(ccoRcved.getFile().getUuid(), tempFile);

                    //System.out.println("After Adding Comment");
//                    for (Map.Entry<String, LocalFile> entry : r.localFileMap.entrySet()) {
//                        String key = entry.getKey();
//                        LocalFile f = entry.getValue();
//                        System.out.println(key + "==" + f.name);
//                        System.out.println("\t\t\t***Comments for file***");
//
//                        ArrayList<Comment> tempCmtLst = f.getComments();
//                        System.out.println("tempCmtLst.size()==>" + tempCmtLst.size());
//
//                        if (!tempCmtLst.isEmpty()) {
//                            System.out.println("-------------");
//                            Iterator it = tempCmtLst.iterator();
//                            while (it.hasNext()) {
//                                Comment tempComment = (Comment) it.next();
//                                System.out.println(tempComment.getNode().getIp() + " commented ::" + tempComment.getComment());
//                            }
//                        }
//                    }
                    //localFileMap.get(ccoRcved.getFile().getUuid()).setComments(tempCmtList);
                    outMessage = "CMTACKOK " + thisNode.getIp() + " " + thisNode.getPort();
                    outMessage = outMessage.length() + " " + outMessage;
                    outBuffer = outMessage.getBytes();
                    InetAddress hostAddress = incoming.getAddress();
                    System.out.println("sysout>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
                    System.out.println("sysout> msg out ==> [" + outMessage + "]");
                    System.out.println("sysout>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
                    DatagramPacket out = new DatagramPacket(outBuffer, outBuffer.length, hostAddress, incoming.getPort());
                    sk.send(out);
                } else if (command.equals("ECHO")) {

                    outMessage = "ECHOOK " + thisNode.getIp() + " " + thisNode.getPort();
                    outMessage = outMessage.length() + " " + outMessage;
                    outBuffer = outMessage.getBytes();
                    InetAddress hostAddress = incoming.getAddress();
                    System.out.println("sysout>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
                    System.out.println("sysout> msg out ==> [" + outMessage + "]");
                    System.out.println("sysout>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
                    DatagramPacket out = new DatagramPacket(outBuffer, outBuffer.length, hostAddress, incoming.getPort());
                    sk.send(out);
                }

            }
        } catch (SocketException ex) {
            Logger.getLogger(InboundCommUtils.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(InboundCommUtils.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void run() {
        listenPort();
    }
}
