package com.gtug.devcamp.thecontact.db;

import java.util.ArrayList;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

	private static String DB_NAME = "my_contact.db";
	private static int DB_Version = 1;
	private String DB_STRUCTURE_CREATE = "CREATE  TABLE contact ('id' INTEGER PRIMARY KEY  AUTOINCREMENT  NOT NULL  UNIQUE , 'firstname' TEXT, 'lastname' TEXT, 'nickname' TEXT, 'mobile' TEXT, 'email' TEXT, 'note' TEXT,'picture' TEXT, 'flag' VARCHAR)";

	public DatabaseHelper(Context context, String name, CursorFactory factory,
			int version) {
		super(context, name, factory, version);
		// TODO Auto-generated constructor stub
	}

	public DatabaseHelper(Context context) {
		super(context, DB_NAME, null, DB_Version);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL("DROP TABLE IF EXISTS contact");
		db.execSQL(DB_STRUCTURE_CREATE);
		// FIXME test data
		db.execSQL("INSERT INTO 'contact' ('id','firstname','lastname','nickname','mobile','email','note','picture','flag') VALUES (1,'testName','testLastName','testNickName','0812345678','test1@local.com','test data','/var/..','C')");
		db.execSQL("INSERT INTO 'contact' ('id','firstname','lastname','nickname','mobile','email','note','picture','flag') VALUES (2,'testName2','testLastName2','testNickName2','0812345679','test2@local.com','test data','/var/..','M')");
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub
		db.execSQL("DROP TABLE IF EXISTS contact");
		onCreate(db);
	}

	// model
	public ContactModel[] getContact() {
		SQLiteDatabase db = getReadableDatabase();
		String[] cols = new String[] { "id", "firstname", "lastName",
				"nickname", "mobile", "email", "note" };

		Cursor cursor = db.query("contact", cols, null, null, null, null, null);

		ArrayList<ContactModel> ret = new ArrayList<ContactModel>();

		while (cursor.moveToNext()) {
			ContactModel contact = new ContactModel();

			contact.setId(cursor.getInt(0));
			contact.setFirstName(cursor.getString(1));
			contact.setLastName(cursor.getString(2));
			contact.setMobile(cursor.getString(3));
			contact.setEmail(cursor.getString(4));
			contact.setNote(cursor.getString(5));

			ret.add(contact);
		}

		return (ContactModel[]) ret.toArray();
	}

	public ContactModel getMyContact() {
		SQLiteDatabase db = getWritableDatabase();
		String[] cols = new String[] { "id", "firstname", "lastName",
				"nickname", "mobile", "email", "note" };

		Cursor cursor = db.query("contact", cols, "flag=?",
				new String[] { "M" }, null, null, null);

		if (cursor != null && cursor.getCount() > 0) {
			cursor.moveToFirst();

			ContactModel contact = new ContactModel();

			contact.setId(cursor.getInt(0));
			contact.setFirstName(cursor.getString(1));
			contact.setLastName(cursor.getString(2));
			contact.setMobile(cursor.getString(3));
			contact.setEmail(cursor.getString(4));
			contact.setNote(cursor.getString(5));

			return contact;

		} else {
			return null;
		}
	}

}
