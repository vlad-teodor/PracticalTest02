package ro.pub.cs.systems.eim.practicaltest02;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URL;
import java.util.HashMap;

public class PracticalTest02MainActivity extends AppCompatActivity {
    EditText serverPortEditText;
    Button requestButton;
    ServerThread serverThread;
    EditText currencyEditText;
    TextView response;
    Button startServerButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_practical_test02_main);

        requestButton = findViewById(R.id.request);

        serverPortEditText = findViewById(R.id.port);

        currencyEditText = findViewById(R.id.currency);

        response = findViewById(R.id.result);

        startServerButton = findViewById(R.id.start);

        startServerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String serverPort = ((EditText)findViewById(R.id.server_port)).getText().toString();
                if (serverPort.isEmpty()) {
                    Toast.makeText(getApplicationContext(), "[MAIN ACTIVITY] Server port should be filled!", Toast.LENGTH_SHORT).show();
                    return;
                }
                serverThread = new ServerThread(Integer.parseInt(serverPort));

                if (serverThread.serverSocket == null) {
                    return;
                }
                try {
                    serverThread.join();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                response.setText(serverThread.getData());
            }
        });

        requestButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String serverPort = serverPortEditText.getText().toString();
                String currency = currencyEditText.getText().toString();
                if (serverPort.isEmpty()) {
                    Toast.makeText(getApplicationContext(), "[MAIN ACTIVITY] Server port should be filled!", Toast.LENGTH_SHORT).show();
                    return;
                }

                response.setText(serverThread.getData());
            }
        });

    }
}

class ServerThread extends Thread {
    ServerSocket serverSocket;
    private String currency = "EUR";
    String response;

    ServerThread(int port) {
        try {
            serverSocket = new ServerSocket(port);
        } catch (IOException ioException) {
            ioException.printStackTrace();
        }
    }

    @Override
    public void run() {
        try {
//            while (!Thread.currentThread().isInterrupted()) {
//                Socket socket = serverSocket.accept();
//                Log.i("i", "[SERVER THREAD] A connection request was received from " + socket.getInetAddress() + ":" + socket.getLocalPort());

                try {
                    HttpURLConnection con = (HttpURLConnection) new URL("https://api.coindesk.com/v1/bpi/currentprice/EUR.json").openConnection();
                    con.setRequestMethod("GET");
                    BufferedReader in = new BufferedReader(
                            new InputStreamReader(con.getInputStream()));
                    String inputLine;
                    StringBuilder response = new StringBuilder();

                    while ((inputLine = in.readLine()) != null) {
                        response.append(inputLine);
                    }
                    this.response = response.toString();
                    in.close();
                }
                catch (ProtocolException e) {
                    e.printStackTrace();
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
//            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String getData() {
        return response;
    }
}