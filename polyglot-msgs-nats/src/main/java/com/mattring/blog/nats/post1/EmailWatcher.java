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

    private static final String POISON = "EmailWatcher.Poison";
    private static final String[] DEFAULT_KEYWORDS =
            new String[]{
                    "secret",
                    "trade",
                    "balance sheet",
                    "legal",
                    "audit",
                    "account"
            };

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

    boolean flagMsg(String msg) {
        final String lcs = msg.toLowerCase();
        return Arrays.stream(keywords).parallel().anyMatch(lcs::contains);
    }

    void alertSomeone(String msg) {
        System.err.println(msg);
    }

    public void stop() {
        enqueueMsg(POISON);
    }

    public void run() {

        if (keywords == null || keywords.length == 0) {
            keywords = DEFAULT_KEYWORDS;
        }

        // Connect to default URL ("nats://localhost:4222")
        try (Connection nc = Nats.connect()) {
            nc.subscribe("BigBrother", m -> {
                enqueueMsg(new String(m.getData()));
            });
            StreamSupport.stream(
                    new KillableSpliterator<String>(this::nextMsg, POISON), true)
                    .filter(this::flagMsg)
                    .forEach(this::alertSomeone);
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
