package musicmaker.theory;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Scanner;

import musicmaker.structure.EdgeWeightedDigraph;

public class ProgressionMap {

   private enum Position {
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

      public String toString() {
         return name;
      }
   }

   public EdgeWeightedDigraph graph;
   private Note key;
   private static final String NAME_SEPARATOR = "->";

   public ProgressionMap(Note key) {
      this.key = key;
      load();
   }

   private void load() {
      graph = new EdgeWeightedDigraph(Position.values().length);
      graph.addEdge(Position.I.ordinal(), Position.iim.ordinal(), 1, Position.I + NAME_SEPARATOR + Position.iim);
      graph.addEdge(Position.I.ordinal(), Position.iiim.ordinal(), 1, Position.I + NAME_SEPARATOR + Position.iiim);
      graph.addEdge(Position.I.ordinal(), Position.IV.ordinal(), 1, Position.I + NAME_SEPARATOR + Position.IV);
      graph.addEdge(Position.I.ordinal(), Position.V.ordinal(), 1, Position.I + NAME_SEPARATOR + Position.V);
      graph.addEdge(Position.I.ordinal(), Position.vim.ordinal(), 1, Position.I + NAME_SEPARATOR + Position.vim);
      graph.addEdge(Position.I.ordinal(), Position.Ibase3.ordinal(), 1, Position.I + NAME_SEPARATOR + Position.Ibase3);
      graph.addEdge(Position.I.ordinal(), Position.Ibase5.ordinal(), 1, Position.I + NAME_SEPARATOR + Position.Ibase5);
      graph.addEdge(Position.I.ordinal(), Position.IVbase1.ordinal(), 1, Position.I + NAME_SEPARATOR + Position.IVbase1);
      graph.addEdge(Position.I.ordinal(), Position.Vbase1.ordinal(), 1, Position.I + NAME_SEPARATOR + Position.Vbase1);

      graph.addEdge(Position.iim.ordinal(), Position.I.ordinal(), 1, Position.iim + NAME_SEPARATOR + Position.I);
      graph.addEdge(Position.iim.ordinal(), Position.iiim.ordinal(), 1, Position.iim + NAME_SEPARATOR + Position.iiim);
      graph.addEdge(Position.iim.ordinal(), Position.V.ordinal(), 1, Position.iim + NAME_SEPARATOR + Position.V);
      //graph.addEdge(Position.iim.ordinal(), Position.Ibase3.ordinal(), 1, Position.iim + NAME_SEPARATOR + Position.Ibase3);
      graph.addEdge(Position.iim.ordinal(), Position.Ibase5.ordinal(), 1, Position.iim + NAME_SEPARATOR + Position.Ibase5);

      graph.addEdge(Position.iiim.ordinal(), Position.I.ordinal(), 1, Position.iiim + NAME_SEPARATOR + Position.I);
      graph.addEdge(Position.iiim.ordinal(), Position.IV.ordinal(), 1, Position.iiim + NAME_SEPARATOR + Position.IV);
      graph.addEdge(Position.iiim.ordinal(), Position.vim.ordinal(), 1, Position.iiim + NAME_SEPARATOR + Position.vim);

      graph.addEdge(Position.IV.ordinal(), Position.I.ordinal(), 1, Position.IV + NAME_SEPARATOR + Position.I);
      graph.addEdge(Position.IV.ordinal(), Position.iim.ordinal(), 1, Position.IV + NAME_SEPARATOR + Position.iim);
      graph.addEdge(Position.IV.ordinal(), Position.V.ordinal(), 1, Position.IV + NAME_SEPARATOR + Position.V);
      graph.addEdge(Position.IV.ordinal(), Position.Ibase3.ordinal(), 1, Position.IV + NAME_SEPARATOR + Position.Ibase3);
      graph.addEdge(Position.IV.ordinal(), Position.Ibase5.ordinal(), 1, Position.IV + NAME_SEPARATOR + Position.Ibase5);

      graph.addEdge(Position.V.ordinal(), Position.I.ordinal(), 1, Position.V + NAME_SEPARATOR + Position.I);
      graph.addEdge(Position.V.ordinal(), Position.iiim.ordinal(), 1, Position.V + NAME_SEPARATOR + Position.iiim);
      graph.addEdge(Position.V.ordinal(), Position.vim.ordinal(), 1, Position.V + NAME_SEPARATOR + Position.vim);

      graph.addEdge(Position.vim.ordinal(), Position.iim.ordinal(), 1, Position.vim + NAME_SEPARATOR + Position.iim);
      graph.addEdge(Position.vim.ordinal(), Position.IV.ordinal(), 1, Position.vim + NAME_SEPARATOR + Position.IV);

      graph.addEdge(Position.Ibase3.ordinal(), Position.iim.ordinal(), 1, Position.Ibase3 + NAME_SEPARATOR + Position.iim);
      //graph.addEdge(Position.Ibase3.ordinal(), Position.IV.ordinal(), 1, Position.Ibase3 + NAME_SEPARATOR + Position.IV);

      graph.addEdge(Position.Ibase5.ordinal(), Position.V.ordinal(), 1, Position.Ibase5 + NAME_SEPARATOR + Position.V);

      graph.addEdge(Position.IVbase1.ordinal(), Position.I.ordinal(), 1, Position.IVbase1 + NAME_SEPARATOR + Position.I);

      graph.addEdge(Position.Vbase1.ordinal(), Position.I.ordinal(), 1, Position.Vbase1 + NAME_SEPARATOR + Position.I);
   }

   public static void main(String[] args) {
      ProgressionMap progressionMapC = new ProgressionMap(Note.get("C"));
      System.out.println(progressionMapC.graph);
   }
}
