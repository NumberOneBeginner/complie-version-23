package org.cloudsky.cordovaPlugins;

import java.io.IOException;

import net.sourceforge.zbar.Config;
import net.sourceforge.zbar.Image;
import net.sourceforge.zbar.ImageScanner;
import net.sourceforge.zbar.Symbol;
import net.sourceforge.zbar.SymbolSet;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.hardware.Camera;
import android.hardware.Camera.AutoFocusCallback;
import android.hardware.Camera.PreviewCallback;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.view.Gravity;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.none.staff.activity.BaseActivity;
import com.none.staff.activity.zbarScanner.AnyEventType;

import de.greenrobot.event.EventBus;

public class ZBarScannerActivity extends BaseActivity
implements SurfaceHolder.Callback {

	// Config ----------------------------------------------------------

	private static int autoFocusInterval = 500; // Interval between AFcallback and next AF attempt.

	// Public Constants ------------------------------------------------

	public static final String EXTRA_QRVALUE = "qrValue";
	public static final int RESULT_ERROR = RESULT_FIRST_USER + 1;

	// State -----------------------------------------------------------

	private Camera camera;
	private Handler autoFocusHandler;
	private SurfaceView scannerSurface;
	private SurfaceHolder holder;
	private ImageScanner scanner;
	private int surfW, surfH;

	// For retrieving R.* resources, from the actual app package
	// (we can't use actual.application.package.R.* in our code as we
	// don't know the applciation package name when writing this plugin).
	private String package_name;
	private Resources resources;

	// Static initialisers (class) -------------------------------------

	static {
		// Needed by ZBar??
		System.loadLibrary("iconv");
	}

	// Activity Lifecycle ----------------------------------------------

	@Override
	public void onCreate (Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
//		if (Build.VERSION.SDK_INT >= 21) {
//			full(false);
//			 this.requestWindowFeature(Window.FEATURE_NO_TITLE);
//		}else{
//	        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
//	        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
//		}
		// Initiate instance variables
		autoFocusHandler = new Handler();
		scanner = new ImageScanner();
		scanner.setConfig(0, Config.X_DENSITY, 3);
		scanner.setConfig(0, Config.Y_DENSITY, 3);

		// Set content view
		setContentView(getResourceId("layout/zbarlayout"));

		// Create preview SurfaceView
		scannerSurface = new SurfaceView (this) {
			@Override
			public void onSizeChanged (int w, int h, int oldW, int oldH) {

				WindowManager wm = ZBarScannerActivity.this.getWindowManager();
				int width = wm.getDefaultDisplay().getWidth();
				int height = wm.getDefaultDisplay().getHeight();

				surfW = width;
				surfH = height;


				//				surfW = w;
				//				surfH = h;
				matchSurfaceToPreviewRatio();
			}
		};
		scannerSurface.setLayoutParams(new FrameLayout.LayoutParams(
				ViewGroup.LayoutParams.MATCH_PARENT,
				ViewGroup.LayoutParams.MATCH_PARENT,
				Gravity.TOP
				));
		scannerSurface.getHolder().addCallback(this);
		SurfaceHolder holder=scannerSurface.getHolder();
		holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);

		// Add preview SurfaceView to the screen
		((FrameLayout) findViewById(getResourceId("id/csZbarScannerView"))).addView(scannerSurface);
		findViewById(getResourceId("id/csZbarScannerOverlay")).bringToFront();
		//findViewById(getResourceId("id/csZbarScannerInstructions")).bringToFront();
		final Handler handler=new Handler(); 

		String des=this.getIntent().getStringExtra(ZBar.DES);
		if(des!=null&&!des.equals("null")){
			TextView tv=(TextView)findViewById(getResourceId("id/csZbarScannerInstructions"));
			//            tv.setText(des);
		}
	}

	@Override
	public void onResume ()
	{
		super.onResume();

		try {
			camera = Camera.open();
		} catch (Exception e){
			die("Error: Could not open the camera.");
//			finish();
			return;
		}
		tryStartPreview();
	}

	@Override
	public void onPause ()
	{
		releaseCamera();
		super.onPause();
	}

	@Override
	public void onDestroy ()
	{
		scanner.destroy();
		super.onDestroy();
	}

	// Event handlers --------------------------------------------------

	@Override
	public void onBackPressed ()
	{
		setResult(RESULT_CANCELED);
		super.onBackPressed();
	}

	// SurfaceHolder.Callback implementation ---------------------------

	@Override
	public void surfaceCreated (SurfaceHolder hld)
	{
		tryStopPreview();
		holder = hld;
		tryStartPreview();
	}

	@Override
	public void surfaceDestroyed (SurfaceHolder holder)
	{
		// No surface == no preview == no point being in this Activity.
		die("The camera surface was destroyed");
	}

	@Override
	public void surfaceChanged (SurfaceHolder hld, int fmt, int w, int h)
	{
		// Sanity check - holder must have a surface...
		if(hld.getSurface() == null) die("There is no camera surface");

		WindowManager wm = this.getWindowManager();
		int width = wm.getDefaultDisplay().getWidth();
		int height = wm.getDefaultDisplay().getHeight();

		surfW = width;
		surfH = height;

		matchSurfaceToPreviewRatio();

		tryStopPreview();
		holder = hld;
		tryStartPreview();
	}

	// Continuously auto-focus -----------------------------------------

	private AutoFocusCallback autoFocusCb = new AutoFocusCallback()
	{
		public void onAutoFocus(boolean success, Camera camera) {
			autoFocusHandler.postDelayed(doAutoFocus, autoFocusInterval);
		}
	};

	private Runnable doAutoFocus = new Runnable()
	{
		public void run() {
			if(camera != null) camera.autoFocus(autoFocusCb);
		}
	};

	// Camera callbacks ------------------------------------------------

	// Receives frames from the camera and checks for barcodes.
	private PreviewCallback previewCb = new PreviewCallback()
	{
		public void onPreviewFrame(byte[] data, Camera camera) {
			Camera.Parameters parameters = camera.getParameters();
			Camera.Size size = parameters.getPreviewSize();

			Image barcode = new Image(size.width, size.height, "Y800");
			barcode.setData(data);

			if (scanner.scanImage(barcode) != 0) {
				String qrValue = "";

				SymbolSet syms = scanner.getResults();
				for (Symbol sym : syms) {
					qrValue = sym.getData();

					// Return 1st found QR code value to the calling Activity.
					Intent result = new Intent ();
					result.putExtra(EXTRA_QRVALUE, qrValue);
					setResult(Activity.RESULT_OK, result);
					EventBus.getDefault().post(new AnyEventType(qrValue));  
					finish();
				}

			}
		}
	};

	// Misc ------------------------------------------------------------

	// finish() due to error
	private void die (String msg)
	{
//		setResult(RESULT_ERROR);
//		finish();
	}

	private int getResourceId (String typeAndName)
	{
		if(package_name == null) package_name = getApplication().getPackageName();
		if(resources == null) resources = getApplication().getResources();
		return resources.getIdentifier(typeAndName, null, package_name);
	}

	// Release the camera resources and state.
	private void releaseCamera ()
	{
		if (camera != null) {
			autoFocusHandler.removeCallbacks(doAutoFocus);
			camera.setPreviewCallback(null);
			camera.release();
			camera = null;
		}
	}

	// Match the aspect ratio of the preview SurfaceView with the camera's preview aspect ratio,
	// so that the displayed preview is not stretched/squashed.
	private void matchSurfaceToPreviewRatio () {
		if(camera == null) return;
		if(surfW == 0 || surfH == 0) return;

		// Resize SurfaceView to match camera preview ratio (avoid stretching).
		Camera.Parameters params = camera.getParameters();
		Camera.Size size = params.getPreviewSize();
		float previewRatio = (float) size.height / size.width; // swap h and w as the preview is rotated 90 degrees
		float surfaceRatio = (float) surfW / surfH;

		if(previewRatio > surfaceRatio) {
			scannerSurface.setLayoutParams(new FrameLayout.LayoutParams(
					surfW,
					Math.round((float) surfW / previewRatio),
					Gravity.TOP
					));
		} else if(previewRatio < surfaceRatio) {
			scannerSurface.setLayoutParams(new FrameLayout.LayoutParams(
					Math.round((float) surfH * previewRatio),
					surfH,
					Gravity.TOP
					));
		}
	}

	// Stop the camera preview safely.
	private void tryStopPreview () {
		// Stop camera preview before making changes.
		try {
			camera.stopPreview();
		} catch (Exception e){
			// Preview was not running. Ignore the error.
		}
	}

	// Start the camera preview if possible.
	// If start is attempted but fails, exit with error message.
	private void tryStartPreview () {
		if(holder != null) {
			try {
				// 90 degrees rotation for Portrait orientation Activity.
				camera.setDisplayOrientation(90);

				camera.setPreviewDisplay(holder);
				camera.setPreviewCallback(previewCb);
				camera.startPreview();
				camera.autoFocus(autoFocusCb);
			} catch (IOException e) {
				die("Could not start camera preview: " + e.getMessage());
			}
		}
	}
	/**
	 * statusbar
	 * @param enable
	 *
	 */
	private void full(boolean enable) {
		// TODO Auto-generated method stub
		WindowManager.LayoutParams p = this.getWindow().getAttributes();
		if (enable) {

			p.flags |= WindowManager.LayoutParams.FLAG_FULLSCREEN;

		} else {
			p.flags &= (~WindowManager.LayoutParams.FLAG_FULLSCREEN);


		}
		getWindow().setAttributes(p);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
	}

}
