package ru.ompro.targets.app;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import ru.ompro.targets.app.database.CrimeBaseHelper;
import ru.ompro.targets.app.database.CrimeCursorWrapper;
import ru.ompro.targets.app.database.CrimeDbSchema.CrimeTable;

/**
 * Created by Ompro on 10.01.2017.
 */

public class TargetLab {
    private static TargetLab sTargetLab;

    private Context mContext;
    private SQLiteDatabase mDatabase;

    public static TargetLab get(Context context) {
        if (sTargetLab == null) {
            sTargetLab = new TargetLab(context);
        }
        return sTargetLab;
    }

    private TargetLab(Context context) {
        mContext = context.getApplicationContext();
        mDatabase = new CrimeBaseHelper(mContext).getWritableDatabase();
    }

    public void addCrime(Target c) {
        ContentValues values = getContentValues(c);

        mDatabase.insert(CrimeTable.NAME, null, values);
    }

    public List<Target> getmCrimes() {
        List<Target> targets = new ArrayList<>();

        CrimeCursorWrapper cursor = queryCrimes(null, null);

        try {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                targets.add(cursor.getCrime());
                cursor.moveToNext();
            }
        } finally {
            cursor.close();
        }
        return targets;
    }

    public Target getCrime(UUID id) {
        CrimeCursorWrapper cursor = queryCrimes(
                CrimeTable.Cols.UUID + " = ?",
                new String[]{id.toString()}
        );

        try {
            if (cursor.getCount() == 0) {
                return null;
            }
            cursor.moveToFirst();
            return cursor.getCrime();
        } finally {
            cursor.close();
        }
    }

    public void updateCrime(Target target) {
        String uuidString = target.getId().toString();
        ContentValues values = getContentValues(target);

        mDatabase.update(CrimeTable.NAME, values, CrimeTable.Cols.UUID + " = ?", new String[]{uuidString});
    }

    private static ContentValues getContentValues(Target target) {
        ContentValues values = new ContentValues();
        values.put(CrimeTable.Cols.UUID, target.getId().toString());
        values.put(CrimeTable.Cols.TITLE, target.getTitle());
        values.put(CrimeTable.Cols.DATE, target.getDate().getTime());
        values.put(CrimeTable.Cols.SOLVED, target.isSolved() ? 1 : 0);

        return values;
    }

    private CrimeCursorWrapper queryCrimes(String whereClause, String[] whereArgs) {
        Cursor cursor = mDatabase.query(
                CrimeTable.NAME,
                null,
                whereClause,
                whereArgs,
                null,
                null,
                null
        );

        return new CrimeCursorWrapper(cursor);
    }

    public void deleteCrime(Target target) {

        mDatabase.execSQL("DELETE FROM crimes WHERE uuid = ?", new String[]{target.getId().toString()});
    }
}
