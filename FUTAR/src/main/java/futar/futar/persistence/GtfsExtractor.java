package futar.futar.persistence;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class GtfsExtractor {
    public static void extractGtfs(String zipPath, String targetDir) throws Exception {
        try (ZipInputStream zis = new ZipInputStream(new FileInputStream(zipPath))) {
            ZipEntry entry;
            while ((entry = zis.getNextEntry()) != null) {
                File newFile = new File(targetDir, entry.getName());
                if (entry.isDirectory()) {
                    newFile.mkdirs();
                    continue;
                }
                new File(newFile.getParent()).mkdirs();
                try (FileOutputStream fos = new FileOutputStream(newFile)) {
                    zis.transferTo(fos);
                }
            }
        }
    }

    public static void cleanDirectory(String dirPath) throws Exception {
        Files.walk(Paths.get(dirPath))
                .map(java.nio.file.Path::toFile)
                .sorted((a, b) -> -a.compareTo(b))
                .forEach(File::delete);
    }
}