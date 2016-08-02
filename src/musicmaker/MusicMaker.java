package musicmaker;

import musicmaker.graphics.Screen;
import musicmaker.theory.ProgressionMap;
import musicmaker.theory.Progression;
import musicmaker.sound.ChordPlayer;
import musicmaker.input.Keyboard;
import musicmaker.input.Mouse;
import musicmaker.level.Level;
import musicmaker.entity.Entity;
import musicmaker.sound.maxim.Maxim;

import java.awt.Canvas;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;

import javax.swing.JFrame;

import musicmaker.sound.MusicPlayer;
import musicmaker.sound.SoundPlayer;

public class MusicMaker extends Canvas implements Runnable {
   private static final long serialVersionUID = 1L;

   private static int width = 300;
   private static int height = width / 16 * 9;
   private static int scale = 3; // The game will be scaled up by this factor
   private static String title = "MusicMaker";

   private Thread thread;
   private JFrame frame;
   private Keyboard key;
   private Mouse mouse;
   private boolean running = false;

   private Screen screen;
   private Level level;
   private Entity offset;
   private static MusicPlayer snd;
   private Maxim maxim;
   private ChordPlayer[] chordPlayer;
   private ProgressionMap progressionMap;
   private Progression progression;

   // The image which will be drawn in the game window
   private BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
   private int[] pixels = ((DataBufferInt) image.getRaster().getDataBuffer()).getData();

   // Initiates the necessary variables of the game
   public MusicMaker() {
      Dimension size = new Dimension(width * scale, height * scale);
      setPreferredSize(size);

      screen = new Screen(width, height);
      frame = new JFrame();
      key = new Keyboard();
      level = new Level(width, height);
      offset = new Entity(width / 2, height / 2);

      snd = new MusicPlayer();
      maxim = new Maxim();

      define("Eb", "I", 24);

      addKeyListener(key);

      mouse = new Mouse();
      addMouseListener(mouse);
      addMouseMotionListener(mouse);
   }

   private void define(String key, String start, int length) {
      level.empty();
      level.add(offset);

      progressionMap = new ProgressionMap(key, start);
      progression = progressionMap.generate(length);
      progression.show();

      if(chordPlayer != null)
         for (int i = 0; i < length; i ++)
            if (chordPlayer[i] != null)
               chordPlayer[i].remove();
      chordPlayer = new ChordPlayer[length];
      for (int i = 0; i < length; i ++)
         chordPlayer[i] = new ChordPlayer(maxim, progression.next());
   }

   // Returns the width of the window with scaling.
   public int getWindowWidth() {
      return frame.getContentPane().getWidth();
   }

   // Returns the height of the window with scaling.
   public int getWindowHeight() {
      return frame.getContentPane().getHeight();
   }

   public Screen getScreen() {
      return screen;
   }

   public synchronized void start() {
      running = true;
      thread = new Thread(this, "Display");
      thread.start();
      snd.start();
   }

   public synchronized void stop() {
      running = false;
      try {
         thread.join();
      } catch (InterruptedException e) {
         e.printStackTrace();
      }
      try {
         snd.join();
      } catch (InterruptedException e) {
         e.printStackTrace();
      }
   }

   public void run() {
      long lastTime = System.nanoTime();
      long timer = System.currentTimeMillis();
      final double ns = 1000000000.0 / 60.0;
      double delta = 0;
      int frames = 0, updates = 0;
      requestFocus();

      //String audioFilePath = "/Res/Music/funky.wav";
      //snd.playMusic(audioFilePath, LoopStart.FUNKY, -1);

      // The game loop
      while (running) {
         long now = System.nanoTime();
         delta += (now - lastTime) / ns;
         lastTime = now;
         // Update 60 times a second
         while (delta >= 1) {
            update();

            Graphics g = getGraphics();
            paint(g);

            updates++;
            delta--;
         }
         frames++;

         // Keep track of and display the game's ups and fps every second
         if (System.currentTimeMillis() - timer >= 1000) {
            timer += 1000;
            frame.setTitle(title + " | ups: " + updates + ", fps: " + frames);
            updates = 0;
            frames = 0;
         }
      }

      // If we get out of the game loop stop the game
      stop();
   }

   // Update the game
   public void update() {

      int xScroll = 0;//offset.getX() - screen.getWidth() / 2;
      int yScroll = 0;//offset.getY() - screen.getHeight() / 2;

      key.update();
      level.update(xScroll, yScroll, this);

      // chordPlayer[curPlayer].setVolume(1);
      chordPlayer[curPlayer].play();
      try {
         Thread.sleep(2000);
      } catch (InterruptedException ex) {
         ex.printStackTrace();
      }
      chordPlayer[curPlayer].stop();
      curPlayer++;
      if (curPlayer >= chordPlayer.length) curPlayer = 0;
   }

   public void update(Graphics g) {
   }

   int curPlayer = 0;
   public void paint(Graphics g) {
      // Clear the screen to black before rendering
      screen.clear(0);

      int xScroll = 0;//offset.getX() - screen.getWidth() / 2;
      int yScroll = 0;//offset.getY() - screen.getHeight() / 2;

      // Render the level with the given screen offset
      level.render(xScroll, yScroll, screen);

      // Draw the image
      g.drawImage(image, 0, 0, getWidth(), getHeight(), null);
      g.drawString(progression + "", 50, 50);
      g.drawString("currently playing: " + chordPlayer[curPlayer].getChord(), 50, 100);
      g.dispose();

      // Copy the screen pixels to the image to be drawn
      System.arraycopy(screen.getPixels(), 0, pixels, 0, pixels.length);
   }

   public static void main(String[] args) {
      System.setProperty("sun.awt.noerasebackground", "true");
      // Create the game
      MusicMaker game = new MusicMaker();
      game.frame.setResizable(true);
      game.frame.setTitle(MusicMaker.title);
      game.frame.add(game);
      game.frame.pack();
      game.frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
      game.frame.setLocationRelativeTo(null);
      game.frame.setVisible(true);

      // Start the game
      game.start();
   }
}
