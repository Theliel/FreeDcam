/*
 *
 *     Copyright (C) 2015 Ingo Fuchs
 *     This program is free software; you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation; either version 2 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License along
 *     with this program; if not, write to the Free Software Foundation, Inc.,
 *     51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 * /
 */

package com.freedcam.ui;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.os.Build;
import android.os.Build.VERSION;
import android.os.Environment;
import android.os.Handler;
import android.os.StatFs;

import com.freedcam.apis.KEYS;
import com.freedcam.apis.basecamera.interfaces.CameraWrapperInterface;
import com.freedcam.apis.basecamera.modules.ModuleChangedEvent;
import com.freedcam.utils.AppSettingsManager;
import com.freedcam.utils.Logger;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by troop on 14.06.2015.
 */
public abstract class AbstractInfoOverlayHandler implements ModuleChangedEvent
{
    private final Handler handler;
    protected CameraWrapperInterface cameraUiWrapper;
    boolean started;
    private final Context context;

    protected String batteryLevel;
    private final BatteryBroadCastListner batteryBroadCastListner;

    private final SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
    protected String timeString;

    //this holds the format for video or picture
    protected String format;
    //this holds the size for video/picture
    protected String size;

    protected String storageSpace;
    protected AppSettingsManager appSettingsManager;

    public AbstractInfoOverlayHandler(Context context, AppSettingsManager appSettingsManager)
    {
        this.context = context;
        this.appSettingsManager =appSettingsManager;
        handler = new Handler();
        batteryBroadCastListner = new BatteryBroadCastListner();
    }

    public void setCameraUIWrapper(CameraWrapperInterface cameraUIWrapper)
    {
        cameraUiWrapper = cameraUIWrapper;
        if (cameraUIWrapper != null && cameraUIWrapper.GetModuleHandler() != null)
            cameraUIWrapper.GetModuleHandler().addListner(this);
    }

    @Override
    public void onModuleChanged(String module) {
    }

    private void startLooperThread()
    {
        if (started)
            handler.postDelayed(runner, 1000);
    }

    public void StartUpdating()
    {
        started = true;
        context.registerReceiver(batteryBroadCastListner, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
        startLooperThread();
    }

    public void StopUpdating()
    {
        started = false;
        handler.removeCallbacks(runner);
        try {
            context.unregisterReceiver(batteryBroadCastListner);
        }
        catch (IllegalArgumentException ex)
        {
            Logger.exception(ex );}

    }

    class BatteryBroadCastListner extends BroadcastReceiver
    {

        @Override
        public void onReceive(Context context, Intent intent) {
            batteryLevel = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, 0)+"%";
        }
    }

    Runnable runner = new Runnable() {
        @Override
        public void run()
        {
            if (cameraUiWrapper == null)
                return;
            timeString = dateFormat.format(new Date());
            getFormat();
            getStorageSpace();
            UpdateViews();


            startLooperThread();

        }
    };



    protected void UpdateViews()
    {

    }

    private void getFormat()
    {
        if (cameraUiWrapper.GetModuleHandler().GetCurrentModuleName().equals(KEYS.MODULE_VIDEO))
        {
            format = "H264";
            size = appSettingsManager.getString(AppSettingsManager.SETTING_VIDEPROFILE);
        }
        else
        {
            if (cameraUiWrapper.GetParameterHandler().PictureFormat != null)
                format = cameraUiWrapper.GetParameterHandler().PictureFormat.GetValue();
            else
                format = "";
            if (cameraUiWrapper.GetParameterHandler().PictureSize != null)
                size = cameraUiWrapper.GetParameterHandler().PictureSize.GetValue();
            else
                size = "";
        }
    }

    public void getStorageSpace()
    {
        try
        {
            //defcomg was here 24/01/2015
            if(!cameraUiWrapper.GetModuleHandler().GetCurrentModuleName().equals(KEYS.MODULE_VIDEO))
                storageSpace = Avail4PIC();
            else
                storageSpace = readableFileSize(SDspace());
        }
        catch (Exception ex)
        {
            storageSpace = "";
        }


    }

    private String readableFileSize(long size) {
        if(size <= 0) return "0";
        String[] units = { "B", "KB", "MB", "GB", "TB" };
        int digitGroups = (int) (Math.log10(size)/Math.log10(1024));
        return new DecimalFormat("#,##0.#").format(size/Math.pow(1024, digitGroups)) + " " + units[digitGroups];
    }

    private  String Avail4PIC()
    {
        // double calc;
        long done;
        done = (long) Calc();
        long a = SDspace() / done;
        return  a + " left";
    }
    private double Calc()
    {
        String[] res = appSettingsManager.getString(AppSettingsManager.SETTING_PICTURESIZE).split("x");

        if(appSettingsManager.getString(AppSettingsManager.SETTING_PICTUREFORMAT).contains(KEYS.BAYER))
        {
            if (Build.MANUFACTURER.contains("HTC"))
                return Integer.parseInt(res[0]) * 2 *Integer.parseInt(res[1]) * 16 / 8;
            else
                return Integer.parseInt(res[0]) *Integer.parseInt(res[1]) * 10 / 8;
        }
        else
            return Integer.parseInt(res[0]) *Integer.parseInt(res[1]) * 8 / 8;
    }

    private long SDspace()
    {
        long bytesAvailable = 0;
        if (!appSettingsManager.GetWriteExternal()) {
            bytesAvailable = Environment.getExternalStorageDirectory().getUsableSpace();
        }
        else
        {
            StatFs stat = new StatFs(System.getenv("SECONDARY_STORAGE"));
            if(VERSION.SDK_INT > 17)
                bytesAvailable = stat.getFreeBytes();
            else
            {
                bytesAvailable = stat.getAvailableBlocks() * stat.getBlockSize();
            }

        }
        return bytesAvailable;
    }




}
