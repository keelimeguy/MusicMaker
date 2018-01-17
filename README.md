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

`java musicmaker.theory.instrument.Ukulele <chord>` to show ukulele fret positions for the given chord

`java musicmaker.theory.instrument.Guitar <chord>` to show guitar fret positions for the given chord

`java musicmaker.sound.StaffPlayer <tempo> <beatsPerMeasure> <chord1> <length1> <chord2> <length2> ...` to play the given chords

`java musicmaker.sound.StaffPlayer <file>` to play the sequence in the given file
	file format: <tempo> <beatsPerMeasure> <newline> [newline] <length> [<pitch1> <octave>] .. [newline] <length> [<pitch2> <octave>] .. end
		where [newline] is an optional line break (<newline> not optional) and signifies that the following pitches will all start at the subsequent beat in the song and multiple newlines may be put in a row when separated by a <length>, <octave> is an optional numerical input used to change the octave, and "end" is placed at the end of the sequence in the file. Other text may appear after end but is ignored. At the moment, pitches are supported, but not chords.
