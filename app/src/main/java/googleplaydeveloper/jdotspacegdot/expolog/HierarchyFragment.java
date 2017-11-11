package googleplaydeveloper.jdotspacegdot.expolog;


import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CursorAdapter;
import android.widget.ListView;
import android.widget.Toast;


/**
 * A simple {@link Fragment} subclass.
 */
public class HierarchyFragment extends Fragment {

    private SQLiteDatabase mDatabase;
    private Cursor mCursor;

    private final String QUERY = "SELECT HIERARCHY._id AS _id, COUNT(EXPOSURE._id) AS COUNT, HIERARCHY.ACTIVITY AS ACTIVITY, " +
            "HIERARCHY.DISCOMFORT AS DISCOMFORT, HIERARCHY.AVOIDANCE AS AVOIDANCE " +
            "FROM HIERARCHY LEFT OUTER JOIN EXPOSURE ON HIERARCHY._id = EXPOSURE.ACTIVITY_ID " +
            "GROUP BY HIERARCHY._id, HIERARCHY.ACTIVITY, HIERARCHY.DISCOMFORT, HIERARCHY.AVOIDANCE " +
            "ORDER BY DISCOMFORT, AVOIDANCE, ACTIVITY ASC";

    public HierarchyFragment() {
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

        View layout = inflater.inflate(R.layout.fragment_hierarchy, container, false);

        ListView hierarchyList = (ListView)layout.findViewById(R.id.hierarchy_list);
        try {
            SQLiteOpenHelper helper = new DBHelper(getActivity());
            mDatabase = helper.getReadableDatabase();
            mCursor = mDatabase.rawQuery(QUERY, null);
            CursorAdapter adapter = new HierarchyCursorAdapter(getActivity(), mCursor, 0);
            hierarchyList.setAdapter(adapter);
        }
        catch (SQLiteException e) {
            Toast.makeText(getActivity(), "Database error", Toast.LENGTH_SHORT).show();
        }
        hierarchyList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Bundle bundle = new Bundle();
                bundle.putInt(HierarchyEditFragment.HIERARCHY_ID, (int) id);
                ((MainActivity) getActivity()).selectItem(6, bundle);
            }
        });

        return layout;

    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_hierarchy, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.hierarchy_add:
                ((MainActivity)getActivity()).selectItem(6, null);
                return true;
            default:
        }
        return super.onOptionsItemSelected(item);
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
    }

}
