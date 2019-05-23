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

public class JobDetailsActivity extends AppCompatActivity implements View.OnClickListener{
    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;
    private Integer JOBID;
    private String empID = "";
    private String messageID = "";
    private String NameofEmployer = "";
    private String NameofUser = "";
    private Integer messageNumber = 0;
    private TextView txtJobName;
    private TextView txtJobID;
    private TextView txtLocation;
    private TextView txtPostedOn;
    private TextView txtLookingFor;
    private TextView txtRatePerDay;
    private TextView txtQualifications;

    private EditText txtMessageContent;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_job_details);
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        txtJobID = findViewById(R.id.txtJobID);
        txtLookingFor = findViewById(R.id.txtApplicantSkills);
        txtJobName = findViewById(R.id.txtJobName);
        txtPostedOn = findViewById(R.id.txtPostedOn);
        txtRatePerDay = findViewById(R.id.txtRatePerDay);
        txtQualifications = findViewById(R.id.txtQualifications);
        txtLocation = findViewById(R.id.txtLocation);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_24dp);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(), ApplicantHomeActivity.class);
                startActivity(i);
            }
        });

        findViewById(R.id.btnSendMessage).setOnClickListener(this);
        findViewById(R.id.btnApplyJob).setOnClickListener(this);

        Bundle bundle = getIntent().getExtras();
        if(bundle != null){
            String text = bundle.getString("jobID");
            String []splitArray=text.split("=");
            String new_text=splitArray[3];
            final String jobID = new_text.substring(0, new_text.length() - 1);
            JOBID = Integer.parseInt(jobID);
            mDatabase.child("jobs").child(jobID).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    String LookingFor = dataSnapshot.child("LookingFor").getValue().toString();
                    String RatePerDay = dataSnapshot.child("RatePerDay").getValue().toString();
                    String Qualification = dataSnapshot.child("Qualification").getValue().toString();
                    String Location = dataSnapshot.child("Location").getValue().toString();
                    String PostedOn = dataSnapshot.child("DatePosted").getValue().toString();
                    String jobID = dataSnapshot.child("JobID").getValue().toString();
                    empID = dataSnapshot.child("Uid").getValue().toString();
                    txtJobName.setText(LookingFor);
                    txtLookingFor.setText(LookingFor);
                    txtLocation.setText("Location : "+Location);
                    txtPostedOn.setText("Date Posted : "+PostedOn);
                    txtJobID.setText("Job ID : "+jobID);
                    txtQualifications.setText(Qualification);
                    txtRatePerDay.setText("Php "+RatePerDay+".00");

                    FirebaseUser user = mAuth.getCurrentUser();
                    messageID = user.getUid() + empID;
                    mDatabase.child("users").child(empID).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            String Firstname = dataSnapshot.child("Firstname").getValue().toString();
                            String Middlename = dataSnapshot.child("Middlename").getValue().toString();
                            String Lastname = dataSnapshot.child("Lastname").getValue().toString();
                            NameofEmployer = Firstname+" "+Middlename+" "+Lastname;
                        }
                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                        }
                    });

                    mDatabase.child("users").child(user.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            String Firstname = dataSnapshot.child("Firstname").getValue().toString();
                            String Middlename = dataSnapshot.child("Middlename").getValue().toString();
                            String Lastname = dataSnapshot.child("Lastname").getValue().toString();
                            NameofUser = Firstname+" "+Middlename+" "+Lastname;
                        }
                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                        }
                    });
                }
                @Override
                public void onCancelled(DatabaseError databaseError) {
                }
            });
        }
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

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if(id == R.id.btnSendMessage){
            AlertDialog.Builder mBuilder = new AlertDialog.Builder(JobDetailsActivity.this);
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
        if(id == R.id.btnApplyJob){
            applyJob();
        }
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

    public void sendMessage(final String messageContent){
        FirebaseUser user = mAuth.getCurrentUser();
        UserMessages usermessages = new UserMessages(messageID, empID, user.getUid(), NameofUser);
        mDatabase.child("userMessages").child(messageID).setValue(usermessages).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                FirebaseUser user = mAuth.getCurrentUser();
                UserMessages usermessages = new UserMessages(messageID, user.getUid(), empID,NameofEmployer);
                mDatabase.child("userMessages").child(empID+user.getUid()).setValue(usermessages).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        FirebaseUser user = mAuth.getCurrentUser();
                        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
                        Date date = new Date();
                        Messages messages = new Messages(messageNumber, messageContent, dateFormat.format(date), messageID, user.getUid(),NameofUser);
//                        mDatabase.child("messages").child(messageID).child(messageNumber.toString()).setValue(messages).addOnSuccessListener(new OnSuccessListener<Void>() {
//                            @Override
//                            public void onSuccess(Void aVoid) {
//                                Toast.makeText(JobDetailsActivity.this, "Successfully Sent a Message.",
//                                        Toast.LENGTH_SHORT).show();
//                                txtMessageContent.setText("");
//                            }
//                        }).addOnFailureListener(new OnFailureListener(){
//                            @Override
//                            public void onFailure(@NonNull Exception e) {
//                                Toast.makeText(JobDetailsActivity.this, "Failed to Send a Message.",
//                                        Toast.LENGTH_SHORT).show();
//                            }
//                        });
                        mDatabase.child("messages").child(messageID).push().setValue(messages).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText(JobDetailsActivity.this, "Successfully Sent a Message.",
                                        Toast.LENGTH_SHORT).show();
                                txtMessageContent.setText("");
                            }
                        }).addOnFailureListener(new OnFailureListener(){
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(JobDetailsActivity.this, "Failed to Send a Message.",
                                        Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }).addOnFailureListener(new OnFailureListener(){
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(JobDetailsActivity.this, "Failed to Send a Message.",
                                Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener(){
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(JobDetailsActivity.this, "Failed to Send a Message.",
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    public static class Employers_Applicant {
        public Integer JobID;
        public String ApplicantID;
        public String JobOwnerID;
        public String DateApplied;
        public String Status;
        public String NameofApplicant;
        public Employers_Applicant() {
            // Default constructor required for calls to DataSnapshot.getValue(User.class)
        }

        public Employers_Applicant(Integer jobID, String applicantID, String jobOwnerID, String dateApplied, String status, String nameofApplicant) {
            this.JobID = jobID;
            this.ApplicantID = applicantID;
            this.JobOwnerID = jobOwnerID;
            this.DateApplied = dateApplied;
            this.Status = status;
            this.NameofApplicant = nameofApplicant;

        }
    }

    public void applyJob(){
        FirebaseUser user = mAuth.getCurrentUser();
        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        Date date = new Date();
        Employers_Applicant employers_applicant = new Employers_Applicant(JOBID, user.getUid(), empID, dateFormat.format(date), "APPLIED",NameofUser);
        mDatabase.child("employers_applicant").child(JOBID.toString()+"_"+user.getUid()).setValue(employers_applicant).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(JobDetailsActivity.this, "Successfully sent an application.",
                        Toast.LENGTH_SHORT).show();

            }
        }).addOnFailureListener(new OnFailureListener(){
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(JobDetailsActivity.this, "Failed to Send a Message.",
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

}
