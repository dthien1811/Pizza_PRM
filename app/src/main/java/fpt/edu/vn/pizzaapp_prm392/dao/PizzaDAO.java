package fpt.edu.vn.pizzaapp_prm392.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import fpt.edu.vn.pizzaapp_prm392.database.DatabaseHelper;
import fpt.edu.vn.pizzaapp_prm392.models.Pizza;

/** PizzaDAO – thao tác bảng pizzas (đồng bộ cách mở/đóng DB). */
public class PizzaDAO {

    private final DatabaseHelper dbHelper;   // singleton
    private SQLiteDatabase db;

    public PizzaDAO(Context context) {
        this.dbHelper = DatabaseHelper.getInstance(context);
    }

    /* ========== Open/Close chuẩn ========== */

    private void openReadable() {
        if (db == null || !db.isOpen()) db = dbHelper.getReadableDatabase();
    }

    private void openWritable() {
        if (db == null || !db.isOpen()) db = dbHelper.getWritableDatabase();
    }

    private void close() {
        if (db != null && db.isOpen()) db.close();
        db = null;
    }

    /* ========== CRUD cơ bản ========== */

    public long addPizza(Pizza pizza) {
        openWritable();
        try {
            ContentValues values = new ContentValues();
            values.put(DatabaseHelper.COLUMN_PIZZA_NAME, pizza.getName());
            values.put(DatabaseHelper.COLUMN_PIZZA_DESCRIPTION, pizza.getDescription());
            values.put(DatabaseHelper.COLUMN_PIZZA_PRICE, pizza.getPrice());
            values.put(DatabaseHelper.COLUMN_PIZZA_IMAGE, pizza.getImage());
            values.put(DatabaseHelper.COLUMN_PIZZA_SIZE, pizza.getSize());
            values.put(DatabaseHelper.COLUMN_PIZZA_CATEGORY, pizza.getCategory());
            values.put(DatabaseHelper.COLUMN_PIZZA_STOCK, pizza.getStock());
            values.put(DatabaseHelper.COLUMN_PIZZA_RATING, pizza.getRating());
            return db.insert(DatabaseHelper.TABLE_PIZZAS, null, values);
        } finally {
            close();
        }
    }

    public List<Pizza> getAllPizzas() {
        openReadable();
        List<Pizza> list = new ArrayList<>();
        try (Cursor c = db.query(DatabaseHelper.TABLE_PIZZAS, null,
                null, null, null, null, null)) {
            while (c != null && c.moveToNext()) list.add(cursorToPizza(c));
        } finally {
            close();
        }
        return list;
    }

    public Pizza getPizzaById(int pizzaId) {
        openReadable();
        try (Cursor c = db.query(
                DatabaseHelper.TABLE_PIZZAS,
                null,
                DatabaseHelper.COLUMN_PIZZA_ID + "=?",
                new String[]{String.valueOf(pizzaId)},
                null, null, null
        )) {
            if (c != null && c.moveToFirst()) return cursorToPizza(c);
            return null;
        } finally {
            close();
        }
    }

    /** Lấy nhiều pizza theo danh sách id (dùng cho Cart). */
    public Map<Integer, Pizza> getPizzasByIds(List<Integer> ids) {
        Map<Integer, Pizza> map = new HashMap<>();
        if (ids == null || ids.isEmpty()) return map;

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < ids.size(); i++) {
            if (i > 0) sb.append(',');
            sb.append('?');
        }
        String inClause = sb.toString();
        String sql = "SELECT * FROM " + DatabaseHelper.TABLE_PIZZAS +
                " WHERE " + DatabaseHelper.COLUMN_PIZZA_ID + " IN (" + inClause + ")";
        String[] args = ids.stream().map(String::valueOf).toArray(String[]::new);

        openReadable();
        try (Cursor c = db.rawQuery(sql, args)) {
            while (c != null && c.moveToNext()) {
                Pizza p = cursorToPizza(c);
                map.put(p.getPizzaId(), p);
            }
        } finally {
            close();
        }
        return map;
    }

    public List<Pizza> searchPizzaByName(String name) {
        openReadable();
        List<Pizza> list = new ArrayList<>();
        try (Cursor c = db.query(
                DatabaseHelper.TABLE_PIZZAS,
                null,
                DatabaseHelper.COLUMN_PIZZA_NAME + " LIKE ?",
                new String[]{"%" + name + "%"},
                null, null, null)) {
            while (c != null && c.moveToNext()) list.add(cursorToPizza(c));
        } finally { close(); }
        return list;
    }

    public List<Pizza> getPizzasByCategory(String category) {
        openReadable();
        List<Pizza> list = new ArrayList<>();
        try (Cursor c = db.query(
                DatabaseHelper.TABLE_PIZZAS,
                null,
                DatabaseHelper.COLUMN_PIZZA_CATEGORY + "=?",
                new String[]{category}, null, null, null)) {
            while (c != null && c.moveToNext()) list.add(cursorToPizza(c));
        } finally { close(); }
        return list;
    }

    public List<Pizza> getPizzasBySize(String size) {
        openReadable();
        List<Pizza> list = new ArrayList<>();
        try (Cursor c = db.query(
                DatabaseHelper.TABLE_PIZZAS,
                null,
                DatabaseHelper.COLUMN_PIZZA_SIZE + "=?",
                new String[]{size}, null, null, null)) {
            while (c != null && c.moveToNext()) list.add(cursorToPizza(c));
        } finally { close(); }
        return list;
    }

    public List<Pizza> getPizzasByPrice(double minPrice, double maxPrice) {
        openReadable();
        List<Pizza> list = new ArrayList<>();
        try (Cursor c = db.query(
                DatabaseHelper.TABLE_PIZZAS,
                null,
                DatabaseHelper.COLUMN_PIZZA_PRICE + " BETWEEN ? AND ?",
                new String[]{String.valueOf(minPrice), String.valueOf(maxPrice)},
                null, null, null)) {
            while (c != null && c.moveToNext()) list.add(cursorToPizza(c));
        } finally { close(); }
        return list;
    }

    public List<Pizza> getTopRatedPizzas(int limit) {
        openReadable();
        List<Pizza> list = new ArrayList<>();
        try (Cursor c = db.query(
                DatabaseHelper.TABLE_PIZZAS,
                null,
                null, null, null, null,
                DatabaseHelper.COLUMN_PIZZA_RATING + " DESC",
                String.valueOf(limit))) {
            while (c != null && c.moveToNext()) list.add(cursorToPizza(c));
        } finally { close(); }
        return list;
    }

    public int updatePizza(Pizza pizza) {
        openWritable();
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
            return db.update(DatabaseHelper.TABLE_PIZZAS, values,
                    DatabaseHelper.COLUMN_PIZZA_ID + "=?",
                    new String[]{String.valueOf(pizza.getPizzaId())});
        } finally { close(); }
    }

    public int updatePizzaRating(int pizzaId, double rating) {
        openWritable();
        try {
            ContentValues values = new ContentValues();
            values.put(DatabaseHelper.COLUMN_PIZZA_RATING, rating);
            return db.update(DatabaseHelper.TABLE_PIZZAS, values,
                    DatabaseHelper.COLUMN_PIZZA_ID + "=?",
                    new String[]{String.valueOf(pizzaId)});
        } finally { close(); }
    }

    public int updatePizzaStock(int pizzaId, int stock) {
        openWritable();
        try {
            ContentValues values = new ContentValues();
            values.put(DatabaseHelper.COLUMN_PIZZA_STOCK, stock);
            return db.update(DatabaseHelper.TABLE_PIZZAS, values,
                    DatabaseHelper.COLUMN_PIZZA_ID + "=?",
                    new String[]{String.valueOf(pizzaId)});
        } finally { close(); }
    }

    public int deletePizza(int pizzaId) {
        openWritable();
        try {
            return db.delete(DatabaseHelper.TABLE_PIZZAS,
                    DatabaseHelper.COLUMN_PIZZA_ID + "=?",
                    new String[]{String.valueOf(pizzaId)});
        } finally { close(); }
    }

    public int getPizzaCount() {
        openReadable();
        try (Cursor c = db.rawQuery("SELECT COUNT(*) FROM " + DatabaseHelper.TABLE_PIZZAS, null)) {
            if (c != null && c.moveToFirst()) return c.getInt(0);
            return 0;
        } finally { close(); }
    }

    /* ========== Các hàm bạn đang dùng cho sync & search – refactor đồng bộ ========== */

    public List<String> getAllCategories() {
        openReadable();
        List<String> list = new ArrayList<>();
        String sql = "SELECT DISTINCT " + DatabaseHelper.COLUMN_PIZZA_CATEGORY +
                " FROM " + DatabaseHelper.TABLE_PIZZAS +
                " WHERE " + DatabaseHelper.COLUMN_PIZZA_CATEGORY + " IS NOT NULL " +
                " ORDER BY " + DatabaseHelper.COLUMN_PIZZA_CATEGORY;
        try (Cursor c = db.rawQuery(sql, null)) {
            while (c != null && c.moveToNext()) {
                list.add(c.getString(c.getColumnIndexOrThrow(DatabaseHelper.COLUMN_PIZZA_CATEGORY)));
            }
        } finally { close(); }
        return list;
    }

    public void insertOrUpdate(List<Pizza> pizzas) {
        if (pizzas == null || pizzas.isEmpty()) return;
        openWritable();
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
                if (p.getStock() != 0) cv.put(DatabaseHelper.COLUMN_PIZZA_STOCK, p.getStock());
                if (p.getSize() != null) cv.put(DatabaseHelper.COLUMN_PIZZA_SIZE, p.getSize());

                long result = db.insertWithOnConflict(
                        DatabaseHelper.TABLE_PIZZAS, null, cv, SQLiteDatabase.CONFLICT_REPLACE);
                Log.d("DAO", "Insert/Update pizzaId=" + p.getPizzaId() + " → " + result);
            }
            db.setTransactionSuccessful();
            Log.d("DAO", "SYNC OK: " + pizzas.size() + " pizzas");
        } catch (Exception e) {
            Log.e("DAO", "SYNC ERR: " + e.getMessage());
        } finally {
            db.endTransaction();
            close();
        }
    }

    public List<Pizza> search(String query, Double minPrice, Double maxPrice, String category, String sortOrder,
                              int page, int size) {
        openReadable();

        StringBuilder sql = new StringBuilder("SELECT * FROM " + DatabaseHelper.TABLE_PIZZAS + " WHERE 1=1");
        List<String> args = new ArrayList<>();

        if (query != null && !query.isEmpty()) {
            sql.append(" AND ").append(DatabaseHelper.COLUMN_PIZZA_NAME).append(" LIKE ?");
            args.add("%" + query + "%");
        }
        if (minPrice != null) {
            sql.append(" AND ").append(DatabaseHelper.COLUMN_PIZZA_PRICE).append(" >= ?");
            args.add(String.valueOf(minPrice));
        }
        if (maxPrice != null && maxPrice < Double.MAX_VALUE) {
            sql.append(" AND ").append(DatabaseHelper.COLUMN_PIZZA_PRICE).append(" <= ?");
            args.add(String.valueOf(maxPrice));
        }
        if (category != null && !category.isEmpty()) {
            sql.append(" AND ").append(DatabaseHelper.COLUMN_PIZZA_CATEGORY).append(" = ?");
            args.add(category);
        }
        if ("asc".equalsIgnoreCase(sortOrder)) {
            sql.append(" ORDER BY ").append(DatabaseHelper.COLUMN_PIZZA_PRICE).append(" ASC");
        } else {
            sql.append(" ORDER BY ").append(DatabaseHelper.COLUMN_PIZZA_PRICE).append(" DESC");
        }
        sql.append(", ").append(DatabaseHelper.COLUMN_PIZZA_NAME)
                .append(" LIMIT ? OFFSET ?");
        args.add(String.valueOf(size));
        args.add(String.valueOf(Math.max(0, (page - 1) * size)));

        List<Pizza> list = new ArrayList<>();
        try (Cursor c = db.rawQuery(sql.toString(), args.toArray(new String[0]))) {
            while (c != null && c.moveToNext()) list.add(cursorToPizza(c));
        } finally { close(); }
        return list;
    }

    public List<String> getSuggestions(String q) {
        openReadable();
        List<String> list = new ArrayList<>();
        String sql = "SELECT " + DatabaseHelper.COLUMN_PIZZA_NAME +
                " FROM " + DatabaseHelper.TABLE_PIZZAS +
                " WHERE " + DatabaseHelper.COLUMN_PIZZA_NAME + " LIKE ? LIMIT 5";
        try (Cursor c = db.rawQuery(sql, new String[]{"%" + q + "%"})) {
            while (c != null && c.moveToNext()) {
                list.add(c.getString(c.getColumnIndexOrThrow(DatabaseHelper.COLUMN_PIZZA_NAME)));
            }
        } finally { close(); }
        return list;
    }

    /* ========== Helper ========== */

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
}
