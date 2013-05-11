package com.infometers.sampleapp2;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import com.infometers.helpers.CalendarHelper;

import java.util.Date;
import java.util.List;

/**
 * Created with IntelliJ IDEA. User: ak61us Date: 3/4/13 Time: 8:14 PM To change
 * this template use File | Settings | File Templates.
 */
public class MeterArrayAdapter<Record> extends ArrayAdapter<Record> {
    private static String TAG = "com.infometers.sampleapp2";

	int mTextViewResourceId;

	public MeterArrayAdapter(Context context, int textViewResourceId,
			List<Record> records) {
		super(context, textViewResourceId, records);
		mTextViewResourceId = textViewResourceId;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null)
			convertView = View.inflate(getContext(), mTextViewResourceId, null);

		View row = convertView;
        try {
            Object item = getItem(position);
            if (item instanceof com.infometers.records.bloodglucose.Record) {
                com.infometers.records.bloodglucose.Record r = (com.infometers.records.bloodglucose.Record) item;
                if (r == null)
                    return row;

                setValue(convertView, R.id.Id, position);
                setValue(convertView, R.id.Time, r.getDateString());
                setValue(convertView, R.id.Value, r.getTextValue());
            } else if (item instanceof com.infometers.records.bloodpressure.Record) {
                com.infometers.records.bloodpressure.Record r = (com.infometers.records.bloodpressure.Record) item;
                if (r == null)
                    return row;

                setValue(convertView, R.id.textViewSystolic, r.getSys());
                setValue(convertView, R.id.textViewDiastolic, r.getDia());
                setValue(convertView, R.id.textViewPulse, r.getPul());
                Date date = new Date(r.getDate());
                setDateDayValue(convertView, R.id.textViewDate, date);
                setDateTimeValue(convertView, R.id.textViewTime, date);

            } else if (item instanceof com.infometers.records.scale.Record) {
                com.infometers.records.scale.Record r = (com.infometers.records.scale.Record) item;
                if (r == null)
                    return row;

                Date date = new Date(r.getDate());
                setValue(convertView, R.id.textViewWeight, r.getWeight());
                setDateDayValue(convertView, R.id.textViewDate, date);
                setDateTimeValue(convertView, R.id.textViewTime, date);

            }
        }
        catch (Exception ex){
            Log.e(TAG, ex.getMessage());
        }

		return (row);
	}

	public static void setValue(View view, int id, String value) {
		TextView textView = (TextView) view.findViewById(id);
		textView.setText(value);
	}

	public static void setValue(View view, int id, Integer value) {
		setValue(view, id, value.toString());
	}
    public static void setValue(View view, int id, Double value) {
        setValue(view, id, value.toString());
    }

	public static void setDateValue(View view, int id, Date date) {
		setValue(view, id, CalendarHelper.toString(date));
	}

	public static void setDateDayValue(View view, int id, Date date) {
		setValue(view, id, CalendarHelper.toDayString(date));
	}

	public static void setDateTimeValue(View view, int id, Date date) {
		setValue(view, id, CalendarHelper.toTimeString(date));
	}

}
