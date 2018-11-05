package iqbal.mykost.Fragment;


import android.content.res.Resources;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CompoundButton;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.firebase.client.Firebase;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


import java.util.List;

import iqbal.mykost.Adapter.AlbumsAdapter;
import iqbal.mykost.Adapter.FilterDekatKampushAdapter;
import iqbal.mykost.Adapter.FilterFasilitasAdapter;
import iqbal.mykost.Adapter.FilterTermurahAdapter;
import iqbal.mykost.Album;
import iqbal.mykost.R;
import iqbal.mykost.SharedVariable;


/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentHome extends Fragment {


    public FragmentHome() {
        // Required empty public constructor
    }

    Spinner sp_filter;
    TextView txtNotif;
    public static ProgressBar progressBar;
    DatabaseReference ref,refUser;
    private FirebaseAuth fAuth;
    private FirebaseAuth.AuthStateListener fStateListener;
    private RecyclerView recyclerView;
    private AlbumsAdapter adapter;
    String filter;
    private List<Album> albumList;
    FilterTermurahAdapter filterTermurahAdapter;
    FilterFasilitasAdapter filterFasilitasAdapter;
    FilterDekatKampushAdapter filterDekatKampushAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        final View view = inflater.inflate(R.layout.fragment_home, container, false);
        Firebase.setAndroidContext(getActivity());
        Firebase.setAndroidContext(this.getActivity());
        FirebaseApp.initializeApp(this.getActivity());
        ref = FirebaseDatabase.getInstance().getReference();

        progressBar = (ProgressBar) view.findViewById(R.id.progressBar);
        sp_filter = (Spinner) view.findViewById(R.id.sp_filter);
        recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
        adapter = new AlbumsAdapter(getActivity());
        filterTermurahAdapter = new FilterTermurahAdapter(getActivity());
        filterFasilitasAdapter = new FilterFasilitasAdapter(getActivity());
        filterDekatKampushAdapter = new FilterDekatKampushAdapter(getActivity());

        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(getActivity(), 3);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.addItemDecoration(new GridSpacingItemDecoration(3, dpToPx(1), true));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adapter);

        sp_filter.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                filter= adapterView.getItemAtPosition(i).toString();

                if (filter.equals("Filter dari Termurah")){
                    RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(getActivity(), 3);
                    recyclerView.setLayoutManager(mLayoutManager);
                    recyclerView.addItemDecoration(new GridSpacingItemDecoration(3, dpToPx(1), true));
                    recyclerView.setItemAnimator(new DefaultItemAnimator());
                    recyclerView.setAdapter(filterTermurahAdapter);
                }else if (filter.equals("Filter dari Fasilitas Terbaik")){
                    RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(getActivity(), 3);
                    recyclerView.setLayoutManager(mLayoutManager);
                    recyclerView.addItemDecoration(new GridSpacingItemDecoration(3, dpToPx(1), true));
                    recyclerView.setItemAnimator(new DefaultItemAnimator());
                    recyclerView.setAdapter(filterFasilitasAdapter);
                }else  if (filter.equals("Filter Dekat ke Kampus")){
                    RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(getActivity(), 3);
                    recyclerView.setLayoutManager(mLayoutManager);
                    recyclerView.addItemDecoration(new GridSpacingItemDecoration(3, dpToPx(1), true));
                    recyclerView.setItemAnimator(new DefaultItemAnimator());
                    recyclerView.setAdapter(filterDekatKampushAdapter);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        return view;
    }


    public class GridSpacingItemDecoration extends RecyclerView.ItemDecoration {

        private int spanCount;
        private int spacing;
        private boolean includeEdge;

        public GridSpacingItemDecoration(int spanCount, int spacing, boolean includeEdge) {
            this.spanCount = spanCount;
            this.spacing = spacing;
            this.includeEdge = includeEdge;
        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            int position = parent.getChildAdapterPosition(view); // item position
            int column = position % spanCount; // item column

            if (includeEdge) {
                outRect.left = spacing - column * spacing / spanCount; // spacing - column * ((1f / spanCount) * spacing)
                outRect.right = (column + 1) * spacing / spanCount; // (column + 1) * ((1f / spanCount) * spacing)

                if (position < spanCount) { // top edge
                    outRect.top = spacing;
                }
                outRect.bottom = spacing; // item bottom
            } else {
                outRect.left = column * spacing / spanCount; // column * ((1f / spanCount) * spacing)
                outRect.right = spacing - (column + 1) * spacing / spanCount; // spacing - (column + 1) * ((1f /    spanCount) * spacing)
                if (position >= spanCount) {
                    outRect.top = spacing; // item top
                }
            }
        }
    }

    /**
     * Converting dp to pixel
     */
    private int dpToPx(int dp) {
        Resources r = getResources();
        return Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, r.getDisplayMetrics()));
    }

}


