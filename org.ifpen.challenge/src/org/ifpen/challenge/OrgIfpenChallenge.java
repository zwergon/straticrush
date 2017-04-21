/*
 * Copyright (C) 2014-2017 by IFPEN
 * All rights reserved.
 * 
 * IFPEN Headquarters:
 * 1 & 4, avenue de Bois-Preau
 * 92852 Rueil-Malmaison Cedex - France
 */
package org.ifpen.challenge;

/**
 *
 * @author lecomtje
 */
public class OrgIfpenChallenge {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {

        Image3D imgMatrix = new Image3D(950, 950, 700);

        ImageIO reader = new ImageIO("/home/irsrvhome1/R11/lecomtje/work/BigDataWS/Benth_1_Scan_1_Calc_bin.raw");
        reader.read(imgMatrix);

        Image3D imgOil = new Image3D(950, 950, 700);
        reader = new ImageIO("/home/irsrvhome1/R11/lecomtje/work/BigDataWS/Benth_1_Scan_1_Sor_N_M_Oil_3Dmedian.raw");
        reader.read(imgOil);

        Image3D imgDest = new Image3D(950, 950, 700);

        {
            byte[] bufMatrix = imgMatrix.getBuffer();
            byte[] bufOil = imgOil.getBuffer();
            byte[] buffer = new byte[imgDest.size()];
            for (int i = 0; i < buffer.length; i++) {
                buffer[i] = (byte) (2 * (int) bufOil[i] - (int) bufMatrix[i] + 1);
            }
            imgDest.setBuffer(buffer);
        }
        imgMatrix = null;
        imgOil = null;
        
        Image3D subDest = imgDest.extract(150, 150, 150, 200, 200, 200);

        ImageIO writer = new ImageIO("/home/irsrvhome1/R11/lecomtje/work/BigDataWS/Benth_1_Scan_1_Sum.raw");
        writer.write(subDest);

    }

}
