package fpt.edu.vn.pizzaapp_prm392.activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import fpt.edu.vn.pizzaapp_prm392.MainActivity;
import fpt.edu.vn.pizzaapp_prm392.R;
import fpt.edu.vn.pizzaapp_prm392.dao.UserDAO;
import fpt.edu.vn.pizzaapp_prm392.models.User;
import fpt.edu.vn.pizzaapp_prm392.utils.SessionManager;
import fpt.edu.vn.pizzaapp_prm392.utils.ValidationHelper;

/**
 * LoginActivity - Màn hình đăng nhập người dùng
 * Tập trung vào bảo mật client-side
 */
public class LoginActivity extends AppCompatActivity {
    
    private EditText emailInput;
    private EditText passwordInput;
    private ImageButton togglePasswordBtn;
    private Button loginBtn;
    private TextView registerLink;
    private TextView forgotPasswordLink;
    
    private UserDAO userDAO;
    private SessionManager sessionManager;
    private boolean isPasswordVisible = false;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        
        // Initialize
        initializeViews();
        setupClickListeners();
        
        // Database & Session
        userDAO = new UserDAO(this);
        sessionManager = new SessionManager(this);
        
//        // Check if already logged in
//        if (sessionManager.isLoggedIn() && sessionManager.isTokenValid()) {
//            navigateToMain();
//        }
    }
    
    /**
     * Initialize UI views
     */
    private void initializeViews() {
        emailInput = findViewById(R.id.email_input);
        passwordInput = findViewById(R.id.password_input);
        togglePasswordBtn = findViewById(R.id.toggle_password_btn);
        loginBtn = findViewById(R.id.login_btn);
        registerLink = findViewById(R.id.register_link);
        forgotPasswordLink = findViewById(R.id.forgot_password_link);
    }
    
    /**
     * Setup click listeners
     */
    private void setupClickListeners() {
        // Login button
        loginBtn.setOnClickListener(v -> performLogin());
        
        // Toggle password visibility
        togglePasswordBtn.setOnClickListener(v -> togglePasswordVisibility());
        
        // Navigate to register
        registerLink.setOnClickListener(v -> navigateToRegister());
        
        // Forgot password (future feature)
        forgotPasswordLink.setOnClickListener(v -> showForgotPasswordMessage());
    }
    
    /**
     * Perform login logic
     */
    private void performLogin() {
        String email = emailInput.getText().toString().trim();
        String password = passwordInput.getText().toString();
        
        // Validate input
        String validationError = ValidationHelper.validateLogin(email, password);
        if (!validationError.isEmpty()) {
            showError(validationError);
            return;
        }
        
        // Disable button to prevent multiple clicks
        loginBtn.setEnabled(false);
        
        // Check user in database
        User user = userDAO.loginUser(email, password);
        
        if (user != null) {
            // Login successful - create session
            String token = generateToken(); // Generate a simple token
            
            sessionManager.createSession(
                    user.getUserId(),
                    user.getName(),
                    user.getEmail(),
                    user.getPhone(),
                    user.getAddress(),
                    token
            );
            
            // Update token in database
            userDAO.updateUserToken(user.getUserId(), token);
            
            showSuccess("Đăng nhập thành công!");
            
            // Navigate to main activity
            navigateToMain();
        } else {
            // Login failed
            showError("Email hoặc mật khẩu không chính xác");
            loginBtn.setEnabled(true);
        }
    }
    
    /**
     * Toggle password visibility
     */
    private void togglePasswordVisibility() {
        if (isPasswordVisible) {
            // Hide password
            passwordInput.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
            togglePasswordBtn.setImageResource(android.R.drawable.ic_menu_view); // Hide icon
        } else {
            // Show password
            passwordInput.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
            togglePasswordBtn.setImageResource(android.R.drawable.ic_menu_close_clear_cancel); // Show icon
        }
        
        isPasswordVisible = !isPasswordVisible;
        
        // Move cursor to end
        passwordInput.setSelection(passwordInput.getText().length());
    }
    
    /**
     * Navigate to register activity
     */
    private void navigateToRegister() {
        Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
        startActivity(intent);
    }
    
    /**
     * Navigate to main activity
     */
    private void navigateToMain() {
        Intent intent = new Intent(LoginActivity.this, ProductListActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
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
    
    /**
     * Navigate to forgot password activity
     */
    private void showForgotPasswordMessage() {
        Intent intent = new Intent(LoginActivity.this, ForgotPasswordActivity.class);
        startActivity(intent);
    }
    
    /**
     * Generate a simple token (in production, this should come from backend)
     */
    private String generateToken() {
        return "TOKEN_" + System.currentTimeMillis();
    }
    
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (userDAO != null) {
            userDAO.close();
        }
    }
}
