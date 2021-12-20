package Connection_AND_ConsoleHelper;


import Message.Message;

import java.io.Closeable;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Objects;

//обертка для Socket, так как через Сокеты будем передавать объекты, то нужно сериализовать
public class Connection implements Closeable {
    private final Socket socket;
    private final ObjectOutputStream oos;
    private final ObjectInputStream ois;


    public Connection(Socket socket) throws IOException {
        Objects.requireNonNull(socket);
        this.socket = socket;
        this.oos = new ObjectOutputStream(socket.getOutputStream());
        this.ois = new ObjectInputStream(socket.getInputStream());
    }


    /**
     * Отправка сообщения (сериализованного объекта Message)
     */
    public void sendMessage(Message message) {
        synchronized (oos) {
            try {
                oos.writeObject(message);
            } catch (IOException e) {
                System.out.println("E--->Connection/sendMessage(...):");
                e.printStackTrace();
            }
        }
    }

    /**
     * Получение сообщения (десириализация объекта Message)
     */
    public Message receive() throws IOException, ClassNotFoundException {
        synchronized (ois) {
            return (Message) ois.readObject();
        }
    }


    @Override
    public void close() throws IOException {
        ois.close();
        oos.close();
        socket.close();
    }
}

