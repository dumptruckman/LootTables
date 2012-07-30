/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */
package loottables;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class FileUtil {

    /**
     * Saves the sourceStream to the destFile.
     */
    public static void streamToFile(InputStream sourceStream, File destFile) throws IOException {
        if (sourceStream == null) {
            throw new IllegalArgumentException("sourceStream may not be null!");
        }
        if (destFile == null) {
            throw new IllegalArgumentException("destFile may not be null!");
        }
        if(!destFile.exists()) {
            destFile.createNewFile();
        }
        OutputStream out = null;
        try {
            out = new FileOutputStream(destFile);
            int read = 0;
            byte[] bytes = new byte[1024];
            while ((read = sourceStream.read(bytes)) != -1) {
                out.write(bytes, 0, read);
            }
        } finally {
            try {
                sourceStream.close();
            } catch (IOException ignore) { }
            if (out != null) {
                try {
                    out.flush();
                } finally {
                    try {
                        out.close();
                    } catch (IOException ignore) { }
                }
            }
        }
    }
}
