package com.shivam.pt.barcodescanner.adapter;

/**
 * Created by Shivam on 7/26/2018.
 */

public class StoreBarDetails {

    String pName,pType,barNum,manDate,expDate,desc;
    int sNo,aNo,pid,price;
    public StoreBarDetails(String pn, String pt, int pr, String bn, String md, String ed,int sNo,int aNo,int pid, String d)
    {
        this.pName=pn;
        this.pType=pt;
        this.price=pr;
        this.barNum=bn;
        this.manDate=md;
        this.expDate=ed;
        this.desc=d;
    }

    public String getpName() {
        return pName;
    }

    public String getpType() {
        return pType;
    }

    public int getPrice() {
        return price;
    }

    public String getBarNum() {
        return barNum;
    }

    public String getManDate() {
        return manDate;
    }

    public String getExpDate() {
        return expDate;
    }

    public String getDesc() {
        return desc;
    }

    public int getsNo() {
        return sNo;
    }

    public int getaNo() {
        return aNo;
    }

    public int getPid() {
        return pid;
    }
}
