package Connection_AND_ConsoleHelper;


import Message.Message;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Objects;

//обертка для Socket, что бы работать с сериализацией
public class Connection {
    private final Socket socket;
    private final ObjectOutputStream oos;
    private final ObjectInputStream ois;


    public Connection(Socket socket) throws IOException {
        Objects.requireNonNull(socket);
        this.socket = socket;
        this.oos = new ObjectOutputStream(socket.getOutputStream());
        this.ois = new ObjectInputStream(socket.getInputStream());
    }


    //отправить
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

    //получить
    public Message receive() throws IOException, ClassNotFoundException {
        synchronized (ois) {
            return (Message) ois.readObject();
        }
    }
}

