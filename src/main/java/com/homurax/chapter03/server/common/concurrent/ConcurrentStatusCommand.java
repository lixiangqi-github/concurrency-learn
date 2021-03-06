package com.homurax.chapter03.server.common.concurrent;

import com.homurax.chapter03.server.common.Command;
import com.homurax.chapter03.server.parallel.log.Logger;
import com.homurax.chapter03.server.parallel.server.ConcurrentServer;

import java.util.concurrent.ThreadPoolExecutor;

public class ConcurrentStatusCommand extends Command {

    public ConcurrentStatusCommand(String[] command) {
        super(command);
        setCacheable(false);
    }


    @Override
    public String execute() {

        StringBuilder sb = new StringBuilder();
        ThreadPoolExecutor executor = ConcurrentServer.getExecutor();

        sb.append("Server Status;");
        sb.append("Actived Threads: ");
        sb.append(executor.getActiveCount());
        sb.append(";");
        sb.append("Maximum Pool Size: ");
        sb.append(executor.getMaximumPoolSize());
        sb.append(";");
        sb.append("Core Pool Size: ");
        sb.append(executor.getCorePoolSize());
        sb.append(";");
        sb.append("Pool Size: ");
        sb.append(executor.getPoolSize());
        sb.append(";");
        sb.append("Largest Pool Size: ");
        sb.append(executor.getLargestPoolSize());
        sb.append(";");
        sb.append("Completed Task Count: ");
        sb.append(executor.getCompletedTaskCount());
        sb.append(";");
        sb.append("Task Count: ");
        sb.append(executor.getTaskCount());
        sb.append(";");
        sb.append("Queue Size: ");
        sb.append(executor.getQueue().size());
        sb.append(";");
        sb.append("Cache Size: ");
        sb.append(String.valueOf(ConcurrentServer.getCache().getItemCount()));
        sb.append(";");
        Logger.sendMessage(sb.toString());
        return sb.toString();
    }

}
