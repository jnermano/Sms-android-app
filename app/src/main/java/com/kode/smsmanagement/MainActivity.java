package com.kode.smsmanagement;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.SparseBooleanArray;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;

import com.kode.smsmanagement.model.Message;

import java.util.List;

import permissions.dispatcher.NeedsPermission;
import permissions.dispatcher.OnPermissionDenied;
import permissions.dispatcher.OnShowRationale;
import permissions.dispatcher.PermissionRequest;
import permissions.dispatcher.RuntimePermissions;

@RuntimePermissions
public class MainActivity extends AppCompatActivity {

    private static final int NEW_SMS = 101;
    private Handler handler;

    private ListView listView;
    private ConversationAdapter conversationAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        /*
         * Check for permissions
         */
        MainActivityPermissionsDispatcher.needSmsWithPermissionCheck(this);
        MainActivityPermissionsDispatcher.needReadContactsWithPermissionCheck(this);

        listView = (ListView) findViewById(R.id.listview);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                Intent intent = new Intent(MainActivity.this, NewSMSActivity.class);

                String number = "";
                Message message = ((Message)adapterView.getItemAtPosition(i));

                if (message.getSender().equals("Me")){
                    number = message.getReceiver();

                }else{
                    number = message.getSender();
                }

                intent.putExtra("number", number);
                startActivityForResult(intent, NEW_SMS);
            }
        });

        listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);
        listView.setMultiChoiceModeListener(new AbsListView.MultiChoiceModeListener() {
            @Override
            public void onItemCheckedStateChanged(ActionMode actionMode, int i, long l, boolean b) {

                final int checkedCount = listView.getCheckedItemCount();
                actionMode.setTitle(checkedCount + " Selected");
                conversationAdapter.toggleSelection(i);
            }

            @Override
            public boolean onCreateActionMode(ActionMode actionMode, Menu menu) {
                actionMode.getMenuInflater().inflate(R.menu.main_delete_menu, menu);
                return true;
            }

            @Override
            public boolean onPrepareActionMode(ActionMode actionMode, Menu menu) {
                return false;
            }

            @Override
            public boolean onActionItemClicked(ActionMode actionMode, MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.delete:
                        SparseBooleanArray selected = conversationAdapter.getSelectedIds();


                        for (int i = (selected.size() - 1); i >= 0; i--) {
                            if (selected.valueAt(i)) {
                                try {
                                    Message selecteditem = conversationAdapter.getItem(selected.keyAt(i));
                                    conversationAdapter.remove(selecteditem);
                                    String interloc = "";
                                    if (selecteditem.getSender().equals("Me"))
                                        interloc = selecteditem.getReceiver();
                                    else
                                        interloc = selecteditem.getSender();

                                    Message.deleteAll(Message.class, "conversation = ?", interloc);

                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                        actionMode.finish();
                        return true;
                    default:
                        return false;
                }
            }

            @Override
            public void onDestroyActionMode(ActionMode actionMode) {

            }
        });


        findViewById(R.id.fab).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(MainActivity.this, NewSMSActivity.class);
                startActivityForResult(i, NEW_SMS);
            }
        });

        handler = new Handler(Looper.getMainLooper()) {

            public void handleMessage(android.os.Message msg) {
                super.handleMessage(msg);
                switch (msg.what){
                    case 1:
                        updateMessageList();
                        break;
                }
            }
        };

        Session.setHandler(handler);

        updateMessageList();

    }


    private void updateMessageList(){
        List<Message> messages = Message.findWithQuery(Message.class, "SELECT * FROM message WHERE id IN (SELECT MAX(id) FROM message GROUP BY conversation) order by id desc");

        conversationAdapter = new ConversationAdapter(this, R.layout.adapter_conversation, messages);

        //ArrayAdapter<Message> adapter = new ArrayAdapter<Message>(this, android.R.layout.simple_list_item_1, messages);
        listView.setAdapter(conversationAdapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Session.setHandler(handler);

        updateMessageList();

    }


    /*
     * Manage permissions by using empty functions
     */

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        // NOTE: delegate the permission handling to generated method
        MainActivityPermissionsDispatcher.onRequestPermissionsResult(this, requestCode, grantResults);
    }

    @NeedsPermission( {Manifest.permission.SEND_SMS, Manifest.permission.READ_SMS, Manifest.permission.RECEIVE_SMS})
    void needSms(){}

    @NeedsPermission(Manifest.permission.READ_CONTACTS)
    void needReadContacts(){}


    @OnPermissionDenied({Manifest.permission.SEND_SMS, Manifest.permission.READ_SMS, Manifest.permission.RECEIVE_SMS})
    void needSmsDenied(){
        finish();
    }

    @OnPermissionDenied(Manifest.permission.READ_CONTACTS)
    void readContactsDenied(){

    }

    @OnShowRationale( {Manifest.permission.SEND_SMS, Manifest.permission.READ_SMS, Manifest.permission.RECEIVE_SMS})
    void showRationalForSms(PermissionRequest request){
        showDialogForRational(R.string.permission_sms_rationale, request);
    }

    @OnShowRationale(Manifest.permission.READ_CONTACTS)
    void showRationalForContact(PermissionRequest request){
        showDialogForRational(R.string.permission_contacts_rationale, request);
    }

    void showDialogForRational(@StringRes int messageResId, final PermissionRequest request) {
        new AlertDialog.Builder(this)
                .setPositiveButton(R.string.button_allow, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(@NonNull DialogInterface dialog, int which) {
                        request.proceed();
                    }
                })
                .setNegativeButton(R.string.button_deny, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(@NonNull DialogInterface dialog, int which) {
                        request.cancel();
                    }
                })
                .setCancelable(false)
                .setMessage(messageResId)
                .show();
    }
}
