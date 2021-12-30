package Client_Folder.Client_Bot;

import Client_Folder.Client_Console_MAIN.Client_Console;
import Connection_AND_ConsoleHelper.ConsoleHelper;
import Message.Message;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Random;

public class Client_Bot extends Client_Console {

    public static void main(String[] args) {
        Client_Bot client_bot = new Client_Bot();
        client_bot.start();
    }

    //----------------------------------------------------------------------------------------------------------
    public class ProcessConnectionWithServerBot extends ProcessConnectionWithServer {
        @Override
        protected void mainClientLoopForMessageFromServer() throws IOException, ClassNotFoundException {
            System.out.println("****************************************************************\n" +
                    "I'm a BOT, I know next commands:\n" +
                    "[date][day][month][year][time][hour][minutes][seconds].\n" +
                    "****************************************************************");
            super.mainClientLoopForMessageFromServer();
        }


        @Override
        protected void incomingMessage(Message messageFromServer) {
            ConsoleHelper.printInConsole(messageFromServer.getData());
            SimpleDateFormat dateFormat = null;
            Calendar data = null;


            String[] nameAndMessage = messageFromServer.getData().split(": ");
            if (nameAndMessage.length != 2)
                return;

            String user_name = nameAndMessage[0];
            String message_command = nameAndMessage[1];

            if (message_command.equalsIgnoreCase("date") || message_command.equalsIgnoreCase("дата")) {
                dateFormat = new SimpleDateFormat("d.MM.yyyy");
                data = Calendar.getInstance();
            } else if (message_command.equalsIgnoreCase("day") || message_command.equalsIgnoreCase("день")) {
                dateFormat = new SimpleDateFormat("d");
                data = Calendar.getInstance();
            } else if (message_command.equalsIgnoreCase("month") || message_command.equalsIgnoreCase("месяц")) {
                dateFormat = new SimpleDateFormat("MMMM");
                data = Calendar.getInstance();
            } else if (message_command.equalsIgnoreCase("year") || message_command.equalsIgnoreCase("год")) {
                dateFormat = new SimpleDateFormat("yyyy");
                data = Calendar.getInstance();
            } else if (message_command.equalsIgnoreCase("time") || message_command.equalsIgnoreCase("время")) {
                dateFormat = new SimpleDateFormat("H:mm:ss");
                data = Calendar.getInstance();
            } else if (message_command.equalsIgnoreCase("hour") || message_command.equalsIgnoreCase("час")) {
                dateFormat = new SimpleDateFormat("H");
                data = Calendar.getInstance();
            } else if (message_command.equalsIgnoreCase("minutes") || message_command.equalsIgnoreCase("минуты")) {
                dateFormat = new SimpleDateFormat("m");
                data = Calendar.getInstance();
            } else if (message_command.equalsIgnoreCase("seconds") || message_command.equalsIgnoreCase("секунды")) {
                dateFormat = new SimpleDateFormat("s");
                data = Calendar.getInstance();
            }
            if (dateFormat != null && data != null) {
                Client_Bot.this.sendTextMessage("INFO for " + user_name + ": "
                        + dateFormat.format(data.getTime()));
                //TODO: тут баг, так как не смотря на флаг все равно отправим на сервак
            }
        }
    }
    //----------------------------------------------------------------------------------------------------------


    @Override
    protected String getUserName() {
        return "chat_BOT_#" + new Random().nextInt(100);
    }

    @Override
    protected ProcessConnectionWithServerBot getConnectionWithServer() {
        return new ProcessConnectionWithServerBot();
    }

    @Override
    protected boolean shouldSendTextFromConsole() {
        //не будем отправлять сообщения на сервер
        return false;
    }
}
