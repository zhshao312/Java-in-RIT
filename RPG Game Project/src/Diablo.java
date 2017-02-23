import javax.swing.*;
import java.io.Serializable;

public class Diablo extends Fighter implements Serializable
{
   private final String CLASS_PATH = "/gamedata/bosses/boss[@id ='0']";
   private GameDataParser myParser;
   private ImageIcon myIcon;
   private int ability1, ability2, ability3;

   public Diablo()
   {
      myParser = new GameDataParser(CLASS_PATH);
      setBaseHealth(myParser.getBaseHealth());

      myIcon = myParser.getIcon();

      ability1 = myParser.getAbilityDamage(1);
      ability2 = myParser.getAbilityDamage(2);
      ability3 = myParser.getAbilityDamage(3);
   }

   public String getName() { return "BlueMan"; }

   protected ImageIcon getIcon() { return myIcon; }

   protected int ability1() { return ability1; }

   protected int ability2() {
      return ability2;
   }

   protected int ability3() { return ability3; }
}