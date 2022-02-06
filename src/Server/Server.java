package Server;

import com.company.AppConfig;
import com.company.FirstNFLine;

import com.rabbitmq.client.*;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Set;
import java.util.concurrent.TimeoutException;

import static com.company.Main.Read1NF;

public class Server implements Runnable{
//    private final Channel mqChannel;
    private static AppConfig appConfig = AppConfig.load();

    public Server() throws IOException, TimeoutException {
//        AppConfig.MessageQueueServer messageQueueServer = appConfig.messageQueueServer;
//        ConnectionFactory factory = new ConnectionFactory();
//        factory.setHost(messageQueueServer.host);
//        factory.setPort(messageQueueServer.port);
//        factory.setUsername(messageQueueServer.username);
//        factory.setPassword(messageQueueServer.password);
//        mqChannel = factory.newConnection().createChannel();
    }

    public static void main(String[] args) throws IOException, TimeoutException {
        new Server().run();
    }

    @Override
    public void run() {
        System.out.println("Running server");
        Read1NF(Server::SendLine);
    }

    private static void SendLine(FirstNFLine line){
        try (Socket socket = new Socket(appConfig.socketServer.host, appConfig.socketServer.port);
             ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream())) {
            oos.writeObject(line);
            oos.flush();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
