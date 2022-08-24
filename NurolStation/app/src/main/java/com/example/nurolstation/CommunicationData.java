package com.example.nurolstation;

public class CommunicationData {

    private String stationName;
    private Boolean requestStatus;
    private Boolean jobStatus;

    public Boolean getJobStatus(){
        return jobStatus;
    }

    public void setJobStatus(Boolean jobStatus){
        this.jobStatus=jobStatus;
    }

    public String getStationName(){
        return stationName;
    }

    public void setStationName(String stationName){
        this.stationName=stationName;
    }

    public Boolean getRequestStatus(){
        return requestStatus;
    }
    public void setRequestStatus(Boolean requestStatus){
        this.requestStatus=requestStatus;
    }

}
