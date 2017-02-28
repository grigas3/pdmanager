package com.pdmanager.notification;

import android.content.Context;

import com.squareup.tape.ObjectQueue;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Created by george on 28/1/2017.
 */

public class BandMessageQueue extends LinkedBlockingQueue<BandMessage> {


    public BandMessageQueue() {

    }


}
