package Client_Folder.Client_GUI;

import java.util.HashSet;
import java.util.Set;

/*
Вся бизнес логика тут, не знает о вьювере и контроллере
 */
public class Model {
    private final Set<String> ALL_USERS = new HashSet<>();
    private String MESSAGE_NEW;

    public void add_user_is_ALL_USER(String userNew) {
        ALL_USERS.add(userNew);
    }

    public void remove_user_from_ALL_USER(String userNew) {
        ALL_USERS.remove(userNew);
    }

    public Set<String> get_ALL_USERS() {
        return ALL_USERS;
    }

    public String getMESSAGE_NEW() {
        return MESSAGE_NEW;
    }

    public void setMESSAGE_NEW(String MESSAGE_NEW) {
        this.MESSAGE_NEW = MESSAGE_NEW;
    }
}
