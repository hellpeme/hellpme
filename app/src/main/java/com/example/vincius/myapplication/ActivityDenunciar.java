package com.example.vincius.myapplication;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class ActivityDenunciar extends AppCompatActivity {

    private EditText editDenuncia;
    private RadioButton btnSpam, btnDesrespeito, btnAssedio, btnImproprio, btnOutro;
    private RadioGroup btnGDenuncia;
    private Button btnEnviarDenuncia;
    private FirebaseUser me;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_denunciar);
        starComponents();

        me = FirebaseAuth.getInstance().getCurrentUser();

        switch (btnGDenuncia.getCheckedRadioButtonId()){
            default:
                    editDenuncia.setVisibility(View.VISIBLE);
                btnEnviarDenuncia.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Denunciar();
                    }
                });
        }





    }

    private void starComponents(){
        editDenuncia = (EditText) findViewById(R.id.editDenuncia);
        btnSpam = (RadioButton) findViewById(R.id.btnSpam);
        btnDesrespeito = (RadioButton) findViewById(R.id.btnDesrespeito);
        btnAssedio = (RadioButton) findViewById(R.id.btnAssedio);
        btnImproprio = (RadioButton) findViewById(R.id.btnImproprio);
        btnOutro = (RadioButton) findViewById(R.id.btnOutro);
        btnGDenuncia = (RadioGroup) findViewById(R.id.btnGDenuncia);
        btnEnviarDenuncia = (Button)findViewById(R.id.btnEnviarDenuncia);

    }

    private void Denunciar(){
        switch (btnGDenuncia.getCheckedRadioButtonId()){
            case R.id.btnSpam:
                MessageDenuncia dn = new MessageDenuncia();

                User userDenunciado = getIntent().getExtras().getParcelable("user");
                String denuncia = editDenuncia.getText().toString();

                    dn.setMessagemDenuncia(denuncia);
                    dn.setUserDenunciado(userDenunciado.getUid());
                    dn.setUsername(me.getDisplayName());
                    FirebaseFirestore.getInstance().collection("denuncias")
                            .document("spam")
                            .collection(me.getUid())
                            .add(dn)
                            .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                @Override
                                public void onSuccess(DocumentReference documentReference) {
                                    Log.i("teste", "denúnicia salva com sucesso");
                                    Toast.makeText(ActivityDenunciar.this, "Denúncia feita com sucesso", Toast.LENGTH_SHORT).show();
                                }
                            });
                    break;

            case R.id.btnDesrespeito:
                MessageDenuncia dn2 = new MessageDenuncia();

                String userDenunciado2 = getIntent().getExtras().getParcelable("user");
                String denuncia2 = editDenuncia.getText().toString();

                dn2.setMessagemDenuncia(denuncia2);
                dn2.setUserDenunciado(userDenunciado2);
                dn2.setUsername(me.getDisplayName());
                FirebaseFirestore.getInstance().collection("denuncias")
                        .document("desrepeito")
                        .collection(me.getUid())
                        .add(dn2)
                        .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                            @Override
                            public void onSuccess(DocumentReference documentReference) {
                                Log.i("teste", "denúnicia salva com sucesso");
                                Toast.makeText(ActivityDenunciar.this, "Denúncia feita com sucesso", Toast.LENGTH_SHORT).show();
                            }
                });
                break;

            case R.id.btnAssedio:
                MessageDenuncia dn3 = new MessageDenuncia();

                String userDenunciado3 = getIntent().getExtras().getParcelable("user");
                String denuncia3 = editDenuncia.getText().toString();

                dn3.setMessagemDenuncia(denuncia3);
                dn3.setUserDenunciado(userDenunciado3);
                dn3.setUsername(me.getDisplayName());
                FirebaseFirestore.getInstance().collection("denuncias")
                        .document("assedio")
                        .collection(me.getUid())
                        .add(dn3)
                        .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                            @Override
                            public void onSuccess(DocumentReference documentReference) {
                                Log.i("teste", "denúnicia salva com sucesso");
                                Toast.makeText(ActivityDenunciar.this, "Denúncia feita com sucesso", Toast.LENGTH_SHORT).show();
                            }
                });
                break;
            case R.id.btnImproprio:
                MessageDenuncia dn4 = new MessageDenuncia();

                String userDenunciado4 = getIntent().getExtras().getParcelable("user");
                String denuncia4 = editDenuncia.getText().toString();

                dn4.setMessagemDenuncia(denuncia4);
                dn4.setUserDenunciado(userDenunciado4);
                dn4.setUsername(me.getDisplayName());
                FirebaseFirestore.getInstance().collection("denuncias")
                        .document("improprio")
                        .collection(me.getUid())
                        .add(dn4)
                        .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                            @Override
                            public void onSuccess(DocumentReference documentReference) {
                                Log.i("teste", "denúnicia salva com sucesso");
                                Toast.makeText(ActivityDenunciar.this, "Denúncia feita com sucesso", Toast.LENGTH_SHORT).show();
                            }
                });
                break;
            case R.id.btnOutro:
                MessageDenuncia dn5 = new MessageDenuncia();

                String userDenunciado5 = getIntent().getExtras().getParcelable("user");
                String denuncia5 = editDenuncia.getText().toString();

                dn5.setMessagemDenuncia(denuncia5);
                dn5.setUserDenunciado(userDenunciado5);
                dn5.setUsername(me.getDisplayName());
                FirebaseFirestore.getInstance().collection("denuncias")
                        .document("Outro")
                        .collection(me.getUid())
                        .add(dn5)
                        .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                            @Override
                            public void onSuccess(DocumentReference documentReference) {
                                Log.i("teste", "denúnicia salva com sucesso");
                                Toast.makeText(ActivityDenunciar.this, "Denúncia feita com sucesso", Toast.LENGTH_SHORT).show();
                            }
                });
                break;
        }

    }


}
