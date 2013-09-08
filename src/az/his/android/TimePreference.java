package az.his.android;

import android.content.Context;
import android.content.res.TypedArray;
import android.preference.DialogPreference;
import android.text.format.DateFormat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.TimePicker;

import java.util.Calendar;

public class TimePreference extends DialogPreference {
    public static final String LOGTAG = "HIS-Time-Pref";

    private Calendar calendar;
    private TimePicker picker = null;
    private java.text.DateFormat timeFormat;

    public TimePreference(Context ctxt) {
        this(ctxt, null);
    }

    public TimePreference(Context ctxt, AttributeSet attrs) {
        this(ctxt, attrs, 0);
    }

    public TimePreference(Context ctxt, AttributeSet attrs, int defStyle) {
        super(ctxt, attrs, defStyle);

        setPositiveButtonText(R.string.timepref_set);
        setNegativeButtonText(R.string.timepref_cancel);

        timeFormat = DateFormat.getTimeFormat(getContext());
    }

    @Override
    protected View onCreateDialogView() {
        picker = new TimePicker(getContext());
        picker.setIs24HourView(DateFormat.is24HourFormat(getContext()));
        return (picker);
    }

    @Override
    protected void onBindDialogView(View v) {
        super.onBindDialogView(v);
        picker.setCurrentHour(calendar.get(Calendar.HOUR_OF_DAY));
        picker.setCurrentMinute(calendar.get(Calendar.MINUTE));
    }

    @Override
    protected void onDialogClosed(boolean positiveResult) {
        super.onDialogClosed(positiveResult);

        if (positiveResult) {
            calendar.set(Calendar.HOUR_OF_DAY, picker.getCurrentHour());
            calendar.set(Calendar.MINUTE, picker.getCurrentMinute());
            Log.d(LOGTAG, "Calendar set hour: " + picker.getCurrentHour() + ", hour in cal: " + calendar.get(Calendar.HOUR_OF_DAY) + ". Date: " + calendar.getTime());
            Log.d(LOGTAG, "j.u.Date TZ: " + calendar.getTime().getTimezoneOffset());

            setSummary(getSummary());
            int val = getNormalValue(calendar);
            if (callChangeListener(val)) {
                persistLong(val);
                notifyChanged();
            }
        }
    }

    private int getNormalValue(Calendar cal) {
        Calendar cal2 = Calendar.getInstance();
        cal2.set(Calendar.HOUR_OF_DAY, 0);
        cal2.set(Calendar.MINUTE, 0);
        cal2.set(Calendar.SECOND, 0);
        cal2.set(Calendar.MILLISECOND, 0);
        return (int) (cal.getTimeInMillis() - cal2.getTimeInMillis());
    }

    @Override
    protected Object onGetDefaultValue(TypedArray a, int index) {
        return (a.getString(index));
    }

    @Override
    protected void onSetInitialValue(boolean restoreValue, Object defaultValue) {
        long val = 0;
        if (restoreValue) {
            val = getPersistedLong(0);
        } else {
            if (defaultValue == null) {
                val = getNormalValue(Calendar.getInstance());
            } else {
                val = Long.parseLong((String) defaultValue);
            }
        }

        calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);

        calendar.add(Calendar.MILLISECOND, (int) val);
        calendar.set(Calendar.MILLISECOND, 0);

        setSummary(getSummary());
    }

    @Override
    public CharSequence getSummary() {
        if (calendar == null) {
            return null;
        }
        Log.d(LOGTAG, "Setting date " + calendar.getTime() + ". Format TZ: " + DateFormat.getTimeFormat(getContext()).getTimeZone());
        Log.d(LOGTAG, "Calendar TZ: " + calendar.getTimeZone());
        return timeFormat.format(calendar.getTime());
    }
}
