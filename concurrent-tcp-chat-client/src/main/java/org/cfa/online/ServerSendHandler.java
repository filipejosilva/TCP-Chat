package org.cfa.online;

import java.io.*;

public class ServerSendHandler implements Runnable{

    private Client client;
    private BufferedReader outputReader;
    private BufferedWriter outputWriter;

    /**
     * Instantiate the server sendHandler to send messages to the clients
     * @param client the client who sends the message
     * @param outputStream connection from the server to the clients
     */
    public ServerSendHandler(Client client, OutputStream outputStream){
        this.client = client;
        this.outputWriter = new BufferedWriter(new OutputStreamWriter(outputStream));
        outputReader = new BufferedReader(new InputStreamReader(System.in));
    }

    /**
     * main loop to send messages
     * allows /quit to exit the chatroom
     */
    @Override
    public void run() {

        String line = "";

        while(true){
            try {

                line = outputReader.readLine();
                outputWriter.write(line);
                outputWriter.newLine();
                outputWriter.flush();

                if(line.equals("/quit")){
                    System.out.println("Exiting the chatRoom");
                    System.exit(0);
                    client.closeStreams();
                }

            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
}

