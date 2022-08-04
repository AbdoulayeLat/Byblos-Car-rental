package com.abdoulayeln.byblos;

import java.util.ArrayList;
import java.util.List;

public class Service {
    private String name;
    private double fee;
    private String id;
    private String requirement;
    private ArrayList<String> requirementList;

    public Service(){}

    public Service(String id, String name, double fee, String requirement) {
        this.name = name;
        this.fee = fee;
        this.id = id;
        this.requirement = requirement;

        requirementList = new ArrayList<>(List.of(requirement.split("; |;")));

    }

    public String getName() {
        return name;
    }

    public String getId(){return id;}

    public void setName(String name) {
        this.name = name;
    }

    public double getFee() {
        return fee;
    }
    public String getRequirement(){return requirement;}

    public ArrayList<String> getRequirementList(){return requirementList;}

    public void setFee(double fee) {
        this.fee = fee;
    }
}
