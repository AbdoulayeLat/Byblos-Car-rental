package com.abdoulayeln.byblos;

import java.io.Serializable;
import java.util.HashMap;

public class Request implements Serializable {
    private String id, serviceId, branchId, status, userID;
    private HashMap<String, String> requirementsFulfilled;


    public Request(){
        id = "";
        serviceId ="";
        branchId ="";
        status = "pending";
        requirementsFulfilled = new HashMap<>();
    }

    public Request(String id, String serviceId, String branchId, String userID) {
        this.id = id;
        this.serviceId = serviceId;
        this.branchId = branchId;
        status = "pending";
    }

    public String getRefId(){return id;}
    public String getServiceId(){return serviceId;}
    public String getBranchId(){return branchId;}
    public String getStatus(){return status;}
    public String getUserID(){return userID;}
    public HashMap<String,String> getRequirementsFulfilled(){return requirementsFulfilled;}

    public void setRefId(String input){this.id = input;}
    public void setServiceId(String input){this.serviceId = input;}
    public void setBranchId(String input){this.branchId = input;}
    public void setStatus(String input){
        if(input.equals("pending") || input.equals("approved") || input.equals("denied")){
            status = input;
        }
    }
    public void setUserID(String input){this.userID = input;}
    public void setUserRequirementData(HashMap<String,String> data){requirementsFulfilled = data;}

    public void addRequirementData(String requirement, String data){requirementsFulfilled.put(requirement, data);}
    public String getRequirementData(String requirement){return requirementsFulfilled.get(requirement);}
}
