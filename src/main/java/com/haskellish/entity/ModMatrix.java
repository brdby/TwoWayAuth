package com.haskellish.entity;

import java.math.*;

public class ModMatrix {
    private int nrows;
    private int ncols;
    private BigInteger[][] data;
    private BigInteger mod;

    public ModMatrix(BigInteger[][] data, BigInteger mod) {
        this.data = data;
        this.nrows = data.length;
        this.ncols = data[0].length;
        this.mod = mod;
    }

    public ModMatrix(int nrow, int ncol, BigInteger mod) {
        this.nrows = nrow;
        this.ncols = ncol;
        data = new BigInteger[nrow][ncol];
        this.mod = mod;
    }

    public int getNrows() {
        return nrows;
    }


    public int getNcols() {
        return ncols;
    }


    public BigInteger[][] getData() {
        return data;
    }

    public BigInteger getValueAt(int i, int j) {
        return data[i][j];
    }

    public void setValueAt(int i, int j, BigInteger value) {
        data[i][j] = value;
    }

    public int size() {
        return ncols;
    }

    // Take the transpose of the Matrix..
    public static ModMatrix transpose(ModMatrix matrix) {
        ModMatrix transposedMatrix = new ModMatrix(matrix.getNcols(), matrix.getNrows(), matrix.mod);
        for (int i = 0; i < matrix.getNrows(); i++) {
            for (int j = 0; j < matrix.getNcols(); j++) {
                transposedMatrix.setValueAt(j, i, matrix.getValueAt(i, j));
            }
        }
        return transposedMatrix;
    }


    public static BigInteger determinant(ModMatrix matrix) {

        if (matrix.size() == 1) {
            return matrix.getValueAt(0, 0);
        }
        if (matrix.size() == 2) {
            //return (matrix.getValueAt(0, 0) * matrix.getValueAt(1, 1)) - (matrix.getValueAt(0, 1) * matrix.getValueAt(1, 0));
            return (matrix.getValueAt(0, 0).multiply(matrix.getValueAt(1, 1))).subtract((matrix.getValueAt(0, 1).multiply(matrix.getValueAt(1, 0))));
        }
        BigInteger sum = new BigInteger("0");
        for (int i = 0; i < matrix.getNcols(); i++) {
            sum = sum.add(changeSign(i).multiply(matrix.getValueAt(0, i).multiply(determinant(createSubMatrix(matrix, 0, i)))));
        }
        return sum;
    }

    private static BigInteger changeSign(int i) {
        if (i % 2 == 0) {
            return new BigInteger("1");
        } else {
            return new BigInteger("-1");
        }
    }

    public static ModMatrix createSubMatrix(ModMatrix matrix, int excluding_row, int excluding_col) {
        ModMatrix mat = new ModMatrix(matrix.getNrows() - 1, matrix.getNcols() - 1, matrix.mod);
        int r = -1;
        for (int i = 0; i < matrix.getNrows(); i++) {
            if (i == excluding_row) {
                continue;
            }
            r++;
            int c = -1;
            for (int j = 0; j < matrix.getNcols(); j++) {
                if (j == excluding_col) {
                    continue;
                }
                mat.setValueAt(r, ++c, matrix.getValueAt(i, j));
            }
        }
        return mat;
    }

    public ModMatrix cofactor(ModMatrix matrix) {
        ModMatrix mat = new ModMatrix(matrix.getNrows(), matrix.getNcols(), mod);
        for (int i = 0; i < matrix.getNrows(); i++) {
            for (int j = 0; j < matrix.getNcols(); j++) {
                mat.setValueAt(i, j, (changeSign(i).multiply(changeSign(j)).multiply(determinant(createSubMatrix(matrix, i, j)))).mod(mod));
            }
        }

        return mat;
    }

    public ModMatrix inverse() {
        ModMatrix res = transpose(cofactor(this));
        res = res.dc(determinant(this));
        return (transpose(cofactor(this)).dc(determinant(this)));
    }

    public ModMatrix multiply(ModMatrix matrix){
        ModMatrix result = new ModMatrix(this.getNrows(), matrix.getNcols(), mod);
        for (int c = 0; c < this.getNrows(); c++) {
            for (int d = 0; d < matrix.getNcols(); d++) {
                BigInteger sum = new BigInteger("0");
                for (int k = 0; k < matrix.getNrows(); k++) {
                    sum = sum.add(this.getValueAt(c, k).multiply(matrix.getValueAt(k, d)));
                }
                result.setValueAt(c, d, sum.mod(mod));
            }
        }
        return result;
    }

    private ModMatrix dc(BigInteger d) {
        BigInteger inv = d.modInverse(mod);
        for (int i = 0; i < nrows; i++) {
            for (int j = 0; j < ncols; j++) {
                data[i][j] = (data[i][j].multiply(inv)).mod(mod);
            }
        }
        return this;
    }
}
