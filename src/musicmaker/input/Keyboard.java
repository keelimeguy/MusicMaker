package musicmaker.input;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class Keyboard implements KeyListener {

   private boolean[] keys = new boolean[600];
   public boolean up, down, left, right, q, e, n1, n2, n3, n4, n5, n6, n7, n8, n9, n0, space, shift, ctrl;

   public void update() {
      up = keys[KeyEvent.VK_UP] || keys[KeyEvent.VK_W];
      down = keys[KeyEvent.VK_DOWN] || keys[KeyEvent.VK_S];
      left = keys[KeyEvent.VK_LEFT] || keys[KeyEvent.VK_A];
      right = keys[KeyEvent.VK_RIGHT] || keys[KeyEvent.VK_D];
      q = keys[KeyEvent.VK_Q];
      e = keys[KeyEvent.VK_E];
      n1 = keys[KeyEvent.VK_1];
      n2 = keys[KeyEvent.VK_2];
      n3 = keys[KeyEvent.VK_3];
      n4 = keys[KeyEvent.VK_4];
      n5 = keys[KeyEvent.VK_5];
      n6 = keys[KeyEvent.VK_6];
      n7 = keys[KeyEvent.VK_7];
      n8 = keys[KeyEvent.VK_8];
      n9 = keys[KeyEvent.VK_9];
      n0 = keys[KeyEvent.VK_0];
      space = keys[KeyEvent.VK_SPACE];
      shift = keys[KeyEvent.VK_SHIFT];
      ctrl = keys[KeyEvent.VK_CONTROL];
   }

   public void keyPressed(KeyEvent e) {
      keys[e.getKeyCode()] = true;
   }

   public void keyReleased(KeyEvent e) {
      keys[e.getKeyCode()] = false;
   }

   public void keyTyped(KeyEvent e) {

   }

}
