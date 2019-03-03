package garg.sarthik.clipboard;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
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

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.layout_rview, container, false);
        rvClipBoard = view.findViewById(R.id.rvClipBoard);
        callAdapter();

        return view;
    }

    private void callAdapter() {
        rvClipBoard.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));
        clipList = ClipApplication.getClipDb().getClipDao().getAll();
        clipAdaptor = new ClipAdaptor(clipList, getContext(), this);
        rvClipBoard.setAdapter(clipAdaptor);
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
