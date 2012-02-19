package com.gtug.devcamp.thecontact;

import java.nio.charset.Charset;
import java.util.Locale;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.Intent;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.gtug.devcamp.mycontact.R;
import com.gtug.devcamp.thecontact.db.ContactModel;
import com.gtug.devcamp.thecontact.db.DatabaseHelper;

public class TheContactActivity extends Activity {
	private final String LOG_TAG = "THE CONTACT";

	private AlertDialog mDialog;
	private NfcAdapter mAdapter;
	private PendingIntent mPendingIntent;
	private NdefMessage mNdefPushMessage;

	private LinearLayout mTagContent;

	private TextView tv1;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		resolveIntent(getIntent());

		mDialog = new AlertDialog.Builder(this).setNeutralButton("Ok", null)
				.create();

		// TODO comment for non NFC device
		mAdapter = NfcAdapter.getDefaultAdapter(this);
		if (mAdapter == null) {
			showMessage(R.string.error, R.string.no_nfc);
		}

		// Handle all of our received NFC intents in this activity.
		mPendingIntent = PendingIntent.getActivity(this, 0, new Intent(this,
				getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);

		// FIXME get data from SQLite

		// mNdefPushMessage = new NdefMessage(new NdefRecord[] { newTextRecord(
		// "Message from NFC Reader :-)", Locale.ENGLISH, true) });
		tv1 = (TextView) findViewById(R.id.textView1);
		// tv1.setText(new Gson().toJson(new DatabaseHelper(
		// getApplicationContext()).getMyContact()));
		String mContact = new Gson().toJson(new DatabaseHelper(
				getApplicationContext()).getMyContact());
		Log.d(LOG_TAG, "MyContact: " + mContact);

		// TODO convert result to send
		// using JSON by GSON
		mNdefPushMessage = new NdefMessage(new NdefRecord[] { newTextRecord(
				mContact, Locale.US, true) });
	}

	// convert String to NFC Data Exchange format
	private NdefRecord newTextRecord(String text, Locale locale,
			boolean encodeInUtf8) {
		byte[] langBytes = locale.getLanguage().getBytes(
				Charset.forName("US-ASCII"));

		Charset utfEncoding = encodeInUtf8 ? Charset.forName("UTF-8") : Charset
				.forName("UTF-16");
		byte[] textBytes = text.getBytes(utfEncoding);

		int utfBit = encodeInUtf8 ? 0 : (1 << 7);
		char status = (char) (utfBit + langBytes.length);

		byte[] data = new byte[1 + langBytes.length + textBytes.length];
		data[0] = (byte) status;
		System.arraycopy(langBytes, 0, data, 1, langBytes.length);
		System.arraycopy(textBytes, 0, data, 1 + langBytes.length,
				textBytes.length);

		return new NdefRecord(NdefRecord.TNF_WELL_KNOWN, NdefRecord.RTD_TEXT,
				new byte[0], data);
	}

	private void showMessage(int title, int message) {
		mDialog.setTitle(title);
		mDialog.setMessage(getText(message));
		mDialog.show();
	}

	private void resolveIntent(Intent intent) {
		String action = intent.getAction();
		String result = "";
		Log.d(LOG_TAG, "Resolve " + action);
		if (NfcAdapter.ACTION_TAG_DISCOVERED.equals(action)
				|| NfcAdapter.ACTION_NDEF_DISCOVERED.equals(action)) {
			Log.d(LOG_TAG, "Action: " + action);
			Parcelable[] rawMsgs = intent
					.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);
			NdefMessage[] msgs;

			Log.d(LOG_TAG, "Rec Data");
			if (rawMsgs != null) {
				Log.d(LOG_TAG, "msgs != null");
				msgs = new NdefMessage[rawMsgs.length];
				for (int i = 0; i < rawMsgs.length; i++) {
					msgs[i] = (NdefMessage) rawMsgs[i];

					result = new String(msgs[i].getRecords()[i].getPayload());

					int idx = result.indexOf("{");

					// Toast.makeText(getApplicationContext(),
					// new String(msgs[i].getRecords()[i].getPayload()),
					// Toast.LENGTH_LONG);
					Log.d(LOG_TAG,
							"Rec Data: "
									+ new String(msgs[i].getRecords()[i]
											.getPayload()).substring(idx));
					// Log.d(LOG_TAG,
					// "Rec Data: "
					// + new String(msgs[i].getRecords()[i]
					// .getPayload()).substring(3));

					result = result.substring(idx);

				}
			}

			// FIXME save to DB
			tv1.setText(result);

			ContactModel contact = new Gson().fromJson(result,
					ContactModel.class);
			contact.setFlag("C");

			// insert to SQLite
			new DatabaseHelper(getApplicationContext()).addContact(contact);

			ContactModel[] cont = new DatabaseHelper(getApplicationContext())
					.getContact();

			for (int i = 0; i < cont.length; i++) {
				Log.d(LOG_TAG, "C0ntact: " + cont[i].getFirstName() + " "
						+ cont[i].getLastName() + " " + cont[i].getNickName());
			}

			// else {
			// // Unknown tag type
			// byte[] empty = new byte[0];
			// byte[] id = intent.getByteArrayExtra(NfcAdapter.EXTRA_ID);
			// Parcelable tag = intent
			// .getParcelableExtra(NfcAdapter.EXTRA_TAG);
			// byte[] payload = null;
			// try {
			// payload = dumpTagData(tag).getBytes();
			// } catch (Exception e) {
			// throw new RuntimeException(e);
			// }
			// NdefRecord record = new NdefRecord(NdefRecord.TNF_UNKNOWN,
			// empty, id, payload);
			// NdefMessage msg = new NdefMessage(new NdefRecord[] { record });
			// msgs = new NdefMessage[] { msg };
			// }
			// // Setup the views
			// buildTagViews(msgs);
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		if (mAdapter != null) {
			if (!mAdapter.isEnabled()) {
				showMessage(R.string.error, R.string.nfc_disabled);
			}
			try {
				mAdapter.enableForegroundDispatch(this, mPendingIntent, null,
						null);
				mAdapter.enableForegroundNdefPush(this, mNdefPushMessage);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	protected void onPause() {
		super.onPause();
		if (mAdapter != null) {
			try {
				mAdapter.disableForegroundDispatch(this);
				mAdapter.disableForegroundNdefPush(this);
				// sAdapter_disableForegroundDispatch.invoke(mAdapter, this);
				// sAdapter_disableForegroundNdefPush.invoke(mAdapter, this);
			} catch (Exception e) {
				// ignore
			}
		}
	}

	// // set view to display
	// void buildTagViews(NdefMessage[] msgs) {
	// if (msgs == null || msgs.length == 0) {
	// return;
	// }
	// LayoutInflater inflater = LayoutInflater.from(this);
	// LinearLayout content = mTagContent;
	//
	// // Parse the first message in the list
	// // Build views for all of the sub records
	// Date now = new Date();
	// List<ParsedNdefRecord> records = NdefMessageParser.parse(msgs[0]);
	// final int size = records.size();
	// for (int i = 0; i < size; i++) {
	// TextView timeView = new TextView(this);
	// timeView.setText(TIME_FORMAT.format(now));
	// content.addView(timeView, 0);
	// ParsedNdefRecord record = records.get(i);
	// content.addView(record.getView(this, inflater, content, i), 1 + i);
	// content.addView(
	// inflater.inflate(R.layout.tag_divider, content, false),
	// 2 + i);
	// }
	// }

	@Override
	public void onNewIntent(Intent intent) {
		setIntent(intent);
		resolveIntent(intent);
	}
}