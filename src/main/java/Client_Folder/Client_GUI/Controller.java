package Client_Folder.Client_GUI;

import Client_Folder.Client_Console_MAIN.Client_Console;

/*
Смежное звено между моделью и уровнем представления
 */
public class Controller extends Client_Console {
    private final Model model = new Model();
    private final Viewer viewer = new Viewer(this);

    public static void main(String[] args) {
        //TODO
    }
//------------------------------------------------------------------------------------------

//------------------------------------------------------------------------------------------


    public Model getModel() {
        return model;
    }
}
