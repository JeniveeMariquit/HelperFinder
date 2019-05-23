package mobapp.dntsjay.helperfinder;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class EmployerHomeActivity extends AppCompatActivity {
    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;
    private TextView txtUserName;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_employer_home);
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        txtUserName = findViewById(R.id.txtUserName);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    }


    //buttons click events
    public  void gotoMyJobs(View v){
        Intent i = new Intent(getApplicationContext(), EmployerJobsActivity.class);
        startActivity(i);
    }

    public  void gotoMyApplicants(View v){
        Intent i = new Intent(getApplicationContext(), EmployersApplicantActivity.class);
        startActivity(i);
    }

    public  void gotoMyMessages(View v){
        Intent i = new Intent(getApplicationContext(), EmployerMessageActivity.class);
        startActivity(i);
    }

    public  void gotoMyNearbyApplicants(View v){
            Intent i = new Intent(getApplicationContext(), EmployersLocationActivity.class);
            startActivity(i);
    }

    public  void gotoMyEmployeeRating(View v){
        Intent i = new Intent(getApplicationContext(), EmployerRatingActivity.class);
        startActivity(i);
    }

    public  void gotoMyAccountSettings(View v){
        Intent i = new Intent(getApplicationContext(), AccountSettingsActivity.class);
        startActivity(i);
    }

    public  void signOut(View v){
        FirebaseAuth.getInstance().signOut();
        updateUI(null);
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
                    if(userType.equals("EMPLOYER")){
                        txtUserName.setText("You are logged in as - "+Lastname+", "+Firstname);
                    }else if(userType.equals("APPLICANT")){
                        Intent intentMain = new Intent(EmployerHomeActivity.this, ApplicantHomeActivity.class);
                        intentMain.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intentMain);
                        finish();
                    }else if(userType.equals("ADMIN")){
//                        Intent intentMain = new Intent(LoginActivity.this, PoliceActivity.class);
//                        intentMain.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//                        startActivity(intentMain);
//                        finish();
                    }else{
                        Toast.makeText(EmployerHomeActivity.this, "Failed Login. Please Try Again", Toast.LENGTH_SHORT).show();
                        return;
                    }
                }
                @Override
                public void onCancelled(DatabaseError databaseError) {
                }
            });
        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.employer_menu, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.logout) {
            FirebaseAuth.getInstance().signOut();
            updateUI(null);
        }

        return super.onOptionsItemSelected(item);
    }

}
