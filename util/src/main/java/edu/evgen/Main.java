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
            //input arguments handle
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
                        path = arg+"/";
                    } else {
                        System.out.println("There is must be a path after [-o]");
                        return;
                    }
                } else {
                    if (!inputFiles.contains(arg))
                        inputFiles.add(arg);
                }
                ;

            }
            //

            openBufferedReaders();
            parsingFiles();
            closingBufferedReaders();
            showStatistics();

        }
    }

    private static void openBufferedReaders() {
        readers = inputFiles
                .stream().map(file -> {
                            try {
                                return new BufferedReader(new FileReader(file));
                            } catch (IOException e) {
                                System.out.println("file not found: " + file);
                                closingBufferedReaders();
                                System.exit(1);
                                return null;
                            }
                        }
                ).collect(Collectors.toList());
    }

    public static void closingBufferedReaders() {
        if (readers != null)
            readers.forEach(bufferedReader -> {
                try {
                    bufferedReader.close();
                } catch (IOException e) {
                    System.out.println("File closing error " + bufferedReader.toString());
                }
            });
    }

    public static void parsingFiles() {
        firstScaningForFiles();
        writingListInFile(integers, path + prefix + "integers.txt");
        writingListInFile(doubles, path + prefix + "floats.txt");
        writingListInFile(strings, path + prefix + "strings.txt");
    }

    private static void writingListInFile(ArrayList<String> arr, String path) {
        if (arr.size() > 0) {
            try (BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(path, additionalRecording))) {
                if (additionalRecording)
                    bufferedWriter.newLine();
                for (int i = 0; i < arr.size(); i++) {
                    try {
                        bufferedWriter.write(arr.get(i));
                        if (i < arr.size() - 1)
                            bufferedWriter.newLine(); //last string doesn't have \n
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }

            } catch (IOException e) {
                System.out.println("Can't write in file " + path);
            }
        }
    }

    private static void firstScaningForFiles() {
        readers.forEach(
                reader -> {
                    String line;
                    try {
                        while ((line = reader.readLine()) != null) {
                            switch (lineVariable(line.trim())) {
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

    public static void showStatistics() {
        if (fullStatiistic) {
            if (integers.size() > 0) {
                try {
                    String intMax = "";
                    String intMin = integers.get(0);
                    Long sum = 0L;
                    for (int i = 0; i < integers.size(); i++) {
                        sum += Long.parseLong(integers.get(i));
                        if (firstIntBigger(integers.get(i), intMax))
                            intMax = integers.get(i);
                        else if (firstIntBigger(intMin, integers.get(i)))
                            intMin = integers.get(i);
                    }
                    System.out.println(String.format("|%-43s|", "integers: " + integers.size()));
                    System.out.println(String.format("|%-10s|%-10s|%-10s|%-10s|", "min", "max", "sum", "average"));
                    System.out.println(String.format("|%-10s|%-10s|%-10d|%-10d|", intMin, intMax, sum, sum / integers.size()));
                    System.out.println("---------------------------------------------");
                } catch (Throwable e) {

                    System.out.println(String.format("|%-43s|", "integers: " + integers.size()));
                    System.out.println(String.format("|%-43s|", "Unforchanetly there is a trouble with parsing too big integer value"));
                    System.out.println("---------------------------------------------");
                }
            } else {
                System.out.println(String.format("|%-43s|", "integers: " + integers.size()));
                System.out.println("---------------------------------------------");
            }
            if (doubles.size() > 0) {
                try {
                    Double min = Double.parseDouble(doubles.get(0));
                    Double max = Double.parseDouble(doubles.get(0));

                    Double sum = 0.0;
                    for (int i = 0; i < doubles.size(); i++) {
                        try {
                            sum += Double.parseDouble(doubles.get(i));
                        } catch (Throwable e) {

                        }
                        if (Double.parseDouble(doubles.get(i)) > max)
                            max = Double.parseDouble(doubles.get(i));
                        else if (Double.parseDouble(doubles.get(i)) < min)
                            min = Double.parseDouble(doubles.get(i));
                    }
                    System.out.println(String.format("|%-43s|", "doubles: " + doubles.size()));
                    System.out.println(String.format("|%-10s|%-10s|%-10s|%-10s|", "min", "max", "sum", "average"));
                    System.out.println(String.format("|%-10f|%-10f|%-10f|%-10f|", min, max, sum, sum / doubles.size()));
                    System.out.println("---------------------------------------------");

                } catch (Throwable e) {

                    System.out.println(String.format("|%-43s|", "doubles: " + doubles.size()));

                    System.out.println(String.format("|%-43s|", "Unforchanetly there is a trouble to parsing some double value"));
                    System.out.println("---------------------------------------------");
                }
            } else {

                System.out.println(String.format("|%-43s|", "doubles: " + doubles.size()));
                System.out.println("---------------------------------------------");
            }

            if (strings.size() > 0) {
                Integer shortest = strings.get(0).length();
                Integer longest = strings.get(0).length();

                for (int i = 0; i < strings.size(); i++) {
                    if (strings.get(i).length() < shortest)
                        shortest = strings.get(i).length();
                    if (strings.get(i).length() > longest)
                        longest = strings.get(i).length();
                }
                System.out.println(String.format("|%-43s|", "strings: " + strings.size()));
                System.out.println(String.format("|%-21s|%-21s|", "shortest size", "longest size"));
                System.out.println(String.format("|%-21d|%-21d|", shortest, longest, "", ""));

                System.out.println("---------------------------------------------");
            } else {

                System.out.println(String.format("|%-43s|", "strings: " + strings.size()));
                System.out.println("---------------------------------------------");
            }
        } else if (shortStatistic) {
            System.out.println("Integers: " + integers.size() + "\nDoubles: " + doubles.size() + "\nStrings: " + strings.size());
        }
    }

    public static boolean firstIntBigger(String first, String second) {
        if (first.length() > second.length())
            return true;
        else if (first.length() == second.length()) {
            for (int i = 0; i < first.length(); i++) {
                if (first.charAt(i) > second.charAt(i))
                    return true;
                else if (first.charAt(i) < second.charAt(i))
                    return false;
            }
        }
        return false;
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
        char c;
        for (int i = 0; i < line.length(); i++) {

            c = line.charAt(i);
            if ((i == 0 && c == '-')||c < 32)
                continue;
            if (c < '0' || c > '9')
                return false;
        }
        return true;
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