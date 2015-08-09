package ru.firsto.yac15money;

/**
 * Created by razor on 09.08.15.
 */

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.CursorWrapper;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.location.Location;

import java.util.Date;

/**
 * Created by razor on 12.03.15.
 **/
public class DBHelper extends SQLiteOpenHelper {
    public static final String TAG = "RunDatabaseHelper";

    private static final String DB_NAME = "goods.sqlite";
    private static final int VERSION = 1;
    private static final String TABLE_GOODS = "goods";
    private static final String COLUMN_ITEM_ID = "_id";
    private static final String COLUMN_ITEM_PARENT_ID = "parent_id";
    private static final String COLUMN_ITEM_INTERNAL_ID = "internal_id";
    private static final String COLUMN_ITEM_TITLE = "title";


    public DBHelper(Context context) {
        super(context, DB_NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Создание таблицы "goods"
        db.execSQL("create table goods (" +
                        "_id integer primary key autoincrement," +
                        " parent_id integer," +
                        " internal_id integer," +
                        " title varchar(100)" +
                        ")"
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Здесь реализуются изменения схемы и преобразования данных
        // при обновлении схемы
    }

    public long insertItem(Item item) {
        ContentValues cv = new ContentValues();
        cv.put(COLUMN_ITEM_PARENT_ID, item.getParent_id());
        cv.put(COLUMN_ITEM_INTERNAL_ID, item.getInternal_id());
        cv.put(COLUMN_ITEM_TITLE, item.getTitle());
        return getWritableDatabase().insert(TABLE_GOODS, null, cv);
    }

    public ItemCursor queryItems() {
        // Эквивалент "select * from goods order by _id asc"
        Cursor wrapped = getReadableDatabase().query(TABLE_GOODS,
                null, null, null, null, null, COLUMN_ITEM_ID + " asc");
        return new ItemCursor(wrapped);
    }

    public ItemCursor queryChildItems(int parent) {
        // Эквивалент "select * from goods where parent_id = parent order by _id asc"
        Cursor wrapped = getReadableDatabase().query(TABLE_GOODS,
                null,
                COLUMN_ITEM_PARENT_ID + " = ?",
                new String[]{ String.valueOf(parent)},
                null,
                null,
                COLUMN_ITEM_ID + " asc");
        return new ItemCursor(wrapped);
    }

    public ItemCursor queryItem(long id) {
        Cursor wrapped = getReadableDatabase().query(TABLE_GOODS,
                null, // Все столбцы
                COLUMN_ITEM_ID + " = ?", // Поиск по идентификатору серии
                new String[]{ String.valueOf(id) }, // С этим значением
                null, // group by
                null, // order by
                null, // having
                "1"); // 1 строка
        return new ItemCursor(wrapped);
    }

    /**
     * Вспомогательный класс с курсором, возвращающим строки таблицы "goods".
     * Метод {@link getItem()} возвращает экземпляр Item, представляющий
     * текущую строку.
     */

    public static class ItemCursor extends CursorWrapper {
        public ItemCursor(Cursor c) {
            super(c);
        }

        /**
         * Возвращает объект Item, представляющий текущую строку,
         * или null, если текущая строка недействительна.
         */
        public Item getItem() {
            if (isBeforeFirst() || isAfterLast())
                return null;
            return new Item(
                    getInt(getColumnIndex(COLUMN_ITEM_ID)),
                    getInt(getColumnIndex(COLUMN_ITEM_PARENT_ID)),
                    getInt(getColumnIndex(COLUMN_ITEM_INTERNAL_ID)),
                    getString(getColumnIndex(COLUMN_ITEM_TITLE))
            );
        }
    }
}
