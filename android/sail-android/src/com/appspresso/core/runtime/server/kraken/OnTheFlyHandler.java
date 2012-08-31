package com.appspresso.core.runtime.server.kraken;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;

import org.apache.commons.logging.Log;
import org.apache.http.HttpException;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.entity.ContentProducer;
import org.apache.http.entity.EntityTemplate;
import org.apache.http.protocol.HttpContext;
import org.apache.http.protocol.HttpRequestHandler;

import com.appspresso.api.AxLog;

public class OnTheFlyHandler implements HttpRequestHandler {

    private static final Log L = AxLog.getLog(OnTheFlyHandler.class);

    private final String contentUri;

    public OnTheFlyHandler(String host, int port, String project) {
        if (L.isTraceEnabled()) {
            L.trace("on-the-fly mode is enabled using nessie: host=" + host + ",port=" + port
                    + ",project=" + project);
        }
        this.contentUri = "http://" + host + ":" + port + "/content/" + project;
    }

    @Override
    public void handle(HttpRequest request, HttpResponse response, HttpContext context)
            throws HttpException, IOException {
        String uri = contentUri + request.getRequestLine().getUri();

        HttpRequest req = HttpClientUtils.newHttpRequest(request.getRequestLine().getMethod(), uri);

        try {
            // req.setHeaders(request.getAllHeaders());
            // TODO:copy request entity...
            HttpResponse res = HttpClientUtils.executeHttpRequest(req);
            response.setStatusLine(res.getStatusLine());

            if (res.getStatusLine().getStatusCode() != HttpStatus.SC_OK) {
                sendDebugServerError(response);
                return;
            }

            final InputStream in = res.getEntity().getContent();
            EntityTemplate entity = new EntityTemplate(new ContentProducer() {
                @Override
                public void writeTo(OutputStream outStream) throws IOException {
                    OutputStream out = new BufferedOutputStream(outStream);
                    try {
                        byte[] buf = new byte[4096];
                        int n;
                        while ((n = in.read(buf)) > 0) {
                            out.write(buf, 0, n);
                        }
                        out.flush();
                    }
                    finally {
                        if (in != null) {
                            try {
                                in.close();
                            }
                            catch (IOException ignored) {}
                        }
                    }
                }
            });
            entity.setContentType(res.getEntity().getContentType());
            HttpHeaderUtils.setNoCacheHeader(response);
            response.setEntity(entity);// res.getEntity());
        }
        catch (IOException e) {
            sendDebugServerError(response);
        }
    }

    private static String DEBUG_SERVER_ERROR_HTML =
            "<!DOCTYPE html><html><head><script type='text/javascript' src='/appspresso/appspresso.js'></script><meta name='viewport' content='width=320;' /><meta http-equiv='pragma' content='no-cache'/><meta http-equiv='Content-Type' content='text/html;charset=UTF-8'></head><style>html, body{height: 100%;}body{margin:0;padding:0;overflow: hidden;background: -webkit-gradient(linear, left top, left bottom, color-stop(0%, #666), color-stop(50%, #000), color-stop(100%, #666));}.content{#background: -webkit-gradient(linear, left top, left bottom, color-stop(0%, #666), color-stop(50%, #000), color-stop(100%, #666));color: #ddd;min-height: 100%;}h2{color: yellow;}#network_progress{display: block;width:65%;height:4px;margin: 0 auto;background-color: #eee;}#network_progress.progress div{height:5px;margin-right: -1px;margin-bottom: -1px;background: -webkit-gradient(linear, left top, left bottom, from(#555), to(#333));-webkit-mask-image:-webkit-gradient(linear, 18 0, 0 10, color-stop(0.23, rgba(0,0,0,0.8)), color-stop(0.3, rgba(0,0,0,1)), color-stop(0.77, rgba(0,0,0,0.8)));-webkit-mask-repeat:repeat-x;-webkit-mask-size:20px 15px;-webkit-animation-name:progressBarAni;-webkit-animation-duration:0.5s;-webkit-animation-iteration-count:infinite;-webkit-animation-timing-function:linear;}.network .img1,.network .img2,.network .img3{position: absolute;margin-top: -12px;}.network .img1{left: 10%;}.network .img2{margin-left: -25px;margin-top: -25px;left: 50%;}.network .img3{right: 10%;}@-webkit-keyframes progressBarAni{from{-webkit-mask-position-x:0;}to{-webkit-mask-position-x:20px;}}button{font-size: 20px;margin-top: 50px;line-height: 36px;font-family: sans-serif;font-weight: bold;color: #999;padding: 0 15px;border: solid 1px 333;border-radius: 3px;background: -webkit-gradient( linear, left top, left bottom, from(#666), to(#333) );box-shadow: inset 0 1px 0 rgba(255,255,255,.2), 0 1px 1px rgba(0,0,0,.08);text-shadow: 0 -1px 0 rgba(0,0,0,.25);height: 50px;display:block;position:absolute;width: 160px;margin-left: -80px;left: 50%;-webkit-tap-highlight-color: rgba(255, 255, 255, 0);}button.pressed{background: -webkit-gradient( linear, left top, left bottom, from(#333), to(#666) );}@media only screen and (-webkit-min-device-pixel-ratio:1.5), only screen and (min-device-pixel-ratio:1.5){button{border:none;padding:0 16px;box-shadow: inset 0 0 1px #000, inset 0 1px 0 #333, 0 1px 1px -1px rgba(0, 0, 0, .5);}}@media all and (orientation:portrait){.content .bd{padding: 20% 5% 30% 5%;}}@media all and (orientation:landscape){.content .bd{padding: 10% 5% 30% 5%;}}</style><script type='text/javascript'>function init(){var x=document.body.querySelector('.img2');var blink=function(){if(x.style.display==='none'){x.style.display='block';}else{x.style.display='none';}};setInterval(blink, 600);document.body.addEventListener('touchstart', function(e){var target=e.target;if(target.tagName==='BUTTON'){target.className='pressed';setTimeout(function(){target.className='';location.reload();}, 200);e.stopPropagation();}}, false);}</script><body onload='init()'><div class='content'><div class='bd'><img src='data:image/gif;base64,iVBORw0KGgoAAAANSUhEUgAAADAAAAAwCAYAAABXAvmHAAAAGXRFWHRTb2Z0d2FyZQBBZG9iZSBJbWFnZVJlYWR5ccllPAAAAxhpVFh0WE1MOmNvbS5hZG9iZS54bXAAAAAAADw/eHBhY2tldCBiZWdpbj0i77u/IiBpZD0iVzVNME1wQ2VoaUh6cmVTek5UY3prYzlkIj8+IDx4OnhtcG1ldGEgeG1sbnM6eD0iYWRvYmU6bnM6bWV0YS8iIHg6eG1wdGs9IkFkb2JlIFhNUCBDb3JlIDUuMC1jMDYwIDYxLjEzNDM0MiwgMjAxMC8wMS8xMC0xODowNjo0MyAgICAgICAgIj4gPHJkZjpSREYgeG1sbnM6cmRmPSJodHRwOi8vd3d3LnczLm9yZy8xOTk5LzAyLzIyLXJkZi1zeW50YXgtbnMjIj4gPHJkZjpEZXNjcmlwdGlvbiByZGY6YWJvdXQ9IiIgeG1sbnM6eG1wPSJodHRwOi8vbnMuYWRvYmUuY29tL3hhcC8xLjAvIiB4bWxuczp4bXBNTT0iaHR0cDovL25zLmFkb2JlLmNvbS94YXAvMS4wL21tLyIgeG1sbnM6c3RSZWY9Imh0dHA6Ly9ucy5hZG9iZS5jb20veGFwLzEuMC9zVHlwZS9SZXNvdXJjZVJlZiMiIHhtcDpDcmVhdG9yVG9vbD0iQWRvYmUgUGhvdG9zaG9wIENTNSIgeG1wTU06SW5zdGFuY2VJRD0ieG1wLmlpZDo3MURGMjU3NzQ2MDYxMUUwODZEQUZFNTVDNzQ2OTkyQyIgeG1wTU06RG9jdW1lbnRJRD0ieG1wLmRpZDo3MURGMjU3ODQ2MDYxMUUwODZEQUZFNTVDNzQ2OTkyQyI+IDx4bXBNTTpEZXJpdmVkRnJvbSBzdFJlZjppbnN0YW5jZUlEPSJ4bXAuaWlkOjcxREYyNTc1NDYwNjExRTA4NkRBRkU1NUM3NDY5OTJDIiBzdFJlZjpkb2N1bWVudElEPSJ4bXAuZGlkOjcxREYyNTc2NDYwNjExRTA4NkRBRkU1NUM3NDY5OTJDIi8+IDwvcmRmOkRlc2NyaXB0aW9uPiA8L3JkZjpSREY+IDwveDp4bXBtZXRhPiA8P3hwYWNrZXQgZW5kPSJyIj8+FB0CoQAAB+JJREFUeNrUWkuPHDUQLnf37Lx2USCP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'/><h3 style='margin: -50px 0 0 60px;'>Debug Server <br/>connection failed</h3><br/><br/><br/><div class='network'><span id='network_progress' class='progress'><div></div></span><img class='img1' src='data:image/gif;base64,iVBORw0KGgoAAAANSUhEUgAAABgAAAAYCAYAAADgdz34AAAAGXRFWHRTb2Z0d2FyZQBBZG9iZSBJbWFnZVJlYWR5ccllPAAAArhJREFUeNq0Vb9PFEEU/nZ2F8jB8WsJoCRi1AoM0ZgrLlpZXYOFPYUx/g2YaCxMxBj/AqJW1FZoIbUWXiGRgkZNJEQPjHucnJC7/THje7Ps/ZDccXvRL3nZ2dn33ve+mTezhlIKjCdrHxxhmrcNGDMKykFCUBwozlVSbsoweHE3l3H1PBMsvX4/ZRjiKb2nh4eGYNs2YuKOCQwDfhCgVCqBSMpSysX7N7LbFn/0q9UF0zTTo6OjkKGPiu8BySUQicDwYBrFYjEd+v4CzS5pgtD3Lg2lHQSeh6mRfly9cBqTg6lE+Xf2D/HucwHfSwcYSKXguj8v87zQBIHspwWk6iVys9OJkzM4Jjd7hnKFUJQn9MM+ntcKaL143bRjf6+tn/ktFxuFfdYNtNmPMAwxd2oQ2XPjUSz5Sql0zhqBogm2GAF9zH8pgFV51Urbyk3LRv5TGZnpMVimoDzQxcb5IoIjVi5WV0Vj2vhaFe0QHDVESDmso1yx1QjAbGxGPVCFqu3SHEPsGudqUhAvkVF31jITnoXGXKotAaKO6gYtCJqrNUXE1KkCPsVxDMtv3APRtCQxK3XDlfOTTdW0M/blmKhYQ6uPa9MKTNPWk3ZPL35XfAz02bg+d1ZbEnCsrp5ILdOuK+hNDRyEzErjtY/bOKwGideeY96sb0X7RwSUs1JXIKwNZRhZVvJ1p4TltTKEEJ1vLJ8jOtGKTplp9UB69BRqveGqUCvkdnG/fJh26Ea1bLurDgp8H26xyMMy2Urtf8B4sPKWfzJ3yGbIRtAd9sg2yZ49XLjmNhG0AhEDtr865jhxT9JVXITyzPmT2IjkZALG4vNXq45T/4vulX7h8a3cfEdnpBXBo5f51cbLbmJ8ojbe/bEbtSA3Aim6dzPTksxquWF/tWrhW+GYj4TsXsG/gsB/xh8BBgBNmaa+X4668AAAAABJRU5ErkJggg=='/><img class='img2' src='data:image/gif;base64,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'/><img class='img3' src='data:image/gif;base64,iVBORw0KGgoAAAANSUhEUgAAABgAAAAYCAYAAADgdz34AAAAGXRFWHRTb2Z0d2FyZQBBZG9iZSBJbWFnZVJlYWR5ccllPAAAAgNJREFUeNrUVc1qU0EU/mYmSZu095o2BBrcuC4uVEofwkXpoishIKivoA/RR7AFIeADiAu3rkRBhS76Al00mySlaZufe2eOZ2YSzG16k9vQIh443OHOOd93vjlwjiAiTFrtCypC4hUENkGoYJ4JdkKLYY6hcXj6HK3E9SRB7TMe8mefPSg/APJ5lzyXIIqBszM+G3TJ4F1zFyfja5kI7qMuIwTVgME5ia7Ye7MdPR9bDQFlEIgI9UnIXKKYIZ6U1/kwAA62gO11ZLIfbeD1T6BcBNpXeDp5l1BAWq8oOKmZwa25WM1gnEdxvJxKIHTMDue3NWE8icVIfyLukCAsZC6PPEY6gTG+CrGgAjPCmEVgA/9nAn0HBPqfK2ASxyAWINBTCuS0AjNVRTYCujH3zggkZSHQ/omkULcn4Bk/n8CpiKGkxPdmdnAXa8HtqLm2XxJNVoWiaxJx4NuvOTghc3ptJ4OJOCweujGhlkrpBIXV6iXFnRXqdRH3xrI8w2pB4NOLEBcRYedjl5FHlSYqFhajn/5EeRwNimvIFUPGVT7ZSucwkVtyZPYsZd79d26by7E2p79cthi/UxXwPG8w5ONzqKCyEfqVOWqOYvy9b35iho9KMMO/TxHxE7X8Ju5yxY3UnWzt2Qe36N+wb7KvZexzh/2Y/f2vlzOW/n2YxD3bHwEGADkY+hRww0/JAAAAAElFTkSuQmCC'/></div><br/><br/><div style='line-height: 28px;'> 1. Does turned on the WIFI? <br/><div style='line-height: 20px;'> 2. Is device under same network with <br/>   Appspresso studio? <br/></div> 3. Please rebuild with Debug mode.</div><button>Retry</button></div></div></body></html>";

    private void sendDebugServerError(HttpResponse response) {

        response.setStatusCode(HttpStatus.SC_INTERNAL_SERVER_ERROR);
        EntityTemplate entity = new EntityTemplate(new ContentProducer() {

            @Override
            public void writeTo(OutputStream outstream) throws IOException {
                PrintStream out = new PrintStream(outstream);
                out.print(DEBUG_SERVER_ERROR_HTML);

            }
        });

        entity.setContentType("text/html");
        response.setEntity(entity);
    }
}
