package com.example.listacompra.DataBase

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

data class Product(
    val id: Int,
    val name: String,
    val quantity: Int,
    val price: Double
)

class ShopDatabaseHelper2(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {
    companion object {
        private const val DATABASE_VERSION = 1
        private const val DATABASE_NAME = "ShopDatabase.db"
        private const val TABLE_NAME = "shop"
        private const val COLUMN_ID = "id"
        private const val COLUMN_NAME = "name"
        private const val COLUMN_QUANTITY = "quantity"
        private const val COLUMN_PRICE = "price"
    }

    override fun onCreate(db: SQLiteDatabase?) {
        val createTable = ("CREATE TABLE $TABLE_NAME ("
                + "$COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT,"
                + "$COLUMN_NAME TEXT,"
                + "$COLUMN_QUANTITY INTEGER,"
                + "$COLUMN_PRICE REAL)")
        db?.execSQL(createTable)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db?.execSQL("DROP TABLE IF EXISTS $TABLE_NAME")
        onCreate(db)
    }

    fun addProduct(product: Product): Long {
        val db = this.writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_NAME, product.name)
            put(COLUMN_QUANTITY, product.quantity)
            put(COLUMN_PRICE, product.price)
        }
        return db.insert(TABLE_NAME, null, values)
    }

    fun deleteProduct(id: Int): Int {
        val db = this.writableDatabase
        return db.delete(TABLE_NAME, "$COLUMN_ID=?", arrayOf(id.toString()))
    }

    fun getAllProducts(): List<Product> {
        val productList = mutableListOf<Product>()
        val db = this.readableDatabase
        val cursor = db.rawQuery("SELECT * FROM $TABLE_NAME", null)
        with(cursor) {
            while (moveToNext()) {
                val product = Product(
                    id = getInt(getColumnIndexOrThrow(COLUMN_ID)),
                    name = getString(getColumnIndexOrThrow(COLUMN_NAME)),
                    quantity = getInt(getColumnIndexOrThrow(COLUMN_QUANTITY)),
                    price = getDouble(getColumnIndexOrThrow(COLUMN_PRICE))
                )
                productList.add(product)
            }
        }
        cursor.close()
        return productList
    }
}