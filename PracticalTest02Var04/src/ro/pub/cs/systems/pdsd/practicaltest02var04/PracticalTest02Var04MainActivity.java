package ro.pub.cs.systems.pdsd.practicaltest02var04;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class PracticalTest02Var04MainActivity extends Activity {
	
	/* Elements. */
	TextView portTextView;
	TextView urlTextView;
	EditText editTextUrlContent;
	Button buttonStart;
	Button buttonStop;
	Button buttonGet;
	
	/* Communication variables. */
	private ServerThread serverThread;
	
	/* Communication thread --- code. */
	private class CommunicationThread extends Thread {
		public static final String TAG = "COMMTHREAD";
		private Map<String, String> hash;
		private Socket socket;
		
		public CommunicationThread(Socket socket, Map<String, String> hash) {
			this.socket = socket;
		}
		
		public String getHtmlContent(String url) {
			if (hash.containsKey(url)) {
				Log.v(TAG, "URL served from LOCAL CACHE.");
				
				return hash.get(url);
			}
			
			// Do a http request.
			Log.v(TAG, "URL server from HTTP REQUEST.");
			
			DoHttpRequest req = new DoHttpRequest(url);
			String htmlContent = req.getContent();
			
			return htmlContent;
		}
		
		@Override
		public void run() {
			try {
				Log.v(TAG, "Connection opened with " + socket.getInetAddress()+":"+socket.getLocalPort());
				
				/* Get reader and read the url. */
				BufferedReader reader = Utilities.getReader(socket);
				String url = reader.readLine();
				
				/* Find the html content and insert it into the hash. */
				String htmlContent = getHtmlContent(url);
				hash.put(url, htmlContent);
				
				/* Get writer and respond with html content. */
				PrintWriter printWriter = Utilities.getWriter(socket);
				printWriter.println(htmlContent);
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
		private Map<String, String> hash;	// Map between url and content.
		public static final String TAG = "SERVER";
		
		public void startServer() {
			hash = new HashMap<String, String>();
			
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
					new CommunicationThread(socket, hash).start();
				}
				
			} catch (IOException ioException) {
				Log.e(TAG, "An exception has occurred: "+ioException.getMessage());
				ioException.printStackTrace();
			}
		}
	}
	
	/* Client thread --- code. */
	private class ClientThread implements Runnable {
		public final static String TAG = "CLIENT";
		private Socket socket = null;
		
		@Override
		public void run() {
			try {
				editTextUrlContent.post(new Runnable() {
					@Override
					public void run() {
						editTextUrlContent.setText("");
					}
				});				
				
				String url = urlTextView.getText().toString();
				int serverPort = Integer.parseInt(portTextView.getText().toString());
				
				socket = new Socket("127.0.0.1", serverPort);
				if (socket == null)
					return;
				
				Log.v(TAG, "Connection opened with "+socket.getInetAddress()+":"+socket.getLocalPort());			
				
				// Send url to server.
				PrintWriter writer = Utilities.getWriter(socket);
				writer.write(url);
				
				// Read.
				BufferedReader bufferedReader = Utilities.getReader(socket);
				String currentLine;
				while ((currentLine = bufferedReader.readLine()) != null) {
					final String finalizedCurrentLine = currentLine;
					editTextUrlContent.post(new Runnable() {
						@Override
						public void run() {
							editTextUrlContent.append(finalizedCurrentLine);
						}
					});
				}
				
				socket.close();
				Log.v(TAG, "Connection closed");
				
			} catch (IOException ioException) {
				Log.e(TAG, "An exception has occurred: "+ioException.getMessage());
				ioException.printStackTrace();
					
			} catch (Exception exception) {
				Log.e(TAG, "An exception has occurred: "+exception.getMessage());
				exception.printStackTrace();
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
	
	/* Get html content button listener. */
	private class ButtonGetListener implements Button.OnClickListener {
		@Override
		public void onClick(View v) {
			new Thread(new ClientThread()).start();
		}
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_practical_test02_var04_main);
		
		/* Find all elements. */
		portTextView = (TextView) findViewById(R.id.textViewPort);
		urlTextView = (TextView) findViewById(R.id.textViewPort);
		editTextUrlContent = (EditText) findViewById(R.id.editTextUrlContent);
		buttonStart = (Button) findViewById(R.id.buttonStart);
		buttonStop = (Button) findViewById(R.id.buttonStop);
		buttonGet = (Button) findViewById(R.id.buttonGet);
		
		/* Add listeners. */
		buttonStart.setOnClickListener(new ButtonStartListener());
		buttonStop.setOnClickListener(new ButtonStopListener());
		buttonGet.setOnClickListener(new ButtonGetListener());
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
