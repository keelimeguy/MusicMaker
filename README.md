# MusicMaker

A program to make, generate, and play: chord progressions, phrases, and other such musical things


## Usage

### Compiling
From the "src/" directory run:

`ant` to compile the code and generate the .jar file

`ant run` to compile the code then run the generated .jar file

`ant warnings` to compile while showing all compiler warnings

`ant clean` to remove files created from compiling

- Compiled class files will be moved to the "build/classes/" directory
- The generated executable .jar file will be moved to the "dist/lib/" directory

### Command line
From the "build/classes/" directory run:

`java musicmaker.theory.Chord <chordId>` to display notes in the given chord

`java musicmaker.theory.Progression <key> <start> <length>` to generate a progression with given parameters

`java musicmaker.theory.ProgressionMap <key>` to show the graph used for generating the progression in the given key

`java musicmaker.theory.Pitch <note> <octave>` to play the given note at the given octave

`java musicmaker.theory.Scale <key> <mode>` to play a scale in the given key and mode

`java musicmaker.theory.instrument.Ukulele [type] <chord|note>` to show ukulele fret positions for the given chord or note w/ [type] = '-c', '-n', '-lc', or '-ln' (l for low g, c for chord, n for note)

`java musicmaker.theory.instrument.Guitar [type] <chord|note>` to show guitar fret positions for the given chord or note w/ [type] = '-c' or '-n' (c for chord, n for note)

`java musicmaker.sound.StaffPlayer <tempo> <beatsPerMeasure> <chord1> <length1> <chord2> <length2> ...` to play the given chords

`java musicmaker.sound.StaffPlayer <file>` to play the sequence in the given file
	file format (see samples/):<br>
		\<tempo> \<beatsPerMeasure> \<numThreads> \<quarterLength><br>
		[{ \<groups>]<br>
		\<length> [\<pitch> \<octave>] [\<pitch> \<octave>] ..<br>
		..<br>
		[,]<br>
		\<length> [\<pitch> \<octave>] [\<pitch> \<octave>] ..<br>
		..<br>
		[,]<br>
		..<br>
		[}]<br>
		[{ \<groups>]<br>
		..<br>
		[}]<br>
		end

`java musicmaker.theory.instrument.ConvertTool <instrument> <file>` to convert the given file of the above format to fret positions for the given instrument type ("-g" for guitar, "-u" for ukulele, "-lu" for low g ukulele)

From the "python_tools/" directory run:

`python mus_to_tab.py [-h] <-t TAB> <-j JAVA_DIR> [-v] [-c] <-f FILE> <-o OUT>` to convert a file from the custom format above to a pdf tab sheet
