package Client_Folder.Client_GUI;

import Client_Folder.Client_Console_MAIN.Client_Console;
import Message.Message;

/*
Смежное звено между моделью и уровнем представления
 */
public class Controller extends Client_Console {
    private final Model MODEL = new Model();
    private final Viewer VIEWER = new Viewer(this);

    public static void main(String[] args) {
        Controller controller = new Controller();
        controller.start();
    }

    @Override
    protected void start() {
        getConnectionWithServer().run(); //!!!
    }

    //------------------------------------------------------------------------------------------
    public class ProcessConnectionWithServerGUI extends ProcessConnectionWithServer {
        @Override
        protected void incomingMessage(Message messageFromServer) {
            MODEL.setMESSAGE_NEW(messageFromServer.getData());
            VIEWER.refreshMessages();
        }

        @Override
        protected void incomingMessageAddUser(Message messageFromServer) {
            MODEL.add_user_is_ALL_USER(messageFromServer.getData());
            VIEWER.refreshUsers();
        }

        @Override
        protected void incomingMessageRemoveUser(Message messageFromServer) {
            MODEL.remove_user_from_ALL_USER(messageFromServer.getData());
            VIEWER.refreshUsers();
        }

        @Override
        protected void setConnectionStatus(boolean status) {
            VIEWER.notifyConnectionStatusChanged(status);
        }
    }
//------------------------------------------------------------------------------------------

    @Override
    protected ProcessConnectionWithServer getConnectionWithServer() {
        return new ProcessConnectionWithServerGUI();
    }

    @Override
    protected String getServerAddress() {
        return VIEWER.getServerAddress();
    }

    @Override
    protected Integer getServerPort() {
        return VIEWER.getServerPort();
    }

    @Override
    protected String getUserName() {
        return VIEWER.getUserName();
    }

    public Model getMODEL() {
        return MODEL;
    }
}
