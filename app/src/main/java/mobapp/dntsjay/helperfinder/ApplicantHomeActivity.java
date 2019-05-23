package mobapp.dntsjay.helperfinder;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
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

import static android.Manifest.permission.ACCESS_FINE_LOCATION;

public class ApplicantHomeActivity extends AppCompatActivity implements View.OnClickListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener,
        LocationListener
{
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    private ListView jobListView;
    private ArrayList<String> jobLists;
    private clsJobLists listOfJobs;
    private EditText txtSearch;

    public GoogleApiClient client;
    public LatLng mylocation;
    public FusedLocationProviderClient fclient;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_applicant_home);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        jobListView = findViewById(R.id.jobListView);
        txtSearch = findViewById(R.id.txtSeachJob);
        findViewById(R.id.btnBotNavMessage).setOnClickListener(this);
        txtSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(!s.toString().equals("")){
                    SimpleAdapter jobs = (SimpleAdapter)jobListView.getAdapter();
                    jobs.getFilter().filter(s);
                }
                else{
                    displayJobs();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        displayJobs();
        checkIfCompliedResume();
        requestPermission();
        if (client == null) {
            client = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
            client.connect();
        }
        if (client != null) {
            client.connect();
//            Toast.makeText(getApplicationContext(), "CONNECTED", Toast.LENGTH_LONG).show();
        }
        fclient = LocationServices.getFusedLocationProviderClient(this);
    }

    public void displayJobs(){
        final HashMap<String,String > jobdescanddateandid = new HashMap<>();
        listOfJobs = new clsJobLists();
        jobLists = new ArrayList<>();
        mDatabase.child("jobs").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot ds: dataSnapshot.getChildren()){
                    listOfJobs = ds.getValue(clsJobLists.class);
                    List<HashMap<String, String>> hashMapList = new ArrayList<>();
                    SimpleAdapter userAdapters = new SimpleAdapter(getApplicationContext(),hashMapList, R.layout.job_info, new String[]{"1","2"},
                            new int[]{R.id.txtJobDesc,R.id.txtDatePosted});
                    jobdescanddateandid.put(listOfJobs.LookingFor, listOfJobs.DatePosted+"\nID="+listOfJobs.JobID);
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
                Intent s = new Intent(ApplicantHomeActivity.this, JobDetailsActivity.class);
                s.putExtra("jobID", jobListView.getItemAtPosition(i).toString());
                startActivity(s);
            }
        });
    }

    public void checkIfCompliedResume(){
        FirebaseUser user = mAuth.getCurrentUser();
        mDatabase.child("users").child(user.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String withAdditional = dataSnapshot.child("WithAdditional").getValue().toString();
                    if(withAdditional == "false"){
                        Toast.makeText(ApplicantHomeActivity.this, "Please fill up all the required data for your resume..", Toast.LENGTH_LONG).show();
                        Intent s = new Intent(ApplicantHomeActivity.this,ApplicantResumeActivity.class);
                        finish();
                        startActivity(s);
                    }
                    else{

                    }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_logout) {
            FirebaseAuth.getInstance().signOut();
            updateUI(null);
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onStart(){
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        updateUI(currentUser);
    }

    private void updateUI(FirebaseUser user) {
        if (user == null) {
            Intent s = new Intent(this,LoginActivity.class);
            finish();
            startActivity(s);
        } else {
            mDatabase.child("users").child(user.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    String userType = dataSnapshot.child("Usertype").getValue().toString();
                    String Firstname = dataSnapshot.child("Firstname").getValue().toString();
                    String Middlename = dataSnapshot.child("Middlename").getValue().toString();
                    String Lastname = dataSnapshot.child("Lastname").getValue().toString();
//                    if(userType.equals("EMPLOYER")){
//                        txtUserName.setText("You are logged in as - "+Lastname+", "+Firstname);
//                    }else if(userType.equals("APPLICANT")){
////                        Intent intentMain = new Intent(LoginActivity.this, SecurityGuardActivity.class);
////                        intentMain.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
////                        startActivity(intentMain);
////                        finish();
//                    }else if(userType.equals("ADMIN")){
////                        Intent intentMain = new Intent(LoginActivity.this, PoliceActivity.class);
////                        intentMain.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
////                        startActivity(intentMain);
////                        finish();
//                    }else{
//                        Toast.makeText(EmployerHomeActivity.this, "Failed Login. Please Try Again", Toast.LENGTH_SHORT).show();
//                        return;
//                    }
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
        if(id == R.id.btnBotNavMessage){
            Intent s = new Intent(ApplicantHomeActivity.this, EmployerMessageActivity.class);
            s.putExtra("typeOfUser","APPLICANT");
            startActivity(s);
        }
    }
    private void requestPermission(){
        ActivityCompat.requestPermissions(this, new String [] {ACCESS_FINE_LOCATION}, 1);
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        fclient.getLastLocation().addOnSuccessListener(ApplicantHomeActivity.this, new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if(location != null){
//                    Toast.makeText(getApplicationContext(), location.toString(), Toast.LENGTH_LONG).show();
                    mylocation = new LatLng(location.getLatitude(), location.getLongitude());
                    insertUserLocation(String.valueOf(location.getLatitude()), String.valueOf(location.getLongitude()),mylocation.toString());
                }
                else{
                    Toast.makeText(getApplicationContext(), "PLEASE CHECK LOCATION SETTINGS", Toast.LENGTH_LONG).show();
                }

            }
        });
    }

    @Override
    public void onConnectionSuspended(int i) {

    }


    public void insertUserLocation(final String userLatitude, final String userLongitude, final String latLng){
        FirebaseUser user = mAuth.getCurrentUser();
        EmployersLocationActivity.UserLocation ul = new EmployersLocationActivity.UserLocation(userLatitude, userLongitude, latLng, user.getUid());
        mDatabase.child("user-location").child(user.getUid()).setValue(ul).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onLocationChanged(Location location) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        fclient.getLastLocation().addOnSuccessListener(ApplicantHomeActivity.this, new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if(location != null){
//                    Toast.makeText(getApplicationContext(), location.toString(), Toast.LENGTH_LONG).show();
                    mylocation = new LatLng(location.getLatitude(), location.getLongitude());
                    insertUserLocation(String.valueOf(location.getLatitude()), String.valueOf(location.getLongitude()),mylocation.toString());
                }
                else{
                    Toast.makeText(getApplicationContext(), "PLEASE CHECK LOCATION SETTINGS", Toast.LENGTH_LONG).show();
                }

            }
        });
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    public static class UserLocation{
        public String UserLatitude;
        public String UserLongitude;
        public String LatLng;
        public String Uid;

        public UserLocation(){

        }

        public UserLocation(String userLatitude, String userLongitude, String latLng, String uid){
            this.UserLatitude = userLatitude;
            this.UserLongitude = userLongitude;
            this.LatLng = latLng;
            this.Uid = uid;
        }
    }
}
