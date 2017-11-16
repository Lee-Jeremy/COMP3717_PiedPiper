package ca.bcit.comp3717.guardian.controller;

import android.app.Dialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import ca.bcit.comp3717.guardian.R;
import ca.bcit.comp3717.guardian.api.HttpHandler;
import ca.bcit.comp3717.guardian.model.User;
import ca.bcit.comp3717.guardian.util.UserValidation;

public class LandingActivity extends AppCompatActivity {

    private String TAG = LandingActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_landing);
    }

    public void register(View view){
        // Create custom dialog object
        final Dialog dialog = new Dialog(LandingActivity.this);
        // Include dialog.xml file
        dialog.setContentView(R.layout.register_layout);
        // Set dialog title
        dialog.setTitle("Register");

        dialog.show();

        Button register = (Button) dialog.findViewById(R.id.button_dialog_register_register);
        register.setOnClickListener(new Button.OnClickListener() {

            @Override
            public void onClick(View v) {
                registerRequest(dialog);
            }
        });
    }

    private void registerRequest(Dialog d) {
        EditText userName = (EditText) d.findViewById(R.id.editText_dialog_register_userName);
        EditText email = (EditText) d.findViewById(R.id.editText_dialog_register_email);
        EditText phone = (EditText) d.findViewById(R.id.editText_dialog_register_phone);
        EditText password = (EditText) d.findViewById(R.id.editText_dialog_register_password);

        if (UserValidation.validateInputs(userName, email, phone, password)) {
            new RegisterUserTask(userName.getText().toString(), password.getText().toString(),
                    email.getText().toString(), phone.getText().toString()).execute();
        }
    }

    private void registerResponse(User user) {
        if (user != null) {
            goToMainActivity(user);
        } else {
            Log.e(TAG, "That username is already taken.");
        }
    }

    public void loginRequest(View v) {
        EditText userName = (EditText) findViewById(R.id.editText_landingActivity_email);
        EditText password = (EditText) findViewById(R.id.editText_landingActivity_password);

        if (UserValidation.validateInputs(userName, password)) {
            new LoginUserTask(userName.getText().toString(), password.getText().toString()).execute();
        }
    }

    private void loginResponse(User user) {
        if (user != null) {
            goToMainActivity(user);

        } else {
            Log.e(TAG, "Username or password is incorrect or account does not exist");
        }
    }

    private void goToMainActivity(User user) {
        Intent i = new Intent(this, MainActivity.class);
        i.putExtra("userName", user.getUserName());
        i.putExtra("email", user.getEmail());
        Toast.makeText(this.getBaseContext(), "Welcome " + user.getUserName(), Toast.LENGTH_SHORT).show();
        startActivity(i);
    }


    private class LoginUserTask extends AsyncTask<Void, Void, User> {
        private String password;
        private String email;

        public LoginUserTask(String email, String password) {
            this.email = email;
            this.password = password;
        }

        @Override
        protected User doInBackground(Void... voidArgs) {
            return HttpHandler.getUser(this.email, this.password);
        }

        @Override
        protected void onPostExecute(User user) {
            super.onPostExecute(user);
            Log.d("API Response", user.toString());
            loginResponse(user);
        }
    }

    private class RegisterUserTask extends AsyncTask<Void, Void, User> {
        private String userName;
        private String password;
        private String email;
        private String phone;

        public RegisterUserTask(String userName, String password, String email, String phone) {
            this.userName = userName;
            this.password = password;
            this.email = email;
            this.phone = phone;
        }

        @Override
        protected User doInBackground(Void... voidArgs) {
            return HttpHandler.createUser(this.userName, this.password, this.email, this.phone);
        }

        @Override
        protected void onPostExecute(User user) {
            super.onPostExecute(user);
            Log.d("API Response", user.toString());
            registerResponse(user);
        }
    }
}
