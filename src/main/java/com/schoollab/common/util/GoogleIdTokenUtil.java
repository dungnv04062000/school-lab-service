package com.schoollab.common.util;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import org.springframework.beans.factory.annotation.Value;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Collections;

public class GoogleIdTokenUtil {

    private static String audience = "670750740225-hk97aiegel52utfitmm9r3ivqafadrpi.apps.googleusercontent.com";

    //lấy thông tin user từ idToken khi register
    public static GoogleIdToken.Payload verifingToken(String idToken) {
        GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(new NetHttpTransport(), new GsonFactory())
                // Specify the CLIENT_ID of the app that accesses the backend:
                .setAudience(Collections.singletonList(audience))
                // Or, if multiple clients access the backend:
                //.setAudience(Arrays.asList(CLIENT_ID_1, CLIENT_ID_2, CLIENT_ID_3))
                .build();

        // (Receive idTokenString by HTTPS POST)
        System.out.println("token " + idToken);

        GoogleIdToken idTokenVerified = null;
        try {
            idTokenVerified = verifier.verify(idToken);
        } catch (GeneralSecurityException | IOException e) {
            e.printStackTrace();
        }
        GoogleIdToken.Payload payload = null;
        if (idTokenVerified != null) {
            payload = idTokenVerified.getPayload();

            // Print user identifier
            String userId = payload.getSubject();
            System.out.println("User ID: " + userId);

            // Get profile information from payload
            String email = payload.getEmail();
            boolean emailVerified = payload.getEmailVerified();
            String name = (String) payload.get("name");
            String pictureUrl = (String) payload.get("picture");
            String locale = (String) payload.get("locale");
            String familyName = (String) payload.get("family_name");
            String givenName = (String) payload.get("given_name");
            System.out.println(email + " " + emailVerified + " " + name + " " + pictureUrl + " " + locale + " " + familyName + " " + givenName);

            // Use or store profile information
            // ...
            return payload;

        } else {
            System.out.println("Invalid ID token.");
            return null;
        }
    }
}
