package cloud.stonehouse.s3backup;

import org.bukkit.entity.Player;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

class Archive {

    private final S3Backup s3Backup;
    private List<String> fileList;

    Archive(S3Backup s3Backup) {
        this.s3Backup = s3Backup;
        this.fileList = new ArrayList<>();
    }

    void zipFile(Player player, String sourceFile, String destinationFile, boolean dryRun) throws IOException {
        FileOutputStream fos = null;
        ZipOutputStream zipOut = null;
        if (!dryRun) {
            fos = new FileOutputStream(destinationFile);
            zipOut = new ZipOutputStream(fos);
        }

        File fileToZip = new File(sourceFile);

        zipFile(player, fileToZip, fileToZip.getName(), zipOut, dryRun);

        if (dryRun) {
            writeDryRunLog(player, destinationFile + ".dryrun.txt");
            return;
        }

        zipOut.close();
        fos.close();
    }

    void deleteFile(String sourceFile) {
        File fileToDelete = new File(sourceFile);
        if (!fileToDelete.delete()) {
            s3Backup.sendMessage(null, "Failed to delete temporary file " + sourceFile);
        }
    }

    private void zipFile(Player player, File fileToZip, String fileName, ZipOutputStream zipOut, boolean dryRun) throws IOException {
        if (fileToZip.isDirectory()) {
            if (!dryRun) {
                if (fileName.endsWith(File.separator)) {
                    zipOut.putNextEntry(new ZipEntry(fileName));
                } else {
                    zipOut.putNextEntry(new ZipEntry(fileName + File.separator));
                }
                zipOut.closeEntry();
            }

            File[] children = fileToZip.listFiles();
            for (File childFile : children) {
                String childPath = childFile.getCanonicalPath();
                if (!childFile.getName().startsWith(s3Backup.getFileConfig().getBackupDir()) &&
                        Arrays.stream(s3Backup.getFileConfig().getIgnoreFiles()).noneMatch(childPath::contains)) {
                    try {
                        zipFile(player, childFile, fileName + File.separator + childFile.getName(), zipOut, dryRun);
                    } catch (IOException e) {
                        s3Backup.exception(player, "Error backing up file " + childFile.getName(), e);
                    }
                }
            }
            return;
        }
        fileList.add(fileToZip.getAbsolutePath());

        if (dryRun) {
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

    private void writeDryRunLog(Player player, String destinationFile) throws IOException {
        FileOutputStream outputStream = new FileOutputStream(destinationFile);
        String implodedFileList = String.join(System.lineSeparator(), fileList);
        outputStream.write(implodedFileList.getBytes(StandardCharsets.UTF_8));
        s3Backup.sendMessage(player, "Wrote dry-run file");
    }
}
