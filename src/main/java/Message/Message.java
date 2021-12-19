package Message;

import java.io.Serializable;

//Serializable так как будем передавать по сети сообщения в видео объектов.
public class Message implements Serializable {
    private final MessageType type;
    private final String data;

    //конструкторы
    public Message(MessageType type, String data) {
        if (data == null || data.isEmpty())
            throw new IllegalArgumentException("Null or empty data !");

        this.type = type;
        this.data = data;
    }
    public Message(MessageType type){
        this.type = type;
        this.data = null;
    }


    //геттеры
    public MessageType getType() {
        return type;
    }

    public String getData() {
        return data;
    }
}
