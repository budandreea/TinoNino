package com.countdown.tinonino;


import android.app.Activity;
import android.content.Context;
import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Vibrator;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class Countdown extends Activity implements OnClickListener {

	private Button btnStart, btnStop;
	private EditText editTimeValue;
	private TextView textViewTime, timp, minute;
	
	private CountDownTimer countDownTimer; 
	private long totalTime;							
	private long timeBlink; 
	private boolean blink;
	
	private Camera camera;
    private boolean isFlashOn;
    Parameters params;
	
	MediaPlayer mpCountdown;
	Vibrator vCountdown;
	
	
	long[] pattern = {0, 100, 1000, 300, 200, 100, 500, 200, 100};


	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_countdown);

		btnStart = (Button) findViewById(R.id.btnStart);
		btnStop = (Button) findViewById(R.id.btnStop);
		textViewTime = (TextView) findViewById(R.id.textViewTime);
		editTimeValue = (EditText) findViewById(R.id.editTimeText);
		timp = (TextView) findViewById(R.id.timp);
		minute = (TextView) findViewById(R.id.minute);
		
		btnStart.setOnClickListener(this);
		btnStop.setOnClickListener(this);
		
		timp.setVisibility(View.GONE);
	
		getCamera();
		
		this.vCountdown = (Vibrator) this.getSystemService(Context.VIBRATOR_SERVICE);
		this.mpCountdown = MediaPlayer.create(this,R.raw.heathens);

	}

	@Override
	public void onClick(View v) {
		if (v.getId() == R.id.btnStart) {
			textViewTime.setTextAppearance(getApplicationContext(),R.style.AppTheme);
			setTimer();
			btnStop.setVisibility(View.VISIBLE);
			btnStart.setVisibility(View.GONE);
			timp.setVisibility(View.VISIBLE);
			minute.setVisibility(View.GONE);
			editTimeValue.setVisibility(View.GONE);
			editTimeValue.setText("");
			startTimer();
			mpCountdown.start();
			vCountdown.vibrate(pattern, 1);
			turnOnFlash();
			

		} else if (v.getId() == R.id.btnStop) {
			countDownTimer.cancel();
			btnStart.setVisibility(View.VISIBLE);
			btnStop.setVisibility(View.GONE);
			timp.setVisibility(View.GONE);
			minute.setVisibility(View.VISIBLE);
			editTimeValue.setVisibility(View.VISIBLE);
			mpCountdown.pause();
			vCountdown.cancel();
			turnOffFlash();
		}
	}
	
	
	
	protected void turnOnFlash() {

        if(!isFlashOn) {
            if(camera == null || params == null) {
                return;
            }

            params = camera.getParameters();
            params.setFlashMode(Parameters.FLASH_MODE_TORCH);
            camera.setParameters(params);
            camera.startPreview();
            isFlashOn = true;
        }

    }
	
	
	private void getCamera() {

        if (camera == null) {
            try {
                camera = Camera.open();
                params = camera.getParameters();
            }catch (Exception e) {

            }
        }
        

    }

    protected void turnOffFlash() {

            if (isFlashOn) {
                if (camera == null || params == null) {
                    return;
                }

                params = camera.getParameters();
                params.setFlashMode(Parameters.FLASH_MODE_OFF);
                camera.setParameters(params);
                camera.stopPreview();
                isFlashOn = false;
            }
    }
    

	private void setTimer() {
		int time = 0;
		if (!editTimeValue.getText().toString().equals("")) {
			time = Integer.parseInt(editTimeValue.getText().toString());
		} else
			
			return;

		totalTime = 60 * time * 1000;
		timeBlink = 30 * 1000;
	}

	private void startTimer() {
		countDownTimer = new CountDownTimer(totalTime, 500) {
			
			@Override
			public void onTick(long leftTimeInMilliseconds) {
				long seconds = leftTimeInMilliseconds / 1000;
				
				if (isFlashOn){
					turnOffFlash();
				}
				else {
					turnOnFlash();
				}
				
				if (leftTimeInMilliseconds < timeBlink) {
					textViewTime.setTextAppearance(getApplicationContext(),
							R.style.AppBaseTheme);
		
					
					if (blink) {
						textViewTime.setVisibility(View.VISIBLE);
					} else {
						textViewTime.setVisibility(View.INVISIBLE);
					}

					blink = !blink; 
				}

				textViewTime.setText(String.format("%02d", seconds / 60)
						+ ":" + String.format("%02d", seconds % 60));
				
			}
			
			@Override
			public void onFinish() {
				textViewTime.setText("Time up!");
				textViewTime.setVisibility(View.VISIBLE);
				btnStart.setVisibility(View.VISIBLE);
				btnStop.setVisibility(View.GONE);
				timp.setVisibility(View.GONE);
				minute.setVisibility(View.VISIBLE);
				editTimeValue.setVisibility(View.VISIBLE);
				mpCountdown.release();
				vCountdown.cancel();
				turnOffFlash();
			}

		}.start();

	}

}
		