package com.jcod3r.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

public class IP {

    public static String randomIp() {
        Random r = new Random();
        StringBuffer str = new StringBuffer();
        str.append(r.nextInt(1000000) % 255);
        str.append(".");
        str.append(r.nextInt(1000000) % 255);
        str.append(".");
        str.append(r.nextInt(1000000) % 255);
        str.append(".");
        str.append(0);

        return str.toString();
    }

    public static void main(String[] args) throws IOException {
        Resource resource = new ClassPathResource("17monipdb.dat");

        IP.load(resource.getFile().getAbsolutePath());

        Long st = System.nanoTime();
        for (int i = 0; i < 1000000; i++) {
            IP.find(IP.randomIp());
        }
        Long et = System.nanoTime();
        System.out.println((et - st) / 1000 / 1000);

        System.out.println(Arrays.toString(IP.find("118.28.8.8")));
    }

    public static boolean enableFileWatch = false;

    private static int offset;
    private static int[] index = new int[256];
    private static ByteBuffer dataBuffer;
    private static ByteBuffer indexBuffer;
    private static Long lastModifyTime = 0L;
    private static File ipFile;
    private static ReentrantLock lock = new ReentrantLock();

    public static void load(String filename) {
        IP.ipFile = new File(filename);
        IP.load();
        if (IP.enableFileWatch) {
            IP.watch();
        }
    }

    public static void load(String filename, boolean strict) throws Exception {
        IP.ipFile = new File(filename);
        if (strict) {
            int contentLength = Long.valueOf(IP.ipFile.length()).intValue();
            if (contentLength < 512 * 1024) {
                throw new Exception("ip data file error.");
            }
        }
        IP.load();
        if (IP.enableFileWatch) {
            IP.watch();
        }
    }

    public static String[] find(String ip) {
        int ip_prefix_value = new Integer(ip.substring(0, ip.indexOf(".")));
        long ip2long_value = IP.ip2long(ip);
        int start = IP.index[ip_prefix_value];
        int max_comp_len = IP.offset - 1028;
        long index_offset = -1;
        int index_length = -1;
        byte b = 0;
        for (start = start * 8 + 1024; start < max_comp_len; start += 8) {
            if (IP.int2long(IP.indexBuffer.getInt(start)) >= ip2long_value) {
                index_offset = IP.bytesToLong(b, IP.indexBuffer.get(start + 6),
                    IP.indexBuffer.get(start + 5),
                    IP.indexBuffer.get(start + 4));
                index_length = 0xFF & IP.indexBuffer.get(start + 7);
                break;
            }
        }

        byte[] areaBytes;

        IP.lock.lock();
        try {
            IP.dataBuffer.position(IP.offset + (int) index_offset - 1024);
            areaBytes = new byte[index_length];
            IP.dataBuffer.get(areaBytes, 0, index_length);
        } finally {
            IP.lock.unlock();
        }

        return new String(areaBytes, Charset.forName("UTF-8")).split("\t", -1);
    }

    private static void watch() {
        Executors.newScheduledThreadPool(1).scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                long time = IP.ipFile.lastModified();
                if (time > IP.lastModifyTime) {
                    IP.lastModifyTime = time;
                    IP.load();
                }
            }
        }, 1000L, 5000L, TimeUnit.MILLISECONDS);
    }

    private static void load() {
        IP.lastModifyTime = IP.ipFile.lastModified();
        FileInputStream fin = null;
        IP.lock.lock();
        try {
            IP.dataBuffer = ByteBuffer.allocate(Long
                .valueOf(IP.ipFile.length()).intValue());
            fin = new FileInputStream(IP.ipFile);
            int readBytesLength;
            byte[] chunk = new byte[4096];
            while (fin.available() > 0) {
                readBytesLength = fin.read(chunk);
                IP.dataBuffer.put(chunk, 0, readBytesLength);
            }
            IP.dataBuffer.position(0);
            int indexLength = IP.dataBuffer.getInt();
            byte[] indexBytes = new byte[indexLength];
            IP.dataBuffer.get(indexBytes, 0, indexLength - 4);
            IP.indexBuffer = ByteBuffer.wrap(indexBytes);
            IP.indexBuffer.order(ByteOrder.LITTLE_ENDIAN);
            IP.offset = indexLength;

            int loop = 0;
            while (loop++ < 256) {
                IP.index[loop - 1] = IP.indexBuffer.getInt();
            }
            IP.indexBuffer.order(ByteOrder.BIG_ENDIAN);
        } catch (IOException ioe) {
            ioe.printStackTrace();
        } finally {
            try {
                if (fin != null) {
                    fin.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            IP.lock.unlock();
        }
    }

    private static long bytesToLong(byte a, byte b, byte c, byte d) {
        return IP.int2long((a & 0xff) << 24 | (b & 0xff) << 16
            | (c & 0xff) << 8 | d & 0xff);
    }

    private static int str2Ip(String ip) {
        String[] ss = ip.split("\\.");
        int a, b, c, d;
        a = Integer.parseInt(ss[0]);
        b = Integer.parseInt(ss[1]);
        c = Integer.parseInt(ss[2]);
        d = Integer.parseInt(ss[3]);
        return a << 24 | b << 16 | c << 8 | d;
    }

    private static long ip2long(String ip) {
        return IP.int2long(IP.str2Ip(ip));
    }

    private static long int2long(int i) {
        long l = i & 0x7fffffffL;
        if (i < 0) {
            l |= 0x080000000L;
        }
        return l;
    }
}