package cloud.stonehouse.s3backup;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Objects;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class Archive {

    private S3Backup s3Backup;
    private String localPrefix;

    public Archive(S3Backup s3Backup) {
        this.s3Backup = s3Backup;
        this.localPrefix = s3Backup.getFileConfig().getString("local-prefix");
    }

    public void zipFolder(File srcFolder, File destZipFile) throws Exception {
        try (FileOutputStream fileWriter = new FileOutputStream(destZipFile);
             ZipOutputStream zip = new ZipOutputStream(fileWriter)) {

            addFolder(srcFolder, srcFolder, zip);
        }
    }

    public void removeFile(File srcFile) {
        srcFile.delete();
    }

    private void addFile(File rootPath, File srcFile, ZipOutputStream zip) throws Exception {
        if (srcFile.isDirectory()) {
            if (!srcFile.getCanonicalPath().endsWith(localPrefix)) {
                addFolder(rootPath, srcFile, zip);
            }
        } else {
            byte[] buf = new byte[1024];
            int len;

            try (FileInputStream in = new FileInputStream(srcFile)) {
                zip.putNextEntry(new ZipEntry(srcFile.getPath().replace(rootPath.getPath() + File.separator, "")));

                while ((len = in.read(buf)) > 0) {
                    zip.write(buf, 0, len);
                }
            }
        }
    }

    private void addFolder(File rootPath, File srcFolder, ZipOutputStream zip) throws Exception {
        for (File fileName : Objects.requireNonNull(srcFolder.listFiles())) {
            addFile(rootPath, fileName, zip);
        }
    }
}
