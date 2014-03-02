/**
 * This package contains a snapshot of the CUP-generated CUP parser.
 *
 * Originally, CUP's source code included the "java_cup" package, which
 * implements the logic of the parser generator, but also included the
 * specification of the CUP input files (written in CUP) and a previous
 * version of CUP in binary form (jar) which would be used to generate
 * the CUP's parser code for grammar files.
 *
 * Basically, CUP (version 0.11b) depends on a previous version (0.11a, I
 * assume) to generate part of its source code. This was fine until I
 * tried to move everything from "java_cup" into "ldf.java_cup" (to avoid
 * future incompatibility between my modified version of the CUP runtime
 * and the original version -- should someone use both of them in their
 * software).
 *
 * That previous version, when it was compiled, belonged to the "java_cup"
 * package. Naturally, it only knew how to generate parsers that would
 * rely on the (hardcoded) "java_cup.runtime" package.
 *
 * While I could rely on my IDE (and its refactoring capabilities) to
 * move all the source code into a new package (and I could specify a new
 * package for the generated parser), I couldn't instruct the (already
 * compiled) parser generator that the runtime package "java_cup.runtime"
 * was moved into "ldf.java_cup.runtime".
 *
 * As a result, I had to make a snapshot of the generated code and make it
 * part of the source code, for now (then rely on my IDE to move everything
 * to the right package). This is why this package was created -- to hold
 * a copy of the generated code.
 *
 * The current version of the program no longer depends on the previous
 * one's binaries, but the downside of this approach is that it is
 * non-trivial to make changes to the syntax of the CUP grammar files
 * (since we are no longer generating the parser from the CUP
 * specification file).
 *
 * The reason I started modifying CUP's source code in the first place was
 * to change the way the parsers were generated,  not to change CUP's
 * specification language.
 *
 * Anyway, after compiling the new version of the parser generator, I can
 * replace that old binary with it and generate parsers again as part of
 * the compilation process (only this time, they will be using the new
 * "ldf.java_cup.runtime" package).
 *
 * @author Cristian Harja
 */
package ldf.java_cup.gen;
