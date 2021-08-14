package com.abdullah.cooking;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class CookingDetailsActivity extends AppCompatActivity {

    ProgressBar cookingDetailsProgressBar;
    TextView cookingDetailsNameTextView, quantityTextViewInCookingDetailsActivity,
            quantityDetailsTextView, productionTextViewInCookingDetailsActivity,
            productionDetailsTextView, cookingDetailsFailTextView;
    Button editButton, deleteButton;
    FirebaseFirestore db;
    Cooking cooking;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cooking_details);
    }

    @Override
    protected void onResume() {
        super.onResume();
        cooking = (Cooking) getIntent().getExtras().get("cooking");
        Clickable(true);
        findingViews();
        Gone();
        Load();
    }

    private void Gone() {
        cookingDetailsNameTextView.setVisibility(View.GONE);
        quantityTextViewInCookingDetailsActivity.setVisibility(View.GONE);
        quantityDetailsTextView.setVisibility(View.GONE);
        productionTextViewInCookingDetailsActivity.setVisibility(View.GONE);
        productionDetailsTextView.setVisibility(View.GONE);
        editButton.setVisibility(View.GONE);
        deleteButton.setVisibility(View.GONE);
        cookingDetailsFailTextView.setVisibility(View.GONE);

    }

    private void findingViews() {
        cookingDetailsNameTextView = findViewById(R.id.cookingDetailsNameTextView);
        quantityTextViewInCookingDetailsActivity = findViewById(R.id.quantityTextViewInCookingDetailsActivity);
        quantityDetailsTextView = findViewById(R.id.quantityDetailsTextView);
        productionTextViewInCookingDetailsActivity = findViewById(R.id.productionTextViewInCookingDetailsActivity);
        productionDetailsTextView = findViewById(R.id.productionDetailsTextView);
        editButton = findViewById(R.id.editButton);
        deleteButton = findViewById(R.id.deleteButton);
        cookingDetailsProgressBar = findViewById(R.id.cookingDetailsProgressBar);
        cookingDetailsFailTextView = findViewById(R.id.cookingDetailsFailTextView);
        db = FirebaseFirestore.getInstance();
    }

    private void Load() {
        cookingDetailsProgressBar.setVisibility(View.VISIBLE);
        if (Connected()) {
            db.collection("Cooking List")
                    .document(cooking.getGeneratedId())
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if (task.isSuccessful()) {
                                cookingDetailsFailTextView.setVisibility(View.GONE);
                                cookingDetailsFailTextView.setText("");
                                cookingDetailsProgressBar.setVisibility(View.GONE);
                                cookingDetailsNameTextView.setVisibility(View.VISIBLE);
                                quantityTextViewInCookingDetailsActivity.setVisibility(View.VISIBLE);
                                quantityDetailsTextView.setVisibility(View.VISIBLE);
                                productionTextViewInCookingDetailsActivity.setVisibility(View.VISIBLE);
                                productionDetailsTextView.setVisibility(View.VISIBLE);
                                editButton.setVisibility(View.VISIBLE);
                                deleteButton.setVisibility(View.VISIBLE);
                                cookingDetailsNameTextView.setText(task.getResult().getData().get("name").toString());
                                cooking.setName(task.getResult().getData().get("name").toString());
                                if (task.getResult().getData().get("quantity").toString().isEmpty()) {
                                    quantityDetailsTextView.setText("لا توجد مقادير");
                                    cooking.setQuantity("");
                                } else {
                                    quantityDetailsTextView.setText(task.getResult().getData().get("quantity").toString());
                                    cooking.setQuantity(quantityDetailsTextView.getText().toString());
                                }
                                if (task.getResult().getData().get("production").toString().isEmpty()) {
                                    productionDetailsTextView.setText("لا توجد طريقة تحضير");
                                    cooking.setProduction("");
                                } else {
                                    productionDetailsTextView.setText(task.getResult().getData().get("production").toString());
                                    cooking.setProduction(productionDetailsTextView.getText().toString());
                                }
                            } else {
                                cookingDetailsFailTextView.setVisibility(View.VISIBLE);
                                cookingDetailsFailTextView.setText("فشل في تحمبل البيانات");
                                cookingDetailsProgressBar.setVisibility(View.GONE);
                                cookingDetailsNameTextView.setVisibility(View.VISIBLE);
                                editButton.setVisibility(View.VISIBLE);
                                deleteButton.setVisibility(View.VISIBLE);
                                quantityTextViewInCookingDetailsActivity.setVisibility(View.GONE);
                                quantityDetailsTextView.setVisibility(View.GONE);
                                productionTextViewInCookingDetailsActivity.setVisibility(View.GONE);
                                productionDetailsTextView.setVisibility(View.GONE);
                            }
                        }
                    });
        } else {
            cookingDetailsFailTextView.setVisibility(View.VISIBLE);
            cookingDetailsFailTextView.setText("يرجى التحقق من الإتصال بالإنترنت");
            cookingDetailsProgressBar.setVisibility(View.GONE);
            cookingDetailsNameTextView.setVisibility(View.VISIBLE);
            editButton.setVisibility(View.VISIBLE);
            deleteButton.setVisibility(View.VISIBLE);
            quantityTextViewInCookingDetailsActivity.setVisibility(View.GONE);
            quantityDetailsTextView.setVisibility(View.GONE);
            productionTextViewInCookingDetailsActivity.setVisibility(View.GONE);
            productionDetailsTextView.setVisibility(View.GONE);
        }
    }

    private void Clickable(boolean b) {
        if (b) {
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        } else {
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

    private AlertDialog AskOption() {
        final AlertDialog myQuittingDialogBox = new AlertDialog.Builder(this)
                .setTitle("Delete")
                .setMessage("هل ترغب في حذف هذا السجل ؟")
                .setIcon(R.mipmap.delete_icon)
                .setPositiveButton("حذف", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        if (Connected()) {
                            Clickable(false);
                            cookingDetailsProgressBar.setVisibility(View.VISIBLE);
                            db.collection("Cooking List").document(cooking.getGeneratedId())
                                    .delete()
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            Toast.makeText(CookingDetailsActivity.this, "تم الحذف بنجاح", Toast.LENGTH_SHORT).show();
                                            finish();
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Toast.makeText(CookingDetailsActivity.this, "نعتذر, لم يتم الحذف", Toast.LENGTH_SHORT).show();
                                            finish();
                                        }
                                    });
                        } else {
                            Toast.makeText(CookingDetailsActivity.this, "يرجى التحقق من الإتصال بالإنترنت", Toast.LENGTH_SHORT).show();
                        }
                        dialog.dismiss();
                    }
                })
                .setNegativeButton("إلغاء", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .create();
        myQuittingDialogBox.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                myQuittingDialogBox.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(getResources().getColor(R.color.alertDialogNegative));
                myQuittingDialogBox.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.alertDialogPositive));
            }
        });

        return myQuittingDialogBox;
    }

    public void EditButtonClicked(View view) {
        Clickable(false);
        Intent ToAddAndEditActivityIntent = new Intent(CookingDetailsActivity.this, AddAndEditActivity.class);
        ToAddAndEditActivityIntent.putExtra("cooking", cooking);
        ToAddAndEditActivityIntent.putExtra("state", "Edit");
        startActivity(ToAddAndEditActivityIntent);
    }

    public void DeleteButtonClicked(View view) {
        AlertDialog diaBox = AskOption();
        diaBox.show();
    }
}
