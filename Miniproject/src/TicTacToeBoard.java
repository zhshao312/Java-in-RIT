import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseEvent;
/*

 Design for ticTacToeBoard
 */
public class TicTacToeBoard extends JPanel implements MouseListener {
   public static final int MARGIN = 30; // space width
   public static final int GRID_SPAN = 35; // grid spans
   public static final int ROWS = 7;//rows number
   public static final int COLS = 7;//cols number

   Point[] ticTacToeList = new Point[(ROWS + 1) * (COLS + 1)]; // Array named ticTacToelist
   boolean isBlack = true;//set black ticTacToe go first
   boolean gameOver = false;// set gameover
   int ticTacToeCount; // set ticTacToeCount
   int xIndex, yIndex; // set the position of ticTacToe which goes right now

   public TicTacToeBoard() {
      setBackground(Color.GREEN);//set background color is orange
      addMouseListener(this);// add listener
      addMouseMotionListener(
            new MouseMotionListener() { // mouseMotionListener into mouseListener
               public void mouseDragged(MouseEvent e) {
               }
            
               public void mouseMoved(MouseEvent e) {
                  int x1 = (e.getX() - MARGIN + GRID_SPAN / 2) / GRID_SPAN; 
               // set the mouse click position as the position on ticTacToeboard 
                  int y1 = (e.getY() - MARGIN + GRID_SPAN / 2) / GRID_SPAN;
               // game is over, can't go
               // out of ticTacToeboard area, can't go
               // have ticTacToe on the position, can't go
                  if (x1 < 0 || x1 > ROWS || y1 < 0 || y1 > COLS || gameOver
                  || findTicTacToe(x1, y1))
                     setCursor(new Cursor(Cursor.DEFAULT_CURSOR)); // set original model
                  else
                     setCursor(new Cursor(Cursor.HAND_CURSOR)); // set hand model
               }
            });
   }

	// Drawing
   public void paintComponent(Graphics g) {
      super.paintComponent(g);
   	// draw ticTacToeBoard
      for (int i = 0; i <= ROWS; i++) { // draw cols
         g.drawLine(MARGIN, MARGIN + i * GRID_SPAN, MARGIN + COLS
            	* GRID_SPAN, MARGIN + i * GRID_SPAN);
      }
      for (int i = 0; i <= COLS; i++) {// draw rows
         g.drawLine(MARGIN + i * GRID_SPAN, MARGIN, MARGIN + i * GRID_SPAN,
            	MARGIN + ROWS * GRID_SPAN);
      }
   	// draw ticTacToees
      for (int i = 0; i < ticTacToeCount; i++) {
         int xPos = ticTacToeList[i].getX() * GRID_SPAN + MARGIN; // x position
         int yPos = ticTacToeList[i].getY() * GRID_SPAN + MARGIN;// y positon
         g.setColor(ticTacToeList[i].getColor()); // set color
         g.fillOval(xPos - Point.DIAMETER / 2, yPos - Point.DIAMETER / 2,
            	Point.DIAMETER, Point.DIAMETER);
      
      }
   
   }

   public void mousePressed(MouseEvent e) {// mousePress
   	// game is over can't go
      if (gameOver)
         return;
      String colorName = isBlack ? "Black" : "White";
   
      xIndex = (e.getX() - MARGIN + GRID_SPAN / 2) / GRID_SPAN; // set the mouse click position as the position on ticTacToeboard
      yIndex = (e.getY() - MARGIN + GRID_SPAN / 2) / GRID_SPAN;
   
   	// out of ticTacToeboard area, can't go		
      if (xIndex < 0 || xIndex > ROWS || yIndex < 0 || yIndex > COLS)
         return;
   	// have ticTacToe on the x, y position, can't go
      if (findTicTacToe(xIndex, yIndex))
         return;
   
      Point ch = new Point(xIndex, yIndex, isBlack ? Color.black: Color.white);
      ticTacToeList[ticTacToeCount++] = ch;
      repaint(); // repaint method
      if (isWin()) {
      	// give win message
         String msg = String.format("Congratulation，%s win！", colorName);
         JOptionPane.showMessageDialog(this, msg);
         gameOver = true;
      }
      isBlack = !isBlack;
   }

	// cover mouseListener method
   public void mouseClicked(MouseEvent e) {
   } // use when click mouse

   public void mouseEntered(MouseEvent e) {
   }// use when in components

   public void mouseExited(MouseEvent e) {
   }// use when leave mouse

   public void mouseReleased(MouseEvent e) {
   } // use when leave components

	// search x, y position in ticTacToeBoard
   private boolean findTicTacToe(int x, int y) {
      for (Point c : ticTacToeList) {
         if (c != null && c.getX() == x && c.getY() == y)
            return true;
      }
      return false;
   }

   private boolean isWin() {//judge who win the game
      int continueCount = 1; // number of continue ticTacToees
   	// west direction
      for (int x = xIndex - 1; x >= 0; x--) {
         Color c = isBlack ? Color.black : Color.white;
         if (getTicTacToe(x, yIndex, c) != null) {
            continueCount++;
         } 
         else
            break;
      }
   	// east direction
      for (int x = xIndex + 1; x <= ROWS; x++) {
         Color c = isBlack ? Color.black : Color.white;
         if (getTicTacToe(x, yIndex, c) != null) {
            continueCount++;
         } 
         else
            break;
      }
      if (continueCount >= 3) {
         return true;
      } 
      else
         continueCount = 1;
   
   	
   	// north direction
      for (int y = yIndex - 1; y >= 0; y--) {
         Color c = isBlack ? Color.black : Color.white;
         if (getTicTacToe(xIndex, y, c) != null) {
            continueCount++;
         } 
         else
            break;
      }
   	// south direction
      for (int y = yIndex + 1; y <= ROWS; y++) {
         Color c = isBlack ? Color.black : Color.white;
         if (getTicTacToe(xIndex, y, c) != null) {
            continueCount++;
         } 
         else
            break;
      }
      if (continueCount >= 3) {
         return true;
      } 
      else
         continueCount = 1;
   
   	// northeast direction
      for (int x = xIndex + 1, y = yIndex - 1; y >= 0 && x <= COLS; x++, y--) {
         Color c = isBlack ? Color.black : Color.white;
         if (getTicTacToe(x, y, c) != null) {
            continueCount++;
         } 
         else
            break;
      }
   	// southeast direction
      for (int x = xIndex - 1, y = yIndex + 1; y <= ROWS && x >= 0; x--, y++) {
         Color c = isBlack ? Color.black : Color.white;
         if (getTicTacToe(x, y, c) != null) {
            continueCount++;
         } 
         else
            break;
      }
      if (continueCount >= 3) {
         return true;
      } 
      else
         continueCount = 1;
   
   	// northwest direction
      for (int x = xIndex - 1, y = yIndex - 1; y >= 0 && x >= 0; x--, y--) {
         Color c = isBlack ? Color.black : Color.white;
         if (getTicTacToe(x, y, c) != null) {
            continueCount++;
         } 
         else
            break;
      }
   	// southwest direction
      for (int x = xIndex + 1, y = yIndex + 1; y <= ROWS && x <= COLS; x++, y++) {
         Color c = isBlack ? Color.black : Color.white;
         if (getTicTacToe(x, y, c) != null) {
            continueCount++;
         } 
         else
            break;
      }
      if (continueCount >= 3) {
         return true;
      } 
      else
         continueCount = 1;
   
      return false;
   }

   private Point getTicTacToe(int xIndex, int yIndex, Color color) {
      for (Point c : ticTacToeList) {
         if (c != null && c.getX() == xIndex && c.getY() == yIndex
         		&& c.getColor() == color)
            return c;
      }
      return null;
   }

   public void restartGame() {
   	// clear ticTacToe
      for (int i = 0; i < ticTacToeList.length; i++)
         ticTacToeList[i] = null;
   	// clear all value
      isBlack = true;
      gameOver = false;// gameover set false
      ticTacToeCount = 0; // set ticTacToeCount to 0
      repaint();
   
   }

	// regret
   public void goback() {
      if (ticTacToeCount == 0)
         return;
   
      ticTacToeList[ticTacToeCount - 1] = null;
      ticTacToeCount--;
      if (ticTacToeCount > 0) {
         xIndex = ticTacToeList[ticTacToeCount - 1].getX();
         yIndex = ticTacToeList[ticTacToeCount - 1].getY();
      }
      isBlack = !isBlack;
      repaint();
   }

	// Dimension: 
   public Dimension getPreferredSize() {
      return new Dimension(MARGIN * 2 + GRID_SPAN * COLS, MARGIN * 2
         	+ GRID_SPAN * ROWS);
   }

}