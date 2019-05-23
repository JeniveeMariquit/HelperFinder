package mobapp.dntsjay.helperfinder;

import android.app.AlertDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
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
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class EmployerJobsActivity extends AppCompatActivity implements View.OnClickListener {
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    private EditText txtLookingFor;
    private EditText txtRatePerDay;
    private EditText txtJobDescription;
    private EditText txtQualifications;
    private EditText txtLocation;
    private Integer jobID = 0;



    private ListView jobListView;
    private ArrayList<String> jobLists;
    private clsJobLists listOfJobs;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_employer_jobs);
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        jobListView = findViewById(R.id.jobListView);
        findViewById(R.id.fabAddJob).setOnClickListener(this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_24dp);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(), EmployerHomeActivity.class);
                startActivity(i);
            }
        });

        mDatabase.child("jobs").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot ds: dataSnapshot.getChildren()){
                    jobID = jobID + 1;
                    Log.d("NUMBER OF USER", jobID.toString());
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        final HashMap<String,String > jobdescanddateandid = new HashMap<>();
        listOfJobs = new clsJobLists();
        jobLists = new ArrayList<>();
        FirebaseUser user = mAuth.getCurrentUser();
        mDatabase.child("jobs").orderByChild("Uid").equalTo(user.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot ds: dataSnapshot.getChildren()){
                    listOfJobs = ds.getValue(clsJobLists.class);
                    List<HashMap<String, String>> hashMapList = new ArrayList<>();
                    SimpleAdapter userAdapters = new SimpleAdapter(getApplicationContext(),hashMapList, R.layout.job_info, new String[]{"1","2"},
                            new int[]{R.id.txtJobDesc,R.id.txtDatePosted});
                    jobdescanddateandid.put(listOfJobs.LookingFor, "ID="+listOfJobs.JobID);
                    Iterator it = jobdescanddateandid.entrySet().iterator();
                    while (it.hasNext()){
                        HashMap<String, String> resultMap = new HashMap<>();
                        Map.Entry pair = (Map.Entry)it.next();
                        resultMap.put("1",pair.getKey().toString());
                        resultMap.put("2", pair.getValue().toString());
                        hashMapList.add(resultMap);
                    }
                    jobListView.setAdapter(userAdapters);

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        jobListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent s = new Intent(EmployerJobsActivity.this, EmployerJobDetailsActivity.class);
                s.putExtra("jobID", jobListView.getItemAtPosition(i).toString());
                startActivity(s);
            }
        });

    }

    //buttons click events
    @Override
    public void onClick(View v) {
        int id = v.getId();
        if(id == R.id.btnAddJob){
            saveJob(txtLookingFor.getText().toString(),txtRatePerDay.getText().toString(),txtJobDescription.getText().toString(),txtQualifications.getText().toString(),txtLocation.getText().toString());
        }
        else if(id == R.id.fabAddJob){
            AlertDialog.Builder mBuilder = new AlertDialog.Builder(EmployerJobsActivity.this);
            View mView = getLayoutInflater().inflate(R.layout.dialog_add_jobs,null);
            txtLookingFor = mView.findViewById(R.id.txtApplicantSkills);
            txtRatePerDay = mView.findViewById(R.id.txtRate);
            txtJobDescription = mView.findViewById(R.id.txtJobDescription);
            txtQualifications = mView.findViewById(R.id.txtQualification);
            txtLocation = mView.findViewById(R.id.txtLocation);
            mView.findViewById(R.id.btnAddJob).setOnClickListener(this);
            mBuilder.setView(mView);
            AlertDialog dialogs =  mBuilder.create();
            dialogs.show();
        }
    }

    public void saveJob(final String lookingfor, final String rate, final String jobdesc, final String qualifications, final String location){
        FirebaseUser user = mAuth.getCurrentUser();
        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        Date date = new Date();
        Job job = new Job(lookingfor, rate, jobdesc, qualifications, location, dateFormat.format(date), jobID, user.getUid());
        mDatabase.child("jobs").child(jobID.toString()).setValue(job).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(EmployerJobsActivity.this, "Successfully Created a Job.",
                        Toast.LENGTH_SHORT).show();
                txtLookingFor.setText("");
                txtRatePerDay.setText("");
                txtJobDescription.setText("");
                txtQualifications.setText("");
                txtLocation.setText("");
            }
        }).addOnFailureListener(new OnFailureListener(){
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(EmployerJobsActivity.this, "Failed to Create a Job.",
                            Toast.LENGTH_SHORT).show();
                }
            });
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
}
