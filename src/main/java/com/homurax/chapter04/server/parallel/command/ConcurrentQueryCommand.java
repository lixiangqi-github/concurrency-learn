package com.homurax.chapter04.server.parallel.command;

import com.homurax.chapter03.server.wdi.data.WDIDAO;

import java.net.Socket;

public class ConcurrentQueryCommand extends ConcurrentCommand {


    public ConcurrentQueryCommand(Socket socket, String[] command) {
        super(socket, command);
    }


    @Override
    public String execute() {

        WDIDAO dao = WDIDAO.getDAO();

        if (command.length == 5) {
            return dao.query(command[3], command[4]);
        } else if (command.length == 6) {
            try {
                return dao.query(command[3], command[4], Short.parseShort(command[5]));
            } catch (Exception e) {
                return "ERROR;Bad Command";
            }
        } else {
            return "ERROR;Bad Command";
        }
    }

}
