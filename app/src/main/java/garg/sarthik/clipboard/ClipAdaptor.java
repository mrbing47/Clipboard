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

import java.util.Collections;
import java.util.List;

public class ClipAdaptor extends RecyclerView.Adapter<ClipAdaptor.ViewHolder> {


    private static final String TAG = "Clip Adapter";
    private ClipboardManager clipboardManager;
    private ClipData clipData;
    private Object object;
    private List<Clip> clipList;
    private Context context;

    public ClipAdaptor(List<Clip> clipList, Context context, Object object) {
        Collections.reverse(clipList);
        this.object = object;
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

        //getAdapterPosition gives the exact position for the adapter
        final int position = viewHolder.getAdapterPosition();

        {//This block sets the state of the checkbox

            boolean isBookmarked = false;
            if (clipList.get(position).getBookmarked() == 1) {
                isBookmarked = true;
            }
            viewHolder.cbItemBookmarked.setChecked(isBookmarked);
        }
        boolean isSelected = clipList.get(position).isChecked();
        viewHolder.cbItemSelected.setChecked(isSelected);


        {//This block sets the content and date of the clip in the Clip Layout

            if (clipList.get(position).getContent().length() <= 256)
                viewHolder.tvClipContent.setText(clipList.get(position).getContent());
            else {
                String txt = clipList.get(position).getContent().substring(0, 253) + "...";
                viewHolder.tvClipContent.setText(txt);
                Log.e(TAG, "onBindViewHolder: " + txt);
            }

            viewHolder.tvClipDate.setText(clipList.get(position).getDate());
        }


        {//This block handles all the listeners app needs

            //This listener starts EditActivity to edit the clip content
            viewHolder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    Log.e("Focus", "onLongClick: " + clipList.get(position).getBookmarked());
                    context.startActivity(new Intent(context, EditActivity.class)
                            .putExtra("clip", clipList.get(position))
                            .putExtra("bookmark", clipList.get(position).getBookmarked()));
                    return true;
                }
            });

            //This listener adds the clip to the clipboard
            viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    clipData = ClipData.newPlainText("adaptor", clipList.get(position).getContent());
                    clipboardManager.setPrimaryClip(clipData);

                    Toast.makeText(context, "Added to Clipboard", Toast.LENGTH_SHORT).show();
                }
            });

            //This listener handles the click when the user selects the clip
            viewHolder.cbItemSelected.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (buttonView.isPressed())
                        clipList.get(position).setChecked(isChecked);
                }
            });

            //This listener handles the click when the user bookmarks the clip
            viewHolder.cbItemBookmarked.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (buttonView.isPressed()) {
                        int checked = buttonView.isChecked() ? 1 : 0;
                        Log.e(TAG, "onCheckedChanged: " + clipList.get(position).getContent());
                        clipList.get(position).setBookmarked(checked);
                        ClipApplication.getClipDb().getClipDao().updateClip(clipList.get(position));
                        if (object instanceof Frag_Clip) {
                            ((Frag_Clip) object).updateOther();

                        } else {
                            ((Frag_Bookmark) object).update();
                        }
                    }
                }
            });
        }

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
