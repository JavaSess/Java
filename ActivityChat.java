package com.example.chat3;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class ActivityChat extends Activity {

	Button btnSend;
	EditText edtMessage;
	TextView tvChat;
	Socket socket;
	Handler handler = new Handler();
	DataOutputStream outputStream;
	BufferedReader inputStream;

	private void log(final String message) {
		handler.post(new Runnable() {

			@Override
			public void run() {
				tvChat.setText(tvChat.getText() + "\n" + message);
			}
		});
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_chat);

		btnSend = (Button) findViewById(R.id.btnSend);
		edtMessage = (EditText) findViewById(R.id.edtMessage);
		tvChat = (TextView) findViewById(R.id.tvChat);

		Intent intent = getIntent();
		final boolean isServer = intent.getBooleanExtra("isServer", true);

		btnSend.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				String message = edtMessage.getText() + "\n";
				try {
					outputStream.write(message.getBytes());
					log("U:" + edtMessage.getText());
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
		
		Thread thread = new Thread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				if (isServer) {
					try {
						log("starting as server...");
						ServerSocket serverSocket = new ServerSocket(8000);
						log("waiting for client...");
						socket = serverSocket.accept();
						log("a new client connected!");
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				} else {
					try {
						log("connecting...");
						socket = new Socket("192.168.64.101", 8000);
						log("connected!");
					} catch (UnknownHostException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}

				try {
					outputStream = new DataOutputStream(
							socket.getOutputStream());
					inputStream = new BufferedReader(new InputStreamReader(
							socket.getInputStream()));
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				while(true){
					try {
						log("H: " + inputStream.readLine());
						
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}

			}
		});
		thread.start();

	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			if (socket != null)
				try {
					socket.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			System.exit(0);
			return true;
		}
		return true;
	}

}
