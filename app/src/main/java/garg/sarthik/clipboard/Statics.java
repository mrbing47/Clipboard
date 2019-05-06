package garg.sarthik.clipboard;

import android.content.Context;
import android.view.Menu;

public class Statics {

    public static final int border = 10;
    public static boolean isListening = false;
    public static Menu menu;
    public static int checkedCounter = 0;
    public static boolean isChecked = false;

    public static void swapMenu(Context context) {

        if (context instanceof MainActivity) {

            if (isChecked) {
                menu.findItem(R.id.miStart).setVisible(false);
                menu.findItem(R.id.miStop).setVisible(false);

                menu.findItem(R.id.miSearch).setVisible(false);

                menu.findItem(R.id.miDelete).setVisible(true);
                menu.findItem(R.id.miDeleteAll).setVisible(true);

            } else {

                if (isListening) {
                    menu.findItem(R.id.miStart).setVisible(false);
                    menu.findItem(R.id.miStop).setVisible(true);
                } else {
                    menu.findItem(R.id.miStart).setVisible(true);
                    menu.findItem(R.id.miStop).setVisible(false);

                }
                menu.findItem(R.id.miSearch).setVisible(true);

                menu.findItem(R.id.miDelete).setVisible(false);
                menu.findItem(R.id.miDeleteAll).setVisible(false);
            }
        }
        if(context instanceof SearchActivity){
            if (isChecked) {
                menu.findItem(R.id.miDeleteSearch).setVisible(true);
                menu.findItem(R.id.miDeleteAllSearch).setVisible(true);

            } else {

                menu.findItem(R.id.miDeleteSearch).setVisible(false);
                menu.findItem(R.id.miDeleteAllSearch).setVisible(false);
            }
        }
    }
}
