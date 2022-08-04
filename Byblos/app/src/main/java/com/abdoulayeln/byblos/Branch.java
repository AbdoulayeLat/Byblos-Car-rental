package com.abdoulayeln.byblos;

import java.util.List;
import java.io.Serializable;

public class Branch implements Serializable{
    public String branchID, branchName, branchPhoneNumber, branchAddress, startTime, endTime;
    public List<String> servicesOfferedID;

    public Branch(){}

    public Branch(String branchID, String branchName, String branchPhoneNumber, String branchAddress, String branchStartTime, String branchEndTime, List<String> servicesOfferedID) {
        this.branchName = branchName;
        this.branchPhoneNumber = branchPhoneNumber;
        this.branchAddress = branchAddress;
        this.branchID = branchID;
        this.startTime = branchStartTime;
        this.endTime = branchEndTime;
        this.servicesOfferedID = servicesOfferedID;
    }

    public String getBranchID() {
        return branchID;
    }

    public void setBranchID(String branchID) {
        this.branchID = branchID;
    }

    public String getBranchName() {
        return branchName;
    }

    public void setBranchName(String branchName) {
        this.branchName = branchName;
    }

    public String getBranchPhoneNumber() {
        return branchPhoneNumber;
    }

    public void setBranchPhoneNumber(String branchPhoneNumber) {
        this.branchPhoneNumber = branchPhoneNumber;
    }

    public String getBranchAddress() {
        return branchAddress;
    }

    public void setBranchAddress(String branchAddress) {
        this.branchAddress = branchAddress;
    }

    public String getStartTime(){return this.startTime;};
    public void setStartTime(String startTime){this.startTime = startTime;};
    public String getEndTime(){return this.endTime;};
    public void setEndTime(String endTime){this.endTime = endTime;};

    public List<String> getServicesOffered(){return servicesOfferedID;}
    public void setServicesOffered(List<String> servicesOffered){this.servicesOfferedID = servicesOffered;}
}
