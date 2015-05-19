package ro.pub.cs.systems.pdsd.practicaltest02var04;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class PracticalTest02Var04MainActivity extends Activity {
	
	/* Elements. */
	TextView portTextView;
	Button buttonStart;
	Button buttonStop;
	
	/* Communication variables. */
	private ServerThread serverThread;
	
	/* Communication thread --- code. */
	private class CommunicationThread extends Thread {
		public static final String TAG = "COMMTHREAD";
		private Socket socket;
		
		public CommunicationThread(Socket socket) {
			this.socket = socket;
		}
		
		@Override
		public void run() {
			try {
				Log.v(TAG, "Connection opened with " + socket.getInetAddress()+":"+socket.getLocalPort());
				
				/* Get reader and write data. */
				BufferedReader reader = Utilities.getReader(socket);
				
				/* Get writer and respond. */
				PrintWriter printWriter = Utilities.getWriter(socket);
				printWriter.println("TODO");
				socket.close();
				
				Log.v(TAG, "Connection closed");
				
			} catch (IOException ioException) {
				Log.e(TAG, "An exception has occurred: " + ioException.getMessage());
				ioException.printStackTrace();
			}
		}
	}
	
	/* Server thread --- code. */
	private class ServerThread extends Thread {
		private boolean isRunning;
		private ServerSocket serverSocket;
		
		public static final String TAG = "SERVER";
		
		public void startServer() {
			isRunning = true;
			start();
			Log.v(TAG, "startServer() method invoked");
		}
		
		public void stopServer() {
			isRunning = false;
			
			try {
				serverSocket.close();
				
			} catch(IOException ioException) {
				Log.e(TAG, "An exception has occurred: "+ioException.getMessage());
				ioException.printStackTrace();
			}
			
			Log.v(TAG, "stopServer() method invoked");
		}
		
		@Override
		public void run() {
			try {
				serverSocket = new ServerSocket(Integer.parseInt(portTextView.getText().toString()));

				while (isRunning) {
					Socket socket = serverSocket.accept();
					new CommunicationThread(socket).start();
				}
				
			} catch (IOException ioException) {
				Log.e(TAG, "An exception has occurred: "+ioException.getMessage());
				ioException.printStackTrace();
			}
		}
	}
	
	/* Start server on port button listener. */
	private class ButtonStartListener implements Button.OnClickListener {
		@Override
		public void onClick(View v) {
			serverThread = new ServerThread();
			serverThread.startServer();
		}
	}
	
	/* End server button listener. */
	private class ButtonStopListener implements Button.OnClickListener {
		@Override
		public void onClick(View v) {
			serverThread.stopServer();
		}
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_practical_test02_var04_main);
		
		/* Find all elements. */
		portTextView = (TextView) findViewById(R.id.textViewPort);
		buttonStart = (Button) findViewById(R.id.buttonStart);
		buttonStop = (Button) findViewById(R.id.buttonStop);
		
		/* Add listeners. */
		buttonStart.setOnClickListener(new ButtonStartListener());
		buttonStop.setOnClickListener(new ButtonStopListener());
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.practical_test02_var04_main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
}
