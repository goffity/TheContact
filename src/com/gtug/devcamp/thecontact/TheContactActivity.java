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
import android.widget.TextView;

import com.gtug.devcamp.mycontact.R;
import com.gtug.devcamp.thecontact.db.DatabaseHelper;

public class TheContactActivity extends Activity {
	private AlertDialog mDialog;
	private NfcAdapter mAdapter;
	private PendingIntent mPendingIntent;
	private NdefMessage mNdefPushMessage;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		mDialog = new AlertDialog.Builder(this).setNeutralButton("Ok", null)
				.create();

		mAdapter = NfcAdapter.getDefaultAdapter(this);
		if (mAdapter == null) {
			showMessage(R.string.error, R.string.no_nfc);
		}

		mPendingIntent = PendingIntent.getActivity(this, 0, new Intent(this,
				getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);

		// FIXME get data from SQLite

		// mNdefPushMessage = new NdefMessage(new NdefRecord[] { newTextRecord(
		// "Message from NFC Reader :-)", Locale.ENGLISH, true) });
//		TextView tv1 = (TextView) findViewById(R.id.textView1);
//		tv1.setText(new DatabaseHelper(getApplicationContext()).getMyContact()
//				.getFirstName());

		//TODO convert result to send
		 mNdefPushMessage = new NdefMessage(new NdefRecord[] { newTextRecord(
		 new DatabaseHelper(getApplicationContext()).getMyContact()
		 .getFirstName(), Locale.US, true) });
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
		if (NfcAdapter.ACTION_TAG_DISCOVERED.equals(action)
				|| NfcAdapter.ACTION_NDEF_DISCOVERED.equals(action)) {
			Parcelable[] rawMsgs = intent
					.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);
			NdefMessage[] msgs;
			if (rawMsgs != null) {
				msgs = new NdefMessage[rawMsgs.length];
				for (int i = 0; i < rawMsgs.length; i++) {
					msgs[i] = (NdefMessage) rawMsgs[i];
				}
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
}