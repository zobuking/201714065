package com.idp.group1.assignment201714029;


import android.content.Context;
import android.content.res.Configuration;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


/**
 * A simple {@link Fragment} subclass.
 */
public class Gyroscope extends Fragment implements SensorEventListener  {

	private TextView xAxisGyroscopeText, yAxisGyroscopeText, zAxisGyroscopeText;
	private Fragment fragment;
	private LinearLayout linearLayout;
	private SensorManager sensorManager;
	private Sensor gyroscope;
	private FirebaseDatabase database;
	private DatabaseReference myRef;
	private int prevOrientation;

	public Gyroscope() {
		// Required empty public constructor
	}


	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState) {
		// Inflate the layout for this fragment
		View view = inflater.inflate(R.layout.fragment_gyroscope, container, false);

		prevOrientation = 0;
		database = FirebaseDatabase.getInstance();
		myRef = database.getReference("Gyroscope");

		sensorManager = (SensorManager) getActivity().getSystemService(Context.SENSOR_SERVICE);
		gyroscope = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
		sensorManager.registerListener(this, gyroscope, sensorManager.SENSOR_DELAY_NORMAL);

//		myRef.child(myRef.push().getKey()).setValue(new Value(-5, -7, 0));

		if (gyroscope == null) {
			Toast.makeText(getContext(), "No Gyroscope Sensor Found", Toast.LENGTH_SHORT).show();
		}
		else {
			Toast.makeText(getContext(), "Gyroscope Sensor Working", Toast.LENGTH_SHORT).show();
		}

		Settings.System.putInt( getContext().getContentResolver(), Settings.System.ACCELEROMETER_ROTATION, 1);


		xAxisGyroscopeText = (TextView) view.findViewById(R.id.xAxisGyroscopeID);
		yAxisGyroscopeText = (TextView) view.findViewById(R.id.yAxisGyroscopeID);
		zAxisGyroscopeText = (TextView) view.findViewById(R.id.zAxisGyroscopeID);

		return view;
	}

	@Override
	public void onSensorChanged(SensorEvent sensorEvent) {

//		Toast.makeText(getContext(), "entered", Toast.LENGTH_SHORT).show();

//		xAxisGyroscopeText.setText("ok too");
		double xGyroscope = (double) sensorEvent.values[0];
		double yGyroscope = (double) sensorEvent.values[1];
		double zGyroscope = (double) sensorEvent.values[2];


//		x = x * 57.2957795f;
//		y = y * 57.2957795f;
//		z = z * 57.2957795f;

		xGyroscope = Math.round(xGyroscope * 1000.0) / 1000.0;
		yGyroscope = Math.round(yGyroscope * 1000.0) / 1000.0;
		zGyroscope = Math.round(zGyroscope * 1000.0) / 1000.0;

		int orientation = getResources().getConfiguration().orientation;

		if (orientation == Configuration.ORIENTATION_LANDSCAPE && prevOrientation == 0) {
			prevOrientation = 1;
			Toast.makeText(getContext(), "Saving Current Gyroscope Value", Toast.LENGTH_SHORT).show();
			myRef.child(myRef.push().getKey()).setValue(new Value(xGyroscope, yGyroscope, zGyroscope));
		}
		else {
			prevOrientation = 0;
		}

		xAxisGyroscopeText.setText(String.valueOf(xGyroscope));
		yAxisGyroscopeText.setText(String.valueOf(yGyroscope));
		zAxisGyroscopeText.setText(String.valueOf(zGyroscope));
	}

	@Override
	public void onAccuracyChanged(Sensor sensor, int i) {

	}

	@Override
	public void onResume() {
		super.onResume();
		sensorManager.registerListener(this, gyroscope, SensorManager.SENSOR_DELAY_NORMAL);
	}

	@Override
	public void onPause()
	{
		super.onPause();
		sensorManager.unregisterListener(this);
	}
}
