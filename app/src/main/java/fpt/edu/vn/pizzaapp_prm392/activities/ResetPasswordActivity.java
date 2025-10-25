package fpt.edu.vn.pizzaapp_prm392.activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
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
 * ResetPasswordActivity - Màn hình đặt lại mật khẩu
 */
public class ResetPasswordActivity extends AppCompatActivity {
    
    private ImageButton backBtn;
    private ImageButton togglePasswordBtn;
    private ImageButton toggleConfirmPasswordBtn;
    private EditText passwordInput;
    private EditText confirmPasswordInput;
    private ProgressBar passwordStrengthBar;
    private TextView passwordStrengthText;
    private Button resetBtn;
    
    private UserDAO userDAO;
    private String email;
    private boolean isPasswordVisible = false;
    private boolean isConfirmPasswordVisible = false;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);
        
        email = getIntent().getStringExtra("email");
        
        initializeViews();
        setupClickListeners();
        setupPasswordStrengthListener();
        
        userDAO = new UserDAO(this);
    }
    
    /**
     * Initialize UI views
     */
    private void initializeViews() {
        backBtn = findViewById(R.id.back_btn);
        passwordInput = findViewById(R.id.password_input);
        confirmPasswordInput = findViewById(R.id.confirm_password_input);
        togglePasswordBtn = findViewById(R.id.toggle_password_btn);
        toggleConfirmPasswordBtn = findViewById(R.id.toggle_confirm_password_btn);
        passwordStrengthBar = findViewById(R.id.password_strength_bar);
        passwordStrengthText = findViewById(R.id.password_strength_text);
        resetBtn = findViewById(R.id.reset_btn);
    }
    
    /**
     * Setup click listeners
     */
    private void setupClickListeners() {
        backBtn.setOnClickListener(v -> finish());
        togglePasswordBtn.setOnClickListener(v -> togglePasswordVisibility());
        toggleConfirmPasswordBtn.setOnClickListener(v -> toggleConfirmPasswordVisibility());
        resetBtn.setOnClickListener(v -> resetPassword());
    }
    
    /**
     * Setup password strength listener
     */
    private void setupPasswordStrengthListener() {
        passwordInput.addTextChangedListener(new android.text.TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                updatePasswordStrength(s.toString());
            }
            
            @Override
            public void afterTextChanged(android.text.Editable s) {}
        });
    }
    
    /**
     * Update password strength
     */
    private void updatePasswordStrength(String password) {
        if (password.isEmpty()) {
            passwordStrengthBar.setProgress(0);
            passwordStrengthText.setText("");
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
    }
    
    /**
     * Calculate password strength
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
     * Reset password
     */
    private void resetPassword() {
        String password = passwordInput.getText().toString();
        String confirmPassword = confirmPasswordInput.getText().toString();
        
        // Validate password
        if (!ValidationHelper.isValidPassword(password)) {
            showError(ValidationHelper.getPasswordStrengthMessage(password));
            return;
        }
        
        // Check if passwords match
        if (!password.equals(confirmPassword)) {
            showError("Mật khẩu không khớp");
            return;
        }
        
        // Disable button
        resetBtn.setEnabled(false);
        
        // Get user and update password
        User user = userDAO.getUserByEmail(email);
        if (user != null) {
            int result = userDAO.updateUserPassword(user.getUserId(), password);
            
            if (result > 0) {
                showSuccess("Đặt lại mật khẩu thành công!");
                
                // Navigate back to login
                Intent intent = new Intent(ResetPasswordActivity.this, LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            } else {
                showError("Đặt lại mật khẩu thất bại");
                resetBtn.setEnabled(true);
            }
        } else {
            showError("Không tìm thấy tài khoản");
            resetBtn.setEnabled(true);
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
     * Show error
     */
    private void showError(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
    
    /**
     * Show success
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
