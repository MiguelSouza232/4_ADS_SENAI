package br.edu.unisenai.chefdebolso;

import android.app.Application;

import com.google.firebase.FirebaseApp;
import com.google.firebase.appcheck.FirebaseAppCheck;
import com.google.firebase.appcheck.debug.DebugAppCheckProviderFactory;

/**
 * Primeira classe que roda quando o app abre, antes de qualquer Activity.
 * Está registrada no AndroidManifest em android:name=".ChefApp".
 */
public class ChefApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        FirebaseApp.initializeApp(this);

        if (BuildConfig.DEBUG) {
            // Precisa colocar o TOKEN no Firebase e na  primeira execução ele imprime no Logcat com o texto abaixo
            //   "Enter this debug secret into the allow list ... : <token>"
            // Filtre no Logcat por DebugAppCheckProvider, copie o token e cadastre
            // no console: Security > App Check > Apps > (menu do app) > Manage debug tokens.
            FirebaseAppCheck.getInstance().installAppCheckProviderFactory(
                    DebugAppCheckProviderFactory.getInstance());
        }
    }
}
