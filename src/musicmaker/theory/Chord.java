package musicmaker.theory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.lang.Exception;
import java.lang.Integer;
import java.lang.Math;

public class Chord {
   
   private static final String CHORD_REGEX = "(?<key>[A-G][#b]?)(?<type>(m|M|dim|aug|mM)?)(?<add>([0-9]*)?)(?<bass>((/[A-G][#b]?)?|(/[0-9]+)?))(?<adjust>((([#b]|add|no[b#]?)[0-9]+)|sus[24])*)";
   private static final String REDUCED_CHORD_REGEX = "[A-G][#b]?(m|M|dim|aug|mM)?([0-9]*)?((/[A-G][#b]?)?|(/[0-9]+)?)((([#b]|add|no[b#]?)[0-9]+)|sus[24])*";
   private ArrayList<Note> notes;

   public Chord() {
      notes = new ArrayList<Note>();
   }

   public Chord(String chordId) {
      notes = new ArrayList<Note>();
      make(chordId);
   }

   private Chord make(String chordId) {
      clear();

      Pattern reg = Pattern.compile(CHORD_REGEX);
      Matcher matcher = reg.matcher(chordId);
      
      String key = "", type = "", add = "", adjust = "", bass = "";
      
      try {
         matcher.matches();
         key = matcher.group("key");
         type = matcher.group("type");
         add = matcher.group("add");
         adjust = matcher.group("adjust");
         bass = matcher.group("bass");
      } catch(Exception e){}

      if (key.length() != 0) {

         // Automatically add root
         Note root = Note.get(key).normal();
         add(root);
         
         // Seventh will get one flat
         int minor7 = 1;

         if (type.length() != 0 ) {
            
            // Add the appropriate third
            if (type.equals("m") || type.equals("dim") || type.equals("mM"))
               add(root.halfStep(findStep(3)).flat());
            else 
               add(root.halfStep(findStep(3)));
            
            if (type.equals("M") || type.equals("mM")) // Don't flatten seventh if major
               minor7 = 0;

            // Add the appropriate fifth
            if (type.equals("dim")) {
               add(root.halfStep(findStep(5)).flat());

               // Flatten seventh twice if diminished
               minor7 = 2;
            }
            else if (type.equals("aug"))
               add(root.halfStep(findStep(5)).sharp());
            else
               add(root.halfStep(findStep(5)));

         } else { // Automatically add third and fifth
            add(root.halfStep(findStep(3)));
            add(root.halfStep(findStep(5))); 
         }
         
         if (add.length() != 0) {
            int addId = Integer.parseInt(add);
            /*
            if(addId > 7) // Add seventh when notes above seventh are implicitly added
               if (minor7==0) 
                  add(root.halfStep(findStep(7)));
               else if (minor7==1) 
                  add(root.halfStep(findStep(7)).flat());
               else if (minor7==2) 
                  add(root.halfStep(findStep(7)).flat().flat());
            */
            if (addId == 7 && minor7==1)
               add(root.halfStep(findStep(addId)).flat());
            else if (addId == 7 && minor7==2)
               add(root.halfStep(findStep(addId)).flat().flat());
            else {
               if (addId != 7 && (type.equals("M") || type.equals("mM"))) // Add seventh when explicitly major
                  add(root.halfStep(findStep(7)));
               add(root.halfStep(findStep(addId)));
            }
         } else if (type.equals("M") || type.equals("mM")) // Add seventh when explicitly major
            add(root.halfStep(findStep(7)));
         
         if (adjust.length() != 0) {

            // First deal with omissions so that we don't overwrite other adjustments
            String noBase = adjust.replaceAll("(((((?<!no)#)|((?<!no)b)|add)[0-9]+)|sus[24])*", "");
            for (String noSplit: noBase.split("no")) {
               if (noSplit.length() != 0)
                  if (noSplit.charAt(0) == 'b')
                     notes.remove(root.halfStep(findStep(Integer.parseInt(noSplit.substring(1)))).flat());
                  else if (noSplit.charAt(0) == '#')
                     notes.remove(root.halfStep(findStep(Integer.parseInt(noSplit.substring(1)))).sharp());
                  else
                     notes.remove(root.halfStep(findStep(Integer.parseInt(noSplit))));
            }

            // Next deal with the suspended third so that thirds added later aren't overwritten
            String susBase = adjust.replaceAll("(([#b]|add|no[b#]?)[0-9]+)*", "");
            for (String susSplit: susBase.split("sus")) {
               if (susSplit.length() != 0) {
                     int nextStep = Integer.parseInt(susSplit);
                     // Remove existing third to be suspended
                     if (type.equals("m") || type.equals("dim"))
                        notes.remove(root.halfStep(findStep(3)).flat());
                     else
                        notes.remove(root.halfStep(findStep(3)));
                     add(root.halfStep(findStep(nextStep)));
               }
            }
            
            // Deal with sharps
            String sharpBase = adjust.replaceAll("(((b|add|no[b#]?)[0-9]+)|sus[24])*", "");
            for (String sharpSplit: sharpBase.split("#")) {
               if (sharpSplit.length() != 0) {
                  Note newNote = root.halfStep(findStep(Integer.parseInt(sharpSplit)));
                  // Remove existing note to be sharpened
                  notes.remove(newNote);
                  add(newNote.sharp());
               }
            }

            // Deal with flats
            String flatBase = adjust.replaceAll("(((#|add|no[b#]?)[0-9]+)|sus[24])*", "");
            for (String flatSplit: flatBase.split("b")) {
               if (flatSplit.length() != 0) {
                  Note newNote = root.halfStep(findStep(Integer.parseInt(flatSplit)));
                  // Remove existing note to be flattened
                  notes.remove(newNote);
                  add(newNote.flat());
               }
            }

            // Deal with additions last so that additions aren't overwritten by flattening or sharpening
            String addBase = adjust.replaceAll("((([#b]|no[b#]?)[0-9]+)|sus[24])*", "");
            for (String addSplit: addBase.split("add"))
               if (addSplit.length() != 0)
                  add(root.halfStep(findStep(Integer.parseInt(addSplit))));
         }

         Note bassNote = root;

         if (bass.length() != 0) {
            String bassId = bass.split("/")[1];
            if (bassId.charAt(0) >= '0' && bassId.charAt(0) <= '9')
               bassNote = root.halfStep(findStep(Integer.parseInt(bassId)));
            else {
               bassNote = Note.get(bassId);
               add(bassNote);
            }
         }

         // Order notes in relation to bass
         order(bassNote);
      } else {
         System.err.println("Error: Invalid chord ?<chordId>=\"" + chordId + "\" in  Chord.make(<chordId>)" +
            "\n\t<chordId> must match \"" + REDUCED_CHORD_REGEX + "\"");
         System.exit(-1);
      }

      System.out.println("key: " + key);
      System.out.println("type: " + type);
      System.out.println("add: " + add);
      System.out.println("adjust: " + adjust);
      System.out.println("bass: " + bass);

      System.out.println();

      for (Note note: notes)
         System.out.println("  "+note);
      
      return null;
   }

   // Calculate needed halfsteps to reach given note of scale
   public static int findStep (int notePos) {
      int note = notePos;
      int step = 0;
      if (note > 7) { // Adjust for octaves
         note = (notePos % 8) + 1;
         step = (notePos / 8) * 12;
      }
      switch (note) {
         case 8:
            step += 12;
            break;
         case 7:
            step += 11;
            break;
         case 6:
            step += 9;
            break;
         case 5:
            step += 7;
            break;
         case 4:
            step += 5;
            break;
         case 3:
            step += 4;
            break;
         case 2:
            step += 2;
            break;
         case 1:
            step += 0;
            break;
         default:
            System.err.println("Error: Unexpected value ?<note>=\"" + note + "\" in Chord.findStep(?<notePos>=\"" + notePos + "\")" +
               "\n\t<notePos> must be greater than zero");
            System.exit(-1);
      } 
      return step;
   }

   public void add(Note note) {
      notes.remove(note);
      notes.add(note);
   }

   public void add(Note note, int pos) {
      notes.remove(note);
      notes.add(pos, note);
   }

   public void clear() {
      notes.clear();
   }

   public void order() {
      if(notes.isEmpty()) return;
      Collections.sort(notes);
   }

   // Sort notes so that root would be positioned first
   public void order(Note root) {
      if (notes.contains(root)) {
         order();
         Note next = notes.get(0);
         while (next != root) {
            notes.remove(next);
            notes.add(next);
            next = notes.get(0);
         }
      } else {
         notes.add(root);
         order(root);
         notes.remove(root);
      }
   }

   public static void main(String[] args) {
      if (args.length != 1) {
         System.err.println("Usage: java Chord <chordId>");
         System.exit(-1);
      }
      Chord chord = new Chord(args[0]);
   }
}