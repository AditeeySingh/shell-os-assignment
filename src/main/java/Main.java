import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
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
    String text = input.substring(5);

    StringBuilder result = new StringBuilder();
    boolean inSingleQuote = false;

    for (int i = 0; i < text.length(); i++) {
        char ch = text.charAt(i);

        if (ch == '\'') {
            inSingleQuote = !inSingleQuote;
            continue;
        }

        if (!inSingleQuote && Character.isWhitespace(ch)) {
            while (i + 1 < text.length() && Character.isWhitespace(text.charAt(i + 1))) {
                i++;
            }
            result.append(' ');
        } else {
            result.append(ch);
        }
    }

    System.out.println(result.toString().trim());
    continue;
}

    System.out.println(text);
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

            String[] parts = input.split(" ");
            String executable = findExecutable(parts[0]);

            if (executable != null) {
                try {
                    ProcessBuilder processBuilder = new ProcessBuilder(parts);
                    processBuilder.directory(currentDirectory.toFile());

                    Process process = processBuilder.start();
                    process.getInputStream().transferTo(System.out);
                    process.waitFor();
                } catch (IOException e) {
                    System.out.println(input + ": command not found");
                }

                continue;
            }

            System.out.println(input + ": command not found");
        }
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