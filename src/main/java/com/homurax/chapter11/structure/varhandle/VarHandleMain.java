package com.homurax.chapter11.structure.varhandle;

import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.TimeUnit;

public class VarHandleMain {

    public static void main(String[] args) {

        VarHandleData data = new VarHandleData();

        for (int i = 0; i < 10; i++) {
            VarHandleTask task = new VarHandleTask(data);
            ForkJoinPool.commonPool().execute(task);
        }

        ForkJoinPool.commonPool().shutdown();

        try {
            ForkJoinPool.commonPool().awaitTermination(1, TimeUnit.DAYS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println("Safe Value: " + data.safeValue);
        System.out.println("Unsafe Value: " + data.unsafeValue);
    }

}
