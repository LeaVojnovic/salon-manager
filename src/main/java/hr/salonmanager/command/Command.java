package hr.salonmanager.command;

/** Command s izvršenjem i poništavanjem operacije. */
public interface Command {
    void execute();
    void undo();
}
