import javafx.application.Application;
import javafx.event.*;
import javafx.scene.*;
import javafx.scene.text.*;
import javafx.scene.control.*;
import javafx.scene.control.Alert.*;
import javafx.scene.layout.*;
import javafx.stage.*;
import javafx.stage.FileChooser.*;
import javafx.geometry.*;
import java.util.*;
import java.io.*;
import java.net.*;

/**
 * Server - a server that handles the messages of a chat application
 * @author A.Franco (if you contributed add your name here)
 * @version 11-29-21
 */

public class Server extends Application{
   private Stage stage;
   private Scene scene;
   private VBox root = new VBox(8);
   private TextArea taLog = new TextArea();
   private Button button = new Button("Start");
   
   private int SERVER_PORT = 32323;
   private PrintWriter pwt = null;
   private PrintWriter pwt2 = null;
   private File savedChat = null;
   private File passwordFile;
   private ServerThread serverThread = null;
   
   private ServerSocket sSocket = null;
   
   public static void main(String[] args){
      launch(args);
   }   
   
   public void start(Stage _stage){
      stage = _stage;
      stage.setTitle("Server Side");
      
      root.getChildren().add(taLog);
      stage.setOnCloseRequest(new EventHandler<WindowEvent>() {
         public void handle(WindowEvent evt) { System.exit(0); }
      });
      taLog.setEditable(false);
      
      scene = new Scene(root, 400, 250);
      stage.setScene(scene);
      stage.show();
   }
   
   public void handle(ActionEvent evt){
      String label = ((Button)evt.getSource()).getText();
      
      switch(label){
         case "Start":
            doStart();
            break;
      }
   }
   
   public void doStart(){
      serverThread = new ServerThread();
      serverThread.start();
   }
   
   class ServerThread extends Thread{
      public void run(){
         try{
            sSocket = new ServerSocket(SERVER_PORT);
         }
         catch(Exception e){
            System.out.println("Excpetion " + e);
         }
      }//end of run
   }//end of ServerThread
   
   class ClientThread extends Thread {
      private Socket cSocket;
      private String clientId = "";

      public ClientThread(Socket _cSocket) {
         cSocket = _cSocket;
         clientId = cSocket.getInetAddress().getHostAddress() + ":" + cSocket.getPort();
      }
      
      // main program for a ClientThread
      public void run() {
         Scanner scn = null;
         PrintWriter pwt = null;
         
         System.out.println(clientId + " Client connected!\n");
         
         try {
            // Open streams 
            scn = new Scanner(new InputStreamReader(cSocket.getInputStream()));
            pwt = new PrintWriter(new OutputStreamWriter(cSocket.getOutputStream()));
         }
         catch(IOException ioe) {
            System.out.println(clientId + " IO Exception (ClientThread): " + ioe + "\n");
            return;
         }

         
         System.out.println(clientId + " Client disconnected!\n");
      }//end of run
   }// end of ClientThread
}