# Moo4S - COW Language Virtual Machine in Scala

Current stable release v1.0.13

## Overview

**Moo4S** is a complete, spec-compliant implementation of the [COW programming language](https://esolangs.org/wiki/COW) written in Scala. COW is an esoteric programming language derived from Brainfuck that uses bovine-themed commands (`moo`, `MOO`, `Moo`, etc.) instead of single-character operators.

This project provides:
- A **Virtual Machine** that executes COW programs
- A **Domain-Specific Language (DSL)** for writing COW programs in idiomatic Scala
- A **Parser** for executing COW source code from strings
- **Comprehensive test suite** covering all instructions and edge cases

## Features

### All 12 COW Instructions Implemented

| Command | Code | Name | Description |
|---------|------|------|-------------|
| `moo` | 0 | Loop Back | Search backward for matching MOO and jump to it |
| `mOo` | 1 | Move Left | Move data pointer left |
| `moO` | 2 | Move Right | Move data pointer right |
| `mOO` | 3 | Execute | Execute cell value as instruction code |
| `Moo` | 4 | I/O | Read character (if cell=0) or write character (if cell≠0) |
| `MOo` | 5 | Decrement | Decrement current cell |
| `MoO` | 6 | Increment | Increment current cell |
| `MOO` | 7 | Conditional Loop | If cell=0, skip to matching moo |
| `OOO` | 8 | Set Zero | Set current cell to zero |
| `MMM` | 9 | Register | Copy/paste register (toggle mode) |
| `OOM` | 10 | Print Int | Print cell value as integer |
| `oom` | 11 | Read Int | Read integer from input |

### Key Features

**Memory Management** - Configurable memory size (default 30,000 cells)

**Scala DSL** - Write COW programs using natural Scala syntax

**String Parser** - Execute COW source code from text files or strings

## Installation

### Prerequisites
- Scala 3
- SBT (Scala Build Tool)

## Usage

### Importing Moo4S

Add the dependency to your build.sbt file
```scala
libraryDependencies ++= Seq(
  "io.github.stephenrinn" %% "moo4s" % "1.0.13",
)
```

### Using the Scala DSL

The easiest way to write COW programs is using the Scala DSL:

```scala
import cow.CowDSL._

val output = cow {
  MoO
  MoO
  MoO
  OOM
}
// output: "3"
```

### Using the Interpreter

Execute COW source code from a string:

```scala
import cow.CowInterpreter

val source = "MoO MoO MoO OOM"
val output = CowInterpreter.run(source)
// output: "3"
```

### Using the Virtual Machine Directly

For advanced use cases, instantiate the VM directly:

```scala
import cow.{VirtualMachine, Command}

val vm = new VirtualMachine(memorySize = 30000)
val commands = List(
  Command.MoOSix,
  Command.MoOSix,
  Command.MoOSix,
  Command.OOMTen
)
val output = vm.execute(commands)
// output: "3"
```

### Custom Memory Size

You can specify a custom memory size when using the DSL:

```scala
val output = cow(5000) {
  MoO
  MoO
  OOM
}
// output: "2"
```

### Getting Commands Without Execution

Use `cowCommands` to get the parsed command list without executing:

```scala
val commands = cowCommands {
  MoO
  MoO
  MOo
}
// commands: List[Command.CowCommand] with 3 elements
```

## Testing

Run the comprehensive test suite:

```bash
sbt test
```

## References

- [COW Language Specification](https://esolangs.org/wiki/COW)
- [Brainfuck Language](https://esolangs.org/wiki/Brainfuck)
- [Esolangs Wiki](https://esolangs.org/)
