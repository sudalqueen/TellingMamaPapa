package com.example.sudal.tellingmamapapa;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.firebase.client.Firebase;
import com.firebase.client.Query;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.kakao.auth.ErrorCode;
import com.kakao.auth.ISessionCallback;
import com.kakao.auth.Session;
import com.kakao.network.ErrorResult;
import com.kakao.usermgmt.UserManagement;
import com.kakao.usermgmt.callback.MeResponseCallback;
import com.kakao.usermgmt.response.model.UserProfile;
import com.kakao.util.exception.KakaoException;
import com.nhn.android.naverlogin.OAuthLogin;
import com.nhn.android.naverlogin.OAuthLoginHandler;
import com.nhn.android.naverlogin.ui.view.OAuthLoginButton;

import org.json.JSONObject;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

import static com.nhn.android.naverlogin.OAuthLogin.mOAuthLoginHandler;

public class LoginActivity extends AppCompatActivity {
    public static final String FIREBASE_POST_URL = "https://tellingmamapapa.firebaseio.com/tellingmamapapa";
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    private Query mRef;

    final static int MY_PERMISSIONS_REQUEST_READ_CONTACTS = 0;
    final static int MY_PERMISSIONS_REQUEST_READ_STORAGE = 1;
    final static int MY_PERMISSIONS_REQUEST_WRITE_STORAGE = 2;

    SessionCallback callback;

    OAuthLogin mOAuthLoginModule;
    OAuthLoginButton authLoginButton;
    Context mActivity;
    public static OAuthLogin mOAuthLoginInstance;
    public Map<String, String> mUserInfoMap;
    String TAG = "TAG";

    Users users;

    SharedPreferences sp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mActivity = this;

        Firebase.setAndroidContext(this);
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();

        checkPermission(LoginActivity.this);

        sp = getSharedPreferences("login", Activity.MODE_PRIVATE);

        callback = new SessionCallback();
        Session.getCurrentSession().addCallback(callback);

        authLoginButton = (OAuthLoginButton) findViewById(R.id.buttonOAuthLoginImg);
        mOAuthLoginInstance = OAuthLogin.getInstance();
        mOAuthLoginInstance.init(
                LoginActivity.this
                , "peui2PNZcPRCZ9EKHrkG"
                , "a6J0EyubwY"
                , "TellingMamaPapa"
        );
        authLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mOAuthLoginInstance.startOauthLoginActivity(LoginActivity.this, mOAuthLoginHandler);
            }
        });
    }

    /**
     * startOAuthLoginActivity() 호출시 인자로 넘기거나, OAuthLoginButton 에 등록해주면 인증이 종료되는 걸 알 수 있다.
     */
    private OAuthLoginHandler mOAuthLoginHandler = new OAuthLoginHandler() {
        @Override
        public void run(boolean success) {
            if (success) {
                String accessToken = mOAuthLoginInstance.getAccessToken(mActivity);
                String refreshToken = mOAuthLoginInstance.getRefreshToken(mActivity);
                long expiresAt = mOAuthLoginInstance.getExpiresAt(mActivity);
                String tokenType = mOAuthLoginInstance.getTokenType(mActivity);
                Log.d(TAG, accessToken);
                Log.d(TAG, refreshToken);
                Log.d(TAG, String.valueOf(expiresAt));
                Log.d(TAG, tokenType);
                Log.d(TAG, mOAuthLoginInstance.getState(mActivity).toString());
                new RequestApiTask().execute();
            } else {
                String errorCode = mOAuthLoginInstance.getLastErrorCode(mActivity).getCode();
                String errorDesc = mOAuthLoginInstance.getLastErrorDesc(mActivity);
                Toast.makeText(mActivity, "errorCode:" + errorCode + ", errorDesc:" + errorDesc, Toast.LENGTH_SHORT).show();
            }
        }

        ;
    };


    private class RequestApiTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {

        }

        @Override
        protected Void doInBackground(Void... params) {
            String url = "https://openapi.naver.com/v1/nid/getUserProfile.xml";
            String at = mOAuthLoginInstance.getAccessToken(mActivity);
            mUserInfoMap = requestNaverUserInfo(mOAuthLoginInstance.requestApi(mActivity, at, url));
            startActivity(new Intent(LoginActivity.this, MainActivity.class));
            return null;
        }

        protected void onPostExecute(Void content) {
            if (mUserInfoMap.get("email") == null) {
                Toast.makeText(mActivity, "로그인 실패하였습니다.  잠시후 다시 시도해 주세요!!", Toast.LENGTH_SHORT).show();
            } else {
                Log.d(TAG, String.valueOf(mUserInfoMap));

            }

        }
    }

    private Map<String, String> requestNaverUserInfo(String data) { // xml 파싱
        String f_array[] = new String[9];

        try {
            XmlPullParserFactory parserCreator = XmlPullParserFactory
                    .newInstance();
            XmlPullParser parser = parserCreator.newPullParser();
            InputStream input = new ByteArrayInputStream(
                    data.getBytes("UTF-8"));
            parser.setInput(input, "UTF-8");

            int parserEvent = parser.getEventType();
            String tag;
            boolean inText = false;
            boolean lastMatTag = false;

            int colIdx = 0;

            while (parserEvent != XmlPullParser.END_DOCUMENT) {
                switch (parserEvent) {
                    case XmlPullParser.START_TAG:
                        tag = parser.getName();
                        if (tag.compareTo("xml") == 0) {
                            inText = false;
                        } else if (tag.compareTo("data") == 0) {
                            inText = false;
                        } else if (tag.compareTo("result") == 0) {
                            inText = false;
                        } else if (tag.compareTo("resultcode") == 0) {
                            inText = false;
                        } else if (tag.compareTo("message") == 0) {
                            inText = false;
                        } else if (tag.compareTo("response") == 0) {
                            inText = false;
                        } else {
                            inText = true;

                        }
                        break;
                    case XmlPullParser.TEXT:
                        tag = parser.getName();
                        if (inText) {
                            if (parser.getText() == null) {
                                f_array[colIdx] = "";
                            } else {
                                f_array[colIdx] = parser.getText().trim();
                            }

                            colIdx++;
                        }
                        inText = false;
                        break;
                    case XmlPullParser.END_TAG:
                        tag = parser.getName();
                        inText = false;
                        break;

                }

                parserEvent = parser.next();
            }
        } catch (Exception e) {
            Log.e("dd", "Error in network call", e);
        }
        for (int i = 0; i < f_array.length; i++) {
            System.out.println(i + ":" + f_array[i]);
        }

        Map<String, String> resultMap = new HashMap<>();
        resultMap.put("nickname", f_array[1]);
        resultMap.put("enc_id", f_array[0]);
        resultMap.put("profile_image", f_array[2]);
        resultMap.put("email", f_array[3]);
        resultMap.put("name", f_array[4]);

        StringTokenizer st = new StringTokenizer(f_array[3], ".");
        String id = st.nextToken();
        StringTokenizer st2 = new StringTokenizer(id,"@");
        String realId = st2.nextToken();

        users = new Users(realId, f_array[3], f_array[4]);
        databaseReference.child("Users").child(id).setValue(users);

        return resultMap;
    }


    private class SessionCallback implements ISessionCallback {

        @Override
        public void onSessionOpened() {

            UserManagement.requestMe(new MeResponseCallback() {

                @Override
                public void onFailure(ErrorResult errorResult) {
                    String message = "failed to get user info. msg=" + errorResult;

                    ErrorCode result = ErrorCode.valueOf(errorResult.getErrorCode());
                    if (result == ErrorCode.CLIENT_ERROR_CODE) {
                        //에러로 인한 로그인 실패
//                        finish();
                    } else {
                        //redirectMainActivity();
                    }
                }

                @Override
                public void onSessionClosed(ErrorResult errorResult) {
                }

                @Override
                public void onNotSignedUp() {

                }

                @Override
                public void onSuccess(UserProfile userProfile) {
                    //로그인에 성공하면 로그인한 사용자의 일련번호, 닉네임, 이미지url등을 리턴합니다.
                    //사용자 ID는 보안상의 문제로 제공하지 않고 일련번호는 제공합니다.

                    Log.e("UserProfile", userProfile.toString());
                    Log.e("UserProfile", userProfile.getId() + "");


                    long number = userProfile.getId();

                    startActivity(new Intent(LoginActivity.this, MainActivity.class));
                }
            });

        }

        @Override
        public void onSessionOpenFailed(KakaoException exception) {
            // 세션 연결이 실패했을때
            // 어쩔때 실패되는지는 테스트를 안해보았음 ㅜㅜ

        }
    }

    public void checkPermission(Activity activity) {
        if (ContextCompat.checkSelfPermission(activity, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(activity,
                    Manifest.permission.READ_EXTERNAL_STORAGE)) {
            } else {
                ActivityCompat.requestPermissions(activity,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        MY_PERMISSIONS_REQUEST_READ_STORAGE);
            }
        }
        if (ContextCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(activity,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            } else {
                ActivityCompat.requestPermissions(activity,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        MY_PERMISSIONS_REQUEST_WRITE_STORAGE);
            }
        }
        if (ContextCompat.checkSelfPermission(LoginActivity.this,
                Manifest.permission.RECORD_AUDIO)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(LoginActivity.this,
                    new String[]{Manifest.permission.RECORD_AUDIO},
                    1234);
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_READ_CONTACTS: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                } else {
                    Log.d("TAG", "permission denied by user");
                }
                return;
            }
            case MY_PERMISSIONS_REQUEST_READ_STORAGE: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                } else {
                    Log.d("TAG", "permission denied by user");
                }
            }
            case MY_PERMISSIONS_REQUEST_WRITE_STORAGE: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                } else {
                    Log.d("TAG", "permission denied by user");
                }
            }
        }
    }

}
