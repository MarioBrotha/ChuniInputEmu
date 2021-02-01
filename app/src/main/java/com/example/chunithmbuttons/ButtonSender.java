package com.example.chunithmbuttons;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.IBinder;

import androidx.annotation.Nullable;
import androidx.preference.PreferenceManager;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Arrays;

public class ButtonSender extends Service
{
    Socket socket;
    DataOutputStream dos;
    PrintWriter pw;

    String ipAddress = "127.0.0.1";
    Boolean[] notesArray;

    private Thread messageThread;

    public ButtonSender()
    {
        notesArray = new Boolean[18];
        Arrays.fill(notesArray,false);
    }

    @Override
    public void onCreate()
    {
        super.onCreate();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public void SetPreferencesAndStart()
    {
        ipAddress = MainActivity.sharedPref.getString(SettingsActivity.KEY_PREF_IPADDRESS, "test");
        startSendingMessage();
    }

    public void playNote(int note)
    {
        if (!notesArray[note-1])
            notesArray[note-1] = true;
    }

    public void stopNote (int note)
    {
        if (notesArray[note-1])
            notesArray[note-1] = false;
    }

    public boolean isNotePressed (int note)
    {
        return (notesArray[note-1]);
    }

    private void startSendingMessage()
    {
        messageThread = new Thread(() -> {
            try
            {
                socket = new Socket(ipAddress, 7800);
                pw = new PrintWriter(socket.getOutputStream(), true);
                while(true)
                {
                    for (int i = 0; i < notesArray.length; i++)
                    {
                        pw.write(notesArray[i] ? "1" : "0");
                    }
                    pw.println();
                }
            }
            //catch (IOException | InterruptedException e)
            catch (IOException e)
            {
               //
            }
        });

        messageThread.start();
    }
}