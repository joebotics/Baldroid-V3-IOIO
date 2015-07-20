package ioio.examples.simple;
// BALANCE ACT 4 + MOGA 2
import ioio.lib.api.PwmOutput;
import ioio.lib.api.exception.ConnectionLostException;
import ioio.lib.util.BaseIOIOLooper;
import ioio.lib.util.IOIOLooper;
import ioio.lib.util.android.IOIOActivity;
import android.os.Bundle;
import android.widget.TextView;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.widget.SeekBar;
import com.bda.controller.Controller;
import com.bda.controller.ControllerListener;
import com.bda.controller.KeyEvent;
import com.bda.controller.MotionEvent;
import com.bda.controller.StateEvent;


public class IOIOSimpleApp extends IOIOActivity implements SensorEventListener{

	private SensorManager mSensorManager;
	private Sensor mRotVectSensor;
	private float[] orientationVals=new float[3];
	private float[] mRotationMatrix=new float[16];
	private TextView textView_Current_Angle;
	private TextView textView_Tilt_adjuster;
	private TextView textView_kP_adjuster;
	private TextView textView_kI_adjuster;
	private TextView textView_kD_adjuster;
	private SeekBar seekBar_Tilt_adjuster;
	private SeekBar seekBar_kP_adjuster;
	private SeekBar seekBar_kI_adjuster;
	private SeekBar seekBar_kD_adjuster;
	Controller mController = null;
	final ExampleControllerListener mListener = new ExampleControllerListener();
	final ExamplePlayer mPlayer = new ExamplePlayer(0.0f, 1.0f, 0.0f);
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		
		mController = Controller.getInstance(this);
		mController.init();
		mController.setListener(mListener, null);
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		
	    mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
	    mRotVectSensor=mSensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR);

	    textView_Current_Angle = (TextView) findViewById(R.id.TextView_CurrentAngle_Value);
	    
	    textView_Tilt_adjuster = (TextView) findViewById(R.id.TextView_Tilt_adjusterValue);
		seekBar_Tilt_adjuster = (SeekBar) findViewById(R.id.SeekBar_Tilt_adjuster);

	    textView_kP_adjuster = (TextView) findViewById(R.id.TextView_kP_adjusterValue);
		seekBar_kP_adjuster = (SeekBar) findViewById(R.id.SeekBar_kP_adjuster);

	    textView_kI_adjuster = (TextView) findViewById(R.id.TextView_kI_adjusterValue);
		seekBar_kI_adjuster = (SeekBar) findViewById(R.id.SeekBar_kI_adjuster);		

	    textView_kD_adjuster = (TextView) findViewById(R.id.TextView_kD_adjusterValue);
		seekBar_kD_adjuster = (SeekBar) findViewById(R.id.SeekBar_kD_adjuster);	
		
		seekBar_Tilt_adjuster.setProgress(500);
		seekBar_kP_adjuster.setProgress(0);
		seekBar_kI_adjuster.setProgress(0);
		seekBar_kD_adjuster.setProgress(0);
		
		enableUi(false);
	}
	
	
	public class ExampleControllerListener implements ControllerListener {

		@Override
		public void onKeyEvent(KeyEvent event) {

				switch (event.getKeyCode()) {
				case KeyEvent.KEYCODE_BUTTON_X:
					seekBar_Tilt_adjuster.setProgress(seekBar_Tilt_adjuster.getProgress() - 2);
					break;
				case KeyEvent.KEYCODE_BUTTON_B:
					seekBar_Tilt_adjuster.setProgress(seekBar_Tilt_adjuster.getProgress() + 2);
					break;
				case KeyEvent.KEYCODE_BUTTON_Y:
					seekBar_kP_adjuster.setProgress(seekBar_kP_adjuster.getProgress() + 2);
					break;
				case KeyEvent.KEYCODE_BUTTON_A:
					seekBar_kP_adjuster.setProgress(seekBar_kP_adjuster.getProgress() - 2);
					break;					
				case KeyEvent.KEYCODE_DPAD_UP:
					seekBar_kI_adjuster.setProgress(seekBar_kI_adjuster.getProgress() + 2);
					break;
				case KeyEvent.KEYCODE_DPAD_DOWN:
					seekBar_kI_adjuster.setProgress(seekBar_kI_adjuster.getProgress() - 2);
					break;		
				case KeyEvent.KEYCODE_DPAD_RIGHT:
					seekBar_kD_adjuster.setProgress(seekBar_kD_adjuster.getProgress() + 2);
					break;
				case KeyEvent.KEYCODE_DPAD_LEFT:
					seekBar_kD_adjuster.setProgress(seekBar_kD_adjuster.getProgress() - 2);
					break;							
				}
		}

		@Override
		public void onMotionEvent(MotionEvent event) {
			mPlayer.mAxisX = event.getAxisValue(MotionEvent.AXIS_X);
			mPlayer.mAxisY = event.getAxisValue(MotionEvent.AXIS_Y);
			mPlayer.mAxisZ = event.getAxisValue(MotionEvent.AXIS_Z);
			mPlayer.mAxisRZ = event.getAxisValue(MotionEvent.AXIS_RZ);
		}

		@Override
		public void onStateEvent(StateEvent event) {
			switch (event.getState()) {
			case StateEvent.STATE_CONNECTION:
				mPlayer.mConnection = event.getAction();
				break;
			}
		}
	}

	
	
	public class ExamplePlayer {
		static final float DEFAULT_SCALE = 4.0f;
		static final float DEFAULT_X = 0.0f;
		static final float DEFAULT_Y = 0.0f;

		boolean gotPadVersion = false;

		public int mConnection = StateEvent.ACTION_DISCONNECTED;
		public int mControllerVersion = StateEvent.STATE_UNKNOWN;
		public int mButtonA = KeyEvent.ACTION_UP;
		public int mButtonB = KeyEvent.ACTION_UP;
		public int mButtonX = KeyEvent.ACTION_UP;
		public int mButtonY = KeyEvent.ACTION_UP;
		public int DpadUp = KeyEvent.ACTION_UP;
		public int DpadDown = KeyEvent.ACTION_UP;
		public int DpadLeft = KeyEvent.ACTION_UP;
		public int DpadRight = KeyEvent.ACTION_UP;
		
		public float mAxisX = 0.0f;
		public float mAxisY = 0.0f;
		public float mAxisZ = 0.0f;
		public float mAxisRZ = 0.0f;
		final float mR;
		final float mG;
		final float mB;
		float mScale = DEFAULT_SCALE;
		float mX = DEFAULT_X;
		float mY = DEFAULT_Y;

		public ExamplePlayer(float r, float g, float b) {
			mR = r;
			mG = g;
			mB = b;
		}
	}
	
	
	
	

	public class Looper extends BaseIOIOLooper {
		private PwmOutput LeftWheel;
		private PwmOutput RightWheel;
		private PwmOutput LeftArm;
		private PwmOutput RightArm;
		
		private int currentAngle;
		private int previousAngle;
		
		private int P = 0;
		private int I = 0;
		private int D = 0;		
		private int PID = 0;
		
		private int D_delta;
		private int I_delta;
			
		public int seekbar_tilt_adjuster_value = 500;
		private int seekbar_kP_adjuster_value = 0;
		private int seekbar_kI_adjuster_value = 0;
		private int seekbar_kD_adjuster_value = 0;
		
		
		
		@Override
		public void setup() throws ConnectionLostException {
			
			LeftArm = ioio_.openPwmOutput(13, 50);
			RightArm = ioio_.openPwmOutput(12, 50);
			LeftWheel = ioio_.openPwmOutput(10, 100); 
			RightWheel = ioio_.openPwmOutput(11, 100);
			enableUi(true);
		}

		@Override
		public void loop() throws ConnectionLostException, InterruptedException {
			seekbar_tilt_adjuster_value = seekBar_Tilt_adjuster.getProgress() - 500;
			seekbar_kP_adjuster_value   = seekBar_kP_adjuster.getProgress();
			seekbar_kI_adjuster_value   = seekBar_kI_adjuster.getProgress();
			seekbar_kD_adjuster_value   = seekBar_kD_adjuster.getProgress();
			
			
			//currentAngle =  (Math.round(orientationVals[1]  * 100)) + seekbar_tilt_adjuster_value - Math.round(mPlayer.mAxisY*10) - Math.round(mPlayer.mAxisRZ*10);
			currentAngle =  (Math.round(orientationVals[1]  * 100)) + seekbar_tilt_adjuster_value;

			
			D_delta = currentAngle - previousAngle;
			I_delta = I_delta + D_delta;
			
			P = (seekbar_kP_adjuster_value * currentAngle) / 2000;
			I = (seekbar_kI_adjuster_value * I_delta) / 4000;
			D = (seekbar_kD_adjuster_value * D_delta) / 4000;
				
			//if (I < -100) { 
			//	I = -100;
			//	} else if (I>100) {
			//		I=100;
			//	}
			
			PID = P + I + D;

			previousAngle = currentAngle;

			RightArm.setPulseWidth(1590 - Math.round(mPlayer.mAxisY*900));
			LeftArm.setPulseWidth(1540 + Math.round(mPlayer.mAxisRZ*900));
 			
			RightWheel.setPulseWidth(1500 + Math.round(mPlayer.mAxisY*35) + PID);
			LeftWheel.setPulseWidth(1502 - Math.round(mPlayer.mAxisRZ*35) - PID);

			
			//LeftWheel.setPulseWidth(1502 - PID);
			//RightWheel.setPulseWidth(1500 + PID);
			
			//setText_current_angle(Integer.toString(currentAngle));
			//setText_tilt_adjuster(Integer.toString(seekbar_tilt_adjuster_value));
			//setText_kP_adjuster(Integer.toString(seekbar_kP_adjuster_value));
			//setText_kI_adjuster(Integer.toString(seekbar_kI_adjuster_value));
			//setText_kD_adjuster(Integer.toString(seekbar_kD_adjuster_value));

			setText_current_angle(Integer.toString(currentAngle));
			setText_tilt_adjuster(Integer.toString(PID));
			setText_kP_adjuster(Integer.toString(P));
			setText_kI_adjuster(Integer.toString(I));
			setText_kD_adjuster(Integer.toString(D)); 

		}

		@Override
		public void disconnected() {
			enableUi(false);
		}
	}

	@Override
	protected IOIOLooper createIOIOLooper() {
		return new Looper();
	}

	private void enableUi(final boolean enable) {
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
			}
		});
	}

	private void setText_current_angle(final String str) {
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				textView_Current_Angle.setText(str);
			}
		});
	}
	
	private void setText_tilt_adjuster(final String str) {
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				textView_Tilt_adjuster.setText(str);
			}
		});
	}

	private void setText_kP_adjuster(final String str) {
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				textView_kP_adjuster.setText(str);
			}
		});
	}
	
	private void setText_kI_adjuster(final String str) {
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				textView_kI_adjuster.setText(str);
			}
		});
	}

	private void setText_kD_adjuster(final String str) {
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				textView_kD_adjuster.setText(str);
			}
		});
	}
	
	@Override
	public void onSensorChanged(SensorEvent event)
	{
	    if(event.sensor.getType()==Sensor.TYPE_ROTATION_VECTOR)
	    {
	        SensorManager.getRotationMatrixFromVector(mRotationMatrix,event.values);
	        SensorManager.remapCoordinateSystem(mRotationMatrix,SensorManager.AXIS_X, SensorManager.AXIS_Z, mRotationMatrix);
	        SensorManager.getOrientation(mRotationMatrix, orientationVals);
	        orientationVals[1]=(float)Math.toDegrees(orientationVals[1]);
	        //seekBar_kD_adjuster.setProgress(Math.round((orientationVals[1]*4)+500));
	    }
	}

	@Override
	  public void onAccuracyChanged(Sensor sensor, int accuracy) {
	  }

	@Override
	  protected void onResume() {
	    super.onResume();
	    // register this class as a listener for the orientation and
	    // accelerometer sensors
	    mSensorManager.registerListener(this,
	        mRotVectSensor,
	        10000);
	    mController.onResume();
		mPlayer.mConnection = mController.getState(Controller.STATE_CONNECTION);
		mPlayer.mControllerVersion = mController.getState(Controller.STATE_CURRENT_PRODUCT_VERSION); // Get controller version

		mPlayer.mButtonA = mController.getKeyCode(Controller.KEYCODE_BUTTON_A);
		mPlayer.mButtonB = mController.getKeyCode(Controller.KEYCODE_BUTTON_B);
		mPlayer.mButtonX = mController.getKeyCode(Controller.KEYCODE_BUTTON_X);
		mPlayer.mButtonY = mController.getKeyCode(Controller.KEYCODE_BUTTON_Y);
		mPlayer.DpadUp = mController.getKeyCode(Controller.KEYCODE_DPAD_UP);
		mPlayer.DpadDown = mController.getKeyCode(Controller.KEYCODE_DPAD_DOWN);
		mPlayer.DpadLeft = mController.getKeyCode(Controller.KEYCODE_DPAD_LEFT);
		mPlayer.DpadRight = mController.getKeyCode(Controller.KEYCODE_DPAD_RIGHT);
		mPlayer.mAxisY = mController.getAxisValue(Controller.AXIS_Y);
		mPlayer.mAxisRZ = mController.getAxisValue(Controller.AXIS_RZ);
	  }

	  @Override
	  protected void onPause() {
	    // unregister listener
	    super.onPause();
	    mSensorManager.unregisterListener(this);
		mController.onPause();
	  }

		@Override
		protected void onDestroy() {
			mController.exit();
			super.onDestroy();
		}

	  
}


// -----------------  ROBOT DIAGRAM -----------------------------  //
//
//                     _________
//					   |  Front |
//					   | Facing |
//                     | Phone  |
//                     ----------
//                        | | 
//                     =========
//  LeftArm = 13  ||===|       |===||  RightArm = 12
//                ||===|       |===|| 
//                ||   |       |   ||
//                ||   |       |   ||
//                     |       |
//                     |       |
//                 ||  |       |  ||
// LeftWheel = 10  ||=============||  RightWheel = 11
//                 ||             ||
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
