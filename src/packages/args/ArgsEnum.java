package packages.args;

public enum ArgsEnum {
    HELP("help", "h", "Show help", ""),
    VERSION("version", "v", "Show version", ""),
    VERBOSE("verbose", "V", "Verbose mode", ""),
    OUTPUT_DIR("output", "o", "Output file", "./output"),
    INPUT_DIR("input", "i", "Input file", "./input"),
    FILE_ENERGY_PREFIX("file_energy_prefix", "fep", "Energy file prefix", "consumption"),
    FILE_WEATHER_PREFIX("file_weather_prefix", "fwp", "File weather prefix", "weather"),
    REMOVE_HEADER("remove_header", "rh", "Remove header", "true"),
    LINE_LIMIT("line_limit", "ll", "Line limit", "0"),
    ;

    private final String longName;
    private final String shortName;
    private final String description;
    private final String defaultValue;

    ArgsEnum(String longName, String shortName, String description, String defaultValue) {
        this.longName = longName;
        this.shortName = shortName;
        this.description = description;
        this.defaultValue = defaultValue;
    }

    public String getLongName() {
        return longName;
    }

    public String getShortName() {
        return shortName;
    }

    public String getDescription() {
        return description;
    }

    public String getDefaultValue() {
        return defaultValue;
    }

    public String compareTo(String arg) {
        if (arg.equals("-" + this.getShortName()) || arg.equals("--" + this.getLongName())) {
            return this.getLongName();
        } else {
            return "";
        }
    }

}