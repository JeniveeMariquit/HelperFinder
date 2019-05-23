package mobapp.dntsjay.helperfinder;

import android.app.AlertDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.TextView;
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

public class EmployerRatingActivity extends AppCompatActivity implements View.OnClickListener{
    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;

    private Spinner spinnerRating;
    private TextView txtNameofApplicant;

    private ListView employersApplicantListView;
    private clsEmployersApplicant listOfEmployersApplicant;

    private static final Integer[] paths = {1,2,3,4,5};
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_employer_rating);
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        employersApplicantListView = findViewById(R.id.employersApplicantListView);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_24dp);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(), EmployerHomeActivity.class);
                startActivity(i);
            }
        });

        final HashMap<String,String > jobdescanddateandid = new HashMap<>();
        listOfEmployersApplicant = new clsEmployersApplicant();
        FirebaseUser user = mAuth.getCurrentUser();
        mDatabase.child("employers_applicant").orderByChild("JobOwnerID").equalTo(user.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot ds: dataSnapshot.getChildren()){
                    listOfEmployersApplicant = ds.getValue(clsEmployersApplicant.class);
                    List<HashMap<String, String>> hashMapList = new ArrayList<>();
                    SimpleAdapter userAdapters = new SimpleAdapter(getApplicationContext(),hashMapList, R.layout.employers_applicant_info, new String[]{"1","2"},
                            new int[]{R.id.txtDateApplied,R.id.txtApplicantID});
                    jobdescanddateandid.put(listOfEmployersApplicant.NameofApplicant,"ID="+listOfEmployersApplicant.ApplicantID);
                    Iterator it = jobdescanddateandid.entrySet().iterator();
                    while (it.hasNext()){
                        HashMap<String, String> resultMap = new HashMap<>();
                        Map.Entry pair = (Map.Entry)it.next();
                        resultMap.put("1",pair.getKey().toString());
                        resultMap.put("2", pair.getValue().toString());
                        hashMapList.add(resultMap);
                    }
                    employersApplicantListView.setAdapter(userAdapters);

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        employersApplicantListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent s = new Intent(EmployerRatingActivity.this, RatingActivity.class);
                s.putExtra("applicantID", employersApplicantListView.getItemAtPosition(i).toString());
                startActivity(s);
//                AlertDialog.Builder mBuilder = new AlertDialog.Builder(EmployerRatingActivity.this);
//                View mView = getLayoutInflater().inflate(R.layout.dialog_rate_employee,null);
//                txtNameofApplicant = mView.findViewById(R.id.txtNameOfApplicant);
//                spinnerRating = mView.findViewById(R.id.txtApplicantRating);
//
//                ArrayAdapter<Integer> adapter = new ArrayAdapter<Integer>(EmployerRatingActivity.this,
//                        android.R.layout.simple_spinner_item,paths);
//                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//                spinnerRating.setAdapter(adapter);
//                mBuilder.setView(mView);
//                AlertDialog dialogs =  mBuilder.create();
//
//                String text = employersApplicantListView.getItemAtPosition(i).toString();
//                String []splitArray=text.split("=");
//                String new_text=splitArray[3];
//                final String applicantID = new_text.substring(0, new_text.length() - 1);
//                mDatabase.child("users").child(applicantID).addListenerForSingleValueEvent(new ValueEventListener() {
//                    @Override
//                    public void onDataChange(DataSnapshot dataSnapshot) {
//                        txtNameofApplicant.setText(dataSnapshot.child("Firstname").getValue().toString()+" "+dataSnapshot.child("Middlename").getValue().toString()+" "+ dataSnapshot.child("Lastname").getValue().toString() );
//                    }
//                    @Override
//                    public void onCancelled(DatabaseError databaseError) {
//                    }
//                });
////                Toast.makeText(getApplicationContext(),applicantID,Toast.LENGTH_LONG).show();
//                dialogs.show();
            }
        });
    }

    @Override
    public void onClick(View v) {

    }
}
