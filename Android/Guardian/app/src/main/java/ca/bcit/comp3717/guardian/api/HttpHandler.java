package ca.bcit.comp3717.guardian.api;

import android.util.Base64;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.ProtocolException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import ca.bcit.comp3717.guardian.model.LinkedUser;
import ca.bcit.comp3717.guardian.model.User;
import ca.bcit.comp3717.guardian.util.UserValidation;

public class HttpHandler {

    private static String TAG = HttpHandler.class.getSimpleName();
    public HttpHandler() {}

    public static class UserController {

        private static final String URL_CreateUser = "http://guardiannewwestapi.azurewebsites.net/user/create";
        private static final String URL_GetUserByEmail = "http://guardiannewwestapi.azurewebsites.net/user/get";
        private static final String URL_GetUserById = "http://guardiannewwestapi.azurewebsites.net/user/getbyid";
        private static final String URL_DeleteUserByEmail = "http://guardiannewwestapi.azurewebsites.net/user/delete";
        private static final String URL_LoginUserByEmail = "http://guardiannewwestapi.azurewebsites.net/user/login";
        private static final String URL_LogoutUserByEmail = "http://guardiannewwestapi.azurewebsites.net/user/logout";

        public static User createUser(String userName, String password, String email, String phone) {
            User user = null;

            try {
                HttpURLConnection conn = openConnection(URL_CreateUser);
                HttpHandler.setConnRequestProperties(conn, email, password, userName, phone);

                if (conn.getResponseCode() != 200) {
                    Log.e(TAG, conn.getResponseCode() + "");

                } else {
                    InputStream in = new BufferedInputStream(conn.getInputStream());
                    String response = HttpHandler.convertStreamToString(in);

                    if (UserValidation.createUserAccountValidation(response)) {
                        user = new User();
                        user.setUserName(userName);
                        user.setEmail(email);
                    }
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
            return user;
        }

        public static User getUserByEmail(String email, String password) {
            User user = null;

            try {
                HttpURLConnection conn = openConnection(URL_GetUserByEmail);
                HttpHandler.setConnRequestProperties(conn, email, password);

                if (conn.getResponseCode() != 200) {
                    Log.e(TAG, "getUserByEmail() response code: " + conn.getResponseCode());

                } else {
                    InputStream in = new BufferedInputStream(conn.getInputStream());
                    String response = HttpHandler.convertStreamToString(in);
                    user = HttpHandler.convertResponseToUser(response);
                }

            } catch (IOException e) {
                e.printStackTrace();
                Log.e(TAG, "ERROR in getUserByEmail(): " + e.getMessage());
            }
            return user;
        }

        public static User getUserById(String email, String password, int userId) {
            User user = null;

            try {
                HttpURLConnection conn = openConnection(URL_GetUserById);
                HttpHandler.setConnRequestProperties(conn, email, password, userId);

                if (conn.getResponseCode() != 200) {
                    Log.e(TAG, "getUserById() response code: " + conn.getResponseCode());

                } else {
                    InputStream in = new BufferedInputStream(conn.getInputStream());
                    String response = HttpHandler.convertStreamToString(in);
                    user = HttpHandler.convertResponseToUser(response);
                }

            } catch (IOException e) {
                e.printStackTrace();
                Log.e(TAG, "ERROR in getUserById(): " + e.getMessage());
            }
            return user;
        }

        public static void deleteUserById(String email, String password, int userId) {
            String response = "";

            try {
                HttpURLConnection conn = openConnection(URL_DeleteUserByEmail);
                HttpHandler.setConnRequestProperties(conn, email, password, userId);

                if (conn.getResponseCode() != 200) {
                    Log.e(TAG, conn.getResponseCode() + "");

                } else {
                    InputStream in = new BufferedInputStream(conn.getInputStream());
                    response = HttpHandler.convertStreamToString(in);
                }

            } catch (IOException e) {
                e.printStackTrace();
                Log.e(TAG, "ERROR in deleteUserById(): " + e.getMessage());
            }
        }

        public static User loginByEmail(String email, String password) {
            User user = null;

            try {
                HttpURLConnection conn = openConnection(URL_LoginUserByEmail);
                HttpHandler.setConnRequestProperties(conn, email, password);

                if (conn.getResponseCode() != 200) {
                    Log.e(TAG, "loginByEmail() response code: " + conn.getResponseCode());

                } else {
                    InputStream in = new BufferedInputStream(conn.getInputStream());
                    String response = HttpHandler.convertStreamToString(in);
                    boolean loginSuccess = UserValidation.validateUserLogout(response);

                    if (loginSuccess) {
                        user = HttpHandler.convertResponseToUser(response);
                        user.setPassword(password);
                    }
                }

            } catch (IOException e) {
                e.printStackTrace();
                Log.e(TAG, "ERROR in loginByEmail(): " + e.getMessage());
            }
            return user;
        }

        public static void logoutByEmail(String email, String password) {
            User user = null;

            try {
                HttpURLConnection conn = openConnection(URL_LogoutUserByEmail);
                HttpHandler.setConnRequestProperties(conn, email, password);

                if (conn.getResponseCode() != 200) {
                    Log.e(TAG, "logoutByEmail() response code: " + conn.getResponseCode());

                } else {
                    InputStream in = new BufferedInputStream(conn.getInputStream());
                    boolean logoutSuccess = UserValidation.validateUserLogout(HttpHandler.convertStreamToString(in));

                    if (logoutSuccess) {
                        Log.i(TAG, "logoutByEmail() response: successfully logged out " + email);
                    } else {
                        Log.i(TAG, "logoutByEmail() response: failed to logout " + email);
                    }
                }

            } catch (IOException e) {
                e.printStackTrace();
                Log.e(TAG, "ERROR in logoutByEmail(): " + e.getMessage());
            }
        }
    }

    public static class LinkedUserController {

        private static final String URL_GetLinkedUsersById = "http://guardiannewwestapi.azurewebsites.net/linkeduser/get/all";

        public static List<LinkedUser> getLinkedUsersById(String email, String password, int userId) {
            List<LinkedUser> linkedUsersList = null;

            try {
                HttpURLConnection conn = openConnection(URL_GetLinkedUsersById);
                HttpHandler.setConnRequestProperties(conn, email, password, userId);

                if (conn.getResponseCode() != 200) {
                    Log.e(TAG, "getLinkedUsersById() response code: " + conn.getResponseCode());

                } else {
                    InputStream in = new BufferedInputStream(conn.getInputStream());
                    String response = HttpHandler.convertStreamToString(in);
                    linkedUsersList = HttpHandler.convertResponseToLinkedUserList(response);
                }

            } catch (IOException e) {
                e.printStackTrace();
                Log.e(TAG, "ERROR in getLinkedUsersById(): " + e.getMessage());
            }
            return linkedUsersList;
        }
    }

    private static HttpURLConnection openConnection(String urlString) throws IOException {
        URL url = new URL(urlString);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        return conn;
    }

    private static void setConnRequestProperties(HttpURLConnection conn, String email,
                                                 String password) {
        try {
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestProperty("Authorization", getB64Auth(email, password));
            conn.setRequestProperty("Email", email);
            conn.setRequestMethod("POST");

        } catch (ProtocolException e) {
            e.printStackTrace();
        }
    }

    private static void setConnRequestProperties(HttpURLConnection conn, String email,
                                                 String password, int userId) {
        try {
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestProperty("Authorization", getB64Auth(email, password));
            conn.setRequestProperty("UserId", userId + "");
            conn.setRequestMethod("POST");

        } catch (ProtocolException e) {
            e.printStackTrace();
        }
    }

    private static void setConnRequestProperties(HttpURLConnection conn, String email,
                                                 String password, String userName, String phone) {
        try {
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestProperty("UserName", userName);
            conn.setRequestProperty("Password", password);
            conn.setRequestProperty("Email", email);
            conn.setRequestProperty("Phone", phone);
            conn.setRequestMethod("POST");

        } catch (ProtocolException e) {
            e.printStackTrace();
        }
    }

    private static String getB64Auth(String login, String pass) {
        String source = login + ":" + pass;
        String formatted = "Basic " + Base64.encodeToString(source.getBytes(),
                Base64.URL_SAFE | Base64.NO_WRAP);
        return formatted;
    }

    private static String convertStreamToString(InputStream is) {
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();

        String line;
        try {
            while ((line = reader.readLine()) != null) {
                sb.append(line).append('\n');
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return sb.toString();
    }

    private static User convertResponseToUser(String response) {
        User user = null;

        try {
            JSONObject responseObj = new JSONObject(response);
            JSONObject userObj = responseObj.getJSONObject("user");
            user = new User();

            user.setId(userObj.getInt("ID"));
            user.setUserName(userObj.getString("UserName"));
            user.setEmail(userObj.getString("Email"));
            user.setPassword(userObj.getString("Password"));
            user.setPhone(userObj.getString("Phone"));
            user.setLogin(userObj.getBoolean("Login"));
            user.setLastLogin(userObj.getString("LastLogin"));
            user.setStatus(userObj.getInt("Status"));
            user.setDeleted(userObj.getBoolean("Deleted"));

        } catch (JSONException e) {
            e.printStackTrace();
            Log.e(TAG, "ERROR in convertResponseToUser(): " + e.getMessage());
            return null;
        }
        return user;
    }

    private static List<LinkedUser> convertResponseToLinkedUserList(String response) {
        List<LinkedUser> linkedUserList = null;

        try {
            JSONObject responseObj = new JSONObject(response);
            JSONArray jsonArrLinkedUsers = responseObj.getJSONArray("linkedUsers");

            if (jsonArrLinkedUsers.length() > 0) {
                linkedUserList = new ArrayList<>();
            }

            for (int i = 0; i < jsonArrLinkedUsers.length(); i++) {
                JSONObject jsonObjLinkedUser = jsonArrLinkedUsers.getJSONObject(i);
                LinkedUser linkedUser = new LinkedUser();

                linkedUser.setUserIdMe(jsonObjLinkedUser.getInt("UserIDMe"));
                linkedUser.setUserIdTarget(jsonObjLinkedUser.getInt("UserIDTarget"));
                linkedUser.setAlertMe(jsonObjLinkedUser.getBoolean("AlertMe"));
                linkedUser.setAlertTarget(jsonObjLinkedUser.getBoolean("AlertTarget"));
                linkedUser.setMuteMe(jsonObjLinkedUser.getBoolean("MuteMe"));
                linkedUser.setMuteTarget(jsonObjLinkedUser.getBoolean("MuteTarget"));
                linkedUser.setDeleted(jsonObjLinkedUser.getBoolean("Deleted"));
                linkedUser.setAddedMe(jsonObjLinkedUser.getBoolean("AddedMe"));
                linkedUser.setAddedTarget(jsonObjLinkedUser.getBoolean("AddedTarget"));

                linkedUserList.add(linkedUser);
            }

        } catch (JSONException e) {
            e.printStackTrace();
            Log.e(TAG, "ERROR in convertResponseToLinkedUserList(): " + e.getMessage());
        }
        return linkedUserList;
    }

    public static String makeServiceCall(String reqUrl) {
        String response = null;
        try {
            HttpURLConnection conn = openConnection(reqUrl);
            conn.setRequestMethod("GET");

            // read the response
            InputStream in = new BufferedInputStream(conn.getInputStream());
            response = convertStreamToString(in);

        } catch (IOException e) {
            Log.e(TAG, "IOException: " + e.getMessage());
        }
        return response;
    }
}