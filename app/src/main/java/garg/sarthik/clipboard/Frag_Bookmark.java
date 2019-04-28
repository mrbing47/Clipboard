package garg.sarthik.clipboard;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

public class Frag_Bookmark extends Fragment {

    List<Clip> clipList;
    RecyclerView rvClipBoard;
    ClipAdaptor clipAdaptor;
    FragmentUpdateAll fragmentUpdateAll;
    SpaceItemDecoration spaceItemDecoration;

    private boolean isBordered = false;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.layout_rview, container, false);
        rvClipBoard = view.findViewById(R.id.rvClipBoard);
        callAdapter();

        return view;
    }

    private void callAdapter() {
        final StaggeredGridLayoutManager staggeredGridLayoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        rvClipBoard.setLayoutManager(staggeredGridLayoutManager);
        spaceItemDecoration = new SpaceItemDecoration(Statics.border);
        clipList = ClipApplication.getClipDb().getClipDao().getBookmarked();
        clipAdaptor = new ClipAdaptor(clipList, getContext(), this);
        rvClipBoard.setAdapter(clipAdaptor);
        if (!isBordered) {
            rvClipBoard.addItemDecoration(spaceItemDecoration);
            isBordered = true;
        }
    }

    public void update() {
        callAdapter();
    }

    public void updateOther() {
        fragmentUpdateAll.updateAdapterAll();
    }

    public List<Clip> send() {
        return clipList;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof FragmentUpdateAll) {
            fragmentUpdateAll = (FragmentUpdateAll) context;
        }
    }

    public interface FragmentUpdateAll {
        void updateAdapterAll();
    }

    public class SpaceItemDecoration extends RecyclerView.ItemDecoration {

        private int space;

        public SpaceItemDecoration(int space) {
            this.space = space;
        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            int position = parent.getChildAdapterPosition(view);
            StaggeredGridLayoutManager.LayoutParams lp = (StaggeredGridLayoutManager.LayoutParams) view.getLayoutParams();
            int spanIndex = lp.getSpanIndex();
            int left;
            int right;
            int top;
            int bottom;

            float dip4 = space >> 1;
            float dip8 = space;
            Resources r = getResources();
            float px4 = TypedValue.applyDimension(
                    TypedValue.COMPLEX_UNIT_DIP,
                    dip4,
                    r.getDisplayMetrics()
            );
            float px8 = TypedValue.applyDimension(
                    TypedValue.COMPLEX_UNIT_DIP,
                    dip8,
                    r.getDisplayMetrics()
            );


            if (position == 0 || position == 1) {
                Log.e("Pos", "getItemOffsets: " + position);
                top = (int) px8;
                bottom = (int) px4;
            } else {
                Log.e("Pos", "getItemOffsets: " + position);
                top = bottom = (int) px4;
            }
            if (spanIndex == 1) {
                left = (int) px4;
                right = (int) px8;
            } else {
                left = (int) px8;
                right = (int) px4;
            }

            outRect.set(left, top, right, bottom);
            Log.e("TAG", "getItemOffsets: " + spanIndex);

        }
    }
}
