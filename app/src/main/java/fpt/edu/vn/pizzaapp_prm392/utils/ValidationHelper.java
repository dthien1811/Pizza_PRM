package fpt.edu.vn.pizzaapp_prm392.utils;

import java.util.regex.Pattern;

/**
 * ValidationHelper - Hỗ trợ validation dữ liệu người dùng
 * Tập trung vào bảo mật client-side
 */
public class ValidationHelper {
    
    // Regex patterns
    private static final String EMAIL_PATTERN = 
            "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$";
    
    private static final String PHONE_PATTERN = 
            "^[0-9]{10,11}$"; // Vietnamese phone number
    
    private static final String PASSWORD_PATTERN = 
            "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).{8,}$"; // Min 8 chars, uppercase, lowercase, digit
    
    private static final String NAME_PATTERN = 
            "^[a-zA-Zñáàâäãåçéèêëíìîïóòôöõúùûüýÿ\\s]{2,50}$"; // 2-50 chars, letters only
    
    /**
     * Validate email format
     */
    public static boolean isValidEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            return false;
        }
        return Pattern.matches(EMAIL_PATTERN, email.trim());
    }
    
    /**
     * Validate password strength
     * - Minimum 8 characters
     * - At least one uppercase letter
     * - At least one lowercase letter
     * - At least one digit
     */
    public static boolean isValidPassword(String password) {
        if (password == null || password.length() < 8) {
            return false;
        }
        return Pattern.matches(PASSWORD_PATTERN, password);
    }
    
    /**
     * Get password strength message
     */
    public static String getPasswordStrengthMessage(String password) {
        if (password == null || password.isEmpty()) {
            return "Mật khẩu không được để trống";
        }
        
        if (password.length() < 8) {
            return "Mật khẩu phải có ít nhất 8 ký tự";
        }
        
        boolean hasUpper = Pattern.compile("[A-Z]").matcher(password).find();
        boolean hasLower = Pattern.compile("[a-z]").matcher(password).find();
        boolean hasDigit = Pattern.compile("[0-9]").matcher(password).find();
        
        if (!hasUpper) {
            return "Mật khẩu phải chứa ít nhất 1 ký tự hoa";
        }
        
        if (!hasLower) {
            return "Mật khẩu phải chứa ít nhất 1 ký tự thường";
        }
        
        if (!hasDigit) {
            return "Mật khẩu phải chứa ít nhất 1 chữ số";
        }
        
        return "Mật khẩu mạnh";
    }
    
    /**
     * Validate phone number (Vietnamese format)
     */
    public static boolean isValidPhone(String phone) {
        if (phone == null || phone.trim().isEmpty()) {
            return false;
        }
        return Pattern.matches(PHONE_PATTERN, phone.trim().replaceAll("-", ""));
    }
    
    /**
     * Validate name
     */
    public static boolean isValidName(String name) {
        if (name == null || name.trim().isEmpty()) {
            return false;
        }
        return name.trim().length() >= 2 && name.trim().length() <= 50;
    }
    
    /**
     * Validate address
     */
    public static boolean isValidAddress(String address) {
        if (address == null || address.trim().isEmpty()) {
            return false;
        }
        return address.trim().length() >= 5 && address.trim().length() <= 200;
    }
    
    /**
     * Check if passwords match
     */
    public static boolean doPasswordsMatch(String password, String confirmPassword) {
        if (password == null || confirmPassword == null) {
            return false;
        }
        return password.equals(confirmPassword);
    }
    
    /**
     * Sanitize input to prevent SQL injection
     */
    public static String sanitizeInput(String input) {
        if (input == null) {
            return "";
        }
        return input.trim()
                .replace("'", "''") // Escape single quotes
                .replace("\"", "\\\""); // Escape double quotes
    }
    
    /**
     * Check if string is not empty or null
     */
    public static boolean isNotEmpty(String str) {
        return str != null && !str.trim().isEmpty();
    }
    
    /**
     * Validate all registration fields
     */
    public static String validateRegistration(String name, String email, 
                                              String password, String confirmPassword, 
                                              String phone) {
        if (!isNotEmpty(name)) {
            return "Vui lòng nhập tên";
        }
        
        if (!isValidName(name)) {
            return "Tên phải có từ 2-50 ký tự";
        }
        
        if (!isNotEmpty(email)) {
            return "Vui lòng nhập email";
        }
        
        if (!isValidEmail(email)) {
            return "Email không hợp lệ";
        }
        
        if (!isNotEmpty(password)) {
            return "Vui lòng nhập mật khẩu";
        }
        
        if (!isValidPassword(password)) {
            return getPasswordStrengthMessage(password);
        }
        
        if (!isNotEmpty(confirmPassword)) {
            return "Vui lòng xác nhận mật khẩu";
        }
        
        if (!doPasswordsMatch(password, confirmPassword)) {
            return "Mật khẩu không khớp";
        }
        
        if (isNotEmpty(phone) && !isValidPhone(phone)) {
            return "Số điện thoại không hợp lệ";
        }
        
        return ""; // Validation passed
    }
    
    /**
     * Validate login fields
     */
    public static String validateLogin(String email, String password) {
        if (!isNotEmpty(email)) {
            return "Vui lòng nhập email";
        }
        
        if (!isNotEmpty(password)) {
            return "Vui lòng nhập mật khẩu";
        }
        
        if (!isValidEmail(email)) {
            return "Email không hợp lệ";
        }
        
        return ""; // Validation passed
    }
}
