package mobapp.dntsjay.helperfinder;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class MessageActivity extends AppCompatActivity implements OnClickListener {
    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;
    private Integer messageNumber = 0;
    private String MessageID = "";
    private String NameofUser = "";
    private RecyclerView messageListView;
    private clsMessageList listOfMessage;
    List<clsMessageListData> list;
    private EditText txtWriteMessage;
    RecyclerViewAdapter recycler;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        messageListView = findViewById(R.id.messageListView);
        txtWriteMessage = findViewById(R.id.txtWriteMessage);
        findViewById(R.id.btnSendMessage).setOnClickListener(this);
        findViewById(R.id.btnVideoCall).setOnClickListener(this);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_24dp);
        toolbar.setNavigationOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(), EmployerMessageActivity.class);
                startActivity(i);
            }
        });

        Bundle bundle = getIntent().getExtras();
        if(bundle != null){
            String text = bundle.getString("messageID");

            String []splitArray=text.split("=");
            String new_text=splitArray[2];
            final String messageID = new_text.substring(0, new_text.length() - 1);
            MessageID = messageID;
            listOfMessage = new clsMessageList();

            mDatabase.child("messages").child(messageID).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    list = new ArrayList<>();
                    for(DataSnapshot ds: dataSnapshot.getChildren()){
                        listOfMessage = ds.getValue(clsMessageList.class);
                        clsMessageListData listdata = new clsMessageListData();
                        String econtent=listOfMessage.getContent();
                        String esentByName=listOfMessage.getSentByName();
                        listdata.setContent(econtent);
                        listdata.setSentByName(esentByName);
                        list.add(listdata);
//                        Toast.makeText(MessageActivity.this,""+econtent,Toast.LENGTH_LONG).show();
                    }
                    recycler = new RecyclerViewAdapter(list);
                    RecyclerView.LayoutManager layoutmanager = new LinearLayoutManager(MessageActivity.this);
                    messageListView.setLayoutManager(layoutmanager);
                    messageListView.setItemAnimator( new DefaultItemAnimator());
                    messageListView.setAdapter(recycler);
                    messageListView.smoothScrollToPosition(recycler.getItemCount() - 1);

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

            mDatabase.child("messages").child(MessageID).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for(DataSnapshot ds: dataSnapshot.getChildren()){
                        messageNumber = messageNumber + 1;
                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }

        FirebaseUser user = mAuth.getCurrentUser();
        mDatabase.child("users").child(user.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String Firstname = dataSnapshot.child("Firstname").getValue().toString();
                String Middlename = dataSnapshot.child("Middlename").getValue().toString();
                String Lastname = dataSnapshot.child("Lastname").getValue().toString();
                NameofUser = Firstname+" "+Middlename+" "+Lastname;
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if(id == R.id.btnSendMessage){
            sendMessage(txtWriteMessage.getText().toString());
        }
        if(id == R.id.btnVideoCall){
            txtWriteMessage.setText("Requested a video call...");
            sendMessage(txtWriteMessage.getText().toString());
            Intent s = new Intent(MessageActivity.this, VideoCallActivity.class);
            s.putExtra("messageID", MessageID);
            startActivity(s);
        }
    }

    public void txtMessageClick(View v){
        messageListView.smoothScrollToPosition(recycler.getItemCount() - 1);
    }


    public static class Messages {
        public Integer MessageNumber;
        public String Content;
        public String DateSent;
        public String OwnRecNo;
        public String SentByID;
        public String SentByName;

        public Messages() {
            // Default constructor required for calls to DataSnapshot.getValue(User.class)
        }

        public Messages(Integer messageNumber, String content, String dateSent, String ownRecNo, String SentByID, String sentByName) {
            this.MessageNumber = messageNumber;
            this.Content = content;
            this.DateSent = dateSent;
            this.OwnRecNo = ownRecNo;
            this.SentByID = SentByID;
            this.SentByName = sentByName;

        }
    }

    public void sendMessage(final String messageContent){
        FirebaseUser user = mAuth.getCurrentUser();
        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        Date date = new Date();
        JobDetailsActivity.Messages messages = new JobDetailsActivity.Messages(messageNumber, messageContent, dateFormat.format(date), MessageID, user.getUid(),NameofUser);
//        mDatabase.child("messages").child(MessageID).child(messageNumber.toString()).setValue(messages).addOnSuccessListener(new OnSuccessListener<Void>() {
//            @Override
//            public void onSuccess(Void aVoid) {
//                txtWriteMessage.setText("");
//            }
//        }).addOnFailureListener(new OnFailureListener(){
//            @Override
//            public void onFailure(@NonNull Exception e) {
//                Toast.makeText(MessageActivity.this, "Failed to Send a Message.",
//                        Toast.LENGTH_SHORT).show();
//            }
//        });
        mDatabase.child("messages").child(MessageID).push().setValue(messages).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                txtWriteMessage.setText("");
            }
        }).addOnFailureListener(new OnFailureListener(){
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(MessageActivity.this, "Failed to Send a Message.",
                        Toast.LENGTH_SHORT).show();
            }
        });
    }
}
