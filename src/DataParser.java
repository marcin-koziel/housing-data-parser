import packages.args.ArgsEnum;
import packages.args.ArgsParser;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

public class DataParser {

    public DataParser(String[] args) {
        ap.parse(args);
    }
    public static double totalLines = 0;
    public static HashMap<String, String> multipleFiles = new HashMap<>();

    private final List<String> energyPaths = new ArrayList<>();
    private final List<String> weatherPaths = new ArrayList<>();
    private static final HashMap<String, String> energyKeys = new HashMap<>();
    private final ArgsParser ap = new ArgsParser("0.0.1");


    public boolean run() {
        long startTime;
        long endTime;

        if (ap.isArgValueTrue(ArgsEnum.VERBOSE.getLongName())) System.out.println("### Verbose mode enabled ###");
        initPaths(
                ap.getArgValue(ArgsEnum.INPUT_DIR.getLongName()),
                ap.getArgValue(ArgsEnum.FILE_ENERGY_PREFIX.getLongName()),
                ap.getArgValue(ArgsEnum.FILE_WEATHER_PREFIX.getLongName()),
                ap.isArgValueTrue(ArgsEnum.VERBOSE.getLongName())
        );

        try {

            startTime = System.currentTimeMillis();
            if (ap.isArgValueTrue(ArgsEnum.VERBOSE.getLongName())) System.out.println("# Deleting output directory...");
            Path outputDir = Paths.get(ap.getArgValue(ArgsEnum.OUTPUT_DIR.getLongName()));
            if (Files.exists(outputDir)) {
                Files.walk(outputDir)
                        .sorted((o1, o2) -> o2.compareTo(o1))
                        .map(Path::toFile)
                        .forEach(File::delete);
            }
            endTime = System.currentTimeMillis();
            if (ap.isArgValueTrue(ArgsEnum.VERBOSE.getLongName())) System.out.println("# Reading consumption files... Elapsed time: " + (endTime - startTime) + " ms");

            startTime = System.currentTimeMillis();
            if (ap.isArgValueTrue(ArgsEnum.VERBOSE.getLongName())) System.out.println("# Reading and Writing consumption files...");
            for (String energyPath : energyPaths) {
                EnergyReadWriteThread energyReadWriteThread = new EnergyReadWriteThread(
                        energyPath,
                        ap.getArgValue(ArgsEnum.FILE_ENERGY_PREFIX.getLongName()),
                        ap.getArgValue(ArgsEnum.OUTPUT_DIR.getLongName()),
                        energyKeys,
                        ap.isArgValueTrue(ArgsEnum.REMOVE_HEADER.getLongName()),
                        ap.getArgValueAsInt(ArgsEnum.LINE_LIMIT.getLongName())
                );
                energyReadWriteThread.run();
            }
            endTime = System.currentTimeMillis();
            if (ap.isArgValueTrue(ArgsEnum.VERBOSE.getLongName())) System.out.println("## Reading and Writing consumption files... Elapsed time: " + (endTime - startTime) + " ms");

            startTime = System.currentTimeMillis();
            if (ap.isArgValueTrue(ArgsEnum.VERBOSE.getLongName())) System.out.println("# Reading and Writing weather files...");
            for (String weatherPath : weatherPaths) {
                WeatherReadWriteThread weatherReadWriteThread = new WeatherReadWriteThread(
                        weatherPath,
                        ap.getArgValue(ArgsEnum.FILE_WEATHER_PREFIX.getLongName()),
                        ap.getArgValue(ArgsEnum.OUTPUT_DIR.getLongName()),
                        energyKeys,
                        ap.isArgValueTrue(ArgsEnum.REMOVE_HEADER.getLongName()),
                        ap.getArgValueAsInt(ArgsEnum.LINE_LIMIT.getLongName())
                );
                weatherReadWriteThread.run();
            }
            endTime = System.currentTimeMillis();
            if (ap.isArgValueTrue(ArgsEnum.VERBOSE.getLongName())) System.out.println("## Reading and Writing weather files... Elapsed time: " + (endTime - startTime) + " ms");

            startTime = System.currentTimeMillis();
            if (ap.isArgValueTrue(ArgsEnum.VERBOSE.getLongName())) System.out.println("\n# Cleaning up files...");
            combineFilesInDir(ap.isArgValueTrue(ArgsEnum.VERBOSE.getLongName()));
            endTime = System.currentTimeMillis();
            if (ap.isArgValueTrue(ArgsEnum.VERBOSE.getLongName())) System.out.println("## Cleaning up files... Elapsed time: " + (endTime - startTime) + " ms");

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

        if (ap.isArgValueTrue(ArgsEnum.VERBOSE.getLongName())) System.out.println("\n");
        return true;
    }

    private void initPaths(String inputDir, String energyPrefix, String weatherPrefix, Boolean verbose) {

        Path path = Paths.get(inputDir);
        if (path.toFile().isDirectory()) {
            for (String file : Objects.requireNonNull(path.toFile().list())) {
                try {
                    if (file.toLowerCase().startsWith(energyPrefix.toLowerCase())) {
                        energyPaths.add(inputDir + "/" + file);
                    } else if (file.toLowerCase().startsWith(weatherPrefix.toLowerCase())) {
                        weatherPaths.add(inputDir + "/" + file);
                    }
                } catch (Exception e) {
                    System.out.println("InitPaths Error: " + e.getMessage());
                    System.exit(1);
                }
            }
        }

        if (verbose) {
            System.out.println("\nEnergy files:");
            for (String energyPath : energyPaths) {
                System.out.println(energyPath);
            }
            System.out.println("\nWeather files:");
            for (String weatherPath : weatherPaths) {
                System.out.println(weatherPath);
            }
        }
        if (verbose) System.out.println("\n");
    }

    private void combineFilesInDir(boolean verbose) {
        for (String key : multipleFiles.keySet()) {
            String dir = multipleFiles.get(key);
            System.out.println("### Multiple files found in dir: " + dir);
            File dirFile = new File(dir);
            File[] files = dirFile.listFiles();
            if (files != null) {
                String shortestFileName = files[0].getName();
                for (File file : files) {
                    if (file.getName().length() < shortestFileName.length()) {
                        shortestFileName = file.getName();
                    }
                }
                try {
                    BufferedWriter writer = new BufferedWriter(new FileWriter(dir + "/" + shortestFileName, true));
                    for (File file : files) {
                        if (!file.getName().equals(shortestFileName)) {
                            BufferedReader reader = new BufferedReader(new FileReader(file));
                            String line;
                            while ((line = reader.readLine()) != null) {
                                writer.append(line);
                                writer.newLine();
                            }
                            reader.close();
                            file.delete();
                        }
                    }
                    writer.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }
    }

}
