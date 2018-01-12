package com.kode.smsmanagement;

import android.content.Context;
import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.kode.smsmanagement.model.Message;

import java.util.List;

/**
 * Created by Ermano
 * on 1/7/2018.
 */

public class ConversationAdapter extends ArrayAdapter<Message> {

    private Context context;
    private List<Message> messages;
    LayoutInflater inflater;

    private SparseBooleanArray mSelectedItemsIds;

    private class ViewHolder {
        TextView tv_numer, tv_msg, tv_date;

        public ViewHolder() {
        }
    }

    public ConversationAdapter(@NonNull Context context, int resource, @NonNull List<Message> objects) {
        super(context, resource, objects);

        this.context = context;
        messages = objects;

        mSelectedItemsIds = new SparseBooleanArray();

        inflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        //super.getView(position, convertView, parent);

        ViewHolder holder;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = inflater.inflate(R.layout.adapter_conversation, parent, false);

            holder.tv_numer = (TextView) convertView.findViewById(R.id.adapter_conv_tv_destinataire);
            holder.tv_msg = (TextView) convertView.findViewById(R.id.adapter_conv_tv_msg);
            holder.tv_date = (TextView) convertView.findViewById(R.id.adapter_conv_tv_date);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        Message message = messages.get(position);

        String pre = "You : ";

        if (message.getSender().equals("Me")) {
            holder.tv_numer.setText(message.getReceiver());

        } else {
            pre = "";
            holder.tv_numer.setText(message.getSender());

            if (message.getViewed() == 0){
                holder.tv_numer.setTypeface(Typeface.DEFAULT_BOLD);
                holder.tv_msg.setTypeface(Typeface.DEFAULT_BOLD);
                holder.tv_date.setTypeface(Typeface.DEFAULT_BOLD);
            }
        }

        String msg = pre + message.getMessage();

        holder.tv_msg.setText(msg);
        holder.tv_date.setText(message.getDatecreated());


        return convertView;
    }

    @Override
    public void remove(Message object) {
        messages.remove(object);
        notifyDataSetChanged();
    }

    public List<Message> getWorldPopulation() {
        return messages;
    }

    public void toggleSelection(int position) {
        selectView(position, !mSelectedItemsIds.get(position));
    }

    public void removeSelection() {
        mSelectedItemsIds = new SparseBooleanArray();
        notifyDataSetChanged();
    }

    public void selectView(int position, boolean value) {
        if (value)
            mSelectedItemsIds.put(position, value);
        else
            mSelectedItemsIds.delete(position);
        notifyDataSetChanged();
    }

    public int getSelectedCount() {
        return mSelectedItemsIds.size();
    }

    public SparseBooleanArray getSelectedIds() {
        return mSelectedItemsIds;
    }
}
