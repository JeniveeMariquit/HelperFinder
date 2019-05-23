package mobapp.dntsjay.helperfinder;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class EmployerMessageActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;


    private ListView UserMessageListView;
    private clsUserMessageList listOfUserMessage;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_employer_message);
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        UserMessageListView = findViewById(R.id.userMessageListView);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_24dp);
        Bundle bundle = getIntent().getExtras();
        if(bundle != null) {
            String text = bundle.getString("typeOfUser");
            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent i = new Intent(getApplicationContext(), ApplicantHomeActivity.class);
                    startActivity(i);
                }
            });
        }
        else{
            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent i = new Intent(getApplicationContext(), EmployerHomeActivity.class);
                    startActivity(i);
                }
            });
        }


        final HashMap<String,String > SentByAndMessageID = new HashMap<>();
        listOfUserMessage = new clsUserMessageList();
        FirebaseUser user = mAuth.getCurrentUser();
        mDatabase.child("userMessages").orderByChild("SentToID").equalTo(user.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot ds: dataSnapshot.getChildren()){
                    listOfUserMessage = ds.getValue(clsUserMessageList.class);
                    List<HashMap<String, String>> hashMapList = new ArrayList<>();
                    SimpleAdapter userMessageAdapters = new SimpleAdapter(getApplicationContext(),hashMapList, R.layout.user_message_info, new String[]{"1","2"},
                            new int[]{R.id.txtSentBy,R.id.txtMessageID});
                    SentByAndMessageID.put(listOfUserMessage.SentByName, listOfUserMessage.MessageID);
                    Iterator it = SentByAndMessageID.entrySet().iterator();
                    while (it.hasNext()){
                        HashMap<String, String> resultMap = new HashMap<>();
                        Map.Entry pair = (Map.Entry)it.next();
                        resultMap.put("1",pair.getKey().toString());
                        resultMap.put("2", pair.getValue().toString());
                        hashMapList.add(resultMap);
                    }
                    UserMessageListView.setAdapter(userMessageAdapters);

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        UserMessageListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent s = new Intent(EmployerMessageActivity.this, MessageActivity.class);
                s.putExtra("messageID", UserMessageListView.getItemAtPosition(i).toString());
                startActivity(s);
            }
        });
    }
}
