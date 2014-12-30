package com.troop.freedcam.i_camera.parameters;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by troop on 17.12.2014.
 */


public abstract class AbstractManualParameter implements I_ManualParameter
{

    private List<I_ManualParameterEvent> events;
    protected AbstractParameterHandler camParametersHandler;

    public AbstractManualParameter(AbstractParameterHandler camParametersHandler)
    {
        this.camParametersHandler = camParametersHandler;
        events = new ArrayList<I_ManualParameterEvent>();
    }

    public interface I_ManualParameterEvent
    {
        void onIsSupportedChanged(boolean value);
        void onIsSetSupportedChanged(boolean value);
        void onMaxValueChanged(int max);
        void onMinValueChanged(int min);
        void onCurrentValueChanged(int current);
        void onValuesChanged(String[] values);
    }


    public void addEventListner(I_ManualParameterEvent eventListner)
    {
        if (!events.contains(eventListner))
            events.add(eventListner);
    }
    public void removeEventListner(I_ManualParameterEvent parameterEvent)
    {
        if (events.contains(parameterEvent))
            events.remove(parameterEvent);
    }

    public void currentValueChanged(int current)
    {
        for (int i= 0; i< events.size(); i ++)
        {
            if (events.get(i) == null)
            {
                events.remove(i);
                i--;

            }
            else
                events.get(i).onCurrentValueChanged(current);
        }
    }

    public void BackgroundIsSupportedChanged(boolean value)
    {
        for (int i= 0; i< events.size(); i ++)
        {
            if (events.get(i) == null)
            {
                events.remove(i);
                i--;

            }
            else
                events.get(i).onIsSupportedChanged(value);
        }
    }
    public void BackgroundIsSetSupportedChanged(boolean value)
    {
        for (int i= 0; i< events.size(); i ++)
        {
            if (events.get(i) == null)
            {
                events.remove(i);
                i--;

            }
            else
                events.get(i).onIsSetSupportedChanged(value);
        }
    }

    public void BackgroundValuesChanged(String[] value)
    {
        for (int i= 0; i< events.size(); i ++)
        {
            if (events.get(i) == null)
            {
                events.remove(i);
                i--;

            }
            else
                events.get(i).onValuesChanged(value);
        }
    }

    @Override
    public boolean IsSupported() {
        return false;
    }

    @Override
    public int GetMaxValue() {
        return 0;
    }

    @Override
    public int GetMinValue() {
        return 0;
    }

    @Override
    public int GetValue() {
        return 0;
    }

    @Override
    public void SetValue(int valueToSet)
    {
        setvalue(valueToSet);
        currentValueChanged(valueToSet);
    }

    @Override
    public void RestartPreview() {

    }

    protected void setvalue(int valueToset)
    {}
}
