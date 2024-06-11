package org.cfa.online;

import java.io.*;

public class ClientHandler implements Runnable{

    private Server server;
    private BufferedReader inputStream;
    private PrintWriter printWriter;
    private int index;

    /**
     * Instantiate the streams between the client and the server
     * @param inputStream where we receive messages from the client
     * @param outputStream where we send messages to the server
     * @param server connection to the server
     * @param index current client position in the client socket array list
     */
    public ClientHandler(InputStream inputStream, OutputStream outputStream, Server server, int index){

        this.inputStream = new BufferedReader(new InputStreamReader(inputStream));
        printWriter = new PrintWriter(outputStream, true);
        this.server = server;
        this.index = index;

    }

    /**
     * main loop for communication between the server and the client
     * allows commands like /help or /list
     */
    @Override
    public void run() {

        try {
            printWriter.println("Type your username");
            String userName = inputStream.readLine();

            while((userName.split(" ")).length > 1){
                printWriter.println("Usernames cannot contain spaces. Type a valid username.");
                userName = inputStream.readLine();
            }

            setUsername(userName);

            sendMessagesWithoutUsername(server.getUserName(index) + " connected to the server.");

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        while(true) {

            try {
                String message = inputStream.readLine();

                if(message.charAt(0) == '/'){
                    if(commands(message)){
                        break;
                    }
                    continue;
                }

                System.out.println("Message from " + server.getUserName(index) + ": " + message);

                sendMessages(message);


            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    /**
     * Commands to be executed when called by the client
     * @param message the command and, if aplicable, the remaining arguments for the command
     * @return true to exit the chatroom, false otherwise
     * @throws IOException
     */
    private boolean commands(String message) throws IOException{
        String[] commandCheck = message.split(" ");

        if (commandCheck[0].equals("/quit")) {
            sendMessagesWithoutUsername(server.getUserName(index) + " exit the server");
            return true;
        }

        if (commandCheck[0].equals("/help")) {
            helpCommand();
            return false;
        }

        if (commandCheck[0].equals("/list")) {
            listCommand();
            return false;
        }

        if (commandCheck[0].equals("/setusername")) {
            if (commandCheck.length != 2){
                printWriter.println("Please insert a valid username without spaces.");
                return false;
            }

            printWriter.println("------------------");
            String currentName = server.getUserName(index);
            if(!setUsername(commandCheck[1])){
                return false;
            }
            sendMessagesWithoutUsername(currentName + " changed username to: " + server.getUserName(index));
            return false;
        }

        if (commandCheck[0].equals("/whisper")) {
            if (commandCheck.length < 3){
                printWriter.println("Please insert a valid user to whisper and then the message you want to send");
                return false;
            }

            whisperCommand(commandCheck);
            return false;
        }

        printWriter.println("This is not a valid command. Use /help for a list of all the available commands.");
        return false;
    }

    /**
     * receive a message from a client and send to all clients
     * @param message message to be sent to all clients
     * @throws IOException
     */
    public void sendMessages(String message) throws IOException {
        for(int i = 0; i < server.getClientSocket().size(); i ++){
            PrintWriter sendMessage = new PrintWriter(server.getClientSocket().get(i).getOutputStream(), true);
            sendMessage.println(server.getUserName(index)+ ": " + message);
        }
    }

    /**
     * used to transmit server messages or information to all clients
     * @param message message to be transmitted
     * @throws IOException
     */
    public void sendMessagesWithoutUsername(String message) throws IOException {
        for(int i = 0; i < server.getClientSocket().size(); i ++){
            PrintWriter sendMessage = new PrintWriter(server.getClientSocket().get(i).getOutputStream(), true);
            sendMessage.println(message);
        }
    }

    /**
     * set up or change username according to client input
     * allows /cancel to cancel the command
     */
    public boolean setUsername(String userName){

            if(containsUser(userName)){
                printWriter.println("That username already exists, please try another one");
                return false;
            }

            server.setUserName(index, userName);
            printWriter.println("Username set to: " + userName);
            System.out.println("Client " + index + " set username to " + server.getUserName(index));
            printWriter.println("------------------");
            return true;
    }

    /**
     * lists all the commands available
     */
    private void helpCommand(){
        printWriter.println("------------------");
        printWriter.println("Commands available: \n" +
                "/help — list all the available commands\n" +
                "/quit — exit chat room\n" +
                "/setusername — change your username\n" +
                "/list — list all the connected clients\n" +
                "/cancel — cancel current command\n" +
                "/whisper — send a message to a specific user");
        printWriter.println("------------------");
    }

    /**
     * lists all connected clients
     */
    private void listCommand(){
        printWriter.println("------------------");
        printWriter.println("The following users are connected:");
        for(int i = 0; i < server.getListUserName().size(); i++){
            printWriter.println(server.getUserName(i));
        }
        printWriter.println("------------------");
    }

    /**
     * allows messages to be sent in private between two clients
     * @throws IOException
     */
    private void whisperCommand(String[] whisper) throws IOException{

        if(!containsUser(whisper[1])){
            printWriter.println("That username doesn't exist, please try another one");
            return;
        }

        int whisperIndex = server.getListUserName().indexOf(whisper[1]);

        String whisperMessage = "";

        for (int i = 2; i < whisper.length; i++){
            whisperMessage += whisper[i] + " ";
        }

        PrintWriter sendWhisper = new PrintWriter(server.getClientSocket().get(whisperIndex).getOutputStream(), true);
        sendWhisper.println("Whisper from " + server.getUserName(index) + ": " + whisperMessage);

    }

    /**
     * check if the username exists in the array list
     * @param username the username you want to check
     * @return
     */
    public boolean containsUser(String username){
        return server.getListUserName().contains(username);
    }
}
