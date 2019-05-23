package mobapp.dntsjay.helperfinder;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class ApplicantResumeActivity extends AppCompatActivity implements View.OnClickListener{
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    private EditText txtSkills;
    private EditText txtEducAttainment;
    private EditText txtWorkExperience;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_applicant_resume);
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        txtSkills = findViewById(R.id.txtApplicantSkills);
        txtEducAttainment = findViewById(R.id.txtEducAttainment);
        txtWorkExperience = findViewById(R.id.txtWorkExperience);
        findViewById(R.id.btnSaveAdditional).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if(id == R.id.btnSaveAdditional){
            saveAdditionalInfo(txtSkills.getText().toString(),txtEducAttainment.getText().toString(),txtWorkExperience.getText().toString());
        }
    }

    public static class WithAdditional {
        public Boolean WithAdditional;

        public WithAdditional() {
            // Default constructor required for calls to DataSnapshot.getValue(User.class)
        }

        public WithAdditional(Boolean withAdditional) {
            this.WithAdditional = withAdditional;

        }
    }

    public static class AdditionalInfo {
        public String Skills;
        public String EducationalAttainment;
        public String WorkExperience;

        public AdditionalInfo() {
            // Default constructor required for calls to DataSnapshot.getValue(User.class)
        }

        public AdditionalInfo(String skills, String educationalAttainment, String workExperience) {
            this.Skills = skills;
            this.EducationalAttainment = educationalAttainment;
            this.WorkExperience = workExperience;
        }
    }

    public void saveAdditionalInfo(final String skills, final String educ, final String exper){
        FirebaseUser user = mAuth.getCurrentUser();
        WithAdditional withAdditional = new WithAdditional(true);
        mDatabase.child("users").child(user.getUid()).child("WithAdditional").setValue(withAdditional).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                AdditionalInfo ai  = new AdditionalInfo(skills,educ,exper);
                FirebaseUser user = mAuth.getCurrentUser();
                mDatabase.child("users").child(user.getUid()).child("AdditionalInfo").setValue(ai).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(ApplicantResumeActivity.this, "Successfully Created a Resume.",
                                Toast.LENGTH_SHORT).show();
                        Intent s = new Intent(ApplicantResumeActivity.this, ApplicantHomeActivity.class);
                        finish();
                        startActivity(s);
                    }
                })
                .addOnFailureListener(new OnFailureListener(){
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(ApplicantResumeActivity.this, "Failed to Create a Resume.",
                                Toast.LENGTH_SHORT).show();
                    }
                });

            }
        })
        .addOnFailureListener(new OnFailureListener(){
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(ApplicantResumeActivity.this, "Failed to Create a Resume.",
                        Toast.LENGTH_SHORT).show();
            }
        });
    }
}
