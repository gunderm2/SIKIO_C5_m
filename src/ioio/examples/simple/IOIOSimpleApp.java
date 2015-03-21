/*
*The MIT License (MIT)
*
*Copyright (c) 2015 Michael Gunderson
*
*Permission is hereby granted, free of charge, to any person obtaining a copy
*of this software and associated documentation files (the "Software"), to deal
*in the Software without restriction, including without limitation the rights
*to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
*copies of the Software, and to permit persons to whom the Software is
*furnished to do so, subject to the following conditions:
*
*The above copyright notice and this permission notice shall be included in
*all copies or substantial portions of the Software.
*
*THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
*IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
*FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
*AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
*LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
*OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
*THE SOFTWARE.
 */
package ioio.examples.simple;

import ioio.lib.api.AnalogInput;
import ioio.lib.api.DigitalOutput;
import ioio.lib.api.IOIO;
import ioio.lib.api.exception.ConnectionLostException;
import ioio.lib.util.BaseIOIOLooper;
import ioio.lib.util.IOIOLooper;
import ioio.lib.util.android.IOIOActivity;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.ToggleButton;

public class IOIOSimpleApp extends IOIOActivity {
//	private TextView title;
	private TextView editTextLabel;
	private EditText editTextInput;
	private TextView timerLabel;
	private TextView timerValue;
	private Button btnStart;
	private Button btnStop;
	int motorOn = 0; // Keeps track of whether motor should be turned on, 1 if so
	Integer timer = 5;
	boolean start = false;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

//		title = (TextView) findViewById(R.id.title);
		editTextLabel = (TextView) findViewById(R.id.editTextLabel);
		editTextInput = (EditText) findViewById(R.id.editText);
		timerLabel = (TextView) findViewById(R.id.timerLabel);
		timerValue = (TextView) findViewById(R.id.timerValue);
		btnStart = (Button) findViewById(R.id.startButton);
		btnStop = (Button) findViewById(R.id.stopButton);
		
		btnStart.setOnClickListener(new View.OnClickListener() {	
            public void onClick(View view) {
            	start = true;
            	timer = Integer.parseInt(editTextInput.getText().toString());            	
            }
        });
		
		btnStop.setOnClickListener(new View.OnClickListener() {	
            public void onClick(View view) {
            	start = false;           	
            }
        });

	}

	class Looper extends BaseIOIOLooper {
		DigitalOutput motor; // Declare motor as digital output
		int motorPin = 18; // Use pin 18
		@Override
		public void setup() throws ConnectionLostException {
			// Ready pin and turn motor off here
			motor = ioio_.openDigitalOutput(motorPin);
			motor.write(false);
		}

		@Override
		public void loop() throws ConnectionLostException, InterruptedException {
			try {
				timer -= 1;
				if(start) {
					if(timer >= 0) setTimerValue(timer);
					if(timer == 0) motorOn = 1;
					// motorOn will be equal to 1 if timer is still counting down, so keep motor running
				    if (motorOn == 1){
				      motor.write(true);
				    }
				}
				if(!start) motor.write(false);
			    // Don't call this loop again for 100 milliseconds
			    Thread.sleep(1000);
			  } catch (InterruptedException e){
			  }
		}
	}

	@Override
	protected IOIOLooper createIOIOLooper() {
		return new Looper();
	}
	
	private void setTimerValue(final Integer value) {
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				timerValue.setText(value.toString());
			}
		});
	}
}