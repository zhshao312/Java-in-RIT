import javax.swing.*;
import java.awt.event.*;
import java.awt.*;
/*
 TicTacToe game
 */
public class StartTicTacToeJFrame extends JFrame {
   private TicTacToeBoard ticTacToeBoard;//ticTacToeBoard
   private JPanel toolbar;//JPanel
		
   private JButton startButton, backButton, exitButton;
	//restart, regret and exit button	
   private JMenuBar menuBar;//jmenu Bar
   private JMenu sysMenu;//jmenu sysmenu
   private JMenuItem startMenuItem, exitMenuItem, backMenuItem;
	//items: start, exit, back	
	
   public StartTicTacToeJFrame() {
      setTitle("TicTacToe 8*8");//set title
      ticTacToeBoard = new TicTacToeBoard();//new TicTacToeBoard
   	// new Menus
      menuBar = new JMenuBar();//new menuBar
      sysMenu = new JMenu("Menu");//new sysmenu
   	//new JMenuItems
      startMenuItem = new JMenuItem("Restart");
      exitMenuItem = new JMenuItem("Exit");
      backMenuItem = new JMenuItem("Regret");
      sysMenu.add(startMenuItem);//add three JMenuItems on JMenu
      sysMenu.add(backMenuItem);
      sysMenu.add(exitMenuItem);
      MyItemListener lis = new MyItemListener();//new ItemListener
      this.startMenuItem.addActionListener(lis);//add Actionlistener for MenuItems
      backMenuItem.addActionListener(lis);
      exitMenuItem.addActionListener(lis);
   
      menuBar.add(sysMenu);//add sysMenu on menuBar
      setJMenuBar(menuBar);// set JMenuBar
   
      toolbar = new JPanel();//new JPanel
      startButton = new JButton("Restart");//Three JButtons
      backButton = new JButton("Regret");
      exitButton = new JButton("Exit");
      toolbar.setLayout(new FlowLayout(FlowLayout.LEFT));//set JPanel as flowLayout 
      toolbar.add(startButton);//add JButtons on JPanel
      toolbar.add(backButton);
      toolbar.add(exitButton);
      startButton.addActionListener(lis);//add ActionListeners for three Buttons
      backButton.addActionListener(lis);
      exitButton.addActionListener(lis);
      add(toolbar, BorderLayout.SOUTH);//set tooBar on SOUTH
      add(ticTacToeBoard);//add all things on TicTacToeBoard
      setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);//set close
   	//setSize(800,800);
      pack(); //set size
   }

   private class MyItemListener implements ActionListener {//actionListener
   
      public void actionPerformed(ActionEvent e) {
         Object obj = e.getSource(); // getSource
         if (obj == StartTicTacToeJFrame.this.startMenuItem || obj == startButton) { 
         	// restart
           // JFiveFrame.this
            System.out.println("Restart...");
            ticTacToeBoard.restartGame();
         
         } 
         else if (obj == exitMenuItem || obj == exitButton) {
            System.exit(0); // close program
         } 
         else if (obj == backMenuItem || obj == backButton) { //regret
            System.out.println("Regret...");
            ticTacToeBoard.goback();
         }
      }
   }
   public static void main(String[] args) {
      StartTicTacToeJFrame f = new StartTicTacToeJFrame(); // create ticTacToeJFrame
      f.setVisible(true); // set visible
   }
}

