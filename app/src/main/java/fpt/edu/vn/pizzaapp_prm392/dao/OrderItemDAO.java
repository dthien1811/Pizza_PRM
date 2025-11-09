package fpt.edu.vn.pizzaapp_prm392.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

import fpt.edu.vn.pizzaapp_prm392.database.DatabaseHelper;
import fpt.edu.vn.pizzaapp_prm392.models.CartItem;
import fpt.edu.vn.pizzaapp_prm392.models.OrderItem;

public class OrderItemDAO {

    private final DatabaseHelper dbHelper;

    public OrderItemDAO(Context ctx) {
        this.dbHelper = DatabaseHelper.getInstance(ctx);
    }

    /** Thêm 1 dòng item */
    public long insert(OrderItem item) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        try {
            ContentValues v = new ContentValues();
            v.put(DatabaseHelper.COLUMN_ORDER_ITEM_ORDER_ID, item.getOrderId());
            v.put(DatabaseHelper.COLUMN_ORDER_ITEM_PIZZA_ID, item.getPizzaId());
            v.put(DatabaseHelper.COLUMN_ORDER_ITEM_QUANTITY, item.getQuantity());
            v.put(DatabaseHelper.COLUMN_ORDER_ITEM_PRICE, item.getPrice());
            return db.insert(DatabaseHelper.TABLE_ORDER_ITEMS, null, v);
        } finally {
            db.close();
        }
    }

    /** Thêm nhiều item từ giỏ hàng (dùng khi đã có orderId) */
    public void insertFromCart(int orderId, List<CartItem> cart) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.beginTransaction();
        try {
            for (CartItem c : cart) {
                ContentValues v = new ContentValues();
                v.put(DatabaseHelper.COLUMN_ORDER_ITEM_ORDER_ID, orderId);
                v.put(DatabaseHelper.COLUMN_ORDER_ITEM_PIZZA_ID, c.getPizzaId());
                v.put(DatabaseHelper.COLUMN_ORDER_ITEM_QUANTITY, c.getQuantity());
                v.put(DatabaseHelper.COLUMN_ORDER_ITEM_PRICE, c.getPrice());
                db.insert(DatabaseHelper.TABLE_ORDER_ITEMS, null, v);
            }
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
            db.close();
        }
    }

    /** Lấy danh sách item trong 1 đơn */
    public List<OrderItem> getByOrderId(int orderId) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        List<OrderItem> list = new ArrayList<>();
        try (Cursor c = db.query(
                DatabaseHelper.TABLE_ORDER_ITEMS,
                null,
                DatabaseHelper.COLUMN_ORDER_ITEM_ORDER_ID + "=?",
                new String[]{String.valueOf(orderId)},
                null, null, null
        )) {
            while (c != null && c.moveToNext()) {
                OrderItem oi = new OrderItem();
                oi.setOrderItemId(c.getInt(c.getColumnIndexOrThrow(DatabaseHelper.COLUMN_ORDER_ITEM_ID)));
                oi.setOrderId(c.getInt(c.getColumnIndexOrThrow(DatabaseHelper.COLUMN_ORDER_ITEM_ORDER_ID)));
                oi.setPizzaId(c.getInt(c.getColumnIndexOrThrow(DatabaseHelper.COLUMN_ORDER_ITEM_PIZZA_ID)));
                oi.setQuantity(c.getInt(c.getColumnIndexOrThrow(DatabaseHelper.COLUMN_ORDER_ITEM_QUANTITY)));
                oi.setPrice(c.getDouble(c.getColumnIndexOrThrow(DatabaseHelper.COLUMN_ORDER_ITEM_PRICE)));
                list.add(oi);
            }
        }
        db.close();
        return list;
    }
}
