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
public class Image3D {

    byte[] buffer;

    int[] n = new int[3];

    public Image3D(int nx, int ny, int nz) {
        this(nx, ny, nz, null);
    }

    public Image3D(int nx, int ny, int nz, byte[] buffer) {
        n[0] = nx;
        n[1] = ny;
        n[2] = nz;

        this.buffer = buffer;
    }

    public byte[] getBuffer() {
        return buffer;
    }

    public void setBuffer(byte[] buffer) {
        this.buffer = buffer;
    }

    public int size() {
        return n[0] * n[1] * n[2];
    }

    public void setValue(int i, int j, int k, byte value) {
        buffer[getIdx(i, j, k)] = value;
    }

    public byte getValue(int i, int j, int k) {
        return buffer[getIdx(i, j, k)];
    }

    int getIdx(int i, int j, int k) {
        return i + j * n[0] + k * n[0] * n[1];
    }

    Image3D extract(int ox, int oy, int oz, int nx, int ny, int nz) {
        Image3D subImg = new Image3D(nx, ny, nz);

        byte[] subBuffer = new byte[subImg.size()];
        for (int k = 0; k < nz; k++) {
            for (int j = 0; j < ny; j++) {
                for (int i = 0; i < nx; i++) {
                    subBuffer[ subImg.getIdx(i, j, k) ] = buffer[getIdx(i+ox, j+oy, k+oz)];
                }
            }
        }
        
        subImg.setBuffer(subBuffer);
        
        return subImg;
    }

}
