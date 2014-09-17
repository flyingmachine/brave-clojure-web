---
title: All the Java You Need
link_title: All the Java You Need
kind: documentation
draft: true
---

# All the Java You Need

There comes a day in every Clojurist's life when she must venture
forth from the sanctuary of pure functions and immutable data
structures into the wild and barbaric Land of Java. This treacherous
journey is necessary because Clojure is hosted on the Java Virtual
Machine (JVM), granting it three fundamental characteristics. First,
you run Clojure applications the same way you run Java
applications. Second, you need to use Java objects for core
functionality like reading files and working with dates. Third, Java
has a vast and wondrous ecosystem of incredible libraries, and Clojure
makes it painless for you to use them. In this way, Clojure is a bit
like a utopian community plunked down in the middle of a non-utopian
country. It's preferable to interact with other utopians, but every
once in a while you need to talk to the locals in order to get things
done.

If this chapter were a guide book for the Land of Java, it'd be
somewhere between a phrasebook ("Where is the bathroom?") and a
full-blown course on the local language and customs. It will give you
an overview of what the JVM is, how it runs programs, and how to
compile programs for it. It will also give you a tour of
frequently-used Java classes and methods and explain how to interact
with them from Clojure. More than that, it will show you how to think
about and understand Java so that you can incorporate any Java library
into your Clojure program.

# The JVM

Developers use the term "JVM" to refer to a few different
things. You'll hear them say, "Clojure runs on *the* JVM", and you'll
also hear "Clojure programs run in *a* JVM". In the first case, "JVM"
refers to an abstraction - the general model of the Java Virtual
Machine. In the second, it refers to a process, an instance of a
running program.  Right now, we're only concerned with the JVM model;
I'll point out when we're talking about running JVM processes.

To understand the Java Virtual Machine, let's first take a step back
and review how plain-ol' machines (also known as computers) work. Deep
in the cockles of a computer's heart is its CPU, and the CPU's job is
to execute operations like "add" and "unsigned multiply". These
operations are represented in assembly language by mnemonics like
"ADD" and "MUL". What operations are availabe depends on the CPU
architecture (x86, ARMv7 and what have you) as part of the
architecture's *instruction set*.

Because it's no fun to program in assembly language, we humans have
invented higher-level languages like C and C++, which get compiled
into the instructions that a CPU will understand. Here's a diagram of
the process:

1. Compiler reads source code.
2. Compiler outputs a file containing CPU instructions
3. The computer executes those instructions

The most important thing here is that, ultimately, you have to
translate programs into the instructions that a CPU will understand,
and the CPU doesn't care what programming language was used to produce
the instructions.

The JVM is analagous to a CPU in that it also executes low-level
instructions, called Java bytecode. As a *virtual* machine, though,
it's implemented as software rather than hardware. A running JVM
executes bytecode by translating it on the fly into the machine code
that its host will understand, a process called just-in-time
compilation.

For a program to run on the JVM, then, it has to get compiled to Java
bytecode. Usually, when you compile programs you store the result as a
JAR (Java archive) file. Just as a CPU doesn't care how machine
instructions are generated, the JVM doesn't care how bytecode gets
created. It doesn't care if you used Scala, JRuby, Clojure, or even
Java to create Java bytecode. Here's a diagram:

1. Compile to bytecode
2. JVM reads bytecode
3. JVM sends machine instructions

So when someone says that Clojure runs on the JVM, one of the things
they mean is that Clojure programs get compiled to Java bytecode and
JVM processes execute them. This matters because you treat Clojure
programs the same as Java programs from an operations perspective. You
compile them to JAR files and run them using the `java` command. If a
client needs a program that runs on the JVM, you could secretly write
it in Clojure instead of Java and they would be none the wiser. Seen
from the outside, you can't tell the difference between a Java and a
Clojure program any more than you can tell the difference between a C
and a C++ program. Clojure allows you to be productive *and* sneaky.

# Compiling and Running a Java Program

I think it's time to see how all of this works with real code. In this
section, you'll build a simple pirate phrasebook using Java. This will
help you feel much more comfortable with the JVM, it will prepare you
for the upcoming section on Java interop, and it will prove invaluable
should a scallywag ever attempt to scuttle your booty on the high
seas. To tie it all together, you'll take a peek at some of Clojure's
Java code.

## Hello World

Go ahead and create new directory called "phrasebook". Within that
directory, create a file named `PiratePhrases.java`, and write the
following in it:

```java
public class PiratePhrases
{
    public static void main(String[] args)
    {
        System.out.println("Shiver me timbers!!!");
    }
}
```

This defines a very simple program which will print the phrase "Shiver
me timbers!!!" (which is how pirates say "Hello, world!") to your
terminal when you run it. It consists of a class, `PiratePhrases`, and
a static method belonging to that class, `main`. Static methods are
essentially class methods.

### Object-Oriented Programming in the World's Tiniest Nutshell

If you're unfamiliar with object-oriented programming, here's the
two-minute lowdown. The central players in OOP are *classes*,
*objects*, and *methods*.

I think of objects as really, really, ridiculously dumb
androids. They're the kind of android that would never inspire moral
or philosophical debate about the ethics of relegating sentient
creatures to perpetual servitude. These androids do two things: they
respond to commands and they maintian data for me. (In my imagination
they do this by writing stuff down on little Hello Kitty clipboards.)
Both the set of commands the android understands and the set of data
it maintains is determined by the factory that makes the android. In
this metaphor, commands correspond to methods and the factories
correspond classes. For example, you might have a `ScaryClown` factory
producing androids that respond to the command `makeBalloonArt`. The
android keeps track of the number of balloons it has by writing down
the number on its clipboard, then erasing that number and writing a
new one whenever the number of balloons it carries changes. It can
report that number with the command `balloonCount` and receive any
number of balloons with `receiveBalloons`. Here's how you might
interact a Java object representing Belly Rubs the Clown:

```java
ScaryClown bellyRubsTheClown = new ScaryClown();
bellyRubsTheClown.balloonCount();
// => 0

bellyRubsTheClown.receiveBalloons(2);
bellyRubsTheClown.balloonCount();
// => 2

bellyRubsTheClown.makeBalloonArt();
// => "Belly Rubs makes a balloon shaped like a clown, because Belly Rubs
// => is trying to scare you and nothing is scarier than clowns."
```

One final aspect of OOP, or at least of Java, is that you can also
send commands to the factory themselves. In real-life terms, you would
say that classes also have methods.
 
### Back to the Pirate Example

Back to our example! In your terminal, compile the PiratePhrases
source code with the command `javac PiratePhrases.java`. If you typed
everything correctly *and* you're pure of heart, you should see a file
named `PiratePhrases.class`:

```sh
$ ls
PiratePhrases.class PiratePhrases.java
```

You've just compiled your first Java program, son! Now run it with
`java PiratePhrases`. You should see:

```
Shiver me timbers!!!
```

What's happening here is you used the Java compiler, `javac`, to
create a Java class file, `PiratePhrases.class`. This file is packed
with oodles (well, for program this size, maybe only one oodle) of
Java bytecode.

When you ran `java PiratePhrases`, the JVM first looked on your
*classpath* for a class named `PiratePhrases`. You can think of the
classpath as the list of filesystem paths that the JVM will search in
order to find a file which defines a class. By default, the classpath
includes the directory you're in when you run `java`. Try running
`java -classpath /tmp PiratePhrases` and you will get an error, even
though `PiratePhrases.class` is right there in your current directory.

In Java, you're only allowed to have one public class per file and the
filename and class name must be the same. This is how `java` knows to
try looking in `PiratePhrases.class` for the ShiverMeTimbers class's
bytecode. After `java` found the bytecode for the `PiratePhrases`
class, it executed that class's `main` method. Java's kind of like C
that way, in that whenever you say "run something, and use this class
as your entry point", it always will run that class's `main`
method. Which means that that method has to be `public`, as you can
see above.

In the next section you'll learn about handling program code that's
spread over more than one file. If you don't remove your socks now,
they're liable to get knocked off!

## Packages and Imports

In this section, you'll learn about how Java handles programs which
are spread over more than one file. You'll also learn how to use Java
libraries. Once again, we'll look at both compiling and running a
program. This section has direct implications for Clojure, where
you'll the same ideas and terminology to interact with Java libraries.

First, a couple definitions:

* **package:** Similar to Clojure's namespaces, packages provide code
  organization. The directory that a Java file lives in must mirror
  the package it belongs to. If a file has the line `package
  com.shapemaster` in it, then it must be located at `com/shapemaster`
  somewhere on your classpath.
* **import:** Java allows you to import classes, which basically means
  that you can refer to them without using their namespace prefix. So,
  if you have a class in `com.shapemaster` named `Square`, you could
  write `import com.shapemaster.Square;` or `import com.shapemaster.*;`
  at the top of a `.java` file so that you can use `Square` in your
  code instead of `com.shapemaster.Square`. You'll see this
  illustrated below.

Now it's time to try out `package` and `import`. To start, you'll
create three files. First, create PirateConversation.java:

```java
import pirate_phrases.*;

public class PirateConversation
{
    public static void main(String[] args)
    {
        Greetings greetings = new Greetings();
        greetings.hello();

        Farewells farewells = new Farewells();
        farewells.goodbye();
    }
}
```

The first line, `import pirate_phrases.*;`, imports all classes in the
`pirate_phrases` package, which will contain the `Greetings` and
`Farewells` classes. Let's create those now. First, create the
directory `pirate_phrases`. This is needed because Java package names
correspond to filesystem directories. Then, create `Greetings.java`
within `pirate_phrases` and write the following:

```java
package pirate_phrases;

public class Greetings
{
    public static void hello()
    {
        System.out.println("Shiver me timbers!!!");
    }
}
```

Now create `Farewells.java` within the `pirate_phrases` directory:

```java
package pirate_phrases;

public class Farewells
{
    public static void goodbye()
    {
        System.out.println("A fair turn of the tide ter ye thar, ye bootlick slimebuckle!");
    }
}
```

If you navigate back to the parent directory of `pirate_phrases` and
run `javac PiratePhrases.java` followed by `java PiratePhrases`, you
should see this:

```java
Shiver me timbers!!!
A fair turn of the tide ter ye thar, ye bootlick slimebuckle!
```

And thar she blows, dear reader. Thar she blows indeed.

One thing to note is that, when you're compiling a Java program, Java
searches your classpath for packages. You can see this if you do the
following:

```java
cd pirate_phrases
javac ../PirateConversation.java
```

Boom! The Java compiler just told you to hang your head in shame, and
maybe weep a little:

```
../PirateConversation.java:1: error: package pirate_phrases does not exist
import pirate_phrases.*;
^
```

It thinks that the `pirate_phrases` package doesn't exist. But that's
stupid, right? I mean, you're in the `pirate_phrases` directory and
everything!

What's happening here is that the default classpath only includes the
directory, which in this case is `pirate_phrases`. It's like `javac`
is trying to find the directory
`phrasebook/pirate_phrases/pirate_phrases`. Without changing
directories, try running `javac ../PirateConversation.java -classpath
../`. Shiver me timbers, it works!

If you feel like breaking things some more, you'll find that moving
either of the .class files in the `pirate_phrases` directory to
another directory will also result in a classpath error. 

To sum things up: packages organize code and require a matching
directory structure. Importing classes allows you to "de-namespace"
them. `javac` and `java` find packages using the classpath.

## JAR Files

JAR files allow you to bundle all your .class files into one single
file. Navigate to your `phrasebook` directory and run the following:

```sh
jar cvfe conversation.jar PirateConversation PirateConversation.class
pirate_phrases/*.class
java -jar conversation.jar
```

This displays the pirate conversation correctly. You bundled all the
class files into `conversation.jar`. You also indicated that the
`PirateConversation` class is the "entry point" with the `e` flag. The
"entry point" is the class that contains the `main` method which
should be executed when the JAR as a whole is run, and `jar` stores
this information in the file `META-INF/MANIFEST.MF` within the JAR
file. If you were to read that file, it would contain this line:

```
Main-Class: PirateConversation
```

By the way, when you execute JAR files, you don't have to worry what
directory you're in in relation to the file. You could change to the
`pirate_phrases` directory and run `java -jar ../conversation.jar` and
it would work fine. The reason is that the JAR file maintains the
directory structure. You can see its contents with `jar tf
conversation.jar`, which outputs:

```
META-INF/
META-INF/MANIFEST.MF
PirateConversation.class
pirate_phrases/Farewells.class
pirate_phrases/Greetings.class
```

One more fun fact about JARs: they're really just zip files with a
".jar" extension. You can treat them the same as any other zip file.

## clojure.jar

Let's pull all of this together with some Clojure! Download
[the 1.6.0 stable release](http://central.maven.org/maven2/org/clojure/clojure/1.6.0/clojure-1.6.0.jar)
and run it:

```sh
java -jar clojure-1.6.0.jar
```

You should see that most soothing of sights, the Clojure REPL. How did
it actually start up? Let's have a look at `META-INF/MANIFEST.MF` in
the jar file:

```
Manifest-Version: 1.0
Archiver-Version: Plexus Archiver
Created-By: Apache Maven
Built-By: hudson
Build-Jdk: 1.6.0_20
Main-Class: clojure.main
```

It looks like `clojure.main` is specified as the entry point. Where
does this class come from? Well, have a look at
[clojure/main.java on github](https://github.com/clojure/clojure/blob/master/src/jvm/clojure/main.java):

```java
/**
 *   Copyright (c) Rich Hickey. All rights reserved.
 *   The use and distribution terms for this software are covered by the
 *   Eclipse Public License 1.0 (http://opensource.org/licenses/eclipse-1.0.php)
 *   which can be found in the file epl-v10.html at the root of this distribution.
 *   By using this software in any fashion, you are agreeing to be bound by
 * 	 the terms of this license.
 *   You must not remove this notice, or any other, from this software.
 **/

package clojure;

import clojure.lang.Symbol;
import clojure.lang.Var;
import clojure.lang.RT;

public class main{

final static private Symbol CLOJURE_MAIN = Symbol.intern("clojure.main");
final static private Var REQUIRE = RT.var("clojure.core", "require");
final static private Var LEGACY_REPL = RT.var("clojure.main", "legacy-repl");
final static private Var LEGACY_SCRIPT = RT.var("clojure.main", "legacy-script");
final static private Var MAIN = RT.var("clojure.main", "main");

public static void legacy_repl(String[] args) {
    REQUIRE.invoke(CLOJURE_MAIN);
    LEGACY_REPL.invoke(RT.seq(args));
}

public static void legacy_script(String[] args) {
    REQUIRE.invoke(CLOJURE_MAIN);
    LEGACY_SCRIPT.invoke(RT.seq(args));
}

public static void main(String[] args) {
    REQUIRE.invoke(CLOJURE_MAIN);
    MAIN.applyTo(RT.seq(args));
}
}
```

As you can see, the file defines a class named `main`. It belongs to
the package `clojure` and defines a `public static main` method, and
the JVM is completely happy to use it as an entry point.

Seen this way, Clojure is a JVM program just like any other. This
isn't meant to be an in-depth Java tutorial, but I hope that it helps
clear up what's meant when programmers talk about Clojure "running on
the JVM" or being a "hosted" language. In the next section, you'll be
treated to more clearings up as you learn how to use Java libraries
within your Clojure project.

TODO more detail about the JVM being hosted: GC, threads
TODO segue into next chapter, implementation in terms of JVM
abstractions
TODO mention that leiningen handles building jar files and classpaths
for you
TODO mention that you might run into classpath problems and that the
cp discussion is meant to give you more insight into what's going on

# Java Interop

One of Rich Hickey's design goals for Clojure was to create a
*practical* language, and for that reason Clojure was designed to make
it straightforward for you to interact with Java classes and
objects. That way, you can make use both of Java's extensive native
functionality and its enormous ecosystem. The ability to use Java
classes, objects, and methods is called *Java Interop*. In this
section, you'll learn how to use Clojure' interop syntax, how to
import Java packages, and how to use the most-frequently used Java
classes.

## Interop Syntax

Interacting with Java objects and classes is straighforward. Let's
start with object interop syntax.

You can call methods on an object using `(.methodName object)`. For
example, since all Clojure strings are implemented as Java strings,
you can call Java methods on them:

```clojure
(.toUpperCase "By Bluebeard's bananas!")
; => "BY BLUEBEARD'S BANANAS!"

(.indexOf "Let's synergize our bleeding edges" "y")
; => 7
```

These are equivalent to:

```java
"By Bluebeard's bananas!".toUpperCase()
"Let's synergize our bleeding edges".indexOf("y")
```

Notice that Clojure's syntax allows you to pass arguments to Java
methods. In the example above, you passed the argument `"y"` to the
`indexOf` method.

You can also call static methods on classes and access classes' static
fields. Observe!

```clojure
(Math/abs -3)
; => 3

Math/PI
; => 3.141592653589793
```

In the first example, you called the `abs` static method on the
`java.lang.Math` class, and in the second you accessed that class's
`PI` static field.

# Notes

* You use local building materials (protocols, interfaces, file and
  date objs, compiling, exec jar file)
* Two perspectives:
    * the java objects perspective
        * Quick Java intro
        * importing
        * instantiating
        * calling methods 
            * dot op
            * doto
        * class methods
        * navigating documentation
        * commonly used classes
            * File
            * Date
            * Readers
            * Writers
            * System: getenv, exit, etc
        * nice clojure interfaces for java libraries
    * the ops perspective
        * what it means to execute a java program (bytecode, jvm)
        * JVM as a platform (lots of library, analogous to GNU on
          linux)
        * compiling to bytecode
        * classpath
        * the ecosystem around java programs

* compiling a clojure program yourself
