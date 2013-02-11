/**
 * User is used to determine authentication status for the client. Applications should
 * interact with the User object using the Simperium object's methods:
 *
 *     simperium.createUser( ... ); // register new user
 *     simperium.authorizeUser( ... ); // authorizes an existing user
 *
 * Applications can provide a User.AuthenticationListener to the Simperium object to 
 * detect a change in the user's status.
 */
package com.simperium;

import android.util.Log;

import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONObject;
import org.json.JSONException;
import org.apache.http.entity.StringEntity;
import org.apache.http.HttpEntity;

import java.util.HashMap;
import java.util.Map;

import java.io.UnsupportedEncodingException;


public class User {
    /**
     * Applications can register a global authentication listener to get notified when a user's
     * authenticated status has changed.
     *
     *     Simperium simperium = new Simperium(appId, appSecret, appContext, new User.AuthenticationListener(){
     *       public void onAuthenticationStatusChange(User.AuthenticationStatus status){
     *          // Prompt user to log in or show they're offline
     *       }
     *     });
     */
    public interface AuthenticationListener {
        void onAuthenticationStatusChange(AuthenticationStatus authorized);
    }
    /**
     * Determines a user's network status with Simperium:
     *   - AUTHENTICATED: user has an access token and is connected to Simperium
     *   - NOT_AUTHENTICATED: user does not have a valid access token. Create or auth the user
     *   - UKNOWN: User objects start in this state and then transitions to AUTHENTICATED or
     *             NOT_AUTHENTICATED. Also the state given when the Simperium is not reachable.
     */
    public enum AuthenticationStatus {
        AUTHENTICATED, NOT_AUTHENTICATED, UNKNOWN
    }
    /**
     * For use with Simperium.createUser and Simperium.authorizeUser
     */
    public interface AuthResponseHandler {
        public void onSuccess(User user);
        public void onInvalid(User user, Throwable error, JSONObject validationErrors);
        public void onFailure(User user, Throwable error, String message);
    }

    public static final String USERNAME_KEY = "username";
    public static final String ACCESS_TOKEN_KEY = "access_token";
    public static final String PASSWORD_KEY = "password";
    public static final String USERID_KEY = "userid";
    
    private String email;
    private String password;
    private String userId;
    private String accessToken;
    private AuthenticationStatus authenticationStatus = AuthenticationStatus.UNKNOWN;
    private AuthenticationListener listener;
    
    // a user that hasn't been logged in
    protected User(AuthenticationListener listener){
        this(null, null, listener);
    }
    
    protected User(String email, AuthenticationListener listener){
        this(email, null, listener);
    }
    
    protected User(String email, String password, AuthenticationListener listener){
        this.email = email;
        this.password = password;
        this.listener = listener;
    }
    
    public AuthenticationStatus getAuthenticationStatus(){
        return authenticationStatus;
    }
    
    protected void setAuthenticationStatus(AuthenticationStatus authenticationStatus){
        if (this.authenticationStatus != authenticationStatus) {
            this.authenticationStatus = authenticationStatus;
            listener.onAuthenticationStatusChange(this.authenticationStatus);
        }
    }
    
    // check if we have an access token
    public boolean needsAuthentication(){
        return accessToken == null;
    }
    
    public boolean hasAccessToken(){
        return accessToken != null;
    }
    
    protected void setCredentials(String email, String password){
        setEmail(email);
        setPassword(password);
    }
    
    public String getEmail(){
        return email;
    }
    
    protected void setEmail(String email){
        this.email = email;
    }
    
    protected void setPassword(String password){
        this.password = password;
    }
    
    
    public String getUserId(){
        return userId;
    }
    
    public String getAccessToken(){
        return accessToken;
    }
    
    protected void setAccessToken(String token){
        this.accessToken = token;
    }
    
    public String toString(){
        return toJSONString();
    }
    
    public String toJSONString(){
        return new JSONObject(toMap()).toString();
    }
    
    private Map<String,String> toMap(){
        HashMap<String,String> fields = new HashMap<String,String>();
        fields.put(USERNAME_KEY, email);
        fields.put(PASSWORD_KEY, password);
        return fields;
    }
    
    public HttpEntity toHttpEntity(){
        StringEntity entity;
        JSONObject json = new JSONObject(toMap());
        try{
            entity = new StringEntity(json.toString());
        } catch(UnsupportedEncodingException e){
            entity = null;
        }
        return entity;
        
    }
    
    protected AsyncHttpResponseHandler getCreateResponseHandler(final User.AuthResponseHandler handler){
        // returns an AsyncHttpResponseHandlert
        final User user = this;
        return new JsonHttpResponseHandler(){
            @Override
            public void onSuccess(int statusCode, JSONObject response){
                // parse the response to JSON
                Simperium.log(String.format("Success: %s", response));
                // user was created, notify of a new user
                try {
                    userId = response.getString(USERID_KEY);
                    accessToken = response.getString(ACCESS_TOKEN_KEY);
                } catch(JSONException error){
                    handler.onFailure(user, error, response.toString());
                    return;
                }
                setAuthenticationStatus(AuthenticationStatus.AUTHENTICATED);
                handler.onSuccess(user);
            }
            @Override
            public void onFailure(Throwable error, JSONObject response){
                Simperium.log(String.format("Error: %s", error));
                Simperium.log(String.format("Reponse: %s", response));
                handler.onInvalid(user, error, response);
            }
            @Override
            public void onFailure(Throwable error, String response){
                Simperium.log(String.format("Error: %s", error));
                Simperium.log(String.format("Reponse: %s", response));
                handler.onFailure(user, error, response);
            }
        };
    }
    
    protected AsyncHttpResponseHandler getAuthorizeResponseHandler(final User.AuthResponseHandler handler){
        final User user = this;
        return new JsonHttpResponseHandler(){
            @Override
            public void onSuccess(int statusCode, JSONObject response){
                // parse the response to JSON
                Simperium.log(String.format("Success: %s", response));
                // user was created, notify of a new user
                try {
                    userId = response.getString(USERID_KEY);
                    accessToken = response.getString(ACCESS_TOKEN_KEY);
                } catch(JSONException error){
                    handler.onFailure(user, error, response.toString());
                    return;
                }
                setAuthenticationStatus(AuthenticationStatus.AUTHENTICATED);
                handler.onSuccess(user);
            }
            @Override
            public void onFailure(Throwable error, JSONObject response){
                Simperium.log(String.format("Error: %s", error));
                Simperium.log(String.format("Reponse: %s", response));
                handler.onInvalid(user, error, response);
            }
            @Override
            public void onFailure(Throwable error, String response){
                Simperium.log(String.format("Error: %s", error));
                Simperium.log(String.format("Reponse: %s", response));
                handler.onFailure(user, error, response);
            }
        };
    }
    
    protected AsyncHttpResponseHandler getUpdateResponseHandler(final User.AuthResponseHandler handler){
        final User user = this;
        return new JsonHttpResponseHandler(){
            @Override
            public void onSuccess(int statusCode, String response){
            }
            @Override
            public void onFailure(Throwable error, JSONObject response){
            }
            @Override
            public void onFailure(Throwable error, String response){
            }
        };
    }            
}