package com.appspresso.core.runtime.server.kraken;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.apache.commons.logging.Log;
import org.apache.http.HttpResponse;
import org.apache.http.entity.ContentProducer;
import org.apache.http.entity.EntityTemplate;
import org.apache.http.impl.DefaultHttpResponseFactory;
import org.apache.http.impl.DefaultHttpServerConnection;
import org.apache.http.impl.NoConnectionReuseStrategy;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.params.CoreProtocolPNames;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.BasicHttpProcessor;
import org.apache.http.protocol.HttpContext;
import org.apache.http.protocol.HttpProcessor;
import org.apache.http.protocol.HttpRequestHandler;
import org.apache.http.protocol.HttpRequestHandlerRegistry;
import org.apache.http.protocol.HttpService;
import org.apache.http.protocol.ResponseConnControl;
import org.apache.http.protocol.ResponseContent;
import org.apache.http.protocol.ResponseDate;
import org.apache.http.protocol.ResponseServer;

import com.appspresso.api.AxLog;

/**
 * This class provides a simple implementation of
 * {@link com.appspresso.core.runtime.server.IWebServer} using <a
 * href="http://hc.apache.org">apache's HttpComponents library</a>.
 * 
 * TODO: more test! more optimization! and more works!
 * 
 */
public class Kraken implements Runnable {
    private static final Log L = AxLog.getLog(Kraken.class.getSimpleName());

    private static final String DEF_ENCODING = "UTF-8";

    private static final int DEF_SO_TIMEOUT = 0; // 0 for infinite
    private static final int DEF_SOCKET_BUFFER_SIZE = 8 * 1024;
    private static final String DEF_SERVER_NAME = "Kraken/1.1";

    private/* final */InetSocketAddress socketAddress;
    private final HttpParams httpParams;
    private final HttpService httpService;
    private final HttpProcessor httpProcessor;
    private final HttpRequestHandlerRegistry httpRequestHandlerRegistry;
    private final ExecutorService executorService;

    private Thread listenerThread;
    private boolean running;
    private ServerSocket serverSocket;

    public Kraken(String host, int port) {
        this(new InetSocketAddress(host, port));
    }

    public Kraken(InetSocketAddress socketAddress) {
        // TODO: tuning point! choose best thread pool size and policy!
        // this(socketAddress, Executors.newCachedThreadPool());
        this(socketAddress, Executors.newFixedThreadPool(10));
        // this(socketAddress, new ThreadPoolExecutor(5, 20, 1,
        // TimeUnit.SECONDS,
        // new LinkedBlockingQueue<Runnable>()));
    }

    public Kraken(InetSocketAddress socketAddress, ExecutorService executorService) {
        this.socketAddress = socketAddress;
        this.executorService = executorService;

        httpParams = createHttpParams();
        httpProcessor = createHttpProcessor();
        httpRequestHandlerRegistry = new HttpRequestHandlerRegistry();

        httpService = new HttpService(httpProcessor, new NoConnectionReuseStrategy(),// XXX:DefaultConnectionReuseStrategy(),
                new DefaultHttpResponseFactory());
        httpService.setHandlerResolver(httpRequestHandlerRegistry);
        httpService.setParams(httpParams);

        running = false;
        // listenerThread = new Thread(this);
        // listenerThread.setDaemon(true);

    }

    protected HttpProcessor createHttpProcessor() {
        BasicHttpProcessor httpProcessor = new BasicHttpProcessor();
        httpProcessor.addInterceptor(new ResponseDate());
        httpProcessor.addInterceptor(new ResponseServer());
        httpProcessor.addInterceptor(new ResponseContent());
        httpProcessor.addInterceptor(new ResponseConnControl());
        return httpProcessor;
    }

    protected HttpParams createHttpParams() {
        BasicHttpParams httpParams = new BasicHttpParams();
        httpParams.setIntParameter(CoreConnectionPNames.SO_TIMEOUT, DEF_SO_TIMEOUT);
        httpParams.setIntParameter(CoreConnectionPNames.SOCKET_BUFFER_SIZE, DEF_SOCKET_BUFFER_SIZE);
        httpParams.setBooleanParameter(CoreConnectionPNames.STALE_CONNECTION_CHECK, false);
        httpParams.setBooleanParameter(CoreConnectionPNames.TCP_NODELAY, true);
        httpParams.setParameter(CoreProtocolPNames.ORIGIN_SERVER, DEF_SERVER_NAME);
        httpParams.setIntParameter(CoreConnectionPNames.MAX_LINE_LENGTH, 0);
        httpParams.setIntParameter(CoreConnectionPNames.MAX_HEADER_COUNT, 0);
        return httpParams;
    }

    public void addHandler(String pattern, HttpRequestHandler handler) {
        httpRequestHandlerRegistry.register(pattern, handler);
    }

    public String getHost() {
        return socketAddress.getHostName();
    }

    public int getPort() {
        // return socketAddress.getPort();
        return serverSocket.getLocalPort();
    }

    public void init() {
        try {
            serverSocket = new ServerSocket();
            try {
                serverSocket.bind(socketAddress);
            }
            catch (SocketException e) {
                // retry binding
                serverSocket = new ServerSocket();
                socketAddress = new InetSocketAddress(socketAddress.getHostName(), 0);
                serverSocket.bind(socketAddress);
            }
        }
        catch (IOException e) {
            if (L.isErrorEnabled()) {
                L.error("failed to open server socket", e);
            }
        }
    }

    public void destroy() {
        try {
            serverSocket.close();
            serverSocket = null;
        }
        catch (IOException e) {
            if (L.isErrorEnabled()) {
                L.error("failed to close server socket", e);
            }
        }
    }

    public synchronized void start() {
        if (!running) { // if(!listenerThread.isAlive()) {
            if (L.isTraceEnabled()) {
                L.trace("start kraken");
            }
            running = true;
            listenerThread = new Thread(this);
            listenerThread.setDaemon(true);
            listenerThread.start();
        }
        else {
            if (L.isTraceEnabled()) {
                L.trace("kraken is already started!");
            }
        }
    }

    public synchronized void stop() {
        if (running) { // if(listenerThread.isAlive()) {
            if (L.isTraceEnabled()) {
                L.trace("stop kraken");
            }
            running = false;
            listenerThread.interrupt();
            // listenerThread.join();
        }
        else {
            if (L.isTraceEnabled()) {
                L.trace("kraken is not yet started!");
            }
        }
    }

    @Override
    public void run() {
        if (L.isTraceEnabled()) {
            L.trace("kraken is listening on " + socketAddress);
        }
        try {
            while (!Thread.interrupted() && running) {
                if (L.isTraceEnabled()) {
                    ThreadPoolExecutor pool = (ThreadPoolExecutor) executorService;
                    L.trace(">>>>>> active=" + pool.getActiveCount() + ",task="
                            + pool.getTaskCount() + ",completed=" + pool.getCompletedTaskCount()
                            + ",corePoolSize=" + pool.getCorePoolSize() + ",maxPoolSize="
                            + pool.getMaximumPoolSize() + ",largestPoolSize="
                            + pool.getLargestPoolSize() + ",keepAlive"
                            + pool.getKeepAliveTime(TimeUnit.MILLISECONDS) + "queueSize="
                            + pool.getQueue().size() + ",queueRemaining="
                            + pool.getQueue().remainingCapacity());
                }
                executorService.execute(new HttpConnectionHandler(serverSocket.accept()));
            }
        }
        catch (Exception e) {
            if (L.isWarnEnabled()) {
                L.warn("unexpected error!", e);
            }
        }
        finally {
            // executorService.shutdown();
        }
        if (L.isTraceEnabled()) {
            L.trace("stopping kraken...");
        }
    }

    // class DebugHttpRequestParser extends AbstractMessageParser {
    //
    // private final HttpRequestFactory requestFactory;
    // private final CharArrayBuffer lineBuf;
    //
    // public DebugHttpRequestParser(final SessionInputBuffer buffer,
    // final LineParser parser,
    // final HttpRequestFactory requestFactory, final HttpParams params) {
    // super(buffer, parser, params);
    // if (requestFactory == null) {
    // throw new IllegalArgumentException(
    // "Request factory may not be null");
    // }
    // this.requestFactory = requestFactory;
    // this.lineBuf = new CharArrayBuffer(128);
    // }
    //
    // protected HttpMessage parseHead(final SessionInputBuffer sessionBuffer)
    // throws IOException, HttpException, ParseException {
    //
    // this.lineBuf.clear();
    // System.err.println("********dataAvailable=" +
    // sessionBuffer.isDataAvailable(0));
    // int i = sessionBuffer.readLine(this.lineBuf);
    // System.err.println("**********" + lineBuf);
    // if (i == -1) {
    // throw new ConnectionClosedException("Client closed connection");
    // }
    // ParserCursor cursor = new ParserCursor(0, this.lineBuf.length());
    // RequestLine requestline = this.lineParser.parseRequestLine(
    // this.lineBuf, cursor);
    // return this.requestFactory.newHttpRequest(requestline);
    // }
    // };
    //
    // class DebugHttpServerConnection extends DefaultHttpServerConnection {
    // protected HttpMessageParser createRequestParser(
    // final SessionInputBuffer buffer,
    // final HttpRequestFactory requestFactory, final HttpParams params) {
    // // override in derived class to specify a line parser
    // return new DebugHttpRequestParser(buffer, null, requestFactory,
    // params);
    // }
    // };

    class HttpConnectionHandler implements Runnable {

        private final Socket clientSocket;

        public HttpConnectionHandler(Socket clientSocket) {
            this.clientSocket = clientSocket;
        }

        @Override
        public void run() {
            if (L.isTraceEnabled()) {
                L.trace("connection from " + clientSocket.getInetAddress());
            }
            // DefaultHttpServerConnection httpServerConnection = new
            // DebugHttpServerConnection();
            DefaultHttpServerConnection httpServerConnection = new DefaultHttpServerConnection();
            try {
                httpServerConnection.bind(clientSocket, httpParams);
            }
            catch (IOException e) {
                if (L.isWarnEnabled()) {
                    L.warn("connection bind failed!", e);
                }
                return;
            }

            HttpContext httpContext = new BasicHttpContext();

            // XXX Mungyu. 연속적으로 sync call 이 들어올 경우 더 이상 ajax 요청이 들어오지 않는 문제가 있어
            // while-loop를 try~catch 밖으로 꺼냈음
            // XXX iolo. 그렇게 했더니 헤더 파싱하다 실패하는 경우가 있어서 원래대로~~
            // 문제는 그게 아닌거 같음... -_-;
            try {
                while (!Thread.interrupted() && httpServerConnection.isOpen()) {
                    httpService.handleRequest(httpServerConnection, httpContext);
                }
            }
            catch (Exception e) {
                // ConnectionClosedException, IOException,HttpException
                if (L.isWarnEnabled()) {
                    L.warn("handleRequest failed!", e);
                }
            }
            finally {
                try {
                    httpServerConnection.shutdown();
                }
                catch (IOException ignore) {}
            }
        }

    }

    public static void sendErrorPage(HttpResponse response, int statusCode, final String message) {
        response.setStatusCode(statusCode);
        EntityTemplate entity = new EntityTemplate(new ContentProducer() {

            @Override
            public void writeTo(OutputStream outstream) throws IOException {
                PrintStream out = new PrintStream(outstream, false, DEF_ENCODING);
                out.print("<html><head><title>");
                out.print(message);
                out.print("</title></head><body><h1>");
                out.print(message);
                out.print("</h1></body></html>");
                outstream.flush();
            }
        });
        entity.setContentType("text/html");
        response.setEntity(entity);
    }

}
