package garg.sarthik.clipboard;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
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
    ClipboardManager clipboardManager;
    ClipData clipData;

    List<Clip> clipList;
    Context context;

    public ClipAdaptor(List<Clip> clipList, Context context) {
        Collections.reverse(clipList);
        this.clipList = clipList;
        this.context = context;
        clipboardManager = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.layout_clipitem, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, final int i) {

        final Clip clip = clipList.get(i);

        viewHolder.tvClipContent.setText(clip.getContent());
        viewHolder.tvClipDate.setText(clip.getDate());

        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clipData = ClipData.newPlainText("adaptor", clip.getContent());
                clipboardManager.setPrimaryClip(clipData);

                Toast.makeText(context, "Added to Clipboard", Toast.LENGTH_SHORT).show();
            }
        });
        viewHolder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                context.startActivity(new Intent(context, EditActivity.class).putExtra("clip", clipList.get(i)));
                return true;
            }
        });

        viewHolder.cbItemSelected.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                clipList.get(i).setChecked(isChecked);
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

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            tvClipContent = itemView.findViewById(R.id.tvClipContent);
            tvClipDate = itemView.findViewById(R.id.tvClipDate);
            cbItemSelected = itemView.findViewById(R.id.cbItemSelected);

        }
    }
}
