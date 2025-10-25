package fpt.edu.vn.pizzaapp_prm392.activities;

import android.content.Intent;
import android.os.Bundle;
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
import fpt.edu.vn.pizzaapp_prm392.services.EmailService;
import fpt.edu.vn.pizzaapp_prm392.utils.OTPHelper;
import fpt.edu.vn.pizzaapp_prm392.utils.ValidationHelper;

/**
 * ForgotPasswordActivity - Màn hình quên mật khẩu
 */
public class ForgotPasswordActivity extends AppCompatActivity {
    
    private ImageButton backBtn;
    private EditText emailInput;
    private Button sendOTPBtn;
    private ProgressBar loadingProgress;
    private TextView infoText;
    
    private UserDAO userDAO;
    private String generatedOTP;
    private long otpGeneratedTime;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);
        
        initializeViews();
        setupClickListeners();
        
        userDAO = new UserDAO(this);
    }
    
    /**
     * Initialize UI views
     */
    private void initializeViews() {
        backBtn = findViewById(R.id.back_btn);
        emailInput = findViewById(R.id.email_input);
        sendOTPBtn = findViewById(R.id.send_otp_btn);
        loadingProgress = findViewById(R.id.loading_progress);
        infoText = findViewById(R.id.info_text);
    }
    
    /**
     * Setup click listeners
     */
    private void setupClickListeners() {
        backBtn.setOnClickListener(v -> finish());
        sendOTPBtn.setOnClickListener(v -> sendOTP());
    }
    
    /**
     * Send OTP to user email
     */
    private void sendOTP() {
        String email = emailInput.getText().toString().trim();
        
        // Validate email
        if (!ValidationHelper.isValidEmail(email)) {
            showError("Email không hợp lệ");
            return;
        }
        
        // Check if email exists
        User user = userDAO.getUserByEmail(email);
        if (user == null) {
            showError("Email này không được đăng ký trong hệ thống");
            return;
        }
        
        // Disable button and show loading
        sendOTPBtn.setEnabled(false);
        loadingProgress.setVisibility(android.view.View.VISIBLE);
        infoText.setText("Đang gửi mã xác thực...");
        
        // Generate OTP
        generatedOTP = OTPHelper.generateOTP();
        otpGeneratedTime = System.currentTimeMillis();
        
        // Send email
        new EmailService(this, email, generatedOTP, email, new EmailService.EmailCallback() {
            @Override
            public void onSuccess() {
                showSuccess("Mã xác thực đã gửi tới " + email);
                loadingProgress.setVisibility(android.view.View.GONE);
                
                // Navigate to OTP verification
                navigateToVerifyOTP(email, generatedOTP, otpGeneratedTime);
            }
            
            @Override
            public void onFailure(String error) {
                showError(error);
                sendOTPBtn.setEnabled(true);
                loadingProgress.setVisibility(android.view.View.GONE);
            }
        }).execute();
    }
    
    /**
     * Navigate to OTP verification activity
     */
    private void navigateToVerifyOTP(String email, String otp, long otpTime) {
        Intent intent = new Intent(ForgotPasswordActivity.this, VerifyOTPActivity.class);
        intent.putExtra("email", email);
        intent.putExtra("otp", otp);
        intent.putExtra("otp_time", otpTime);
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
    
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (userDAO != null) {
            userDAO.close();
        }
    }
}
