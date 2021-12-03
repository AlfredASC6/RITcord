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
import javafx.fxml.*;
import java.util.*;
import java.io.*;
import java.net.*;
import javafx.event.ActionEvent.*;
import javafx.event.EventHandler.*;

/**
 * Client - a class that displays a log in page and moderate chats
 * 
 * @author A.Franco, AErskine, W.Celentano (add names if you contributed)
 * @version 12-1-21
 */
public class Client extends Application {

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

   public static void main(String[] args) {
      launch(args);
   }

   public void start(Stage _stage) throws Exception {
      stage = _stage;
      stage.setTitle("RITcord");

      // open login window
      doLogin();

      FlowPane fpTop = new FlowPane(8, 8);
      fpTop.getChildren().addAll(taChat);
      fpTop.setAlignment(Pos.CENTER);
      taChat.setDisable(true);
      taChat.setPrefHeight(250);

      FlowPane fpMid = new FlowPane(8, 8);
      fpMid.getChildren().addAll(lblMsg, tfMsg);
      fpMid.setAlignment(Pos.CENTER);
      tfMsg.setPrefColumnCount(20);

      tfMsg.setOnAction(new EventHandler<ActionEvent>() {
         public void handle(ActionEvent evt) {
            doSendMsg(tfMsg.getText());
         }
      });

      root.getChildren().addAll(fpTop, fpMid, btnSend);
      scene = new Scene(root, 500, 350);
      stage.setScene(scene);
      stage.show();

      stage.setOnCloseRequest(new EventHandler<WindowEvent>() {
         public void handle(WindowEvent evt) {
            // doDisconnect();
         }
      });
   }// end of start

   private void doDisconnect() {
      try {
         scn.close();
         pwt.close();
         socket.close();
      } catch (IOException ioe) {
         Alert alert = new Alert(AlertType.ERROR, "Exception " + ioe);
         alert.showAndWait();
      }
   }// end doDisconnect()

   public void doConnect() {
      try {
         socket = new Socket(serverIP, SERVER_PORT);
         scn = new Scanner(new InputStreamReader(socket.getInputStream()));
         pwt = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()));
         
         recMsg();
      } catch (IOException ioe) {
         Alert alert = new Alert(AlertType.ERROR, "Exception " + ioe);
         alert.showAndWait();
      }
   }// end of doConnect()

   /*
    * doSendMsg Method:
    * Put the message in the TextArea and send to clients
    */
   private void doSendMsg(String message) {
      if (!tfMsg.getText().isEmpty()) {
         taChat.appendText("<" + username + ">" + message + "\n");
         tfMsg.setText("");
      }
      recMsg();
      // idk how but this message has to be sent to the other clients that
      // server - Andy
      // once user is validated and logged in, any messages sent to server will be
      // stored in local string and on chatlog
      // as soon as server recieves, it resends to all clients, even the original
      // client. client will only append taChat when it recieves msgs from server
   } // end doSendMsg

   private void recMsg(){
      while(scn.hasNextLine()){
         String message = scn.nextLine();
         taChat.appendText(message);
      }
   
   }
   private void sendUserInfo() {
      // send username and password
      pwt.println(username + "#" + password);
      pwt.flush();
   }

   /*
    * doLogin Method:
    * Create stage to send username and password to server
    */
   public void doLogin() throws Exception {
      Parent root = FXMLLoader.load(getClass().getResource("Login.fxml"));
      Stage loginStage = new Stage();
      Scene scene = new Scene(root);
      loginStage.initModality(Modality.APPLICATION_MODAL);

      TextField tfUser = new TextField();
      PasswordField tfPass = new PasswordField();
      TextField tfServer = new TextField();

      Button btnSubmit = new Button("Submit");
      btnSubmit.setDefaultButton(true);

      btnSubmit.setOnAction(new EventHandler<ActionEvent>() {
         public void handle(ActionEvent e) {
            // send user info here
            username = tfUser.getText();
            password = tfPass.getText();

            serverIP = tfServer.getText();

            if (username.isEmpty() || password.isEmpty() || serverIP.isEmpty()) {
               Alert alert = new Alert(AlertType.INFORMATION);
               alert.setContentText("Please enter information into all fields");
               alert.show();
            } else {
               // doConnect();
               // sendUserInfo();

               loginStage.close();
            }
         }
      });

      // Label label1 = new Label("RITCord Login");
      // Label label2 = new Label("Username: ");
      // Label label3 = new Label("Password: ");
      // Label label4 = new Label("Server: ");

      // VBox layout = new Vbox();

      // layout.prefHeight(400);
      // layout.prefWidth(640);

      // layout.add(tfUser, 1,1);
      // layout.add(tfPass, 1,2);
      // layout.add(tfServer, 1, 3);
      // layout.add(btnSubmit, 1,4);
      // layout.add(label1, 1,0);
      // layout.add(label2, 0,1);
      // layout.add(label3, 0,2);
      // layout.add(label4, 0, 3);

      loginStage.setTitle("RITCord Login");
      loginStage.setScene(scene);
      loginStage.showAndWait();
   }// end of doLogin
    // switches to signup window

   public void switchToSignup(ActionEvent event) throws Exception {
      Parent root = FXMLLoader.load(getClass().getResource("Signup.fxml"));
      stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
      scene = new Scene(root);
      stage.setScene(scene);
      stage.show();
   }

   // switches to login window
   public void switchToLogin(ActionEvent event) throws Exception {
      Parent root = FXMLLoader.load(getClass().getResource("Login.fxml"));
      stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
      scene = new Scene(root);
      stage.setScene(scene);
      stage.show();
   }

   // switches window to forgot password
   public void switchToForgot(ActionEvent event) throws Exception {
      Parent root = FXMLLoader.load(getClass().getResource("ForgotPass.fxml"));
      stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
      scene = new Scene(root);
      stage.setScene(scene);
      stage.show();
   }
}
