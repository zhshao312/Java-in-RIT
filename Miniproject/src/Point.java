import java.awt.Color;

/*
 Design for ticTacToe pieces
 */
public class Point {
   private int x; // x value for ticTacToeboard
   private int y; // y value for ticTacToeboard
   private Color color;//color
   public static final int DIAMETER = 30;//DIAMETER
   public Point(int x, int y, Color color) {
      this.x = x;
      this.y = y;
      this.color = color;
   }
   public int getX() {// getX
      return x;
   }
   public int getY() {// getY
      return y;
   }
   public Color getColor() {//getColor
      return color;
   }
}


