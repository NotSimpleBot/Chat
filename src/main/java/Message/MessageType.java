package Message;

//это ПРОТОКОЛ, нужен для указания типа сообщения, в соответствии с типом будет происходить соответственная обработка соощений Message
public enum MessageType {
    NAME_REQUEST, NAME_ACCEPTED, USER_NAME,
    TEXT,
    USER_ADD, USER_DELETE
}
