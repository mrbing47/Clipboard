package garg.sarthik.clipboard;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
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

        clipList = ClipApplication.getClipDb().getClipDao().getAll();
        View view;
        if (clipList.isEmpty())
            view = inflater.inflate(R.layout.layout_noclip, container, false);
        else {
            view = inflater.inflate(R.layout.layout_rview, container, false);
            rvClipBoard = view.findViewById(R.id.rvClipBoard);
            callAdapter();
        }
        return view;
    }

    private void callAdapter() {

        clipList = ClipApplication.getClipDb().getClipDao().getAll();

        if (!clipList.isEmpty()) {
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
        }
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
