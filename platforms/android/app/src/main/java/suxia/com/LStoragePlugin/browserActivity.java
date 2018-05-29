package suxia.com.LStoragePlugin;

import android.annotation.TargetApi;
import android.content.Intent;
import android.net.http.SslError;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.webkit.SslErrorHandler;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import java.net.MalformedURLException;
import java.net.URL;

public class browserActivity extends AppCompatActivity {

    private  String localStorageFileName;

    @Override
    public void onBackPressed() {

        super.onBackPressed();

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_browser2);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
                Intent intent=new Intent();
                intent.putExtra("localStorageName",localStorageFileName);
                setResult(RESULT_OK,intent);
                finish();
            }
        });

        Intent intent=getIntent();
        String url=intent.getStringExtra("url");
        WebView wbBrowser=(WebView)findViewById(R.id.wbBrowser);

        WebSettings wSet = wbBrowser.getSettings();
        wSet.setJavaScriptEnabled(true);
        wSet.setDatabaseEnabled(true);
        wSet.setAllowFileAccess(true);
        wSet.setAllowContentAccess(true);
        wbBrowser.setFocusable(true);
        wbBrowser.setFocusableInTouchMode(true);
        wSet.setJavaScriptEnabled(true);
        //mWebview.getSettings().setPluginsEnabled(true);
        //mWebview.getSettings().setRenderPriority(WebSettings.RenderPriority.HIGH);
        wSet.setCacheMode(WebSettings.LOAD_NO_CACHE);
        wSet.setDomStorageEnabled(true);
        wSet.setDatabaseEnabled(true);
        wSet.setAppCacheEnabled(true);

        //如果不设置个属性，webview不支持localstorage
        wSet.setDomStorageEnabled(true);

        wbBrowser.loadUrl(url);
        wbBrowser.setWebViewClient(new WebViewClient(){
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                Log.e("URL", "shouldoverrideurlloading:----"+url.toString());
                view.loadUrl(url);
                return true;
            }

            @TargetApi(Build.VERSION_CODES.LOLLIPOP)
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                Log.i("URL", "wk,zoumeiyzou a -----:"+request.toString());
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    String currentUrl=request.getUrl().toString();
                    try {
                        URL url=new URL(currentUrl);
                        String protocal=url.getProtocol();
                        int port=url.getPort();
                        String host=url.getHost();
                        if(port<0){
                            port=0;
                        }
                        localStorageFileName=protocal+"_"+host+"_"+port+".localstorage";
                        Log.i("URL", "currentUrl -----:"+currentUrl);
                        Log.i("URL", "localStorageFileName -----:"+localStorageFileName);
                        view.loadUrl(request.getUrl().toString());

                    } catch (MalformedURLException e) {
                        e.printStackTrace();
                    }
                } else {
                    view.loadUrl(request.toString());
                }
                return true;
            }




            @Override
            public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
                Log.i("URL", "有错误啊 -----:"+error.toString());
                //super.onReceivedSslError(view, handler, error);
                handler.proceed();
            }
        });
    }

}
