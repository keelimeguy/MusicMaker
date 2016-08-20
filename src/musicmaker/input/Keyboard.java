package musicmaker.input;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class Keyboard implements KeyListener {

   private boolean[] keys = new boolean[600];
   public boolean up, down, left, right, a, b, c, d, e, f, g, h, i, j, k, l, m, n,
      o, p, q, r, s, t, u, v, w, x, y, z, n1, n2, n3, n4, n5, n6, n7, n8, n9, n0,
      np1, np2, np3, np4, np5, np6, np7, np8, np9, np0, space, shift, ctrl, enter;

   public void update() {
      up = keys[KeyEvent.VK_UP];
      down = keys[KeyEvent.VK_DOWN];
      left = keys[KeyEvent.VK_LEFT];
      right = keys[KeyEvent.VK_RIGHT];
      a = keys[KeyEvent.VK_A];
      b = keys[KeyEvent.VK_B];
      c = keys[KeyEvent.VK_C];
      d = keys[KeyEvent.VK_D];
      e = keys[KeyEvent.VK_E];
      f = keys[KeyEvent.VK_F];
      g = keys[KeyEvent.VK_G];
      h = keys[KeyEvent.VK_H];
      i = keys[KeyEvent.VK_I];
      j = keys[KeyEvent.VK_J];
      k = keys[KeyEvent.VK_K];
      l = keys[KeyEvent.VK_L];
      m = keys[KeyEvent.VK_M];
      n = keys[KeyEvent.VK_N];
      o = keys[KeyEvent.VK_O];
      p = keys[KeyEvent.VK_P];
      q = keys[KeyEvent.VK_Q];
      r = keys[KeyEvent.VK_R];
      s = keys[KeyEvent.VK_S];
      t = keys[KeyEvent.VK_T];
      u = keys[KeyEvent.VK_U];
      v = keys[KeyEvent.VK_V];
      w = keys[KeyEvent.VK_W];
      x = keys[KeyEvent.VK_X];
      y = keys[KeyEvent.VK_Y];
      z = keys[KeyEvent.VK_Z];
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
      np1 = keys[KeyEvent.VK_NUMPAD1];
      np2 = keys[KeyEvent.VK_NUMPAD2];
      np3 = keys[KeyEvent.VK_NUMPAD3];
      np4 = keys[KeyEvent.VK_NUMPAD4];
      np5 = keys[KeyEvent.VK_NUMPAD5];
      np6 = keys[KeyEvent.VK_NUMPAD6];
      np7 = keys[KeyEvent.VK_NUMPAD7];
      np8 = keys[KeyEvent.VK_NUMPAD8];
      np9 = keys[KeyEvent.VK_NUMPAD9];
      np0 = keys[KeyEvent.VK_NUMPAD0];
      space = keys[KeyEvent.VK_SPACE];
      shift = keys[KeyEvent.VK_SHIFT];
      ctrl = keys[KeyEvent.VK_CONTROL];
      enter = keys[KeyEvent.VK_ENTER];
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
