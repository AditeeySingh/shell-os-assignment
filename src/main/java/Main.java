import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Main {

    private static Path currentDirectory = Paths.get(System.getProperty("user.dir"));

    public static void main(String[] args) throws Exception {
        Scanner scanner = new Scanner(System.in);

        while (true) {
            System.out.print("$ ");

            String input = scanner.nextLine();

            if (input.equals("exit") || input.equals("exit 0")) {
                System.exit(0);
            }

            if (input.equals("pwd")) {
                System.out.println(currentDirectory);
                continue;
            }

            if (input.startsWith("cd ")) {
                String directory = input.substring(3);

                if (directory.equals("~")) {
                    currentDirectory = Paths.get(System.getenv("HOME"));
                    continue;
                }

                Path newPath;

                if (Paths.get(directory).isAbsolute()) {
                    newPath = Paths.get(directory);
                } else {
                    newPath = currentDirectory.resolve(directory);
                }

                if (newPath.toFile().isDirectory()) {
                    currentDirectory = newPath.normalize();
                } else {
                    System.out.println("cd: " + directory + ": No such file or directory");
                }

                continue;
            }

            if (input.startsWith("echo ")) {
                String[] parts = parseCommand(input);

                String stdoutFile = null;
                String stderrFile = null;

                for (int i = 0; i < parts.length; i++) {
                    if (parts[i].equals(">") || parts[i].equals("1>")) {
                        stdoutFile = parts[i + 1];
                        break;
                    }
                }

                StringBuilder output = new StringBuilder();

                int limit = parts.length;

                for (int i = 0; i < parts.length; i++) {
                    if (parts[i].equals(">") || parts[i].equals("1>")) {
                        limit = i;
                        break;
                    }
                }

                for (int i = 1; i < limit; i++) {
                    if (i > 1) {
                        output.append(" ");
                    }
                    output.append(parts[i]);
                }

                if (redirectFile != null) {
                    Files.writeString(
                            currentDirectory.resolve(redirectFile),
                            output + System.lineSeparator(),
                            StandardOpenOption.CREATE,
                            StandardOpenOption.TRUNCATE_EXISTING
                    );
                } else {
                    System.out.println(output);
                }

                continue;
            }

            if (input.startsWith("type ")) {
                String command = input.substring(5);

                if (command.equals("echo")) {
                    System.out.println("echo is a shell builtin");
                } else if (command.equals("exit")) {
                    System.out.println("exit is a shell builtin");
                } else if (command.equals("type")) {
                    System.out.println("type is a shell builtin");
                } else if (command.equals("pwd")) {
                    System.out.println("pwd is a shell builtin");
                } else if (command.equals("cd")) {
                    System.out.println("cd is a shell builtin");
                } else {
                    String path = findExecutable(command);

                    if (path != null) {
                        System.out.println(command + " is " + path);
                    } else {
                        System.out.println(command + ": not found");
                    }
                }

                continue;
            }

            String[] parts = parseCommand(input);

            if (parts.length == 0) {
                continue;
            }

            String stdoutFile = null;
String stderrFile = null;

            int redirectIndex = -1;

for (int i = 0; i < parts.length; i++) {
    if (parts[i].equals(">") || parts[i].equals("1>")) {
        stdoutFile = parts[i + 1];
        redirectIndex = i;
        break;
    }

    if (parts[i].equals("2>")) {
        stderrFile = parts[i + 1];
        redirectIndex = i;
        break;
    }
}

if (redirectIndex != -1) {
    String[] temp = new String[redirectIndex];
    System.arraycopy(parts, 0, temp, 0, redirectIndex);
    parts = temp;
}

            String executable = findExecutable(parts[0]);

            if (executable != null) {
                try {
                    ProcessBuilder processBuilder = new ProcessBuilder(parts);
                    processBuilder.directory(currentDirectory.toFile());

                    Process process = processBuilder.start();

                    String stdout = new String(process.getInputStream().readAllBytes());
                    String stderr = new String(process.getErrorStream().readAllBytes());

                        process.waitFor();

                        if (stdoutFile != null) {
    Files.writeString(
            currentDirectory.resolve(stdoutFile),
            stdout,
            StandardOpenOption.CREATE,
            StandardOpenOption.TRUNCATE_EXISTING
    );
} else {
    System.out.print(stdout);
}

if (stderrFile != null) {
    Files.writeString(
            currentDirectory.resolve(stderrFile),
            stderr,
            StandardOpenOption.CREATE,
            StandardOpenOption.TRUNCATE_EXISTING
    );
} else if (!stderr.isEmpty()) {
    System.err.print(stderr);
}
                } catch (IOException e) {
                    System.out.println(input + ": command not found");
                }

                continue;
            }

            System.out.println(input + ": command not found");
        }
    }

    private static String[] parseCommand(String input) {
        List<String> tokens = new ArrayList<>();
        StringBuilder current = new StringBuilder();

        boolean inSingleQuote = false;
        boolean inDoubleQuote = false;

        for (int i = 0; i < input.length(); i++) {
            char ch = input.charAt(i);

            if (ch == '\\') {
                if (inSingleQuote) {
                    current.append(ch);
                    continue;
                }

                if (inDoubleQuote) {
                    if (i + 1 < input.length()) {
                        char next = input.charAt(i + 1);

                        if (next == '\\' || next == '"' || next == '$' || next == '\n') {
                            current.append(next);
                            i++;
                        } else {
                            current.append('\\');
                        }
                    } else {
                        current.append('\\');
                    }
                    continue;
                }

                if (i + 1 < input.length()) {
                    current.append(input.charAt(i + 1));
                    i++;
                }
                continue;
            }

            if (ch == '\'' && !inDoubleQuote) {
                inSingleQuote = !inSingleQuote;
                continue;
            }

            if (ch == '"' && !inSingleQuote) {
                inDoubleQuote = !inDoubleQuote;
                continue;
            }

            if (Character.isWhitespace(ch) && !inSingleQuote && !inDoubleQuote) {
                if (current.length() > 0) {
                    tokens.add(current.toString());
                    current.setLength(0);
                }
            } else {
                current.append(ch);
            }
        }

        if (current.length() > 0) {
            tokens.add(current.toString());
        }

        return tokens.toArray(new String[0]);
    }

    private static String findExecutable(String command) {
        String pathEnv = System.getenv("PATH");

        if (pathEnv == null) {
            return null;
        }

        String[] paths = pathEnv.split(File.pathSeparator);

        for (String path : paths) {
            File file = new File(path, command);

            if (file.isFile() && file.canExecute()) {
                return file.getAbsolutePath();
            }
        }

        return null;
    }
}