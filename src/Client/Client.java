package Client;

import Server.Server;
import com.company.AppConfig;
import com.company.FirstNFLine;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import static com.company.Main.*;

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
