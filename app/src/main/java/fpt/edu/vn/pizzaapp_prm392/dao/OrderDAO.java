package fpt.edu.vn.pizzaapp_prm392.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

import fpt.edu.vn.pizzaapp_prm392.database.DatabaseHelper;
import fpt.edu.vn.pizzaapp_prm392.models.Order;
import java.util.List;
import java.util.UUID;

import fpt.edu.vn.pizzaapp_prm392.models.CartItem;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;


/**
 * OrderDAO - Thao tác dữ liệu đơn hàng
 */
public class OrderDAO {
    
    private DatabaseHelper dbHelper;
    private SQLiteDatabase db;
    
    public OrderDAO(Context context) {
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
     * Tạo đơn hàng mới
     */
    public long createOrder(Order order) {
        open();
        try {
            ContentValues values = new ContentValues();
            values.put(DatabaseHelper.COLUMN_ORDER_CODE, order.getOrderCode());
            values.put(DatabaseHelper.COLUMN_ORDER_USER_ID, order.getUserId());
            values.put(DatabaseHelper.COLUMN_ORDER_TOTAL_PRICE, order.getTotalPrice());
            values.put(DatabaseHelper.COLUMN_ORDER_ADDRESS, order.getAddress());
            values.put(DatabaseHelper.COLUMN_ORDER_PHONE, order.getPhone());
            values.put(DatabaseHelper.COLUMN_ORDER_NOTES, order.getNotes());
            values.put(DatabaseHelper.COLUMN_ORDER_STATUS, order.getStatus());
            values.put(DatabaseHelper.COLUMN_ORDER_PAYMENT_METHOD, order.getPaymentMethod());
            
            return db.insert(DatabaseHelper.TABLE_ORDERS, null, values);
        } finally {
            close();
        }
    }
    
    /**
     * Lấy đơn hàng theo ID
     */
    public Order getOrderById(int orderId) {
        open();
        try {
            Cursor cursor = db.query(
                    DatabaseHelper.TABLE_ORDERS,
                    null,
                    DatabaseHelper.COLUMN_ORDER_ID + " = ?",
                    new String[]{String.valueOf(orderId)},
                    null, null, null
            );
            
            if (cursor != null && cursor.moveToFirst()) {
                Order order = cursorToOrder(cursor);
                cursor.close();
                return order;
            }
            return null;
        } finally {
            close();
        }
    }
    
    /**
     * Lấy đơn hàng theo mã đơn hàng
     */
    public Order getOrderByCode(String orderCode) {
        open();
        try {
            Cursor cursor = db.query(
                    DatabaseHelper.TABLE_ORDERS,
                    null,
                    DatabaseHelper.COLUMN_ORDER_CODE + " = ?",
                    new String[]{orderCode},
                    null, null, null
            );
            
            if (cursor != null && cursor.moveToFirst()) {
                Order order = cursorToOrder(cursor);
                cursor.close();
                return order;
            }
            return null;
        } finally {
            close();
        }
    }
    
    /**
     * Lấy tất cả đơn hàng của người dùng
     */
    public List<Order> getOrdersByUser(int userId) {
        open();
        List<Order> orders = new ArrayList<>();
        try {
            Cursor cursor = db.query(
                    DatabaseHelper.TABLE_ORDERS,
                    null,
                    DatabaseHelper.COLUMN_ORDER_USER_ID + " = ?",
                    new String[]{String.valueOf(userId)},
                    null, null,
                    DatabaseHelper.COLUMN_ORDER_CREATED_AT + " DESC"
            );
            
            if (cursor != null) {
                while (cursor.moveToNext()) {
                    orders.add(cursorToOrder(cursor));
                }
                cursor.close();
            }
        } finally {
            close();
        }
        return orders;
    }
    
    /**
     * Lấy tất cả đơn hàng
     */
    public List<Order> getAllOrders() {
        open();
        List<Order> orders = new ArrayList<>();
        try {
            Cursor cursor = db.query(
                    DatabaseHelper.TABLE_ORDERS,
                    null, null, null, null, null,
                    DatabaseHelper.COLUMN_ORDER_CREATED_AT + " DESC"
            );
            
            if (cursor != null) {
                while (cursor.moveToNext()) {
                    orders.add(cursorToOrder(cursor));
                }
                cursor.close();
            }
        } finally {
            close();
        }
        return orders;
    }
    
    /**
     * Lấy đơn hàng theo trạng thái
     */
    public List<Order> getOrdersByStatus(String status) {
        open();
        List<Order> orders = new ArrayList<>();
        try {
            Cursor cursor = db.query(
                    DatabaseHelper.TABLE_ORDERS,
                    null,
                    DatabaseHelper.COLUMN_ORDER_STATUS + " = ?",
                    new String[]{status},
                    null, null,
                    DatabaseHelper.COLUMN_ORDER_CREATED_AT + " DESC"
            );
            
            if (cursor != null) {
                while (cursor.moveToNext()) {
                    orders.add(cursorToOrder(cursor));
                }
                cursor.close();
            }
        } finally {
            close();
        }
        return orders;
    }
    
    /**
     * Cập nhật trạng thái đơn hàng
     */
    public int updateOrderStatus(int orderId, String status) {
        open();
        try {
            ContentValues values = new ContentValues();
            values.put(DatabaseHelper.COLUMN_ORDER_STATUS, status);
            values.put(DatabaseHelper.COLUMN_ORDER_UPDATED_AT, nowString());

            return db.update(
                    DatabaseHelper.TABLE_ORDERS,
                    values,
                    DatabaseHelper.COLUMN_ORDER_ID + " = ?",
                    new String[]{String.valueOf(orderId)}
            );
        } finally {
            close();
        }
    }
    
    /**
     * Cập nhật đơn hàng
     */
    public int updateOrder(Order order) {
        open();
        try {
            ContentValues values = new ContentValues();
            values.put(DatabaseHelper.COLUMN_ORDER_ADDRESS, order.getAddress());
            values.put(DatabaseHelper.COLUMN_ORDER_PHONE, order.getPhone());
            values.put(DatabaseHelper.COLUMN_ORDER_NOTES, order.getNotes());
            values.put(DatabaseHelper.COLUMN_ORDER_STATUS, order.getStatus());
            values.put(DatabaseHelper.COLUMN_ORDER_PAYMENT_METHOD, order.getPaymentMethod());
            values.put(DatabaseHelper.COLUMN_ORDER_UPDATED_AT, nowString());

            return db.update(
                    DatabaseHelper.TABLE_ORDERS,
                    values,
                    DatabaseHelper.COLUMN_ORDER_ID + " = ?",
                    new String[]{String.valueOf(order.getOrderId())}
            );
        } finally {
            close();
        }
    }
    
    /**
     * Xóa đơn hàng
     */
    public int deleteOrder(int orderId) {
        open();
        try {
            return db.delete(
                    DatabaseHelper.TABLE_ORDERS,
                    DatabaseHelper.COLUMN_ORDER_ID + " = ?",
                    new String[]{String.valueOf(orderId)}
            );
        } finally {
            close();
        }
    }
    
    /**
     * Lấy số đơn hàng theo trạng thái
     */
    public int getOrderCountByStatus(String status) {
        open();
        try {
            Cursor cursor = db.rawQuery(
                    "SELECT COUNT(*) FROM " + DatabaseHelper.TABLE_ORDERS + 
                    " WHERE " + DatabaseHelper.COLUMN_ORDER_STATUS + " = ?",
                    new String[]{status}
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
     * Tính tổng doanh thu
     */
    public double getTotalRevenue() {
        open();
        try {
            Cursor cursor = db.rawQuery(
                    "SELECT SUM(" + DatabaseHelper.COLUMN_ORDER_TOTAL_PRICE + ") FROM " + 
                    DatabaseHelper.TABLE_ORDERS,
                    null
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
     * Chuyển Cursor thành đối tượng Order
     */
    private Order cursorToOrder(Cursor cursor) {
        Order order = new Order();
        order.setOrderId(cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_ORDER_ID)));
        order.setOrderCode(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_ORDER_CODE)));
        order.setUserId(cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_ORDER_USER_ID)));
        order.setTotalPrice(cursor.getDouble(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_ORDER_TOTAL_PRICE)));
        order.setAddress(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_ORDER_ADDRESS)));
        order.setPhone(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_ORDER_PHONE)));
        order.setNotes(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_ORDER_NOTES)));
        order.setStatus(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_ORDER_STATUS)));
        order.setPaymentMethod(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_ORDER_PAYMENT_METHOD)));
        order.setCreatedAt(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_ORDER_CREATED_AT)));
        order.setUpdatedAt(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_ORDER_UPDATED_AT)));
        return order;
    }

    public long createOrderFromCart(int userId,
                                    String name,
                                    String phone,
                                    String address,
                                    String notes,
                                    String paymentMethod,
                                    List<CartItem> cart,
                                    double totalPrice) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.beginTransaction();
        long orderId = -1;

        try {
            // 1) Insert orders
            ContentValues ov = new ContentValues();
            ov.put(DatabaseHelper.COLUMN_ORDER_CODE, genOrderCode());
            ov.put(DatabaseHelper.COLUMN_ORDER_USER_ID, userId);
            ov.put(DatabaseHelper.COLUMN_ORDER_TOTAL_PRICE, totalPrice);
            ov.put(DatabaseHelper.COLUMN_ORDER_ADDRESS, address);
            ov.put(DatabaseHelper.COLUMN_ORDER_PHONE, phone);
            ov.put(DatabaseHelper.COLUMN_ORDER_NOTES, notes);
            ov.put(DatabaseHelper.COLUMN_ORDER_STATUS, "PENDING");
            ov.put(DatabaseHelper.COLUMN_ORDER_PAYMENT_METHOD, paymentMethod);

            orderId = db.insert(DatabaseHelper.TABLE_ORDERS, null, ov);
            if (orderId <= 0) throw new RuntimeException("Insert orders failed");

            // 2) Insert order_items từ giỏ
            for (CartItem c : cart) {
                ContentValues iv = new ContentValues();
                iv.put(DatabaseHelper.COLUMN_ORDER_ITEM_ORDER_ID, orderId);
                iv.put(DatabaseHelper.COLUMN_ORDER_ITEM_PIZZA_ID, c.getPizzaId());
                iv.put(DatabaseHelper.COLUMN_ORDER_ITEM_QUANTITY, c.getQuantity());
                iv.put(DatabaseHelper.COLUMN_ORDER_ITEM_PRICE, c.getPrice());
                long r = db.insert(DatabaseHelper.TABLE_ORDER_ITEMS, null, iv);
                if (r <= 0) throw new RuntimeException("Insert order_items failed");
            }

            // 3) Xoá giỏ của user
            db.delete(DatabaseHelper.TABLE_CART_ITEMS,
                    DatabaseHelper.COLUMN_CART_USER_ID + "=?",
                    new String[]{String.valueOf(userId)});

            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
            db.close();
        }
        return orderId;
    }

    private String genOrderCode() {
        return "OD" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }

    private String nowString() {
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US).format(new Date());
    }

}
