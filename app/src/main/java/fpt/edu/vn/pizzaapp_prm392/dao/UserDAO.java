package fpt.edu.vn.pizzaapp_prm392.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import fpt.edu.vn.pizzaapp_prm392.database.DatabaseHelper;
import fpt.edu.vn.pizzaapp_prm392.models.User;

/**
 * UserDAO - Thao tác dữ liệu người dùng
 */
public class UserDAO {
    
    private DatabaseHelper dbHelper;
    private SQLiteDatabase db;
    
    public UserDAO(Context context) {
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
     * Đăng ký người dùng mới
     */
    public long registerUser(User user) {
        open();
        try {
            ContentValues values = new ContentValues();
            values.put(DatabaseHelper.COLUMN_USER_NAME, user.getName());
            values.put(DatabaseHelper.COLUMN_USER_EMAIL, user.getEmail());
            values.put(DatabaseHelper.COLUMN_USER_PASSWORD, user.getPassword());
            values.put(DatabaseHelper.COLUMN_USER_PHONE, user.getPhone());
            values.put(DatabaseHelper.COLUMN_USER_ADDRESS, user.getAddress());
            
            return db.insert(DatabaseHelper.TABLE_USERS, null, values);
        } finally {
            close();
        }
    }
    
    /**
     * Đăng nhập người dùng - kiểm tra email và password
     */
    public User loginUser(String email, String password) {
        open();
        try {
            Cursor cursor = db.query(
                    DatabaseHelper.TABLE_USERS,
                    null,
                    DatabaseHelper.COLUMN_USER_EMAIL + " = ? AND " + 
                    DatabaseHelper.COLUMN_USER_PASSWORD + " = ?",
                    new String[]{email, password},
                    null, null, null
            );
            
            if (cursor != null && cursor.moveToFirst()) {
                User user = cursorToUser(cursor);
                cursor.close();
                return user;
            }
            return null;
        } finally {
            close();
        }
    }
    
    /**
     * Lấy thông tin người dùng theo ID
     */
    public User getUserById(int userId) {
        open();
        try {
            Cursor cursor = db.query(
                    DatabaseHelper.TABLE_USERS,
                    null,
                    DatabaseHelper.COLUMN_USER_ID + " = ?",
                    new String[]{String.valueOf(userId)},
                    null, null, null
            );
            
            if (cursor != null && cursor.moveToFirst()) {
                User user = cursorToUser(cursor);
                cursor.close();
                return user;
            }
            return null;
        } finally {
            close();
        }
    }
    
    /**
     * Lấy người dùng theo email
     */
    public User getUserByEmail(String email) {
        open();
        try {
            Cursor cursor = db.query(
                    DatabaseHelper.TABLE_USERS,
                    null,
                    DatabaseHelper.COLUMN_USER_EMAIL + " = ?",
                    new String[]{email},
                    null, null, null
            );
            
            if (cursor != null && cursor.moveToFirst()) {
                User user = cursorToUser(cursor);
                cursor.close();
                return user;
            }
            return null;
        } finally {
            close();
        }
    }
    
    /**
     * Kiểm tra email đã tồn tại hay chưa
     */
    public boolean isEmailExists(String email) {
        return getUserByEmail(email) != null;
    }
    
    /**
     * Cập nhật thông tin người dùng
     */
    public int updateUser(User user) {
        open();
        try {
            ContentValues values = new ContentValues();
            values.put(DatabaseHelper.COLUMN_USER_NAME, user.getName());
            values.put(DatabaseHelper.COLUMN_USER_EMAIL, user.getEmail());
            values.put(DatabaseHelper.COLUMN_USER_PHONE, user.getPhone());
            values.put(DatabaseHelper.COLUMN_USER_ADDRESS, user.getAddress());
            values.put(DatabaseHelper.COLUMN_USER_AVATAR, user.getAvatar());
            values.put(DatabaseHelper.COLUMN_USER_UPDATED_AT, java.time.LocalDateTime.now().toString());
            
            return db.update(
                    DatabaseHelper.TABLE_USERS,
                    values,
                    DatabaseHelper.COLUMN_USER_ID + " = ?",
                    new String[]{String.valueOf(user.getUserId())}
            );
        } finally {
            close();
        }
    }
    
    /**
     * Cập nhật token người dùng
     */
    public int updateUserToken(int userId, String token) {
        open();
        try {
            ContentValues values = new ContentValues();
            values.put(DatabaseHelper.COLUMN_USER_TOKEN, token);
            
            return db.update(
                    DatabaseHelper.TABLE_USERS,
                    values,
                    DatabaseHelper.COLUMN_USER_ID + " = ?",
                    new String[]{String.valueOf(userId)}
            );
        } finally {
            close();
        }
    }
    
    /**
     * Cập nhật password người dùng
     */
    public int updateUserPassword(int userId, String newPassword) {
        open();
        try {
            ContentValues values = new ContentValues();
            values.put(DatabaseHelper.COLUMN_USER_PASSWORD, newPassword);
            values.put(DatabaseHelper.COLUMN_USER_UPDATED_AT, java.time.LocalDateTime.now().toString());
            
            return db.update(
                    DatabaseHelper.TABLE_USERS,
                    values,
                    DatabaseHelper.COLUMN_USER_ID + " = ?",
                    new String[]{String.valueOf(userId)}
            );
        } finally {
            close();
        }
    }
    
    /**
     * Xóa người dùng
     */
    public int deleteUser(int userId) {
        open();
        try {
            return db.delete(
                    DatabaseHelper.TABLE_USERS,
                    DatabaseHelper.COLUMN_USER_ID + " = ?",
                    new String[]{String.valueOf(userId)}
            );
        } finally {
            close();
        }
    }
    
    /**
     * Chuyển Cursor thành đối tượng User
     */
    private User cursorToUser(Cursor cursor) {
        User user = new User();
        user.setUserId(cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_USER_ID)));
        user.setName(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_USER_NAME)));
        user.setEmail(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_USER_EMAIL)));
        user.setPhone(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_USER_PHONE)));
        user.setPassword(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_USER_PASSWORD)));
        user.setAddress(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_USER_ADDRESS)));
        user.setAvatar(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_USER_AVATAR)));
        user.setToken(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_USER_TOKEN)));
        user.setCreatedAt(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_USER_CREATED_AT)));
        user.setUpdatedAt(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_USER_UPDATED_AT)));
        return user;
    }
}
