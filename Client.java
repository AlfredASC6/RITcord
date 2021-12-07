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
 * 
 * @author A.Franco, AErskine, W.Celentano (add names if you contributed)
 * @version 12-1-21
 */
public class Client extends Application {

   private Stage stage;
   private Scene sceneMain;
   private Scene sceneLogin;
   private Scene sceneSignUp;
   private Scene sceneForgotPass;
   private VBox root = new VBox(8);

   private TextArea taChat = new TextArea();
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
   private boolean userVerified = false;
   private String masterCode = "123";

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
      sceneMain = new Scene(root, 500, 350);
      stage.setX(100);
      stage.setY(200);
      stage.setOnCloseRequest(new EventHandler<WindowEvent>() {
         public void handle(WindowEvent evt) {
             doDisconnect();
             System.exit(0);
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
         System.out.println(ioe);
         alert.showAndWait();
      }
   }// end doDisconnect()

   public void doConnect(String _serverIP) {
      serverIP = _serverIP;
      try {
         socket = new Socket(serverIP, SERVER_PORT);
         scn = new Scanner(new InputStreamReader(socket.getInputStream()));
         pwt = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()));

         if (scn.nextLine().equals("Client Connected")) {
            Alert alert = new Alert(AlertType.INFORMATION, "Connected to server, login to continue.");
            alert.showAndWait();
         }

      } catch (IOException ioe) {
         Alert alert = new Alert(AlertType.ERROR, "Cannot open Sockets " + ioe);
         alert.showAndWait();
         System.out.println(ioe);
      }
         userVerified = true;
   }// end of doConnect()

   /*
    * doSendMsg Method:
    * Put the message in the TextArea and send to clients
    */
   private void doSendMsg(String message) {
      pwt.println(tfMsg.getText());
      if (tfMsg.getText().isEmpty()) {
         Alert alert = new Alert(AlertType.INFORMATION, "Please Type a message to be sent");
         alert.showAndWait();
         return;
      } else {
         pwt.println("<" + username + ">" + message + "\n");
         pwt.flush();
      }
      recMsg();
      // idk how but this message has to be sent to the other clients that
      // server - Andy
      // once user is validated and logged in, any messages sent to server will be
      // stored in local string and on chatlog
      // as soon as server recieves, it resends to all clients, even the original
      // client. client will only append taChat when it recieves msgs from server
   } // end doSendMsg
     // recMsg takes any incoming information from server. Added condition to check
     // whether it is a command or a message.

   private void recMsg() {
      while (scn.hasNextLine()) {
         String message = scn.nextLine();
         if(message.equals("Client Connected")){
            taChat.appendText(username + " has entered the server \n");
            return;
         }
         if(message.contains("<")){
            taChat.appendText(message + "\n");
            tfMsg.setText("");
            return;
         }
      }//while
   }

   // Sends user and pass to server.
   private void sendUserInfo(String _username, String _password) {
      username = _username;
      password = _password;
      try{
         doConnect("localhost");
         pwt.println("!cmd" + username + "#" + password);
         pwt.flush();
      }
      catch(Exception e){
         System.out.println(e);
      }
   }

   private void doPassChange(String resetCode, String _user, String _pass) {
      // check if resetCode is correct, then call sendUserInfo(); output alert if any
      // value is wrong
      // IE resetCode is wrong or password does not have minimum requirements
      // Once done, show alert to confirm account was made and change scene to
      // sceneMain
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

      Button btnConnect = new Button("Connect");
      btnConnect.setLayoutX(290);
      btnConnect.setLayoutY(361);
      btnConnect.setPrefSize(149, 25);
      btnConnect.setTextAlignment(TextAlignment.CENTER);

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

      login.getChildren().addAll(btnSignUp, btnLogin, btnFgtPass, btnConnect, tfUser, tfPass, tfServer, ritCordLabel);

      btnSignUp.setOnAction(new EventHandler<ActionEvent>() {
         public void handle(ActionEvent e) {
            doSignup();
         }
      });
      btnFgtPass.setOnAction(new EventHandler<ActionEvent>() {
         public void handle(ActionEvent e) {
            doForgotPass();
         }
      });
      btnLogin.setOnAction(new EventHandler<ActionEvent>() {
         public void handle(ActionEvent e) {
            if (userVerified) {
               sendUserInfo(tfUser.getText(), tfPass.getText());
               stage.setScene(sceneMain);
            } else {
               Alert alert = new Alert(AlertType.INFORMATION, "Username or password is incorrect.");
               alert.showAndWait();
            }
         }
      });
      btnConnect.setOnAction(new EventHandler<ActionEvent>() {
         public void handle(ActionEvent e) {
            doConnect(tfServer.getText());
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

      signUp.getChildren().addAll(lblPassRequirements, btnCreateAcc, btnReturnLogin, tfPass, tfVerifyPass, tfUser,
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
            if (tfPass.getText().equals(tfVerifyPass.getText())) {
               sendUserInfo(tfUser.getText(), tfPass.getText());
               doConnect("localhost");
               stage.setScene(sceneMain);
            } else {
               Alert alert = new Alert(AlertType.ERROR, "Passwords do not match!");
               alert.showAndWait();
            }
         }
      });
   }

   public void doForgotPass() {

      AnchorPane forgotPass = new AnchorPane();

      forgotPass.setPrefSize(445, 400);

      Button btnChangePass = new Button("Change Password");
      btnChangePass.setLayoutX(318);
      btnChangePass.setLayoutY(361);

      Button btnReturnLogin = new Button("Back to Login");
      btnReturnLogin.setLayoutX(23);
      btnReturnLogin.setLayoutY(361);
      btnReturnLogin.setPrefSize(116, 25);
      btnReturnLogin.setTextAlignment(TextAlignment.CENTER);

      TextField tfUser = new TextField();
      tfUser.setPromptText("Username");
      tfUser.setLayoutX(49);
      tfUser.setLayoutY(178);
      tfUser.setPrefSize(198, 35);

      TextField tfResetCode = new TextField();
      tfResetCode.setPromptText("Reset Code");
      tfResetCode.setLayoutX(49);
      tfResetCode.setLayoutY(99);
      tfResetCode.setPrefSize(230, 51);

      PasswordField tfPass = new PasswordField();
      tfPass.setLayoutX(49);
      tfPass.setLayoutY(230);
      tfPass.setPrefSize(198, 35);
      tfPass.setPromptText("New Password");

      PasswordField tfVerifyPass = new PasswordField();
      tfVerifyPass.setLayoutX(49);
      tfVerifyPass.setLayoutY(282);
      tfVerifyPass.setPrefSize(198, 35);
      tfVerifyPass.setPromptText("Verify Password");

      Label lblForgot = new Label("Forgot Password");
      lblForgot.setLayoutX(26);
      lblForgot.setLayoutY(14);
      lblForgot.setTextAlignment(TextAlignment.CENTER);
      lblForgot.setFont(Font.font(22));

      Label lblUser = new Label("Username:");
      lblUser.setLayoutX(49);
      lblUser.setLayoutY(161);
      lblUser.setPrefSize(116, 17);

      Label lblInfo = new Label(
            "If you have forgotten your password, please contact one of the developers to assist you with resetting your password.");
      lblInfo.setLayoutX(49);
      lblInfo.setLayoutY(58);
      lblInfo.setPrefSize(338, 35);
      lblInfo.setWrapText(true);

      Label lblPassRequirements = new Label(
            "Password must contain a minimum of 6 characters; 1 uppercase, 1 number and a special character. (!@#$%&*)");
      lblPassRequirements.setLayoutX(49);
      lblPassRequirements.setLayoutY(317);
      lblPassRequirements.setPrefSize(345, 69);
      lblPassRequirements.setWrapText(true);
      lblPassRequirements.setOpacity(0.50);
      lblPassRequirements.setAlignment(Pos.TOP_LEFT);

      Label lblPass = new Label("Password");
      lblPass.setLayoutX(49);
      lblPass.setLayoutY(213);

      Label lblVerifyPass = new Label("Verify Password");
      lblVerifyPass.setLayoutX(49);
      lblVerifyPass.setLayoutY(265);

      Label lbl = new Label("Verify Password");
      lblVerifyPass.setLayoutX(49);
      lblVerifyPass.setLayoutY(265);

      forgotPass.getChildren().addAll(lblForgot, lblPass, lblUser, lblVerifyPass, lblPassRequirements,
            lblInfo, tfResetCode, btnChangePass, btnReturnLogin, tfPass, tfVerifyPass, tfUser);

      sceneForgotPass = new Scene(forgotPass);
      stage.setScene(sceneForgotPass);
      btnReturnLogin.setOnAction(new EventHandler<ActionEvent>() {
         public void handle(ActionEvent e) {
            doLogin();
         }
      });
      btnChangePass.setOnAction(new EventHandler<ActionEvent>() {
         public void handle(ActionEvent e) {
            if (tfPass.getText().equals(tfVerifyPass.getText()) && tfResetCode.getText().equals(masterCode)) {
               doPassChange(tfResetCode.getText(), tfUser.getText(), tfPass.getText());
            } else {
               Alert alert = new Alert(AlertType.ERROR, "Passwords do not match or reset code is incorrect");
               alert.showAndWait();
            }
         }
      });
   }
}
