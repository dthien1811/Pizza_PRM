package fpt.edu.vn.pizzaapp_prm392.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

import fpt.edu.vn.pizzaapp_prm392.database.DatabaseHelper;
import fpt.edu.vn.pizzaapp_prm392.models.Payment;

/**
 * PaymentDAO - Thao tác dữ liệu thanh toán
 */
public class PaymentDAO {
    
    private DatabaseHelper dbHelper;
    private SQLiteDatabase db;
    
    public PaymentDAO(Context context) {
        dbHelper = new DatabaseHelper(context);
    }
    
    /**
     * Mở kết nối cơ sở dữ liệu
     */
    public void open() {
        db = dbHelper.getWritableDatabase();
    }
    
    /**
     * Đóng kết nối cơ sở dữ liệu
     */
    public void close() {
        dbHelper.close();
    }
    
    /**
     * Tạo thanh toán mới
     */
    public long createPayment(Payment payment) {
        open();
        try {
            ContentValues values = new ContentValues();
            values.put(DatabaseHelper.COLUMN_PAYMENT_ORDER_ID, payment.getOrderId());
            values.put(DatabaseHelper.COLUMN_PAYMENT_METHOD, payment.getPaymentMethod());
            values.put(DatabaseHelper.COLUMN_PAYMENT_AMOUNT, payment.getAmount());
            values.put(DatabaseHelper.COLUMN_PAYMENT_STATUS, payment.getStatus());
            values.put(DatabaseHelper.COLUMN_PAYMENT_TRANSACTION_ID, payment.getTransactionId());
            
            return db.insert(DatabaseHelper.TABLE_PAYMENTS, null, values);
        } finally {
            close();
        }
    }
    
    /**
     * Lấy thông tin thanh toán theo ID
     */
    public Payment getPaymentById(int paymentId) {
        open();
        try {
            Cursor cursor = db.query(
                    DatabaseHelper.TABLE_PAYMENTS,
                    null,
                    DatabaseHelper.COLUMN_PAYMENT_ID + " = ?",
                    new String[]{String.valueOf(paymentId)},
                    null, null, null
            );
            
            if (cursor != null && cursor.moveToFirst()) {
                Payment payment = cursorToPayment(cursor);
                cursor.close();
                return payment;
            }
            return null;
        } finally {
            close();
        }
    }
    
    /**
     * Lấy thông tin thanh toán theo đơn hàng
     */
    public Payment getPaymentByOrderId(int orderId) {
        open();
        try {
            Cursor cursor = db.query(
                    DatabaseHelper.TABLE_PAYMENTS,
                    null,
                    DatabaseHelper.COLUMN_PAYMENT_ORDER_ID + " = ?",
                    new String[]{String.valueOf(orderId)},
                    null, null, null
            );
            
            if (cursor != null && cursor.moveToFirst()) {
                Payment payment = cursorToPayment(cursor);
                cursor.close();
                return payment;
            }
            return null;
        } finally {
            close();
        }
    }
    
    /**
     * Lấy tất cả thanh toán
     */
    public List<Payment> getAllPayments() {
        open();
        List<Payment> payments = new ArrayList<>();
        try {
            Cursor cursor = db.query(
                    DatabaseHelper.TABLE_PAYMENTS,
                    null, null, null, null, null,
                    DatabaseHelper.COLUMN_PAYMENT_CREATED_AT + " DESC"
            );
            
            if (cursor != null) {
                while (cursor.moveToNext()) {
                    payments.add(cursorToPayment(cursor));
                }
                cursor.close();
            }
        } finally {
            close();
        }
        return payments;
    }
    
    /**
     * Lấy thanh toán theo trạng thái
     */
    public List<Payment> getPaymentsByStatus(String status) {
        open();
        List<Payment> payments = new ArrayList<>();
        try {
            Cursor cursor = db.query(
                    DatabaseHelper.TABLE_PAYMENTS,
                    null,
                    DatabaseHelper.COLUMN_PAYMENT_STATUS + " = ?",
                    new String[]{status},
                    null, null,
                    DatabaseHelper.COLUMN_PAYMENT_CREATED_AT + " DESC"
            );
            
            if (cursor != null) {
                while (cursor.moveToNext()) {
                    payments.add(cursorToPayment(cursor));
                }
                cursor.close();
            }
        } finally {
            close();
        }
        return payments;
    }
    
    /**
     * Lấy thanh toán theo phương thức
     */
    public List<Payment> getPaymentsByMethod(String method) {
        open();
        List<Payment> payments = new ArrayList<>();
        try {
            Cursor cursor = db.query(
                    DatabaseHelper.TABLE_PAYMENTS,
                    null,
                    DatabaseHelper.COLUMN_PAYMENT_METHOD + " = ?",
                    new String[]{method},
                    null, null,
                    DatabaseHelper.COLUMN_PAYMENT_CREATED_AT + " DESC"
            );
            
            if (cursor != null) {
                while (cursor.moveToNext()) {
                    payments.add(cursorToPayment(cursor));
                }
                cursor.close();
            }
        } finally {
            close();
        }
        return payments;
    }
    
    /**
     * Cập nhật trạng thái thanh toán
     */
    public int updatePaymentStatus(int paymentId, String status) {
        open();
        try {
            ContentValues values = new ContentValues();
            values.put(DatabaseHelper.COLUMN_PAYMENT_STATUS, status);
            
            return db.update(
                    DatabaseHelper.TABLE_PAYMENTS,
                    values,
                    DatabaseHelper.COLUMN_PAYMENT_ID + " = ?",
                    new String[]{String.valueOf(paymentId)}
            );
        } finally {
            close();
        }
    }
    
    /**
     * Cập nhật ID giao dịch thanh toán
     */
    public int updateTransactionId(int paymentId, String transactionId) {
        open();
        try {
            ContentValues values = new ContentValues();
            values.put(DatabaseHelper.COLUMN_PAYMENT_TRANSACTION_ID, transactionId);
            
            return db.update(
                    DatabaseHelper.TABLE_PAYMENTS,
                    values,
                    DatabaseHelper.COLUMN_PAYMENT_ID + " = ?",
                    new String[]{String.valueOf(paymentId)}
            );
        } finally {
            close();
        }
    }
    
    /**
     * Cập nhật thông tin thanh toán
     */
    public int updatePayment(Payment payment) {
        open();
        try {
            ContentValues values = new ContentValues();
            values.put(DatabaseHelper.COLUMN_PAYMENT_METHOD, payment.getPaymentMethod());
            values.put(DatabaseHelper.COLUMN_PAYMENT_AMOUNT, payment.getAmount());
            values.put(DatabaseHelper.COLUMN_PAYMENT_STATUS, payment.getStatus());
            values.put(DatabaseHelper.COLUMN_PAYMENT_TRANSACTION_ID, payment.getTransactionId());
            
            return db.update(
                    DatabaseHelper.TABLE_PAYMENTS,
                    values,
                    DatabaseHelper.COLUMN_PAYMENT_ID + " = ?",
                    new String[]{String.valueOf(payment.getPaymentId())}
            );
        } finally {
            close();
        }
    }
    
    /**
     * Xóa thanh toán
     */
    public int deletePayment(int paymentId) {
        open();
        try {
            return db.delete(
                    DatabaseHelper.TABLE_PAYMENTS,
                    DatabaseHelper.COLUMN_PAYMENT_ID + " = ?",
                    new String[]{String.valueOf(paymentId)}
            );
        } finally {
            close();
        }
    }
    
    /**
     * Lấy số lượng thanh toán thành công
     */
    public int getSuccessfulPaymentCount() {
        open();
        try {
            Cursor cursor = db.rawQuery(
                    "SELECT COUNT(*) FROM " + DatabaseHelper.TABLE_PAYMENTS + 
                    " WHERE " + DatabaseHelper.COLUMN_PAYMENT_STATUS + " = ?",
                    new String[]{"COMPLETED"}
            );
            
            int count = 0;
            if (cursor != null) {
                cursor.moveToFirst();
                count = cursor.getInt(0);
                cursor.close();
            }
            return count;
        } finally {
            close();
        }
    }
    
    /**
     * Tính tổng tiền thanh toán
     */
    public double getTotalPaymentAmount(String status) {
        open();
        try {
            Cursor cursor = db.rawQuery(
                    "SELECT SUM(" + DatabaseHelper.COLUMN_PAYMENT_AMOUNT + ") FROM " + 
                    DatabaseHelper.TABLE_PAYMENTS + " WHERE " + 
                    DatabaseHelper.COLUMN_PAYMENT_STATUS + " = ?",
                    new String[]{status}
            );
            
            double total = 0;
            if (cursor != null) {
                cursor.moveToFirst();
                total = cursor.getDouble(0);
                cursor.close();
            }
            return total;
        } finally {
            close();
        }
    }
    
    /**
     * Chuyển Cursor thành đối tượng Payment
     */
    private Payment cursorToPayment(Cursor cursor) {
        Payment payment = new Payment();
        payment.setPaymentId(cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_PAYMENT_ID)));
        payment.setOrderId(cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_PAYMENT_ORDER_ID)));
        payment.setPaymentMethod(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_PAYMENT_METHOD)));
        payment.setAmount(cursor.getDouble(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_PAYMENT_AMOUNT)));
        payment.setStatus(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_PAYMENT_STATUS)));
        payment.setTransactionId(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_PAYMENT_TRANSACTION_ID)));
        payment.setCreatedAt(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_PAYMENT_CREATED_AT)));
        return payment;
    }
}
