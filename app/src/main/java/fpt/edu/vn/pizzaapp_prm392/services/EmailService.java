package fpt.edu.vn.pizzaapp_prm392.services;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.util.Log;

import java.io.UnsupportedEncodingException;
import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

/**
 * EmailService - Gửi email OTP tới người dùng
 */
public class EmailService extends AsyncTask<Void, Void, Boolean> {
    
    // Email configuration
    private static final String SENDER_EMAIL = "mailmatecs1@gmail.com";
    private static final String SENDER_NAME = "MAILMate Team";
    private static final String SMTP_HOST = "smtp.gmail.com";
    private static final String SMTP_PORT = "587";
    private static final String SMTP_USERNAME = "mailmatecs1@gmail.com";
    private static final String SMTP_PASSWORD = "gzselksfvkvxxoyf";
    
    private String recipientEmail;
    private String otp;
    private String userEmail;
    private EmailCallback callback;
    private Context context;
    
    public interface EmailCallback {
        void onSuccess();
        void onFailure(String error);
    }
    
    public EmailService(Context context, String recipientEmail, String otp, String userEmail, EmailCallback callback) {
        this.context = context;
        this.recipientEmail = recipientEmail;
        this.otp = otp;
        this.userEmail = userEmail;
        this.callback = callback;
        
        Log.d("EmailService", "EmailService constructor called");
        Log.d("EmailService", "Context: " + (context != null ? "Provided" : "Null"));
        Log.d("EmailService", "Recipient: " + recipientEmail);
        Log.d("EmailService", "OTP: " + otp);
        Log.d("EmailService", "User Email: " + userEmail);
        Log.d("EmailService", "Callback: " + (callback != null ? "Provided" : "Null"));
    }
    
    /**
     * Validate email parameters before sending
     */
    private boolean validateParameters() {
        Log.d("EmailService", "=== VALIDATION ===");
        
        if (recipientEmail == null || recipientEmail.trim().isEmpty()) {
            Log.e("EmailService", "❌ Recipient email is null or empty");
            return false;
        }
        
        if (otp == null || otp.trim().isEmpty()) {
            Log.e("EmailService", "❌ OTP is null or empty");
            return false;
        }
        
        if (userEmail == null || userEmail.trim().isEmpty()) {
            Log.e("EmailService", "❌ User email is null or empty");
            return false;
        }
        
        if (callback == null) {
            Log.e("EmailService", "❌ Callback is null");
            return false;
        }
        
        // Basic email format validation
        if (!recipientEmail.contains("@") || !recipientEmail.contains(".")) {
            Log.e("EmailService", "❌ Invalid email format: " + recipientEmail);
            return false;
        }
        
        Log.d("EmailService", "✅ All parameters are valid");
        return true;
    }
    
    /**
     * Check network connectivity
     */
    private boolean isNetworkAvailable() {
        if (context == null) {
            Log.e("EmailService", "❌ Context is null, cannot check network");
            return false;
        }
        
        ConnectivityManager connectivityManager = 
            (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        
        if (connectivityManager == null) {
            Log.e("EmailService", "❌ ConnectivityManager is null");
            return false;
        }
        
        NetworkInfo activeNetwork = connectivityManager.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting();
        
        Log.d("EmailService", "Network available: " + isConnected);
        if (activeNetwork != null) {
            Log.d("EmailService", "Network type: " + activeNetwork.getTypeName());
            Log.d("EmailService", "Network state: " + activeNetwork.getState());
        }
        
        return isConnected;
    }
    
    @Override
    protected Boolean doInBackground(Void... voids) {
        Log.d("EmailService", "=== BẮT ĐẦU GỬI EMAIL ===");
        Log.d("EmailService", "Recipient: " + recipientEmail);
        Log.d("EmailService", "OTP: " + otp);
        Log.d("EmailService", "User Email: " + userEmail);
        
        // Validate parameters first
        if (!validateParameters()) {
            Log.e("EmailService", "❌ Validation failed, aborting email send");
            return false;
        }
        
        // Check network connectivity
        if (!isNetworkAvailable()) {
            Log.e("EmailService", "❌ No network connection, aborting email send");
            return false;
        }
        
        try {
            Log.d("EmailService", "1. Thiết lập email properties...");
            // Setup email properties
            Properties props = new Properties();
            props.put("mail.smtp.host", SMTP_HOST);
            props.put("mail.smtp.port", SMTP_PORT);
            props.put("mail.smtp.auth", "true");
            props.put("mail.smtp.starttls.enable", "true");
            props.put("mail.smtp.starttls.required", "true");
            props.put("mail.smtp.connectiontimeout", 15000); // Tăng timeout
            props.put("mail.smtp.timeout", 15000); // Tăng timeout
            props.put("mail.smtp.writetimeout", 15000); // Thêm write timeout
            props.put("mail.smtp.ssl.trust", SMTP_HOST); // Trust SMTP host
            props.put("mail.smtp.ssl.protocols", "TLSv1.2"); // Specify TLS version
            
            Log.d("EmailService", "SMTP Host: " + SMTP_HOST);
            Log.d("EmailService", "SMTP Port: " + SMTP_PORT);
            Log.d("EmailService", "SMTP Username: " + SMTP_USERNAME);
            
            Log.d("EmailService", "2. Tạo session...");
            // Create session
            Session session = Session.getInstance(props, new Authenticator() {
                @Override
                protected PasswordAuthentication getPasswordAuthentication() {
                    Log.d("EmailService", "Authenticating with username: " + SMTP_USERNAME);
                    return new PasswordAuthentication(SMTP_USERNAME, SMTP_PASSWORD);
                }
            });
            
            Log.d("EmailService", "2.1. Test SMTP connection...");
            if (!testSMTPConnection(session)) {
                Log.e("EmailService", "❌ SMTP connection test failed, aborting");
                return false;
            }
            
            Log.d("EmailService", "3. Tạo message...");
            // Create message
            Message message = new MimeMessage(session);
            try {
                Log.d("EmailService", "Setting from address: " + SENDER_EMAIL + " (" + SENDER_NAME + ")");
                message.setFrom(new InternetAddress(SENDER_EMAIL, SENDER_NAME));
            } catch (UnsupportedEncodingException e) {
                Log.e("EmailService", "Lỗi encoding sender name: " + e.getMessage());
                e.printStackTrace();
                return false;
            }
            
            Log.d("EmailService", "Setting recipient: " + recipientEmail);
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(recipientEmail));
            
            String subject = "🍕 PizzaApp - Mã xác thực đặt lại mật khẩu";
            Log.d("EmailService", "Setting subject: " + subject);
            message.setSubject(subject);
            
            Log.d("EmailService", "4. Tạo HTML content...");
            // Create HTML email content
            String htmlContent = buildEmailContent(otp, userEmail);
            Log.d("EmailService", "HTML content length: " + htmlContent.length());
            message.setContent(htmlContent, "text/html; charset=utf-8");
            
            Log.d("EmailService", "5. Gửi email...");
            // Send email
            Transport.send(message);
            Log.d("EmailService", "✅ EMAIL GỬI THÀNH CÔNG!");
            return true;
            
        } catch (MessagingException e) {
            Log.e("EmailService", "❌ LỖI MESSAGING: " + e.getMessage());
            Log.e("EmailService", "Exception type: " + e.getClass().getSimpleName());
            e.printStackTrace();
            
            // Log chi tiết hơn về lỗi
            if (e.getMessage() != null) {
                Log.e("EmailService", "Error details: " + e.getMessage());
            }
            
            return false;
        } catch (Exception e) {
            Log.e("EmailService", "❌ LỖI KHÁC: " + e.getMessage());
            Log.e("EmailService", "Exception type: " + e.getClass().getSimpleName());
            e.printStackTrace();
            return false;
        }
    }
    
    @Override
    protected void onPostExecute(Boolean result) {
        super.onPostExecute(result);
        Log.d("EmailService", "=== KẾT QUẢ GỬI EMAIL ===");
        Log.d("EmailService", "Result: " + result);
        
        if (result) {
            Log.d("EmailService", "✅ Gọi callback onSuccess");
            if (callback != null) {
                callback.onSuccess();
            }
        } else {
            Log.e("EmailService", "❌ Gọi callback onFailure");
            if (callback != null) {
                callback.onFailure("Gửi email thất bại. Vui lòng kiểm tra:\n" +
                        "1. Kết nối internet\n" +
                        "2. Email đích có hợp lệ không\n" +
                        "3. Xem log chi tiết trong Logcat");
            }
        }
    }
    
    /**
     * Test SMTP connection
     */
    private boolean testSMTPConnection(Session session) {
        try {
            Log.d("EmailService", "Testing SMTP connection...");
            Transport transport = session.getTransport("smtp");
            transport.connect(SMTP_HOST, Integer.parseInt(SMTP_PORT), SMTP_USERNAME, SMTP_PASSWORD);
            transport.close();
            Log.d("EmailService", "✅ SMTP connection test successful");
            return true;
        } catch (Exception e) {
            Log.e("EmailService", "❌ SMTP connection test failed: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Tạo HTML content cho email
     */
    private String buildEmailContent(String otp, String userEmail) {
        return "<!DOCTYPE html>\n" +
                "<html>\n" +
                "<head>\n" +
                "    <meta charset=\"UTF-8\">\n" +
                "    <style>\n" +
                "        body { font-family: 'Segoe UI', Arial, sans-serif; background-color: #f5f5f5; margin: 0; padding: 0; }\n" +
                "        .container { max-width: 600px; margin: 20px auto; background-color: white; border-radius: 8px; box-shadow: 0 2px 8px rgba(0,0,0,0.1); overflow: hidden; }\n" +
                "        .header { background: linear-gradient(135deg, #FF6B6B 0%, #EE5A4A 100%); color: white; padding: 30px 20px; text-align: center; }\n" +
                "        .header h1 { margin: 0; font-size: 28px; }\n" +
                "        .header p { margin: 5px 0 0 0; font-size: 14px; opacity: 0.9; }\n" +
                "        .content { padding: 30px 20px; }\n" +
                "        .greeting { font-size: 16px; color: #333; margin-bottom: 20px; }\n" +
                "        .otp-section { background-color: #f9f9f9; border: 2px dashed #FF6B6B; border-radius: 8px; padding: 20px; margin: 20px 0; text-align: center; }\n" +
                "        .otp-label { font-size: 14px; color: #666; margin-bottom: 10px; }\n" +
                "        .otp-code { font-size: 32px; font-weight: bold; color: #FF6B6B; letter-spacing: 4px; font-family: 'Courier New', monospace; margin: 10px 0; }\n" +
                "        .otp-expiry { font-size: 12px; color: #FF9800; margin-top: 10px; }\n" +
                "        .info-box { background-color: #E3F2FD; border-left: 4px solid #2196F3; padding: 15px; margin: 20px 0; border-radius: 4px; }\n" +
                "        .info-box p { margin: 0; font-size: 14px; color: #1565C0; }\n" +
                "        .warning-box { background-color: #FFF3E0; border-left: 4px solid #FF9800; padding: 15px; margin: 20px 0; border-radius: 4px; }\n" +
                "        .warning-box p { margin: 0; font-size: 14px; color: #E65100; }\n" +
                "        .footer { background-color: #f5f5f5; padding: 20px; text-align: center; border-top: 1px solid #ddd; }\n" +
                "        .footer p { margin: 5px 0; font-size: 12px; color: #999; }\n" +
                "        .divider { height: 1px; background-color: #ddd; margin: 20px 0; }\n" +
                "    </style>\n" +
                "</head>\n" +
                "<body>\n" +
                "    <div class=\"container\">\n" +
                "        <!-- Header -->\n" +
                "        <div class=\"header\">\n" +
                "            <h1>🍕 PizzaApp</h1>\n" +
                "            <p>Đặt lại mật khẩu của bạn</p>\n" +
                "        </div>\n" +
                "        \n" +
                "        <!-- Content -->\n" +
                "        <div class=\"content\">\n" +
                "            <p class=\"greeting\">Xin chào,</p>\n" +
                "            \n" +
                "            <p>Bạn đã yêu cầu đặt lại mật khẩu cho tài khoản của mình trên PizzaApp. Vui lòng sử dụng mã xác thực bên dưới để tiếp tục.</p>\n" +
                "            \n" +
                "            <!-- OTP Section -->\n" +
                "            <div class=\"otp-section\">\n" +
                "                <div class=\"otp-label\">Mã xác thực của bạn:</div>\n" +
                "                <div class=\"otp-code\">" + otp + "</div>\n" +
                "                <div class=\"otp-expiry\">⏱️ Mã này sẽ hết hạn trong 10 phút</div>\n" +
                "            </div>\n" +
                "            \n" +
                "            <!-- Info Box -->\n" +
                "            <div class=\"info-box\">\n" +
                "                <p><strong>ℹ️ Thông tin tài khoản:</strong></p>\n" +
                "                <p>Email: " + userEmail + "</p>\n" +
                "            </div>\n" +
                "            \n" +
                "            <!-- Warning Box -->\n" +
                "            <div class=\"warning-box\">\n" +
                "                <p><strong>⚠️ Lưu ý bảo mật:</strong></p>\n" +
                "                <p>Nếu bạn không yêu cầu đặt lại mật khẩu, vui lòng bỏ qua email này. Đừng chia sẻ mã này với bất kỳ ai.</p>\n" +
                "            </div>\n" +
                "            \n" +
                "            <div class=\"divider\"></div>\n" +
                "            \n" +
                "            <p style=\"font-size: 14px; color: #666;\">Nếu bạn gặp vấn đề, vui lòng liên hệ với chúng tôi qua email support.</p>\n" +
                "        </div>\n" +
                "        \n" +
                "        <!-- Footer -->\n" +
                "        <div class=\"footer\">\n" +
                "            <p>&copy; 2025 PizzaApp. Tất cả quyền được bảo lưu.</p>\n" +
                "            <p>Đây là email tự động, vui lòng không trả lời trực tiếp.</p>\n" +
                "            <p>📧 support@pizzaapp.com | 🌐 www.pizzaapp.com</p>\n" +
                "        </div>\n" +
                "    </div>\n" +
                "</body>\n" +
                "</html>";
    }
}
