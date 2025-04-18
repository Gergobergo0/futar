package futar.futar.persistence;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.MessageDigest;

public class GtfsDownloader {
    private static final String GTFS_URL = "https://bkk.hu/gtfs/budapest_gtfs.zip"; // TODO: cseréld ki BKK GTFS URL-re
    private static final String DOWNLOAD_PATH = "data/gtfs.zip";
    private static final String LAST_HASH_PATH = "data/gtfs.hash";

    public static boolean isUpdated() throws Exception {
        byte[] newData = downloadBytes(GTFS_URL);
        String newHash = sha256(newData);

        File hashFile = new File(LAST_HASH_PATH);
        if (hashFile.exists()) {
            String oldHash = Files.readString(Paths.get(LAST_HASH_PATH));
            if (oldHash.equals(newHash)) return false;
        }

        Files.write(Paths.get(DOWNLOAD_PATH), newData);
        Files.writeString(Paths.get(LAST_HASH_PATH), newHash);
        return true;
    }

    private static byte[] downloadBytes(String urlStr) throws Exception {
        URL url = new URL(urlStr);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        try (InputStream in = conn.getInputStream()) {
            return in.readAllBytes();
        }
    }

    private static String sha256(byte[] data) throws Exception {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] hash = digest.digest(data);
        StringBuilder hexString = new StringBuilder();
        for (byte b : hash) hexString.append(String.format("%02x", b));
        return hexString.toString();
    }
}