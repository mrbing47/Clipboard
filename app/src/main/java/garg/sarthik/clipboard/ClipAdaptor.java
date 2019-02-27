package garg.sarthik.clipboard;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

public class ClipAdaptor extends RecyclerView.Adapter<ClipAdaptor.ViewHolder> {


    private static final String TAG = "Clip Adapter";
    private ClipboardManager clipboardManager;
    private ClipData clipData;

    private MainActivity ma;
    private List<Clip> clipList;
    private Context context;

    public ClipAdaptor(List<Clip> clipList, Context context, MainActivity ma) {
        //Collections.reverse(clipList);
        this.ma = ma;
        this.clipList = clipList;
        this.context = context;
        clipboardManager = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
        Log.e(TAG, "ClipAdaptor: ");
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.layout_clipitem, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, final int i) {

        boolean isChecked = false;
        if (clipList.get(i).getBookmarked() == 1) {
            isChecked = true;
        }

        if (clipList.get(i).getContent().length() <= 256)
            viewHolder.tvClipContent.setText(clipList.get(i).getContent());
        else {
            String txt = clipList.get(i).getContent().substring(0, 253) + "...";
            viewHolder.tvClipContent.setText(txt);
            Log.e(TAG, "onBindViewHolder: " + txt);
        }

        viewHolder.tvClipDate.setText(clipList.get(i).getDate());
        viewHolder.cbItemBookmarked.setChecked(isChecked);

        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clipData = ClipData.newPlainText("adaptor", clipList.get(i).getContent());
                clipboardManager.setPrimaryClip(clipData);

                Toast.makeText(context, "Added to Clipboard", Toast.LENGTH_SHORT).show();
            }
        });
        viewHolder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Log.e("Focus", "onLongClick: " + clipList.get(i).getBookmarked());
                context.startActivity(new Intent(context, EditActivity.class)
                        .putExtra("clip", clipList.get(i))
                        .putExtra("bookmark", clipList.get(i).getBookmarked()));
                return true;
            }
        });

        viewHolder.cbItemSelected.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                clipList.get(i).setChecked(isChecked);
            }
        });
        viewHolder.cbItemBookmarked.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                int checked = isChecked ? 1 : 0;
                clipList.get(i).setBookmarked(checked);
                ClipApplication.getClipDb().getClipDao().updateClip(clipList.get(i));
            }
        });

    }

    @Override
    public int getItemCount() {
        return clipList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        TextView tvClipContent;
        TextView tvClipDate;
        CheckBox cbItemSelected;
        CheckBox cbItemBookmarked;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            tvClipContent = itemView.findViewById(R.id.tvClipContent);
            tvClipDate = itemView.findViewById(R.id.tvClipDate);
            cbItemSelected = itemView.findViewById(R.id.cbItemSelected);
            cbItemBookmarked = itemView.findViewById(R.id.cbItemBookmarked);

        }
    }
}
