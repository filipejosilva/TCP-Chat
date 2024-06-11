package org.cfa.online;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class Server {

    private ServerSocket serverSocket;
    private ArrayList<Socket> clientSocket;
    private ArrayList<String> userName;
    private static final int DEFAULT_PORT = 9092;

    /**
     * Instantiate a server socket
     * Instantiate an array list for client sockets and one for usernames
     * @param port the port that the server will listen on
     */
    public Server(int port) {

        try {

            serverSocket = new ServerSocket(port);
            clientSocket = new ArrayList<>();
            userName = new ArrayList<>();

            System.out.println("Server started! Waiting for clients.");

        } catch (IOException e) {
            throw new RuntimeException("Oops. Something went wrong accessing this port.");
        }
    }

    /**
     * Accept connections from clients
     */
    private void init(){
        while (true){

            try {

                Socket newClient = serverSocket.accept();

                addUser(newClient);


            } catch (IOException e) {
                throw new RuntimeException("Something went wrong initiating the server.");
            }
        }
    }

    /**
     * Add a client to the array list
     * @param newClient new client connection
     * @throws IOException
     */
    private void addUser(Socket newClient) throws IOException{
        clientSocket.add(newClient);
        int index = clientSocket.indexOf(newClient);
        userName.add(index, "userName" + index);
        Thread thread = new Thread(new ClientHandler(clientSocket.get(index).getInputStream(), clientSocket.get(index).getOutputStream(), this, index));
        thread.start();
        System.out.println("Client " + index + " connected!");
    }


    /**
     * @return client socket array list
     */
    public ArrayList<Socket> getClientSocket(){
        return clientSocket;
    }

    /**
     * @param index client socket position in the array list
     * @return the username in that same index
     */
    public String getUserName(int index){
        return userName.get(index);
    }

    /**
     * @return all usernames in the array list
     */
    public ArrayList<String> getListUserName(){
        return userName;
    }

    /**
     * set up or change a client username
     * @param index client index in the array list
     * @param newName new username for the client
     */
    public void setUserName(int index, String newName){
        userName.set(index, newName);
    }



    public static void main(String[] args) {
        Server server = new Server(DEFAULT_PORT);
        server.init();
    }
}
