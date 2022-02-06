package Server;

import com.company.AppConfig;
import com.company.FirstNFLine;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import org.apache.commons.lang3.SerializationUtils;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.concurrent.TimeoutException;

import static com.company.Main.Read1NF;

public class Server implements Runnable{
//    private final Channel mqChannel;
    private static int socketOrRMQ;
    private static AppConfig appConfig = AppConfig.load();

    public Server() {}
    public Server (int socketOrRMQ){
        this.socketOrRMQ = socketOrRMQ;
    }

    public static void main(String[] args) throws IOException, TimeoutException {
        new Server(Integer.valueOf((String)args[0])).run();
    }

    @Override
    public void run() {
        System.out.println("Running server");
        Read1NF(Server::SendLine);
    }

    private static void SendLine(FirstNFLine line){
        if (socketOrRMQ == 1){
            try (Socket socket = new Socket(appConfig.socketServer.host, appConfig.socketServer.port);
                 ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream())) {
                oos.writeObject(line);
                oos.flush();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (socketOrRMQ == 2){
            ConnectionFactory factory = new ConnectionFactory();
            factory.setHost(appConfig.messageQueueServer.host);
            factory.setPort(appConfig.messageQueueServer.port);
            factory.setUsername(appConfig.messageQueueServer.username);
            factory.setPassword(appConfig.messageQueueServer.password);
            try (Connection connection = factory.newConnection();
                 Channel channel = connection.createChannel()) {
                channel.queueDeclare("hello", false, false, false, null);
                channel.basicPublish("", "hello", null, SerializationUtils.serialize(line));
                System.out.println(" [x] Sent '" + line.toString() + "'");
            } catch (IOException e) {
                e.printStackTrace();
            } catch (TimeoutException e) {
                e.printStackTrace();
            }
        }
    }
}
