package googleplaydeveloper.jdotspacegdot.expolog;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

public class HierarchyCursorAdapter extends CursorAdapter {

    public HierarchyCursorAdapter(Context context, Cursor cursor, int flags) {
        super(context, cursor, 0);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.item_hierarchy, parent, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        TextView exposures = (TextView) view.findViewById(R.id.hierarchy_exposures);
        TextView discomfort = (TextView) view.findViewById(R.id.hierarchy_discomfort);
        TextView avoidance = (TextView) view.findViewById(R.id.hierarchy_avoidance);
        TextView activity = (TextView) view.findViewById(R.id.hierarchy_activity);

        exposures.setText(Integer.toString(cursor.getInt(cursor.getColumnIndex("COUNT"))));
        discomfort.setText(Integer.toString(cursor.getInt(cursor.getColumnIndex("DISCOMFORT"))));
        avoidance.setText(Integer.toString(cursor.getInt(cursor.getColumnIndex("AVOIDANCE"))));
        activity.setText(cursor.getString(cursor.getColumnIndex("ACTIVITY")));
    }

}
