package com.ats.assetservice.entity;


public enum AssetStatus {
	AVAILABLE,
    ASSIGNED,
    UNDER_MAINTENANCE,
    RETIRED;

//    public static AssetStatus from(String s) {
//        if (s == null) return null;
//        return AssetStatus.valueOf(s.trim().toUpperCase().replace("-", "_"));
//    }
}
