package com.homurax.chapter06.keyword.concurrent;

import com.homurax.chapter06.keyword.common.Word;

import java.io.File;
import java.util.Arrays;
import java.util.Date;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.Phaser;

public class ConcurrentKeywordExtraction {

    public static void main(String[] args) {

        ConcurrentHashMap<String, Word> globalVoc = new ConcurrentHashMap<>();
        ConcurrentHashMap<String, Integer> globalKeywords = new ConcurrentHashMap<>();

        long start = System.currentTimeMillis();
        File source = new File("src\\main\\java\\com\\homurax\\chapter05\\indexing\\data");

        File[] files = source.listFiles(f -> f.getName().endsWith(".txt"));
        if (files == null) {
            System.err.println("The 'data' folder not found!");
            return;
        }
        ConcurrentLinkedDeque<File> concurrentFileListPhase1 = new ConcurrentLinkedDeque<>(Arrays.asList(files));
        ConcurrentLinkedDeque<File> concurrentFileListPhase2 = new ConcurrentLinkedDeque<>(Arrays.asList(files));

        int numDocuments = files.length;

        System.out.println(concurrentFileListPhase1.size());
        System.out.println(concurrentFileListPhase2.size());

        int factor = 1;
        if (args.length > 0) {
            factor = Integer.valueOf(args[0]);
        }

        int numTasks = factor * Runtime.getRuntime().availableProcessors();
        Phaser phaser = new Phaser();

        Thread[] threads = new Thread[numTasks];
        KeywordExtractionTask[] tasks = new KeywordExtractionTask[numTasks];


        for (int i = 0; i < numTasks; i++) {
            tasks[i] = new KeywordExtractionTask(concurrentFileListPhase1, concurrentFileListPhase2, phaser, globalVoc,
                    globalKeywords, concurrentFileListPhase1.size(), "Task " + i, i == 0);
            phaser.register();
            System.out.println(phaser.getRegisteredParties() + " tasks arrived to the Phaser.");
        }

        for (int i = 0; i < numTasks; i++) {
            threads[i] = new Thread(tasks[i]);
            threads[i].start();
        }

        for (int i = 0; i < numTasks; i++) {
            try {
                threads[i].join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        System.out.println("Is Terminated: " + phaser.isTerminated());
        long end = System.currentTimeMillis();

        System.out.println();
        System.out.println("Execution Time: " + (end - start));
        System.out.println("Vocabulary Size: " + globalVoc.size());
        System.out.println("Keyword Size: " + globalKeywords.size());
        System.out.println("Number of Documents: " + numDocuments);
    }

}
