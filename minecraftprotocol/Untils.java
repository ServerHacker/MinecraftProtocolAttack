package minecraftprotocol;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.zip.Deflater;

public class Untils {

    public static int unsignedByteToInt(byte b) {
        return (int) b & 0xFF;
    }

    public static int readVarint(byte[] arrays) {
        int value = 0;
        for (int x = 0; x < arrays.length; x++) {
            value += (arrays[x] < 0 ? arrays[x] & 0x7F : arrays[x]) << x * 7;
        }
        return value;
    }

    public static int readVarint(int[] arrays) {
        int value = 0;
        for (int x = 0; x < arrays.length; x++) {
            value += (arrays[x] & 0x7F) << x * 7;
        }
        return value;
    }

    public static byte[] parVarint(int i) {
        ArrayList<Integer> list = new ArrayList<Integer>();
        int counter = 0;
        int x;
        while ((x = i >> (7 * (counter++))) != 0) {
            x = x & 0x7f;//保留7位
            x = x | 0x80;//msb保持
            list.add(x);
        }
        list.set(list.size() - 1, list.get(list.size() - 1) & 0x7f/*设置结束符*/);
        byte[] bytes = new byte[list.size()];
        for (int k = 0; k < list.size(); k++) {
            bytes[k] = (byte) (int) list.get(k);
        }
        return bytes;
    }

    public static byte[] getStringBytes(String str) throws UnsupportedEncodingException {
        byte[] bytes = str.getBytes("UTF-8");
        int length = bytes.length;
        byte[] size = parVarint(length);
        byte[] newbytes = new byte[size.length + bytes.length];
        for (int i = 0; i < size.length; i++) {
            newbytes[i] = size[i];
        }
        for (int j = size.length; j < newbytes.length; j++) {
            newbytes[j] = bytes[j - size.length];
        }
        return newbytes;
    }

    public static byte[] compress(byte[] data) {
        byte[] output = new byte[0];

        Deflater compresser = new Deflater();

        compresser.reset();
        compresser.setInput(data);
        compresser.finish();
        ByteArrayOutputStream bos = new ByteArrayOutputStream(data.length);
        try {
            byte[] buf = new byte[1024];
            while (!compresser.finished()) {
                int i = compresser.deflate(buf);
                bos.write(buf, 0, i);
            }
            output = bos.toByteArray();
        } catch (Exception e) {
            output = data;
            e.printStackTrace();
        } finally {
            try {
                bos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        compresser.end();
        return output;
    }

    
    public static byte[] append(byte[] b1, byte[] b2) {
        byte[] bytes = new byte[b1.length + b2.length];
        for (int i = 0; i < b1.length; i++) {
            bytes[i] = b1[i];
        }
        for (int j = b1.length; j < bytes.length; j++) {
            bytes[j] = b2[j - b1.length];
        }
        return bytes;
    }
}
