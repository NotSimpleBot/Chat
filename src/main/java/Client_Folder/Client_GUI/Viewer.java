package Client_Folder.Client_GUI;

import javax.swing.*;
import java.awt.*;

/*
Класс отвечает за представление ГУИ интерфейса клиенту и отправку команд через
контроллер в модель
 */
public class Viewer {
    private final Controller CONTROLLER;

    private final JFrame FRAME_MAIN;
    private final JTextField MY_MESSAGE_TEXT;
    private final JTextArea ALL_MESSAGES_TEXT;
    private final JTextArea ALL_USERS_LIST;


    public Viewer(Controller controller) {
        this.CONTROLLER = controller;

        FRAME_MAIN = new JFrame("Чат");
        MY_MESSAGE_TEXT = new JTextField(50);
        ALL_MESSAGES_TEXT = new JTextArea(10, 50);
        ALL_USERS_LIST = new JTextArea(10, 10);

        initView();
    }

    //инициализация вьювера, доступ к модели через контроллер
    private void initView() {
        MY_MESSAGE_TEXT.setEditable(false);
        ALL_MESSAGES_TEXT.setEditable(false);
        ALL_USERS_LIST.setEditable(false); //делаем изменяемыми

        FRAME_MAIN.getContentPane().add(MY_MESSAGE_TEXT, BorderLayout.NORTH);
        FRAME_MAIN.getContentPane().add(new JScrollPane(ALL_MESSAGES_TEXT), BorderLayout.WEST);
        FRAME_MAIN.getContentPane().add(new JScrollPane(ALL_USERS_LIST), BorderLayout.EAST);

        FRAME_MAIN.pack(); //размеры окон подстраиваются под текст
        FRAME_MAIN.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        FRAME_MAIN.setVisible(true);

        MY_MESSAGE_TEXT.addActionListener((event) -> {
            CONTROLLER.sendTextMessage(MY_MESSAGE_TEXT.getText()); //TODO: может быть баг с отображением
            MY_MESSAGE_TEXT.setText("");
        });
    }

    /**
     * Всплывающее окно с запросом адреса сервера
     */
    public String getServerAddress() {
        return JOptionPane.showInputDialog(
                FRAME_MAIN,
                "Введите адрес сервера:",
                "Конфигурация клиента",
                JOptionPane.QUESTION_MESSAGE);
    }

    /**
     * Всплывающее окно с запросом порта сервера
     */
    public int getServerPort() {
        while (true) {
            String port = JOptionPane.showInputDialog(
                    FRAME_MAIN,
                    "Введите порт сервера:",
                    "Конфигурация клиента",
                    JOptionPane.QUESTION_MESSAGE);
            try {
                return Integer.parseInt(port.trim());
            } catch (Exception e) {
                JOptionPane.showMessageDialog(
                        FRAME_MAIN,
                        "Был введен некорректный порт сервера. Попробуйте еще раз.",
                        "Конфигурация клиента",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    //всплывающее окно с запросом имени пользователя
    public String getUserName() {
        return JOptionPane.showInputDialog(
                FRAME_MAIN,
                "Введите ваше имя:",
                "Конфигурация клиента",
                JOptionPane.QUESTION_MESSAGE);
    }

    //окно оповещающее об успешном/не успешном соединении
    public void notifyConnectionStatusChanged(boolean clientConnected) {
        MY_MESSAGE_TEXT.setEditable(clientConnected);
        if (clientConnected) {
            JOptionPane.showMessageDialog(
                    FRAME_MAIN,
                    "Соединение с сервером установлено",
                    "Чат",
                    JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(
                    FRAME_MAIN,
                    "Клиент не подключен к серверу",
                    "Чат",
                    JOptionPane.ERROR_MESSAGE);
        }

    }

    //вывод нового сообщение от сервака
    public void refreshMessages() {
        ALL_MESSAGES_TEXT.append(CONTROLLER.getMODEL().getMESSAGE_NEW() + "\n");
    }

    //выводим всех доступных пользователей
    public void refreshUsers() {
        Model model = CONTROLLER.getMODEL();
        StringBuilder sb = new StringBuilder();
        for (String userName : model.get_ALL_USERS()) {
            sb.append(userName).append("\n");
        }
        ALL_USERS_LIST.setText(sb.toString());
    }
}
