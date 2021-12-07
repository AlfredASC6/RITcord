import javafx.scene.control.TextArea;

public class MyLogger {
    public final TextArea textArea;

    private boolean verboseMode = false;
    private boolean debugMode = false;

    public MyLogger(TextArea textArea) {
        this.textArea = textArea;
    }

    public MyLogger setVerboseMode(boolean value) {
        verboseMode = value;
        return this;
    }

    public MyLogger setDebugMode(boolean value) {
        debugMode = value;
        return this;
    }

    public boolean writeMessage(String msg) {
        textArea.appendText(msg);
        return true;
    }

    public boolean logMessage(String msg) {
        return writeMessage(msg + "n");
    }
}