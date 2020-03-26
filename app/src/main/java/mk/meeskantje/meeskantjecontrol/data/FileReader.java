//TODO maybe delete?
package mk.meeskantje.meeskantjecontrol.data;

import android.content.Context;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;

public class FileReader {

    public final static String LOCALSTORAGEFILENAME = "storage.json";

    public String read(Context context, String fileName) {
        try {
            FileInputStream fis = context.openFileInput(fileName);
            InputStreamReader isr = new InputStreamReader(fis);
            BufferedReader bufferedReader = new BufferedReader(isr);
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                sb.append(line);
            }
            return sb.toString();
        } catch (FileNotFoundException fileNotFound) {
            return null;
        } catch (IOException ioException) {
            return null;
        }
    }

    public boolean create(Context context, String fileName, String jsonString) {
        try {
            FileOutputStream fos = context.openFileOutput(fileName, Context.MODE_PRIVATE);
            if (jsonString != null) {
                fos.write(jsonString.getBytes());
            }
            fos.close();
            return true;
        } catch (FileNotFoundException fileNotFound) {
            return false;
        } catch (IOException ioException) {
            return false;
        }

    }

    public boolean isFilePresent(Context context, String fileName) {
        String path = context.getFilesDir().getAbsolutePath() + "/" + fileName;
        File file = new File(path);
        return file.exists();
    }

    public boolean checkIfLocalStorageActivated(Context context, String filename) {
        boolean isFilePresent = this.isFilePresent(context, filename);
        if (isFilePresent) {
            return true;
        } else {
            boolean isFileCreated = this.create(context, filename, "{}");
            if (isFileCreated) {
                System.out.println("File created");
                checkIfLocalStorageActivated(context, filename);
            } else {
                System.out.println("ERROR");
                return false;
            }
        }
        return false;
    }

    public void clearFile(Context context, String filename) {
        if (context.deleteFile(filename)) {
            System.out.println(filename + ": deleted;");
        } else {
            System.out.println(" ====== ERROR ======");
        }
    }
}
