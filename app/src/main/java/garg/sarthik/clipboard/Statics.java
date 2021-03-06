package garg.sarthik.clipboard;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.util.TypedValue;
import android.view.Menu;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

public class Statics {

    public static final String gridView = "grid";
    public static final String listView = "list";
    public static final int border = 10;
    public static boolean initial = true;
    public static String currentActivity = "";
    public static boolean isListening = false;
    public static boolean isHiddenActivity = false;
    public static String layout;
    public static Menu menuMain;
    public static Menu menuSearch;
    public static int checkedCounter = 0;


    public static void updateMenu(Context context) {

        if (context instanceof MainActivity) {

            if (checkedCounter > 0) {
                menuMain.findItem(R.id.miStart).setVisible(false);
                menuMain.findItem(R.id.miStop).setVisible(false);


                menuMain.findItem(R.id.miGridView).setVisible(false);
                menuMain.findItem(R.id.miListView).setVisible(false);

                menuMain.findItem(R.id.miHidden).setVisible(false);

                menuMain.findItem(R.id.miSearch).setVisible(false);

                menuMain.findItem(R.id.miDelete).setVisible(true);
                menuMain.findItem(R.id.miDeleteAll).setVisible(true);

                if (isHiddenActivity)
                    menuMain.findItem(R.id.miUnhide).setVisible(true);
                else
                    menuMain.findItem(R.id.miHide).setVisible(true);

            } else {

                if (layout.equals(gridView)) {
                    menuMain.findItem(R.id.miGridView).setVisible(false);
                    menuMain.findItem(R.id.miListView).setVisible(true);
                } else {
                    menuMain.findItem(R.id.miGridView).setVisible(true);
                    menuMain.findItem(R.id.miListView).setVisible(false);
                }


                Log.e("Statics", "updateMenu: \n\n\n" + isHiddenActivity);

                if (isHiddenActivity) {
                    menuMain.findItem(R.id.miHidden).setVisible(false);
                    menuMain.findItem(R.id.miStart).setVisible(false);
                    menuMain.findItem(R.id.miStop).setVisible(false);
                }
                else {

                    if (isListening) {
                        menuMain.findItem(R.id.miStart).setVisible(false);
                        menuMain.findItem(R.id.miStop).setVisible(true);
                    } else {
                        menuMain.findItem(R.id.miStart).setVisible(true);
                        menuMain.findItem(R.id.miStop).setVisible(false);
                    }

                    menuMain.findItem(R.id.miHidden).setVisible(true);
                }
                menuMain.findItem(R.id.miSearch).setVisible(true);

                menuMain.findItem(R.id.miUnhide).setVisible(false);
                menuMain.findItem(R.id.miHide).setVisible(false);

                menuMain.findItem(R.id.miDelete).setVisible(false);
                menuMain.findItem(R.id.miDeleteAll).setVisible(false);
            }
        }
        if (context instanceof SearchActivity) {
            if (checkedCounter > 0) {
                menuSearch.findItem(R.id.miDeleteSearch).setVisible(true);
                menuSearch.findItem(R.id.miDeleteAllSearch).setVisible(true);

            } else {

                menuSearch.findItem(R.id.miDeleteSearch).setVisible(false);
                menuSearch.findItem(R.id.miDeleteAllSearch).setVisible(false);
            }
        }
    }

    public static class SpaceItemDecoration extends RecyclerView.ItemDecoration {

        private int space;
        private List<Clip> clipList;
        private Context context;

        public SpaceItemDecoration(List<Clip> clipList, Context context) {

            this.space = border;
            this.clipList = clipList;
            this.context = context;
        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            int position = parent.getChildAdapterPosition(view);
            int left;
            int right;
            int top;
            int bottom;

            float dip4 = space >> 1;
            float dip8 = space;
            Resources r = context.getResources();
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


            if (layout.equals(Statics.gridView)) {

                StaggeredGridLayoutManager.LayoutParams lp = (StaggeredGridLayoutManager.LayoutParams) view.getLayoutParams();
                int spanIndex = lp.getSpanIndex();


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

            } else {

                if (position == 0) {

                    Log.e("Pos", "getItemOffsets: " + position);
                    top = (int) px8;
                    bottom = (int) px4;

                } else {
                    if (position == clipList.size() - 1) {
                        Log.e("Pos", "getItemOffsets: " + position);
                        top = (int) px4;
                        bottom = (int) px8;
                    } else {
                        Log.e("Pos", "getItemOffsets: " + position);
                        top = bottom = (int) px4;
                    }
                }
                left = (int) px8;
                right = (int) px8;
                outRect.set(left, top, right, bottom);

            }

        }
    }
}
