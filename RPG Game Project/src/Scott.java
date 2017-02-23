import javax.swing.*;
import java.io.Serializable;

public class Scott extends Fighter implements Serializable {
   protected String name;
   private final String CLASS_PATH = "/gamedata/players/player[@class='Scott']";
   private GameDataParser myParser;
   private ImageIcon myIcon;
   private int ability1, ability2;

   protected Scott(String _name)
   {
      myParser = new GameDataParser(CLASS_PATH);

      name = _name;
      setBaseHealth(myParser.getBaseHealth());

      myIcon = myParser.getIcon();

      ability1 = myParser.getAbilityDamage(1);
      ability2 = myParser.getAbilityDamage(2);
   }

   protected String getClassName() { return "Best Teacher Ever"; }

   protected String getName() {
      return name;
   }

   protected void setName(String _name) {
      name = _name;
   }

   protected int ability1() { return ability1; }

   protected int ability2() { return ability2; }

   protected ImageIcon getIcon() { return myIcon; }

   protected String getAbilityDescription(int num) { return (myParser.getAbilityDescription(num)); }

   protected String getAbilityName(int num) { return (myParser.getAbilityName(num)); }
}
