package fpt.edu.vn.pizzaapp_prm392.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import fpt.edu.vn.pizzaapp_prm392.R;
import fpt.edu.vn.pizzaapp_prm392.dao.UserDAO;
import fpt.edu.vn.pizzaapp_prm392.models.User;
import fpt.edu.vn.pizzaapp_prm392.utils.SessionManager;
import fpt.edu.vn.pizzaapp_prm392.utils.ValidationHelper;

/**
 * ProfileActivity - Màn hình xem và cập nhật thông tin cá nhân
 */
public class ProfileActivity extends AppCompatActivity {
    
    private ImageButton backBtn;
    private TextView emailDisplay;
    private EditText nameInput;
    private EditText phoneInput;
    private EditText addressInput;
    private Button updateBtn;
    private Button logoutBtn;
    
    private UserDAO userDAO;
    private SessionManager sessionManager;
    private int currentUserId;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        
        // Initialize
        initializeViews();
        setupClickListeners();
        
        // Database & Session
        userDAO = new UserDAO(this);
        sessionManager = new SessionManager(this);
        
        // Get current user ID
        currentUserId = sessionManager.getUserId();
        
        // Load user information
        loadUserInfo();
    }
    
    /**
     * Initialize UI views
     */
    private void initializeViews() {
        backBtn = findViewById(R.id.back_btn);
        emailDisplay = findViewById(R.id.email_display);
        nameInput = findViewById(R.id.name_input);
        phoneInput = findViewById(R.id.phone_input);
        addressInput = findViewById(R.id.address_input);
        updateBtn = findViewById(R.id.update_btn);
        logoutBtn = findViewById(R.id.logout_btn);
    }
    
    /**
     * Setup click listeners
     */
    private void setupClickListeners() {
        backBtn.setOnClickListener(v -> finish());
        updateBtn.setOnClickListener(v -> updateProfile());
        logoutBtn.setOnClickListener(v -> performLogout());
    }
    
    /**
     * Load user information from session
     */
    private void loadUserInfo() {
        // Get user data from session
        String name = sessionManager.getUserName();
        String email = sessionManager.getUserEmail();
        String phone = sessionManager.getUserPhone();
        String address = sessionManager.getUserAddress();
        
        // Display email (read-only)
        emailDisplay.setText("Email: " + email);
        
        // Display editable fields
        nameInput.setText(name);
        phoneInput.setText(phone != null ? phone : "");
        addressInput.setText(address != null ? address : "");
    }
    
    /**
     * Update user profile information
     */
    private void updateProfile() {
        String name = nameInput.getText().toString().trim();
        String phone = phoneInput.getText().toString().trim();
        String address = addressInput.getText().toString().trim();
        
        // Validate inputs
        if (!ValidationHelper.isValidName(name)) {
            showError("Tên phải có từ 2-50 ký tự");
            return;
        }
        
        if (!phone.isEmpty() && !ValidationHelper.isValidPhone(phone)) {
            showError("Số điện thoại không hợp lệ");
            return;
        }
        
        if (!address.isEmpty() && !ValidationHelper.isValidAddress(address)) {
            showError("Địa chỉ phải có từ 5-200 ký tự");
            return;
        }
        
        // Disable button to prevent multiple clicks
        updateBtn.setEnabled(false);
        
        // Get current user from database
        User user = userDAO.getUserById(currentUserId);
        
        if (user != null) {
            // Update user information
            user.setName(name);
            user.setPhone(phone.isEmpty() ? null : phone);
            user.setAddress(address.isEmpty() ? null : address);
            
            int result = userDAO.updateUser(user);
            
            if (result > 0) {
                // Update successful - update session
                sessionManager.updateUserInfo(name, phone.isEmpty() ? "" : phone, address.isEmpty() ? "" : address);
                
                showSuccess("Cập nhật thông tin thành công!");
            } else {
                showError("Cập nhật thông tin thất bại");
            }
        } else {
            showError("Không tìm thấy thông tin người dùng");
        }
        
        updateBtn.setEnabled(true);
    }
    
    /**
     * Perform logout
     */
    private void performLogout() {
        // Clear session
        sessionManager.logout();
        
        // Navigate to login
        Intent intent = new Intent(ProfileActivity.this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
        
        showSuccess("Đã đăng xuất");
    }
    
    /**
     * Show error message
     */
    private void showError(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
    
    /**
     * Show success message
     */
    private void showSuccess(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
    
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (userDAO != null) {
            userDAO.close();
        }
    }
}
