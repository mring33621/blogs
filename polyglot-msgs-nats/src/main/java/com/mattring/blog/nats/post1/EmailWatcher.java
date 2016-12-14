package com.mattring.blog.nats.post1;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Created by mring on 12/11/2016.
 */
public class EmailWatcher {

    private static final String POISON = "EmailWatcher.Poison";

    private final BlockingQueue<String> inbox;

    public EmailWatcher() {
        inbox = new LinkedBlockingQueue<>();
    }
}
