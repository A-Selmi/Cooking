package com.abdullah.cooking;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import java.util.HashMap;
import java.util.Map;

public class AddAndEditActivity extends AppCompatActivity {

    ProgressBar saveOrCancleProgressBar;
    TextView addOrEditTextView, cookingNameTextView , quantityTextView, productionTextView;
    Button saveButton, cancleButton;
    EditText cookingNameEditText, quantityEditText, productionEditText;
    FirebaseFirestore db;
    String addOrEditState, quantity , production, id , name;
    boolean check;
    Cooking cooking;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_and_edit);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Clickable(true);
        CloseKeyboard();
        findingViews();
        if(addOrEditState.equals("Add")) {
            addOrEditTextView.setText("إضافة");
        }else {
            addOrEditTextView.setText("تعديل");
            cooking = (Cooking) getIntent().getExtras().get("cooking");
            quantity = cooking.getQuantity();
            production = cooking.getProduction();
            id = cooking.getGeneratedId();
            name = cooking.getName();
            cookingNameEditText.setText(name);
            quantityEditText.setText(quantity);
            productionEditText.setText(production);
        }
        saveOrCancleProgressBar.setVisibility(View.GONE);
    }

    private void Add() {
        check = true;
        if(Connected()) {
            saveOrCancleProgressBar.setVisibility(View.GONE);
            if (cookingNameEditText.getText().toString().trim().isEmpty()) {
                cookingNameEditText.setError("يرجى تعبئة هذه الخانة");
                cookingNameEditText.requestFocus();
            }else {
                db.collection("Cooking List")
                        .get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    if (document.getData().get("name").equals(cookingNameEditText.getText().toString().trim())) {
                                        cookingNameEditText.setError("هذا الإسم مستخدم, يرجى إدخال إسم آخر");
                                        cookingNameEditText.requestFocus();
                                        check = false;
                                    }
                                }
                                if (check) {
                                    saveOrCancleProgressBar.setVisibility(View.VISIBLE);
                                    Clickable(false);
                                    Map<String, Object> cooking = new HashMap<>();
                                    cooking.put("name", cookingNameEditText.getText().toString().trim());
                                    cooking.put("quantity", quantityEditText.getText().toString().trim());
                                    cooking.put("production", productionEditText.getText().toString().trim());
                                    db.collection("Cooking List")
                                            .add(cooking)
                                            .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                                @Override
                                                public void onSuccess(DocumentReference documentReference) {
                                                    Toast.makeText(AddAndEditActivity.this, "تمت الإضافة بنجاح", Toast.LENGTH_SHORT).show();
                                                    saveOrCancleProgressBar.setVisibility(View.GONE);
                                                    finish();
                                                }
                                            })
                                            .addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    Toast.makeText(AddAndEditActivity.this, "فشل في إضافة البيانات", Toast.LENGTH_SHORT).show();
                                                    saveOrCancleProgressBar.setVisibility(View.GONE);
                                                    finish();
                                                }
                                            });
                                }
                            }
                        });
            }
        }else {
            Toast.makeText(this, "يرجى التحقق من الإتصال بالإنترنت", Toast.LENGTH_SHORT).show();
        }
    }

    private void findingViews() {
        addOrEditTextView = findViewById(R.id.addOrEditTextView);
        cookingNameTextView = findViewById(R.id.cookingNameTextView);
        cookingNameEditText = findViewById(R.id.cookingNameEditText);
        quantityTextView = findViewById(R.id.quantityTextView);
        quantityEditText = findViewById(R.id.quantityEditText);
        productionTextView = findViewById(R.id.productionTextView);
        productionEditText = findViewById(R.id.productionEditText);
        saveButton = findViewById(R.id.saveButton);
        cancleButton = findViewById(R.id.cancleButton);
        saveOrCancleProgressBar = findViewById(R.id.saveOrCancleProgressBar);
        db = FirebaseFirestore.getInstance();
        addOrEditState = getIntent().getStringExtra("state");
    }

    public void CloseKeyboard() {
        View view = this.getCurrentFocus();
        if(view != null) {
            InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            assert inputMethodManager != null;
            inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    private void Clickable(boolean b) {
        if(b) {
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        }else {
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        }
    }

    private boolean Connected() {
        ConnectivityManager manager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = manager.getActiveNetworkInfo();
        if (info != null && info.isConnected()) {
            return true;
        }
        return false;
    }

    public void SaveButtonClicked(View view) {
        CloseKeyboard();
        if(addOrEditState.equals("Add")) {
            Add();
        }else {
            Edit();
        }
    }

    private void Edit() {
        check = true;
        if(Connected()) {
            saveOrCancleProgressBar.setVisibility(View.GONE);
            if (cookingNameEditText.getText().toString().trim().isEmpty()) {
                cookingNameEditText.setError("يرجى تعبئة هذه الخانة");
                cookingNameEditText.requestFocus();
            }else {
                db.collection("Cooking List")
                        .get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    if(!document.getId().equals(cooking.getGeneratedId())) {
                                        if (document.getData().get("name").equals(cookingNameEditText.getText().toString().trim())) {
                                            cookingNameEditText.setError("هذا الإسم مستخدم, يرجى إدخال إسم آخر");
                                            cookingNameEditText.requestFocus();
                                            check = false;
                                        }
                                    }
                                }
                                if (check) {
                                    saveOrCancleProgressBar.setVisibility(View.VISIBLE);
                                    Clickable(false);
                                    db.collection("Cooking List")
                                            .document(id)
                                            .update("quantity", quantityEditText.getText().toString().trim(),
                                                    "production", productionEditText.getText().toString().trim())
                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {
                                                    Toast.makeText(AddAndEditActivity.this, "تم التعديل بنجاح", Toast.LENGTH_SHORT).show();
                                                    saveOrCancleProgressBar.setVisibility(View.GONE);
                                                    finish();
                                                }
                                            }).addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Toast.makeText(AddAndEditActivity.this, "حدث فشل في عملية التعديل", Toast.LENGTH_SHORT).show();
                                                saveOrCancleProgressBar.setVisibility(View.GONE);
                                                finish();
                                            }
                                            });
                                }
                            }
                        });
            }
        }else {
            Toast.makeText(this, "يرجى التحقق من الإتصال بالإنترنت", Toast.LENGTH_SHORT).show();
        }
    }

    public void CancleButtonClicked(View view) {
        finish();
    }
}
