package googleplaydeveloper.jdotspacegdot.expolog;


import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;


/**
 * A simple {@link Fragment} subclass.
 */
public class HierarchyEditFragment extends Fragment {

    private int mHierarchyId;
    private String[] mDiscomfortValues;
    private String[] mAvoidanceValues;
    public static final int NEW_ACTIVITY = -100;
    public static final int UNDEFINED_ACTIVITY = -1;
    public static final String HIERARCHY_ID = "HIERARCHY_ID";

    private EditText mInputActivity;
    private TextView mHierarchyDiscomfortText;
    private SeekBar mHierarchyDiscomfortInput;
    private TextView mHierarchyAvoidanceText;
    private SeekBar mHierarchyAvoidanceInput;

    public HierarchyEditFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        Bundle bundle = this.getArguments();
        mHierarchyId = bundle == null ? NEW_ACTIVITY : bundle.getInt(HIERARCHY_ID);
        mDiscomfortValues = getResources().getStringArray(R.array.discomfort_ratings);
        mAvoidanceValues = getResources().getStringArray(R.array.avoidance_ratings);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View layout = inflater.inflate(R.layout.fragment_hierarchy_edit, container, false);

        mInputActivity = (EditText)layout.findViewById(R.id.input_activity);
        mHierarchyDiscomfortText = (TextView)layout.findViewById(R.id.hierarchy_discomfort_text);
        mHierarchyDiscomfortInput = (SeekBar)layout.findViewById(R.id.hierarchy_discomfort_input);
        mHierarchyAvoidanceText = (TextView)layout.findViewById(R.id.hierarchy_avoidance_text);
        mHierarchyAvoidanceInput = (SeekBar)layout.findViewById(R.id.hierarchy_avoidance_input);

        setInitialValues();
        setupSeekBars();

        return layout;

    }

    private void setupSeekBars() {

        mHierarchyDiscomfortText.setText(mDiscomfortValues[mHierarchyDiscomfortInput.getProgress()]);
        mHierarchyDiscomfortInput.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                mHierarchyDiscomfortText.setText(mDiscomfortValues[progress]);
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });

        mHierarchyAvoidanceText.setText(mAvoidanceValues[mHierarchyAvoidanceInput.getProgress()]);
        mHierarchyAvoidanceInput.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                mHierarchyAvoidanceText.setText(mAvoidanceValues[progress]);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });

    }

    private void setInitialValues() {
        if (mHierarchyId != NEW_ACTIVITY) {
            SQLiteDatabase database = null;
            Cursor cursor = null;
            try {
                database = new DBHelper(getActivity()).getReadableDatabase();
                cursor = database.query(
                        "HIERARCHY",
                        new String[]{"DISCOMFORT", "AVOIDANCE", "ACTIVITY"},
                        "_id = ?",
                        new String[]{Integer.toString(mHierarchyId)},
                        null, null, null
                );
                cursor.moveToFirst();
                mInputActivity.setText(cursor.getString(cursor.getColumnIndex("ACTIVITY")));
                mHierarchyDiscomfortInput.setProgress(cursor.getInt(cursor.getColumnIndex("DISCOMFORT")));
                mHierarchyAvoidanceInput.setProgress(cursor.getInt(cursor.getColumnIndex("AVOIDANCE")));
            }
            catch (SQLiteException e) {
                Toast.makeText(getActivity(), "Database error", Toast.LENGTH_SHORT).show();
            }
            finally {
                if (cursor != null) {
                    cursor.close();
                }
                if (database != null) {
                    database.close();
                }
            }
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_hierarchy_edit, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.hierarchy_edit_save:
                saveHierarchyActivity();
                ((MainActivity)getActivity()).hideKeyboard(getActivity());
                getActivity().getFragmentManager().popBackStack();
                return true;
            case R.id.hierarchy_edit_delete:
                deleteHierarchyActivity();
                ((MainActivity)getActivity()).hideKeyboard(getActivity());
                getActivity().getFragmentManager().popBackStack();
                return true;
            default:
        }
        return super.onOptionsItemSelected(item);
    }

    private void saveHierarchyActivity() {

        ContentValues values = new ContentValues();
        values.put("ACTIVITY", mInputActivity.getText().toString().trim());
        values.put("DISCOMFORT", mHierarchyDiscomfortInput.getProgress());
        values.put("AVOIDANCE", mHierarchyAvoidanceInput.getProgress());

        SQLiteDatabase database = null;
        try {
            database = new DBHelper(getActivity()).getWritableDatabase();
            if (mHierarchyId == NEW_ACTIVITY) {
                database.insert("HIERARCHY", null, values);
            }
            else {
                database.update("HIERARCHY", values, "_id = ?", new String[]{Integer.toString(mHierarchyId)});
            }
        }
        catch (SQLiteException e) {
            Toast.makeText(getActivity(), "Database error", Toast.LENGTH_SHORT).show();
        }
        finally {
            if (database != null) {
                database.close();
            }
        }

    }

    private void deleteHierarchyActivity() {
        if (mHierarchyId != NEW_ACTIVITY) {
            SQLiteDatabase database = null;
            try {
                database = new DBHelper(getActivity()).getWritableDatabase();
                database.delete("HIERARCHY", "_id = ?", new String[]{Integer.toString(mHierarchyId)});
                // need to update all the other activities
                ContentValues values = new ContentValues();
                values.put("ACTIVITY_ID", UNDEFINED_ACTIVITY);
                database.update("EXPOSURE", values, "ACTIVITY_ID = ?", new String[] {Integer.toString(mHierarchyId)});
            } catch (SQLiteException e) {
                Toast.makeText(getActivity(), "Database error", Toast.LENGTH_SHORT).show();
            } finally {
                if (database != null) {
                    database.close();
                }
            }
        }
    }

}
