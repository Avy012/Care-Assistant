package com.example.lighthouseofmemory;

import android.util.Log;

public class LocationUtils {
    private static final double    Earth_Radius = 6371;

    // 반경 내 있는지 확인
    // 정확도 너무 떨어짐 (정확도 올려야 함)
    public static boolean isWithinRadius (double pat_lat, double pat_long, double care_lat, double care_long, double radius) {
        double distance = newCalculateDistance(pat_lat, pat_long, care_lat, care_long);

//        return distance <= radius;
        if (distance <= radius)
            return true;
        else
            return false;
    }

    // 두 지점 간 거리 계산 (위, 경도 입력 받아 km 단위 변환)
    public static double calculateBetweenDistance(double pat_lat, double pat_long, double care_lat, double care_long) {
        double dLat = Math.toRadians(care_lat - pat_lat);
        double dLong = Math.toRadians(care_long - pat_long);

        double a = Math.sin(dLat / 2) * Math.sin(dLong / 2)
                + Math.cos(Math.toRadians(pat_lat)) * Math.cos(Math.toRadians(care_lat))
                * Math.sin(dLong / 2) * Math.sin(dLong / 2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        return Earth_Radius * c;
    }

    public static double calculateDistance(double lat1, double long1, double lat2, double long2) {
        double dLat = Math.toRadians(lat2 - lat1);
        double dLong = Math.toRadians(long2 - long1);

        double a = Math.sin(dLat / 2) * Math.sin(dLong / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(dLong / 2) * Math.sin(dLong / 2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        return Earth_Radius * c;
    }

    public static double newCalculateDistance(double pat_lat, double pat_long, double curr_lat, double curr_long) {
        // WGS-84 기준 타원체 상수
        double a = 6378137.0; // 장축 반지름 (meters)
        double f = 1 / 298.257223563; // 편평률
        double b = 6356752.314245; // 단축 반지름 (meters)

        double phi1 = Math.toRadians(pat_lat);
        double phi2 = Math.toRadians(curr_lat);
        double L = Math.toRadians(curr_long - pat_long);

        double U1 = Math.atan((1 - f) * Math.tan(phi1));
        double U2 = Math.atan((1 - f) * Math.tan(phi2));

        double sinU1 = Math.sin(U1), cosU1 = Math.cos(U1);
        double sinU2 = Math.sin(U2), cosU2 = Math.cos(U2);

        double lambda = L, lambdaPrev;
        int iterLimit = 100;
        double sinLambda, cosLambda, sinSigma, cosSigma, sigma, sinAlpha, cos2Alpha, C;

        do {
            sinLambda = Math.sin(lambda);
            cosLambda = Math.cos(lambda);
            sinSigma = Math.sqrt(
                    (cosU2 * sinLambda) * (cosU2 * sinLambda) +
                            (cosU1 * sinU2 - sinU1 * cosU2 * cosLambda) * (cosU1 * sinU2 - sinU1 * cosU2 * cosLambda)
            );
            cosSigma = sinU1 * sinU2 + cosU1 * cosU2 * cosLambda;
            sigma = Math.atan2(sinSigma, cosSigma);
            sinAlpha = cosU1 * cosU2 * sinLambda / sinSigma;
            cos2Alpha = 1 - sinAlpha * sinAlpha;
            C = f / 16 * cos2Alpha * (4 + f * (4 - 3 * cos2Alpha));
            lambdaPrev = lambda;
            lambda = L + (1 - C) * f * sinAlpha * (
                    sigma + C * sinSigma * (
                            cos2Alpha + C * cosSigma * (-1 + 2 * cos2Alpha * cos2Alpha)
                    )
            );
        } while (Math.abs(lambda - lambdaPrev) > 1e-12 && --iterLimit > 0);

        if (iterLimit == 0) {
            throw new ArithmeticException("Vincenty formula failed to converge");
        }

        double u2 = cos2Alpha * (a * a - b * b) / (b * b);
        double A = 1 + u2 / 16384 * (4096 + u2 * (-768 + u2 * (320 - 175 * u2)));
        double B = u2 / 1024 * (256 + u2 * (-128 + u2 * (74 - 47 * u2)));
        double deltaSigma = B * sinSigma * (
                cosSigma + B / 4 * (
                        cos2Alpha * (-1 + 2 * cosSigma * cosSigma) -
                                B / 6 * cosSigma * (-3 + 4 * sinSigma * sinSigma) * (-3 + 4 * cos2Alpha * cos2Alpha)
                )
        );

        double distance = b * A * (sigma - deltaSigma);

        System.out.println(distance);

        return distance; // 두 점 사이의 거리 (meters)
    }
}
