package musicmaker.theory;

import musicmaker.structure.EdgeWeightedDigraph;

import java.util.Random;

public class ProgressionMap {

   public enum Position {
      I("I", 1, "", new String[]{"2","6","M7","M9","sus4"}), iim("iim", 2, "m", new String[]{"m7","m9"}),
         iiim("iiim", 3, "m", new String[]{"m7"}), IV("IV", 4, "", new String[]{"6", "M7", "m", "m6"}),
         V("V", 5, "", new String[]{"7","9","11","13","sus4"}), vim("vim", 6, "m", new String[]{"m7", "m9"}),
         Ibase3("I/3", 1, "/3", new String[]{}), Ibase5("I/5", 1, "/5", new String[]{}),
         IVbase1("IV/1", 4, "/1", new String[]{}), Vbase1("V/1", 5, "/1", new String[]{}),

         // Mb9no7 used instead of b9 for now to avoid conflicts with Bb 9 and B b9 for instance
         IIIm7b5("IIIm7b5", 3, "m7b5", new String[]{}), VI("VI", 6, "", new String[]{"7", "9", "Mb9no7"}),
         sharpIdim7("#Idim7", 1.5, "dim7", new String[]{}), sharpIVm7b5("#IVm7b5", 4.5, "m7b5", new String[]{}),
         VII("VII", 7, "", new String[]{"7", "9", "Mb9no7"}), sharpIIdim7("#IIdim7", 2.5, "dim7", new String[]{}),
         Vm("Vm", 5, "m", new String[]{"7"}), I_ALT("I*", 1, "7", new String[]{"7", "9", "Mb9no7"}),
         Im6("Im6", 1, "m6", new String[]{}), Vbase2("V/2", 5, "/2", new String[]{}),
         II("II", 2, "", new String[]{"7", "9", "Mb9no7"}), bVI("bVI", -6.5, "", new String[]{}),
         bVII("bVII", -7.5, "", new String[]{"9"}), IVm7("IVm7", 4, "m7", new String[]{}),
         bII7("bII7", -2.5, "7", new String[]{}), VIm7b5baseb3("VIm7b5/b3", 6, "m7b5/b3", new String[]{}),
         sharpVdim7("#5dim7", 5.5, "dim7", new String[]{}), III("III", 3, "", new String[]{"7", "9", "Mb9no7"}),
         VIIm7b5("VIIm7b5", 7, "m7b5", new String[]{}), bVI7("bVI7", -6.5, "7", new String[]{}),
         bVII9("bVII9", -7.5, "9", new String[]{}), Idimbaseb3("Idim/b3", 1, "dim/b3", new String[]{});

      private Position(String name, double rootPos, String baseAdjust, String[] adjustments) {
         this.rootPos = rootPos;
         this.baseAdjust = baseAdjust;
         this.adjustments = adjustments;
         this.name = name;
      }

      private String[] adjustments;
      private String baseAdjust;
      private double rootPos;
      private String name;

      public Chord getBaseChord(Note key) {
         double pos = Math.abs(rootPos);
         Note note = key.halfStep(Chord.findStep((int)pos));
         if ((double)((int)pos) != pos){
            if (rootPos < 0)
               note = note.flat();
            else
               note = note.sharp();
         }
         String adjust = baseAdjust;
         if (adjust.matches(".*/(##?|bb?)?[0-9]+")) {
            int step = 0;
            String orig = "";
            if (adjust.indexOf("/") != 0)
               orig = adjust.substring(0, adjust.indexOf("/"));
            adjust = adjust.substring(adjust.indexOf("/"));
            if (adjust.charAt(1) == '#') {
               step++;
               adjust = adjust.substring(2);
            } else if (adjust.charAt(1) == 'b') {
               step--;
               adjust = adjust.substring(2);
            } else
               adjust = adjust.substring(1);

            if (adjust.charAt(0) == '#') {
               step++;
               adjust = adjust.substring(1);
            } else if (adjust.charAt(0) == 'b') {
               step--;
               adjust = adjust.substring(1);
            }

            adjust = orig + "/" + key.halfStep(Chord.findStep(Integer.parseInt(adjust)) + step);
         }
         return new Chord("" + note + adjust);
      }

      public Chord getAdjustedChord(Note key, int i) {
         if (!(i >= 0 && i < adjustments.length))
            return getBaseChord(key);
         double pos = Math.abs(rootPos);
         Note note = key.halfStep(Chord.findStep((int)pos));
         if ((double)((int)pos) != pos){
            if (rootPos < 0)
               note = note.flat();
            else
               note = note.sharp();
         }
         String adjust = adjustments[i];
         // if (adjust.matches("/[0-9]+"))
            // adjust = "/" + key.halfStep(Chord.findStep(Integer.parseInt(adjust.substring(1))));
         return new Chord("" + note + adjust);
      }

      public String[] getAdjustments() { return adjustments; }

      public static Position get(int ordinal) {
         if (ordinal >= 0 && ordinal < Position.values().length)
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

   public ProgressionMap(String key, String start) {
      this.key = Note.get(key);
      if (key == null) {
         System.err.println("Error: Invalid key ?<key>=\"" + key +"\" in ProgressionMap(<key>, ?<start>=\"" + start + "\")");
         System.exit(-1);
      }
      startPos = Position.get(start);
      if (startPos == null) {
         System.err.println("Error: Invalid position id ?<start>=\"" + start +"\" in ProgressionMap(?<key>=\"" + key + "\", <start>)");
         System.exit(-1);
      }
      setup();
   }

   public ProgressionMap(String key) {
      this.key = Note.get(key);
      if (key == null) {
         System.err.println("Error: Invalid key ?<key>=\"" + key +"\" in ProgressionMap(<key>)");
         System.exit(-1);
      }
      startPos = Position.I;
      setup();
   }

   public void setKey(Note key) {
      this.key = key;
   }

   public void setStart(Position startPos) {
      this.startPos = startPos;
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

   private Chord getRandomChordExtension(Position pos) {
      Random rand = new Random();
      String[] adj = pos.getAdjustments();
      if (adj.length == 0)
         return pos.getBaseChord(key);
      return pos.getAdjustedChord(key, rand.nextInt(adj.length + 1));
   }

   public Progression generate(int length, boolean loop, boolean extension) {
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
         if (extension) {
            progression.add(getRandomChordExtension(curPos));
         } else
            progression.add(getChord(curPos));
      }

      return progression;
   }

   public Progression generate(int length) {
      return generate(length, true, false);
   }

   public String toString() {
      String str = map.V() + " " + map.E() + "\n";
      if (key != null)
         str = "key: " + key + "\n" + str;
      for (int v = 0; v < map.V(); v++) {
         str += v + ": \t";
         for (EdgeWeightedDigraph.DirectedEdge e: map.adj(v)) {
            str += e + "\t";
            if (e.value().length() <= 5)
               str += "\t";
            str += "\t" + convertAsString(e) + "\n     \t";
         }
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
            "\n\t<key> should match [A-G](##?|bb?)?");
         System.exit(-1);
      }

      ProgressionMap progressionMap = new ProgressionMap(key);
      progressionMap.show();
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




      map.addEdge(Position.I.ordinal(), Position.IIIm7b5.ordinal(), 1, Position.I + NAME_SEPARATOR + Position.IIIm7b5);
      map.addEdge(Position.I.ordinal(), Position.VI.ordinal(), 1, Position.I + NAME_SEPARATOR + Position.VI);
      map.addEdge(Position.I.ordinal(), Position.sharpIdim7.ordinal(), 1, Position.I + NAME_SEPARATOR + Position.sharpIdim7);
      map.addEdge(Position.I.ordinal(), Position.sharpIVm7b5.ordinal(), 1, Position.I + NAME_SEPARATOR + Position.sharpIVm7b5);
      map.addEdge(Position.I.ordinal(), Position.VII.ordinal(), 1, Position.I + NAME_SEPARATOR + Position.VII);
      map.addEdge(Position.I.ordinal(), Position.sharpIIdim7.ordinal(), 1, Position.I + NAME_SEPARATOR + Position.sharpIIdim7);
      map.addEdge(Position.I.ordinal(), Position.Vm.ordinal(), 1, Position.I + NAME_SEPARATOR + Position.Vm);
      map.addEdge(Position.I.ordinal(), Position.I_ALT.ordinal(), 1, Position.I + NAME_SEPARATOR + Position.I_ALT);
      map.addEdge(Position.I.ordinal(), Position.Im6.ordinal(), 1, Position.I + NAME_SEPARATOR + Position.Im6);
      map.addEdge(Position.I.ordinal(), Position.Vbase2.ordinal(), 1, Position.I + NAME_SEPARATOR + Position.Vbase2);
      map.addEdge(Position.I.ordinal(), Position.II.ordinal(), 1, Position.I + NAME_SEPARATOR + Position.II);
      map.addEdge(Position.I.ordinal(), Position.bVI.ordinal(), 1, Position.I + NAME_SEPARATOR + Position.bVI);
      map.addEdge(Position.I.ordinal(), Position.bVII.ordinal(), 1, Position.I + NAME_SEPARATOR + Position.bVII);
      map.addEdge(Position.I.ordinal(), Position.IVm7.ordinal(), 1, Position.I + NAME_SEPARATOR + Position.IVm7);
      map.addEdge(Position.I.ordinal(), Position.bII7.ordinal(), 1, Position.I + NAME_SEPARATOR + Position.bII7);
      map.addEdge(Position.I.ordinal(), Position.VIm7b5baseb3.ordinal(), 1, Position.I + NAME_SEPARATOR + Position.VIm7b5baseb3);
      map.addEdge(Position.I.ordinal(), Position.sharpVdim7.ordinal(), 1, Position.I + NAME_SEPARATOR + Position.sharpVdim7);
      map.addEdge(Position.I.ordinal(), Position.VIIm7b5.ordinal(), 1, Position.I + NAME_SEPARATOR + Position.VIIm7b5);
      map.addEdge(Position.I.ordinal(), Position.III.ordinal(), 1, Position.I + NAME_SEPARATOR + Position.III);
      map.addEdge(Position.I.ordinal(), Position.Idimbaseb3.ordinal(), 1, Position.I + NAME_SEPARATOR + Position.Idimbaseb3);
      map.addEdge(Position.I.ordinal(), Position.bVI7.ordinal(), 1, Position.I + NAME_SEPARATOR + Position.bVI7);
      map.addEdge(Position.I.ordinal(), Position.bVII9.ordinal(), 1, Position.I + NAME_SEPARATOR + Position.bVII9);

      map.addEdge(Position.iim.ordinal(), Position.IVm7.ordinal(), 1, Position.iim + NAME_SEPARATOR + Position.IVm7);
      map.addEdge(Position.iim.ordinal(), Position.bII7.ordinal(), 1, Position.iim + NAME_SEPARATOR + Position.bII7);

      map.addEdge(Position.IIIm7b5.ordinal(), Position.VI.ordinal(), 1, Position.IIIm7b5 + NAME_SEPARATOR + Position.VI);
      map.addEdge(Position.IIIm7b5.ordinal(), Position.IV.ordinal(), 1, Position.IIIm7b5 + NAME_SEPARATOR + Position.IV);

      map.addEdge(Position.VI.ordinal(), Position.iim.ordinal(), 1, Position.VI + NAME_SEPARATOR + Position.iim);

      map.addEdge(Position.sharpIdim7.ordinal(), Position.iim.ordinal(), 1, Position.sharpIdim7 + NAME_SEPARATOR + Position.iim);

      map.addEdge(Position.sharpIVm7b5.ordinal(), Position.VII.ordinal(), 1, Position.sharpIVm7b5 + NAME_SEPARATOR + Position.VII);
      map.addEdge(Position.sharpIVm7b5.ordinal(), Position.V.ordinal(), 1, Position.sharpIVm7b5 + NAME_SEPARATOR + Position.V);
      map.addEdge(Position.sharpIVm7b5.ordinal(), Position.Ibase5.ordinal(), 1, Position.sharpIVm7b5 + NAME_SEPARATOR + Position.Ibase5);

      map.addEdge(Position.VII.ordinal(), Position.iiim.ordinal(), 1, Position.VII + NAME_SEPARATOR + Position.iiim);

      map.addEdge(Position.sharpIIdim7.ordinal(), Position.iiim.ordinal(), 1, Position.sharpIIdim7 + NAME_SEPARATOR + Position.iiim);

      map.addEdge(Position.Vm.ordinal(), Position.I_ALT.ordinal(), 1, Position.Vm + NAME_SEPARATOR + Position.I_ALT);

      map.addEdge(Position.I_ALT.ordinal(), Position.IV.ordinal(), 1, Position.I_ALT + NAME_SEPARATOR + Position.IV);

      map.addEdge(Position.Im6.ordinal(), Position.Vbase2.ordinal(), 1, Position.Im6 + NAME_SEPARATOR + Position.Vbase2);
      map.addEdge(Position.Im6.ordinal(), Position.II.ordinal(), 1, Position.Im6 + NAME_SEPARATOR + Position.II);

      map.addEdge(Position.Vbase2.ordinal(), Position.II.ordinal(), 1, Position.Vbase2 + NAME_SEPARATOR + Position.II);

      map.addEdge(Position.II.ordinal(), Position.V.ordinal(), 1, Position.II + NAME_SEPARATOR + Position.V);

      map.addEdge(Position.bVI.ordinal(), Position.bVII.ordinal(), 1, Position.bVI + NAME_SEPARATOR + Position.bVII);

      map.addEdge(Position.bVII.ordinal(), Position.I.ordinal(), 1, Position.bVII + NAME_SEPARATOR + Position.I);

      map.addEdge(Position.IVm7.ordinal(), Position.I.ordinal(), 1, Position.IVm7 + NAME_SEPARATOR + Position.I);

      map.addEdge(Position.bII7.ordinal(), Position.I.ordinal(), 1, Position.bII7 + NAME_SEPARATOR + Position.I);

      map.addEdge(Position.VIm7b5baseb3.ordinal(), Position.II.ordinal(), 1, Position.VIm7b5baseb3 + NAME_SEPARATOR + Position.II);

      map.addEdge(Position.sharpVdim7.ordinal(), Position.vim.ordinal(), 1, Position.sharpVdim7 + NAME_SEPARATOR + Position.vim);

      map.addEdge(Position.VIIm7b5.ordinal(), Position.III.ordinal(), 1, Position.VIIm7b5 + NAME_SEPARATOR + Position.III);

      map.addEdge(Position.III.ordinal(), Position.vim.ordinal(), 1, Position.III + NAME_SEPARATOR + Position.vim);

      map.addEdge(Position.Idimbaseb3.ordinal(), Position.iim.ordinal(), 1, Position.Idimbaseb3 + NAME_SEPARATOR + Position.iim);

      map.addEdge(Position.bVI7.ordinal(), Position.Ibase5.ordinal(), 1, Position.bVI7 + NAME_SEPARATOR + Position.Ibase5);

      map.addEdge(Position.bVII9.ordinal(), Position.Ibase5.ordinal(), 1, Position.bVII9 + NAME_SEPARATOR + Position.Ibase5);
   }
}
