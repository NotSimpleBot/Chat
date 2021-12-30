package Client_Folder.Client_GUI;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/*
Класс отвечает за представление ГУИ интерфейса клиенту и отправку команд через
контроллер в модель
 */
public class Viewer {
    private final Controller controller;

    private final JFrame frame;
    private final JTextField textField;
    private final JTextArea messages;
    private final JTextArea users;


    public Viewer(Controller controller) {
        this.controller = controller;

        frame = new JFrame("Чат");
        textField = new JTextField(50);
        messages = new JTextArea(10, 50);
        users = new JTextArea(10, 10);

        initView();
    }

    //инициализация вьювера, доступ к модели через контроллер
    private void initView() {
        textField.setEditable(false);
        messages.setEditable(false);
        users.setEditable(false); //делаем изменяемыми

        frame.getContentPane().add(textField, BorderLayout.NORTH);
        frame.getContentPane().add(new JScrollPane(messages), BorderLayout.WEST);
        frame.getContentPane().add(new JScrollPane(users), BorderLayout.EAST);
        frame.pack();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);

        textField.addActionListener((event) -> {
            controller.sendTextMessage(textField.getText());
            textField.setText("");
        });
    }

    //всплывающее окно с запросом адреса сервера
    public String getServerAddress() {
        return JOptionPane.showInputDialog(
                frame,
                "Введите адрес сервера:",
                "Конфигурация клиента",
                JOptionPane.QUESTION_MESSAGE);
    }

    //всплывающее окно с запросом порта сервера
    public int getServerPort() {
        while (true) {
            String port = JOptionPane.showInputDialog(
                    frame,
                    "Введите порт сервера:",
                    "Конфигурация клиента",
                    JOptionPane.QUESTION_MESSAGE);
            try {
                return Integer.parseInt(port.trim());
            } catch (Exception e) {
                JOptionPane.showMessageDialog(
                        frame,
                        "Был введен некорректный порт сервера. Попробуйте еще раз.",
                        "Конфигурация клиента",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    //всплывающее окно с запросом имени пользователя
    public String getUserName() {
        return JOptionPane.showInputDialog(
                frame,
                "Введите ваше имя:",
                "Конфигурация клиента",
                JOptionPane.QUESTION_MESSAGE);
    }

    //окно оповещающее об успешном/не успешном соединении
    public void notifyConnectionStatusChanged(boolean clientConnected) {
        textField.setEditable(clientConnected);
        if (clientConnected) {
            JOptionPane.showMessageDialog(
                    frame,
                    "Соединение с сервером установлено",
                    "Чат",
                    JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(
                    frame,
                    "Клиент не подключен к серверу",
                    "Чат",
                    JOptionPane.ERROR_MESSAGE);
        }

    }

    //вывод нового сообщение от сервака
    public void refreshMessages() {
        messages.append(controller.getModel().getNewMessage() + "\n");
    }

    //выводим всех доступных пользователей
    public void refreshUsers() {
        Model model = controller.getModel();
        StringBuilder sb = new StringBuilder();
        for (String userName : model.getAllUserNames()) {
            sb.append(userName).append("\n");
        }
        users.setText(sb.toString());
    }
}
