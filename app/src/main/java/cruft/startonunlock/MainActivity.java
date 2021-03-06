package cruft.startonunlock;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Switch;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final Switch enableSwitch = findViewById(R.id.enable_switch);

        isEnabled();

        enableSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                SharedPreferences.Editor editor = sp.edit();

                if (enableSwitch.isChecked()) {

                    editor.putBoolean("enabled", true);
                    editor.apply();

                    Log.w("CRUFT", "StartOnUnlock enabled via switch");
                }
                else if (!enableSwitch.isChecked()){

                    editor.putBoolean("enabled", false);
                    editor.apply();

                    Log.w("CRUFT", "StartOnUnlock disabled via switch");
                }

                isEnabled();
            }
        });

        Button chooserButton = findViewById(R.id.chooser_button);
        chooserButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Log.w("CRUFT", "Button pressed. Starting AppChooser");

                Intent StartAppChooser = new Intent(getApplicationContext(), AppChooser.class);
                startActivityForResult(StartAppChooser, 1);
            }
        });
    }

    public void isEnabled() {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        boolean enabled = sp.getBoolean("enabled", false);

        Switch enableSwitch = findViewById(R.id.enable_switch);
        Intent BackgroundService = new Intent(this, BackgroundService.class);

        if(!enabled) {
            Log.w("CRUFT", "StartOnUnlock is currently set to disabled");
            enableSwitch.setChecked(false);
            stopService(BackgroundService);
        }
        else {
            Log.w("CRUFT", "StartOnUnlock is currently set to enabled");
            enableSwitch.setChecked(true);
            startService(BackgroundService);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        if(null != data){
            if(requestCode == 1){
                // app successfully chosen

                String message=data.getStringExtra("MESSAGE_package_name");

                Log.w("CRUFT", "Chosen app: " + message);

                SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                SharedPreferences.Editor editor = sp.edit();
                editor.putString("appName", message);
                editor.apply();
            }
        }
    }
}
