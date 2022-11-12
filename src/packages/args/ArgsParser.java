package packages.args;

import java.util.HashMap;
import java.util.Map;

public class ArgsParser {

    private final Map<ArgsEnum, String> parsedArgs = new HashMap<>();

    public ArgsParser() {
        parsedArgs.put(ArgsEnum.VERSION, ArgsEnum.VERSION.getDefaultValue());
    }

    public ArgsParser(String version) {
        parsedArgs.put(ArgsEnum.VERSION, version);
    }

    public void parse(String[] args) {

        try {
            for (int i = 0; i < args.length; i++) {
                for (ArgsEnum arg : ArgsEnum.values()) {
                    if (arg.compareTo(args[i]).equals(arg.getLongName())) {
                        if (arg.equals(ArgsEnum.HELP)) {
                            printHelp();
                            System.exit(0);
                        } else if (arg.equals(ArgsEnum.VERSION)) {
                            printVersion();
                            System.exit(0);
                        } else {
                            parsedArgs.put(arg, args[i + 1]);
                        }
                    }
                }
            }
        } catch (Exception e) {
            System.out.println("Parse Error: " + e.getMessage());
            System.exit(1);
        }

        parsedArgs.putIfAbsent(ArgsEnum.VERBOSE, ArgsEnum.VERBOSE.getDefaultValue());
        parsedArgs.putIfAbsent(ArgsEnum.INPUT_DIR, ArgsEnum.INPUT_DIR.getDefaultValue());
        parsedArgs.putIfAbsent(ArgsEnum.OUTPUT_DIR, ArgsEnum.OUTPUT_DIR.getDefaultValue());
        parsedArgs.putIfAbsent(ArgsEnum.FILE_ENERGY_PREFIX, ArgsEnum.FILE_ENERGY_PREFIX.getDefaultValue());
        parsedArgs.putIfAbsent(ArgsEnum.FILE_WEATHER_PREFIX, ArgsEnum.FILE_WEATHER_PREFIX.getDefaultValue());
        parsedArgs.putIfAbsent(ArgsEnum.REMOVE_HEADER, ArgsEnum.REMOVE_HEADER.getDefaultValue());
        parsedArgs.putIfAbsent(ArgsEnum.LINE_LIMIT, ArgsEnum.LINE_LIMIT.getDefaultValue());

    }

    public String getArgValue(String arg) {
        for (ArgsEnum argEnum : ArgsEnum.values()) {
            if (argEnum.getLongName().equals(arg)) {
                return parsedArgs.get(argEnum);
            }
        }
        return "";
    }

    public int getArgValueAsInt(String arg) { return Integer.parseInt(getArgValue(arg)); }

    public Boolean isArgValueTrue(String arg) {
        return Boolean.parseBoolean(getArgValue(arg));
    }

    private void printHelp() {
        System.out.println("Usage: java -jar <jarfile> [options]");
        System.out.println("Options:");
        for (ArgsEnum arg : ArgsEnum.values()) {
            System.out.println(String.format("  --%s, -%s\t%s\t(default: %s)", arg.getLongName(), arg.getShortName(), arg.getDescription(), arg.getDefaultValue()));
        }
    }

    private void printVersion() {
        if (parsedArgs.containsKey(ArgsEnum.VERSION)) {
            System.out.println("Version: " + parsedArgs.get(ArgsEnum.VERSION));
        } else {
            System.out.println("Version: " + ArgsEnum.VERSION.getDefaultValue());
        }
    }

}
