package com.homurax.chapter05.indexing.concurrent;

import com.homurax.chapter05.indexing.common.Document;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class MultipleConcurrentIndexing {

    public static void main(String[] args) {

        int numCores = Runtime.getRuntime().availableProcessors();
        ThreadPoolExecutor executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(Math.max(numCores - 1, 1));
        ExecutorCompletionService<List<Document>> completionService = new ExecutorCompletionService<>(executor);
        ConcurrentHashMap<String, StringBuffer> invertedIndex = new ConcurrentHashMap<>();
        final int NUMBER_OF_DOCUMENTS = 5000;


        File source = new File("src\\main\\java\\com\\homurax\\chapter05\\indexing\\data");
        File[] files = source.listFiles();

        long startTime = System.currentTimeMillis();
        MultipleInvertedIndexTask invertedIndexTask = new MultipleInvertedIndexTask(completionService, invertedIndex);
        Thread thread1 = new Thread(invertedIndexTask);
        thread1.start();
        MultipleInvertedIndexTask invertedIndexTask2 = new MultipleInvertedIndexTask(completionService, invertedIndex);
        Thread thread2 = new Thread(invertedIndexTask2);
        thread2.start();

        List<File> taskFiles = new ArrayList<>();
        for (File file : files) {
            taskFiles.add(file);
            if (taskFiles.size() == NUMBER_OF_DOCUMENTS) {
                MultipleIndexingTask task = new MultipleIndexingTask(taskFiles);
                completionService.submit(task);
                taskFiles = new ArrayList<>();
            }

            if (executor.getQueue().size() > 10) {
                do {
                    try {
                        TimeUnit.MILLISECONDS.sleep(50);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                } while (executor.getQueue().size() > 10);
            }
        }

        if (taskFiles.size() > 0) {
            MultipleIndexingTask task = new MultipleIndexingTask(taskFiles);
            completionService.submit(task);
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
        thread1.interrupt();
        thread2.interrupt();
    }

}
