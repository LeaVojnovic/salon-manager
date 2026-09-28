package hr.salonmanager.command;

/** Čuva i poništava posljednju promjenu tijekom trenutne sesije. */
public class CommandInvoker {
    private Command lastCommand;

    public void executeCommand(Command command) {
        command.execute();
        lastCommand = command;
    }

    public void undoLast() {
        if (lastCommand == null) {
            throw new IllegalStateException("Nema promjene koju je moguće poništiti.");
        }
        lastCommand.undo();
        lastCommand = null;
    }

    public boolean canUndo() {
        return lastCommand != null;
    }
}
