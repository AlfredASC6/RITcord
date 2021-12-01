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
 * @author A.Franco, AErskine, W.Celentano (add names if you contributed)
 * @version 12-1-21
 */
public class Client extends Application{

   private Stage stage;
   private Scene scene;
   private VBox root = new VBox(8);

   private TextArea taChat = new TextArea();
   private TextField tfMsg = new TextField();
   private Label lblMsg = new Label("Message");
   private Button btnSend = new Button("Send");
  
   private String serverIP;   
   private Socket socket = null;
   private Scanner scn;
   private PrintWriter pwt;
   private int SERVER_PORT = 32323;
   
   static String username;
   static String password;
   
   public static void main(String[] args){
      launch(args);
   }
   
   public void start(Stage _stage){
      stage = _stage;
      stage.setTitle("RITcord");
      
      //open login window
      doLogin();
      
      
      FlowPane fpTop = new FlowPane(8,8);
      fpTop.getChildren().addAll(taChat);
      fpTop.setAlignment(Pos.CENTER);
      
      FlowPane fpMid = new FlowPane(8, 8);
      fpMid.getChildren().addAll(lblMsg, tfMsg);
      fpMid.setAlignment(Pos.CENTER);
      tfMsg.setPrefColumnCount(20);
      
      tfMsg.setOnAction(new EventHandler<WindowEvent>() {
         public void handle(WindowEvent evt) {
            //doSendMsg
         }
      });
      
      root.getChildren().addAll(fpTop, fpMid, btnSend);
      scene = new Scene(root, 500, 350);
      stage.setScene(scene);
      stage.show();
      
      stage.setOnCloseRequest(new EventHandler<WindowEvent>() {
         public void handle(WindowEvent evt) {
            //doDisconnect();
         }
      });
   }//end of start
   
   private void doDisconnect(){
      try{
         scn.close();
         pwt.close();
         socket.close();
      }
      catch(IOException ioe){
         Alert alert = new Alert(AlertType.ERROR, "Exception " + ioe);
         alert.showAndWait(); 
      }
   }//end doDisconnect()
   
   public void doConnect(){
      try{
         socket = new Socket(serverIP, SERVER_PORT);
         scn = new Scanner(new InputStreamReader(socket.getInputStream()));
         pwt = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()));
      }
      catch(IOException ioe){
         Alert alert = new Alert(AlertType.ERROR, "Exception " + ioe);
         alert.showAndWait(); 
      }
   }//end of doConnect()
   
   private void sendMsg(){}
   
   
   private void sendUserInfo(){
      //send username and password 
      pwt.println(username + "#" + password);
      pwt.flush();
   }
   
   
   /*
   * doLogin Method: 
   * Create stage to send username and password to server 
   */
   private void doLogin(){
        Stage loginStage = new Stage();
        loginStage.initModality(Modality.APPLICATION_MODAL);
         
        TextField tfUser = new TextField();
        TextField tfPass = new TextField();
        TextField tfServer = new TextField();
        
        Button btnSubmit = new Button("Submit");
        btnSubmit.setDefaultButton(true);
        
        btnSubmit.setOnAction(new EventHandler<ActionEvent>(){
         public void handle(ActionEvent e){
            //send user info here 
            username = tfUser.getText();
            password = tfPass.getText();
            
            serverIP = tfServer.getText();
            
            //doConnect();
            // sendUserInfo();
            
            loginStage.close();
            
         }
        });
        
     
        Label label1 = new Label("RITCord Login");
        Label label2 = new Label("Username: ");
        Label label3 = new Label("Password: ");
        Label label4 = new Label("Server: ");
         
        GridPane layout = new GridPane();
         
        layout.setPadding(new Insets(10, 10, 10, 10)); 
        layout.setVgap(5); 
        layout.setHgap(5); 
         
        layout.add(tfUser, 1,1);
        layout.add(tfPass, 1,2);
        layout.add(tfServer, 1, 3);
        layout.add(btnSubmit, 1,4);
        layout.add(label1, 1,0);
        layout.add(label2, 0,1);
        layout.add(label3, 0,2);
        layout.add(label4, 0, 3);
         
        Scene scene = new Scene(layout, 250, 250);          
        loginStage.setTitle("RITCord Login");
        loginStage.setScene(scene);
        loginStage.showAndWait();   
   }//end of doLogin
   
   
}
