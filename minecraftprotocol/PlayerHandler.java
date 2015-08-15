package minecraftprotocol;


import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.Socket;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

public class PlayerHandler extends Socket {

    String host;
    int port;

    public PlayerHandler(String host, int port) throws IOException {
        super(host, port);
        this.host = host;
        this.port = port;
    }

    public void chat(String msg) throws UnsupportedEncodingException, IOException {
        byte[] data = Untils.getStringBytes(msg);
        byte[] packetId = new byte[]{0, 0x01};
        byte[] length = Untils.parVarint(data.length + packetId.length);
        write(length);
        write(packetId);
        write(data);
    }

    public void writeChannel(byte[] bytes) throws UnsupportedEncodingException, IOException {
        byte[] packetId = new byte[]{(byte) 0x17};//0x00 for uncompressed
        byte[] channel = Untils.getStringBytes("MC|BEdit");
        //channel占位 保证数据长度
        byte[] data = Untils.compress(Untils.append(Untils.append(packetId, channel), bytes));
        byte[] dataLeng = Untils.parVarint(data.length);
        byte[] leng = Untils.parVarint(dataLeng.length + data.length);
        write(leng);
        write(dataLeng);
        write(data);
    }

    static Random rand = new Random();

    public void writeItemChange() throws IOException {
        byte[] data = new byte[]{0x00, (byte) rand.nextInt(9)};
        byte[] packetId = new byte[]{0x00, 0x09};
        byte[] length = Untils.parVarint(data.length + packetId.length);
        while (true) {
            write(length);
            write(packetId);
            write(data);
        }
    }

    public void writeAnimation() throws IOException {
        byte[] packetId = new byte[]{0x00, 0x0A};
        byte[] length = Untils.parVarint(packetId.length);
        while (true) {
            write(length);
            write(packetId);
        }
    }

    public void writeTabComplete(byte[] data) throws UnsupportedEncodingException, IOException {
        byte[] packetId = new byte[]{0x14};
        byte[] bool = new byte[]{0};
        byte[] compression = Untils.compress(Untils.append(Untils.append(packetId, data),bool));
        byte[] dataLength = Untils.parVarint(compression.length);
        byte[] length = Untils.parVarint(dataLength.length+compression.length);
        write(length);
        write(dataLength);
        write(compression);
    }

    private void write(byte[] bytes) throws IOException {
        handle().write(bytes);
    }

    public void handshake() throws UnsupportedEncodingException, IOException {
        byte[] packetid = new byte[]{(byte) 0};
        byte[] version = Untils.parVarint(47);
        byte[] server = Untils.getStringBytes(host);
        byte[] port = new byte[]{(byte) (this.port >> 8), (byte) (this.port & 0xff)};
        byte[] state = Untils.parVarint(2);
        byte[] length = Untils.parVarint(packetid.length + version.length
                + server.length + port.length + state.length);
        handle().write(length);
        handle().write(packetid);
        handle().write(version);
        handle().write(server);
        handle().write(port);
        handle().write(state);
    }

    public void login(String name) throws UnsupportedEncodingException, IOException {
        byte[] packetid = new byte[]{(byte) 0};
        byte[] nameBytes = Untils.getStringBytes(name);
        byte[] length = Untils.parVarint(packetid.length + nameBytes.length);
        handle().write(length);
        handle().write(packetid);
        handle().write(nameBytes);
    }

    public void writeVarint(int i) throws IOException {
        handle().write(Untils.parVarint(i));
    }

    public void writeString(String str) throws UnsupportedEncodingException, IOException {
        byte[] bytes = str.getBytes("UTF-8");
        int length = bytes.length;
        writeVarint(length);
        handle().write(bytes);
    }

    public OutputStream handle() {
        try {
            return this.getOutputStream();
        } catch (IOException ex) {
            Logger.getLogger(PlayerHandler.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
}
