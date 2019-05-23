package mobapp.dntsjay.helperfinder;

import android.app.AlertDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener{
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    private EditText txtUsername;
    private EditText txtPassword;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        txtUsername = findViewById(R.id.txtUsername);
        txtPassword = findViewById(R.id.txtPassword);


    }

    //These are the buttons onClick
    public void login(View v){
        signIn(txtUsername.getText().toString(), txtPassword.getText().toString());
    }

    public void gotoRegistration(View v){
        Intent i = new Intent(getApplicationContext(), RegistrationActivity.class);
        startActivity(i);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if(id == R.id.btnSendEmailVerification){
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

            user.sendEmailVerification()
            .addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        // email sent
                    Toast.makeText(getApplicationContext(), "Successfully sent a verification to your email address!\nPlease check your email account.\nThank You!",Toast.LENGTH_LONG).show();

                        // after email is sent just logout the user and finish this activity
                        FirebaseAuth.getInstance().signOut();
                        Intent s = new Intent(LoginActivity.this, LoginActivity.class);
                        startActivity(s);
                        finish();
                    }
                    else
                    {
                        // email not sent, so display message and restart the activity or do whatever you wish to do

                        //restart this activity
                        overridePendingTransition(0, 0);
                        finish();
                        overridePendingTransition(0, 0);
                        startActivity(getIntent());

                    }
                }
            });
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        updateUI(currentUser);
    }

    @Override
    public void onBackPressed() {
//        super.onBackPressed();
//        this.finish();
        finishAffinity();
    }

    private void updateUI(FirebaseUser user) {
        if (user != null) {
            if(user.isEmailVerified()){
                mDatabase.child("users").child(user.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        String userType = dataSnapshot.child("Usertype").getValue().toString();
                        if(userType.equals("EMPLOYER")){
                            Intent intentResident = new Intent(getApplicationContext(), EmployerHomeActivity.class);
//                        intentResident.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intentResident);
                            finish();
                        }else if(userType.equals("APPLICANT")){
                            Intent intentMain = new Intent(LoginActivity.this, ApplicantHomeActivity.class);
                            intentMain.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intentMain);
                            finish();
                        }else if(userType.equals("ADMIN")){
//                        Intent intentMain = new Intent(LoginActivity.this, PoliceActivity.class);
//                        intentMain.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//                        startActivity(intentMain);
//                        finish();
                        }else{
                            Toast.makeText(LoginActivity.this, "Failed Login. Please Try Again", Toast.LENGTH_SHORT).show();
                            return;
                        }
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }
                });
            }
            else{
                AlertDialog.Builder mBuilder = new AlertDialog.Builder(LoginActivity.this);
                View mView = getLayoutInflater().inflate(R.layout.dialog_verify_email,null);
                mView.findViewById(R.id.btnSendEmailVerification).setOnClickListener(this);
                mBuilder.setView(mView);
                AlertDialog dialogs =  mBuilder.create();
                dialogs.show();
            }
        } else {

        }
    }

    private void signIn(String email, String password) {
        Log.d("USER", "signIn:" + email);
        if (!validateForm()) {
            return;
        }

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d("USER", "signInWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w("USER", "signInWithEmail:failure", task.getException());
                            Toast.makeText(LoginActivity.this, "Authentication failed:\n"+task.getException().getMessage(),
                                    Toast.LENGTH_SHORT).show();
                            updateUI(null);
                        }


                        if (!task.isSuccessful()) {
                        }

                    }
                });
    }

    private boolean validateForm() {
        boolean valid = true;
        String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
        String email = txtUsername.getText().toString();
        if (TextUtils.isEmpty(email)) {
            txtUsername.setError("Required.");
            valid = false;
        } else {
            txtUsername.setError(null);
        }
        if (!email.matches(emailPattern)) {
            txtUsername.setError("Please input a valid email address.");
            valid = false;
        } else {
            txtUsername.setError(null);
        }

        String password = txtPassword.getText().toString();
        if (TextUtils.isEmpty(password)) {
            txtPassword.setError("Password is required.");
            valid = false;
        } else {
            txtPassword.setError(null);
        }

        return valid;
    }
}
