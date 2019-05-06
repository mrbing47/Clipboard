package garg.sarthik.clipboard;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

public class Frag_Bookmark extends Fragment {

    List<Clip> clipList;
    RecyclerView rvClipBoard;
    ClipAdaptor clipAdaptor;
    FragmentUpdateAll fragmentUpdateAll;

    private boolean isBordered = false;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

//        clipList = ClipApplication.getClipDb().getClipDao().getBookmarked();
//        View view;
//        if (clipList.isEmpty())
//            view = inflater.inflate(R.layout.layout_nobookmark, container, false);
//        else {
//            view = inflater.inflate(R.layout.layout_rview, container, false);
//            rvClipBoard = view.findViewById(R.id.rvClipBoard);
//            callAdapter();
//        }
        View view = inflater.inflate(R.layout.layout_nobookmark, container, false);
        rvClipBoard = view.findViewById(R.id.rvClipBoardBookmark);

        callAdapter();
        return view;
    }

    private void callAdapter() {

        clipList = ClipApplication.getClipDb().getClipDao().getBookmarked();
        Log.e("TAG_ClipBookmark", "callAdapter: \n\n" + clipList.size());
        if (!clipList.isEmpty()) {

            rvClipBoard.setVisibility(View.VISIBLE);

            if (Statics.layout.equals(Statics.gridView))
                rvClipBoard.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));
            else
                rvClipBoard.setLayoutManager(new LinearLayoutManager(getContext()));


            clipAdaptor = new ClipAdaptor(clipList, getContext(), this);
            rvClipBoard.setAdapter(clipAdaptor);
            if (!isBordered) {
                rvClipBoard.addItemDecoration(new Statics.SpaceItemDecoration(clipList, getContext()));
                isBordered = true;
            }
        } else
            rvClipBoard.setVisibility(View.GONE);
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


}
