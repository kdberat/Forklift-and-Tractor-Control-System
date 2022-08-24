package com.example.nurolfmt;

public class TypeClass {
    public static int FORKLIFT_TYPE = 0;
    public static int TOW_TRUCK_TYPE = 1;

    public static String getCollectionName(int type){
        if (type == 0){
            return "ForkliftRequest";
        } else {
            return "TowTruckRequest";
        }
    }

    public static String getCName(int type){
        if (type == 0){
            return "Forklifts";
        } else {
            return "TowTruckRequest";
        }
    }

}