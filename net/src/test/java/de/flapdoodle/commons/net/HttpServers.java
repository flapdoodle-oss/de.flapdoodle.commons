/*
 * Copyright (C) 2016
 *   Michael Mosmann <michael@mosmann.de>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package de.flapdoodle.commons.net;

import fi.iki.elonen.NanoHTTPD;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Optional;

public class HttpServers {

	public static NanoHTTPD.Response response(int status, String mimeType, byte[] data) {
		return response(status, mimeType, data, data.length);
	}

	public static NanoHTTPD.Response response(int status, String mimeType, byte[] data, int contentLength) {
		NanoHTTPD.Response ret = NanoHTTPD.newFixedLengthResponse(NanoHTTPD.Response.Status.lookup(status), mimeType, new ByteArrayInputStream(data), data.length);
		ret.addHeader("content-length", "" + contentLength);
		return ret;

	}

	public static NanoHTTPD.Response chunkedResponse(int status, String mimeType, byte[] data) {
		return NanoHTTPD.newChunkedResponse(NanoHTTPD.Response.Status.lookup(status), mimeType, new ByteArrayInputStream(data));
	}

	public static NanoHTTPD.Response rawResponse(byte[] data) {
		return response(200, "text/text", data);
	}

	public static HttpServers.HttpServer httpServer(int port, HttpServerFactory.Listener<NanoHTTPD.IHTTPSession, NanoHTTPD.Response> listener)
		throws IOException {
		return new HttpServers.HttpServer(port, listener);
	}

	public static HttpServers.HttpsServer httpsServer(int port, HttpServerFactory.Listener<NanoHTTPD.IHTTPSession, NanoHTTPD.Response> listener)
		throws IOException {
		return new HttpServers.HttpsServer(port, listener);
	}

	static class HttpServer extends NanoHTTPD implements HttpServerFactory.HttpServer {

		private final HttpServerFactory.Listener<NanoHTTPD.IHTTPSession, NanoHTTPD.Response> listener;

		public HttpServer(int port, HttpServerFactory.Listener<NanoHTTPD.IHTTPSession, NanoHTTPD.Response> listener) throws IOException {
			super("localhost", port);
			this.listener = listener;
			start(NanoHTTPD.SOCKET_READ_TIMEOUT, true);
		}

		@Override
		public void close() {
			this.stop();
		}

		@Override
		public String getProtocol() {
			return "http://";
		}
		
		@Override
		public Response serve(IHTTPSession session) {
			Optional<Response> response = listener.serve(session);
			return response
				.orElseGet(() -> super.serve(session));
		}
	}

	static class HttpsServer extends NanoHTTPD implements HttpServerFactory.HttpServer {

		private final HttpServerFactory.Listener<NanoHTTPD.IHTTPSession, NanoHTTPD.Response> listener;

		public HttpsServer(int port, HttpServerFactory.Listener<NanoHTTPD.IHTTPSession, NanoHTTPD.Response> listener) throws IOException {
			super("localhost", port);

			// keytool -genkey -keyalg RSA -alias selfsigned -keystore keystore.jks -storepass password -validity 360 -keysize 2048 -ext SAN=DNS:localhost,IP:127.0.0.1  -validity 9999
			makeSecure(NanoHTTPD.makeSSLSocketFactory("/localhost-keystore.jks", "password".toCharArray()), null);

			this.listener = listener;
			start(NanoHTTPD.SOCKET_READ_TIMEOUT, true);
		}

		@Override
		public void close() {
			this.stop();
		}

		@Override
		public String getProtocol() {
			return "https://";
		}

		@Override
		public Response serve(IHTTPSession session) {
			Optional<Response> response = listener.serve(session);
			return response
				.orElseGet(() -> super.serve(session));
		}
	}

}
