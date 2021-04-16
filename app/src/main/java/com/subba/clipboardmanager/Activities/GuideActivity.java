package com.subba.clipboardmanager.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.subba.clipboardmanager.R;
import com.subba.clipboardmanager.databinding.ActivityGuideBinding;

public class GuideActivity extends AppCompatActivity {

    private ActivityGuideBinding mBinding;
    private Toolbar toolbar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = ActivityGuideBinding.inflate(getLayoutInflater());
        setContentView(mBinding.getRoot());

        toolbar = findViewById(R.id.guide_activity_toolbar);
        this.setSupportActionBar(toolbar);

    }

    @Override
    protected void onStart() {
        super.onStart();
        getSupportActionBar().setTitle(getResources().getString(R.string.report_an_issue));

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.guide_activity_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.send_report:
                String emailSubject = mBinding.issueTitle.getText().toString();
                String emailBody = mBinding.issueDescription.getText().toString();

                if(emailSubject.isEmpty() || emailSubject.trim().isEmpty()){
                    Toast.makeText(this, R.string.empty_input, Toast.LENGTH_SHORT).show();
                    return true;
                }
                
                Intent emailIntent = new Intent(Intent.ACTION_SEND);
                emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{ getResources().getString(R.string.email_id)});
                emailIntent.putExtra(Intent.EXTRA_SUBJECT, emailSubject);
                emailIntent.putExtra(Intent.EXTRA_TEXT, emailBody);
                emailIntent.setType("message/rfc822");
                startActivity(Intent.createChooser(emailIntent, "Choose an e-mail app: "));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}