package com.example.vincius.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.LoggingBehavior;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Arrays;

public class ActivityLogin extends AppCompatActivity {

    private TextView textRegister, textForgotPass, textTitle;
    private EditText editEmail, editSenha;
    private Button btnlogin, btnLoginFacebook;
    private FirebaseAuth mAuth;
    private CallbackManager mCallbackManager;
    private ProgressBar loading;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        startComponents();
        verficarAuth();


        FacebookSdk.setIsDebugEnabled(true);
        FacebookSdk.addLoggingBehavior(LoggingBehavior.INCLUDE_ACCESS_TOKENS);

        mAuth = FirebaseAuth.getInstance();

        btnlogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loading.setVisibility(View.VISIBLE);
                String email = editEmail.getText().toString();
                String senha = editSenha.getText().toString();
                login(email,senha);
            }
        });

        textRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(ActivityLogin.this, ActivityCadastro.class);
                startActivity(i);
            }

        });

        textForgotPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(ActivityLogin.this, ActivityResetSenha.class);
                startActivity(i);
            }

        });
        mCallbackManager = CallbackManager.Factory.create();

        btnLoginFacebook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LoginManager.getInstance().logInWithReadPermissions(ActivityLogin.this, Arrays.asList("email", "public_profile"));
                LoginManager.getInstance().registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult) {
                        Log.d("teste", "facebook:onSuccess:" + loginResult);
                        handleFacebookAccessToken(loginResult.getAccessToken());
                    }

                    @Override
                    public void onCancel() {
                        Log.d("teste", "facebook:onCancel");
                        // ...
                    }

                    @Override
                    public void onError(FacebookException error) {
                        Log.d("teste", "facebook:onError", error);
                        // ...
                    }
                });
            }
        });

    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser!= null && currentUser.isEmailVerified()) {
            updateUI();
        }
    }

    private void updateUI() {
        alert("Você esta logado com o facebook");
        Intent intent = new Intent(this, ActivityFragmentsNavigation.class);
        startActivity(intent);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Pass the activity result back to the Facebook SDK
        mCallbackManager.onActivityResult(requestCode, resultCode, data);
    }

    private void handleFacebookAccessToken(AccessToken token) {
        Log.d("teste", "handleFacebookAccessToken:" + token);

        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d("teste", "signInWithCredential:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            alert("Authentication Success");

                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w("teste", "signInWithCredential:failure", task.getException());
                            Toast.makeText(ActivityLogin.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                            updateUI();

                        }

                        // ...
                    }
                });
    }

        private void login(String email, String senha) {
        if (email == null || email.isEmpty() || senha == null || senha.isEmpty()) {
            alert("Login e senha devem ser preechidos");
            return;
        }

        FirebaseAuth.getInstance().signInWithEmailAndPassword(email, senha)
               .addOnCompleteListener(ActivityLogin.this, new OnCompleteListener<AuthResult>() {
                   @Override
                   public void onComplete(@NonNull Task<AuthResult> task) {
                       loading.setVisibility(View.GONE);
                       if(task.isSuccessful()){
                           if(FirebaseAuth.getInstance().getCurrentUser().isEmailVerified()) {
                               Intent i = new Intent(ActivityLogin.this, ActivityFragmentsNavigation.class);
                               i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                               startActivity(i);
                               alert("Login realizado com sucesso!");
                           }
                           else {
                               alert("Porfavor verifique seu email");
                           }
                       }else{
                           alert("Email e senha não coincidem");
                       }
                   }
               });
    }

    private void verficarAuth() {

        if (FirebaseAuth.getInstance().getUid() != null) {
            if(FirebaseAuth.getInstance().getCurrentUser().isEmailVerified()) {
                Intent intent = new Intent(ActivityLogin.this, ActivityFragmentsNavigation.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        }
    }


    private void updateUI(FirebaseUser currentUser) {
        if (currentUser != null) {
            String username = ( currentUser.getDisplayName());
            String uid = (currentUser.getUid());

        }
    }

    private void alert(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    private void startComponents() {
            loading = findViewById(R.id.loadingLogin);
            textTitle = findViewById(R.id.txtTitle);
            editEmail = findViewById(R.id.viewEditEmail);
            editSenha = findViewById(R.id.viewEditSenha);
            btnlogin = findViewById(R.id.btlogin);
            btnLoginFacebook = findViewById(R.id.btnLoginFacebook);
            textRegister = findViewById(R.id.textRegister);
            textForgotPass = findViewById(R.id.textForgotPass);
    }
}
