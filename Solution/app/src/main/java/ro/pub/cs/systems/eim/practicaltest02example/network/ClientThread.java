package ro.pub.cs.systems.eim.practicaltest02example.network;

import android.util.Log;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;

import ro.pub.cs.systems.eim.practicaltest02example.general.Constants;
import ro.pub.cs.systems.eim.practicaltest02example.general.Utilities;

public class ClientThread extends Thread{
    private final String address;
    private final int port;
    private final String city;
    private final String informationType;
    private final TextView weatherForecastTextView;
    private Socket socket;

    public ClientThread(String address, int port, String city, String informationType, TextView weatherForecastTextView) {
        this.address = address;
        this.port = port;
        this.city = city;
        this.informationType = informationType;
        this.weatherForecastTextView = weatherForecastTextView;
    }

    @Override
    public void run() {
        try {
            socket = new Socket(address, port);

            if (socket == null) {
                Log.e(Constants.TAG, "[CLIENT THREAD] Could not create socket!");
                return;
            }

            BufferedReader bufferedReader = Utilities.getReader(socket);
            PrintWriter printWriter = Utilities.getWriter(socket);

            if (bufferedReader == null || printWriter == null) {
                Log.e(Constants.TAG, "[CLIENT THREAD] Buffered Reader / Print Writer are null!");
                return;
            }

            printWriter.println(city);
            printWriter.flush();
            printWriter.println(informationType);
            printWriter.flush();

            String weatherInformation;

            while ((weatherInformation = bufferedReader.readLine()) != null) {
                final String finalizedWeatherInformation = weatherInformation;
                weatherForecastTextView.post(() -> weatherForecastTextView.setText(finalizedWeatherInformation));
            }
        } catch (IOException ioException) {
            Log.e(Constants.TAG, "[CLIENT THREAD] An exception has occurred: " + ioException.getMessage());
            if (Constants.DEBUG) {
                ioException.printStackTrace();
            }
        } finally {
            if (socket != null) {
                try {
                    socket.close();
                } catch (IOException ioException) {
                    Log.e(Constants.TAG, "[CLIENT THREAD] An exception has occurred: " + ioException.getMessage());
                    if (Constants.DEBUG) {
                        ioException.printStackTrace();
                    }
                }
            }
        }
    }
}
