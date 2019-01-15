/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.overlay.dcapp;

import static java.lang.Integer.max;
import java.util.concurrent.atomic.AtomicInteger;

/**
 *
 * @author 10938 Lamport timestamps
 */
public class LamportTS {

    public static Integer lamportTs = 0;

    public int sendTs() {
        return lamportTs++;
    }

    public int receiveTs(int tsIn) {
        lamportTs = max(lamportTs, tsIn) + 1;
        return lamportTs;
    }

    public static void main(String[] args) {
        System.out.println("Gettings Human!");
    }
}
