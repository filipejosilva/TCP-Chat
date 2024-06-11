package org.cfa.online;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class ServerReaderHandler implements Runnable{

    private BufferedReader inputStream;


    public ServerReaderHandler(InputStream inputStream){
        this.inputStream = new BufferedReader(new InputStreamReader(inputStream));
    }

    /**
     * the main loop to read messages from the server
     */
    @Override
    public void run() {

        try {
            while(true){
                System.out.println(inputStream.readLine());
            }


        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
