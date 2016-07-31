package musicmaker.theory;

import musicmaker.structure.EdgeWeightedDigraph;

import java.util.Random;

public class ProgressionMap {

   public enum Position {
      I("I", 1, "", new String[]{"2","6","M7","M9","sus"}), iim("iim", 2, "m", new String[]{"m7","m9"}),
         iiim("iiim", 3, "m", new String[]{"m7"}), IV("IV", 4, "", new String[]{"6", "M7", "m", "m6"}),
         V("V", 5, "", new String[]{"7","9","11","13","sus"}), vim("vim", 6, "m", new String[]{"m7", "m9"}),
         Ibase3("I/3", 1, "/3", new String[]{}), Ibase5("I/5", 1, "/5", new String[]{}),
         IVbase1("IV/1", 4, "/1", new String[]{}), Vbase1("V/1", 5, "/1", new String[]{});

      private Position(String name, int rootPos, String baseAdjust, String[] adjustments) {
         this.rootPos = rootPos;
         this.baseAdjust = baseAdjust;
         this.adjustments = adjustments;
         this.name = name;
      }

      private String[] adjustments;
      private String baseAdjust;
      private int rootPos;
      private String name;

      public Chord getBaseChord(Note key) {
         Note note = key.halfStep(Chord.findStep(rootPos));
         return new Chord("" + note + baseAdjust);
      }

      public static Position get(int ordinal) {
         if (ordinal >=0 && ordinal < Position.values().length)
            return Position.values()[ordinal];
         return null;
      }

      public static Position get(String name) {
         for (Position position: Position.values())
            if (position.name.equals(name))
               return position;
         return null;
      }

      public static String[] getValidNames() {
         String[] arr = new String[Position.values().length];
         for (int i = 0; i < Position.values().length; i++) {
            arr[i] = Position.get(i).toString();
         }
         return arr;
      }

      public String toString() {
         return name;
      }
   }

   private EdgeWeightedDigraph map;
   private Note key;
   private Position startPos;
   private static final String NAME_SEPARATOR = "->";

   public ProgressionMap(Note key, String start) {
      this.key = key;
      startPos = Position.get(start);
      if (startPos == null) {
         System.err.println("Error: Invalid position id ?<start>=\"" + start +"\" in ProgressionMap(?<key>=\"" + key + "\", <start>)");
         System.exit(-1);
      }
      setup();
   }

   public ProgressionMap(Note key) {
      this.key = key;
      startPos = Position.I;
      setup();
   }

   public void setKey(Note key) {
      this.key = key;
   }

   public void setStart(Position startPos) {
      this.startPos = startPos;
   }

   private void setup() {
      map = new EdgeWeightedDigraph(Position.values().length);
      map.addEdge(Position.I.ordinal(), Position.iim.ordinal(), 1, Position.I + NAME_SEPARATOR + Position.iim);
      map.addEdge(Position.I.ordinal(), Position.iiim.ordinal(), 1, Position.I + NAME_SEPARATOR + Position.iiim);
      map.addEdge(Position.I.ordinal(), Position.IV.ordinal(), 1, Position.I + NAME_SEPARATOR + Position.IV);
      map.addEdge(Position.I.ordinal(), Position.V.ordinal(), 1, Position.I + NAME_SEPARATOR + Position.V);
      map.addEdge(Position.I.ordinal(), Position.vim.ordinal(), 1, Position.I + NAME_SEPARATOR + Position.vim);
      map.addEdge(Position.I.ordinal(), Position.Ibase3.ordinal(), 1, Position.I + NAME_SEPARATOR + Position.Ibase3);
      map.addEdge(Position.I.ordinal(), Position.Ibase5.ordinal(), 1, Position.I + NAME_SEPARATOR + Position.Ibase5);
      map.addEdge(Position.I.ordinal(), Position.IVbase1.ordinal(), 1, Position.I + NAME_SEPARATOR + Position.IVbase1);
      map.addEdge(Position.I.ordinal(), Position.Vbase1.ordinal(), 1, Position.I + NAME_SEPARATOR + Position.Vbase1);

      map.addEdge(Position.iim.ordinal(), Position.I.ordinal(), 1, Position.iim + NAME_SEPARATOR + Position.I);
      map.addEdge(Position.iim.ordinal(), Position.iiim.ordinal(), 1, Position.iim + NAME_SEPARATOR + Position.iiim);
      map.addEdge(Position.iim.ordinal(), Position.V.ordinal(), 1, Position.iim + NAME_SEPARATOR + Position.V);
      //map.addEdge(Position.iim.ordinal(), Position.Ibase3.ordinal(), 1, Position.iim + NAME_SEPARATOR + Position.Ibase3);
      map.addEdge(Position.iim.ordinal(), Position.Ibase5.ordinal(), 1, Position.iim + NAME_SEPARATOR + Position.Ibase5);

      map.addEdge(Position.iiim.ordinal(), Position.I.ordinal(), 1, Position.iiim + NAME_SEPARATOR + Position.I);
      map.addEdge(Position.iiim.ordinal(), Position.IV.ordinal(), 1, Position.iiim + NAME_SEPARATOR + Position.IV);
      map.addEdge(Position.iiim.ordinal(), Position.vim.ordinal(), 1, Position.iiim + NAME_SEPARATOR + Position.vim);

      map.addEdge(Position.IV.ordinal(), Position.I.ordinal(), 1, Position.IV + NAME_SEPARATOR + Position.I);
      map.addEdge(Position.IV.ordinal(), Position.iim.ordinal(), 1, Position.IV + NAME_SEPARATOR + Position.iim);
      map.addEdge(Position.IV.ordinal(), Position.V.ordinal(), 1, Position.IV + NAME_SEPARATOR + Position.V);
      map.addEdge(Position.IV.ordinal(), Position.Ibase3.ordinal(), 1, Position.IV + NAME_SEPARATOR + Position.Ibase3);
      map.addEdge(Position.IV.ordinal(), Position.Ibase5.ordinal(), 1, Position.IV + NAME_SEPARATOR + Position.Ibase5);

      map.addEdge(Position.V.ordinal(), Position.I.ordinal(), 1, Position.V + NAME_SEPARATOR + Position.I);
      map.addEdge(Position.V.ordinal(), Position.iiim.ordinal(), 1, Position.V + NAME_SEPARATOR + Position.iiim);
      map.addEdge(Position.V.ordinal(), Position.vim.ordinal(), 1, Position.V + NAME_SEPARATOR + Position.vim);

      map.addEdge(Position.vim.ordinal(), Position.iim.ordinal(), 1, Position.vim + NAME_SEPARATOR + Position.iim);
      map.addEdge(Position.vim.ordinal(), Position.IV.ordinal(), 1, Position.vim + NAME_SEPARATOR + Position.IV);

      map.addEdge(Position.Ibase3.ordinal(), Position.iim.ordinal(), 1, Position.Ibase3 + NAME_SEPARATOR + Position.iim);
      //map.addEdge(Position.Ibase3.ordinal(), Position.IV.ordinal(), 1, Position.Ibase3 + NAME_SEPARATOR + Position.IV);

      map.addEdge(Position.Ibase5.ordinal(), Position.V.ordinal(), 1, Position.Ibase5 + NAME_SEPARATOR + Position.V);

      map.addEdge(Position.IVbase1.ordinal(), Position.I.ordinal(), 1, Position.IVbase1 + NAME_SEPARATOR + Position.I);

      map.addEdge(Position.Vbase1.ordinal(), Position.I.ordinal(), 1, Position.Vbase1 + NAME_SEPARATOR + Position.I);
   }

   private String convertAsString(EdgeWeightedDigraph.DirectedEdge e) {
      Chord from = Position.get(e.from()).getBaseChord(key);
      Chord to = Position.get(e.to()).getBaseChord(key);
      return from + NAME_SEPARATOR + to;
   }

   private Chord convertNextChord(EdgeWeightedDigraph.DirectedEdge e) {
      return Position.get(e.to()).getBaseChord(key);
   }

   private Chord convertPrevChord(EdgeWeightedDigraph.DirectedEdge e) {
      return Position.get(e.from()).getBaseChord(key);
   }

   private Position getNextPosition(EdgeWeightedDigraph.DirectedEdge e) {
      return Position.get(e.to());
   }

   private Position getPrevPosition(EdgeWeightedDigraph.DirectedEdge e) {
      return Position.get(e.from());
   }

   private Chord getChord(Position pos) {
      return pos.getBaseChord(key);
   }

   public Progression generate(int length, boolean loop) {
      if (length <= 0) {
         System.err.println("Error: Invalid progression length ?<length>=\"" + length +"\"" +
               "\n\t<length> must be a number greater than zero");
         System.exit(-1);
      }

      Progression progression = new Progression(loop);
      Position curPos = startPos;
      progression.add(getChord(curPos));
      for (int i = 1; i < length; i++) {
         Random rand = new Random();
         int next = rand.nextInt(map.outdegree(curPos.ordinal()));
         curPos = getNextPosition(map.adj(curPos.ordinal()).get(next));
         progression.add(getChord(curPos));
      }

      return progression;
   }

   public Progression generate(int length) {
      return generate(length, true);
   }

   public String toString() {
      String str = map.V() + " " + map.E() + "\n";
      if (key != null)
         str = "key: " + key + "\n" + str;
      for (int v = 0; v < map.V(); v++) {
         str += v + ": ";
         for (EdgeWeightedDigraph.DirectedEdge e: map.adj(v))
            str += e + "  \t" + convertAsString(e) + "\n   ";
         str += "\n";
      }
      return str;
   }

   public void show() {
      System.out.println(this);
   }

   public String getValidNamesAsString() {
      String[] validNames = Position.getValidNames();
      String namesList = "\"";
      for (int i = 0; i < validNames.length; i++) {
         namesList += validNames[i];
         if (i != validNames.length - 1) namesList += "\", \"";
      }
      namesList += "\"";
      return namesList;
   }

   public static void main(String[] args) {
      if (args.length != 1) {
         System.err.println("Usage: java ProgressionMap <key>");
         System.exit(-1);
      }

      Note key = Note.get(args[0]);
      if (key == null) {
         System.err.println("Error: Invalid key ?<key>=\"" + args[0] +"\"" +
            "\n\t<key> should match [A-G][#b]?");
         System.exit(-1);
      }

      ProgressionMap progressionMap = new ProgressionMap(key);
      progressionMap.show();
   }
}
