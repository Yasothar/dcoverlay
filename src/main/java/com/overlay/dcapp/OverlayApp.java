/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.overlay.dcapp;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;

/**
 *
 * @author 10938
 */
public class OverlayApp {

    private LocalUtils localUtils = new LocalUtils();
    private final Node thisNode = localUtils.getLocalIpPort();
    private final Node bsNode = localUtils.getBsIpPort();
    private int hops = localUtils.getHops();

    private LamportTS lTs = new LamportTS();

    public static void main(String[] args) {

        Repository r = new Repository();
        OverlayApp v = new OverlayApp();

        ExecutorService executorService = Executors.newFixedThreadPool(5);
        OverlayMain main = new OverlayMain(v.thisNode, r, v.lTs, v.bsNode, v.hops);
        InboundCommUtils inbound = new InboundCommUtils(v.thisNode /*,main.localFileMap,main.fileList*/,
                main.neighbourList /*searchResultMap*/,
                r, v.lTs);
        //HeartBeat h = new HeartBeat(main.neighbourList);
        executorService.submit(main);
        executorService.submit(inbound);
        //executorService.submit(h);
    }
}
