import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Arrays;


public class Main {
    public static void main(String[] args) throws Exception {
        BooleanSearchEngine engine = new BooleanSearchEngine(new File(ServerConfigurations.pdfsDir));
        engine.loadStopTxtFile(ServerConfigurations.stopTxt);

        try (ServerSocket serverSocket = new ServerSocket(ServerConfigurations.PORT)) { // стартуем сервер один(!) раз

            System.out.println("Сервер запущен");

            while (true) { // в цикле(!) принимаем подключения
                try (
                        Socket socket = serverSocket.accept();
                        BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                        PrintWriter out = new PrintWriter(socket.getOutputStream());
                ) {
                    System.out.println("Новый запрос");
                    String str = in.readLine();
                    System.out.println(str);


                    GsonBuilder builder = new GsonBuilder();
                    out.write(builder.setPrettyPrinting().create().toJson(engine.search(str)));
                    System.out.println(builder.setPrettyPrinting().create().toJson(engine.search(str)));


                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
            }
        } catch (IOException e) {
            System.out.println("Не могу стартовать сервер");
            e.printStackTrace();
        }

    }
}