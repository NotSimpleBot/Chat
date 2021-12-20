package Server_Folder;

import Connection_AND_ConsoleHelper.Connection;
import Connection_AND_ConsoleHelper.ConsoleHelper;
import Message.Message;
import Message.MessageType;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class Server {
    private final static Map<String, Connection> CLIENT_MAP = new HashMap<>();


    public static void main(String[] args) {
        Server server = new Server();
        server.start();
    }


    /**
     * Запуск сервера.
     * Бесконечный цикл с ожиданием подключений от неограниченного числа клиентов, под каждого
     * клиента создается экземпляр класса ServerHandlerForClients и запускается его нить.
     */
    private void start() {
        System.out.print("Enter the port of the local server: ");
        ServerSocket serverSocket = null;
        try {
            serverSocket = new ServerSocket(ConsoleHelper.getIntFromConsole());
            System.out.println("Server started.");
            while (true) {
                Socket newClientSocket = serverSocket.accept(); //ждем подключения от клиента
                ServerHandlerForClients handler = new ServerHandlerForClients(newClientSocket);
                handler.start();
            }
        } catch (IOException e) {
            e.printStackTrace();
            try {
                Objects.requireNonNull(serverSocket);
                serverSocket.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    //----------------------------------------------------------------------------------------------------------
    public class ServerHandlerForClients extends Thread {
        private final Socket client_socket;


        public ServerHandlerForClients(Socket client_socket) {
            Objects.requireNonNull(client_socket);
            this.client_socket = client_socket;
        }

        /**
         * Знакомство сервера с клиентом.
         * 1)Обмен рукопожатиями - запрос имени клиента.
         * 2)Оповещение клиента о всех, кто уже подключен.
         * 3)Запуск бесконечного цикла для клиента (ожидание от него входящих сообщений и отправка их
         * бродкастом.
         * <p>
         * 4)Удаление клиента из мапы клиентов, при дисконнекте клиента.
         */
        @Override
        public void run() {
            System.out.println("New connection for new client: " + client_socket.getRemoteSocketAddress());
            String userName = null;

            try (Connection connection_client = new Connection(client_socket)) {
                userName = this.serverHandShake(connection_client);

                Server.broadcastForAllClients(new Message(MessageType.USER_ADD, userName));
                notifyUser(connection_client, userName);
                serverMainLoopForClient(connection_client, userName);

            } catch (IOException e) {
                e.printStackTrace();
            }
            if (userName != null) {
                CLIENT_MAP.remove(userName);
                Server.broadcastForAllClients(new Message(MessageType.USER_DELETE, userName));
                System.out.println("Connection for " + userName + " has been closed.");
            }
        }

        /**
         * Метод обеспечивает запрос в сторону клиента, подразумевает запрос имени клиента и
         * проверку ответа от клиента на 'пустое' имя и 'null' + проверка на корректный тип сообщения,
         * так как введен протокол обмена сообщениями (enum MessageType).
         */
        public String serverHandShake(Connection client_connection) {
            final Message REQUEST_NAME = new Message(MessageType.NAME_REQUEST);
            Message messageFromClient;

            System.out.println("User name request...");
            client_connection.sendMessage(REQUEST_NAME);
            while (true) {
                try {
                    messageFromClient = client_connection.receive();
                    String clientName = messageFromClient.getData();
                    MessageType messageType = messageFromClient.getType();
                    if (clientName == null || clientName.equals("") || messageType != MessageType.USER_NAME) {
                        System.out.println("Repeated request for client name...");
                        client_connection.sendMessage(REQUEST_NAME);
                    } else {
                        System.out.println("Name " + "'" + clientName + "' was ACCEPTED");
                        CLIENT_MAP.put(clientName, client_connection);
                        client_connection.sendMessage(new Message(MessageType.NAME_ACCEPTED));
                        return clientName;
                    }
                } catch (IOException | ClassNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }
    }
//----------------------------------------------------------------------------------------------------------

    /**
     * Основной метод для ожидания сообщения от порта клиента и рассылки его всем,
     * бесконечный цикл.
     */
    private void serverMainLoopForClient(Connection connection, String userName) {
        while (true) {
            try {
                Message messageFromClient = connection.receive();
                if (messageFromClient != null && messageFromClient.getType() == MessageType.TEXT) {
                    String newMessage = userName + ": " + messageFromClient.getData(); //что бы было видно от кого
                    Server.broadcastForAllClients(new Message(MessageType.TEXT, newMessage));
                }
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
                return;
            }
        }
    }

    /**
     * Бродкаст отправка сообщения всем из мапы
     */
    private static void broadcastForAllClients(Message messageForAll) {
        if (CLIENT_MAP.isEmpty()) {
            System.out.println("No clients in map !");
            return;
        }
        for (Map.Entry<String, Connection> entry : CLIENT_MAP.entrySet()) {
            (entry.getValue()).sendMessage(messageForAll);
        }
    }

    /**
     * Оповещаем юзера из аргумента о всех юзерах из мапы
     */
    private void notifyUser(Connection connection_client, String userName) {
        for (Map.Entry<String, Connection> entry : CLIENT_MAP.entrySet()) {
            if (!(entry.getKey()).equals(userName))
                connection_client.sendMessage(new Message(MessageType.USER_ADD, entry.getKey()));
        }
    }
}
