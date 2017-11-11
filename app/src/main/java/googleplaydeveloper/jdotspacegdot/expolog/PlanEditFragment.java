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
import android.widget.SimpleCursorAdapter;
import android.widget.Spinner;
import android.widget.Toast;


/**
 * A simple {@link Fragment} subclass.
 */
public class PlanEditFragment extends Fragment {

    public static final String PLAN_ID = "PLAN_ID";

    private Spinner mInputHierarchyActivity;
    private EditText mInputInstance;
    private EditText mInputPrediction;
    private EditText mInputChallenge;

    private SQLiteDatabase mHierarchyDatabase;
    private Cursor mHierarchyCursor;


    public PlanEditFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View layout = inflater.inflate(R.layout.fragment_plan_edit, container, false);

        mInputHierarchyActivity = (Spinner)layout.findViewById(R.id.input_hierarchy_activity);
        mInputInstance = (EditText)layout.findViewById(R.id.input_instance);
        mInputPrediction = (EditText)layout.findViewById(R.id.input_prediction);
        mInputChallenge = (EditText)layout.findViewById(R.id.input_challenge);

        setupHierarchyActivitySpinner();

        return layout;
    }

    private void setupHierarchyActivitySpinner() {
        try {
            mHierarchyDatabase = new DBHelper(getActivity()).getReadableDatabase();
            mHierarchyCursor = mHierarchyDatabase.query(
                    "HIERARCHY",
                    new String[]{"_id", "ACTIVITY"},
                    null, null, null, null,
                    "DISCOMFORT, AVOIDANCE, ACTIVITY ASC"
            );
            SimpleCursorAdapter adapter = new SimpleCursorAdapter(
                    getActivity(),
                    android.R.layout.simple_spinner_item,
                    mHierarchyCursor,
                    new String[] {"ACTIVITY"},
                    new int[] {android.R.id.text1},
                    0
            );
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            mInputHierarchyActivity.setAdapter(adapter);
        }
        catch (SQLiteException e) {
            Toast.makeText(getActivity(), "Database error", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mHierarchyCursor.close();
        mHierarchyDatabase.close();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_plan_edit, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.plan_edit_save:
                savePlan();
                ((MainActivity)getActivity()).hideKeyboard(getActivity());
                getActivity().getFragmentManager().popBackStack();
                return true;
            case R.id.plan_edit_delete:
                ((MainActivity)getActivity()).hideKeyboard(getActivity());
                getActivity().getFragmentManager().popBackStack();
                return true;
            default:
        }
        return super.onOptionsItemSelected(item);
    }

    private void savePlan() {
        ContentValues values = new ContentValues();
        values.put("ACTIVITY_ID", ((Cursor)mInputHierarchyActivity.getSelectedItem()).getInt(0));
        values.put("INSTANCE", mInputInstance.getText().toString().trim());
        values.put("PREDICTION", mInputPrediction.getText().toString().trim());
        values.put("CHALLENGE", mInputChallenge.getText().toString().trim());

        SQLiteDatabase database = null;
        try {
            database = new DBHelper(getActivity()).getWritableDatabase();
            database.insert("PLAN", null, values);
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

}
