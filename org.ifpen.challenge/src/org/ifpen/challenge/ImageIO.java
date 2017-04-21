/*
 * Copyright (C) 2014-2017 by IFPEN
 * All rights reserved.
 * 
 * IFPEN Headquarters:
 * 1 & 4, avenue de Bois-Preau
 * 92852 Rueil-Malmaison Cedex - France
 */
package org.ifpen.challenge;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 *
 * @author lecomtje
 */
public class ImageIO {
    
    String filename;
    
    Image3D image;
    
    public ImageIO( String filename ){   
        this.filename = filename;
    }
    
 
    /**
     * Read the given binary file, and return its contents as a byte array.
     */
    public boolean read( Image3D image ) {
        log("Reading in binary file named : " + filename);
        File file = new File(filename);
        log("File size: " + file.length());
        
        if ( image.size() != file.length() ){
            log("image has not the expected size !");
            return false;
        }
        byte[] buffer = new byte[(int) file.length()];
        try {
            InputStream input = null;
            try {
                int totalBytesRead = 0;
                input = new BufferedInputStream(new FileInputStream(file));
                while (totalBytesRead < buffer.length) {
                    int bytesRemaining = buffer.length - totalBytesRead;
                    //input.read() returns -1, 0, or more :
                    int bytesRead = input.read(buffer, totalBytesRead, bytesRemaining);
                    if (bytesRead > 0) {
                        totalBytesRead = totalBytesRead + bytesRead;
                    }
                }
                /*
         the above style is a bit tricky: it places bytes into the 'result' array; 
         'result' is an output parameter;
         the while loop usually has a single iteration only.
                 */
                log("Num bytes read: " + totalBytesRead);
            } finally {
                log("Closing input stream.");
                input.close();
            }
        } catch (FileNotFoundException ex) {
            log("File not found.");
            return false;
        } catch (IOException ex) {
            log(ex);
            return false;
        }
        
        image.setBuffer( buffer );
        
        return true;
    }

    /**
     * Write a byte array to the given file. Writing binary data is
     * significantly simpler than reading it.
     */
    void write( Image3D image ) {
        byte[] buffer = image.getBuffer();
        log("Writing binary file...");
        try {
            OutputStream output = null;
            try {
                output = new BufferedOutputStream(new FileOutputStream(filename));
                output.write(buffer);
            } finally {
                output.close();
            }
        } catch (FileNotFoundException ex) {
            log("File not found.");
        } catch (IOException ex) {
            log(ex);
        }
    }

    private static void log(Object aThing) {
        System.out.println(String.valueOf(aThing));
    }
}
