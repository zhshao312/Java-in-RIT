/**
 * Created by Josh
 */
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.font.TextAttribute;
import java.rmi.activation.ActivationInstantiator;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Map;

public class SelectionScreen extends JFrame implements ActionListener
{
   private final int ABILITIES_ROWS = 3;
   private final int ABILITES_COLUMNS = 2;
   private int listPosition;

   private ArrayList<Fighter> classList;

   //absolutely terrible way to do this, but currently using arrays of jlabels to display the classInfo
   private JLabel[][] jlAbilities = new JLabel[ABILITIES_ROWS][ABILITES_COLUMNS];
   private JLabel className;
   private JLabel baseHealth;
   private JLabel classIcon;
   private JTextField jtfName;

   private JButton jbCreate;

   protected Fighter myFighter;

   public SelectionScreen()
   {
      setTitle("Choose Your Class!");
      setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

      Font titleFont = new Font("Book Antiqua", Font.BOLD, 20);
      Font headerFont = new Font("Arial", Font.BOLD, 17);
      Font statsFont = new Font("Verdana", Font.PLAIN, 14);

      //making an arraylist to hold copies of each class in order to pull info from them(stats, abilities, etc.)
      classList = new ArrayList<Fighter>();
      classList.add(new Warrior("Warrior"));
      classList.add(new Wizard("Wizard"));
      classList.add(new Rogue("Rogue"));

      listPosition = 0;

      //making a label for the class name
      className = new JLabel();
      className.setFont(titleFont);
      className.setHorizontalAlignment(SwingConstants.CENTER);

      baseHealth = new JLabel();
      baseHealth.setFont(headerFont);
      baseHealth.setHorizontalAlignment(SwingConstants.CENTER);

      classIcon = new JLabel();
      classIcon.setHorizontalAlignment(SwingConstants.CENTER);

      //holds the class name, base health, and a divider
      JPanel classNamePanel = new JPanel(new GridLayout(0,1));
         classNamePanel.add(className);
         classNamePanel.add(baseHealth);
         classNamePanel.add(new JSeparator(SwingConstants.HORIZONTAL));

      //holds the ability descriptions
      JPanel abilityPanel = new JPanel(new GridLayout(ABILITIES_ROWS, ABILITES_COLUMNS));

      for(int i=0; i<ABILITIES_ROWS; i++)
      {
         for(int j=0; j<ABILITES_COLUMNS; j++)
         {
            JLabel label = new JLabel();
            label.setFont(statsFont);
            label.setHorizontalAlignment(SwingConstants.CENTER);

            jlAbilities[i][j] = label;
            abilityPanel.add(label);
         }
      }

      jlAbilities[0][0].setText("Ability Name");
      jlAbilities[0][0].setFont(headerFont);

      jlAbilities[0][1].setText("Description");
      jlAbilities[0][1].setFont(headerFont);

      //will hold the selection panel and the name panel
      JPanel bottomPanel = new JPanel(new FlowLayout());

         JPanel namePanel = new JPanel(new FlowLayout());
            jtfName = new JTextField("Fighter Name",15);

            //clears the textfield when the user clicks in the name text field
            jtfName.addFocusListener(new FocusListener()
            {
               public void focusGained(FocusEvent e) { jtfName.setText(""); }

               public void focusLost(FocusEvent e) {}
            });
            namePanel.add(jtfName);

            //button to create the character that you selected
            jbCreate = new JButton("Create!");
            jbCreate.addActionListener(this);
            namePanel.add(jbCreate);

         JPanel selectionPanel = new JPanel(new FlowLayout());
            JButton jbPrev = new JButton("<=");
            jbPrev.addActionListener(this);
            selectionPanel.add(jbPrev);

            JButton jbNext = new JButton("=>");
            jbNext.addActionListener(this);
            selectionPanel.add(jbNext);

         bottomPanel.add(classIcon);
         bottomPanel.add(namePanel);
         bottomPanel.add(selectionPanel);

      //adding all of the panels
      add(classNamePanel, BorderLayout.NORTH);
      add(abilityPanel, BorderLayout.CENTER);
      add(bottomPanel, BorderLayout.SOUTH);

      updateText();

      setSize(800,375);
      setLocationRelativeTo(null);
      setVisible(true);
      requestFocus();
   }

   //updates the text to reflect the proper class stats/abilities
   private void updateText()
   {
      className.setText(getCurrentFighter().getClassName());
      baseHealth.setText("Health: " + getCurrentFighter().getBaseHealth());
      classIcon.setIcon(getCurrentFighter().getIcon());

      jlAbilities[1][0].setText(getCurrentFighter().getAbilityName(1));
      jlAbilities[1][1].setText("<html>" + getCurrentFighter().getAbilityDescription(1) + "</html>");

      jlAbilities[2][0].setText(getCurrentFighter().getAbilityName(2));
      jlAbilities[2][1].setText("<html>" + getCurrentFighter().getAbilityDescription(2) + "</html>");
   }

   //returns the current fighter on screen
   protected Fighter getCurrentFighter()
   {
      return classList.get(listPosition);
   }

   public void actionPerformed(ActionEvent ae)
   {
      String choice = ae.getActionCommand();

      //The choices endlessly cycle rather than stopping at either end
      if(choice == "<=")
      {
         if(listPosition == 0)
         {
            listPosition = classList.size() - 1; //endless cycle
         }
         else
         {
            listPosition--;
         }
         updateText();
      }

      else if(choice == "=>")
      {
         if(listPosition == classList.size() - 1) //endless cycle
         {
            listPosition = 0;
         }
         else
         {
            listPosition++;
         }
         updateText();
      }

      else if (choice == "Create!")
      {
         String charName = jtfName.getText();
         if(charName.equals("") || charName.equals("Fighter Name"))
         {
            JOptionPane.showMessageDialog(jbCreate, "Please enter a fighter name.");
         }
         if(charName.equals("Scott"))
         {
            myFighter = new Scott(jtfName.getText());
            dispose();
         }
         else
         {
            myFighter = getCurrentFighter();
            myFighter.setName(charName);
            dispose();
         }
      }
   }
}