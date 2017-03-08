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

package freed.cam.apis.camera1.parameters.modes;

import android.hardware.Camera.Parameters;
import freed.utils.Log;

import com.troop.freedcam.R;

import freed.cam.apis.basecamera.CameraWrapperInterface;
import freed.cam.apis.camera1.CameraHolder;
import freed.cam.apis.camera1.CameraHolder.Frameworks;
import freed.cam.apis.camera1.parameters.ParametersHandler;

/**
 * Created by troop on 05.02.2016.
 */
public class PictureFormatHandler extends BaseModeParameter
{
    private final String TAG = PictureFormatHandler.class.getSimpleName();
    private boolean rawSupported;
    private String captureMode = cameraUiWrapper.getResString(R.string.jpeg_);
    private String rawFormat;

    private String[] rawFormats;



    private BayerFormat BayerFormats;
    private final ParametersHandler parametersHandler;



    /***
     * @param parameters   Hold the Camera Parameters
     * @param cameraUiWrapper Hold the camera object
     */
    public PictureFormatHandler(Parameters parameters, CameraWrapperInterface cameraUiWrapper, ParametersHandler parametersHandler)
    {
        super(parameters, cameraUiWrapper);
        this.parametersHandler = parametersHandler;
        isSupported = cameraUiWrapper.GetAppSettingsManager().pictureFormat.isSupported();
        rawSupported = cameraUiWrapper.GetAppSettingsManager().rawPictureFormat.isSupported();
        if (rawSupported) {
            rawFormat = cameraUiWrapper.GetAppSettingsManager().rawPictureFormat.get();
            rawFormats = cameraUiWrapper.GetAppSettingsManager().rawPictureFormat.getValues();
            BayerFormats = new BayerFormat(parameters, cameraUiWrapper, "");
            parametersHandler.bayerformat = BayerFormats;
        }
        Log.d(TAG, "rawsupported:" + rawSupported + "isSupported:"+ isSupported);
    }

    @Override
    public void SetValue(String valueToSet, boolean setToCam)
    {
        Log.d(TAG, "SetValue:" + valueToSet);
        captureMode = valueToSet;
        if (((CameraHolder) cameraUiWrapper.GetCameraHolder()).DeviceFrameWork != Frameworks.MTK)
        {
            if (valueToSet.equals(cameraUiWrapper.getResString(R.string.jpeg_)))
                setString(valueToSet,setToCam);
            else if(valueToSet.equals(cameraUiWrapper.getResString(R.string.bayer_)))
            {
                setString(rawFormat,setToCam);
                cameraUiWrapper.GetParameterHandler().SetDngActive(false);
            }
            else if(valueToSet.equals(cameraUiWrapper.getResString(R.string.dng_)))
            {
                setString(rawFormat,setToCam);
                cameraUiWrapper.GetParameterHandler().SetDngActive(true);
            }
        }
        onValueHasChanged(valueToSet);
    }

    private void setString(String val, boolean setTocam)
    {
        Log.d(TAG, "setApiString:" +val);
        parameters.set(cameraUiWrapper.getResString(R.string.picture_format), val);
        ((ParametersHandler) cameraUiWrapper.GetParameterHandler()).SetParametersToCamera(parameters);
    }

    @Override
    public boolean IsSupported()
    {
        Log.d(TAG,"IsSupported:"+ isSupported);
        return isSupported;
    }

    @Override
    public String GetValue() {
        return captureMode;
    }

    @Override
    public String[] GetValues()
    {
        return cameraUiWrapper.GetAppSettingsManager().pictureFormat.getValues();
    }

    @Override
    public void onModuleChanged(String module)
    {
        if (module.equals(cameraUiWrapper.getResString(R.string.module_video)))
        {
            onIsSupportedChanged(false);
        }
        else
        {
            onIsSupportedChanged(true);
        }
    }

    @Override
    public void onParameterValuesChanged(String[] values) {
        super.onParameterValuesChanged(values);
    }

    public class BayerFormat extends BaseModeParameter
    {

        /***
         * @param parameters   Hold the Camera Parameters
         * @param cameraHolder Hold the camera object
         * @param values
         */
        public BayerFormat(Parameters parameters, CameraWrapperInterface cameraHolder, String values) {
            super(parameters, cameraHolder);
        }

        @Override
        public String GetValue()
        {
            return rawFormat;
        }

        @Override
        public String[] GetValues() {
            return rawFormats;
        }

        @Override
        public boolean IsSupported() {
            return rawFormats != null && rawFormats.length>0;
        }

        @Override
        public boolean IsVisible() {
            return rawFormats != null && rawFormats.length>0;
        }

        @Override
        public void SetValue(String valueToSet, boolean setToCam)
        {
            rawFormat = valueToSet;
            if (captureMode.equals(cameraUiWrapper.getResString(R.string.bayer_))|| captureMode.equals(cameraUiWrapper.getResString(R.string.dng_))) {
                PictureFormatHandler.this.SetValue(captureMode, true);
            }
        }
    }
}
