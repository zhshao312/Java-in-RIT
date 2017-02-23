import javax.swing.*;
import java.io.Serializable;

public class DemonKing extends Fighter implements Serializable
{
    private final String CLASS_PATH = "/gamedata/bosses/boss[@id = '1']";
    private GameDataParser myParser;
    private int ability1, ability2, ability3;
    private ImageIcon myIcon;

    public DemonKing()
    {
        myParser = new GameDataParser(CLASS_PATH);
        setBaseHealth(myParser.getBaseHealth());

        myIcon = myParser.getIcon();

        ability1 = myParser.getAbilityDamage(1);
        ability2 = myParser.getAbilityDamage(2);
        ability3 = myParser.getAbilityDamage(3);
    }

    public String getName() { return "DemonKing"; }
    protected ImageIcon getIcon() { return myIcon; }

    protected int ability1() { return ability1; }

    protected int ability2() {
        return ability2;
    }

    protected int ability3() {
        return ability3;
    }
}