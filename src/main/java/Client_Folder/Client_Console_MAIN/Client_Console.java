package Client_Folder.Client_Console_MAIN;

import Connection_AND_ConsoleHelper.Connection;
import Connection_AND_ConsoleHelper.ConsoleHelper;
import Message.Message;
import Message.MessageType;

import java.io.IOException;
import java.net.Socket;

public class Client_Console {
    protected Connection connectionWithServer;
    protected boolean connection_is_established = false; //статус соединения


    public static void main(String[] args) {
        Client_Console client_console = new Client_Console();
        client_console.start();
    }


    //----------------------------------------------------------------------------------------------------------
    protected class ProcessConnectionWithServer extends Thread {

        @Override
        public void run() {
            System.out.println("Running client...");

            String server_address = Client_Console.this.getServerAddress();
            Integer server_port = Client_Console.this.getServerPort();

            try {
                connectionWithServer = new Connection(new Socket(server_address, server_port));

                clientHandShake(connectionWithServer);
                mainClientLoopForMessageFromServer();
            } catch (IOException | ClassNotFoundException e) {
                setConnectionStatus(false);
                e.printStackTrace();
            }
        }


        /**
         * Метод для установки соединения с сервером, обмен первичными рукопожатиями.
         * <p>
         * Ожидает ответа/запроса от сервера.
         * <p>
         * В случае успешного 'знакомства' поле 'connection_is_established' установится в true и произойдет
         * выход из цикла.
         *
         * @param server_connection текущий клиент, вернее его соединение в сторону сервера
         */
        protected void clientHandShake(Connection server_connection) {
            String userName;
            Message messageFromServer;
            while (true) {
                try {
                    messageFromServer = server_connection.receive();
                    if (messageFromServer != null) {
                        final MessageType typeMessageFromServer = messageFromServer.getType();
                        if (typeMessageFromServer == MessageType.NAME_REQUEST) {
                            System.out.print("Enter userName: ");
                            userName = ConsoleHelper.getStringFromConsole();
                            server_connection.sendMessage(new Message(MessageType.USER_NAME, userName));
                        } else {
                            if (typeMessageFromServer == MessageType.NAME_ACCEPTED) {
                                System.out.println("Name accepted !");
                                this.setConnectionStatus(true);
                                return;
                            } else throw new IOException("SomeProblemsWithConnection");
                        }
                    }
                } catch (IOException | ClassNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }

        /**
         * Метод содержит бесконечный цикл, для ожидания сообщений от сервера, в зависимости от
         * типа принятого сообщения - будет произведен вывод соответствующей информации (в этом случае в консоль) с помощью
         * соответствующих методов...
         */
        protected void mainClientLoopForMessageFromServer() throws IOException, ClassNotFoundException {
            Message messageFromServer;

            while (true) {
                messageFromServer = connectionWithServer.receive();
                if (messageFromServer != null) {
                    MessageType messageFromServerType = messageFromServer.getType();
                    if (messageFromServerType == MessageType.TEXT) {
                        incomingMessage(messageFromServer);
                    } else {
                        if (messageFromServerType == MessageType.USER_ADD) {
                            incomingMessageAddUser(messageFromServer);
                        } else if (messageFromServerType == MessageType.USER_DELETE) {
                            incomingMessageRemoveUser(messageFromServer);
                        } else {
                            connectionWithServer.close();
                            throw new IOException("Something wrong with message");
                        }
                    }
                }
            }
        }

        /**
         * Вывод сообщения с сервера в консоль.
         */
        protected void incomingMessage(Message messageFromServer) {
            String message = messageFromServer.getData();
            ConsoleHelper.printInConsole(message);
        }

        /**
         * Вывод инфы о добавленном юзере в консоль
         */
        protected void incomingMessageAddUser(Message messageFromServer) {
            String message = messageFromServer.getData();
            System.out.println("New user '" + messageFromServer.getData() + "' connected");
        }

        /**
         * Вывод инфы об удаленном юзере
         */
        protected void incomingMessageRemoveUser(Message messageFromServer) {
            String message = messageFromServer.getData();
            System.out.println("User '" + message + "' was deleted");
        }


        /**
         * Меняем статус соединения на переданный в аргументе.
         * <p>
         * + Освобождаем нить главного потока объекта Client_Console.this
         */
        protected void setConnectionStatus(boolean status) {
            synchronized (Client_Console.this) {
                Client_Console.this.connection_is_established = status;
                Client_Console.this.notify();
            }
        }
    }
    //----------------------------------------------------------------------------------------------------------

    /**
     * Основной метод для запуска клиента
     */
    protected void start() {
        synchronized (this) {
            ProcessConnectionWithServer connection = getConnectionWithServer();
            connection.setDaemon(true);
            connection.start();

            try {
                this.wait();
                if (connection_is_established) {
                    while (true) {
                        Thread.sleep(3);
                        System.out.println("Type your message:");
                        String data = ConsoleHelper.getStringFromConsole();
                        if (data.equals("exit") || data.isEmpty())
                            break;
                        Message newMessageForServer = new Message(MessageType.TEXT, data);
                        connectionWithServer.sendMessage(newMessageForServer);
                    }
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Возвращает новый экземпляр внутреннего класса
     */
    protected ProcessConnectionWithServer getConnectionWithServer() {
        return new ProcessConnectionWithServer();
    }

    /**
     * Запрос адреса сервера (ввод в консоль)
     */
    private String getServerAddress() {
        System.out.print("Enter server address: ");
        return ConsoleHelper.getStringFromConsole();
    }

    /**
     * Запрос порта сервера (ввод в консоль)
     */
    private Integer getServerPort() {
        System.out.print("Enter server port: ");
        return ConsoleHelper.getIntFromConsole();
    }
}