package com.homurax.chapter04.server.parallel.command;

import com.homurax.chapter03.server.wdi.data.WDIDAO;

import java.net.Socket;

public class ConcurrentReportCommand extends ConcurrentCommand {

    public ConcurrentReportCommand(Socket socket, String[] command) {
        super(socket, command);
    }

    @Override
    public String execute() {

        WDIDAO dao = WDIDAO.getDAO();
        return dao.report(command[3]);
    }
}
