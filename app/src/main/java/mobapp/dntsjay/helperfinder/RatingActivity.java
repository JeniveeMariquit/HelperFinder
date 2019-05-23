package mobapp.dntsjay.helperfinder;

import android.app.AlertDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class RatingActivity extends AppCompatActivity implements View.OnClickListener{

    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;

    private Spinner spinnerRating;
    private TextView txtNameofApplicant;

    public Double numberOfRating;
    public Double rating;
    public Double total_rating;

    public String applicantUID;

    private static final Integer[] paths = {1,2,3,4,5};
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rating);
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        txtNameofApplicant = findViewById(R.id.txtNameOfApplicant);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_24dp);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(), EmployerRatingActivity.class);
                startActivity(i);
            }
        });

        spinnerRating = findViewById(R.id.txtApplicantRating);
        findViewById(R.id.btnSaveRating).setOnClickListener(this);

        ArrayAdapter<Integer> adapter = new ArrayAdapter<Integer>(RatingActivity.this,
                android.R.layout.simple_spinner_item,paths);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerRating.setAdapter(adapter);

        Bundle bundle = getIntent().getExtras();
        if(bundle != null){
            String text = bundle.getString("applicantID");
            String []splitArray=text.split("=");
            String new_text=splitArray[3];
            final String applicantID = new_text.substring(0, new_text.length() - 1);
            applicantUID = applicantID;
            mDatabase.child("users").child(applicantID).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    txtNameofApplicant.setText(dataSnapshot.child("Firstname").getValue().toString()+" "+dataSnapshot.child("Middlename").getValue().toString()+" "+ dataSnapshot.child("Lastname").getValue().toString() );
                }
                @Override
                public void onCancelled(DatabaseError databaseError) {
                }
            });

            mDatabase.child("user_rating").child(applicantID).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    numberOfRating = Double.parseDouble(dataSnapshot.child("NumberOfRating").getValue().toString());
                    rating = Double.parseDouble(dataSnapshot.child("Rating").getValue().toString());
                    total_rating = Double.parseDouble(dataSnapshot.child("TotalRating").getValue().toString());
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if(id == R.id.btnSaveRating){
            saveRating(spinnerRating.getSelectedItem().toString());
        }
    }

    public void saveRating(final String newRating){
        Double numRating = numberOfRating + 1.00;
        Double ratings = rating +  Double.parseDouble(newRating);
        Double ratingTotal = ratings / numRating;
        User_Rating ur = new User_Rating(numRating,ratings,ratingTotal);
        mDatabase.child("user_rating").child(applicantUID).setValue(ur).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(getApplicationContext(),"SUCCESSFULLY RATED AN APPLICANT",Toast.LENGTH_SHORT).show();
                Intent s = new Intent(RatingActivity.this, EmployerRatingActivity.class);
                startActivity(s);
            }
        });
    }

    public static class User_Rating {
        public Double NumberOfRating;
        public Double Rating;
        public Double TotalRating;

        public User_Rating() {
            // Default constructor required for calls to DataSnapshot.getValue(User.class)
        }

        public User_Rating(Double numberOfRating, Double rating, Double totalRating) {
            this.NumberOfRating = numberOfRating;
            this.Rating = rating;
            this.TotalRating = totalRating;

        }
    }
}
