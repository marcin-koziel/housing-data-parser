import java.util.concurrent.TimeUnit;

public class Main {

    public static void main(String[] args) {
        long startTime = System.currentTimeMillis();

        DataParser dataParser = new DataParser(args);
        boolean result = dataParser.run();

        if (result) {
            long endTime = System.currentTimeMillis();
            System.out.println("### SUCCESS ###");
            System.out.println("Total lines: " + String.format("%,d", (long) DataParser.totalLines));
            System.out.println("Time elapsed: " + String.format("%02d:%02d:%02d",
                    TimeUnit.MILLISECONDS.toHours(endTime - startTime),
                    TimeUnit.MILLISECONDS.toMinutes(endTime - startTime) -
                            TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(endTime - startTime)),
                    TimeUnit.MILLISECONDS.toSeconds(endTime - startTime) -
                            TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(endTime - startTime))));
            System.out.println("###############");
        } else {
            System.out.println("### ERROR ###");
        }

    }

}
