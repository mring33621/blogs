package com.mattring.blog.nats.post1;

import io.nats.client.Connection;
import io.nats.client.Nats;

import java.io.IOException;
import java.util.Arrays;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.stream.StreamSupport;

/**
 * Created by mring on 12/11/2016.
 */
public class EmailWatcher {

    private static final String[] DEFAULT_KEYWORDS =
            new String[]{
                    "secret",
                    "trade",
                    "balance sheet",
                    "legal",
                    "audit",
                    "account"
            };

    static final String KILL_SIGNAL = "whte_rbt.obj";

    static boolean detectKillSignal(String msg) {
        return KILL_SIGNAL.equals(msg);
    }

    private final BlockingQueue<String> inbox;
    private String[] keywords;

    public EmailWatcher() {
        inbox = new LinkedBlockingQueue<>();
    }

    public void setKeywords(String[] keywords) {
        this.keywords = keywords;
    }

    void enqueueMsg(String msg) {
        try {
            inbox.put(msg);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    String nextMsg() {
        String nextItem = "";
        try {
            nextItem = inbox.take();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return nextItem;
    }

    boolean scanMsg(String msg) {
        final String lcs = msg.toLowerCase();
        return Arrays.stream(keywords).parallel().anyMatch(lcs::contains);
    }

    boolean fwdMsg(String msg) {
        return true;
    }

    void onHit(String msg) {
        System.err.println(msg);
    }

    public void stop() {
        enqueueMsg(KILL_SIGNAL);
    }

    public void run() {

        if (keywords == null || keywords.length == 0) {
            keywords = DEFAULT_KEYWORDS;
        }

        // Connect to default URL ("nats://localhost:4222")
        try (Connection nc = Nats.connect()) {
            nc.subscribe("BigBrother", m -> {
                final String msg = new String(m.getData());
                System.out.println("msg: " + msg);
                enqueueMsg(msg);
            });
            StreamSupport.stream(
                    new KillableSpliterator<>(this::nextMsg, EmailWatcher::detectKillSignal), true)
                    .filter(this::fwdMsg)
                    .forEach(this::onHit);
        } catch (IOException ioex) {
            ioex.printStackTrace();
        }
    }

    public static void main(String[] args) {
        EmailWatcher watcher = new EmailWatcher();
        watcher.setKeywords(args);
        watcher.run();
    }
}
