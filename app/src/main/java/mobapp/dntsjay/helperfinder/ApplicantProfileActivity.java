package mobapp.dntsjay.helperfinder;

import android.app.AlertDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
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
import java.util.Date;

public class ApplicantProfileActivity extends AppCompatActivity implements View.OnClickListener {
    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;

    public String messageID = "";
    public String NameofApplicant = "";
    public String NameofEmployer = "";
    public Integer messageNumber = 0;

    public String ApplicantID;

    public TextView txtApplicantName;
    public TextView txtApplicantAddress;
    public TextView txtApplicantContact;
    public TextView txtApplicantEmail;
    public TextView txtApplicantExperience;
    public TextView txtApplicantSkills;
    public TextView txtApplicantEducational;
    public EditText txtMessageContent;
    public TextView txtRating;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_applicant_profile);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_24dp);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(), EmployersLocationActivity.class);
                startActivity(i);
            }
        });

        mDatabase = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        txtApplicantName = findViewById(R.id.txtApplicantName);
        txtApplicantAddress = findViewById(R.id.txtApplicantAddress);
        txtApplicantContact = findViewById(R.id.txtApplicantContact);
        txtApplicantEmail = findViewById(R.id.txtApplicantEmail);
        txtApplicantExperience = findViewById(R.id.txtApplicantExperience);
        txtApplicantSkills = findViewById(R.id.txtApplicantSkills);
        txtApplicantEducational = findViewById(R.id.txtApplicantEduc);
        txtRating = findViewById(R.id.txtRating);

        findViewById(R.id.btnSendMessage).setOnClickListener(this);

        Bundle bundle = getIntent().getExtras();
        if(bundle != null){
            String text = bundle.getString("applicantID");
            final String job_applicantID = text;
            ApplicantID = job_applicantID;
            FirebaseUser user = mAuth.getCurrentUser();
            messageID = user.getUid() + ApplicantID;
//            Toast.makeText(getApplicationContext(), job_applicantID, Toast.LENGTH_SHORT).show();
            mDatabase.child("users").child(job_applicantID).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    txtApplicantName.setText(dataSnapshot.child("Firstname").getValue().toString() + " "+dataSnapshot.child("Middlename").getValue().toString() +" "+ dataSnapshot.child("Lastname").getValue().toString());
                    NameofApplicant = (dataSnapshot.child("Firstname").getValue().toString() + " "+dataSnapshot.child("Middlename").getValue().toString() +" "+ dataSnapshot.child("Lastname").getValue().toString());
                    txtApplicantAddress.setText(dataSnapshot.child("Address").getValue().toString());
                    txtApplicantEmail.setText(dataSnapshot.child("Email").getValue().toString());
                    txtApplicantContact.setText(dataSnapshot.child("ContactNumber").getValue().toString());
                    txtApplicantExperience.setText(dataSnapshot.child("AdditionalInfo").child("WorkExperience").getValue().toString());
                    txtApplicantSkills.setText(dataSnapshot.child("AdditionalInfo").child("Skills").getValue().toString());
                    txtApplicantEducational.setText(dataSnapshot.child("AdditionalInfo").child("EducationalAttainment").getValue().toString());
                }
                @Override
                public void onCancelled(DatabaseError databaseError) {
                }
            });
            mDatabase.child("users").child(user.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    NameofEmployer = (dataSnapshot.child("Firstname").getValue().toString() + " "+dataSnapshot.child("Middlename").getValue().toString() +" "+ dataSnapshot.child("Lastname").getValue().toString());
                }
                @Override
                public void onCancelled(DatabaseError databaseError) {
                }
            });

            mDatabase.child("user_rating").child(ApplicantID).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    txtRating.setText("Ratings from "+dataSnapshot.child("NumberOfRating").getValue().toString()+" is: "+dataSnapshot.child("TotalRating").getValue().toString());
                }
                @Override
                public void onCancelled(DatabaseError databaseError) {
                }
            });

            mDatabase.child("messages").child(messageID).addValueEventListener(new ValueEventListener() {
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
    }

    @Override
    public void onClick(View v) {
        int id  = v.getId();
        if(id == R.id.btnSendMessage){
            AlertDialog.Builder mBuilder = new AlertDialog.Builder(ApplicantProfileActivity.this);
            View mView = getLayoutInflater().inflate(R.layout.dialog_new_message,null);
            txtMessageContent = mView.findViewById(R.id.txtMessageContent);
            mView.findViewById(R.id.btnMessageSent).setOnClickListener(this);
            mBuilder.setView(mView);
            AlertDialog dialogs =  mBuilder.create();
            dialogs.show();
        }
        if(id == R.id.btnMessageSent){
            sendMessage(txtMessageContent.getText().toString());
        }
    }

    public void sendMessage(final String messageContent){
        FirebaseUser user = mAuth.getCurrentUser();
        UserMessages usermessages = new UserMessages(messageID, ApplicantID, user.getUid(), NameofEmployer);
        mDatabase.child("userMessages").child(messageID).setValue(usermessages).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                FirebaseUser user = mAuth.getCurrentUser();
                UserMessages usermessages = new UserMessages(user.getUid()+ApplicantID, user.getUid(), ApplicantID,NameofApplicant);
                mDatabase.child("userMessages").child(ApplicantID+user.getUid()).setValue(usermessages).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        FirebaseUser user = mAuth.getCurrentUser();
                        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
                        Date date = new Date();
                        Messages messages = new Messages(messageNumber, messageContent, dateFormat.format(date), messageID, user.getUid(),NameofEmployer);
//                        mDatabase.child("messages").child(messageID).child(messageNumber.toString()).setValue(messages).addOnSuccessListener(new OnSuccessListener<Void>() {
//                            @Override
//                            public void onSuccess(Void aVoid) {
//                                Toast.makeText(ApplicantProfileActivity.this, "Successfully Sent a Message.",
//                                        Toast.LENGTH_SHORT).show();
//                                txtMessageContent.setText("");
//                            }
//                        }).addOnFailureListener(new OnFailureListener(){
//                            @Override
//                            public void onFailure(@NonNull Exception e) {
//                                Toast.makeText(ApplicantProfileActivity.this, "Failed to Send a Message.",
//                                        Toast.LENGTH_SHORT).show();
//                            }
//                        });
                        mDatabase.child("messages").child(messageID).push().setValue(messages).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText(ApplicantProfileActivity.this, "Successfully Sent a Message.",
                                        Toast.LENGTH_SHORT).show();
                                txtMessageContent.setText("");
                            }
                        }).addOnFailureListener(new OnFailureListener(){
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(ApplicantProfileActivity.this, "Failed to Send a Message.",
                                        Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }).addOnFailureListener(new OnFailureListener(){
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(ApplicantProfileActivity.this, "Failed to Send a Message.",
                                Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener(){
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(ApplicantProfileActivity.this, "Failed to Send a Message.",
                        Toast.LENGTH_SHORT).show();
            }
        });
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

    public static class UserMessages {
        public String MessageID;
        public String SentToID;
        public String SentByID;
        public String SentByName;

        public UserMessages() {
            // Default constructor required for calls to DataSnapshot.getValue(User.class)
        }

        public UserMessages(String messageID, String sentToID, String sentByID, String sentByName) {
            this.MessageID = messageID;
            this.SentToID = sentToID;
            this.SentByID = sentByID;
            this.SentByName = sentByName;

        }
    }
}
