package googleplaydeveloper.jdotspacegdot.expolog;


import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.CursorAdapter;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Spinner;
import android.widget.Toast;


public class ExposureFragment extends Fragment {

    private SQLiteDatabase mDatabase;
    private Cursor mCursor;
    private Cursor mHierarchyCursor;
    private View mView;
    private CheckBox mHierarchyFilterCheckBox;
    private Spinner mHierarchyFilterSpinner;

    private static final int REFILTER_LIST_ALL = -1;
    private final String QUERY_ALL = "SELECT _id, DATETIME(TIMESTAMP, 'LOCALTIME') AS TIME, DATE(TIMESTAMP, 'LOCALTIME') AS TIMESTAMP, INSTANCE" +
            " FROM EXPOSURE" +
            " ORDER BY TIME DESC";
    private final String QUERY_FILTERED = "SELECT _id, DATETIME(TIMESTAMP, 'LOCALTIME') AS TIME, DATE(TIMESTAMP, 'LOCALTIME') AS TIMESTAMP, INSTANCE" +
            " FROM EXPOSURE" +
            " WHERE ACTIVITY_ID = ?" +
            " ORDER BY TIME DESC";

    public ExposureFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        mView = inflater.inflate(R.layout.fragment_exposure, container, false);
        mHierarchyFilterCheckBox = (CheckBox)mView.findViewById(R.id.hierarchy_filter_checkbox);
        mHierarchyFilterSpinner = (Spinner)mView.findViewById(R.id.hierarchy_filter_spinner);

        mDatabase = new DBHelper(getActivity()).getReadableDatabase();
        setupExposureList();
        setupHierarchyActivitySpinner();
        setupHierarchyActivityCheckBox();

        return mView;

    }

    private void setupExposureList() {

        try {
            mCursor = mDatabase.rawQuery(QUERY_ALL, null);
            ListView exposureList = (ListView) mView.findViewById(R.id.exposure_list);
            SimpleCursorAdapter adapter = new SimpleCursorAdapter(
                    getActivity(),
                    android.R.layout.simple_list_item_2,
                    mCursor,
                    new String[]{"TIMESTAMP", "INSTANCE"},
                    new int[]{android.R.id.text1, android.R.id.text2},
                    0
            );
            exposureList.setAdapter(adapter);
            exposureList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    // start exposure edit
                    Bundle bundle = new Bundle();
                    bundle.putInt(ExposureEditFragment.EXPOSURE_ID, (int) id);
                    ((MainActivity) getActivity()).selectItem(7, bundle);
                }
            });
        }
        catch (SQLiteException e) {
            Toast.makeText(getActivity(), "Database error", Toast.LENGTH_SHORT).show();
        }

    }

    private void setupHierarchyActivitySpinner() {
        try {
            mHierarchyCursor = mDatabase.query(
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
            mHierarchyFilterSpinner.setAdapter(adapter);
            mHierarchyFilterSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    if (mHierarchyFilterCheckBox.isChecked()) {
                        refilterList((int)id);
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });
        }
        catch (SQLiteException e) {
            Toast.makeText(getActivity(), "Database error", Toast.LENGTH_SHORT).show();
        }
    }

    private void setupHierarchyActivityCheckBox() {
        mHierarchyFilterCheckBox.setChecked(false);
        mHierarchyFilterCheckBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mHierarchyFilterCheckBox.isChecked()) {
                    int id = ((Cursor)mHierarchyFilterSpinner.getSelectedItem()).getInt(0);
                    refilterList(id);
                }
                else {
                    refilterList(REFILTER_LIST_ALL);
                }
            }
        });
    }

    private void refilterList(int id) {
        try {
            Cursor newCursor = null;
            if (id == REFILTER_LIST_ALL) {
                newCursor = mDatabase.rawQuery(QUERY_ALL, null);
            } else {
                newCursor = mDatabase.rawQuery(QUERY_FILTERED, new String[] {Integer.toString(id)});
            }
            ListView exposureList = (ListView) mView.findViewById(R.id.exposure_list);
            CursorAdapter adapter = (CursorAdapter) exposureList.getAdapter();
            adapter.changeCursor(newCursor);
            mCursor = newCursor;
        }
        catch (SQLiteException e) {
            Toast.makeText(getActivity(), "Database error", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mCursor != null) {
            mCursor.close();
        }
        if (mDatabase != null) {
            mDatabase.close();
        }
        if (mHierarchyCursor != null) {
            mHierarchyCursor.close();
        }
    }

}
