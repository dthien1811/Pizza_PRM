package fpt.edu.vn.pizzaapp_prm392.activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import fpt.edu.vn.pizzaapp_prm392.R;
import fpt.edu.vn.pizzaapp_prm392.dao.UserDAO;
import fpt.edu.vn.pizzaapp_prm392.models.User;
import fpt.edu.vn.pizzaapp_prm392.utils.ValidationHelper;

/**
 * RegisterActivity - Màn hình đăng ký người dùng mới
 * Tập trung vào validation và bảo mật client-side
 */
public class RegisterActivity extends AppCompatActivity {
    
    private EditText nameInput;
    private EditText emailInput;
    private EditText phoneInput;
    private EditText passwordInput;
    private EditText confirmPasswordInput;
    private ImageButton togglePasswordBtn;
    private ImageButton toggleConfirmPasswordBtn;
    private Button registerBtn;
    private TextView loginLink;
    private ProgressBar passwordStrengthBar;
    private TextView passwordStrengthText;
    
    private UserDAO userDAO;
    private boolean isPasswordVisible = false;
    private boolean isConfirmPasswordVisible = false;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        
        // Initialize
        initializeViews();
        setupClickListeners();
        setupPasswordStrengthListener();
        
        // Database
        userDAO = new UserDAO(this);
    }
    
    /**
     * Initialize UI views
     */
    private void initializeViews() {
        nameInput = findViewById(R.id.name_input);
        emailInput = findViewById(R.id.email_input);
        phoneInput = findViewById(R.id.phone_input);
        passwordInput = findViewById(R.id.password_input);
        confirmPasswordInput = findViewById(R.id.confirm_password_input);
        togglePasswordBtn = findViewById(R.id.toggle_password_btn);
        toggleConfirmPasswordBtn = findViewById(R.id.toggle_confirm_password_btn);
        registerBtn = findViewById(R.id.register_btn);
        loginLink = findViewById(R.id.login_link);
        passwordStrengthBar = findViewById(R.id.password_strength_bar);
        passwordStrengthText = findViewById(R.id.password_strength_text);
    }
    
    /**
     * Setup click listeners
     */
    private void setupClickListeners() {
        // Register button
        registerBtn.setOnClickListener(v -> performRegistration());
        
        // Toggle password visibility
        togglePasswordBtn.setOnClickListener(v -> togglePasswordVisibility());
        
        // Toggle confirm password visibility
        toggleConfirmPasswordBtn.setOnClickListener(v -> toggleConfirmPasswordVisibility());
        
        // Navigate back to login
        loginLink.setOnClickListener(v -> finish());
    }
    
    /**
     * Setup password strength listener
     */
    private void setupPasswordStrengthListener() {
        passwordInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String password = s.toString();
                updatePasswordStrength(password);
            }
            
            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }
    
    /**
     * Update password strength indicator
     */
    private void updatePasswordStrength(String password) {
        if (password.isEmpty()) {
            passwordStrengthBar.setProgress(0);
            passwordStrengthText.setText("");
            resetPasswordRequirements();
            return;
        }
        
        int strength = calculatePasswordStrength(password);
        passwordStrengthBar.setProgress(strength);
        
        if (strength < 25) {
            passwordStrengthText.setText("Rất yếu");
            passwordStrengthText.setTextColor(getResources().getColor(android.R.color.holo_red_dark));
        } else if (strength < 50) {
            passwordStrengthText.setText("Yếu");
            passwordStrengthText.setTextColor(getResources().getColor(android.R.color.holo_orange_dark));
        } else if (strength < 75) {
            passwordStrengthText.setText("Trung bình");
            passwordStrengthText.setTextColor(getResources().getColor(android.R.color.holo_blue_dark));
        } else {
            passwordStrengthText.setText("Mạnh");
            passwordStrengthText.setTextColor(getResources().getColor(android.R.color.holo_green_dark));
        }
        
        // Update password requirements display
        updatePasswordRequirements(password);
    }
    
    /**
     * Update password requirements UI
     */
    private void updatePasswordRequirements(String password) {
        TextView reqLength = findViewById(R.id.req_length);
        TextView reqUppercase = findViewById(R.id.req_uppercase);
        TextView reqLowercase = findViewById(R.id.req_lowercase);
        TextView reqDigit = findViewById(R.id.req_digit);
        
        // Check length requirement
        boolean hasLength = password.length() >= 8;
        updateRequirementUI(reqLength, hasLength, "✓ Ít nhất 8 ký tự", "○ Ít nhất 8 ký tự");
        
        // Check uppercase requirement
        boolean hasUppercase = password.matches(".*[A-Z].*");
        updateRequirementUI(reqUppercase, hasUppercase, "✓ Chứa chữ hoa (A-Z)", "○ Chứa chữ hoa (A-Z)");
        
        // Check lowercase requirement
        boolean hasLowercase = password.matches(".*[a-z].*");
        updateRequirementUI(reqLowercase, hasLowercase, "✓ Chứa chữ thường (a-z)", "○ Chứa chữ thường (a-z)");
        
        // Check digit requirement
        boolean hasDigit = password.matches(".*[0-9].*");
        updateRequirementUI(reqDigit, hasDigit, "✓ Chứa chữ số (0-9)", "○ Chứa chữ số (0-9)");
    }
    
    /**
     * Update single requirement UI element
     */
    private void updateRequirementUI(TextView textView, boolean isMet, String metText, String unmetText) {
        if (isMet) {
            textView.setText(metText);
            textView.setTextColor(getResources().getColor(android.R.color.holo_green_dark));
        } else {
            textView.setText(unmetText);
            textView.setTextColor(getResources().getColor(android.R.color.holo_red_dark));
        }
    }
    
    /**
     * Reset password requirements to default state
     */
    private void resetPasswordRequirements() {
        TextView reqLength = findViewById(R.id.req_length);
        TextView reqUppercase = findViewById(R.id.req_uppercase);
        TextView reqLowercase = findViewById(R.id.req_lowercase);
        TextView reqDigit = findViewById(R.id.req_digit);
        
        reqLength.setText("○ Ít nhất 8 ký tự");
        reqLength.setTextColor(getResources().getColor(android.R.color.holo_red_dark));
        
        reqUppercase.setText("○ Chứa chữ hoa (A-Z)");
        reqUppercase.setTextColor(getResources().getColor(android.R.color.holo_red_dark));
        
        reqLowercase.setText("○ Chứa chữ thường (a-z)");
        reqLowercase.setTextColor(getResources().getColor(android.R.color.holo_red_dark));
        
        reqDigit.setText("○ Chứa chữ số (0-9)");
        reqDigit.setTextColor(getResources().getColor(android.R.color.holo_red_dark));
    }
    
    /**
     * Calculate password strength score
     */
    private int calculatePasswordStrength(String password) {
        int score = 0;
        
        if (password.length() >= 8) score += 20;
        if (password.length() >= 12) score += 10;
        if (password.matches(".*[a-z].*")) score += 15;
        if (password.matches(".*[A-Z].*")) score += 15;
        if (password.matches(".*[0-9].*")) score += 15;
        if (password.matches(".*[!@#$%^&*()_+\\-=\\[\\]{};':\",./<>?].*")) score += 10;
        
        return Math.min(score, 100);
    }
    
    /**
     * Perform registration logic
     */
    private void performRegistration() {
        String name = nameInput.getText().toString().trim();
        String email = emailInput.getText().toString().trim();
        String phone = phoneInput.getText().toString().trim();
        String password = passwordInput.getText().toString();
        String confirmPassword = confirmPasswordInput.getText().toString();
        
        // Validate all fields
        String validationError = ValidationHelper.validateRegistration(name, email, password, confirmPassword, phone);
        if (!validationError.isEmpty()) {
            showError(validationError);
            return;
        }
        
        // Check if email already exists
        if (userDAO.isEmailExists(email)) {
            showError("Email này đã được đăng ký");
            return;
        }
        
        // Disable button to prevent multiple clicks
        registerBtn.setEnabled(false);
        
        // Create user
        User newUser = new User(name, email, password);
        newUser.setPhone(phone);
        
        long result = userDAO.registerUser(newUser);
        
        if (result > 0) {
            // Registration successful
            showSuccess("Đăng ký thành công!");
            
            // Clear inputs
            clearInputs();
            
            // Navigate to login
            navigateToLogin();
        } else {
            // Registration failed
            showError("Đăng ký thất bại. Vui lòng thử lại");
            registerBtn.setEnabled(true);
        }
    }
    
    /**
     * Toggle password visibility
     */
    private void togglePasswordVisibility() {
        if (isPasswordVisible) {
            passwordInput.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
            togglePasswordBtn.setImageResource(android.R.drawable.ic_menu_view);
        } else {
            passwordInput.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
            togglePasswordBtn.setImageResource(android.R.drawable.ic_menu_close_clear_cancel);
        }
        
        isPasswordVisible = !isPasswordVisible;
        passwordInput.setSelection(passwordInput.getText().length());
    }
    
    /**
     * Toggle confirm password visibility
     */
    private void toggleConfirmPasswordVisibility() {
        if (isConfirmPasswordVisible) {
            confirmPasswordInput.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
            toggleConfirmPasswordBtn.setImageResource(android.R.drawable.ic_menu_view);
        } else {
            confirmPasswordInput.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
            toggleConfirmPasswordBtn.setImageResource(android.R.drawable.ic_menu_close_clear_cancel);
        }
        
        isConfirmPasswordVisible = !isConfirmPasswordVisible;
        confirmPasswordInput.setSelection(confirmPasswordInput.getText().length());
    }
    
    /**
     * Navigate back to login
     */
    private void navigateToLogin() {
        Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }
    
    /**
     * Clear all input fields
     */
    private void clearInputs() {
        nameInput.setText("");
        emailInput.setText("");
        phoneInput.setText("");
        passwordInput.setText("");
        confirmPasswordInput.setText("");
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
