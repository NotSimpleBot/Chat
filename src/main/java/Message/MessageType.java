package Message;

//это ПРОТОКОЛ, нужен для указания типа сообщения
public enum MessageType {
    NAME_REQUEST, NAME_ACCEPTED, USER_NAME,
    TEXT,
    USER_ADD, USER_DELETE;
}
