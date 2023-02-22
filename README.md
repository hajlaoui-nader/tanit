# Scala with Cats Code

POC for pure functional scala with cats. The applicaiton runs `fs2` streams in parallel,
one for producing events in kafka and the other for reading them and storing them in `opensearch` database. Hexagonal architecture is used to separate the business logic from the infrastructure.



## Getting Started

You will need to have `git`, `Java 11`, and `sbt` installed.

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

## Lint
run `scalafix` linter before pushing to GitHub

```bash
sbt runLinter
```
## Run
run the docker-compose before launch `Main.scala`

## Tests

To run Unit Tests:

```
sbt test
```

To run Integration Tests: 

```
docker-compose up
sbt it:test
docker-compose down
````

## Build Docker image

```
sbt docker:publishLocal
```

To run the application using our Docker image, run the following command:

```
cd /app
docker-compose up
```

### TODO
- set avro serdes
- add tests (weaver)
- IT test
- fix kafka producer/consumer new connections
