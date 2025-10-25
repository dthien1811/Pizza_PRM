package fpt.edu.vn.pizzaapp_prm392.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * DatabaseHelper - Quản lý cơ sở dữ liệu SQLite cho ứng dụng Pizza
 * Tạo và cập nhật các bảng dữ liệu theo yêu cầu
 */
public class DatabaseHelper extends SQLiteOpenHelper {
    
    // Database info
    private static final String DATABASE_NAME = "pizzaapp.db";
    private static final int DATABASE_VERSION = 1;
    
    // Table names
    public static final String TABLE_USERS = "users";
    public static final String TABLE_PIZZAS = "pizzas";
    public static final String TABLE_CART_ITEMS = "cart_items";
    public static final String TABLE_ORDERS = "orders";
    public static final String TABLE_ORDER_ITEMS = "order_items";
    public static final String TABLE_PAYMENTS = "payments";
    public static final String TABLE_ORDER_STATUS = "order_status";
    
    // Users table columns
    public static final String COLUMN_USER_ID = "user_id";
    public static final String COLUMN_USER_NAME = "name";
    public static final String COLUMN_USER_EMAIL = "email";
    public static final String COLUMN_USER_PHONE = "phone";
    public static final String COLUMN_USER_PASSWORD = "password";
    public static final String COLUMN_USER_ADDRESS = "address";
    public static final String COLUMN_USER_AVATAR = "avatar";
    public static final String COLUMN_USER_TOKEN = "token";
    public static final String COLUMN_USER_CREATED_AT = "created_at";
    public static final String COLUMN_USER_UPDATED_AT = "updated_at";
    
    // Pizzas table columns
    public static final String COLUMN_PIZZA_ID = "pizza_id";
    public static final String COLUMN_PIZZA_NAME = "name";
    public static final String COLUMN_PIZZA_DESCRIPTION = "description";
    public static final String COLUMN_PIZZA_PRICE = "price";
    public static final String COLUMN_PIZZA_IMAGE = "image";
    public static final String COLUMN_PIZZA_SIZE = "size";
    public static final String COLUMN_PIZZA_CATEGORY = "category";
    public static final String COLUMN_PIZZA_RATING = "rating";
    public static final String COLUMN_PIZZA_STOCK = "stock";
    
    // Cart Items table columns
    public static final String COLUMN_CART_ID = "cart_id";
    public static final String COLUMN_CART_USER_ID = "user_id";
    public static final String COLUMN_CART_PIZZA_ID = "pizza_id";
    public static final String COLUMN_CART_QUANTITY = "quantity";
    public static final String COLUMN_CART_PRICE = "price";
    public static final String COLUMN_CART_NOTES = "notes";
    public static final String COLUMN_CART_ADDED_AT = "added_at";
    
    // Orders table columns
    public static final String COLUMN_ORDER_ID = "order_id";
    public static final String COLUMN_ORDER_CODE = "order_code";
    public static final String COLUMN_ORDER_USER_ID = "user_id";
    public static final String COLUMN_ORDER_TOTAL_PRICE = "total_price";
    public static final String COLUMN_ORDER_ADDRESS = "address";
    public static final String COLUMN_ORDER_PHONE = "phone";
    public static final String COLUMN_ORDER_NOTES = "notes";
    public static final String COLUMN_ORDER_STATUS = "status";
    public static final String COLUMN_ORDER_PAYMENT_METHOD = "payment_method";
    public static final String COLUMN_ORDER_CREATED_AT = "created_at";
    public static final String COLUMN_ORDER_UPDATED_AT = "updated_at";
    
    // Order Items table columns
    public static final String COLUMN_ORDER_ITEM_ID = "order_item_id";
    public static final String COLUMN_ORDER_ITEM_ORDER_ID = "order_id";
    public static final String COLUMN_ORDER_ITEM_PIZZA_ID = "pizza_id";
    public static final String COLUMN_ORDER_ITEM_QUANTITY = "quantity";
    public static final String COLUMN_ORDER_ITEM_PRICE = "price";
    
    // Payments table columns
    public static final String COLUMN_PAYMENT_ID = "payment_id";
    public static final String COLUMN_PAYMENT_ORDER_ID = "order_id";
    public static final String COLUMN_PAYMENT_METHOD = "payment_method";
    public static final String COLUMN_PAYMENT_AMOUNT = "amount";
    public static final String COLUMN_PAYMENT_STATUS = "status";
    public static final String COLUMN_PAYMENT_TRANSACTION_ID = "transaction_id";
    public static final String COLUMN_PAYMENT_CREATED_AT = "created_at";
    
    // Order Status table columns
    public static final String COLUMN_STATUS_ID = "status_id";
    public static final String COLUMN_STATUS_ORDER_ID = "order_id";
    public static final String COLUMN_STATUS_NAME = "status_name";
    public static final String COLUMN_STATUS_TIMESTAMP = "timestamp";
    public static final String COLUMN_STATUS_DESCRIPTION = "description";
    
    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
    
    @Override
    public void onCreate(SQLiteDatabase db) {
        // Create Users table
        String CREATE_TABLE_USERS = "CREATE TABLE " + TABLE_USERS + "(" +
                COLUMN_USER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_USER_NAME + " TEXT NOT NULL, " +
                COLUMN_USER_EMAIL + " TEXT UNIQUE NOT NULL, " +
                COLUMN_USER_PHONE + " TEXT, " +
                COLUMN_USER_PASSWORD + " TEXT NOT NULL, " +
                COLUMN_USER_ADDRESS + " TEXT, " +
                COLUMN_USER_AVATAR + " TEXT, " +
                COLUMN_USER_TOKEN + " TEXT, " +
                COLUMN_USER_CREATED_AT + " DATETIME DEFAULT CURRENT_TIMESTAMP, " +
                COLUMN_USER_UPDATED_AT + " DATETIME DEFAULT CURRENT_TIMESTAMP)";
        db.execSQL(CREATE_TABLE_USERS);
        
        // Create Pizzas table
        String CREATE_TABLE_PIZZAS = "CREATE TABLE " + TABLE_PIZZAS + "(" +
                COLUMN_PIZZA_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_PIZZA_NAME + " TEXT NOT NULL, " +
                COLUMN_PIZZA_DESCRIPTION + " TEXT, " +
                COLUMN_PIZZA_PRICE + " REAL NOT NULL, " +
                COLUMN_PIZZA_IMAGE + " TEXT, " +
                COLUMN_PIZZA_SIZE + " TEXT, " +
                COLUMN_PIZZA_CATEGORY + " TEXT, " +
                COLUMN_PIZZA_RATING + " REAL DEFAULT 0, " +
                COLUMN_PIZZA_STOCK + " INTEGER DEFAULT 0)";
        db.execSQL(CREATE_TABLE_PIZZAS);
        
        // Create Cart Items table
        String CREATE_TABLE_CART_ITEMS = "CREATE TABLE " + TABLE_CART_ITEMS + "(" +
                COLUMN_CART_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_CART_USER_ID + " INTEGER NOT NULL, " +
                COLUMN_CART_PIZZA_ID + " INTEGER NOT NULL, " +
                COLUMN_CART_QUANTITY + " INTEGER NOT NULL DEFAULT 1, " +
                COLUMN_CART_PRICE + " REAL NOT NULL, " +
                COLUMN_CART_NOTES + " TEXT, " +
                COLUMN_CART_ADDED_AT + " DATETIME DEFAULT CURRENT_TIMESTAMP, " +
                "FOREIGN KEY(" + COLUMN_CART_USER_ID + ") REFERENCES " + TABLE_USERS + "(" + COLUMN_USER_ID + "), " +
                "FOREIGN KEY(" + COLUMN_CART_PIZZA_ID + ") REFERENCES " + TABLE_PIZZAS + "(" + COLUMN_PIZZA_ID + "))";
        db.execSQL(CREATE_TABLE_CART_ITEMS);
        
        // Create Orders table
        String CREATE_TABLE_ORDERS = "CREATE TABLE " + TABLE_ORDERS + "(" +
                COLUMN_ORDER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_ORDER_CODE + " TEXT UNIQUE NOT NULL, " +
                COLUMN_ORDER_USER_ID + " INTEGER NOT NULL, " +
                COLUMN_ORDER_TOTAL_PRICE + " REAL NOT NULL, " +
                COLUMN_ORDER_ADDRESS + " TEXT NOT NULL, " +
                COLUMN_ORDER_PHONE + " TEXT NOT NULL, " +
                COLUMN_ORDER_NOTES + " TEXT, " +
                COLUMN_ORDER_STATUS + " TEXT DEFAULT 'PENDING', " +
                COLUMN_ORDER_PAYMENT_METHOD + " TEXT, " +
                COLUMN_ORDER_CREATED_AT + " DATETIME DEFAULT CURRENT_TIMESTAMP, " +
                COLUMN_ORDER_UPDATED_AT + " DATETIME DEFAULT CURRENT_TIMESTAMP, " +
                "FOREIGN KEY(" + COLUMN_ORDER_USER_ID + ") REFERENCES " + TABLE_USERS + "(" + COLUMN_USER_ID + "))";
        db.execSQL(CREATE_TABLE_ORDERS);
        
        // Create Order Items table
        String CREATE_TABLE_ORDER_ITEMS = "CREATE TABLE " + TABLE_ORDER_ITEMS + "(" +
                COLUMN_ORDER_ITEM_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_ORDER_ITEM_ORDER_ID + " INTEGER NOT NULL, " +
                COLUMN_ORDER_ITEM_PIZZA_ID + " INTEGER NOT NULL, " +
                COLUMN_ORDER_ITEM_QUANTITY + " INTEGER NOT NULL, " +
                COLUMN_ORDER_ITEM_PRICE + " REAL NOT NULL, " +
                "FOREIGN KEY(" + COLUMN_ORDER_ITEM_ORDER_ID + ") REFERENCES " + TABLE_ORDERS + "(" + COLUMN_ORDER_ID + "), " +
                "FOREIGN KEY(" + COLUMN_ORDER_ITEM_PIZZA_ID + ") REFERENCES " + TABLE_PIZZAS + "(" + COLUMN_PIZZA_ID + "))";
        db.execSQL(CREATE_TABLE_ORDER_ITEMS);
        
        // Create Payments table
        String CREATE_TABLE_PAYMENTS = "CREATE TABLE " + TABLE_PAYMENTS + "(" +
                COLUMN_PAYMENT_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_PAYMENT_ORDER_ID + " INTEGER NOT NULL UNIQUE, " +
                COLUMN_PAYMENT_METHOD + " TEXT NOT NULL, " +
                COLUMN_PAYMENT_AMOUNT + " REAL NOT NULL, " +
                COLUMN_PAYMENT_STATUS + " TEXT DEFAULT 'PENDING', " +
                COLUMN_PAYMENT_TRANSACTION_ID + " TEXT, " +
                COLUMN_PAYMENT_CREATED_AT + " DATETIME DEFAULT CURRENT_TIMESTAMP, " +
                "FOREIGN KEY(" + COLUMN_PAYMENT_ORDER_ID + ") REFERENCES " + TABLE_ORDERS + "(" + COLUMN_ORDER_ID + "))";
        db.execSQL(CREATE_TABLE_PAYMENTS);
        
        // Create Order Status History table
        String CREATE_TABLE_ORDER_STATUS = "CREATE TABLE " + TABLE_ORDER_STATUS + "(" +
                COLUMN_STATUS_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_STATUS_ORDER_ID + " INTEGER NOT NULL, " +
                COLUMN_STATUS_NAME + " TEXT NOT NULL, " +
                COLUMN_STATUS_TIMESTAMP + " DATETIME DEFAULT CURRENT_TIMESTAMP, " +
                COLUMN_STATUS_DESCRIPTION + " TEXT, " +
                "FOREIGN KEY(" + COLUMN_STATUS_ORDER_ID + ") REFERENCES " + TABLE_ORDERS + "(" + COLUMN_ORDER_ID + "))";
        db.execSQL(CREATE_TABLE_ORDER_STATUS);
        
        // Create indexes for better query performance
        db.execSQL("CREATE INDEX idx_user_email ON " + TABLE_USERS + "(" + COLUMN_USER_EMAIL + ")");
        db.execSQL("CREATE INDEX idx_cart_user ON " + TABLE_CART_ITEMS + "(" + COLUMN_CART_USER_ID + ")");
        db.execSQL("CREATE INDEX idx_order_user ON " + TABLE_ORDERS + "(" + COLUMN_ORDER_USER_ID + ")");
        db.execSQL("CREATE INDEX idx_order_status ON " + TABLE_ORDERS + "(" + COLUMN_ORDER_STATUS + ")");
        db.execSQL("CREATE INDEX idx_payment_status ON " + TABLE_PAYMENTS + "(" + COLUMN_PAYMENT_STATUS + ")");
    }
    
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop tables if they exist (for development)
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ORDER_STATUS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PAYMENTS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ORDER_ITEMS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ORDERS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CART_ITEMS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PIZZAS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
        
        // Recreate database
        onCreate(db);
    }
}
