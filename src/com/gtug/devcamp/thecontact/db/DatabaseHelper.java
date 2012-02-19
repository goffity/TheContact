package com.gtug.devcamp.thecontact.db;

import java.util.ArrayList;
import java.util.Iterator;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DatabaseHelper extends SQLiteOpenHelper {

	private final String LOG_TAG = "THE CONTACT";

	private static String DB_NAME = "my_contact.db";
	private static int DB_Version = 2;
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
		db.execSQL("INSERT INTO 'contact' ('id','firstname','lastname','nickname','mobile','email','note','picture','flag') VALUES (1,'testName','testLastName','testNickName','0812345678','test1@local.com','test data','/var/..','M')");
		db.execSQL("INSERT INTO 'contact' ('id','firstname','lastname','nickname','mobile','email','note','picture','flag') VALUES (2,'testName2','testLastName2','testNickName2','0812345679','test2@local.com','test data','/var/..','C')");
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub
		db.execSQL("DROP TABLE IF EXISTS contact");
		onCreate(db);
	}

	/*
	 * Model
	 */

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
			contact.setNickName(cursor.getString(3));
			contact.setMobile(cursor.getString(4));
			contact.setEmail(cursor.getString(5));
			contact.setNote(cursor.getString(6));

			ret.add(contact);
		}

		ContactModel[] cont = new ContactModel[ret.size()];
		for (int i = 0; i < cont.length; i++) {
			ContactModel temp = ret.get(i);
			cont[i] = temp;
		}
		// cont = (ContactModel[]) ret.toArray();

		db.close();

		return cont;
	}

	public Cursor getContactCursor() {
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
			contact.setNickName(cursor.getString(3));
			contact.setMobile(cursor.getString(4));
			contact.setEmail(cursor.getString(5));
			contact.setNote(cursor.getString(6));

			ret.add(contact);
		}

		ContactModel[] cont = new ContactModel[ret.size()];
		for (int i = 0; i < cont.length; i++) {
			ContactModel temp = ret.get(i);
			cont[i] = temp;
		}
		// cont = (ContactModel[]) ret.toArray();

		db.close();

		return cursor;
	}

	public ContactModel getMyContact() {
		SQLiteDatabase db = getWritableDatabase();
		String[] cols = new String[] { "id", "firstname", "lastName",
				"nickname", "mobile", "email", "note" };

		Cursor cursor = db.query("contact", cols, "flag=?",
				new String[] { "M" }, null, null, null);

		Log.d(LOG_TAG, cursor.getCount() + " rows");

		if (cursor != null && cursor.getCount() > 0) {
			cursor.moveToFirst();

			ContactModel contact = new ContactModel();

			contact.setId(cursor.getInt(0));
			contact.setFirstName(cursor.getString(1));
			contact.setLastName(cursor.getString(2));
			contact.setNickName(cursor.getString(3));
			contact.setMobile(cursor.getString(4));
			contact.setEmail(cursor.getString(5));
			contact.setNote(cursor.getString(6));

			db.close();

			return contact;

		} else {
			Log.d(LOG_TAG, "result null");
			return null;
		}
	}

	public ContactModel getContactByNumber(String mobile) {
		SQLiteDatabase db = getWritableDatabase();
		String[] cols = new String[] { "id", "firstname", "lastName",
				"nickname", "mobile", "email", "note" };

		Cursor cursor = db.query("contact", cols, "mobile=?",
				new String[] { mobile }, null, null, null);

		Log.d(LOG_TAG, cursor.getCount() + " rows");

		if (cursor != null && cursor.getCount() > 0) {
			cursor.moveToFirst();

			ContactModel contact = new ContactModel();

			contact.setId(cursor.getInt(0));
			contact.setFirstName(cursor.getString(1));
			contact.setLastName(cursor.getString(2));
			contact.setMobile(cursor.getString(3));
			contact.setEmail(cursor.getString(4));
			contact.setNote(cursor.getString(5));

			db.close();

			return contact;

		} else {
			Log.d(LOG_TAG, "result null");
			return null;
		}
	}

	public long addContact(ContactModel contactModel) {

		SQLiteDatabase db = getWritableDatabase();
		ContentValues values = new ContentValues();

		values.put("firstname", contactModel.getFirstName());
		values.put("lastname", contactModel.getLastName());
		values.put("nickname", "" + contactModel.getNickName());
		values.put("mobile", contactModel.getMobile());
		values.put("email", contactModel.getEmail());
		values.put("note", contactModel.getNote());
		values.put("picture", contactModel.getPicture());
		values.put("flag", contactModel.getFlag());

		// check dup
		long id = 0;
		if (getContactByNumber(contactModel.getMobile()) != null) {
			id = db.insert("contact", null, values);
		}

		db.close();

		return id;

	}

	private void populateObj(ContactModel contact) {

	}

}
