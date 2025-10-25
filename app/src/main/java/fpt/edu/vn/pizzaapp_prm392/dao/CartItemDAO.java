package fpt.edu.vn.pizzaapp_prm392.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

import fpt.edu.vn.pizzaapp_prm392.database.DatabaseHelper;
import fpt.edu.vn.pizzaapp_prm392.models.CartItem;

/**
 * CartItemDAO - Thao tác dữ liệu giỏ hàng
 */
public class CartItemDAO {
    
    private DatabaseHelper dbHelper;
    private SQLiteDatabase db;
    
    public CartItemDAO(Context context) {
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
     * Thêm mục vào giỏ hàng
     */
    public long addToCart(CartItem cartItem) {
        open();
        try {
            // Kiểm tra nếu pizza đã có trong giỏ hàng của user
            CartItem existingItem = getCartItemByUserAndPizza(cartItem.getUserId(), cartItem.getPizzaId());
            
            if (existingItem != null) {
                // Cập nhật số lượng nếu đã tồn tại
                existingItem.setQuantity(existingItem.getQuantity() + cartItem.getQuantity());
                updateCartItem(existingItem);
                return existingItem.getCartId();
            } else {
                // Thêm mục mới
                ContentValues values = new ContentValues();
                values.put(DatabaseHelper.COLUMN_CART_USER_ID, cartItem.getUserId());
                values.put(DatabaseHelper.COLUMN_CART_PIZZA_ID, cartItem.getPizzaId());
                values.put(DatabaseHelper.COLUMN_CART_QUANTITY, cartItem.getQuantity());
                values.put(DatabaseHelper.COLUMN_CART_PRICE, cartItem.getPrice());
                values.put(DatabaseHelper.COLUMN_CART_NOTES, cartItem.getNotes());
                
                return db.insert(DatabaseHelper.TABLE_CART_ITEMS, null, values);
            }
        } finally {
            close();
        }
    }
    
    /**
     * Lấy giỏ hàng của người dùng
     */
    public List<CartItem> getCartByUser(int userId) {
        open();
        List<CartItem> cartItems = new ArrayList<>();
        try {
            Cursor cursor = db.query(
                    DatabaseHelper.TABLE_CART_ITEMS,
                    null,
                    DatabaseHelper.COLUMN_CART_USER_ID + " = ?",
                    new String[]{String.valueOf(userId)},
                    null, null, null
            );
            
            if (cursor != null) {
                while (cursor.moveToNext()) {
                    cartItems.add(cursorToCartItem(cursor));
                }
                cursor.close();
            }
        } finally {
            close();
        }
        return cartItems;
    }
    
    /**
     * Lấy mục giỏ hàng theo user và pizza
     */
    private CartItem getCartItemByUserAndPizza(int userId, int pizzaId) {
        open();
        try {
            Cursor cursor = db.query(
                    DatabaseHelper.TABLE_CART_ITEMS,
                    null,
                    DatabaseHelper.COLUMN_CART_USER_ID + " = ? AND " + 
                    DatabaseHelper.COLUMN_CART_PIZZA_ID + " = ?",
                    new String[]{String.valueOf(userId), String.valueOf(pizzaId)},
                    null, null, null
            );
            
            if (cursor != null && cursor.moveToFirst()) {
                CartItem item = cursorToCartItem(cursor);
                cursor.close();
                return item;
            }
            return null;
        } finally {
            close();
        }
    }
    
    /**
     * Lấy mục giỏ hàng theo ID
     */
    public CartItem getCartItemById(int cartId) {
        open();
        try {
            Cursor cursor = db.query(
                    DatabaseHelper.TABLE_CART_ITEMS,
                    null,
                    DatabaseHelper.COLUMN_CART_ID + " = ?",
                    new String[]{String.valueOf(cartId)},
                    null, null, null
            );
            
            if (cursor != null && cursor.moveToFirst()) {
                CartItem item = cursorToCartItem(cursor);
                cursor.close();
                return item;
            }
            return null;
        } finally {
            close();
        }
    }
    
    /**
     * Cập nhật số lượng mục trong giỏ
     */
    public int updateCartQuantity(int cartId, int quantity) {
        open();
        try {
            ContentValues values = new ContentValues();
            values.put(DatabaseHelper.COLUMN_CART_QUANTITY, quantity);
            
            return db.update(
                    DatabaseHelper.TABLE_CART_ITEMS,
                    values,
                    DatabaseHelper.COLUMN_CART_ID + " = ?",
                    new String[]{String.valueOf(cartId)}
            );
        } finally {
            close();
        }
    }
    
    /**
     * Cập nhật mục giỏ hàng
     */
    public int updateCartItem(CartItem cartItem) {
        open();
        try {
            ContentValues values = new ContentValues();
            values.put(DatabaseHelper.COLUMN_CART_QUANTITY, cartItem.getQuantity());
            values.put(DatabaseHelper.COLUMN_CART_PRICE, cartItem.getPrice());
            values.put(DatabaseHelper.COLUMN_CART_NOTES, cartItem.getNotes());
            
            return db.update(
                    DatabaseHelper.TABLE_CART_ITEMS,
                    values,
                    DatabaseHelper.COLUMN_CART_ID + " = ?",
                    new String[]{String.valueOf(cartItem.getCartId())}
            );
        } finally {
            close();
        }
    }
    
    /**
     * Xóa mục khỏi giỏ hàng
     */
    public int removeFromCart(int cartId) {
        open();
        try {
            return db.delete(
                    DatabaseHelper.TABLE_CART_ITEMS,
                    DatabaseHelper.COLUMN_CART_ID + " = ?",
                    new String[]{String.valueOf(cartId)}
            );
        } finally {
            close();
        }
    }
    
    /**
     * Xóa toàn bộ giỏ hàng của người dùng
     */
    public int clearUserCart(int userId) {
        open();
        try {
            return db.delete(
                    DatabaseHelper.TABLE_CART_ITEMS,
                    DatabaseHelper.COLUMN_CART_USER_ID + " = ?",
                    new String[]{String.valueOf(userId)}
            );
        } finally {
            close();
        }
    }
    
    /**
     * Tính tổng giá của giỏ hàng
     */
    public double getCartTotal(int userId) {
        open();
        try {
            Cursor cursor = db.rawQuery(
                    "SELECT SUM(" + DatabaseHelper.COLUMN_CART_PRICE + " * " + 
                    DatabaseHelper.COLUMN_CART_QUANTITY + ") FROM " + 
                    DatabaseHelper.TABLE_CART_ITEMS + " WHERE " + 
                    DatabaseHelper.COLUMN_CART_USER_ID + " = ?",
                    new String[]{String.valueOf(userId)}
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
     * Lấy số lượng mục trong giỏ
     */
    public int getCartItemCount(int userId) {
        open();
        try {
            Cursor cursor = db.rawQuery(
                    "SELECT COUNT(*) FROM " + DatabaseHelper.TABLE_CART_ITEMS + 
                    " WHERE " + DatabaseHelper.COLUMN_CART_USER_ID + " = ?",
                    new String[]{String.valueOf(userId)}
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
     * Lấy tổng số lượng sản phẩm trong giỏ
     */
    public int getTotalQuantityInCart(int userId) {
        open();
        try {
            Cursor cursor = db.rawQuery(
                    "SELECT SUM(" + DatabaseHelper.COLUMN_CART_QUANTITY + ") FROM " + 
                    DatabaseHelper.TABLE_CART_ITEMS + " WHERE " + 
                    DatabaseHelper.COLUMN_CART_USER_ID + " = ?",
                    new String[]{String.valueOf(userId)}
            );
            
            int total = 0;
            if (cursor != null) {
                cursor.moveToFirst();
                total = cursor.getInt(0);
                cursor.close();
            }
            return total;
        } finally {
            close();
        }
    }
    
    /**
     * Chuyển Cursor thành đối tượng CartItem
     */
    private CartItem cursorToCartItem(Cursor cursor) {
        CartItem item = new CartItem();
        item.setCartId(cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_CART_ID)));
        item.setUserId(cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_CART_USER_ID)));
        item.setPizzaId(cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_CART_PIZZA_ID)));
        item.setQuantity(cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_CART_QUANTITY)));
        item.setPrice(cursor.getDouble(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_CART_PRICE)));
        item.setNotes(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_CART_NOTES)));
        item.setAddedAt(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_CART_ADDED_AT)));
        return item;
    }
}
