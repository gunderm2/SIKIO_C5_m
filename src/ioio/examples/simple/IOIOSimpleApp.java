/*
 * Copyright 2015 Michael Gunderson. All rights reserved.
 *
 *
 * Redistribution and use in source and binary forms, with or without modification, are
 * permitted provided that the following conditions are met:
 *
 *    1. Redistributions of source code must retain the above copyright notice, this list of
 *       conditions and the following disclaimer.
 *
 *    2. Redistributions in binary form must reproduce the above copyright notice, this list
 *       of conditions and the following disclaimer in the documentation and/or other materials
 *       provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED "AS IS" AND ANY EXPRESS OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
 * FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL ARSHAN POURSOHI OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF
 * ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 * The views and conclusions contained in the software and documentation are those of the
 * authors and should not be interpreted as representing official policies, either expressed
 * or implied.
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