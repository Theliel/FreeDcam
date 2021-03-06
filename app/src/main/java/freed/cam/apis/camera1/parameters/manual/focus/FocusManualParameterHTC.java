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

package freed.cam.apis.camera1.parameters.manual.focus;

import android.hardware.Camera.Parameters;

import freed.cam.apis.basecamera.CameraWrapperInterface;
import freed.settings.Settings;
import freed.cam.apis.camera1.parameters.ParametersHandler;
import freed.cam.apis.camera1.parameters.manual.BaseManualParameter;
import freed.settings.SettingsManager;

/**
 * Created by troop on 17.08.2014.
 */
public class FocusManualParameterHTC extends BaseManualParameter
{
    private final String TAG =FocusManualParameterHTC.class.getSimpleName();

    public FocusManualParameterHTC(Parameters parameters, CameraWrapperInterface cameraUiWrapper) {
        super(parameters, "", "", "", cameraUiWrapper,1);
        isSupported = SettingsManager.get(Settings.M_Focus).isSupported();
        key_value = SettingsManager.get(Settings.M_Focus).getKEY();
        isVisible = isSupported;
        stringvalues = SettingsManager.get(Settings.M_Focus).getValues();
    }


    @Override
    public void setValue(int valueToSet, boolean setToCamera)
    {
        parameters.set(key_value, valueToSet+"");
        ((ParametersHandler) cameraUiWrapper.getParameterHandler()).SetParametersToCamera(parameters);
    }

}
