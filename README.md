# MusicMaker

A program to make, generate, and play: chord progressions, phrases, and other such musical things


## Usage

### Compiling
From the src/ directory run:

`ant` to compile the code and generate the .jar file

`ant run` to compile the code then run the generated .jar file

`ant clean` to remove files created from compiling

- Compiled class files will be moved to the build/classes/ directory
- The generated executable .jar file will be moved to the dist/lib/ directory

### Command line
From the build/classes/ directory run:

`java musicmaker.theory.Chord <chordId>` to display notes in the given chord

`java musicmaker.theory.Progression <key> <start> <length>` to generate a progression of with given parameters

`java musicmaker.theory.ProgressionMap <key>` to show the graph used for generating the progression in the given key

`java musicmaker.theory.Pitch <note> <octave>` to play the given note at the given octave
