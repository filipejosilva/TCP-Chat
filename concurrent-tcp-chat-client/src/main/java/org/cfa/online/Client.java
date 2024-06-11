package org.cfa.online;


import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

public class Client{

    private Socket clientSocket;
    private InputStream inputStream;
    private OutputStream outputStream;
    private static final int DEFAULT_PORT = 9092;

    /**
     * Instantiate the client with a given port and set up the communication streams
     * @param port the port to the server
     */
    public Client(int port) {

        try {
            clientSocket = new Socket("localhost", port);
            inputStream = clientSocket.getInputStream();
            outputStream = clientSocket.getOutputStream();

        } catch (IOException e){
            throw new RuntimeException("Could not connect to this port.");

        }
    }

    /**
     * Start the communication threads between client and server
     */
    public void init(){
        //Create threads
        Thread sendThread = new Thread(new ServerSendHandler(this, outputStream));
        Thread readerThread = new Thread(new ServerReaderHandler(inputStream));

        //Start Threads
        sendThread.start();
        readerThread.start();

    }

    /**
     * close the communication streams between the client and the server
     * @throws IOException
     */
    public void closeStreams() throws IOException {
        inputStream.close();
        outputStream.close();
        clientSocket.close();
        System.out.println("Quitting the server");
    }


    public static void main(String[] args) {
        Client client = new Client(DEFAULT_PORT);
        client.init();

    }
}