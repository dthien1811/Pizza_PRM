package fpt.edu.vn.pizzaapp_prm392.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import fpt.edu.vn.pizzaapp_prm392.database.DatabaseHelper;
import fpt.edu.vn.pizzaapp_prm392.models.Pizza;

/**
 * PizzaDAO - Thao tác dữ liệu sản phẩm Pizza
 */
public class PizzaDAO {
    
    private DatabaseHelper dbHelper;
    private SQLiteDatabase db;
    
    public PizzaDAO(Context context) {
        dbHelper = new DatabaseHelper(context);
    }
    
    /**
     * Mở kết nối cơ sở dữ liệu
     */
    public void open() {
        db = dbHelper.getReadableDatabase();
    }
    
    /**
     * Đóng kết nối cơ sở dữ liệu
     */
    public void close() {
        dbHelper.close();
    }
    
    /**
     * Thêm pizza mới
     */
    public long addPizza(Pizza pizza) {
        open();
        try {
            ContentValues values = new ContentValues();
            values.put(DatabaseHelper.COLUMN_PIZZA_NAME, pizza.getName());
            values.put(DatabaseHelper.COLUMN_PIZZA_DESCRIPTION, pizza.getDescription());
            values.put(DatabaseHelper.COLUMN_PIZZA_PRICE, pizza.getPrice());
            values.put(DatabaseHelper.COLUMN_PIZZA_IMAGE, pizza.getImage());
            values.put(DatabaseHelper.COLUMN_PIZZA_SIZE, pizza.getSize());
            values.put(DatabaseHelper.COLUMN_PIZZA_CATEGORY, pizza.getCategory());
            values.put(DatabaseHelper.COLUMN_PIZZA_STOCK, pizza.getStock());
            
            return db.insert(DatabaseHelper.TABLE_PIZZAS, null, values);
        } finally {
            close();
        }
    }
    
    /**
     * Lấy tất cả pizza
     */
    public List<Pizza> getAllPizzas() {
        open();
        List<Pizza> pizzas = new ArrayList<>();
        try {
            Cursor cursor = db.query(
                    DatabaseHelper.TABLE_PIZZAS,
                    null, null, null, null, null, null
            );
            
            if (cursor != null) {
                while (cursor.moveToNext()) {
                    pizzas.add(cursorToPizza(cursor));
                }
                cursor.close();
            }
        } finally {
            close();
        }
        return pizzas;
    }
    
    /**
     * Lấy pizza theo ID
     */
    public Pizza getPizzaById(int pizzaId) {
        open();
        try {
            Cursor cursor = db.query(
                    DatabaseHelper.TABLE_PIZZAS,
                    null,
                    DatabaseHelper.COLUMN_PIZZA_ID + " = ?",
                    new String[]{String.valueOf(pizzaId)},
                    null, null, null
            );
            
            if (cursor != null && cursor.moveToFirst()) {
                Pizza pizza = cursorToPizza(cursor);
                cursor.close();
                return pizza;
            }
            return null;
        } finally {
            close();
        }
    }
    
    /**
     * Tìm kiếm pizza theo tên
     */
    public List<Pizza> searchPizzaByName(String name) {
        open();
        List<Pizza> pizzas = new ArrayList<>();
        try {
            Cursor cursor = db.query(
                    DatabaseHelper.TABLE_PIZZAS,
                    null,
                    DatabaseHelper.COLUMN_PIZZA_NAME + " LIKE ?",
                    new String[]{"%" + name + "%"},
                    null, null, null
            );
            
            if (cursor != null) {
                while (cursor.moveToNext()) {
                    pizzas.add(cursorToPizza(cursor));
                }
                cursor.close();
            }
        } finally {
            close();
        }
        return pizzas;
    }
    
    /**
     * Lấy pizza theo danh mục
     */
    public List<Pizza> getPizzasByCategory(String category) {
        open();
        List<Pizza> pizzas = new ArrayList<>();
        try {
            Cursor cursor = db.query(
                    DatabaseHelper.TABLE_PIZZAS,
                    null,
                    DatabaseHelper.COLUMN_PIZZA_CATEGORY + " = ?",
                    new String[]{category},
                    null, null, null
            );
            
            if (cursor != null) {
                while (cursor.moveToNext()) {
                    pizzas.add(cursorToPizza(cursor));
                }
                cursor.close();
            }
        } finally {
            close();
        }
        return pizzas;
    }
    
    /**
     * Lấy pizza theo kích thước
     */
    public List<Pizza> getPizzasBySize(String size) {
        open();
        List<Pizza> pizzas = new ArrayList<>();
        try {
            Cursor cursor = db.query(
                    DatabaseHelper.TABLE_PIZZAS,
                    null,
                    DatabaseHelper.COLUMN_PIZZA_SIZE + " = ?",
                    new String[]{size},
                    null, null, null
            );
            
            if (cursor != null) {
                while (cursor.moveToNext()) {
                    pizzas.add(cursorToPizza(cursor));
                }
                cursor.close();
            }
        } finally {
            close();
        }
        return pizzas;
    }
    
    /**
     * Lấy pizza theo giá (từ - đến)
     */
    public List<Pizza> getPizzasByPrice(double minPrice, double maxPrice) {
        open();
        List<Pizza> pizzas = new ArrayList<>();
        try {
            Cursor cursor = db.query(
                    DatabaseHelper.TABLE_PIZZAS,
                    null,
                    DatabaseHelper.COLUMN_PIZZA_PRICE + " BETWEEN ? AND ?",
                    new String[]{String.valueOf(minPrice), String.valueOf(maxPrice)},
                    null, null, null
            );
            
            if (cursor != null) {
                while (cursor.moveToNext()) {
                    pizzas.add(cursorToPizza(cursor));
                }
                cursor.close();
            }
        } finally {
            close();
        }
        return pizzas;
    }
    
    /**
     * Lấy pizza được đánh giá cao nhất
     */
    public List<Pizza> getTopRatedPizzas(int limit) {
        open();
        List<Pizza> pizzas = new ArrayList<>();
        try {
            Cursor cursor = db.query(
                    DatabaseHelper.TABLE_PIZZAS,
                    null,
                    null, null, null, null,
                    DatabaseHelper.COLUMN_PIZZA_RATING + " DESC",
                    String.valueOf(limit)
            );
            
            if (cursor != null) {
                while (cursor.moveToNext()) {
                    pizzas.add(cursorToPizza(cursor));
                }
                cursor.close();
            }
        } finally {
            close();
        }
        return pizzas;
    }
    
    /**
     * Cập nhật thông tin pizza
     */
    public int updatePizza(Pizza pizza) {
        open();
        try {
            ContentValues values = new ContentValues();
            values.put(DatabaseHelper.COLUMN_PIZZA_NAME, pizza.getName());
            values.put(DatabaseHelper.COLUMN_PIZZA_DESCRIPTION, pizza.getDescription());
            values.put(DatabaseHelper.COLUMN_PIZZA_PRICE, pizza.getPrice());
            values.put(DatabaseHelper.COLUMN_PIZZA_IMAGE, pizza.getImage());
            values.put(DatabaseHelper.COLUMN_PIZZA_SIZE, pizza.getSize());
            values.put(DatabaseHelper.COLUMN_PIZZA_CATEGORY, pizza.getCategory());
            values.put(DatabaseHelper.COLUMN_PIZZA_RATING, pizza.getRating());
            values.put(DatabaseHelper.COLUMN_PIZZA_STOCK, pizza.getStock());
            
            return db.update(
                    DatabaseHelper.TABLE_PIZZAS,
                    values,
                    DatabaseHelper.COLUMN_PIZZA_ID + " = ?",
                    new String[]{String.valueOf(pizza.getPizzaId())}
            );
        } finally {
            close();
        }
    }
    
    /**
     * Cập nhật rating pizza
     */
    public int updatePizzaRating(int pizzaId, double rating) {
        open();
        try {
            ContentValues values = new ContentValues();
            values.put(DatabaseHelper.COLUMN_PIZZA_RATING, rating);
            
            return db.update(
                    DatabaseHelper.TABLE_PIZZAS,
                    values,
                    DatabaseHelper.COLUMN_PIZZA_ID + " = ?",
                    new String[]{String.valueOf(pizzaId)}
            );
        } finally {
            close();
        }
    }
    
    /**
     * Cập nhật tồn kho pizza
     */
    public int updatePizzaStock(int pizzaId, int stock) {
        open();
        try {
            ContentValues values = new ContentValues();
            values.put(DatabaseHelper.COLUMN_PIZZA_STOCK, stock);
            
            return db.update(
                    DatabaseHelper.TABLE_PIZZAS,
                    values,
                    DatabaseHelper.COLUMN_PIZZA_ID + " = ?",
                    new String[]{String.valueOf(pizzaId)}
            );
        } finally {
            close();
        }
    }
    
    /**
     * Xóa pizza
     */
    public int deletePizza(int pizzaId) {
        open();
        try {
            return db.delete(
                    DatabaseHelper.TABLE_PIZZAS,
                    DatabaseHelper.COLUMN_PIZZA_ID + " = ?",
                    new String[]{String.valueOf(pizzaId)}
            );
        } finally {
            close();
        }
    }
    
    /**
     * Lấy số lượng pizza
     */
    public int getPizzaCount() {
        open();
        try {
            Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM " + DatabaseHelper.TABLE_PIZZAS, null);
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
     * Chuyển Cursor thành đối tượng Pizza
     */
    private Pizza cursorToPizza(Cursor cursor) {
        Pizza pizza = new Pizza();
        pizza.setPizzaId(cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_PIZZA_ID)));
        pizza.setName(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_PIZZA_NAME)));
        pizza.setDescription(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_PIZZA_DESCRIPTION)));
        pizza.setPrice(cursor.getDouble(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_PIZZA_PRICE)));
        pizza.setImage(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_PIZZA_IMAGE)));
        pizza.setSize(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_PIZZA_SIZE)));
        pizza.setCategory(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_PIZZA_CATEGORY)));
        pizza.setRating(cursor.getDouble(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_PIZZA_RATING)));
        pizza.setStock(cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_PIZZA_STOCK)));
        return pizza;
    }

    // ----------------------------------- -------------------------------------------

    public List<String> getAllCategories() {
        db = dbHelper.getReadableDatabase();
        List<String> list = new ArrayList<>();

        String sql = "SELECT DISTINCT " +
                DatabaseHelper.COLUMN_PIZZA_CATEGORY +
                " FROM " + DatabaseHelper.TABLE_PIZZAS +
                " WHERE " + DatabaseHelper.COLUMN_PIZZA_CATEGORY +
                " IS NOT NULL ORDER BY " +
                DatabaseHelper.COLUMN_PIZZA_CATEGORY;

        Cursor c = db.rawQuery(sql, null);

        while (c.moveToNext()) {
            list.add(c.getString(c.getColumnIndexOrThrow(DatabaseHelper.COLUMN_PIZZA_CATEGORY)));
        }
        c.close();
        db.close();
        return list;
    }

    public void insertOrUpdate(List<Pizza> pizzas) {
        db = dbHelper.getWritableDatabase();
        db.beginTransaction();
        try {
            for (Pizza p : pizzas) {
                ContentValues cv = new ContentValues();
                cv.put(DatabaseHelper.COLUMN_PIZZA_ID, p.getPizzaId());
                cv.put(DatabaseHelper.COLUMN_PIZZA_NAME, p.getName());
                cv.put(DatabaseHelper.COLUMN_PIZZA_DESCRIPTION, p.getDescription());
                cv.put(DatabaseHelper.COLUMN_PIZZA_PRICE, p.getPrice());
                cv.put(DatabaseHelper.COLUMN_PIZZA_IMAGE, p.getImage());
                cv.put(DatabaseHelper.COLUMN_PIZZA_CATEGORY, p.getCategory());
                cv.put(DatabaseHelper.COLUMN_PIZZA_RATING, p.getRating());

                if (p.getStock() != 0)
                    cv.put(DatabaseHelper.COLUMN_PIZZA_STOCK, p.getStock());

                if (p.getSize() != null)
                    cv.put(DatabaseHelper.COLUMN_PIZZA_SIZE, p.getSize());

                long result = db.insertWithOnConflict(DatabaseHelper.TABLE_PIZZAS, null, cv, SQLiteDatabase.CONFLICT_REPLACE);
                Log.d("DAO", "Insert pizzaId=" + p.getPizzaId() + " → result=" + result);
            }
            db.setTransactionSuccessful();
            Log.d("DAO", "THÀNH CÔNG: Lưu " + pizzas.size() + " pizza");
        } catch (Exception e) {
            Log.e("DAO", "LỖI LƯU DB: " + e.getMessage());
        }
        finally {
            db.endTransaction();
            db.close();
        }
    }

    public List<Pizza> search(String query, Double minPrice, Double maxPrice, String category, int page, int size) {
        db = dbHelper.getReadableDatabase();

        String sql = "SELECT * FROM " + DatabaseHelper.TABLE_PIZZAS + " WHERE 1=1";
        List<String> args = new ArrayList<>();

        if (query != null && !query.isEmpty()) {
            sql += " AND name LIKE ?";
            args.add("%" + query + "%");
        }
        if (minPrice != null) {
            sql += " AND price >= ?";
            args.add(String.valueOf(minPrice));
        }
        if (maxPrice != null && maxPrice < Double.MAX_VALUE) {
            sql += " AND price <= ?";
            args.add(String.valueOf(maxPrice));
        }
        if (category != null && !category.isEmpty()) {
            sql += " AND category = ?";
            args.add(category);
        }
        sql += " ORDER BY name LIMIT ? OFFSET ?";
        args.add(String.valueOf(size));
        args.add(String.valueOf((page - 1) * size));


        Cursor c = db.rawQuery(sql, args.toArray(new String[0]));
        List<Pizza> list = new ArrayList<>();

        while (c.moveToNext()) {
            Pizza p = new Pizza();
            p.setPizzaId(c.getInt(c.getColumnIndexOrThrow(DatabaseHelper.COLUMN_PIZZA_ID)));
            p.setName(c.getString(c.getColumnIndexOrThrow(DatabaseHelper.COLUMN_PIZZA_NAME)));
            p.setDescription(c.getString(c.getColumnIndexOrThrow(DatabaseHelper.COLUMN_PIZZA_DESCRIPTION)));
            p.setPrice(c.getDouble(c.getColumnIndexOrThrow(DatabaseHelper.COLUMN_PIZZA_PRICE)));
            p.setImage(c.getString(c.getColumnIndexOrThrow(DatabaseHelper.COLUMN_PIZZA_IMAGE)));
            p.setCategory(c.getString(c.getColumnIndexOrThrow(DatabaseHelper.COLUMN_PIZZA_CATEGORY)));
            p.setRating(c.getDouble(c.getColumnIndexOrThrow(DatabaseHelper.COLUMN_PIZZA_RATING)));

            p.setSize(c.getString(c.getColumnIndexOrThrow(DatabaseHelper.COLUMN_PIZZA_SIZE)));
            p.setStock(c.getInt(c.getColumnIndexOrThrow(DatabaseHelper.COLUMN_PIZZA_STOCK)));

            list.add(p);
        }
        c.close();
        db.close();
        return list;
    }

    public List<String> getSuggestions(String query) {
        db = dbHelper.getReadableDatabase();
        List<String> list = new ArrayList<>();
        String sql = "SELECT name FROM pizzas WHERE name LIKE ? LIMIT 5";
        Cursor c = db.rawQuery(sql, new String[]{"%" + query + "%"});
        while (c.moveToNext()) {
            list.add(c.getString(c.getColumnIndexOrThrow(DatabaseHelper.COLUMN_PIZZA_NAME)));
        }
        c.close();
        db.close();
        return list;
    }
}
