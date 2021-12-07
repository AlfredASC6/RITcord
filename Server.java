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
import java.util.Map.Entry;
import java.io.*;
import java.net.*;

/**
 * Server - a server that handles the messages of a chat application
 * 
 * @author A.Franco, Jon Wong
 * @version 11-29-21
 */

public class Server extends Application implements EventHandler<ActionEvent> {
   private Stage stage;
   private Scene scene;
   private VBox root = new VBox(8);
   private TextArea taLog = new TextArea();
   private Button button = new Button("Start");

   private int SERVER_PORT = 32323;
   private PrintWriter pwt = null;
   private PrintWriter pwt2 = null;
   private File savedChat = null;
   private File encryptedPass = new File("./encryptedPass.dat");
   private File usernameData = new File("./usernameData.txt");
   private ServerThread serverThread = null;
   private passwordManager pwm = new passwordManager();

   private boolean userFound;
   private ServerSocket sSocket = null;

   private Vector<ClientThread> clients = null;

   public static void main(String[] args) {
      launch(args);
   }

   public void start(Stage _stage) {
      stage = _stage;
      stage.setTitle("Server Side");

      FlowPane fpTop = new FlowPane(8, 8);
      fpTop.getChildren().addAll(taLog);

      FlowPane fpMid = new FlowPane(8, 8);
      fpMid.getChildren().addAll(button);
      fpMid.setAlignment(Pos.CENTER);

      root.getChildren().addAll(fpTop, fpMid);
      stage.setOnCloseRequest(new EventHandler<WindowEvent>() {
         public void handle(WindowEvent evt) {
            System.exit(0);
         }
      });
      // taLog.setEditable(false);

      button.setOnAction(this);

      scene = new Scene(root, 400, 250);
      stage.setX(800);
      stage.setY(200);
      stage.setScene(scene);
      stage.show();
   }

   public void handle(ActionEvent evt) {
      String label = ((Button) evt.getSource()).getText();

      switch (label) {
         case "Start":
            doStart();
            break;
      }
   }

   public void doStart() {
      serverThread = new ServerThread();
      serverThread.start();
      taLog.appendText("Server Started\n");
   }

   class ServerThread extends Thread {
      public void run() {
         try {
            sSocket = new ServerSocket(SERVER_PORT);
            acceptClients();
         } catch (Exception e) {
            taLog.appendText("Excpetion " + e);
         }
      }// end of run
   }// end of ServerThread

   class ClientThread extends Thread {
      private Socket cSocket;
      private String clientId = "";
      String line;

      public ClientThread(Socket _cSocket) {
         cSocket = _cSocket;
         clientId = cSocket.getInetAddress().getHostAddress() + ":" + cSocket.getPort();
         taLog.appendText(clientId + " Client connected!\n");
      }

      // main program for a ClientThread
      public void run() {
         Scanner scn = null;
         PrintWriter pwt = null;
         BufferedWriter bw = null;
         BufferedReader br = null;
         BufferedReader br1 = null;
         BufferedReader br2 = null;
         DataOutputStream dos = null;
         int currentIndex = 0;

         // taLog.appendText(clientId + " Client connected!\n");

         try {
            // Open streams
            scn = new Scanner(new InputStreamReader(cSocket.getInputStream()));
            pwt = new PrintWriter(new OutputStreamWriter(cSocket.getOutputStream()));
            // let client know that streams are open
            pwt.println("Client Connected");
            pwt.flush();

         } catch (IOException ioe) {
            taLog.appendText(clientId + " IO Exception (ClientThread): " + ioe + "\n");
            return;
         }
         while (scn.hasNextLine()) {
            line = scn.nextLine();
            if (line.contains("!cmd")) {
               HashMap<String, String> map = new HashMap<>();
               line = line.replace("!cmd", "");
               String[] userInfo = line.split("#");
               try {
                  br = new BufferedReader(new InputStreamReader(new FileInputStream(usernameData)));
                  String currentLine;
                  while ((currentLine = br.readLine()) != null) {
                     if (currentLine.contains(userInfo[0])) {
                        userFound = true;
                        System.out.println("user found");
                        try {
                           br1 = new BufferedReader(new InputStreamReader(new FileInputStream(encryptedPass)));
                           br2 = new BufferedReader(new InputStreamReader(new FileInputStream(usernameData)));
                           String[] salt = null;
                           String securePassword = null;
                           // currentIndex = currentIndex - 1;
                           for (int i = 0; i <= currentIndex; i++) {
                              securePassword = br1.readLine();
                              System.out.println(securePassword);
                              salt = br2.readLine().split(":");
                           }
                           System.out.println("verify start");
                           pwm.verifyUserPassword(userInfo[1], securePassword, salt[1]);
                           System.out.println("verify finish");
                           pwt.print("true");
                           pwt.flush();
                           System.out.println("Sent verify");
                        } catch (Exception e) {
                           taLog.appendText("error verifying user data");
                        }
                        break;
                     } else {
                        currentIndex++;
                     }
                  }
               } catch (Exception e) {
                  taLog.appendText("Error checking if user exists");
               }
               if (!userFound) {
                  map.put(userInfo[0], pwm.getSalt(16));
                  try {
                     bw = new BufferedWriter(new FileWriter(usernameData, true));
                     bw.write(userInfo[0] + ":" + map.get(userInfo[0]) + "\n");
                     bw.flush();
                     System.out.println(userInfo[0] + ":" + map.get(userInfo[0]) + "\n");
                  } catch (Exception e) {
                     taLog.appendText("error creating user data file");
                  }
                  try {
                     dos = new DataOutputStream(new FileOutputStream(encryptedPass, true));
                     dos.writeUTF(pwm.generateSecurePassword(userInfo[1], map.get(userInfo[0])) + "\n");
                     dos.flush();
                  } catch (Exception e) {
                     taLog.appendText("error creating encrypted password file");
                  }
               }
            } else if (line.contains("<")) {
               for (int i = 0; i < clients.size(); i++) {
                  // Client client = clients.get(i);
               }
               System.out.println(line);
               taLog.appendText(line + "\n");
               pwt.println(line);
               pwt.flush();
            }
         }
      }// end of run

      public void toAllClients(String Message) {

      }
   }// end of ClientThread

   public void acceptClients() {
      clients = new Vector<ClientThread>();
      while (true) {
         Socket cSocket = null;
         try {
            cSocket = sSocket.accept();
         }

         catch (IOException ioe) {
            taLog.appendText("Socket failed");
         }
         ClientThread clientThread = new ClientThread(cSocket);
         Thread thread = new Thread(clientThread);
         thread.start();
         clients.add(clientThread);
         // clientThread.start(); making 2 intances of thread

      }
   }// end of accept clients
}