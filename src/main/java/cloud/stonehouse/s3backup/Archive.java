package cloud.stonehouse.s3backup;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Objects;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

class Archive {

    private final S3Backup s3Backup;

    Archive(S3Backup s3Backup) {
        this.s3Backup = s3Backup;
    }

    void zipFolder(File srcFolder, File dstZipFile) throws Exception {
        ZipOutputStream zip = new ZipOutputStream(new FileOutputStream(dstZipFile));
        addFolder(srcFolder, srcFolder, zip);
    }

    void removeFile(File srcFile) {
        if (!srcFile.delete()) {
            s3Backup.sendMessage(null, true, "Failed to delete temporary file " + srcFile);
        }
    }

    private void addFile(File rootPath, File srcFile, ZipOutputStream zip) throws Exception {
        if (srcFile.isDirectory()) {
            if (!srcFile.getCanonicalPath().endsWith(s3Backup.getFileConfig().getLocalPrefix())) {
                addFolder(rootPath, srcFile, zip);
            }
        } else {
            byte[] buf = new byte[1024];
            int len;

            FileInputStream in = new FileInputStream(srcFile);
            zip.putNextEntry(new ZipEntry(srcFile.getPath().replace(rootPath.getPath() + File.separator, "")));

            while ((len = in.read(buf)) > 0) {
                zip.write(buf, 0, len);
            }
        }
    }

    private void addFolder(File rootPath, File srcFolder, ZipOutputStream zip) throws Exception {
        for (File fileName : Objects.requireNonNull(srcFolder.listFiles())) {
            addFile(rootPath, fileName, zip);
        }
    }
}
