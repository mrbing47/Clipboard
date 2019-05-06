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

public class Frag_Clip extends Fragment {

    List<Clip> clipList;
    RecyclerView rvClipBoard;
    ClipAdaptor clipAdaptor;
    FragmentUpdateBookmark fragmentUpdateBookmark;

    private boolean isBordered = false;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.layout_noclip, container, false);
        rvClipBoard = view.findViewById(R.id.rvClipBoardAll);

        callAdapter();
        return view;
    }

    private void callAdapter() {

        clipList = ClipApplication.getClipDb().getClipDao().getAll();
        Log.e("TAG_ClipAll", "callAdapter: \n\n" + clipList.size());
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

    void update() {
        callAdapter();
    }

    public List<Clip> send() {
        return clipList;
    }

    public void updateOther() {
        fragmentUpdateBookmark.updateAdapterBookmark();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof FragmentUpdateBookmark) {
            fragmentUpdateBookmark = (FragmentUpdateBookmark) context;
        }
    }

    public interface FragmentUpdateBookmark {
        void updateAdapterBookmark();
    }


}
