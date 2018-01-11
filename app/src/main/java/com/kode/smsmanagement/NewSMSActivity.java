package com.kode.smsmanagement;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.telephony.SmsManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;

import com.kode.smsmanagement.model.Message;

import java.util.List;

public class NewSMSActivity extends AppCompatActivity {

    private EditText edt_number, edt_msg;
    private ListView listView;

    private Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_sms);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        edt_msg = (EditText) findViewById(R.id.edt_msg);
        listView = (ListView) findViewById(R.id.list_msg_of_a_conversation);

        edt_number = (EditText) findViewById(R.id.edt_number);
        edt_number.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                String txt = editable.toString();
                if (txt.length() > 3){
                    updateConversation();
                }
            }
        });

        findViewById(R.id.btn_send).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendSms();
            }
        });

        handler = new Handler(Looper.getMainLooper()) {

            public void handleMessage(android.os.Message msg) {
                super.handleMessage(msg);
                switch (msg.what){
                    case 1:
                        updateConversation();
                        break;
                }
            }
        };

        Session.setHandler(handler);

        Intent intent = getIntent();
        if (intent !=null){
            edt_number.setText(intent.getStringExtra("number"));
            if (edt_number.getText().toString().length() > 0){
                edt_number.setEnabled(false);
            }
        }

        updateConversation();

    }

    private void updateConversation(){
        String number = edt_number.getText().toString();

        List<Message> messages = Message.find(Message.class, "sender = ? or receiver = ?", new String[]{number, number});

        MessageAdapter adapter = new MessageAdapter(this, R.layout.left_chat_adapter, messages);

        //ArrayAdapter<Message> adapter = new ArrayAdapter<Message>(this, android.R.layout.simple_list_item_1, messages);

        listView.setAdapter(adapter);

        listView.setSelection(adapter.getCount() - 1);

    }

    void sendSms(){
        String number = edt_number.getText().toString();
        String msg = edt_msg.getText().toString();

        String SMS_SENT = "SMS_SENT";

        edt_msg.setText("");

        if (number.length() >= 3){

            if (number.length() == 8){
                number = "+509" + number;
                edt_number.setText(number);
            }

            final Message message = new Message();
            message.setDatecreated(Session.getDateAndTime());
            message.setReceiver(number);
            message.setSender("Me");
            message.setMessage(msg);
            message.setOperator(Session.getOperatorName(this));
            message.setStatus(-101);
            long id_msg = message.save();

            updateConversation();

            PendingIntent sentPendingIntent = PendingIntent.getBroadcast(this, (int)id_msg, new Intent(SMS_SENT), 0);

            registerReceiver(new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    message.setStatus(getResultCode());
                    message.save();
                    updateConversation();
                }
            }, new IntentFilter(SMS_SENT));

            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(number, null, msg, sentPendingIntent, null);


        }
    }

}
