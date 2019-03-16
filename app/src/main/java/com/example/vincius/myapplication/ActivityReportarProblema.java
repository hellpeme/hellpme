package com.example.vincius.myapplication;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

public class ActivityReportarProblema extends AppCompatActivity {
    private EditText txtReportarProblema;
    private Button btnReportarProblema;
    private FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    private String username;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reportar_problema);

        startComponents();
        fetchAtributeName();
        btnReportarProblema.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ReportarProblema();
            }
        });

    }

    private void fetchAtributeName() {
        FirebaseFirestore.getInstance().collection("users").document(user.getUid())
                .addSnapshotListener(new EventListener<DocumentSnapshot>() {
                    @Override
                    public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                        User user = documentSnapshot.toObject(User.class);
                        username = user.getUsername();
                    }
                });
    }

    private void startComponents(){
        txtReportarProblema = (EditText) findViewById(R.id.txtReportarProblema);
        btnReportarProblema = (Button) findViewById(R.id.btnReportarProblema);
    }

    private void ReportarProblema(){
        MessageReport rep = new MessageReport();

        String me = user.getUid();
        String reportarProblema = txtReportarProblema.getText().toString();

        if (!reportarProblema.isEmpty()) {
            rep.setMessagemReport(reportarProblema);
            rep.setUsername(username);

            FirebaseFirestore.getInstance().collection("reportarProblemas")
                    .document(me)
                    .set(rep).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    Log.i("teste", "report salvo com sucesso");
                    Toast.makeText(ActivityReportarProblema.this, "Reportado com sucesso", Toast.LENGTH_SHORT).show();
                }
            });
        }



    }


}
