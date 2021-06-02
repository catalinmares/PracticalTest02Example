package ro.pub.cs.systems.eim.practicaltest02example.view;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import ro.pub.cs.systems.eim.practicaltest02example.R;
import ro.pub.cs.systems.eim.practicaltest02example.general.Constants;
import ro.pub.cs.systems.eim.practicaltest02example.network.ClientThread;
import ro.pub.cs.systems.eim.practicaltest02example.network.ServerThread;

public class PracticalTest02ExampleMainActivity extends AppCompatActivity {
    private EditText serverPortEditText;
    private Button connectButton;

    private EditText clientAddressEditText;
    private EditText clientPortEditText;
    private EditText cityEditText;
    private Spinner informationTypeSpinner;
    private Button getWeatherForecastButton;
    private TextView weatherForecastTextView;

    private ServerThread serverThread;
    private ClientThread clientThread;

    private final ConnectButtonClickListener connectButtonClickListener = new ConnectButtonClickListener();
    private class ConnectButtonClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            String serverPort = serverPortEditText.getText().toString();

            if (serverPort.isEmpty()) {
                Toast.makeText(getApplicationContext(), "Server port should be filled!", Toast.LENGTH_SHORT).show();
            }

            serverThread = new ServerThread(Integer.parseInt(serverPort));

            if (serverThread.getServerSocket() == null) {
                Log.e(Constants.TAG, "[MAIN ACTIVITY] Could not create server thread!");
            }

            serverThread.start();
        }
    }

    private final GetWeatherForecastButtonClickListener getWeatherForecastButtonClickListener = new GetWeatherForecastButtonClickListener();
    private class GetWeatherForecastButtonClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            String clientAddress = clientAddressEditText.getText().toString();
            String clientPort = clientPortEditText.getText().toString();

            if (clientAddress.isEmpty() || clientPort.isEmpty()) {
                Toast.makeText(getApplicationContext(), "Client connection parameters should be filled!", Toast.LENGTH_SHORT).show();
                return;
            }

            if (serverThread == null || !serverThread.isAlive()) {
                Toast.makeText(getApplicationContext(), "There is no server to connect to!", Toast.LENGTH_SHORT).show();
                return;
            }

            String city = cityEditText.getText().toString();
            String informationType = informationTypeSpinner.getSelectedItem().toString();

            if (city == null || city.isEmpty() || informationType == null || informationType.isEmpty()) {
                Toast.makeText(getApplicationContext(), "Parameters from client (city / information type) should be filled!", Toast.LENGTH_SHORT).show();
                return;
            }

            city = city.replace(" ", "%20");

            weatherForecastTextView.setText(Constants.EMPTY_STRING);

            clientThread = new ClientThread(
                    clientAddress, Integer.parseInt(clientPort), city, informationType, weatherForecastTextView
            );

            clientThread.start();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(Constants.TAG, "[MAIN ACTIVITY] onCreate() callback method has been invoked");
        setContentView(R.layout.activity_practical_test02_example_main);

        serverPortEditText = findViewById(R.id.server_port);
        connectButton = findViewById(R.id.server_button);
        connectButton.setOnClickListener(connectButtonClickListener);

        clientAddressEditText = findViewById(R.id.client_address);
        clientPortEditText = findViewById(R.id.client_port);
        cityEditText = findViewById(R.id.client_city);
        informationTypeSpinner = findViewById(R.id.client_data);
        getWeatherForecastButton = findViewById(R.id.client_button);
        getWeatherForecastButton.setOnClickListener(getWeatherForecastButtonClickListener);
        weatherForecastTextView = findViewById(R.id.client_forecast);
    }

    @Override
    protected void onDestroy() {
        Log.i(Constants.TAG, "[MAIN ACTIVITY] onDestroy() callback method has been invoked");

        if (serverThread != null) {
            serverThread.stopThread();
        }

        super.onDestroy();
    }
}