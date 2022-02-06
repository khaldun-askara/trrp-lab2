package Client;

import com.company.AppConfig;
import com.company.FirstNFLine;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DeliverCallback;
import org.apache.commons.lang3.SerializationUtils;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeoutException;

import static com.company.Main.CreateDBWithTables;
import static com.company.Main.PrintToPostgreSQL;

public class Client implements Runnable{
    private int socketOrRMQ;
    private final AppConfig appConfig;
    public Client(){
        appConfig = AppConfig.load();
    }
    public Client(int socketOrRMQ){
        appConfig = AppConfig.load();
        this.socketOrRMQ = socketOrRMQ;
    }

    public static void main(String[] args) throws IOException, TimeoutException {
        CreateDBWithTables();
        new Client(Integer.valueOf((String)args[0])).run();
    }

    @Override
    public void run() {
        if (socketOrRMQ == 1){
            try (ServerSocket serverSocket = new ServerSocket(
                    appConfig.socketServer.port, 50, InetAddress.getByName(appConfig.socketServer.host))) {
                System.out.printf("Running client");
                ExecutorService pool = Executors.newCachedThreadPool();
                try {
                    while (true) {
                        Socket socket = serverSocket.accept();
                        pool.execute(() -> handleConnection(socket));
                    }
                } finally {
                    pool.shutdown();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (socketOrRMQ == 2){
            ConnectionFactory factory = new ConnectionFactory();
            factory.setHost(appConfig.messageQueueServer.host);
            factory.setPort(appConfig.messageQueueServer.port);
            factory.setUsername(appConfig.messageQueueServer.username);
            factory.setPassword(appConfig.messageQueueServer.password);
            try{
                Connection connection = factory.newConnection();
                Channel channel = connection.createChannel();

                channel.queueDeclare("hello", false, false, false, null);

                DeliverCallback deliverCallback = (consumerTag, delivery) -> {
                    FirstNFLine line = (FirstNFLine) SerializationUtils.deserialize(delivery.getBody());
                    PrintToPostgreSQL(line);
                };
                channel.basicConsume("hello", true, deliverCallback, consumerTag -> { });
            } catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    private void handleConnection(Socket socket) {
        try (ObjectInputStream ois = new ObjectInputStream(socket.getInputStream())) {
            FirstNFLine line = (FirstNFLine) ois.readObject();
            PrintToPostgreSQL(line);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
