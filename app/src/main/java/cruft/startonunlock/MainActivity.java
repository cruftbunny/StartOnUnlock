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

        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

        boolean enabled = sp.getBoolean("enabled", false);

        if(!enabled) {
            Log.w("CRUFT", "StartOnUnlock is currently set to disabled");
        }
        else {
            Log.w("CRUFT", "StartOnUnlock is currently set to enabled");
            enableSwitch.setChecked(true);

            // TODO: start background service
        }

        enableSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                SharedPreferences.Editor editor = sp.edit();

                if (enableSwitch.isChecked()) {

                    editor.putBoolean("enabled", true);
                    editor.apply();

                    Log.w("CRUFT", "StartOnUnlock enabled via switch");

                    // TODO: start background service

                }
                else if (!enableSwitch.isChecked()){

                    editor.putBoolean("enabled", false);
                    editor.apply();

                    Log.w("CRUFT", "StartOnUnlock disabled via switch");
                }
            }
        });

        Button chooserButton = findViewById(R.id.chooser_button);
        chooserButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Log.w("CRUFT", "Button pressed. Starting AppChooser");

                // TODO: start app chooser

            }
        });
    }
}
