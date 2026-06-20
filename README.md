# Build Your Own Shell (Java)

This repository contains my solution for the CodeCrafters **Build Your Own Shell** challenge, implemented in Java.

The project is a POSIX-style shell that supports command parsing, built-in commands, execution of external programs, job control, redirection, and pipelines.

## Features Implemented

### Base Shell Stages
- Interactive REPL loop
- Command execution
- External executable discovery using `PATH`
- Built-in command support
- Error handling for unknown commands

### Built-in Commands
- `echo`
- `pwd`
- `cd`
- `type`
- `exit`
- `jobs`

### Navigation
- Relative path navigation
- Absolute path navigation
- Home directory (`~`) support
- Working directory management

### Quoting & Parsing
- Single quotes (`'`)
- Double quotes (`"`)
- Escape sequence handling (`\`)
- Proper tokenization of command arguments
- Mixed quoted and unquoted argument parsing

### Redirection

#### Standard Output
- `>`
- `1>`
- `>>`
- `1>>`

#### Standard Error
- `2>`
- `2>>`

#### Features
- File creation
- File truncation
- File append mode
- Separate stdout/stderr handling

### Background Jobs
- Command execution using `&`
- Job tracking
- Job number allocation
- Job number recycling
- `jobs` builtin support
- Background process completion notifications
- Finished job cleanup

### Pipelines

#### External Pipelines
- Dual-command pipelines
- Multi-command pipelines
- Streaming between processes using `ProcessBuilder.startPipeline()`

Examples:

```bash
cat file.txt | wc
cat file.txt | grep hello | wc
tail -f log.txt | head -n 5

## Pipelines with Built-ins

Support for built-in commands participating in pipelines.

### Examples

```bash
echo hello | wc
ls | type exit
pwd | wc
```

## Technical Highlights

### Process Management

- Java ProcessBuilder
- Background process execution
- Process lifecycle tracking
- Process output capture

### File System Operations

- Java NIO (`Path`, `Paths`, `Files`)
- Directory navigation
- File creation and modification
- Stream redirection

### Shell Features

- Command parsing
- Quote handling
- Escape character processing
- Pipeline execution
- Job control

## Technologies Used

- Java
- ProcessBuilder API
- Java NIO
- Virtual Threads
- POSIX shell concepts

## Learning Outcomes

Through this project I gained practical experience with:

- Shell architecture and design
- Command parsing techniques
- Process creation and management
- Inter-process communication (IPC)
- Pipeline implementation
- Job control systems
- File descriptor redirection
- POSIX shell behavior
- Java ProcessBuilder internals

## Progress

### Completed Stages

- ✅ Base Shell
- ✅ Navigation
- ✅ Quoting & Parsing
- ✅ Redirection
- ✅ Background Jobs (Full Extension)
- ✅ Pipelines

### Current Status

Successfully completed all Shell stages up to **Pipelines (Multi-command Pipelines)** in the CodeCrafters Shell challenge.
