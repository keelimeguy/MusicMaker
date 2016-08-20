package musicmaker;

import musicmaker.graphics.Screen;
import musicmaker.theory.ProgressionMap;
import musicmaker.theory.Progression;
import musicmaker.theory.Note;
import musicmaker.theory.PitchBank;
import musicmaker.sound.StaffPlayer;
import musicmaker.theory.instrument.Ukulele;
import musicmaker.theory.instrument.Guitar;
import musicmaker.theory.Staff;
import musicmaker.theory.Metronome;
// import musicmaker.sound.ChordPlayer;
import musicmaker.input.Keyboard;
import musicmaker.input.Mouse;
import musicmaker.level.Level;
import musicmaker.level.entity.Entity;
import musicmaker.level.entity.StaffEntity;
import musicmaker.level.entity.UkuleleEntity;
import musicmaker.level.entity.GuitarEntity;
import musicmaker.level.entity.ProgressionEntity;
import musicmaker.level.entity.gui.PlayButton;
import musicmaker.level.entity.gui.PauseButton;
import musicmaker.level.entity.gui.StopButton;
import musicmaker.level.entity.gui.ResetButton;
import musicmaker.level.entity.gui.KeyAdjuster;
import musicmaker.level.entity.gui.BeatAdjuster;
import musicmaker.level.entity.gui.LengthAdjuster;
import musicmaker.sound.maxim.Maxim;

import java.awt.Canvas;
import java.awt.Font;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.JFrame;
import javax.swing.KeyStroke;
import javax.swing.JOptionPane;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JMenu;

public class MusicMaker extends Canvas implements Runnable {
   private static final long serialVersionUID = 1L;

   private static int width = 900;
   private static int height = width / 16 * 9;
   private static int scale = 1; // The game will be scaled up by this factor
   private static String title = "MusicMaker";

   private Thread thread;
   private JFrame frame;
   private Keyboard key;
   private Mouse mouse;
   private boolean running = false;

   private Screen screen;
   private Level level;
   private Entity offset;
   private StaffEntity staffEntity;
   private UkuleleEntity ukuleleEntity;
   private GuitarEntity guitarEntity;
   private ProgressionEntity progressionEntity;
   private Maxim maxim;
   // private ChordPlayer[] chordPlayer;
   private StaffPlayer staffPlayer;
   private Metronome metronome;
   private ProgressionMap progressionMap;
   private Progression progression;

   private int anim = 0, speed = 8, step = 0;
   private String startKey, start;
   private int length, beatsPerMeasure, beatType, tempo;

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

      maxim = new Maxim();

      restoreDefault();
      define();

      addKeyListener(key);

      mouse = new Mouse();
      addMouseListener(mouse);
      addMouseMotionListener(mouse);
   }

   public void restoreDefault() {
      startKey = "A";
      start = "I";
      length = 8;
      beatsPerMeasure = 4;
      beatType = 4;
      tempo = 160;
   }

   public void define() {
      level.empty();
      level.add(offset);

      metronome = new Metronome();

      progressionMap = new ProgressionMap(startKey, start);
      progression = progressionMap.generate(length, true, true);
      progression.show();

      if(staffPlayer != null)
         staffPlayer.clear();

      Staff staff = new Staff(beatsPerMeasure, beatType, tempo);
      int octave = 4;

      for (int i = 0; i < length; i ++) {
         PitchBank bank = new PitchBank();
         /*octave = */bank.add(progression.next(), octave);
         staff.add(bank, beatsPerMeasure);
      }

      staffPlayer = new StaffPlayer(maxim, staff);
      staffPlayer.init(metronome);
      staffPlayer.setLooping(true);

      staffEntity = new StaffEntity(50, 20, staffPlayer);
      metronome.subscribe(staffEntity);

      progressionEntity = new ProgressionEntity(50, 50, progression, staffPlayer);
      metronome.subscribe(progressionEntity);

      ukuleleEntity = new UkuleleEntity(50, 140, new Ukulele(15), staffPlayer);
      metronome.subscribe(ukuleleEntity);

      guitarEntity = new GuitarEntity(50, 260, new Guitar(21), staffPlayer);
      metronome.subscribe(guitarEntity);

      level.add(staffEntity);
      level.add(progressionEntity);
      level.add(ukuleleEntity);
      level.add(guitarEntity);

      PlayButton playButton = new PlayButton(50, 440, 60, 20, 0xeeff00ff, this);
      PauseButton pauseButton = new PauseButton(120, 440, 60, 20, 0xeeff00ff, this);
      StopButton stopButton = new StopButton(190, 440, 60, 20, 0xeeff00ff, this);
      BeatAdjuster beatAdjuster = new BeatAdjuster(50, 470, 130, 20, 0xeeff00ff, 0xee00ff00, 0xeeff0000, BeatAdjuster.Type.VERTICAL, this);
      ResetButton resetButton = new ResetButton(190, 470, 60, 20, 0xeeff00ff, this);
      KeyAdjuster keyAdjuster = new KeyAdjuster(280, 440, 250, 20, 0xeeff00ff, 0xee00ff00, 0xeeff0000, KeyAdjuster.Type.VERTICAL, this);
      LengthAdjuster lengthAdjuster = new LengthAdjuster(280, 470, 250, 20, 0xeeff00ff, 0xee00ff00, 0xeeff0000, LengthAdjuster.Type.VERTICAL, this);
      level.add(playButton);
      level.add(pauseButton);
      level.add(stopButton);
      level.add(beatAdjuster);
      level.add(resetButton);
      level.add(keyAdjuster);
      level.add(lengthAdjuster);
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
   }

   public synchronized void stop() {
      running = false;
      try {
         thread.join();
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

      staffPlayer.play();
      // The game loop
      while (running) {
         long now = System.nanoTime();
         delta += (now - lastTime) / ns;
         lastTime = now;
         // Update 60 times a second
         while (delta >= 1) {
            update();

            updates++;
            delta--;
         }

         render();
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

      int xScroll = 1000;//offset.getX() - screen.getWidth() / 2;
      int yScroll = 100;//offset.getY() - screen.getHeight() / 2;

      key.update();
      level.update(xScroll, yScroll, this);

      if (anim < 7500)
         anim++;
      else
         anim = 0;

      if (anim % speed == speed - 1) step++;
      if (step >= 1) {
         step = anim = 0;
         if (key.space)
            startPlayer();
         else if (key.shift) {
            playCurBeat();
         } else if (key.p)
            pausePlayer();
         else if (key.ctrl)
            stopPlayer();
         else if (key.right)
            nextBeat();
         else if (key.left)
            prevBeat();
         else if (key.up)
            setStartKey(Note.get(startKey).sharp().getName());
         else if (key.down)
            setStartKey(Note.get(startKey).flat().getName());
         else if (key.enter)
            define();
      }
      // curPlayer++;
      // if (curPlayer >= chordPlayer.length) curPlayer = 0;
   }

   public String getStartKey() { return startKey; }

   public void setStartKey(String startKey) {
      this.startKey = startKey;
   }

   public void setStart(String start) {
      this.start = start;
   }

   public void setLength(int length) {
      this.length = length;
   }

   public void setBeatsPerMeasure(int beatsPerMeasure) {
      this.beatsPerMeasure = beatsPerMeasure;
   }

   public void setBeatType(int beatType) {
      this.beatType = beatType;
   }

   public void setTempo(int tempo) {
      this.tempo = tempo;
   }

   public void setProgressionLength(int length) {
      if (length > 0)
         this.length = length;
   }

   public int getProgressionLength() { return length; }

   public void startPlayer() {
      staffPlayer.play();
   }

   public void playCurBeat() {
      staffPlayer.play();
      staffPlayer.pause();
      staffPlayer.prevBeat();
      metronome.tick();
   }

   public void pausePlayer() {
      staffPlayer.pause();
   }

   public void stopPlayer() {
      staffPlayer.stop();
   }

   public void nextBeat() {
      staffPlayer.nextBeat();
   }

   public void prevBeat() {
      staffPlayer.prevBeat();
   }

   public void update(Graphics g) {
      render();
   }

   public void paint(Graphics g) {
   }

   public void render() {
      BufferStrategy bs = this.getBufferStrategy();
      if (bs == null) {
         createBufferStrategy(3);
         return;
      }

      // Clear the screen to black before rendering
      screen.clear(0);

      int xScroll = 0;//offset.getX() - screen.getWidth() / 2;
      int yScroll = 0;//offset.getY() - screen.getHeight() / 2;

      Graphics g = bs.getDrawGraphics();

      // Render the level to the screen with the given screen offset
      level.render(xScroll, yScroll, this);

      // Copy the screen pixels to the image to be drawn
      System.arraycopy(screen.getPixels(), 0, pixels, 0, pixels.length);

      g.drawImage(image, 0, 0, getWidth(), getHeight(), null);

      g.dispose();

      bs.show();
   }

   public JMenuBar makeMenu(){
      JMenuBar menu = new JMenuBar();
      JMenu help = new JMenu("Help");
      // help.setMnemonic(KeyEvent.VK_H);
      // help.getAccessibleContext().setAccessibleDescription("");
      menu.add(help);
      JMenuItem menuItem = new JMenuItem("View Keyboard Shortcuts");
      menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, ActionEvent.ALT_MASK));
      menuItem.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent e) {
            showHelp();
         }
      });
      help.add(menuItem);

      return menu;
   }

   public void showHelp() {
      JOptionPane.showMessageDialog(null, "space = start metronome     ctrl = stop playing     p = pause metronome" +
         "\nleft = previous beat     right = next beat     shift = play current beat" +
         "\nup/down = adjust key     enter = new progression with selected length and key");
   }

   public static void main(String[] args) {
      System.setProperty("sun.awt.noerasebackground", "true");
      // Create the game
      MusicMaker game = new MusicMaker();
      game.frame.setResizable(true);
      game.frame.setTitle(MusicMaker.title);
      game.frame.add(game);
      game.frame.setJMenuBar(game.makeMenu());
      game.frame.pack();
      game.frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
      game.frame.setLocationRelativeTo(null);
      game.frame.setVisible(true);

      // Start the game
      game.start();
   }
}
