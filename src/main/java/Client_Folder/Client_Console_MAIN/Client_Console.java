package Client_Folder.Client_Console_MAIN;

import Connection_AND_ConsoleHelper.Connection;
import Connection_AND_ConsoleHelper.ConsoleHelper;
import Message.Message;
import Message.MessageType;

import java.io.IOException;

public class Client_Console {
    protected Connection connectionWithServer;
    protected boolean connection_is_established = false; //статус соединения


    public static void main(String[] args) {
        Client_Console client_console = new Client_Console();
        client_console.start();
    }


    //----------------------------------------------------------------------------------------------------------
    protected class Client_Console_Connection_With_Server extends Thread {
        //TODO установка соединения с сервером



        @Override
        public void run() {
            //TODO коннект к серверу, основной метод
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

        /***/
        protected void mainClientLoopForMessageFromServer(){
            Message messageFromServer;
            while (true){
                try {
                    messageFromServer = connectionWithServer.receive();
                    if (messageFromServer != null){
                        MessageType messageFromServerType = messageFromServer.getType();
                        if (messageFromServerType == MessageType.TEXT){
        //TODO
                        }
                    }
                } catch (IOException | ClassNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }

        /**Вывод сообщения с сервера в консоль.*/
        protected void incomingMessage(Message messageFromServer){
            String message = messageFromServer.getData();
            ConsoleHelper.printInConsole(message);
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


        //TODO дописать методы для вывода сообщений с сервера в консоль
    }
    //----------------------------------------------------------------------------------------------------------

    /**
     * Основной метод для запуска клиента
     */
    protected void start() {
        //TODO
    }

    /**
     * Возвращает новый экземпляр внутреннего класса
     */
    protected Client_Console_Connection_With_Server getConnectionWithServer() {
        return new Client_Console_Connection_With_Server();
    }

    //TODO дописать методы для ожидания воода в консоль и отправки на сервер
}
