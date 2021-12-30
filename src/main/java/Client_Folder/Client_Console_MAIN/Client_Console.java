package Client_Folder.Client_Console_MAIN;

import Connection_AND_ConsoleHelper.Connection;
import Connection_AND_ConsoleHelper.ConsoleHelper;
import Message.Message;
import Message.MessageType;

import java.io.IOException;
import java.net.Socket;
import java.util.concurrent.TimeUnit;

/*
Класс отвечает за:
1) Установку соединения с сервером через внутренний класс;
2) Бесконечный цикл ожидания ввода сообщения от клиента и отправка его на сервер.
 */
public class Client_Console {
    protected Connection connectionWithServer;
    protected boolean connection_is_established = false; //статус соединения


    public static void main(String[] args) {
        Client_Console client_console = new Client_Console();
        client_console.start();
    }


    //----------------------------------------------------------------------------------------------------------
    protected class ProcessConnectionWithServer extends Thread {
        /*
        Класс отвечает за:
        1) Установка соединения с сервером;
        2) Бесконечное ожидание сообщений от сервера;
        3) Вывод данных о сообщениях в консоль.
         */

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
                            userName = Client_Console.this.getUserName();
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
                while (!connection_is_established)
                    this.wait(); //демон выше должен пробудить (если установит соединение с сервером)
                TimeUnit.SECONDS.sleep(1);

                System.out.println("Type your first message:");
                while (true) {
                    TimeUnit.SECONDS.sleep(1);
                    System.out.println("Type your message:");

                    String data = ConsoleHelper.getStringFromConsole();
                    if (data.equals("exit") || data.isEmpty())
                        break;
                    if (shouldSendTextFromConsole())
                        sendTextMessage(data);
                }

            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Отправить сообщение серверу
     */
    public void sendTextMessage(String messageForServer) {
        System.out.println("---------------------------------------------------------\n" +
                "New message: ");
        try {
            connectionWithServer.sendMessage(new Message(MessageType.TEXT, messageForServer));
        } catch (Exception e) {
            e.printStackTrace();
            connection_is_established = false;
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
    protected String getServerAddress() {
        System.out.print("Enter server address: ");
        return ConsoleHelper.getStringFromConsole();
    }

    /**
     * Запрос порта сервера (ввод в консоль)
     */
    protected Integer getServerPort() {
        System.out.print("Enter server port: ");
        return ConsoleHelper.getIntFromConsole();
    }

    /**
     * Запрос имени пользователя (ввод в консоль)
     */
    protected String getUserName() {
        System.out.print("Enter user name: ");
        return ConsoleHelper.getStringFromConsole();
    }

    /**
     * Будем отправлять сообщение от клиента на сервер ?
     * <p>
     * Актуально для бота, например.
     *
     * @return true/false - будем/не будем.
     */
    protected boolean shouldSendTextFromConsole() {
        return true;
    }
}