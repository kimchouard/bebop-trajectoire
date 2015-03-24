package com.parrot.bobopdronepiloting;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.format.Time;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import com.parrot.bebopdronepiloting.R;

import android.os.Environment;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;

import com.parrot.arsdk.arcommands.ARCOMMANDS_ARDRONE3_PILOTINGSTATE_FLYINGSTATECHANGED_STATE_ENUM;
import com.parrot.arsdk.arcommands.ARCOMMANDS_ARDRONE3_GPSSETTINGSSTATE_GPSUPDATESTATECHANGED_STATE_ENUM;
import com.parrot.arsdk.ardiscovery.ARDiscoveryDeviceService;

import java.util.ArrayList;
import java.io.File;
import java.io.FileOutputStream;

public class PilotingActivity extends Activity implements DeviceControllerListener
{
    private static String TAG = PilotingActivity.class.getSimpleName();
    public static String EXTRA_DEVICE_SERVICE = "pilotingActivity.extra.device.service";

    public DeviceController deviceController;
    public ARDiscoveryDeviceService service;
    public ArrayList<String> cmd;

    private Button emergencyBt;
    private Button takeoffBt;
    private Button landingBt;
    private Button flipBt;
    private Button pathBt;

    private Button gazUpBt;
    private Button gazDownBt;
    private Button yawLeftBt;
    private Button yawRightBt;

    private Button forwardBt;
    private Button backBt;
    private Button rollLeftBt;
    private Button rollRightBt;

    private TextView batteryLabel;
    private TextView altitudeLabel;
    private TextView rollLabel;
    private TextView yawLabel;
    private TextView pitchLabel;
    private TextView longitudeLabel;
    private TextView latitudeLabel;
    private TextView GPSLabel;

    private AlertDialog alertDialog;

    File fGPSLog;
    FileOutputStream fsGPSLog;
    OutputStreamWriter fswGPSLog;
    private static String GPSLogFilename = "gps.log.";
    File fSensorsLog;
    FileOutputStream fsSensorsLog;
    OutputStreamWriter fswSensorsLog;
    private static String SensorsLogFilename = "sensors.log.";
    File fHeightLog;
    FileOutputStream fsHeightLog;
    OutputStreamWriter fswHeightLog;
    private static String HeightLogFilename = "height.log.";
    Time time;



    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_piloting);

        Intent intent = getIntent();
        service = intent.getParcelableExtra(EXTRA_DEVICE_SERVICE);
        cmd = intent.getStringArrayListExtra(PlanificationActivity.COMMAND_LIST);

        emergencyBt = (Button) findViewById(R.id.emergencyBt);
        emergencyBt.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v)
            {
                if (deviceController != null)
                {
                    deviceController.sendEmergency();
                }
            }
        });

        pathBt = (Button) findViewById(R.id.pathBt);
        pathBt.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v)
            {
                if (deviceController != null)
                {
                    deviceController.sendTakeoff();
                    deviceController.waitTime(5000);
                    for (int i=0; i<cmd.size(); i++)
                    {
                        switch (cmd.get(i))
                        {
                            case "Avancer" :
                                deviceController.moveFront();
                                break;
                            case "Reculer" :
                                deviceController.moveBack();
                                break;
                            case "Tourner à gauche" :
                                deviceController.rotateLeft();
                                break;
                            case "Tourner à droite" :
                                deviceController.rotateRight();
                                break;
                            case "Monter" :
                                deviceController.moveUp();
                                break;
                            case "Descendre" :
                                deviceController.moveDown();
                                break;
                            case "Back flip" :
                                deviceController.backFlip();
                                break;
                            case "Front flip" :
                                deviceController.frontFlip();
                                break;
                            case "Left flip" :
                                deviceController.leftFlip();
                                break;
                            case "Right flip" :
                                deviceController.rightFlip();
                                break;
                            default:
                                break;
                        }
                    }
                    deviceController.sendLanding();
                }
            }
        });

        flipBt = (Button) findViewById(R.id.flipBt);
        flipBt.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v)
            {
                if (deviceController != null)
                {
                    deviceController.sendTakeoff();
                    deviceController.waitTime(5000);

                    for (int i=0; i<7; i++)
                    {
                        deviceController.setGaz((byte) 20);
                        deviceController.setYaw((byte) 45);
                        deviceController.waitTime(1000);
                        deviceController.setGaz((byte) 0);
                        deviceController.setYaw((byte) 0);
                        deviceController.setPitch((byte) 10);
                        deviceController.setFlag((byte) 1);
                        deviceController.waitTime(2000);
                        deviceController.setPitch((byte) 0);
                        deviceController.setFlag((byte) 0);
                    }

                    deviceController.sendLanding();
                }
            }
        });

        takeoffBt = (Button) findViewById(R.id.takeoffBt);
        takeoffBt.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v)
            {
                if (deviceController != null)
                {
                    deviceController.sendTakeoff();
                }
            }
        });
        landingBt = (Button) findViewById(R.id.landingBt);
        landingBt.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v)
            {
                if (deviceController != null)
                {
                    deviceController.sendLanding();
                }
            }
        });

        gazUpBt = (Button) findViewById(R.id.gazUpBt);
        gazUpBt.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event)
            {
                switch (event.getAction())
                {
                    case MotionEvent.ACTION_DOWN:
                        v.setPressed(true);
                        if (deviceController != null)
                        {
                            deviceController.setGaz((byte) 50);
                        }
                        break;

                    case MotionEvent.ACTION_UP:
                        v.setPressed(false);
                        if (deviceController != null)
                        {
                            deviceController.setGaz((byte)0);

                        }
                        break;

                    default:

                        break;
                }

                return true;
            }
        });

        gazDownBt = (Button) findViewById(R.id.gazDownBt);
        gazDownBt.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event)
            {
                switch (event.getAction())
                {
                    case MotionEvent.ACTION_DOWN:
                        v.setPressed(true);
                        if (deviceController != null)
                        {
                            deviceController.setGaz((byte)-50);

                        }
                        break;

                    case MotionEvent.ACTION_UP:
                        v.setPressed(false);
                        if (deviceController != null)
                        {
                            deviceController.setGaz((byte)0);
                        }
                        break;

                    default:

                        break;
                }

                return true;
            }
        });
        yawLeftBt = (Button) findViewById(R.id.yawLeftBt);
        yawLeftBt.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event)
            {
                switch (event.getAction())
                {
                    case MotionEvent.ACTION_DOWN:
                        v.setPressed(true);
                        if (deviceController != null)
                        {
                            deviceController.setYaw((byte)-50);

                        }
                        break;

                    case MotionEvent.ACTION_UP:
                        v.setPressed(false);
                        if (deviceController != null)
                        {
                            deviceController.setYaw((byte) 0);
                        }
                        break;

                    default:

                        break;
                }

                return true;
            }
        });
        yawRightBt = (Button) findViewById(R.id.yawRightBt);
        yawRightBt.setOnTouchListener(new View.OnTouchListener()
        {
            @Override
            public boolean onTouch(View v, MotionEvent event)
            {
                switch (event.getAction())
                {
                    case MotionEvent.ACTION_DOWN:
                        v.setPressed(true);
                        if (deviceController != null)
                        {
                            deviceController.setYaw((byte) 50);

                        }
                        break;

                    case MotionEvent.ACTION_UP:
                        v.setPressed(false);
                        if (deviceController != null)
                        {
                            deviceController.setYaw((byte) 0);
                        }
                        break;

                    default:

                        break;
                }

                return true;
            }
        });

        forwardBt = (Button) findViewById(R.id.forwardBt);
        forwardBt.setOnTouchListener(new View.OnTouchListener()
        {
            @Override
            public boolean onTouch(View v, MotionEvent event)
            {
                switch (event.getAction())
                {
                    case MotionEvent.ACTION_DOWN:
                        v.setPressed(true);
                        if (deviceController != null)
                        {
                            deviceController.setPitch((byte) 50);
                            deviceController.setFlag((byte) 1);
                        }
                        break;

                    case MotionEvent.ACTION_UP:
                        v.setPressed(false);
                        if (deviceController != null)
                        {
                            deviceController.setPitch((byte) 0);
                            deviceController.setFlag((byte) 0);
                        }
                        break;

                    default:

                        break;
                }

                return true;
            }
        });
        backBt = (Button) findViewById(R.id.backBt);
        backBt.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event)
            {
                switch (event.getAction())
                {
                    case MotionEvent.ACTION_DOWN:
                        v.setPressed(true);
                        if (deviceController != null)
                        {
                            deviceController.setPitch((byte)-50);
                            deviceController.setFlag((byte)1);
                        }
                        break;

                    case MotionEvent.ACTION_UP:
                        v.setPressed(false);
                        if (deviceController != null)
                        {
                            deviceController.setPitch((byte)0);
                            deviceController.setFlag((byte)0);
                        }
                        break;

                    default:

                        break;
                }

                return true;
            }
        });
        rollLeftBt = (Button) findViewById(R.id.rollLeftBt);
        rollLeftBt.setOnTouchListener(new View.OnTouchListener()
        {
            @Override
            public boolean onTouch(View v, MotionEvent event)
            {
                switch (event.getAction())
                {
                    case MotionEvent.ACTION_DOWN:
                        v.setPressed(true);
                        if (deviceController != null)
                        {
                            deviceController.setRoll((byte) -50);
                            deviceController.setFlag((byte) 1);
                        }
                        break;

                    case MotionEvent.ACTION_UP:
                        v.setPressed(false);
                        if (deviceController != null)
                        {
                            deviceController.setRoll((byte) 0);
                            deviceController.setFlag((byte) 0);
                        }
                        break;

                    default:

                        break;
                }

                return true;
            }
        });
        rollRightBt = (Button) findViewById(R.id.rollRightBt);
        rollRightBt.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event)
            {
                switch (event.getAction())
                {
                    case MotionEvent.ACTION_DOWN:
                        v.setPressed(true);
                        if (deviceController != null)
                        {
                            deviceController.setRoll((byte)50);
                            deviceController.setFlag((byte)1);
                        }
                        break;

                    case MotionEvent.ACTION_UP:
                        v.setPressed(false);
                        if (deviceController != null)
                        {
                            deviceController.setRoll((byte)0);
                            deviceController.setFlag((byte)0);
                        }
                        break;

                    default:

                        break;
                }

                return true;
            }
        });

        batteryLabel = (TextView) findViewById(R.id.batteryLabel);
        altitudeLabel = (TextView) findViewById(R.id.altitudeValue);
        rollLabel = (TextView) findViewById(R.id.rollValue);
        pitchLabel = (TextView) findViewById(R.id.pitchValue);
        yawLabel = (TextView) findViewById(R.id.yawValue);
        longitudeLabel = (TextView) findViewById(R.id.longitudeValue);
        latitudeLabel = (TextView) findViewById(R.id.latitudeValue);
        GPSLabel = (TextView) findViewById(R.id.GPLValue);

        deviceController = new DeviceController(this, service);
        deviceController.setListener(this);

        initLogs();
    }

    private void initLogs()
    {
        // write on SD card file data in the text box
        try {
            time = new Time();
            time.setToNow();
            Long millis = time.toMillis(false);

            // Init gps logs
            fGPSLog = new File(Environment.getExternalStorageDirectory() + File.separator + GPSLogFilename + millis.toString() + ".csv");
            fGPSLog.createNewFile();
            fsGPSLog = new FileOutputStream(fGPSLog);
            fswGPSLog = new OutputStreamWriter(fsGPSLog);
            String GPSheader = "Time,Lattitude,Longitude,Altitude";
            writeGPSLog(GPSheader);

            // Init sensor logs
            fSensorsLog = new File(Environment.getExternalStorageDirectory() + File.separator + SensorsLogFilename + millis.toString() + ".csv");
            fSensorsLog.createNewFile();
            fsSensorsLog = new FileOutputStream(fSensorsLog);
            fswSensorsLog= new OutputStreamWriter(fsSensorsLog);
            String SensorHeader = "Time,Roll,Pitch,Yaw";
            writeSensorLog(SensorHeader);

            // Init height logs
            fHeightLog = new File(Environment.getExternalStorageDirectory() + File.separator + HeightLogFilename + millis.toString() + ".csv");
            fHeightLog.createNewFile();
            fsHeightLog = new FileOutputStream(fHeightLog);
            fswHeightLog= new OutputStreamWriter(fsHeightLog);
            String HeightHeader = "Time,Altitude";
            writeHeightLog(HeightHeader);

            Log.d("FILE", "Logs initiated");
        } catch (Exception e) {
            Log.e("FILE", e.getMessage());
        }
    }

    private void writeGPSLog(String mess)
    {
        try {
            fswGPSLog.append(mess+"\n");
        } catch (Exception e) {
            Log.e("FILE", e.getMessage());
        }
    }

    private void writeHeightLog(String mess)
    {
        try {
            fswHeightLog.append(mess+"\n");
        } catch (Exception e) {
            Log.e("FILE", e.getMessage());
        }
    }

    private void writeSensorLog(String mess)
    {
        try {
            fswSensorsLog.append(mess+"\n");
        } catch (Exception e) {
            Log.e("FILE", e.getMessage());
        }
    }

    private void closeLogs()
    {
        // write on SD card file data in the text box
        try {
            fswGPSLog.close();
            fsGPSLog.close();
            fswSensorsLog.close();
            fsSensorsLog.close();
        } catch (Exception e) {
            Log.e("FILE", e.getMessage());
        }
    }

    @Override
    public void onStart()
    {
        super.onStart();

        if (deviceController != null)
        {
            final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(PilotingActivity.this);

            // set title
            alertDialogBuilder.setTitle("Connecting ...");


            // create alert dialog
            alertDialog = alertDialogBuilder.create();
            alertDialog.show();

            new Thread(new Runnable() {
                @Override
                public void run()
                {
                    boolean failed = false;

                    failed = deviceController.start();

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run()
                        {
                            //alertDialog.hide();
                            alertDialog.dismiss();
                        }
                    });

                    if (failed)
                    {
                        finish();
                    }
                }
            }).start();

        }
    }

    private void stopDeviceController()
    {
        if (deviceController != null)
        {
            final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(PilotingActivity.this);

            // set title
            alertDialogBuilder.setTitle("Disconnecting ...");

            // show it
            runOnUiThread(new Runnable()
            {
                @Override
                public void run()
                {
                    // create alert dialog
                    alertDialog = alertDialogBuilder.create();
                    alertDialog.show();

                    new Thread(new Runnable() {
                        @Override
                        public void run()
                        {
                            deviceController.stop();
                            deviceController = null;

                            runOnUiThread(new Runnable() {
                                @Override
                                public void run()
                                {
                                    //alertDialog.hide();
                                    alertDialog.dismiss();
                                    finish();
                                }
                            });

                        }
                    }).start();
                }
            });
            //alertDialog.show();

        }
    }

    @Override
    protected void onStop()
    {
        if (deviceController != null)
        {
            closeLogs();
            deviceController.stop();
            deviceController = null;
        }

        super.onStop();
    }

    @Override
    public void onBackPressed()
    {
        stopDeviceController();
    }

    @Override
    public void onDisconnect()
    {
        stopDeviceController();
    }

    @Override
    public void onUpdateBattery(final byte percent)
    {
        runOnUiThread(new Runnable() {
            @Override
            public void run()
            {
                batteryLabel.setText(String.format("%d%%", percent));
            }
        });

    }

    @Override
    public void onFlyingStateChanged(final ARCOMMANDS_ARDRONE3_PILOTINGSTATE_FLYINGSTATECHANGED_STATE_ENUM state)
    {
        // on the UI thread, disable and enable buttons according to flying state
        runOnUiThread(new Runnable()
        {
            @Override
            public void run()
            {
                switch (state) {
                    case ARCOMMANDS_ARDRONE3_PILOTINGSTATE_FLYINGSTATECHANGED_STATE_LANDED:
                        takeoffBt.setEnabled(true);
                        landingBt.setEnabled(false);
                        break;
                    case ARCOMMANDS_ARDRONE3_PILOTINGSTATE_FLYINGSTATECHANGED_STATE_FLYING:
                    case ARCOMMANDS_ARDRONE3_PILOTINGSTATE_FLYINGSTATECHANGED_STATE_HOVERING:
                        takeoffBt.setEnabled(false);
                        landingBt.setEnabled(true);
                        break;
                    default:
                        // in all other cases, take of and landing are not enabled
                        takeoffBt.setEnabled(false);
                        landingBt.setEnabled(false);
                        break;
                }
            }
        });
    }

    @Override
    public void onAltitudeChanged(final double altitude)
    {
        runOnUiThread(new Runnable() {
            @Override
            public void run()
            {
                altitudeLabel.setText(String.format("%f", altitude));
                time.setToNow();
                Long millis = time.toMillis(false);
                writeHeightLog(millis.toString()+","+altitude);
            }
        });
    }

    @Override
    public void onAttitudeChanged(final float roll, final float pitch, final float yaw)
    {
        runOnUiThread(new Runnable() {
            @Override
            public void run()
            {
                rollLabel.setText(String.format("%f", roll));
                pitchLabel.setText(String.format("%f", pitch));
                yawLabel.setText(String.format("%f", yaw));
                time.setToNow();
                Long millis = time.toMillis(false);
                writeSensorLog(millis.toString()+","+roll+","+pitch+","+yaw);
            }
        });
    }

    @Override
    public void onPositionChanged(final double latitude, final double longitude, final double altitude)
    {
        runOnUiThread(new Runnable() {
            @Override
            public void run()
            {
                latitudeLabel.setText(String.format("%f", latitude));
                longitudeLabel.setText(String.format("%f", longitude));
//                altitudeLabel.setText(String.format("%f", altitude));
                time.setToNow();
                Long millis = time.toMillis(false);
                writeGPSLog(millis.toString()+","+latitude+","+longitude+","+altitude);
            }
        });
    }

    @Override
    public void onGPSStatusChanged(final ARCOMMANDS_ARDRONE3_GPSSETTINGSSTATE_GPSUPDATESTATECHANGED_STATE_ENUM state)
    {
        runOnUiThread(new Runnable() {
            @Override
            public void run()
            {
                longitudeLabel.setText(String.format("%s", state));
            }
        });
    }
}
