import java.io.*;
import java.util.HashMap;


public class WeatherReadWriteThread extends ReadWriteThread {

    public WeatherReadWriteThread(String rawFilePath, String filePrefix, String outputDir, HashMap<String, String> keyList, boolean removeHeader, int limit) {
        super(rawFilePath, filePrefix, outputDir, keyList, removeHeader, limit);
        this.outputDir = outputDir + "/weatherdata";
    }

    @Override
    public void run() {
        start();
    }

    public void start() {
        try {
            DataParser.totalLines += readWriteToFile();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public double readWriteToFile() throws Exception {
        BufferedReader br = new BufferedReader(new FileReader(rawFilePath));
        BufferedWriter bw = null;
        File file;

        if (removeHeader) br.readLine();

        String[] textLinesSplit;
        String currentKey = "";
        String line;
        String year;
        String ru;

        int countSimilarKeys = 0;
        double lineCount = 0;

        while ((line = br.readLine()) != null) {
            textLinesSplit = line.split("\t");
            year = textLinesSplit[0].substring(0, 4);
            ru = rawFilePath.split("_")[1].replace(".txt", "");
            lineCount++;

            if (!currentKey.startsWith(year + "-" + ru)) {
                if (bw != null) {
                    bw.close();
                    bw = null;
                }
                countSimilarKeys = 0;
                currentKey = year + "-" + ru;
            }

            if (keyList.keySet().stream().anyMatch(currentKey::startsWith) && !keyList.get(currentKey).equals(rawFilePath)) {

                if (!DataParser.multipleFiles.containsKey(currentKey.matches("^[0-9]+([+-]?(?=\\.\\d|\\d)(?:\\d+)?(?:\\.?\\d*))(?:[eE]([+-]?\\d+))?$"))) {
                    DataParser.multipleFiles.put(currentKey, outputDir + "/" + ru + "/" + year);
                }

                String finalYear = year;
                String finalRu = ru;
                countSimilarKeys = keyList.keySet().stream().filter(key -> key.startsWith(finalYear + "-" + finalRu)).toArray().length;
                if (bw != null) bw.close();
                file = initFile(outputDir + "/" + ru + "/" + year + "/" + filePrefix + "-" + currentKey, true, countSimilarKeys);
                bw = new BufferedWriter(new FileWriter(file, true));
                bw.write(line + "\n");
                currentKey = year + "-" + ru + "-" + countSimilarKeys;
                keyList.put(currentKey, rawFilePath);
            } else if (keyList.keySet().stream().anyMatch(currentKey::startsWith)) {
                if (bw == null) {
                    file = initFile(outputDir + "/" + ru + "/" + year + "/" + filePrefix + "-" + currentKey, false, countSimilarKeys);
                    bw = new BufferedWriter(new FileWriter(file, true));
                }
                bw.write(line + "\n");
            } else if (keyList.keySet().stream().noneMatch(currentKey::startsWith)) {
                if (bw != null) bw.close();
                file = initFile(outputDir + "/" + ru + "/" + year + "/" + filePrefix + "-" + currentKey, true, 0);
                bw = new BufferedWriter(new FileWriter(file, true));
                bw.write(line + "\n");
                keyList.put(currentKey, rawFilePath);
            }

            if (limit > 0 && lineCount >= limit) break;
        }

        bw.close();
        br.close();
        return lineCount;
    }


}
