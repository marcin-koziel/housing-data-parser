import java.io.*;
import java.util.HashMap;


abstract class ReadWriteThread extends Thread {

    public String rawFilePath;
    public String filePrefix;
    public String outputDir;
    public final HashMap<String, String> keyList;
    public boolean removeHeader = false;
    public int limit = 0;

    public ReadWriteThread(String rawFilePath, String filePrefix, String outputDir, HashMap<String, String> keyList, boolean removeHeader, int limit) {
        this.rawFilePath = rawFilePath;
        this.filePrefix = filePrefix;
        this.outputDir = outputDir;
        this.keyList = keyList;
        this.removeHeader = removeHeader;
        this.limit = limit;
    }

    public File initFile(String filePath, boolean create, int index) throws IOException {
        File file = new File(index > 0 ? filePath + "-" + index + ".txt" : filePath + ".txt");
        if (create) {
            file.getParentFile().mkdirs();
            file.createNewFile();
        }
        return file;
    }

}
