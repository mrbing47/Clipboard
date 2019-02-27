package garg.sarthik.clipboard;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

public class Frag_Clip extends Fragment {

    public static final String TAG = "Fragment";
    List<Clip> clipList;
    String type;
    RecyclerView rvClipBoard;
    ClipAdaptor clipAdaptor;
    MainActivity ma;

    public Frag_Clip newInstance(String type, ArrayList<Clip> clipList, MainActivity ma) {
        Frag_Clip fragClip = new Frag_Clip();
        Bundle bundle = new Bundle();
        bundle.putString("type", type);
        bundle.putParcelable("ma",ma);
        bundle.putParcelableArrayList("clips",clipList);
        fragClip.setArguments(bundle);
        return fragClip;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.layout_rview, container, false);
        rvClipBoard = view.findViewById(R.id.rvClipBoard);

        if (getArguments() != null) {
            type = getArguments().getString("type");
            clipList = getArguments().getParcelableArrayList("clips");
            ma = getArguments().getParcelable("ma");
            callAdapter();
        }

        return view;
    }

    public void callAdapter() {
        rvClipBoard.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));

//        if (type.equals("all")) {
//            clipList = ClipApplication.getClipDb().getClipDao().getAll();
//            Log.e(TAG, "callAdapter: \nSize = " + clipList.size() + "\nType = " + type);
//        } else {
//            clipList = ClipApplication.getClipDb().getClipDao().getBookmarked();
//            Log.e(TAG, "callAdapter: \nSize = " + clipList.size() + "\nType = " + type);
//        }

        clipAdaptor = new ClipAdaptor(clipList, getContext(), ma);
        rvClipBoard.setAdapter(clipAdaptor);
    }

    void updateAdapter(){
        Log.e(TAG, "updateAdapter: Received");
        clipAdaptor.notifyDataSetChanged();
    }
}
