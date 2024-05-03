package it.polimi.ingsw.gc07.main;

import it.polimi.ingsw.gc07.NicknameCheck;
import it.polimi.ingsw.gc07.network.rmi.RmiClient;
import it.polimi.ingsw.gc07.network.VirtualServerGamesManager;
import it.polimi.ingsw.gc07.network.socket.SocketClient;

import java.io.*;
import java.net.Socket;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ClientMain {
    public static void main(String[] args) {
        try {
            Scanner scan = new Scanner(System.in);

            String ip;
            do {
                System.out.println("Insert IP: ");
                System.out.print("> ");
                ip = scan.nextLine();
            }while(!checkValidIp(ip));

            boolean wrongInput = true;
            boolean connectionType = false;
            while(wrongInput) {
                System.out.println("Insert connection type (1 = RMI, 0 = Socket)");
                System.out.print("> ");
                int connectionTypeInt = scan.nextInt();
                scan.nextLine();
                if(connectionTypeInt == 1) {
                    wrongInput = false;
                    connectionType = true;
                }else if(connectionTypeInt == 0) {
                    wrongInput = false;
                    connectionType = false;
                }else {
                    System.out.println("No such connection type");
                }
            }

            wrongInput = true;
            boolean interfaceType = false;
            while(wrongInput) {
                System.out.println("Insert interface type(1 = GUI, 0 = TUI)");
                System.out.print("> ");
                int interfaceTypeInt = scan.nextInt();
                scan.nextLine();
                if(interfaceTypeInt == 1) {
                    wrongInput = false;
                    interfaceType = true;
                }else if(interfaceTypeInt == 0) {
                    wrongInput = false;
                    interfaceType = false;
                }else {
                    System.out.println("No such interface type");
                }
            }

            if(connectionType) {
                // RMI connection
                Registry registry = null;
                try {
                    registry = LocateRegistry.getRegistry(ip, 1234);
                } catch (RemoteException ex) {
                    throw new RuntimeException(ex);
                }
                VirtualServerGamesManager rmiServerGamesManager = (VirtualServerGamesManager) registry.lookup("VirtualServerGamesManager");

                String nickname;
                NicknameCheck check;
                do{
                    System.out.println("Insert nickname: ");
                    System.out.print("> ");
                    nickname = scan.nextLine();

                    // check RMI
                    check = rmiServerGamesManager.checkNickname(nickname);

                    //TODO
                    // check if a socket client has that name

                }while(check.equals(NicknameCheck.EXISTING_NICKNAME));

                RmiClient newRmiClient = new RmiClient(nickname, rmiServerGamesManager);
                // add virtual view to rmiServerGamesManager
                rmiServerGamesManager.connect(newRmiClient);
                newRmiClient.connectToGamesManagerServer(connectionType, interfaceType);

                if(check.equals(NicknameCheck.NEW_NICKNAME)) {
                    newRmiClient.runCliJoinGame();
                }
            }else {
                // Socket connection
                //TODO per adesso la porta e l'host è data da linea di comando, stabilire se bisogna cambiarlo
                //String host = args[0];
                //int port = Integer.parseInt(args[1]);
                String host = "127.0.0.1";
                int port = 65000;
                Socket sc = new Socket(host, port);

                System.out.println("Insert nickname: ");
                System.out.print("> ");
                String nickname = scan.nextLine();

                SocketClient socketClient = new SocketClient(nickname, sc);
                socketClient.connectToGamesManager(connectionType, interfaceType);
            }

        }catch(NotBoundException | IOException e) {
            e.printStackTrace();
            throw new RuntimeException();
            //TODO manage exception
        }
    }

    private static boolean checkValidIp(String ip) {
        String regex = "^((25[0-5]|(2[0-4]|1\\d|[1-9]|)\\d)\\.?\\b){4}$";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(ip);
        return matcher.matches();
    }
}
