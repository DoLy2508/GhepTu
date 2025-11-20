package com.example.Model;

import java.io.Serializable;

public class TuVungGhepTu implements Serializable {
    int key;
    String maTu;
    String tiengAnh;
    String tiengViet;
    public TuVungGhepTu(){
    }

    public TuVungGhepTu(int key, String maTu, String tiengAnh, String tiengViet) {
        this.key = key;
        this.maTu = maTu;
        this.tiengAnh = tiengAnh;
        this.tiengViet = tiengViet;

    }

    public int getKey() {
        return key;
    }

    public void setKey(int key) {
        this.key = key;
    }

    public String getMaTu() {
        return maTu;
    }

    public void setMaTu(String maTu) {
        this.maTu = maTu;
    }

    public String getTiengAnh() {
        return tiengAnh;
    }

    public void setTiengAnh(String tiengAnh) {
        this.tiengAnh = tiengAnh;
    }

    public String getTiengViet() {
        return tiengViet;
    }

    public void setTiengViet(String tiengViet) {
        this.tiengViet = tiengViet;
    }
}
