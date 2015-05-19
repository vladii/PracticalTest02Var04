package ro.pub.cs.systems.pdsd.practicaltest02var04;

import java.io.BufferedInputStream;
import java.io.InputStream;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.ByteArrayBuffer;

import android.os.AsyncTask;

public class DoHttpRequest {
	private String url;
	private String responseString = null;
	
	public DoHttpRequest(String url) {
		this.url = url;
	}
	
	public String getContent() {
		HttpRequest asyncReq = new HttpRequest(url);
		
		asyncReq.execute();
		try {
			asyncReq.get();
			
		} catch (Exception e) {
			e.printStackTrace();
		}

		// The result will be stored in responseString.
		// Trim the whitespaces and parse the result.
		return responseString;
	}

	private class HttpRequest extends AsyncTask {
		private String url;
		
		public HttpRequest(String url) {
			super();
			
			this.url = url;
		}

		@Override
		protected Object doInBackground(Object... params) {
			// string buffers the url
			StringBuffer buffer_string = new StringBuffer(url);
			String replyString = "";

			// instanciate an HttpClient
			HttpClient httpclient = new DefaultHttpClient();
			// instanciate an HttpGet
			HttpGet httpget = new HttpGet(buffer_string.toString());

			try {
				// get the responce of the httpclient execution of the url
				HttpResponse response = httpclient.execute(httpget);
				InputStream is = response.getEntity().getContent();

				// buffer input stream the result
				BufferedInputStream bis = new BufferedInputStream(is);
				ByteArrayBuffer baf = new ByteArrayBuffer(20);
				int current = 0;
				while ((current = bis.read()) != -1) {
					baf.append((byte) current);
				}
				
				// the result as a string is ready for parsing
				replyString = new String(baf.toByteArray());
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			responseString = replyString;
			return replyString;
		}
		
	}
}
