# Scala with Cats Code

Sandbox project for pure functional scala with cats.

## Getting Started

You will need to have Git, Java 8, and [SBT][sbt] installed.

Start SBT using the `sbt` command to enter SBT's *interactive mode*
(`>` prompt):

```bash
$ sbt
[info] Loading global plugins from <DIRECTORY>
[info] Loading project definition from <DIRECTORY>
[info] Set current project to <PROJECT_NAME>

>
```

From the SBT prompt you can run the code in `Main.scala`:

```bash
> run
[info] Updating {file:<DIRECTORY>}cats-sandbox...
[info] Resolving jline#jline;2.14.4 ...
[info] Done updating.
[info] Compiling 1 Scala source to <DIRECTORY>...
[info] Running sandbox.Main
Hello Cats!
[success]
```

You can also start a *Scala console* (`scala>` prompt)
to play with small snippets of code:

```bash
> console
[info] Starting scala interpreter...
[info]
Welcome to Scala 2.12.3 (Java HotSpot(TM) 64-Bit Server VM, Java 1.8.0_112).
Type in expressions for evaluation. Or try :help.

scala> import cats._, cats.implicits._, cats.data._
import cats._
import cats.implicits._
import cats.data._

scala> "Hello " |+| "Cats!"
res0: String = Hello Cats!

scala>
```

Press `Ctrl+D` to quit the Scala console
and return to SBT interactive mode.

Press `Ctrl+D` again to quit SBT interactive mode
and return to your shell.


## Run
run the docker-compose before launch `Main.scala`