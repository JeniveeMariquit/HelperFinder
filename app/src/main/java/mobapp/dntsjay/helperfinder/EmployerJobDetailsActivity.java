package mobapp.dntsjay.helperfinder;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
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

public class EmployerJobDetailsActivity extends AppCompatActivity implements View.OnClickListener {
    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;
    private EditText txtLookingFor;
    private EditText txtRatePerDay;
    private EditText txtJobDescription;
    private EditText txtQualifications;
    private EditText txtLocation;
    private Integer JOBID;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_employer_job_details);
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        txtLookingFor = findViewById(R.id.txtApplicantSkills);
        txtRatePerDay = findViewById(R.id.txtRate);
        txtJobDescription = findViewById(R.id.txtJobDescription);
        txtQualifications = findViewById(R.id.txtQualification);
        txtLocation = findViewById(R.id.txtLocation);
        findViewById(R.id.btnUpdateJob).setOnClickListener(this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_24dp);
        toolbar.setNavigationOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(), EmployerJobsActivity.class);
                startActivity(i);
            }
        });

        Bundle bundle = getIntent().getExtras();
        if(bundle != null){
//            txtViewUID.setText(bundle.getString("UID"));
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
                    String JobDescription = dataSnapshot.child("JobDescription").getValue().toString();
                    String Qualification = dataSnapshot.child("Qualification").getValue().toString();
                    String Location = dataSnapshot.child("Location").getValue().toString();
                    txtLookingFor.setText(LookingFor);
                    txtRatePerDay.setText(RatePerDay);
                    txtJobDescription.setText(JobDescription);
                    txtQualifications.setText(Qualification);
                    txtLocation.setText(Location);
                }
                @Override
                public void onCancelled(DatabaseError databaseError) {
                }
            });
        }
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if(id == R.id.btnUpdateJob){
            updateJob(txtLookingFor.getText().toString(),txtRatePerDay.getText().toString(),txtJobDescription.getText().toString(),txtQualifications.getText().toString(),txtLocation.getText().toString());
        }
    }

    public static class Job {
        public String LookingFor;
        public String RatePerDay;
        public String JobDescription;
        public String Qualification;
        public String Location;
        public String DatePosted;
        public Integer JobID;
        public String Uid;

        public Job() {
            // Default constructor required for calls to DataSnapshot.getValue(User.class)
        }

        public Job(String lookingFor, String ratePerDay, String jobDescription, String qualification, String location, String datePosted, Integer jobID, String uid) {
            this.LookingFor = lookingFor;
            this.RatePerDay = ratePerDay;
            this.JobDescription = jobDescription;
            this.Qualification = qualification;
            this.Location = location;
            this.DatePosted = datePosted;
            this.JobID = jobID;
            this.Uid = uid;

        }
    }

    public void updateJob(final String lookingfor, final String rate, final String jobdesc, final String qualifications, final String location){
        FirebaseUser user = mAuth.getCurrentUser();
        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        Date date = new Date();
        Job job = new Job(lookingfor, rate, jobdesc, qualifications, location, dateFormat.format(date), JOBID, user.getUid());
        mDatabase.child("jobs").child(JOBID.toString()).setValue(job).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(EmployerJobDetailsActivity.this, "Successfully Updated a Job.",
                        Toast.LENGTH_SHORT).show();
                Intent s = new Intent(getApplicationContext(), EmployerJobsActivity.class);
                startActivity(s);

            }
        }).addOnFailureListener(new OnFailureListener(){
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(EmployerJobDetailsActivity.this, "Failed to Create a Job.",
                        Toast.LENGTH_SHORT).show();
            }
        });
    }
}
