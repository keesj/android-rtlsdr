package rtlsdr.android;

import java.util.HashMap;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;

public class MainActivity extends Activity implements View.OnClickListener,
		Runnable {

	private static final String TAG = "MainActivity";

	private UsbManager mUsbManager;

	Thread myNativeThread = null;

	static {
		try {
			System.loadLibrary("usb");
			System.loadLibrary("rtlsdr");
			System.loadLibrary("rtltest");
			System.loadLibrary("rtltcp");
		} catch (Throwable t) {
			Log.w(TAG, "Failed to load native library:" + t.getMessage(), t);
		}
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mUsbManager = (UsbManager) getSystemService(Context.USB_SERVICE);
	}

	@Override
	public void onPause() {
		super.onPause();
	}

	/*
	 * Method called by the native code to get a device handle
	 */
	public UsbDeviceConnection open(String device_name) {
		Log.d(TAG, "Open called " + device_name);
		UsbDevice usbDevice;
		usbDevice = mUsbManager.getDeviceList().get(device_name);
		if (usbDevice != null) {
			if (mUsbManager.hasPermission(usbDevice)) {
				return mUsbManager.openDevice(usbDevice);
			} else {
				Log.d(TAG, "Missing permissions to open device\n");
			}

		}
		return null;
	}

	@Override
	public void onResume() {
		super.onResume();

		Intent intent = getIntent();
		Log.d(TAG, "intent: " + intent);
		String action = intent.getAction();

		UsbDevice device = (UsbDevice) intent
				.getParcelableExtra(UsbManager.EXTRA_DEVICE);
		if (UsbManager.ACTION_USB_DEVICE_ATTACHED.equals(action)) {
			setDevice(device);
		} else if (UsbManager.ACTION_USB_DEVICE_DETACHED.equals(action)) {
			setDevice(null);
		}

		HashMap<String, UsbDevice> deviceList = mUsbManager.getDeviceList();

		Log.d(TAG, "Listing keys" + deviceList.keySet());
		setDevice(device);

	}

	void setDevice(UsbDevice device) {
		if (myNativeThread == null) {
			myNativeThread = new Thread(this);
			myNativeThread.start();
		}
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
	}

	public void onClick(View v) {
	}

	@Override
	public void run() {
		Log.d(TAG, "Starting nativeMain");
		int retval = nativeRtlSdrTcp();
		Log.d(TAG, "Native main returned " + retval);
	}

	private native int nativeRtlSdrTest();
	private native int nativeRtlSdrTcp();

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}
}
