/* Copyright (c) 2014, 2015 Qualcomm Technologies Inc

All rights reserved.

Redistribution and use in source and binary forms, with or without modification,
are permitted (subject to the limitations in the disclaimer below) provided that
the following conditions are met:

Redistributions of source code must retain the above copyright notice, this list
of conditions and the following disclaimer.

Redistributions in binary form must reproduce the above copyright notice, this
list of conditions and the following disclaimer in the documentation and/or
other materials provided with the distribution.

Neither the name of Qualcomm Technologies Inc nor the names of its contributors
may be used to endorse or promote products derived from this software without
specific prior written permission.

NO EXPRESS OR IMPLIED LICENSES TO ANY PARTY'S PATENT RIGHTS ARE GRANTED BY THIS
LICENSE. THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
"AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE
FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE. */

package org.firstinspires.ftc.robotcontroller.internal;
import android.app.ActionBar;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.res.Configuration;
import android.graphics.Color;
import android.hardware.Camera;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.google.blocks.ftcrobotcontroller.BlocksActivity;
import com.google.blocks.ftcrobotcontroller.ProgrammingModeActivity;
import com.google.blocks.ftcrobotcontroller.ProgrammingModeControllerImpl;
import com.google.blocks.ftcrobotcontroller.runtime.BlocksOpMode;
import com.qualcomm.ftccommon.AboutActivity;
import com.qualcomm.ftccommon.ClassManagerFactory;
import com.qualcomm.ftccommon.Device;
import com.qualcomm.ftccommon.FtcEventLoop;
import com.qualcomm.ftccommon.FtcEventLoopIdle;
import com.qualcomm.ftccommon.FtcRobotControllerService;
import com.qualcomm.ftccommon.FtcRobotControllerService.FtcRobotControllerBinder;
import com.qualcomm.ftccommon.FtcRobotControllerSettingsActivity;
import com.qualcomm.ftccommon.LaunchActivityConstantsList;
import com.qualcomm.ftccommon.ProgrammingModeController;
import com.qualcomm.ftccommon.Restarter;
import org.firstinspires.ftc.ftccommon.external.SoundPlayingRobotMonitor;
import com.qualcomm.ftccommon.UpdateUI;
import com.qualcomm.ftccommon.configuration.EditParameters;
import com.qualcomm.ftccommon.configuration.FtcLoadFileActivity;
import com.qualcomm.ftccommon.configuration.RobotConfigFile;
import com.qualcomm.ftccommon.configuration.RobotConfigFileManager;
import com.qualcomm.ftcrobotcontroller.CameraPreview;
import com.qualcomm.ftcrobotcontroller.R;
import com.qualcomm.hardware.HardwareFactory;
import com.qualcomm.robotcore.eventloop.EventLoopManager;
import com.qualcomm.robotcore.eventloop.opmode.OpModeRegister;
import com.qualcomm.robotcore.hardware.configuration.Utility;
import com.qualcomm.robotcore.robocol.Command;
import com.qualcomm.robotcore.robocol.PeerAppRobotController;
import com.qualcomm.robotcore.robot.Robot;
import com.qualcomm.robotcore.robot.RobotState;
import com.qualcomm.robotcore.robot.RobotStatus;
import com.qualcomm.robotcore.util.Dimmer;
import com.qualcomm.robotcore.util.ImmersiveMode;
import com.qualcomm.robotcore.util.ReadWriteFile;
import com.qualcomm.robotcore.util.RobotLog;
import com.qualcomm.robotcore.wifi.NetworkConnectionFactory;
import com.qualcomm.robotcore.wifi.NetworkType;
import com.qualcomm.robotcore.wifi.WifiDirectAssistant;

import org.firstinspires.ftc.robotcore.internal.AppUtil;
import org.firstinspires.inspection.RcInspectionActivity;
import org.swerverobotics.library.SynchronousOpMode;
import org.swerverobotics.library.internal.RobotStateTransitionNotifier;
import org.swerverobotics.library.internal.SwerveFtcEventLoop;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.net.ServerSocket;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;


public class FtcRobotControllerActivity extends Activity {


	///////////////////////////////////////////////////////////
	/////////               MODDED           //////////////////
	///////////////////////////////////////////////////////////





	private Camera mCamera;
	private CameraPreview mPreview;


	private Camera.PictureCallback mPicture = new Camera.PictureCallback() {

		@Override
		public void onPictureTaken(byte[] data, Camera camera) {
			camera.startPreview();
			File pictureFile = getOutputMediaFile();
			if (pictureFile == null) {
//                Log.d(TAG, "Error creating media file, check storage permissions: " +
//                        e.getMessage());
				return;
			}

			try {
				FileOutputStream fos = new FileOutputStream(pictureFile);
				fos.write(data);
				fos.close();


			} catch (FileNotFoundException e) {
				Log.d("FileNotFounc", "File not found: " + e.getMessage());
			} catch (IOException e) {
				Log.d("IOException", "Error accessing file: " + e.getMessage());
			}



		}
	};



	/** Create a File for saving an image or video */
	private static File getOutputMediaFile(){
		// To be safe, you should check that the SDCard is mounted
		// using Environment.getExternalStorageState() before doing this.

		int imageNumber = 0;

		// Retrieve file.
		File file = new File("/sdcard/Pictures","imageNumber");
		StringBuilder text = new StringBuilder();
		// Attempt to load line from file into the buffer.
		try {
			BufferedReader br = new BufferedReader(new FileReader(file));
			String line;
			// Ensure that the first line is not null.
			while ((line = br.readLine()) != null) {
				text.append(line);
			}
			// Close the buffer reader
			br.close();
		}
		// Catch exceptions... Or don't because that would require effort.
		catch (IOException e) {
		}

		// Provide in a more user friendly form.
		if(text.toString() == "") {
			imageNumber = 0;
		} else {
			imageNumber = Integer.parseInt(text.toString()) + 1;
		}

		try {
			File imageNumberFile = new File("/sdcard/Pictures", "imageNumber");
			FileOutputStream outputStream;
			//outputStream = openFileOutput(filename, Context.MODE_PRIVATE);
			outputStream = new FileOutputStream(file,false);
			outputStream.write(String.valueOf(imageNumber).getBytes());
			outputStream.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

		File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
				Environment.DIRECTORY_PICTURES), "processing");
		// This location works best if you want the created images to be shared
		// between applications and persist after your app has been uninstalled.

		// Create the storage directory if it does not exist
		if (! mediaStorageDir.exists()){
			if (! mediaStorageDir.mkdirs()){
				Log.d("processing", "failed to create directory");
				return null;
			}
		}

		// Create a media file name
		File mediaFile;
		mediaFile = new File(mediaStorageDir.getPath() + File.separator + "proc" + imageNumber + ".jpg");
		return mediaFile;
	}


	private static int getFrontCameraId(){
		int camId = -1;
		int numberOfCameras = Camera.getNumberOfCameras();
		Camera.CameraInfo ci = new Camera.CameraInfo();

		for(int i = 0;i < numberOfCameras;i++){
			Camera.getCameraInfo(i,ci);
			if(ci.facing == Camera.CameraInfo.CAMERA_FACING_FRONT){
				camId = i;
			}
		}

		return camId;
	}
	/** A safe way to get an instance of the Camera object. */
	public static Camera getCameraInstance() {
		Camera c = null;
		try {
			c = Camera.open(getFrontCameraId()); // attempt to get a Camera instance
		} catch (Exception e) {
			// Camera is not available (in use or does not exist)
			Log.d("uhhh", "yep couldnt get camera");
		}
		return c; // returns null if camera is unavailable
	}

	private void releaseCamera(){
		if (mCamera != null){
			mCamera.release();        // release the camera for other applications
			mCamera = null; // Create an instance of Camera
			mCamera = getCameraInstance();

			// Create our Preview view and set it as the content of our activity.
			mPreview = new CameraPreview(this, mCamera);
			FrameLayout preview = (FrameLayout) findViewById(R.id.camera_preview);
			preview.addView(mPreview);
		}
	}



	///////////////////////////////////////////////////////////
	/////////              END MODDED        //////////////////
	///////////////////////////////////////////////////////////



  public static final String TAG = "RCActivity";

  private static final int REQUEST_CONFIG_WIFI_CHANNEL = 1;
  private static final boolean USE_DEVICE_EMULATION = false;
  private static final int NUM_GAMEPADS = 2;

  public static final String NETWORK_TYPE_FILENAME = "ftc-network-type.txt";

  protected WifiManager.WifiLock wifiLock;
  protected RobotConfigFileManager cfgFileMgr;

  protected ProgrammingModeController programmingModeController;

  protected UpdateUI.Callback callback;
  protected Context context;
  protected Utility utility;
  protected AppUtil appUtil = AppUtil.getInstance();

  protected ImageButton buttonMenu;
  protected TextView textDeviceName;
  protected TextView textNetworkConnectionStatus;
  protected TextView textRobotStatus;
  protected TextView[] textGamepad = new TextView[NUM_GAMEPADS];
  protected TextView textOpMode;
  protected TextView textErrorMessage;
  protected ImmersiveMode immersion;

  protected SwerveUpdateUIHook updateUI;
  protected Dimmer dimmer;
  protected LinearLayout entireScreenLayout;

  protected FtcRobotControllerService controllerService;
  protected NetworkType networkType;

  protected FtcEventLoop eventLoop;
  protected Queue<UsbDevice> receivedUsbAttachmentNotifications;

  protected class RobotRestarter implements Restarter {

    public void requestRestart() {
      requestRobotRestart();
    }

  }

  protected ServiceConnection connection = new ServiceConnection() {
    @Override
    public void onServiceConnected(ComponentName name, IBinder service) {
      FtcRobotControllerBinder binder = (FtcRobotControllerBinder) service;
      onServiceBind(binder.getService());
    }

    @Override
    public void onServiceDisconnected(ComponentName name) {
      RobotLog.vv(FtcRobotControllerService.TAG, "%s.controllerService=null", TAG);
      controllerService = null;
    }
  };

  @Override
  protected void onNewIntent(Intent intent) {
    super.onNewIntent(intent);

    if (UsbManager.ACTION_USB_DEVICE_ATTACHED.equals(intent.getAction())) {
      UsbDevice usbDevice = intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);
      if (usbDevice != null) {  // paranoia
        // We might get attachment notifications before the event loop is set up, so
        // we hold on to them and pass them along only when we're good and ready.
        if (receivedUsbAttachmentNotifications != null) { // *total* paranoia
          receivedUsbAttachmentNotifications.add(usbDevice);
          passReceivedUsbAttachmentsToEventLoop();
        }
      }
    }
  }

  protected void passReceivedUsbAttachmentsToEventLoop() {
    if (this.eventLoop != null) {
      for (;;) {
        UsbDevice usbDevice = receivedUsbAttachmentNotifications.poll();
        if (usbDevice == null)
          break;
        this.eventLoop.onUsbDeviceAttached(usbDevice);
      }
    }
    else {
      // Paranoia: we don't want the pending list to grow without bound when we don't
      // (yet) have an event loop
      while (receivedUsbAttachmentNotifications.size() > 100) {
        receivedUsbAttachmentNotifications.poll();
      }
    }
  }

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    RobotLog.vv(TAG, "onCreate()");

    receivedUsbAttachmentNotifications = new ConcurrentLinkedQueue<UsbDevice>();
    eventLoop = null;

    setContentView(R.layout.activity_ftc_controller);

	  ///////////////////////////////////////////////////////////
	  /////////               MODDED           //////////////////
	  ///////////////////////////////////////////////////////////
		String side;

		  // Retrieve file.
		  File file = new File("/sdcard/Pictures","prefs");
		  StringBuilder text = new StringBuilder();
		  // Attempt to load line from file into the buffer.
		  try {
			  BufferedReader br = new BufferedReader(new FileReader(file));
			  String line;
			  // Ensure that the first line is not null.
			  while ((line = br.readLine()) != null) {
				  text.append(line);
			  }
			  // Close the buffer reader
			  br.close();
		  }
		  // Catch exceptions... Or don't because that would require effort.
		  catch (IOException e) {
		  }

		  // Provide in a more user friendly form.
		  side = text.toString();


	  Button sideColor = (Button) findViewById(R.id.sideColor);

	  if (side == "blue") {
		  sideColor.setBackgroundColor(Color.BLUE);
	  } else if (side == "red") {
		  sideColor.setBackgroundColor(Color.RED);
	  } else {
		  sideColor.setBackgroundColor(Color.WHITE);
	  }



	  Thread thread = new Thread(new Runnable(){
		@Override
		public void run() {
			ServerSocket soc;
			soc = null;
			Log.d("thread", "###################################I am running!");
			try {
				//Inet6Address i6 = new Inet6Address("::1");
				soc = new ServerSocket(2856, 50);

				//Yes this solution is incredibly ratchet... If you have a better solution please tell me!

				soc.accept();
				mCamera.takePicture(null, null, mPicture);
				Log.d("###############lol", "end");
				soc.accept();
				mCamera.takePicture(null, null, mPicture);
				soc.accept();
				mCamera.takePicture(null, null, mPicture);
				soc.accept();
				mCamera.takePicture(null, null, mPicture);
				soc.accept();
				mCamera.takePicture(null, null, mPicture);
				soc.accept();
				mCamera.takePicture(null, null, mPicture);
				soc.accept();
				mCamera.takePicture(null, null, mPicture);
				soc.accept();
				mCamera.takePicture(null, null, mPicture);
				Log.d("ERROR", "OUT OF PICTURES, PLEASE RESTART APPLICATION!!!!!");
				soc.close();

			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	});

	  thread.start();



	  // Create an instance of Camera
	  mCamera = getCameraInstance();
	  mCamera.setDisplayOrientation(90);
	  // Create our Preview view and set it as the content of our activity.
	  mPreview = new CameraPreview(this, mCamera);
	  FrameLayout preview = (FrameLayout) findViewById(R.id.camera_preview);
	  preview.addView(mPreview);


//    Button captureButton = (Button) findViewById(R.id.capture);
//    captureButton.setOnClickListener(
//            new View.OnClickListener() {
//              @Override
//              public void onClick(View v) {


	  //mCamera.takePicture(null, null, mPicture)



//              }
//            }
//    );

	  //mCamera.takePicture(null, null, mPicture);




	  ///////////////////////////////////////////////////////////
	  /////////               END MODDED       //////////////////
	  ///////////////////////////////////////////////////////////


	utility = new Utility(this);
	context = this;
    utility = new Utility(this);
    appUtil.setThisApp(new PeerAppRobotController(context));

    entireScreenLayout = (LinearLayout) findViewById(R.id.entire_screen);
    buttonMenu = (ImageButton) findViewById(R.id.menu_buttons);
	ImageButton alternateMenu = (ImageButton) findViewById(R.id.alternate_menu);
    buttonMenu.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        AppUtil.getInstance().openOptionsMenuFor(FtcRobotControllerActivity.this);
      }
    });
	alternateMenu.setOnClickListener(new View.OnClickListener() {
	  @Override
	  public void onClick(View v) {
		  openOptionsMenu();
	  }
	});

    BlocksOpMode.setActivityAndWebView(this, (WebView) findViewById(R.id.webViewBlocksRuntime));

    ClassManagerFactory.processClasses();
    cfgFileMgr = new RobotConfigFileManager(this);

    // Clean up 'dirty' status after a possible crash
    RobotConfigFile configFile = cfgFileMgr.getActiveConfig();
    if (configFile.isDirty()) {
      configFile.markClean();
      cfgFileMgr.setActiveConfig(false, configFile);
    }

    textDeviceName = (TextView) findViewById(R.id.textDeviceName);
    textNetworkConnectionStatus = (TextView) findViewById(R.id.textNetworkConnectionStatus);
    textRobotStatus = (TextView) findViewById(R.id.textRobotStatus);
    textOpMode = (TextView) findViewById(R.id.textOpMode);
    textErrorMessage = (TextView) findViewById(R.id.textErrorMessage);
    textGamepad[0] = (TextView) findViewById(R.id.textGamepad1);
    textGamepad[1] = (TextView) findViewById(R.id.textGamepad2);
    immersion = new ImmersiveMode(getWindow().getDecorView());
    dimmer = new Dimmer(this);
    dimmer.longBright();

    programmingModeController = new ProgrammingModeControllerImpl(
        this, (TextView) findViewById(R.id.textRemoteProgrammingMode));

    updateUI = createUpdateUI();
    callback = createUICallback(updateUI);

    PreferenceManager.setDefaultValues(this, R.xml.preferences, false);

    WifiManager wifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
    wifiLock = wifiManager.createWifiLock(WifiManager.WIFI_MODE_FULL_HIGH_PERF, "");

    hittingMenuButtonBrightensScreen();

    if (USE_DEVICE_EMULATION) { HardwareFactory.enableDeviceEmulation(); }

    // save 4MB of logcat to the SD card
    RobotLog.writeLogcatToDisk(this, 4 * 1024);
    wifiLock.acquire();
    callback.networkConnectionUpdate(WifiDirectAssistant.Event.DISCONNECTED);
    bindToService();
  }

  protected SwerveUpdateUIHook createUpdateUI() {
    Restarter restarter = new RobotRestarter();
    SwerveUpdateUIHook result = new SwerveUpdateUIHook(this, dimmer);
    result.setRestarter(restarter);
    result.setTextViews(textNetworkConnectionStatus, textRobotStatus, textGamepad, textOpMode, textErrorMessage, textDeviceName);
    return result;
  }

  protected UpdateUI.Callback createUICallback(SwerveUpdateUIHook updateUI) {
    UpdateUI.Callback result = updateUI.new CallbackHook();
    result.setStateMonitor(new SoundPlayingRobotMonitor());
    return result;
  }

  @Override
  protected void onStart() {
    super.onStart();
    RobotLog.vv(TAG, "onStart()");

    // Undo the effects of shutdownRobot() that we might have done in onStop()
    updateUIAndRequestRobotSetup();

    cfgFileMgr.getActiveConfigAndUpdateUI();

    entireScreenLayout.setOnTouchListener(new View.OnTouchListener() {
		@Override
		public boolean onTouch(View v, MotionEvent event) {
			dimmer.handleDimTimer();
			return false;
		}
	});

    wifiLock.acquire();
      @Override
      public boolean onTouch(View v, MotionEvent event) {
        dimmer.handleDimTimer();
        return false;
      }
    });
	}

  @Override
  protected void onResume() {
    super.onResume();
    RobotLog.vv(TAG, "onResume()");
    readNetworkType(NETWORK_TYPE_FILENAME);
  }

  @Override
  public void onPause() {
    super.onPause();
    RobotLog.vv(TAG, "onPause()");
    if (programmingModeController.isActive()) {
      programmingModeController.stopProgrammingMode();
    }
  }

  @Override
  protected void onStop() {
    // Note: this gets called even when the configuration editor is launched. That is, it gets
    // called surprisingly often.
    super.onStop();
    RobotLog.vv(TAG, "onStop()");

    // We *do* shutdown the robot even when we go into configuration editing
    if (controllerService != null) controllerService.shutdownRobot();
  }

  @Override
  public void onDestroy() {
    super.onDestroy();
    RobotLog.vv(TAG, "onDestroy()");

    unbindFromService();
    wifiLock.release();
    RobotLog.cancelWriteLogcatToDisk(this);
  }

  protected void bindToService() {
    RobotLog.vv(TAG, "bindToService()");
    readNetworkType(NETWORK_TYPE_FILENAME);
    Intent intent = new Intent(this, FtcRobotControllerService.class);
    intent.putExtra(NetworkConnectionFactory.NETWORK_CONNECTION_TYPE, networkType);
    bindService(intent, connection, Context.BIND_AUTO_CREATE);
  }

  protected void unbindFromService() {
    if (controllerService != null) {
      unbindService(connection);
    }
  }

  public void writeNetworkTypeFile(String fileName, String fileContents){
    ReadWriteFile.writeFile(AppUtil.FIRST_FOLDER, fileName, fileContents);
  }

  protected void readNetworkType(String fileName) {
    NetworkType defaultNetworkType;
    File directory = RobotConfigFileManager.CONFIG_FILES_DIR;
    File networkTypeFile = new File(directory, fileName);
    if (!networkTypeFile.exists()) {
      if (Build.MODEL.equals(Device.MODEL_410C)) {
        defaultNetworkType = NetworkType.SOFTAP;
      } else {
        defaultNetworkType = NetworkType.WIFIDIRECT;
      }
      writeNetworkTypeFile(NETWORK_TYPE_FILENAME, defaultNetworkType.toString());
    }

    String fileContents = readFile(networkTypeFile);
    networkType = NetworkConnectionFactory.getTypeFromString(fileContents);
    programmingModeController.setCurrentNetworkType(networkType);
  }

  private String readFile(File file) {
    return ReadWriteFile.readFile(file);
  }

  @Override
  public void onWindowFocusChanged(boolean hasFocus){
    super.onWindowFocusChanged(hasFocus);
    // When the window loses focus (e.g., the action overflow is shown),
    // cancel any pending hide action. When the window gains focus,
    // hide the system UI.
    if (hasFocus) {
      if (ImmersiveMode.apiOver19()){
        // Immersive flag only works on API 19 and above.
        immersion.hideSystemUI();
      }
    } else {
      immersion.cancelSystemUIHide();
    }
  }


  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    getMenuInflater().inflate(R.menu.ftc_robot_controller, menu);
    return true;
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    int id = item.getItemId();

    if (id == R.id.action_programming_mode) {
      if (cfgFileMgr.getActiveConfig().isNoConfig()) {
        // Tell the user they must configure the robot before starting programming mode.
        AppUtil.getInstance().showToast(
            context, context.getString(R.string.toastConfigureRobotBeforeProgrammingMode));
      } else {
        Intent programmingModeIntent = new Intent(ProgrammingModeActivity.launchIntent);
        programmingModeIntent.putExtra(
            LaunchActivityConstantsList.PROGRAMMING_MODE_ACTIVITY_NETWORK_TYPE, networkType);
        startActivity(programmingModeIntent);
      }
      return true;
    } else if (id == R.id.action_inspection_mode) {
      Intent inspectionModeIntent = new Intent(RcInspectionActivity.rcLaunchIntent);
      startActivity(inspectionModeIntent);
      return true;
    }
    else if (id == R.id.action_blocks) {
      Intent blocksIntent = new Intent(BlocksActivity.launchIntent);
      startActivity(blocksIntent);
      return true;
    }
    else if (id == R.id.action_restart_robot) {
      dimmer.handleDimTimer();
      AppUtil.getInstance().showToast(context, context.getString(R.string.toastRestartingRobot));
      requestRobotRestart();
      return true;
	}
    else if (id == R.id.action_configure_robot) {
      EditParameters parameters = new EditParameters();
      Intent intentConfigure = new Intent(FtcLoadFileActivity.launchIntent);
      parameters.putIntent(intentConfigure);
      startActivityForResult(intentConfigure, LaunchActivityConstantsList.FTC_CONFIGURE_REQUEST_CODE_ROBOT_CONTROLLER);
    }
    else if (id == R.id.action_settings) {
      Intent settingsIntent = new Intent(FtcRobotControllerSettingsActivity.launchIntent);
      startActivityForResult(settingsIntent, LaunchActivityConstantsList.FTC_CONFIGURE_REQUEST_CODE_ROBOT_CONTROLLER);
      return true;
    }
    else if (id == R.id.action_about) {
      Intent intent = new Intent(AboutActivity.launchIntent);
      intent.putExtra(LaunchActivityConstantsList.ABOUT_ACTIVITY_CONNECTION_TYPE, networkType);
      startActivity(intent);
      return true;
    }
    else if (id == R.id.action_exit_app) {
      finish();
      return true;
    }

   return super.onOptionsItemSelected(item);
  }

  @Override
  public void onConfigurationChanged(Configuration newConfig) {
    super.onConfigurationChanged(newConfig);
    // don't destroy assets on screen rotation
  }

  @Override
  protected void onActivityResult(int request, int result, Intent intent) {
    if (request == REQUEST_CONFIG_WIFI_CHANNEL) {
      if (result == RESULT_OK) {
        AppUtil.getInstance().showToast(context, context.getString(R.string.toastWifiConfigurationComplete));
      }
    }
    if (request == LaunchActivityConstantsList.FTC_CONFIGURE_REQUEST_CODE_ROBOT_CONTROLLER) {
      // We always do a refresh, whether it was a cancel or an OK, for robustness
      cfgFileMgr.getActiveConfigAndUpdateUI();
    }
  }

  public void onServiceBind(FtcRobotControllerService service) {
    RobotLog.vv(FtcRobotControllerService.TAG, "%s.controllerService=bound", TAG);
    controllerService = service;
    updateUI.setControllerService(controllerService);

    updateUIAndRequestRobotSetup();
  }

  private void updateUIAndRequestRobotSetup() {
    if (controllerService != null) {
      callback.networkConnectionUpdate(controllerService.getNetworkConnectionStatus());
      callback.updateRobotStatus(controllerService.getRobotStatus());
      requestRobotSetup();
    }
  }

  private void requestRobotSetup() {
    if (controllerService == null) return;

    HardwareFactory factory;
    RobotConfigFile file = cfgFileMgr.getActiveConfigAndUpdateUI();
    HardwareFactory hardwareFactory = new HardwareFactory(context);
    hardwareFactory.setXmlPullParser(file.getXml());
    factory = hardwareFactory;

    eventLoop = new SwerveFtcEventLoop(factory, createOpModeRegister(), callback, this, programmingModeController);
    FtcEventLoopIdle idleLoop = new FtcEventLoopIdle(factory, callback, this, programmingModeController);

    controllerService.setCallback(callback);
    controllerService.setupRobot(eventLoop, idleLoop);

    passReceivedUsbAttachmentsToEventLoop();
  }

  protected OpModeRegister createOpModeRegister() {
    return new FtcOpModeRegister();
  }

  private void requestRobotShutdown() {
    if (controllerService == null) return;
    controllerService.shutdownRobot();
  }

  private void requestRobotRestart() {
    requestRobotShutdown();
    requestRobotSetup();
  }

  protected void hittingMenuButtonBrightensScreen() {
    ActionBar actionBar = getActionBar();
    if (actionBar != null) {
      actionBar.addOnMenuVisibilityListener(new ActionBar.OnMenuVisibilityListener() {
        @Override
        public void onMenuVisibilityChanged(boolean isVisible) {
          if (isVisible) {
            dimmer.handleDimTimer();
          }
        }
      });
    }
  }
    // ==================================================================================
    // Hooking infrastructure (Swerve)
    //
    // The code below has been added to the stock FtcRobotControllerActivity in order to hook
    // into state transitions of various kinds that happen within the robot controller application.
    // Most of what's here is of necessity pretty obscure and technical in nature, but
    // fortunately those details won't be of significance to most.

    static class SwerveEventLoopMonitorHook implements EventLoopManager.EventLoopMonitor
    // Hook to receive event monitor state transition
        {
        //------------------------------------------------------------------------------------------
        // State
        //------------------------------------------------------------------------------------------

        // The previously installed hook
        final EventLoopManager.EventLoopMonitor prevMonitor;

        // The activity in which we live
        final FtcRobotControllerActivity activity;

        //------------------------------------------------------------------------------------------
        // Construction
        //------------------------------------------------------------------------------------------

        SwerveEventLoopMonitorHook(EventLoopManager.EventLoopMonitor prevMonitor, FtcRobotControllerActivity activity)
            {
            this.prevMonitor = prevMonitor;
            this.activity    = activity;
            }

        // Make sure we're installed in the in the hook of the current event loop
        public synchronized static void installIfNecessary(FtcRobotControllerService service, FtcRobotControllerActivity activity)
            {
            if (service == null)
                return;

            Robot robot = service.getRobot();
            if (robot == null)
                return;

            EventLoopManager eventLoopManager = robot.eventLoopManager;
            if (eventLoopManager == null)
                return;

            // Ok, the EventLoopManager is up and running. Install our hooks if we haven't already done so

            EventLoopManager.EventLoopMonitor monitor = eventLoopManager.getMonitor();
            if (monitor != null)
                {
                if (monitor instanceof SwerveEventLoopMonitorHook)
                    {
                    // we're already installed
                    }
                else
                    {
                    SwerveEventLoopMonitorHook newMonitor = new SwerveEventLoopMonitorHook(monitor, activity);
                    eventLoopManager.setMonitor(newMonitor);
                    Log.v(SynchronousOpMode.LOGGING_TAG, "installed SwerveEventLoopMonitorHook");
                    }
                }
            }

        //------------------------------------------------------------------------------------------
        // Notifications
        //------------------------------------------------------------------------------------------

        @Override
        public void onStateChange(RobotState newState)
            {
            this.prevMonitor.onStateChange(newState);
            RobotStateTransitionNotifier.onRobotStateChange(newState);
            }

        @Override public void onTelemetryTransmitted()
            {
            this.prevMonitor.onTelemetryTransmitted();
            }

        @Override public void onPeerConnected(boolean peerLikelyChanged)
            {
            this.prevMonitor.onPeerConnected(peerLikelyChanged);
            }

        @Override public void onPeerDisconnected()
            {
            this.prevMonitor.onPeerDisconnected();
            }
        }

    class SwerveUpdateUIHook extends UpdateUI
    // Hook used to augment the user interface
        {
        //------------------------------------------------------------------------------------------
        // State
        //------------------------------------------------------------------------------------------

        FtcRobotControllerActivity activity;
        FtcRobotControllerService  controllerService;

        //------------------------------------------------------------------------------------------
        // Construction
        //------------------------------------------------------------------------------------------

        SwerveUpdateUIHook(FtcRobotControllerActivity activity, Dimmer dimmer)
            {
            super(activity, dimmer);
            this.activity = activity;
            this.controllerService = null;
            }

        @Override
        public void setControllerService(FtcRobotControllerService controllerService)
            {
            super.setControllerService(controllerService);
            this.controllerService = controllerService;
            }

        //------------------------------------------------------------------------------------------
        // Operations
        //------------------------------------------------------------------------------------------

        class CallbackHook extends UpdateUI.Callback
            {
            //--------------------------------------------------------------------------------------
            // Operations
            //--------------------------------------------------------------------------------------

            @Override
            public void updateRobotStatus(final RobotStatus status)
                // Called from FtcRobotControllerService.reportRobotStatus(). That is called from many
                // places, but in particular it is called *immediately* after RobotFactory.createRobot()
                // is called in FtcRobotControllerService.run(); that ensures we get to see the raw
                // initial state.
                {
                // Make sure we get to see all the robot state transitions
                SwerveEventLoopMonitorHook.installIfNecessary(controllerService, FtcRobotControllerActivity.this);

                super.updateRobotStatus(status);
                RobotStateTransitionNotifier.onRobotUpdate(status);
                }
            }
        }
}