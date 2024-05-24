package it.polimi.ingsw.gc07.network.socket;

import it.polimi.ingsw.gc07.enumerations.NicknameCheck;
import it.polimi.ingsw.gc07.game_commands.*;
import it.polimi.ingsw.gc07.model_view.GameView;
import it.polimi.ingsw.gc07.network.Client;
import it.polimi.ingsw.gc07.network.PingSender;
import it.polimi.ingsw.gc07.updates.*;
import it.polimi.ingsw.gc07.view.Ui;
import it.polimi.ingsw.gc07.view.gui.Gui;
import it.polimi.ingsw.gc07.view.tui.Tui;
import javafx.application.Application;

import java.io.*;
import java.net.Socket;
import java.rmi.RemoteException;
import java.util.Scanner;

public class SocketClient implements Client, PingSender {
    private String nickname;
    private final Socket mySocket;
    private GameView gameView;
    private final ObjectInputStream input;
    private VirtualSocketServer myServer;
    private boolean clientAlive;
    private Ui ui;
    private static final int maxMissedPongs = 3;
    private boolean pong;
    public SocketClient(Socket mySocket, boolean interfaceType) throws IOException {
        System.out.println("SC> costruttore");
        InputStream temp_input;
        OutputStream temp_output;

        this.mySocket = mySocket;

        this.ui = null;

        temp_output = this.mySocket.getOutputStream();
        ObjectOutputStream output = new ObjectOutputStream(temp_output);
        output.flush();

        temp_input = this.mySocket.getInputStream();
        this.input = new ObjectInputStream(temp_input);

        this.clientAlive = true;
        this.pong = true;

        manageSetUp(output, interfaceType);

    }

    @Override
    public GameView getGameView() {
        return gameView;
    }

    private void manageSetUp(ObjectOutputStream output, boolean interfaceType){
        Scanner scan = new Scanner(System.in);
        String nickname;
        NicknameCheck check = null;
        do{
            System.out.println("Insert nickname: ");
            System.out.print("> ");
            nickname = scan.nextLine();
            try {
                output.writeObject(nickname);
            } catch (IOException e) {
                closeConnection();
                break;
            }
            try {
                check = (NicknameCheck) input.readObject();
            } catch (IOException | ClassNotFoundException e) {
                closeConnection();
                break;
            }
        }while(check.equals(NicknameCheck.EXISTING_NICKNAME));

        this.nickname = nickname;
        this.gameView = new GameView(nickname);
        this.myServer = new VirtualSocketServer(output);

        if(interfaceType) {
            new Thread(() -> Application.launch(Gui.class));
            this.ui = Gui.getGuiInstance();
            this.ui.setNickname(nickname);
            this.ui.setClient(this);
        } else {
            this.ui = new Tui(nickname, this);
        }
        this.gameView.addViewListener(ui);
        if(isClientAlive()){
            if(check != null && check.equals(NicknameCheck.NEW_NICKNAME)){
                try {
                    myServer.setAndExecuteCommand(new AddPlayerToPendingCommand(nickname, false, interfaceType));
                } catch (IOException e) {
                    //Thrown if the connection failed after the initialization and before the ping-pong notices it
                    closeConnection();
                }
                this.runCliJoinGame();
            }else{
                try {
                    output.writeBoolean(interfaceType);
                    output.reset();
                    output.flush();
                } catch (IOException e) {
                    closeConnection();
                    //ui.askForReconnection();
                }
                new Thread(this::manageReceivedUpdate).start();
                new Thread(this::startGamePing).start();
                new Thread(this::checkPong).start();
                runCliGame();
            }
        }else{
            //ui.askForReconnection();
        }
    }

    private void connectToGamesManagerServer(boolean interfaceType) {
        System.out.println("SC> connectToGMS");
        try {
            myServer.setAndExecuteCommand(new AddPlayerToPendingCommand(nickname, false, interfaceType));
        } catch (IOException e) {
            System.out.println("\n(4) Connection failed.\n");
            closeConnection();
        }
        this.runCliJoinGame();
    }

    public void runCliJoinGame() {
        assert(ui != null);
        ui.runJoinGameInterface();
        if(isClientAlive()){
            String result;
            try {
                result = (String) input.readObject();
            } catch (IOException | ClassNotFoundException e) {
                closeConnection();
                result = null;
            }
            if(result != null && result.equals("Game joined.")){
                new Thread(this::manageReceivedUpdate).start();
                // game joined
                new Thread(this::startGamePing).start();
                new Thread(this::checkPong).start();
                runCliGame();
            }else{
                if(result != null && result.equals("Display successful.")){
                    Update update;
                    try {
                        update = (Update) input.readObject();
                        update.execute(gameView);
                    } catch (IOException | ClassNotFoundException e) {
                        closeConnection();
                    }
                }
                runCliJoinGame();
            }
        }else{
            //ask for reconnection
            //ui.askForReconnection();
        }
    }//TODO la parte con un unico thread che gestisce input e output dovrebbe essere stata coperta
    //TODO nella parte con un unico thread ogni volta che si compie .readObject/.writeObject l'eccezione è legata al fatto che la connessione è catuda ed
    //TODO è stata osservata per la prima volta, il caso in cui IOException è dovuta ha stream già chiuso in precenenza non dovrebbe succedere in questa parte

    private void manageReceivedUpdate() {
        System.out.println("SC-T> manageReceivedUpdate");
        Update update;
        while (true){
            try {
                update = (Update) input.readObject();
                update.execute(gameView);
                synchronized (this){
                    pong = true;
                }
            } catch (IOException | ClassNotFoundException e) {
                System.out.println("\nServer has closed connection.\n");
                closeConnection();
                break;
            }
        }
    }

    private synchronized void closeConnection(){
        //TODO system exit (?)
        if(isClientAlive()){
            setClientAlive(false);
            try{
                input.close();
                myServer.closeConnection();
                mySocket.close();
            }catch (IOException e){
                throw new RuntimeException();
            }
        }
    }

    public void runCliGame() {
        assert(ui != null);
        ui.runGameInterface();
    }

    @Override
    public void setAndExecuteCommand(GamesManagerCommand gamesManagerCommand) {
        try {
            myServer.setAndExecuteCommand(gamesManagerCommand);
        } catch (IOException e) {
            closeConnection();
        }
    }

    @Override
    public void setAndExecuteCommand(GameControllerCommand gameControllerCommand) {
        try {
            myServer.setAndExecuteCommand(gameControllerCommand);
        } catch (IOException e) {
            System.out.println("\n(9) Connection failed.\n");
            closeConnection();
        }
    }

    @Override
    public synchronized void setClientAlive(boolean isAlive) {
        if(isAlive){
            this.clientAlive = isAlive;
        }else{
            closeConnection();
        }
    }

    @Override
    public synchronized boolean isClientAlive() {
        return clientAlive;
    }

    @Override
    public void startGamePing() {
        System.out.println("SC-T2> startGamePing");
        while(true) {
            boolean isAlive;
            synchronized (this) {
                isAlive = clientAlive;
            }
            if (!isAlive) {
                break;
            } else {
                try {
                    myServer.setAndExecuteCommand(new SendPingCommand(nickname));
                } catch (IOException e) {
                    closeConnection();
                }
            }
            try {
                Thread.sleep(1000); // wait one second between two ping
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }

    /**
     * Method that checks if the client is receiving pongs from server.
     */
    private void checkPong() {
        int missedPong = 0;
        while(true){
            if(!isClientAlive()){
                break;
            }
            synchronized(this) {
                if(pong) {
                    missedPong = 0;
                }else {
                    missedPong ++;
                    if(missedPong >= maxMissedPongs) {
                        System.out.println("you lost the connection :(");
                        closeConnection();
                        break;
                    }
                }
                pong = false;

            }
            try {
                Thread.sleep(1000); // wait one second between two pong checks
            } catch (InterruptedException e) {
                throw new RuntimeException();
            }
        }
    }
}
