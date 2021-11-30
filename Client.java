
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
 * @author A.Franco, AErskine (add names if you contributed)
 * @version 11-29-21
 */
public class Client extends Application{

   private Stage stage;
   private Scene scene;
   private VBox root = new VBox(8);

   private TextArea taChat = new TextArea();
   private TextField tfServer = new TextField();
   private Label lblServer = new Label("Server");
   private Button btnSend = new Button("Send");
  
   private String serverIP;   
   private Socket socket = null;
   private Scanner in;
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
      fpTop.getChildren().addAll(lblServer, tfServer, taChat);
      fpTop.setAlignment(Pos.CENTER);
      
      root.getChildren().addAll(fpTop, btnSend);
      scene = new Scene(root, 500, 350);
      stage.setScene(scene);
      stage.show();
   }
   public void sendUserInfo(){
      
   }
   
   /*
   *doLogin Method: Create stage to send username and password to server 
   *
   */
   private void doLogin(){
        Stage loginStage = new Stage();
        loginStage.initModality(Modality.APPLICATION_MODAL);
         
        TextField tfUser = new TextField();
        TextField tfPass = new TextField();
         
        Button btnSubmit = new Button("Submit");

        btnSubmit.setOnAction(new EventHandler<ActionEvent>(){
         public void handle(ActionEvent e){
            //send user info here 
            username = tfUser.getText();
            password = tfPass.getText();
            
            //sendUserInfo();
            
            loginStage.close();
            
         }
        });
        
     
        Label label1 = new Label("RITCord Login");
        Label label2 = new Label("Username:");
        Label label3 = new Label("Password:");
         
        GridPane layout = new GridPane();
         
        layout.setPadding(new Insets(10, 10, 10, 10)); 
        layout.setVgap(5); 
        layout.setHgap(5); 
         
        layout.add(tfUser, 1,1);
        layout.add(tfPass, 1,2);
        layout.add(btnSubmit, 1,3);
        layout.add(label1, 1,0);
        layout.add(label2, 0,1);
        layout.add(label3, 0,2);
         
        Scene scene = new Scene(layout, 250, 150);          
        loginStage.setTitle("RITCord Login");
        loginStage.setScene(scene);
        loginStage.showAndWait();   
   }//end of doLogin
}
