package edu.evgen;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Main {
    static ArrayList<String> integers = new ArrayList<>();

    static ArrayList<String> doubles = new ArrayList<>();

    static ArrayList<String> strings = new ArrayList<>();

    static Boolean additionalRecording = false;
    static String prefix = "";
    static String path = "";
    static Boolean shortStatistic = false;
    static Boolean fullStatiistic = false;
    static ArrayList<String> inputFiles = new ArrayList<>();
    static List<BufferedReader> readers = null;

    public static void main(String[] args) {
        if (args.length == 0) {
            System.out.println("No arguments found!\nUsage: util.jar inputFiles...");
            System.out.println("Options: [-a] - additional recording");
            System.out.println("         [-p prefix] - add a prefix to output files");
            System.out.println("         [-o some/path] - add a path to output files");
            System.out.println("         [-s] - show short statistics");
            System.out.println("         [-f] - show full statistics");
        } else {

            for (int i = 0; i < args.length; i++) {
                String arg = args[i];
                if (arg.equals("-a"))
                    additionalRecording = true;
                else if (arg.equals("-p")) {
                    if (i < args.length - 1) {
                        arg = args[++i];
                        prefix = arg;
                    } else {
                        System.out.println("There is must be a prefix after [-p]");
                        return;
                    }
                } else if (arg.equals("-s")) shortStatistic = true;
                else if (arg.equals("-f")) fullStatiistic = true;
                else if (arg.equals("-o")) {
                    if (i < args.length - 1) {
                        arg = args[++i];
                        path = arg;
                    } else {
                        System.out.println("There is must be a path after [-a]");
                        return;
                    }
                } else {
                    if (!inputFiles.contains(arg))
                        inputFiles.add(arg);
                }
                ;

            }

            readers = inputFiles
                    .stream().map(file -> {
                                try {
                                    return new BufferedReader(new FileReader(file));
                                } catch (IOException e) {
                                    System.out.println("file not found: " + file);
                                    closingReaders();
                                    System.exit(1);
                                    return null;
                                }
                            }
                    ).collect(Collectors.toList());

            parsingFiles();
            showStatistics();

        }
    }

    public static void parsingFiles() {
        firstScaningForFiles();
        writingListInFile(integers, path + prefix + "integers.txt");
        writingListInFile(doubles, path + prefix + "floats.txt");
        writingListInFile(strings, path + prefix + "strings.txt");
    }

    private static void writingListInFile(ArrayList<String> arr, String path) {
        try (BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(path, additionalRecording))) {
            arr.forEach(
                    elem -> {
                        try {
                            bufferedWriter.write(elem);
                            bufferedWriter.newLine();
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    }
            );
        } catch (IOException e) {
            System.out.println("Can't write in file " + path);
        }
    }

    private static void firstScaningForFiles() {
        readers.forEach(
                reader -> {
                    String line;
                    try {
                        while ((line = reader.readLine()) != null) {
                            switch (lineVariable(line)) {
                                case 1:
                                    integers.add(line);
                                    break;
                                case 2:
                                    doubles.add(line);
                                    break;
                                case 3:
                                    strings.add(line);
                                    break;
                            }
                        }
                    } catch (IOException e) {
                        System.out.println("IO exception " + e);
                    }
                }
        );
    }

    public static void closingReaders() {
        if (readers != null)
            readers.forEach(bufferedReader -> {
                try {
                    bufferedReader.close();
                } catch (IOException e) {
                    System.out.println("File closing error " + bufferedReader.toString());
                }
            });
    }

    public static void showStatistics() {
        if (fullStatiistic) {

        } else if (shortStatistic) {
            System.out.println("Integers " + integers.size() + "\nDoubles " + doubles.size() + "\nStrings " + strings.size());
        }
    }

    public static int lineVariable(String line) {
        if (Main.isValidInteger(line)) return 1;
        else if (Main.isValidFloat(line)) return 2;
        else return 3;
    }

    public static boolean isValidInteger(String line) {
        if (line == null) {
            return false;
        }
        try {
            Integer.parseInt(line.trim());
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }


    public static boolean isValidFloat(String line) {
        if (line == null) {
            return false;
        }
        try {
            Double.parseDouble(line);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

}