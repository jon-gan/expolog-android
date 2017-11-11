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
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;


/**
 * A simple {@link Fragment} subclass.
 */
public class PlanFragment extends Fragment {

    private SQLiteDatabase mDatabase;
    private Cursor mCursor;

    public PlanFragment() {
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

        View layout = inflater.inflate(R.layout.fragment_plan, container, false);
        ListView planList = (ListView)layout.findViewById(R.id.plan_list);

        try {
            SQLiteOpenHelper helper = new DBHelper(getActivity());
            mDatabase = helper.getReadableDatabase();
            mCursor = mDatabase.query(
                    "PLAN",
                    new String[]{"_id", "INSTANCE"},
                    null, null, null, null, null
            );
            CursorAdapter adapter = new SimpleCursorAdapter(
                    getActivity(),
                    android.R.layout.simple_list_item_1,
                    mCursor,
                    new String[] {"INSTANCE"},
                    new int[] {android.R.id.text1},
                    0
            );
            planList.setAdapter(adapter);
        }
        catch (SQLiteException e) {
            Toast.makeText(getActivity(), "Database error", Toast.LENGTH_SHORT).show();
        }
        planList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Bundle bundle = new Bundle();
                bundle.putInt(PlanEditFragment.PLAN_ID, (int) id);
                ((MainActivity) getActivity()).selectItem(5, bundle);
            }
        });

        return layout;

    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_plan, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.plan_plan:
                ((MainActivity)getActivity()).selectItem(4, null);
                return true;
            default:
        }
        return super.onOptionsItemSelected(item);
    }

}
