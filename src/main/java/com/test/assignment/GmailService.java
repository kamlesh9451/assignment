package com.test.assignment;

import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.gmail.Gmail;
import com.google.api.services.gmail.GmailScopes;

import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Collections;

@Service
public class GmailService {

    private static final String APPLICATION_NAME = "EmailCountAPI";
    private static final GsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();
    private static Gmail gmailService;

    public Gmail getGmailService() throws Exception {
        if (gmailService != null) return gmailService;

        InputStream in = getClass().getResourceAsStream("/credentials.json");
        GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(in));

        var httpTransport = GoogleNetHttpTransport.newTrustedTransport();

        GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
                httpTransport, JSON_FACTORY, clientSecrets, Collections.singleton(GmailScopes.GMAIL_READONLY))
                .setDataStoreFactory(new FileDataStoreFactory(new java.io.File("tokens")))
                .setAccessType("offline")
                .build();

        var credential = new com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp(
                flow, new com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver())
                .authorize("user");

        gmailService = new Gmail.Builder(httpTransport, JSON_FACTORY, credential)
                .setApplicationName(APPLICATION_NAME)
                .build();

        return gmailService;
    }

    public int countEmailsFrom(String senderEmail) throws Exception {
        Gmail gmail = getGmailService();

        String query = "from:" + senderEmail;

        var response = gmail.users().messages().list("me").setQ(query).execute();

        if (response.getMessages() == null)
            return 0;

        return response.getMessages().size();
    }
}
