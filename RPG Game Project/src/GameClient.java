import java.awt.*;
import java.awt.event.*;
import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.text.BadLocationException;
import java.io.*;
import java.net.*;
import java.util.*;

public class GameClient
{
   //Attributes
   private JTextArea jtaMessages;
   private JTextField jtfSendMessage;
   private String ipAddr = "localhost";
   private int port = 4444;
   private int currentTurnNumber;
   private Fighter myFighter;
   private ImageIcon turnIcon;
   Integer myTurnNumber;
   Vector<Fighter> clientList;

   //arrays to hold JLabels for all of the class info(health/names/pictures)
                                               //boss              //first          //second             //third
   private JProgressBar[] fighterHealths = {new JProgressBar(), new JProgressBar(), new JProgressBar(), new JProgressBar()};
   private JLabel[] turnIndicator        = {new JLabel(" "), new JLabel(" "), new JLabel(" "), new JLabel(" ")};
   private JLabel[] fighterPictures      = {new JLabel(" "), new JLabel(" "), new JLabel(" "), new JLabel(" ")};
   private JLabel[] playerNames          = {new JLabel(" Connected Players: "), new JLabel(""), new JLabel(" "), new JLabel(" ")};

   private JButton jbAbility1 = new JButton();
   private JButton jbAbility2 = new JButton();
   private JButton jbAbility3 = new JButton();

   private JButton[] abilities = {jbAbility1, jbAbility2};

   public JFrame myFrame;

   private ObjectOutputStream oos;
   private ObjectInputStream ois;

   public static void main(String[] args)
   {
      new GameClient("localhost", 4444, new Warrior("TESTING"));
   }
   //Constructor
   public GameClient(String _ipAddr, int _port, Fighter _myFighter)
   {
      myFighter = _myFighter;
      ipAddr = _ipAddr;
      port = _port;

      //if this fails, a message will pop up and the game will close
      connectToServer();

      //actually draws the game screen
      drawGame();

      Thread inputThread = new Thread(new ReceiveObjects());
      inputThread.start();

      setButtonsEnabled(false);

      //make the turn indicator icon
      try
      {
         Image icon = ImageIO.read(getClass().getResource("turnIndicator.png"));
         turnIcon = new ImageIcon(icon);
      }
      catch(IOException ioe){ ioe.printStackTrace(); }
   }

   private void drawGame()
   {
      myFrame = new JFrame("RPG Client");

      //holds the player list
      JPanel jpPlayerList = new JPanel(new GridLayout(0,1));

      //add all of the jlabels to the connected players list
      for(JLabel a : playerNames) { jpPlayerList.add(a); }

      //holds the fight area
      JPanel jpFightArea = new JPanel(new BorderLayout());
      jpFightArea.setBorder(BorderFactory.createLineBorder(Color.black,5, false));  //color, thickness of border, rounded edges

      //panel to hold the boss
      JPanel jpBossArea = new JPanel(new GridLayout(2,2,0,0));
      JPanel bossHealth = new JPanel(new FlowLayout());
      bossHealth.add(fighterHealths[0]);
      jpBossArea.add(bossHealth);

      jpBossArea.add(new JLabel()); //filler

      fighterPictures[0].setHorizontalAlignment(SwingConstants.CENTER);
      jpBossArea.add(fighterPictures[0]);

      jpBossArea.add(new JLabel()); //filler
      jpFightArea.add(jpBossArea, BorderLayout.WEST);

      //panel for the players, and adding the jlabels for all their info
      JPanel jpFighterArea = new JPanel(new GridLayout(0,1));

      for(int i=1; i < fighterHealths.length; i++)
      {
         fighterHealths[i].setVisible(false);
         JPanel bla = new JPanel(new FlowLayout());
         bla.add(turnIndicator[i]);
         bla.add(fighterPictures[i]);
         bla.add(fighterHealths[i]);
         jpFighterArea.add(bla);
      }
      jpFightArea.add(jpFighterArea, BorderLayout.EAST);

      //holds the sending info panel, abilities panel, and text box
      JPanel jpBottom = new JPanel(new GridLayout(0,2));
         jtaMessages = new JTextArea(6,0);
         jtaMessages.setLineWrap(true);
         jtaMessages.setWrapStyleWord(true);
         jtaMessages.setEditable(false);
         jtaMessages.setLineWrap(true);
         JScrollPane jspText = new JScrollPane(jtaMessages);
         jpBottom.add(jspText);

      //holds the user text field/send button and the ability buttons
      JPanel jpBottomRight = new JPanel(new GridLayout(2,0));

         //holds the ability buttons
         JPanel jpAbilities = new JPanel(new FlowLayout());

         //making the action listener for the abilities to use
         AbilityListener abilityCaster = new AbilityListener();

         //loop to set all of the button names/tooltips/add an action listener to them
         for(int i=0; i < abilities.length; i++)
         {
            abilities[i].setText(myFighter.getAbilityName(i+1));
            abilities[i].setToolTipText("<html>" + myFighter.getAbilityDescription(i+1) + "</html>");
            abilities[i].addActionListener(abilityCaster);

            jpAbilities.add(abilities[i]);
         }
         jpBottomRight.add(jpAbilities);

      //holds the user text field/send button
      JPanel jpSendingInfo = new JPanel(new FlowLayout());
         jtfSendMessage = new JTextField(25);
         jpSendingInfo.add(jtfSendMessage);
         JButton jbSend = new JButton("Send");
         jbSend.addActionListener(new SendButtonListener());
         jpSendingInfo.add(jbSend);
         jpBottomRight.add(jpSendingInfo);

      jpBottom.add(jpBottomRight);

      myFrame.add(jpFightArea, BorderLayout.CENTER);
      myFrame.add(jpPlayerList, BorderLayout.EAST);
      myFrame.add(jpBottom, BorderLayout.SOUTH);

      myFrame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

      //size of the game window is hard coded
      myFrame.setSize(750,400);

      //this gets the default resolution of your main monitor(multi monitor friendly)
      GraphicsDevice gd = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
      int width = gd.getDisplayMode().getWidth();
      int height = gd.getDisplayMode().getHeight();

      //sets the frame to always be in the middle of your default screen, regardless of resolution
      myFrame.setLocation((width / 2) - (myFrame.getWidth() / 2), (height / 2) - (myFrame.getHeight() / 2));
      myFrame.setVisible(true);
      jtfSendMessage.requestFocus();
   }//end drawGame()

   private void connectToServer()
   {
      try
      {
         Socket client = new Socket();
         client.connect(new InetSocketAddress(ipAddr, port), 2000);

         oos = new ObjectOutputStream(new DataOutputStream(client.getOutputStream()));
         ois = new ObjectInputStream(new DataInputStream(client.getInputStream()));

         oos.writeObject(myFighter);
         oos.flush();
         System.out.println("wrote out myFighter");

         myTurnNumber = ois.readInt();
         System.out.println("Turn Number: " + myTurnNumber);
      }
      catch(SocketException se)
      {
         JOptionPane.showMessageDialog(null, "Couldn't connect to server!");
         System.exit(0);
      }
      catch(IOException ioe) { ioe.printStackTrace(); }
   }

   private void setButtonsEnabled(boolean b)
   {
      for(JButton myButton: abilities)
      {
         myButton.setEnabled(b);
      }
   }

   private void followText()
   {
      int focusIn = jtaMessages.getDocument().getLength();
      jtaMessages.setCaretPosition(focusIn);
      jtaMessages.requestFocusInWindow();
   }

   class SendButtonListener implements ActionListener
   {
      public void actionPerformed(ActionEvent ae)
      {
         try
         {
            String message = myFighter.getName() + ": " + jtfSendMessage.getText();
            oos.writeObject(message);
            oos.flush();
         }
         catch(IOException ioe){ ioe.printStackTrace(); }

         jtfSendMessage.setText("");
         jtfSendMessage.requestFocus();
      }
   }//end inner class 1 (action listeners)

   class AbilityListener implements ActionListener
   {
      public void actionPerformed(ActionEvent ae)
      {
         try
         {
            Object choice = ae.getSource();

            if (choice == jbAbility1)
            {
               oos.writeObject(myFighter.getName() + " attacked " + clientList.get(0).getName() + " for " + myFighter.ability1() + " damage!");
               clientList.get(0).changeCurrentHealth(-myFighter.ability1());
            }
            else if (choice == jbAbility2)
            {
               oos.writeObject(myFighter.getName() + " performed " + myFighter.getAbilityName(2) + " and healed for " + myFighter.ability2() + " hp!");
               clientList.get(myTurnNumber).changeCurrentHealth(myFighter.ability1());
            }
            setButtonsEnabled(false);
            oos.writeObject(clientList);
            oos.flush();
         }
         catch(IOException ioe){ ioe.printStackTrace(); }
      }
   }//end inner class 2 (action listeners)

   //tweaked this so we can use one stream for everything and just check which type of data came in I know we previously
   //were planning to use multiple streams but I was running into issues with creating more than one I/O stream per socket
   class ReceiveObjects implements Runnable
   {
      public void run()
      {
         Object obj;
         try
         {
            while(((obj = ois.readObject()) != null))
            {
               if(obj instanceof String)
               {
                  if(obj.equals("You lose!"))
                  {
                     JOptionPane.showMessageDialog(jbAbility1, clientList.get(0).getName() + " has defeated you!");
                     System.exit(0);
                  }
                  if(obj.equals("You win!"))
                  {
                     JOptionPane.showMessageDialog(jbAbility1, "Victory!");
                     System.exit(0);
                  }
                  else
                  {
                     jtaMessages.append(obj + "\n");
                     followText();
                  }
               }
               else if(obj instanceof Vector)
               {
                  clientList = (Vector)obj;
                  updateGUI(clientList);
               }
               else if(obj instanceof Integer)
               {
                  currentTurnNumber = ((Integer) obj).intValue();

                  for(int i=0; i < clientList.size(); i++)
                  {
                     turnIndicator[i].setIcon(null);
                  }
                  turnIndicator[currentTurnNumber].setIcon(turnIcon);

                  if(currentTurnNumber == myTurnNumber.intValue())
                  {
                     if(clientList.get(myTurnNumber.intValue()).isFighterAlive())
                     {
                        setButtonsEnabled(true);
                     }
                     else
                     {
                        oos.writeObject(clientList);
                        oos.flush();
                     }
                  }
               }
            }
         }
         catch(IOException ioe) { ioe.printStackTrace(); }
         catch(ClassNotFoundException cnfe){ cnfe.printStackTrace(); }
      }

      private void updateProgressBars(int i)
      {
         fighterHealths[i].setOrientation(JProgressBar.HORIZONTAL);
         fighterHealths[i].setMinimum(0);
         fighterHealths[i].setMaximum(clientList.get(i).getBaseHealth());
         fighterHealths[i].setValue(clientList.get(i).getCurrentHealth());
         fighterHealths[i].setString(clientList.get(i).getName() +": "+ Integer.toString(clientList.get(i).getCurrentHealth()) + "/" + Integer.toString(clientList.get(i).getBaseHealth()));
         fighterHealths[i].setVisible(true);
         fighterHealths[i].setStringPainted(true);
         fighterHealths[i].setOpaque(false);
         fighterHealths[i].setForeground(Color.RED);
      }

      private void updateGUI(Vector<Fighter> clientList)
      {
         for(int i=1; i < clientList.size(); i++)
         {
            playerNames[i].setHorizontalAlignment(SwingConstants.CENTER);
            playerNames[i].setText("<html>" + clientList.get(i).getName() + "<br> Class - " + clientList.get(i).getClassName() + "</html>");
         }
         for(int i=0; i < clientList.size(); i++)
         {
            updateProgressBars(i);
            fighterPictures[i].setIcon(clientList.get(i).getIcon());
         }
         myFrame.revalidate();
      }
   }//end inner class 3 (threadz)
}//end class