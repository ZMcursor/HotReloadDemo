package hotreload.scheduler;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.math.BigInteger;
import java.nio.channels.FileChannel;
import java.security.MessageDigest;

public class Utils {

    // 计算文件的MD5散列值
    public static String getFileMD5(File file) {
        if (!(file.isFile() && file.canRead())) {
            return null;
        }
        MessageDigest md = null;
        FileInputStream fis = null;
        byte buffer[] = new byte[4 * 1024];
        int len;
        try {
            md = MessageDigest.getInstance("MD5");
            fis = new FileInputStream(file);
            while ((len = fis.read(buffer)) != -1) {
                md.update(buffer, 0, len);
            }
            fis.close();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        BigInteger bigInt = new BigInteger(1, md.digest());
        return bigInt.toString(16);
    }

    // 复制文件
    public static boolean copyFile(File from, File to) {
        FileInputStream inStream = null;
        FileOutputStream outStream = null;
        try {
            if (!(from.exists() && from.isFile() && from.canRead()))
                return false;
            to.createNewFile();

            inStream = new FileInputStream(from);
            FileChannel in = inStream.getChannel();
            outStream = new FileOutputStream(to);
            FileChannel out = outStream.getChannel();
            in.transferTo(0, in.size(), out);
            return true;
        } catch (Exception e) {
        } finally {
            try {
                if (inStream != null)
                    inStream.close();
                if (outStream != null)
                    outStream.close();
            } catch (Exception e) {
            }
        }
        return false;
    }
}