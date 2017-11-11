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
import android.widget.TextView;
import android.widget.Toast;


/**
 * A simple {@link Fragment} subclass.
 */
public class ExposureEditFragment extends Fragment {

    private int mExposureId;
    private View mView;
    private TextView mActivity;
    private TextView mDate;
    private EditText mInstance;
    private EditText mPrediction;
    private EditText mChallenge;
    private EditText mExperience;
    private EditText mReflection;
    private EditText mNotes;
    public static final String EXPOSURE_ID = "EXPOSURE_ID";


    public ExposureEditFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        mExposureId = this.getArguments().getInt(EXPOSURE_ID);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        mView = inflater.inflate(R.layout.fragment_exposure_edit, container, false);
        mActivity = (TextView)mView.findViewById(R.id.exposure_activity);
        mDate = (TextView)mView.findViewById(R.id.exposure_date);
        mInstance = (EditText)mView.findViewById(R.id.exposure_instance);
        mPrediction = (EditText)mView.findViewById(R.id.exposure_prediction);
        mChallenge = (EditText)mView.findViewById(R.id.exposure_challenge);
        mExperience = (EditText)mView.findViewById(R.id.exposure_experience);
        mReflection = (EditText)mView.findViewById(R.id.exposure_reflection);
        mNotes = (EditText)mView.findViewById(R.id.exposure_notes);

        retrieveExposureData();

        return mView;

    }

    private void retrieveExposureData() {

        SQLiteDatabase database = null;
        Cursor cursor = null;
        try {
            database = new DBHelper(getActivity()).getReadableDatabase();
            cursor = database.query(
                    "EXPOSURE",
                    new String[] {
                            "_id",
                            "ACTIVITY_ID",
                            "INSTANCE",
                            "PREDICTION",
                            "CHALLENGE",
                            "EXPERIENCE",
                            "REFLECTION",
                            "NOTES",
                            "DATE(TIMESTAMP, 'LOCALTIME') AS TIMESTAMP"
                    },
                    "_id = ?",
                    new String[]{Integer.toString(mExposureId)},
                    null, null, null
            );
            cursor.moveToFirst();

            int activityId = cursor.getInt(cursor.getColumnIndex("ACTIVITY_ID"));
            mDate.setText(cursor.getString(cursor.getColumnIndex("TIMESTAMP")));
            mInstance.setText(cursor.getString(cursor.getColumnIndex("INSTANCE")));
            mPrediction.setText(cursor.getString(cursor.getColumnIndex("PREDICTION")));
            mChallenge.setText(cursor.getString(cursor.getColumnIndex("CHALLENGE")));
            mExperience.setText(cursor.getString(cursor.getColumnIndex("EXPERIENCE")));
            mReflection.setText(cursor.getString(cursor.getColumnIndex("REFLECTION")));
            mNotes.setText(cursor.getString(cursor.getColumnIndex("NOTES")));

            if (activityId == HierarchyEditFragment.UNDEFINED_ACTIVITY) {
                mActivity.setText("(undefined)");
            }
            else {
                cursor = database.query(
                        "HIERARCHY",
                        new String[] {"ACTIVITY"},
                        "_id = ?",
                        new String[] {Integer.toString(activityId)},
                        null, null, null
                );
                if (cursor != null && cursor.getCount() > 0) {
                    cursor.moveToFirst();
                    mActivity.setText(cursor.getString(cursor.getColumnIndex("ACTIVITY")));
                }
                else {
                    mActivity.setText("(undefined)");
                }
            }

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

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_exposure_edit, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.exposure_edit_save:
                saveExposure();
                ((MainActivity)getActivity()).hideKeyboard(getActivity());
                getActivity().getFragmentManager().popBackStack();
                return true;
            case R.id.exposure_edit_delete:
                deleteExposure();
                ((MainActivity)getActivity()).hideKeyboard(getActivity());
                getActivity().getFragmentManager().popBackStack();
                return true;
            default:
        }
        return super.onOptionsItemSelected(item);
    }

    private void saveExposure() {
        ContentValues values = new ContentValues();
        values.put("INSTANCE", mInstance.getText().toString().trim());
        values.put("PREDICTION", mPrediction.getText().toString().trim());
        values.put("CHALLENGE", mChallenge.getText().toString().trim());
        values.put("EXPERIENCE", mExperience.getText().toString().trim());
        values.put("REFLECTION", mReflection.getText().toString().trim());
        values.put("NOTES", mNotes.getText().toString().trim());

        SQLiteDatabase database = null;
        try {
            database = new DBHelper(getActivity()).getWritableDatabase();
            database.update("EXPOSURE", values, "_id = ?", new String[] {Integer.toString(mExposureId)});
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

    private void deleteExposure() {
        SQLiteDatabase database = null;
        try {
            database = new DBHelper(getActivity()).getWritableDatabase();
            database.delete("EXPOSURE", "_id = ?", new String[]{Integer.toString(mExposureId)});
        } catch (SQLiteException e) {
            Toast.makeText(getActivity(), "Database error", Toast.LENGTH_SHORT).show();
        } finally {
            if (database != null) {
                database.close();
            }
        }
    }

}
