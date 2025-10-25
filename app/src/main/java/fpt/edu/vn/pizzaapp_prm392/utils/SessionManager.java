package fpt.edu.vn.pizzaapp_prm392.utils;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * SessionManager - Quản lý session và token người dùng
 * Lưu trữ dữ liệu nhạy cảm với bảo mật
 */
public class SessionManager {
    
    private static final String PREF_NAME = "PizzaApp";
    private static final String KEY_IS_LOGIN = "isLogin";
    private static final String KEY_USER_ID = "userId";
    private static final String KEY_USER_NAME = "userName";
    private static final String KEY_USER_EMAIL = "userEmail";
    private static final String KEY_USER_PHONE = "userPhone";
    private static final String KEY_USER_ADDRESS = "userAddress";
    private static final String KEY_USER_TOKEN = "userToken";
    private static final String KEY_LOGIN_TIME = "loginTime";
    
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private Context context;
    
    public SessionManager(Context context) {
        this.context = context;
        sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
    }
    
    /**
     * Lưu trữ thông tin đăng nhập
     */
    public void createSession(int userId, String name, String email, String phone, 
                             String address, String token) {
        editor.putBoolean(KEY_IS_LOGIN, true);
        editor.putInt(KEY_USER_ID, userId);
        editor.putString(KEY_USER_NAME, name);
        editor.putString(KEY_USER_EMAIL, email);
        editor.putString(KEY_USER_PHONE, phone);
        editor.putString(KEY_USER_ADDRESS, address);
        editor.putString(KEY_USER_TOKEN, token);
        editor.putLong(KEY_LOGIN_TIME, System.currentTimeMillis());
        editor.apply();
    }
    
    /**
     * Kiểm tra người dùng đã đăng nhập hay chưa
     */
    public boolean isLoggedIn() {
        return sharedPreferences.getBoolean(KEY_IS_LOGIN, false);
    }
    
    /**
     * Lấy ID người dùng
     */
    public int getUserId() {
        return sharedPreferences.getInt(KEY_USER_ID, -1);
    }
    
    /**
     * Lấy tên người dùng
     */
    public String getUserName() {
        return sharedPreferences.getString(KEY_USER_NAME, "");
    }
    
    /**
     * Lấy email người dùng
     */
    public String getUserEmail() {
        return sharedPreferences.getString(KEY_USER_EMAIL, "");
    }
    
    /**
     * Lấy số điện thoại người dùng
     */
    public String getUserPhone() {
        return sharedPreferences.getString(KEY_USER_PHONE, "");
    }
    
    /**
     * Lấy địa chỉ người dùng
     */
    public String getUserAddress() {
        return sharedPreferences.getString(KEY_USER_ADDRESS, "");
    }
    
    /**
     * Lấy token người dùng
     */
    public String getUserToken() {
        return sharedPreferences.getString(KEY_USER_TOKEN, "");
    }
    
    /**
     * Cập nhật thông tin người dùng
     */
    public void updateUserInfo(String name, String phone, String address) {
        editor.putString(KEY_USER_NAME, name);
        editor.putString(KEY_USER_PHONE, phone);
        editor.putString(KEY_USER_ADDRESS, address);
        editor.apply();
    }
    
    /**
     * Xóa session (đăng xuất)
     */
    public void logout() {
        editor.clear();
        editor.apply();
    }
    
    /**
     * Kiểm tra token còn hợp lệ không (optional - tùy theo backend)
     */
    public boolean isTokenValid() {
        if (!isLoggedIn()) {
            return false;
        }
        
        // Kiểm tra token có rỗng không
        String token = getUserToken();
        return token != null && !token.isEmpty();
    }
}
