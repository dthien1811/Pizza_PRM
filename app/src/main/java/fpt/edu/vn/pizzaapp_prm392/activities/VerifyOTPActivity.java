package fpt.edu.vn.pizzaapp_prm392.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import fpt.edu.vn.pizzaapp_prm392.R;
import fpt.edu.vn.pizzaapp_prm392.utils.OTPHelper;

/**
 * VerifyOTPActivity - Màn hình xác thực OTP
 */
public class VerifyOTPActivity extends AppCompatActivity {
    
    private ImageButton backBtn;
    private EditText otpInput;
    private Button verifyBtn;
    private Button resendBtn;
    private TextView emailDisplay;
    private TextView timerText;
    
    private String email;
    private String correctOTP;
    private long otpGeneratedTime;
    private CountDownTimer countDownTimer;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify_otp);
        
        // Get data from previous activity
        email = getIntent().getStringExtra("email");
        correctOTP = getIntent().getStringExtra("otp");
        otpGeneratedTime = getIntent().getLongExtra("otp_time", 0);
        
        initializeViews();
        setupClickListeners();
        startTimer();
        
        emailDisplay.setText("Mã xác thực đã gửi tới:\n" + email);
    }
    
    /**
     * Initialize UI views
     */
    private void initializeViews() {
        backBtn = findViewById(R.id.back_btn);
        otpInput = findViewById(R.id.otp_input);
        verifyBtn = findViewById(R.id.verify_btn);
        resendBtn = findViewById(R.id.resend_btn);
        emailDisplay = findViewById(R.id.email_display);
        timerText = findViewById(R.id.timer_text);
    }
    
    /**
     * Setup click listeners
     */
    private void setupClickListeners() {
        backBtn.setOnClickListener(v -> finish());
        verifyBtn.setOnClickListener(v -> verifyOTP());
        resendBtn.setOnClickListener(v -> resendOTP());
    }
    
    /**
     * Start countdown timer
     */
    private void startTimer() {
        resendBtn.setEnabled(false);
        
        countDownTimer = new CountDownTimer(10 * 60 * 1000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                long minutes = millisUntilFinished / (60 * 1000);
                long seconds = (millisUntilFinished % (60 * 1000)) / 1000;
                timerText.setText(String.format("⏱️ Hết hạn trong: %d:%02d", minutes, seconds));
            }
            
            @Override
            public void onFinish() {
                timerText.setText("⏱️ Mã đã hết hạn");
                verifyBtn.setEnabled(false);
                resendBtn.setEnabled(true);
            }
        }.start();
    }
    
    /**
     * Verify OTP
     */
    private void verifyOTP() {
        String enteredOTP = otpInput.getText().toString().trim();
        
        // Check if OTP is empty
        if (enteredOTP.isEmpty()) {
            showError("Vui lòng nhập mã xác thực");
            return;
        }
        
        // Check if OTP is expired
        if (!OTPHelper.isOTPValid(otpGeneratedTime)) {
            showError("Mã xác thực đã hết hạn. Vui lòng gửi lại.");
            verifyBtn.setEnabled(false);
            resendBtn.setEnabled(true);
            return;
        }
        
        // Verify OTP
        if (enteredOTP.equals(correctOTP)) {
            showSuccess("Xác thực thành công!");
            
            if (countDownTimer != null) {
                countDownTimer.cancel();
            }
            
            // Navigate to reset password activity
            navigateToResetPassword();
        } else {
            showError("Mã xác thực không chính xác");
        }
    }
    
    /**
     * Resend OTP
     */
    private void resendOTP() {
        showError("Vui lòng quay lại màn hình trước để gửi mã mới");
        finish();
    }
    
    /**
     * Navigate to reset password activity
     */
    private void navigateToResetPassword() {
        Intent intent = new Intent(VerifyOTPActivity.this, ResetPasswordActivity.class);
        intent.putExtra("email", email);
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
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
    }
}
