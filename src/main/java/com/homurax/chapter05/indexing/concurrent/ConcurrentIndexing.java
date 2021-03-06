package com.homurax.chapter05.indexing.concurrent;

import com.homurax.chapter05.indexing.common.Document;

import java.io.File;
import java.util.Date;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class ConcurrentIndexing {

    public static void main(String[] args) {

        int numCores = Runtime.getRuntime().availableProcessors();
        ThreadPoolExecutor executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(Math.max(numCores - 1, 1));

        ExecutorCompletionService<Document> completionService = new ExecutorCompletionService<>(executor);
        ConcurrentHashMap<String, StringBuffer> invertedIndex = new ConcurrentHashMap<>();

        File source = new File("src\\main\\java\\com\\homurax\\chapter05\\indexing\\data");
        File[] files = source.listFiles();

        InvertedIndexTask invertedIndexTask = new InvertedIndexTask(completionService, invertedIndex);
        Thread thread1 = new Thread(invertedIndexTask);
        thread1.start();

        InvertedIndexTask invertedIndexTask2 = new InvertedIndexTask(completionService, invertedIndex);
        Thread thread2 = new Thread(invertedIndexTask2);
        thread2.start();

        long startTime = System.currentTimeMillis();
        for (File file : files) {
            IndexingTask task = new IndexingTask(file);
            completionService.submit(task);
            if (executor.getQueue().size() > 1000) {
                do {
                    try {
                        TimeUnit.MILLISECONDS.sleep(50);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                } while (executor.getQueue().size() > 1000);
            }
        }

        executor.shutdown();

        try {
            executor.awaitTermination(1, TimeUnit.DAYS);

            thread1.interrupt();
            thread2.interrupt();
            thread1.join();
            thread2.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        long endTime = System.currentTimeMillis();


        System.out.println("Execution Time: " + (endTime - startTime));
        System.out.println("invertedIndex: " + invertedIndex.size());
        System.out.println(invertedIndex.get("book").length());
    }

}
