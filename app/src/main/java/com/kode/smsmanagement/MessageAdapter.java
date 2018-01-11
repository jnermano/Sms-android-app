package com.kode.smsmanagement;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.kode.smsmanagement.model.Message;

import java.util.List;
import java.util.Locale;

/**
 * Created by Ermano
 * on 1/8/2018.
 */

public class MessageAdapter extends ArrayAdapter<Message> {

    private Context context;
    private List<Message> messages;
    LayoutInflater inflater ;

    private class ViewHolder{
        TextView tv_msg, tv_date;
        public ViewHolder(){}
    }


    public MessageAdapter(@NonNull Context context, int resource, @NonNull List<Message> objects) {
        super(context, resource, objects);

        this.context = context;
        messages = objects;

        inflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        ViewHolder holder;
        int layoutResource = 0;
        String status = "";
        Message message = messages.get(position);

        if (message.getSender().equals("Me")){
            layoutResource = R.layout.right_chat_adapter;

            if(message.getStatus() == -101)
                status = "* Sending...";
            else if(message.getStatus() == Activity.RESULT_OK)
                status = "* Sent";
            else if(message.getStatus() == 0)
                status = "* No response";
            else
                status = "* Error";

        }else{
            layoutResource = R.layout.left_chat_adapter;
        }



        //if (convertView == null){
            holder = new ViewHolder();
            convertView = inflater.inflate(layoutResource, parent, false);

            holder.tv_msg = (TextView) convertView.findViewById(R.id.adapter_conv_tv_msg);
            holder.tv_date = (TextView) convertView.findViewById(R.id.adapter_conv_tv_date);

            convertView.setTag(holder);
        /*}else {
            holder = (ViewHolder) convertView.getTag();
        }*/

        holder.tv_msg.setText(message.getMessage());

        holder.tv_date.setText(String.format(Locale.US, "%s * %s %s", message.getDatecreated(), message.getOperator(), status));


        return convertView;
    }
}
