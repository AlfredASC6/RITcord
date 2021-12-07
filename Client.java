import javafx.application.Application;
import javafx.event.*;
import javafx.scene.*;
import javafx.scene.text.*;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.*;
import javafx.stage.*;
import javafx.geometry.*;
import java.util.*;
import java.io.*;
import java.net.*;

/**
 * Client - a class that displays a log in page and moderate chats
 * 
 * @author A.Franco, AErskine, W.Celentano (add names if you contributed)
 * @version 12-1-21
 */
public class Client extends Application {

   private Stage stage;
   private Scene sceneMain;
   private Scene sceneLogin;
   private Scene sceneSignUp;
   private VBox root = new VBox(8);

   public TextArea taChat = new TextArea();
   private TextField tfMsg = new TextField();
   private Label lblMsg = new Label("Message");
   private Button btnSend = new Button("Send");

   private String serverIP;
   private Socket socket = null;
   private Scanner scn;
   private PrintWriter pwt = null;
   private int SERVER_PORT = 32001;

   static String username;
   static String password;

   public static void main(String[] args) {
      launch(args);

   }

   public void start(Stage _stage) throws Exception {
      doConnect("localhost");
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

      btnSend.setOnAction(new EventHandler<ActionEvent>() {
         public void handle(ActionEvent evt) {
            doSendMsg(tfMsg.getText());
         }
      });

      root.getChildren().addAll(fpTop, fpMid, btnSend);
      sceneMain = new Scene(root, 500, 350);
      stage.setX(100);
      stage.setY(200);
      stage.setOnCloseRequest(new EventHandler<WindowEvent>() {
         public void handle(WindowEvent evt) {
            doDisconnect();
         }
      });
   }// end of start

   private void doDisconnect() {
      try {
         // scn needs to be initialized. Runtime loop error when trying to close if
         // connection is not established
         scn.close();
         pwt.close();
         socket.close();
      } catch (IOException ioe) {
         Alert alert = new Alert(AlertType.ERROR, "Exception " + ioe);
         alert.showAndWait();
      }
   }// end doDisconnect()

   public void doConnect(String _serverIP) {
      serverIP = _serverIP;
      try {
         socket = new Socket(serverIP, SERVER_PORT);
         scn = new Scanner(new InputStreamReader(socket.getInputStream()));
         pwt = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()), true);

         // if (scn.nextLine().equals("Client Connected")) {
         // Alert alert = new Alert(AlertType.INFORMATION, "Connected to server, login to
         // continue.");
         // alert.showAndWait();
         // }

      } catch (IOException ioe) {
         Alert alert = new Alert(AlertType.ERROR, "Cannot open Sockets " + ioe);
         alert.showAndWait();
      }
   }// end of doConnect()

   /*
    * doSendMsg Method:
    * Put the message in the TextArea and send to clients
    */
   private void doSendMsg(String message) {
      if (message.equals("!login")) {
         pwt.println(message);
         pwt.flush();
      } else if (message.length() > 0) {
         pwt.println("<" + username + ">" + message + "\n");
         pwt.flush();
      } else {
         Alert alert = new Alert(AlertType.INFORMATION, "Please Type a message to be sent");
         alert.showAndWait();
      }
      message = "";
      recMsg();
   } // end doSendMsg

   private synchronized void recMsg() {
      while (scn.hasNextLine()) {
         String message = scn.nextLine();
         if (message.equals("!verified")) {
            stage.setScene(sceneMain);
            return;
         }
         if (message.equals("!unverified")) {
            Alert alert = new Alert(AlertType.INFORMATION, "Username or password is incorrect");
            alert.showAndWait();
            break;
         }
         if (message.equals("Client Connected")) {
            taChat.appendText(username + " has entered the server \n");
            return;
         }
         if (message.contains("<")) {
            taChat.appendText(message + "\n");
            tfMsg.setText("");
            return;
         }
      } // while
   }

   // Sends user and pass to server.
   private void sendUserInfo(String _username, String _password) {
      username = _username;
      password = _password;
      try {
         pwt.println("!cmd" + username + "#" + password);
         pwt.flush();
      } catch (Exception e) {
         System.out.println(e);
      }
   }

   /*
    * doLogin Method:
    * Changes stage to login scene.
    */
   public void doLogin() {
      AnchorPane login = new AnchorPane();

      login.setPrefSize(640, 400);

      Button btnSignUp = new Button("Sign Up");
      btnSignUp.setLayoutX(557);
      btnSignUp.setLayoutY(329);
      btnSignUp.setPrefSize(58, 25);
      btnSignUp.setTextAlignment(TextAlignment.CENTER);

      Button btnLogin = new Button("Login");
      btnLogin.setLayoutX(492);
      btnLogin.setLayoutY(329);
      btnLogin.setPrefSize(58, 25);
      btnLogin.setTextAlignment(TextAlignment.CENTER);

      Button btnFgtPass = new Button("Forgot Password");
      btnFgtPass.setLayoutX(492);
      btnFgtPass.setLayoutY(361);
      btnFgtPass.setPrefSize(123, 25);
      btnFgtPass.setTextAlignment(TextAlignment.CENTER);

      TextField tfUser = new TextField();
      tfUser.setPromptText("Username");
      tfUser.setLayoutX(30);
      tfUser.setLayoutY(329);
      tfUser.setPrefSize(210, 25);

      PasswordField tfPass = new PasswordField();
      tfPass.setLayoutX(259);
      tfPass.setLayoutY(329);
      tfPass.setPrefSize(210, 25);
      tfPass.setPromptText("Password");

      TextField tfServer = new TextField();
      tfServer.setPromptText("Server IP");
      tfServer.setLayoutX(30);
      tfServer.setLayoutY(361);
      tfServer.setPrefSize(210, 25);

      Label ritCordLabel = new Label("RITcord");
      ritCordLabel.setLayoutX(171);
      ritCordLabel.setLayoutY(14);
      ritCordLabel.setPrefSize(298, 148);
      ritCordLabel.setAlignment(Pos.CENTER);
      ritCordLabel.setTextAlignment(TextAlignment.CENTER);
      ritCordLabel.setTextOverrun(OverrunStyle.WORD_ELLIPSIS);
      ritCordLabel.setFont(Font.font(64));

      login.getChildren().addAll(btnSignUp, btnLogin, btnFgtPass, tfUser, tfPass, tfServer, ritCordLabel);

      btnSignUp.setOnAction(new EventHandler<ActionEvent>() {
         public void handle(ActionEvent e) {
            doSignup();
         }
      });
      btnLogin.setOnAction(new EventHandler<ActionEvent>() {
         public void handle(ActionEvent e) {
            try {
               if (socket == null) {
                  doConnect("localhost");
               }
               sendUserInfo(tfUser.getText(), tfPass.getText());
               doSendMsg("!login");
            } catch (Exception E) {
               Alert alert = new Alert(AlertType.INFORMATION, "Error verifying" + E);
               alert.showAndWait();
            }
         }
      });
      sceneLogin = new Scene(login);
      stage.setScene(sceneLogin);
      stage.show();
   }// end of doLogin

   /*
    * doSignup:
    * Changes stage to signup scene.
    */

   public void doSignup() {
      AnchorPane signUp = new AnchorPane();

      signUp.setPrefSize(428, 280);

      Button btnCreateAcc = new Button("Create Account");
      btnCreateAcc.setLayoutX(318);
      btnCreateAcc.setLayoutY(241);

      Button btnReturnLogin = new Button("Back to Login");
      btnReturnLogin.setLayoutX(16);
      btnReturnLogin.setLayoutY(241);
      btnReturnLogin.setPrefSize(116, 25);
      btnReturnLogin.setTextAlignment(TextAlignment.CENTER);

      TextField tfUser = new TextField();
      tfUser.setPromptText("Username");
      tfUser.setLayoutX(42);
      tfUser.setLayoutY(64);
      tfUser.setPrefSize(198, 35);

      PasswordField tfPass = new PasswordField();
      tfPass.setLayoutX(42);
      tfPass.setLayoutY(116);
      tfPass.setPrefSize(198, 35);
      tfPass.setPromptText("Password");

      PasswordField tfVerifyPass = new PasswordField();
      tfVerifyPass.setLayoutX(42);
      tfVerifyPass.setLayoutY(168);
      tfVerifyPass.setPrefSize(198, 35);
      tfVerifyPass.setPromptText("Verify Password");

      Label lblSignUp = new Label("RITcord Signup");
      lblSignUp.setLayoutX(16);
      lblSignUp.setLayoutY(14);
      lblSignUp.setPrefSize(300, 17);
      lblSignUp.setTextAlignment(TextAlignment.CENTER);
      lblSignUp.setFont(Font.font(22));

      Label lblPassRequirements = new Label(
            "Password must contain a minimum of 6 characters; 1 uppercase, 1 number and a special character. (!@#$%&*)");
      lblPassRequirements.setLayoutX(42);
      lblPassRequirements.setLayoutY(203);
      lblPassRequirements.setPrefSize(345, 69);
      lblPassRequirements.setWrapText(true);
      lblPassRequirements.setOpacity(0.50);
      lblPassRequirements.setAlignment(Pos.TOP_LEFT);

      Label lblUser = new Label("Username:");
      lblUser.setLayoutX(42);
      lblUser.setLayoutY(47);
      lblUser.setPrefSize(116, 17);

      Label lblPass = new Label("Password");
      lblPass.setLayoutX(42);
      lblPass.setLayoutY(99);
      lblPass.setPrefSize(300, 17);

      Label lblVerifyPass = new Label("Verify Password");
      lblVerifyPass.setLayoutX(42);
      lblVerifyPass.setLayoutY(151);
      lblVerifyPass.setPrefSize(300, 17);

      signUp.getChildren().addAll(btnCreateAcc, btnReturnLogin, tfPass, tfVerifyPass, tfUser,
            lblSignUp, lblPass,
            lblUser, lblVerifyPass);

      sceneSignUp = new Scene(signUp);
      stage.setScene(sceneSignUp);
      btnReturnLogin.setOnAction(new EventHandler<ActionEvent>() {
         public void handle(ActionEvent e) {
            doLogin();
         }
      });

      btnCreateAcc.setOnAction(new EventHandler<ActionEvent>() {
         public void handle(ActionEvent e) {
            doConnect("localhost");
            if (tfPass.getText().equals(tfVerifyPass.getText())) {
               sendUserInfo(tfUser.getText(), tfPass.getText());
               stage.setScene(sceneMain);
            } else {
               Alert alert = new Alert(AlertType.ERROR, "Passwords do not match!");
               alert.showAndWait();
            }
         }
      });
   }
}
