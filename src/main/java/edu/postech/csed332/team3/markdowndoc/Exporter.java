package edu.postech.csed332.team3.markdowndoc;

import org.jetbrains.annotations.Nullable;

import java.io.*;
import java.util.Arrays;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import static edu.postech.csed332.team3.markdowndoc.explorer.MdDocElementVisitor.MARKDOWNDOC;

public class Exporter {
    private Exporter() {
    }

    /**
     * Export html directories to zip file.
     *
     * @param name name of zip file.
     * @param directoryPath the path to the directory
     * @throws FileNotFoundException when there is no html directory,
     *                               which means never run.
     */
    public static void export(String name, @Nullable String directoryPath) throws FileNotFoundException {
        String actualPath = directoryPath == null ? "" : directoryPath + File.separator;
        File file = new File( actualPath + MARKDOWNDOC);
        if (!file.exists())
            throw new FileNotFoundException("There is no html directory.");

        try {
            FileOutputStream fileOut = new FileOutputStream(actualPath + name + ".zip");
            ZipOutputStream zipOut = new ZipOutputStream(fileOut);

            zipFile(file, file.getName(), zipOut);

            zipOut.close();
            fileOut.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void zipFile(File fileToZip, String path, ZipOutputStream zipOut) throws IOException {
        if (fileToZip.isHidden()) return;

        if (fileToZip.isDirectory()) {
            zipOut.putNextEntry(new ZipEntry(path + "/"));
            zipOut.closeEntry();

            File[] children = fileToZip.listFiles();
            Arrays.stream(children).forEach(childFile -> {
                try {
                    zipFile(childFile, path + "/" + childFile.getName(), zipOut);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
            return;
        }

        try (FileInputStream fileIn = new FileInputStream(fileToZip)) {
            ZipEntry zipEntry = new ZipEntry(path);
            zipOut.putNextEntry(zipEntry);
            byte[] bytes = new byte[1024];
            int length;
            while ((length = fileIn.read(bytes)) >= 0) {
                zipOut.write(bytes, 0, length);
            }
        }
    }
}
