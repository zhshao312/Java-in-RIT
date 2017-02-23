import java.io.*;
import java.awt.*;
import java.net.*;
import java.util.*;
import javax.swing.*;
import java.awt.event.*;


public class MainMenu extends JFrame implements ActionListener{

   //Server Info panel
   private JPanel jpServerInfo;
   private JLabel jlServerIP;
   private JLabel jlPort;
   private JLabel jlCharacterName;
   private JTextField jtfServerIP;
   private JTextField jtfPort;
   //Controls panel
   private JPanel jpButtons;
   private JButton jbJoin;
   private JButton jbChooseCharacter;
   private JButton jbExit;

   private Fighter myFighter = new Warrior("Default");

   private SelectionScreen mySelectionScreen;

   public static void main (String[]args)
   {
      new MainMenu();
   }//end main

   //Constructor
   public MainMenu() { drawMenu(); }

   private void drawMenu()
   {
      //Server info panel(ip address/port number)
      jpServerInfo = new JPanel(new FlowLayout());
      jpServerInfo.add(jlServerIP = new JLabel("Server IP: "));
      jpServerInfo.add(jtfServerIP = new JTextField(8));
      jpServerInfo.add(jlPort = new JLabel("Port: "));
      jpServerInfo.add(jtfPort = new JTextField(4));
      //Insertion of panel
      add(jpServerInfo, BorderLayout.NORTH);

      jpButtons = new JPanel(new GridLayout(0,1));
         JPanel jpChooseFighter = new JPanel(new FlowLayout());
         jpChooseFighter.add(jbChooseCharacter = new JButton("Choose Fighter"));

         JPanel jpCharName = new JPanel(new FlowLayout());
         jlCharacterName = new JLabel("Current Fighter: " + myFighter.getName() + " - " + myFighter.getClassName());
         jlCharacterName.setVerticalAlignment(SwingConstants.CENTER);
         jpCharName.add(jlCharacterName);

         JPanel jpJoinQuit = new JPanel(new FlowLayout());
         jpJoinQuit.add(jbJoin = new JButton("Join!"));
         jpJoinQuit.add(jbExit = new JButton("Quit"));

      jpButtons.add(jpJoinQuit);
      jpButtons.add(jpCharName);
      jpButtons.add(jpChooseFighter);
      //Insertion of panel 2
      add(jpButtons, BorderLayout.CENTER);

      // TODO: 12/6/2015 Add in an indicator of the currently selected fighter. To switch your class, hit the choose class button

      //Adding action listeners
      jbJoin.addActionListener(this);
      jbChooseCharacter.addActionListener(this);
      jbExit.addActionListener(this);

      //General stuff for frames
      setLocationRelativeTo(null);
      setDefaultCloseOperation(EXIT_ON_CLOSE);
      setVisible(true);
      setTitle("RPG GAME LOG IN...");
      setSize(350,175);
   }
   
   //Methods
   public void actionPerformed(ActionEvent ae)
   {
      //Getting sources
      Object choice = ae.getSource();
      
      if(choice == jbJoin)
      {
         String ipAddr = jtfServerIP.getText();
         String port = jtfPort.getText();


            if(ipAddr.equals("") || port.equals(""))
            {
               JOptionPane.showMessageDialog(jbJoin, "You must enter a valid IP address and port number.");
            }
            else
            {
               this.dispose();
               new GameClient(ipAddr, Integer.parseInt(port), myFighter);
            }

         //catch(ConnectException ce){ JOptionPane.showMessageDialog(jbJoin, "Server doesn't appear to be running."); }
         //catch(IOException ioe){ ioe.printStackTrace(); }
         //catch(Exception e){ }
      }
      else if(choice == jbChooseCharacter)
      {
         mySelectionScreen = new SelectionScreen();
         FighterWaiter myWaiter = new FighterWaiter(); //starts a thread that waits for the user to select a character
         myWaiter.start();
      }
      else if(choice == jbExit)
      {
         System.exit(0);
      }
      
   }// end action performed

   //Once started, it checks every 100 ms to see if the selection screen has been closed. If it has been closed, it pulls
   //the fighter and updates the MainMenu's jlCharacterName label to the proper name as input by the user in the SelectionScreen
   class FighterWaiter extends Thread
   {
      public FighterWaiter(){}

      public void run()
      {
         while(mySelectionScreen.isVisible())
         {
            try{ Thread.sleep(100); }
            catch(InterruptedException ie){ ie.printStackTrace(); }
         }
         myFighter = mySelectionScreen.myFighter;
         jlCharacterName.setText("Current Fighter: " + myFighter.getName() + " - " + myFighter.getClassName());
      }
   }
}//end class