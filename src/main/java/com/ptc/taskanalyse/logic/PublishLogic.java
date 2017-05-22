package com.ptc.taskanalyse.logic;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by asasmaz on 22/05/2017.
 */
public class PublishLogic {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    /**
     * Publish to given subscribers
     * @param subscribers
     */
    public void publishToSubscribers(Set<String> subscribers) {
        if (subscribers == null)
            return;

        logger.info("Starting to call " + subscribers.size() + " many subscribers one by one now.");

        ExecutorService executorService = Executors.newFixedThreadPool(10);

        for (String url : subscribers) {
            executorService.execute(() -> callUrl(url));
        }

        executorService.shutdown();
    }

    /**
     * Call a given URL, ping it
     * @param url
     */
    private void callUrl(String url) {

        logger.info("Calling subscriber URL " + url);

        try {
            URL subUrl = new URL(url);
            URLConnection subConn = subUrl.openConnection();
            BufferedReader in = new BufferedReader(new InputStreamReader(subConn.getInputStream()));
            in.close();

        } catch (Exception e) {
            logger.error("Couldn't ping subscriber url " + url, e);
        }
    }

}
