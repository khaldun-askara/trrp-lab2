package Client;

import Server.Server;
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
import java.util.concurrent.TimeoutException;

import static com.company.Main.CreateDBWithTables;
import static com.company.Main.PrintToPostgreSQL;

public class Client implements Runnable{
    public static void main(String[] args) throws IOException, TimeoutException {
        CreateDBWithTables();
        new Client().run();
    }

    @Override
    public void run() {
        try (ServerSocket serverSocket = new ServerSocket(
                25836, 50, InetAddress.getByName("localhost"))) {
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

    private void handleConnection(Socket socket) {
        try (ObjectInputStream ois = new ObjectInputStream(socket.getInputStream())) {
            FirstNFLine line = (FirstNFLine) ois.readObject();
            PrintToPostgreSQL(line);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
