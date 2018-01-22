import os
import argparse

from subprocess import *

note_dict = { "'A":"H", "''A":"a", "'''A":"h", "''''A":"o", "'''''A":"v",
              "'B":"I", "''B":"b", "'''B":"i", "''''B":"p", "'''''B":"w",
              "'C":"J", "''C":"c", "'''C":"j", "''''C":"q", "'''''C":"x",
              "'D":"K", "''D":"d", "'''D":"k", "''''D":"r", "'''''D":"y",
              "'E":"L", "''E":"e", "'''E":"l", "''''E":"s", "'''''E":"z",
              "'F":"M", "''F":"f", "'''F":"m", "''''F":"t",
              "'G":"N", "''G":"g", "'''G":"n", "''''G":"u"              }

header = """\\documentclass{article}
\\usepackage{musixtex,graphicx}
\\usepackage[margin=.5in]{geometry}

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
    parser.add_argument('-c', '--connected', action="store_true",
                        help='Connect consecutive eighth notes, etc.')
    parser.add_argument('-f', '--file', default=None,
                        help='Input file of .mus type')
    parser.add_argument('-o', '--out', default=None,
                        help='Output file of .tex type')
    args = parser.parse_args()

    if args.file == None:
        print("No input file.")
        exit()

    using_connected_notes = args.connected
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
            string = strings
            inNotes = False
            connected_note = False
            connect_type = max_beats
            catch_up_str = []
            for cur_r_num in range(len(result)):
                r = result[cur_r_num]
                if args.verbose:
                    print(r)
                r = r.split()
                if len(r)>0:
                    if r[0][0] == 'M':
                        measure = int(r[0].split("=")[1])
                        beat = int(r[1].split("=")[1])
                        # print(str(lastMeasure)+", "+str(lastBeat))
                        # print(r)
                        # print("\n")
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
                                    if using_connected_notes:
                                        if cur_r_num+strings+2 < len(result):
                                            temp_measure = int(result[cur_r_num+strings+2].split()[0].split("=")[1])
                                            temp_beat = int(result[cur_r_num+strings+2].split()[1].split("=")[1])
                                            if lastMeasure == measure and len(r) > 3:
                                                if (measure + 1 == temp_measure and max_beats - beat <= max_beats/8) or (measure == temp_measure and temp_beat - beat <= max_beats/8):
                                                    if connected_note:
                                                        connect_type = beatDiff
                                                        noteHeadMain = "\\qb0"
                                                    else:
                                                        noteHeadMain = "\\ibu0l0\\qb0"
                                                        connected_note = True
                                                        connect_type = beatDiff
                                                else:
                                                    if connected_note:
                                                        connected_note = False
                                                        connect_type = max_beats
                                                        noteHeadMain = "\\tbu0\\qb0"
                                                    else:
                                                        noteHeadMain = "\\ca"
                                            else:
                                                if connected_note:
                                                    connected_note = False
                                                    connect_type = max_beats
                                                    noteHeadMain = "\\tbu0\\qb0"
                                                else:
                                                    noteHeadMain = "\\ca"
                                        else:
                                            if connected_note:
                                                connected_note = False
                                                connect_type = max_beats
                                                noteHeadMain = "\\tbu0\\qb0"
                                            else:
                                                noteHeadMain = "\\ca"
                                    else:
                                        noteHeadMain = "\\ca"
                                elif beatDiff >= max_beats/16:
                                    if using_connected_notes:
                                        if cur_r_num+strings+2 < len(result):
                                            temp_measure = int(result[cur_r_num+strings+2].split()[0].split("=")[1])
                                            temp_beat = int(result[cur_r_num+strings+2].split()[1].split("=")[1])
                                            if lastMeasure == measure and len(r) > 3:
                                                if (measure + 1 == temp_measure and max_beats - beat <= max_beats/8) or (measure == temp_measure and temp_beat - beat <= max_beats/8):
                                                    if connected_note:
                                                        if connect_type > max_beats/16:
                                                            if (measure + 1 == temp_measure and max_beats - beat != beatDiff) or (measure == temp_measure and temp_beat - beat != beatDiff):
                                                                noteHeadMain = "\\roff{\\tbbu0\\qb0"
                                                            else:
                                                                noteHeadMain = "\\nbbu0\\qb0"
                                                            connect_type = beatDiff
                                                        elif (measure + 1 == temp_measure and max_beats - beat >= max_beats/8) or (measure == temp_measure and temp_beat - beat >= max_beats/8):
                                                            noteHeadMain = "\\tbbu0\\qb0"
                                                            connect_type = beatDiff
                                                        # elif connect_type < max_beats/16:
                                                        #     noteHeadMain = "\\nbbu0\\qb0"
                                                        else:
                                                            connect_type = beatDiff
                                                            noteHeadMain = "\\qb0"
                                                    else:
                                                        noteHeadMain = "\\ibbu0l0\\qb0"
                                                        connected_note = True
                                                        connect_type = beatDiff
                                                else:
                                                    if connected_note:
                                                        connected_note = False
                                                        connect_type = max_beats
                                                        if connect_type == beatDiff:
                                                            noteHeadMain = "\\tbu0\\qb0"
                                                        else:
                                                            noteHeadMain = "\\roff{\\tbbu0\\tbu0\\qb0"
                                                    else:
                                                        noteHeadMain = "\\cca"
                                            else:
                                                if connected_note:
                                                    connected_note = False
                                                    connect_type = max_beats
                                                    if connect_type == beatDiff:
                                                        noteHeadMain = "\\tbu0\\qb0"
                                                    else:
                                                        noteHeadMain = "\\roff{\\tbbu0\\tbu0\\qb0"
                                                else:
                                                    noteHeadMain = "\\cca"
                                        else:
                                            if connected_note:
                                                if connect_type == beatDiff:
                                                    noteHeadMain = "\\tbu0\\qb0"
                                                else:
                                                    noteHeadMain = "\\roff{\\tbbu0\\tbu0\\qb0"
                                                connected_note = False
                                                connect_type = max_beats
                                            else:
                                                noteHeadMain = "\\cca"
                                    else:
                                        noteHeadMain = "\\cca"
                                elif beatDiff >= max_beats/32:
                                    if using_connected_notes:
                                        if cur_r_num+strings+2 < len(result):
                                            temp_measure = int(result[cur_r_num+strings+2].split()[0].split("=")[1])
                                            temp_beat = int(result[cur_r_num+strings+2].split()[1].split("=")[1])
                                            if lastMeasure == measure and len(r) > 3:
                                                if (measure + 1 == temp_measure and max_beats - beat <= max_beats/8) or (measure == temp_measure and temp_beat - beat <= max_beats/8):
                                                    if connected_note:
                                                        if connect_type > max_beats/32:
                                                            if (measure + 1 == temp_measure and max_beats - beat != beatDiff) or (measure == temp_measure and temp_beat - beat != beatDiff):
                                                                noteHeadMain = "\\roff{\\tbbbu0\\tbbu0\\qb0"
                                                            else:
                                                                noteHeadMain = "\\nbbbu0\\qb0"
                                                            connect_type = beatDiff
                                                        elif (measure + 1 == temp_measure and max_beats - beat >= max_beats/8) or (measure == temp_measure and temp_beat - beat >= max_beats/8):
                                                            noteHeadMain = "\\tbbu0\\qb0"
                                                            connect_type = beatDiff
                                                        elif (measure + 1 == temp_measure and max_beats - beat >= max_beats/16) or (measure == temp_measure and temp_beat - beat >= max_beats/16):
                                                            noteHeadMain = "\\tbbbu0\\qb0"
                                                            connect_type = beatDiff
                                                        else:
                                                            connect_type = beatDiff
                                                            noteHeadMain = "\\qb0"
                                                    else:
                                                        noteHeadMain = "\\ibbbu0l0\\qb0"
                                                        connected_note = True
                                                        connect_type = beatDiff
                                                else:
                                                    if connected_note:
                                                        connected_note = False
                                                        connect_type = max_beats
                                                        if connect_type == beatDiff:
                                                            noteHeadMain = "\\tbu0\\qb0"
                                                        else:
                                                            noteHeadMain = "\\roff{\\tbbbu0\\tbbu0\\tbu0\\qb0"
                                                    else:
                                                        noteHeadMain = "\\ccca"
                                            else:
                                                if connected_note:
                                                    connected_note = False
                                                    connect_type = max_beats
                                                    if connect_type == beatDiff:
                                                        noteHeadMain = "\\tbu0\\qb0"
                                                    else:
                                                        noteHeadMain = "\\roff{\\tbbbu0\\tbbu0\\tbu0\\qb0"
                                                else:
                                                    noteHeadMain = "\\ccca"
                                        else:
                                            if connected_note:
                                                if connect_type == beatDiff:
                                                    noteHeadMain = "\\tbu0\\qb0"
                                                else:
                                                    noteHeadMain = "\\roff{\\tbbbu0\\tbbu0\\tbu0\\qb0"
                                                connected_note = False
                                                connect_type = max_beats
                                            else:
                                                noteHeadMain = "\\ccca"
                                    else:
                                        noteHeadMain = "\\ccca"
                                elif beatDiff >= max_beats/64:
                                    if using_connected_notes:
                                        if cur_r_num+strings+2 < len(result):
                                            temp_measure = int(result[cur_r_num+strings+2].split()[0].split("=")[1])
                                            temp_beat = int(result[cur_r_num+strings+2].split()[1].split("=")[1])
                                            if lastMeasure == measure and len(r) > 3:
                                                if (measure + 1 == temp_measure and max_beats - beat <= max_beats/8) or (measure == temp_measure and temp_beat - beat <= max_beats/8):
                                                    if connected_note:
                                                        if connect_type > max_beats/64:
                                                            noteHeadMain = "\\nbbbbu0\\qb0"
                                                            connect_type = beatDiff
                                                        elif (measure + 1 == temp_measure and max_beats - beat >= max_beats/8) or (measure == temp_measure and temp_beat - beat >= max_beats/8):
                                                            noteHeadMain = "\\tbbu0\\qb0"
                                                            connect_type = beatDiff
                                                        elif (measure + 1 == temp_measure and max_beats - beat >= max_beats/16) or (measure == temp_measure and temp_beat - beat >= max_beats/16):
                                                            noteHeadMain = "\\tbbbu0\\qb0"
                                                            connect_type = beatDiff
                                                        elif (measure + 1 == temp_measure and max_beats - beat >= max_beats/32) or (measure == temp_measure and temp_beat - beat >= max_beats/32):
                                                            noteHeadMain = "\\tbbbbu0\\qb0"
                                                            connect_type = beatDiff
                                                        else:
                                                            connect_type = beatDiff
                                                            noteHeadMain = "\\qb0"
                                                    else:
                                                        noteHeadMain = "\\ibbbbu0l0\\qb0"
                                                        connected_note = True
                                                        connect_type = beatDiff
                                                else:
                                                    if connected_note:
                                                        connected_note = False
                                                        connect_type = max_beats
                                                        if connect_type == beatDiff:
                                                            noteHeadMain = "\\tbu0\\qb0"
                                                        else:
                                                            noteHeadMain = "\\roff{\\tbbbbu0\\tbbbu0\\tbbu0\\tbu0\\qb0"
                                                    else:
                                                        noteHeadMain = "\\cccca"
                                            else:
                                                if connected_note:
                                                    connected_note = False
                                                    connect_type = max_beats
                                                    if connect_type == beatDiff:
                                                        noteHeadMain = "\\tbu0\\qb0"
                                                    else:
                                                        noteHeadMain = "\\roff{\\tbbbbu0\\tbbbu0\\tbbu0\\tbu0\\qb0"
                                                else:
                                                    noteHeadMain = "\\cccca"
                                        else:
                                            if connected_note:
                                                if connect_type == beatDiff:
                                                    noteHeadMain = "\\tbu0\\qb0"
                                                else:
                                                    noteHeadMain = "\\roff{\\tbbbbu0\\tbbbu0\\tbbu0\\tbu0\\qb0"
                                                connected_note = False
                                                connect_type = max_beats
                                            else:
                                                noteHeadMain = "\\cccca"
                                    else:
                                        noteHeadMain = "\\cccca"
                                elif beatDiff >= max_beats/128:
                                    if using_connected_notes:
                                        if cur_r_num+strings+2 < len(result):
                                            temp_measure = int(result[cur_r_num+strings+2].split()[0].split("=")[1])
                                            temp_beat = int(result[cur_r_num+strings+2].split()[1].split("=")[1])
                                            if lastMeasure == measure and len(r) > 3:
                                                if (measure + 1 == temp_measure and max_beats - beat <= max_beats/8) or (measure == temp_measure and temp_beat - beat <= max_beats/8):
                                                    if connected_note:
                                                        if connect_type > max_beats/128:
                                                            noteHeadMain = "\\nbbbbbu0\\qb0"
                                                            connect_type = beatDiff
                                                        elif (measure + 1 == temp_measure and max_beats - beat >= max_beats/8) or (measure == temp_measure and temp_beat - beat >= max_beats/8):
                                                            noteHeadMain = "\\tbbu0\\qb0"
                                                            connect_type = beatDiff
                                                        elif (measure + 1 == temp_measure and max_beats - beat >= max_beats/16) or (measure == temp_measure and temp_beat - beat >= max_beats/16):
                                                            noteHeadMain = "\\tbbbu0\\qb0"
                                                            connect_type = beatDiff
                                                        elif (measure + 1 == temp_measure and max_beats - beat >= max_beats/32) or (measure == temp_measure and temp_beat - beat >= max_beats/32):
                                                            noteHeadMain = "\\tbbbbu0\\qb0"
                                                            connect_type = beatDiff
                                                        elif (measure + 1 == temp_measure and max_beats - beat >= max_beats/64) or (measure == temp_measure and temp_beat - beat >= max_beats/64):
                                                            noteHeadMain = "\\tbbbbbu0\\qb0"
                                                            connect_type = beatDiff
                                                        else:

                                                            connect_type = beatDiff
                                                            noteHeadMain = "\\qb0"
                                                    else:
                                                        noteHeadMain = "\\ibbbbbu0l0\\qb0"
                                                        connected_note = True
                                                        connect_type = beatDiff
                                                else:
                                                    if connected_note:
                                                        connected_note = False
                                                        connect_type = max_beats
                                                        if connect_type == beatDiff:
                                                            noteHeadMain = "\\tbu0\\qb0"
                                                        else:
                                                            noteHeadMain = "\\roff{\\tbbbbbu0\\tbbbbu0\\tbbbu0\\tbbu0\\tbu0\\qb0"
                                                    else:
                                                        noteHeadMain = "\\ccccca"
                                            else:
                                                if connected_note:
                                                    connected_note = False
                                                    connect_type = max_beats
                                                    if connect_type == beatDiff:
                                                        noteHeadMain = "\\tbu0\\qb0"
                                                    else:
                                                        noteHeadMain = "\\roff{\\tbbbbbu0\\tbbbbu0\\tbbbu0\\tbbu0\\tbu0\\qb0"
                                                else:
                                                    noteHeadMain = "\\ccccca"
                                        else:
                                            if connected_note:
                                                if connect_type == beatDiff:
                                                    noteHeadMain = "\\tbu0\\qb0"
                                                else:
                                                    noteHeadMain = "\\roff{\\tbbbbbu0\\tbbbbu0\\tbbbu0\\tbbu0\\tbu0\\qb0"
                                                connected_note = False
                                                connect_type = max_beats
                                            else:
                                                noteHeadMain = "\\ccccca"
                                    else:
                                        noteHeadMain = "\\ccccca"

                                o.write(" & ");
                                for i in range(len(catch_up_str)):
                                    if i != len(catch_up_str)-1:
                                        o.write(noteHead + catch_up_str[i] + ("}" if ("\\roff" in noteHeadMain) else ""));
                                    else:
                                        o.write(noteHeadMain + catch_up_str[i] + ("}" if ("\\roff" in noteHeadMain) else "") + "\\en\n");
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
                                    # oct_str = (octave-last_octave)*'\''
                                    oct_str = octave*'\''
                                    last_octave = octave
                                if (oct_str+note) in note_dict:
                                    note = note_dict[oct_str+note]
                                    oct_str = ""
                                if i == len(values)-1:
                                    catch_up_str.append("{" + oct_str+accidental+note + "}")
                                else:
                                    catch_up_str.append("{" + oct_str+accidental+note + "}")

                        if measure != lastMeasure and lastMeasure != -1:
                            o.write(" \\bar\n")
                        lastMeasure = measure
                        lastBeat = beat
                        string = strings
                    elif len(r)>1:
                        if not inNotes:
                            o.write("   \\Notes\\hsk")
                        inNotes = True
                        if "(" in r[1]:
                            r[1] = r[1].split("(")[1].split(")")[0]
                        o.write("\\str{" + str(string) + "}{" + r[1] + "}")
                        string -= 1
            if inNotes:
                beat = max_beats-lastBeat
                measureDiff = measure-lastMeasure
                if measureDiff >= 1:
                    beatDiff = measureDiff*max_beats - lastBeat + beat
                else:
                    beatDiff = beat
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
                    if connected_note:
                        noteHeadMain = "\\tbu0\\qb0"
                    else:
                        noteHeadMain = "\\ca"
                elif beatDiff >= max_beats/16:
                    if connected_note:
                        noteHeadMain = "\\tbu0\\qb0"
                    else:
                        noteHeadMain = "\\cca"
                elif beatDiff >= max_beats/32:
                    if connected_note:
                        noteHeadMain = "\\tbu0\\qb0"
                    else:
                        noteHeadMain = "\\ccca"
                elif beatDiff >= max_beats/64:
                    if connected_note:
                        noteHeadMain = "\\tbu0\\qb0"
                    else:
                        noteHeadMain = "\\cccca"
                elif beatDiff >= max_beats/128:
                    if connected_note:
                        noteHeadMain = "\\tbu0\\qb0"
                    else:
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
