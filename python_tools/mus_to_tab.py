import os
import argparse

from subprocess import *

header = """\\documentclass{article}
\\usepackage{musixtex,graphicx}

% custom clef
\\newcommand\\TAB[1]{%
  \\setclefsymbol{#1}{\\,\\rotatebox{90}{TAB}}%
  \\setclef{#1}9}

% internal string choosing command
%  #1: string (a number from 1--6)
%  #2: finger
\\makeatletter
\\newcommand\\@str[2]{%
  \\ifcase#1\\relax\\@strerror
  \\or\\def\\@strnr{-1}%
  \\or\\def\\@strnr{1}%
  \\or\\def\\@strnr{3}%
  \\or\\def\\@strnr{5}%
  \\or\\def\\@strnr{7}%
  \\or\\def\\@strnr{9}%
  \\else\\@strerror
  \\fi
  \\zchar\\@strnr{\\footnotesize#2}}
% \\@strerror could be defined to issue some warning/error

% User level commands
\\newcommand\\STr[2]{\\@str{#1}{#2}\\sk}  % with a full note skip
\\newcommand\\Str[2]{\\@str{#1}{#2}\\hsk} % with a half note skip
\\newcommand\\str[2]{\\@str{#1}{#2}}     % with no skip
\\makeatother
\\begin{document}

\\setlength\\parindent{0pt}
\\begin{music}
 \\instrumentnumber{2}
 \\nobarnumbers
 \\TAB1
"""

def jarWrapper(*args):
    process = Popen(['java']+list(args), stdout=PIPE, stderr=PIPE)
    ret = []
    while process.poll() is None:
        line = process.stdout.readline()
        if line != '' and line.endswith('\n'):
            ret.append(line[:-1])
    stdout, stderr = process.communicate()
    ret += stdout.split('\n')
    if stderr != '':
        ret += stderr.split('\n')
    ret.remove('')
    return ret

if __name__ == "__main__":
    parser = argparse.ArgumentParser(
        description='Convert a custom .mus file into a readable pdf.')
    parser.add_argument('-t', '--tab', default=None,
                        help='Tab type argument: ukulele, guitar, or none')
    parser.add_argument('-j', '--java_dir', default="../build/classes",
                        help='Top directory of musicmaker java classes hierarchy')
    parser.add_argument('-v', '--verbose', action="store_true",
                        help='Print all possible instrument positions as well')
    parser.add_argument('-f', '--file', default=None,
                        help='Input file of .mus type')
    parser.add_argument('-o', '--out', default=None,
                        help='Output file of .tex type')
    args = parser.parse_args()

    if args.file == None:
        print("No input file.")
        exit()

    classname = "musicmaker.theory.instrument.ConvertTool"
    instrument = None
    strings = 0
    if args.tab == "ukulele":
        instrument = "-u"
        strings = 4
    elif args.tab == "lgukulele":
        instrument = "-lu"
        strings = 4
    elif args.tab == "guitar":
        instrument = "-g"
        strings = 6
    else:
        print("Invalid tab type {}".format(args.tab))
        exit()

    header += " \\setlines1{"+str(strings)+"}\n \\startpiece\n"

    beats = []
    current_dir = os.getcwd()
    if args.file and os.path.isfile(os.path.join(current_dir,args.file)):
        with open(os.path.join(current_dir,args.file),"r") as f:
            line = f.readline().split()
            max_beats = int(line[1])
            quarter_beat = int(line[3])
    else:
        print("Could not find file: {}".format(args.file))
        exit()

    if args.out:
        with open(os.path.join(current_dir,args.out),"w") as o:

            o.write(header)
            jargs = ['-cp', args.java_dir, classname, instrument,args.file]
            result = jarWrapper(*jargs)
            lastMeasure = -1
            lastBeat = -1
            beat = 0
            string = 1
            inNotes = False
            catch_up_str = []
            for r in result:
                if args.verbose:
                    print(r)
                r = r.split()
                if len(r)>0:
                    if r[0][0] == 'M':
                        measure = int(r[0].split("=")[1])
                        beat = int(r[1].split("=")[1])

                        if len(r)>2:
                            if inNotes:
                                measureDiff = measure-lastMeasure
                                if measureDiff >= 1:
                                    beatDiff = measureDiff*max_beats - lastBeat + beat
                                else:
                                    beatDiff = beat-lastBeat
                                if beatDiff == 0:
                                    beatDiff = max_beats
                                if beatDiff <= quarter_beat:
                                    noteHead = "\\zq"
                                    noteHeadMain = "\\qa"
                                else:
                                    noteHead = "\\zh"
                                    noteHeadMain = "\\ha"

                                if beatDiff >= max_beats:
                                    noteHeadMain = "\\wh"
                                    noteHead = "\\zw"
                                elif beatDiff >= max_beats/2:
                                    noteHeadMain = "\\ha"
                                elif beatDiff >= max_beats/4:
                                    noteHeadMain = "\\qa"
                                elif beatDiff >= max_beats/8:
                                    noteHeadMain = "\\ca"
                                elif beatDiff >= max_beats/16:
                                    noteHeadMain = "\\cca"
                                elif beatDiff >= max_beats/32:
                                    noteHeadMain = "\\ccca"
                                elif beatDiff >= max_beats/64:
                                    noteHeadMain = "\\cccca"
                                elif beatDiff >= max_beats/128:
                                    noteHeadMain = "\\ccccca"

                                o.write(" & ");
                                for i in range(len(catch_up_str)):
                                    if i != len(catch_up_str)-1:
                                        o.write(noteHead+catch_up_str[i]);
                                    else:
                                        o.write(noteHeadMain+catch_up_str[i] + "\\en\n");
                                catch_up_str = []
                                inNotes = False
                            values = r[3:]
                            last_octave = 0
                            for i in range(len(values)):
                                value = values[i].split(",")[0]
                                note = value[0]
                                accidental = ""
                                if value[1] == "#":
                                    value = value[1:]
                                    accidental = "^"
                                elif value[1] == "b":
                                    value = value[1:]
                                    accidental = "_"
                                octave = int(value[1:])
                                if note < 'C':
                                    octave -= 1
                                else:
                                    octave -= 2
                                oct_str = ""
                                if octave > 0:
                                    oct_str = (octave-last_octave)*'\''
                                    last_octave = octave
                                if i == len(values)-1:
                                    catch_up_str.append("{" + oct_str+accidental+note + "}")
                                else:
                                    catch_up_str.append("{" + oct_str+accidental+note + "}")

                        if measure != lastMeasure and lastMeasure != -1:
                            o.write(" \\bar\n")
                        lastMeasure = measure
                        lastBeat = beat
                        string = 1
                    elif len(r)>1:
                        if not inNotes:
                            o.write("   \\Notes\\hsk")
                        inNotes = True
                        if "(" in r[1]:
                            r[1] = r[1].split("(")[1].split(")")[0]
                        o.write("\\str{" + str(string) + "}{" + r[1] + "}")
                        string += 1
            if inNotes:
                beat = max_beats-lastBeat
                measureDiff = measure-lastMeasure
                if measureDiff >= 1:
                    beatDiff = measureDiff*max_beats - lastBeat + beat
                else:
                    beatDiff = beat-lastBeat
                if beatDiff == 0:
                    beatDiff = max_beats
                if beatDiff <= quarter_beat:
                    noteHead = "\\zq"
                    noteHeadMain = "\\qa"
                else:
                    noteHead = "\\zh"
                    noteHeadMain = "\\ha"

                if beatDiff >= max_beats:
                    noteHeadMain = "\\wh"
                    noteHead = "\\zw"
                elif beatDiff >= max_beats/2:
                    noteHeadMain = "\\ha"
                elif beatDiff >= max_beats/4:
                    noteHeadMain = "\\qa"
                elif beatDiff >= max_beats/8:
                    noteHeadMain = "\\ca"
                elif beatDiff >= max_beats/16:
                    noteHeadMain = "\\cca"
                elif beatDiff >= max_beats/32:
                    noteHeadMain = "\\ccca"
                elif beatDiff >= max_beats/64:
                    noteHeadMain = "\\cccca"
                elif beatDiff >= max_beats/128:
                    noteHeadMain = "\\ccccca"


                o.write(" & ");
                for i in range(len(catch_up_str)):
                    if i != len(catch_up_str)-1:
                        o.write(noteHead+catch_up_str[i]);
                    else:
                        o.write(noteHeadMain+catch_up_str[i] + "\\en\n");

            o.write("""  \\endpiece
\\end{music}

\\end{document}
""")
    else:
        print("Could not find file: {}".format(args.out))

    exit()
