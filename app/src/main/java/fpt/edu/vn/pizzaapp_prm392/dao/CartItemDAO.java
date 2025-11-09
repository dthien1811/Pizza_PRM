package fpt.edu.vn.pizzaapp_prm392.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

import fpt.edu.vn.pizzaapp_prm392.database.DatabaseHelper;
import fpt.edu.vn.pizzaapp_prm392.models.CartItem;

public class CartItemDAO {

    private final DatabaseHelper dbHelper; // dùng singleton
    private SQLiteDatabase db;

    public CartItemDAO(Context context) {
        // rất quan trọng: dùng getInstance để tránh mở nhiều DB rồi đóng nhầm
        this.dbHelper = DatabaseHelper.getInstance(context);
    }

    /** Mở DB nếu chưa mở */
    public void open() {
        if (db == null || !db.isOpen()) {
            db = dbHelper.getWritableDatabase();
        }
    }

    /** Đóng DB nếu đang mở (chỉ nên gọi ở public API top-level) */
    public void close() {
        if (db != null && db.isOpen()) {
            db.close();
        }
    }

    /**
     * Thêm mục vào giỏ (nếu đã có cùng userId + pizzaId thì cộng dồn quantity)
     * Top-level API: tự open/close
     */
    public long addToCart(CartItem cartItem) {
        open();
        try {
            CartItem existing = getCartItemByUserAndPizza_NoOpenClose(
                    cartItem.getUserId(), cartItem.getPizzaId());

            if (existing != null) {
                existing.setQuantity(existing.getQuantity() + cartItem.getQuantity());
                updateCartItem_NoOpenClose(existing);
                return existing.getCartId(); // trả lại id của dòng hiện có
            } else {
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

    /** Lấy danh sách giỏ theo userId (top-level API) */
    public List<CartItem> getCartByUser(int userId) {
        open();
        List<CartItem> list = new ArrayList<>();
        try (Cursor cursor = db.query(
                DatabaseHelper.TABLE_CART_ITEMS,
                null,
                DatabaseHelper.COLUMN_CART_USER_ID + "=?",
                new String[]{String.valueOf(userId)},
                null, null, null
        )) {
            if (cursor != null) {
                while (cursor.moveToNext()) {
                    list.add(cursorToCartItem(cursor));
                }
            }
            return list;
        } finally {
            close();
        }
    }

    /** Lấy item theo cartId (top-level API) */
    public CartItem getCartItemById(int cartId) {
        open();
        try (Cursor cursor = db.query(
                DatabaseHelper.TABLE_CART_ITEMS,
                null,
                DatabaseHelper.COLUMN_CART_ID + "=?",
                new String[]{String.valueOf(cartId)},
                null, null, null
        )) {
            if (cursor != null && cursor.moveToFirst()) {
                return cursorToCartItem(cursor);
            }
            return null;
        } finally {
            close();
        }
    }

    /** Cập nhật số lượng (top-level API) */
    public int updateCartQuantity(int cartId, int quantity) {
        open();
        try {
            ContentValues values = new ContentValues();
            values.put(DatabaseHelper.COLUMN_CART_QUANTITY, quantity);
            return db.update(
                    DatabaseHelper.TABLE_CART_ITEMS,
                    values,
                    DatabaseHelper.COLUMN_CART_ID + "=?",
                    new String[]{String.valueOf(cartId)}
            );
        } finally {
            close();
        }
    }

    /** Cập nhật toàn bộ item (top-level API) */
    public int updateCartItem(CartItem cartItem) {
        open();
        try {
            return updateCartItem_NoOpenClose(cartItem);
        } finally {
            close();
        }
    }

    /** Xoá 1 dòng khỏi giỏ (top-level API) */
    public int removeFromCart(int cartId) {
        open();
        try {
            return db.delete(
                    DatabaseHelper.TABLE_CART_ITEMS,
                    DatabaseHelper.COLUMN_CART_ID + "=?",
                    new String[]{String.valueOf(cartId)}
            );
        } finally {
            close();
        }
    }

    /** Xoá toàn bộ giỏ của user (top-level API) */
    public int clearUserCart(int userId) {
        open();
        try {
            return db.delete(
                    DatabaseHelper.TABLE_CART_ITEMS,
                    DatabaseHelper.COLUMN_CART_USER_ID + "=?",
                    new String[]{String.valueOf(userId)}
            );
        } finally {
            close();
        }
    }

    /** Tổng tiền giỏ hàng (top-level API) */
    public double getCartTotal(int userId) {
        open();
        try (Cursor c = db.rawQuery(
                "SELECT SUM(" + DatabaseHelper.COLUMN_CART_PRICE + " * " + DatabaseHelper.COLUMN_CART_QUANTITY + ") " +
                        "FROM " + DatabaseHelper.TABLE_CART_ITEMS + " WHERE " + DatabaseHelper.COLUMN_CART_USER_ID + "=?",
                new String[]{String.valueOf(userId)}
        )) {
            if (c != null && c.moveToFirst()) {
                return c.getDouble(0);
            }
            return 0;
        } finally {
            close();
        }
    }

    /** Số dòng trong giỏ (top-level API) */
    public int getCartItemCount(int userId) {
        open();
        try (Cursor c = db.rawQuery(
                "SELECT COUNT(*) FROM " + DatabaseHelper.TABLE_CART_ITEMS +
                        " WHERE " + DatabaseHelper.COLUMN_CART_USER_ID + "=?",
                new String[]{String.valueOf(userId)}
        )) {
            if (c != null && c.moveToFirst()) {
                return c.getInt(0);
            }
            return 0;
        } finally {
            close();
        }
    }

    /** Tổng số lượng (sum quantity) (top-level API) */
    public int getTotalQuantityInCart(int userId) {
        open();
        try (Cursor c = db.rawQuery(
                "SELECT SUM(" + DatabaseHelper.COLUMN_CART_QUANTITY + ") FROM " + DatabaseHelper.TABLE_CART_ITEMS +
                        " WHERE " + DatabaseHelper.COLUMN_CART_USER_ID + "=?",
                new String[]{String.valueOf(userId)}
        )) {
            if (c != null && c.moveToFirst()) {
                return c.getInt(0);
            }
            return 0;
        } finally {
            close();
        }
    }

    /* ================== HÀM INTERNAL: KHÔNG open()/close() ================== */

    /** Lấy item theo (userId, pizzaId) để cộng dồn — KHÔNG open/close */
    private CartItem getCartItemByUserAndPizza_NoOpenClose(int userId, int pizzaId) {
        try (Cursor cursor = db.query(
                DatabaseHelper.TABLE_CART_ITEMS,
                null,
                DatabaseHelper.COLUMN_CART_USER_ID + "=? AND " +
                        DatabaseHelper.COLUMN_CART_PIZZA_ID + "=?",
                new String[]{String.valueOf(userId), String.valueOf(pizzaId)},
                null, null, null
        )) {
            if (cursor != null && cursor.moveToFirst()) {
                return cursorToCartItem(cursor);
            }
            return null;
        }
    }

    /** Update item khi đã có DB mở sẵn — KHÔNG open/close */
    private int updateCartItem_NoOpenClose(CartItem cartItem) {
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.COLUMN_CART_QUANTITY, cartItem.getQuantity());
        values.put(DatabaseHelper.COLUMN_CART_PRICE, cartItem.getPrice());
        values.put(DatabaseHelper.COLUMN_CART_NOTES, cartItem.getNotes());
        return db.update(
                DatabaseHelper.TABLE_CART_ITEMS,
                values,
                DatabaseHelper.COLUMN_CART_ID + "=?",
                new String[]{String.valueOf(cartItem.getCartId())}
        );
    }

    /* ================== Helper ================== */
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
