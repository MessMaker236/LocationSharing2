package com.example.bm121.locationsharing;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.bm121.locationsharing.Models.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

/**
 * Created by bm121 on 7/12/2016.
 */
public class CreateAccount  extends BaseActivity implements View.OnClickListener{

    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;

    private static final String TAG = "CreateAccountActivity";

    private EditText mUsernameField;
    private EditText mCreatePasswordField;
    private EditText mPasswordConfirmField;
    private EditText mCreateEmailField;
    private Button mCreateButton;
    private TextView mCancelButton;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_account);

        mDatabase = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();

        // Views
        mUsernameField = (EditText) findViewById(R.id.field_username);
        mCreateEmailField = (EditText) findViewById(R.id.field_create_email);
        mCreatePasswordField = (EditText) findViewById(R.id.field_create_password);
        mPasswordConfirmField = (EditText) findViewById(R.id.field_confirm_password);
        mCreateButton = (Button) findViewById(R.id.button_create);
        mCancelButton = (TextView) findViewById(R.id.button_cancel);

        //put click listeners on the buttons
        mCreateButton.setOnClickListener(this);
        mCancelButton.setOnClickListener(this);
    }
    public void onClick(View v){
        switch (v.getId()) {
            case R.id.button_create:
                create();
                break;
            case R.id.button_cancel:
                //go back to sign in
                startActivity(new Intent(CreateAccount.this, SignInActivity.class));
                finish();
        }
    }
    public void create (){
        if (!validateForm()) {
            return;
        }
        showProgressDialog();

        //read the form
        String email = mCreateEmailField.getText().toString();
        String password = mCreatePasswordField.getText().toString();

        //create a firebase user
        mAuth.createUserWithEmailAndPassword(email, password)
                //add  progressDialog until the account is created
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d(TAG, "createUser:onComplete:" + task.isSuccessful());
                        hideProgressDialog();

                        if (task.isSuccessful()) {
                            onAuthSuccess(task.getResult().getUser());
                        } else {
                            Toast.makeText(CreateAccount.this, "Sign Up Failed",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
    // check the form to see if everything fits
    private boolean validateForm() {
        boolean result = true;

        //Check if email is empty
        if (TextUtils.isEmpty(mCreateEmailField.getText().toString())) {
            mCreateEmailField.setError("Required");
            result = false;
        } else {
            mCreateEmailField.setError(null);
        }
        //check if password 1 is empty
        if (TextUtils.isEmpty(mCreatePasswordField.getText().toString())) {
            mCreatePasswordField.setError("Required");
            result = false;
        }
        //check if password 2 is empty
        if (TextUtils.isEmpty(mPasswordConfirmField.getText().toString())){
            mPasswordConfirmField.setError("Required");
            result = false;
        }
        if(result) {
            //check if password > 6 characters
            if (mCreatePasswordField.getText().toString().length() < 6) {
                mCreatePasswordField.setError("Password Must be at least 6 characters");
                result = false;
            }
            else {
                //check if passwords match
                if (!mCreatePasswordField.getText().toString().equals(mPasswordConfirmField.getText().toString())){
                    mPasswordConfirmField.setError("Passwords do not match");
                    result = false;
                }
                else {
                    mCreatePasswordField.setError(null);
                }
            }
        }
        return result;
    }
    private void onAuthSuccess(FirebaseUser user) {
        String username;
        //check if user supplied a username
        if (TextUtils.isEmpty(mUsernameField.getText().toString())){
            username = usernameFromEmail(user.getEmail());
        }
        else{
            username = mUsernameField.getText().toString();
        }
        // Write new user to database
        writeNewUser(user.getUid(), username, user.getEmail());
        // Go to MainActivity
        startActivity(new Intent(CreateAccount.this, MapsActivity.class));
    }
    //chop first part of email into a username
    private String usernameFromEmail(String email) {
        if (email.contains("@")) {
            return email.split("@")[0];
        } else {
            return email;
        }
    }
    //write to firebase database
    private void writeNewUser(String userId, String name, String email) {
        User user = new User(name, email);

        mDatabase.child("users").child(userId).setValue(user);
    }
}


