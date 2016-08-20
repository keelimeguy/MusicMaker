package musicmaker.level.entity.gui;

import musicmaker.level.entity.Entity;
import musicmaker.graphics.Sprite;
import musicmaker.input.Mouse;
import musicmaker.graphics.Screen;
import musicmaker.MusicMaker;

import java.awt.Graphics;
import java.awt.Font;
import java.awt.Color;

public class Button extends Entity {
   protected String text = "";
   protected int anim = 0, speed = 8, step = 0;

   public Button(int x, int y, int width, int height, int color) {
      super(x, y);
      sprite = new Sprite(width, height, color);
   }

   public Button(int x, int y, Sprite sprite) {
      super(x, y);
      this.sprite = sprite;
   }

   public Button(int x, int y, int width, int height, Sprite sprite) {
      super(x, y);
      this.sprite = new Sprite(width, height, sprite);
   }

   public void setText(String text) {
      if (text == null)
         this.text = "";
      else
         this.text = text;
   }

   public void press() {
   }

   public void update(MusicMaker game) {
      super.update(game);
      if (anim < 7500)
         anim++;
      else
         anim = 0;
      if (anim % speed == speed - 1) step++;
      if (step > 7500) step = 1;
      if (step >= 1 && isClicked()) {
         step = anim = 0;
         press();
      }
   }

   public void render(Graphics g) {
      g.setFont(new Font("Verdana", Font.BOLD, 15));
      g.setColor(Color.yellow);
      if (isClicked())
         g.setColor(Color.red);
      g.drawString(text, x + 5, y + sprite.SIZE_Y/2 + 5);
   }

   protected void checkIfClicked(int width, int height, Screen screen) {
      if (sprite == null || screen == null)
         return;
      if (Mouse.getB() > 0 && Mouse.getX() > (x - screen.getXOffset()) * width / screen.getWidth() && Mouse.getX() < (sprite.SIZE_X + x - screen.getXOffset()) * width / screen.getWidth() && Mouse.getY() > (y - screen.getYOffset()) * height / screen.getHeight() && Mouse.getY() < (sprite.SIZE_Y - screen.getYOffset() + y)  * height / screen.getHeight())
         clicked = true;
      else
         clicked = false;
   }
}
