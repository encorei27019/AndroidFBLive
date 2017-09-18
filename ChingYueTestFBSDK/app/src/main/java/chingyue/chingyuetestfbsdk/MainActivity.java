package chingyue.chingyuetestfbsdk;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.Profile;
import com.facebook.login.LoginBehavior;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;


public class MainActivity extends AppCompatActivity {
    CallbackManager callbackManager;
    AccessToken accessToken;
    TextView mTextView ;
    public String stream_url;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        FacebookSdk.sdkInitialize(getApplicationContext());
        super.onCreate(savedInstanceState);
        LoginManager.getInstance().logOut();
        setContentView(R.layout.activity_main);
        callbackManager = CallbackManager.Factory.create();
        mTextView = (TextView)findViewById(R.id.text);
        Button liveButton = (Button)findViewById(R.id.live_button);
        LoginButton loginButton = (LoginButton) findViewById(R.id.login_button);
        loginButton.setReadPermissions("public_profile","email");
        loginButton.setLoginBehavior(LoginBehavior.WEB_VIEW_ONLY);
        LoginManager.getInstance().logInWithReadPermissions(this,Arrays.asList("email"));
        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                accessToken = loginResult.getAccessToken();
                Log.d("FB","access token got.");
                GraphRequest request = null;
                try {
                    request = GraphRequest.newPostRequest(
                            accessToken,
                            "/me/live_videos",
                            new JSONObject("{}"),
                            new GraphRequest.Callback(){
                                @Override
                                public void onCompleted(GraphResponse response){
                                    Log.d("FB","complete");
                                    response.getConnection();
                                    Log.i("test","SOS::"+response);
                                    if(response != null){
                                        JSONObject object = response.getJSONObject();
                                        Log.d("FB",object.optString("id"));
                                        Log.d("FB",object.optString("stream_url"));
                                        stream_url = object.optString("stream_url");
                                    }
                                    //Log.d("FB",response.getRequest(ttt));
                                    //Log.d("FB",object.optString("email"));
                                    //Log.d("FB",object.optString("link"));
                                    //Log.d("FB",object.optString("id"));
                                    String name = Profile.getCurrentProfile().getName();
                                    mTextView.setText(name);
                                }
                            }
                    );
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                //Bundle parameters = new Bundle();
                //parameters.putString("fields","id,name,link");
                //request.setParameters(parameters);
                request.executeAsync();
            }
            @Override
            public void onCancel() {
                Log.d("FB","CANCEL");
            }
            @Override
            public void onError(FacebookException exception) {
                Log.d("FB","ERROR");
            }
        });

        liveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startNextPage();
            }
        });

    }
    @Override
    protected void onActivityResult(int requestCode,int resultCode,Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    private void startNextPage(){
        Intent intent = new Intent();
        intent.putExtra("rtmp" , stream_url); //將值(名稱="rtmp"，值=stream_url)傳送到PlayActivity
        intent.setClass(this , PlayActivity.class);
        startActivity(intent);
    }

}
