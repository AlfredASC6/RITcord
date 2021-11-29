import javafx.application.Application;
import javafx.event.*;
import javafx.scene.*;
import javafx.scene.text.*;
import javafx.scene.control.*;
import javafx.scene.control.Alert.*;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.*;
import javafx.stage.*;
import javafx.geometry.*;
import java.util.*;
import java.io.*;
import java.net.*;
import javafx.event.ActionEvent.*;
import javafx.event.EventHandler.*;

/**
 * Client - a class that displays a log in page and moderate chats
 * @author A.Franco (add names if you contributed)
 * @version 11-29-21
 */
public class Client extends Application{
   private Stage stage;
   private Scene scene;
   private VBox root = new VBox(8);
   private TextArea chat = new TextArea();
   private TextField tfServer = new TextField();
   private Button send = new Button("Send");
   private String serverIP;
   
   private Socket socket = null;
   private Scanner in;
   private PrintWriter pwt;
   private int SERVER_PORT = 32323;
   
   public static void main(String[] args){
      launch(args);
   }
   
   public void start(Stage _stage){
      stage = _stage;
      stage.setTitle("RITcord");
      //TilePane r = new TilePane();
      TextInputDialog td = new TextInputDialog();
      
      
      //sendUserInfo();
      EventHandler<ActionEvent> event = new EventHandler<ActionEvent>() {
         public void handle(ActionEvent e){
             td.showAndWait();  
         }};
         
      root.getChildren().addAll(send, chat, tfServer);
      scene = new Scene(root, 400, 250);
      stage.setScene(scene);
      stage.show();
   }
   public void sendUserInfo(){
      
   }
}