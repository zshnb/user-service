package com.zshnb.userservice.util;

import java.util.regex.Pattern;
import org.springframework.stereotype.Component;

@Component
public class GeoUtil {
    public boolean checkCoordinate(String longitude, String latitude) {
        String longitudePattern = "^[\\-\\+]?(0?\\d{1,2}\\.\\d{1,8}|1[0-7]?\\d{1}\\.\\d{1,8}|180\\.0{1,8})$";
        String latitudePattern = "^[\\-\\+]?([0-8]?\\d{1}\\.\\d{1,8}|90\\.0{1,8})$";
        boolean longitudeMatch = Pattern.matches(longitudePattern, longitude);
        boolean latitudeMatch = Pattern.matches(latitudePattern, latitude);
        return longitudeMatch && latitudeMatch;
    }
}
