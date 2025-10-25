package fpt.edu.vn.pizzaapp_prm392.utils;

import java.util.Random;

/**
 * OTPHelper - Tạo và quản lý OTP (One-Time Password)
 */
public class OTPHelper {
    
    private static final int OTP_LENGTH = 6;
    private static final long OTP_VALIDITY_TIME = 10 * 60 * 1000; // 10 minutes in milliseconds
    
    /**
     * Tạo OTP ngẫu nhiên 6 chữ số
     */
    public static String generateOTP() {
        Random random = new Random();
        int otp = 100000 + random.nextInt(900000);
        return String.valueOf(otp);
    }
    
    /**
     * Kiểm tra OTP còn hợp lệ không
     */
    public static boolean isOTPValid(long otpGeneratedTime) {
        long currentTime = System.currentTimeMillis();
        return (currentTime - otpGeneratedTime) < OTP_VALIDITY_TIME;
    }
    
    /**
     * Lấy thời gian hết hạn OTP (phút:giây)
     */
    public static String getOTPExpirationTime(long otpGeneratedTime) {
        long currentTime = System.currentTimeMillis();
        long timePassed = currentTime - otpGeneratedTime;
        long timeRemaining = OTP_VALIDITY_TIME - timePassed;
        
        if (timeRemaining <= 0) {
            return "0:00";
        }
        
        long minutes = timeRemaining / (60 * 1000);
        long seconds = (timeRemaining % (60 * 1000)) / 1000;
        
        return String.format("%d:%02d", minutes, seconds);
    }
}
