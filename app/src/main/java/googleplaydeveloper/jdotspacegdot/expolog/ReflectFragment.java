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
public class ReflectFragment extends Fragment {

    private int mPlanId;
    private int mActivityId;
    private View mView;
    private EditText mInputExperience;
    private EditText mInputReflection;
    private EditText mInputNotes;
    private String mInstanceText;
    private String mPredictionText;
    private String mChallengeText;

    public ReflectFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        mPlanId = this.getArguments().getInt(PlanEditFragment.PLAN_ID);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View layout = inflater.inflate(R.layout.fragment_reflect, container, false);

        mView = layout;
        mInputExperience = (EditText)layout.findViewById(R.id.input_experience);
        mInputReflection = (EditText)layout.findViewById(R.id.reflect_reflection);
        mInputNotes = (EditText)layout.findViewById(R.id.reflect_notes);

        retrievePlanData();

        return layout;

    }

    private void retrievePlanData() {

        SQLiteDatabase database = null;
        Cursor cursor = null;
        try {
            database = new DBHelper(getActivity()).getReadableDatabase();
            cursor = database.query(
                    "PLAN",
                    new String[]{"ACTIVITY_ID", "INSTANCE", "PREDICTION", "CHALLENGE"},
                    "_id = ?",
                    new String[]{Integer.toString(mPlanId)},
                    null, null, null
            );
            cursor.moveToFirst();

            mInstanceText = cursor.getString(cursor.getColumnIndex("INSTANCE"));
            mPredictionText = cursor.getString(cursor.getColumnIndex("PREDICTION"));
            mChallengeText = cursor.getString(cursor.getColumnIndex("CHALLENGE"));

            ((TextView)mView.findViewById(R.id.reflect_instance)).setText(mInstanceText);
            ((TextView)mView.findViewById(R.id.reflect_prediction)).setText(mPredictionText);
            ((TextView)mView.findViewById(R.id.reflect_challenge)).setText(mChallengeText);

            mActivityId = cursor.getInt(cursor.getColumnIndex("ACTIVITY_ID"));
            if (mActivityId == HierarchyEditFragment.UNDEFINED_ACTIVITY) {
                ((TextView)mView.findViewById(R.id.reflect_activity)).setText("(undefined)");
            }
            else {
                cursor = database.query(
                        "HIERARCHY",
                        new String[] {"ACTIVITY"},
                        "_id = ?",
                        new String[] {Integer.toString(mActivityId)},
                        null, null, null
                );
                if (cursor != null && cursor.getCount() > 0) {
                    cursor.moveToFirst();
                    ((TextView) mView.findViewById(R.id.reflect_activity)).setText(
                            cursor.getString(cursor.getColumnIndex("ACTIVITY"))
                    );
                }
                else {
                    ((TextView)mView.findViewById(R.id.reflect_activity)).setText("(undefined)");
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
        inflater.inflate(R.menu.menu_reflect, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.reflect_save:
                saveReflection();
                deletePlan();
                ((MainActivity)getActivity()).hideKeyboard(getActivity());
                getActivity().getFragmentManager().popBackStack();
                return true;
            case R.id.reflect_delete_plan:
                deletePlan();
                ((MainActivity)getActivity()).hideKeyboard(getActivity());
                getActivity().getFragmentManager().popBackStack();
                return true;
            default:
        }
        return super.onOptionsItemSelected(item);
    }

    private void saveReflection() {
        ContentValues values = new ContentValues();
        values.put("ACTIVITY_ID", mActivityId);
        values.put("INSTANCE", mInstanceText);
        values.put("PREDICTION", mPredictionText);
        values.put("CHALLENGE", mChallengeText);
        values.put("EXPERIENCE", mInputExperience.getText().toString().trim());
        values.put("REFLECTION", mInputReflection.getText().toString().trim());
        values.put("NOTES", mInputNotes.getText().toString().trim());

        SQLiteDatabase database = null;
        try {
            database = new DBHelper(getActivity()).getWritableDatabase();
            database.insert("EXPOSURE", null, values);
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

    private void deletePlan() {
        SQLiteDatabase database = null;
        try {
            database = new DBHelper(getActivity()).getWritableDatabase();
            database.delete("PLAN", "_id = ?", new String[]{Integer.toString(mPlanId)});
        } catch (SQLiteException e) {
            Toast.makeText(getActivity(), "Database error", Toast.LENGTH_SHORT).show();
        } finally {
            if (database != null) {
                database.close();
            }
        }
    }

}
