package cloud.stonehouse.s3backup;

import org.bukkit.entity.Player;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

class Archive {

    private final S3Backup s3Backup;

    Archive(S3Backup s3Backup) {
        this.s3Backup = s3Backup;
    }

    void zipFile(Player player, String sourceFile, String destinationFile) throws IOException {
        FileOutputStream fos = new FileOutputStream(destinationFile);
        ZipOutputStream zipOut = new ZipOutputStream(fos);
        File fileToZip = new File(sourceFile);

        zipFile(player, fileToZip, fileToZip.getName(), zipOut);
        zipOut.close();
        fos.close();
    }

    void deleteFile(String sourceFile) {
        File fileToDelete = new File(sourceFile);
        if (!fileToDelete.delete()) {
            s3Backup.sendMessage(null, "Failed to delete temporary file " + sourceFile);
        }
    }

    private void zipFile(Player player, File fileToZip, String fileName, ZipOutputStream zipOut) throws IOException {
        if (fileToZip.isDirectory()) {
            if (fileName.endsWith(File.separator)) {
                zipOut.putNextEntry(new ZipEntry(fileName));
                zipOut.closeEntry();
            } else {
                zipOut.putNextEntry(new ZipEntry(fileName + File.separator));
                zipOut.closeEntry();
            }
            File[] children = fileToZip.listFiles();
            for (File childFile : children) {
                if (!childFile.getName().startsWith(s3Backup.getFileConfig().getLocalPrefix()) &&
                        !childFile.getCanonicalPath().endsWith("s3backup/config.yml")) {
                    try {
                        zipFile(player, childFile, fileName + File.separator + childFile.getName(), zipOut);
                    } catch (IOException e) {
                        s3Backup.exception(player, "Error backing up file " + childFile.getName(), e);
                    }
                }
            }
            return;
        }
        FileInputStream fis = new FileInputStream(fileToZip);
        ZipEntry zipEntry = new ZipEntry(fileName);
        zipOut.putNextEntry(zipEntry);
        byte[] bytes = new byte[1024];
        int length;

        while ((length = fis.read(bytes)) >= 0) {
            zipOut.write(bytes, 0, length);
        }
        fis.close();
    }
}
