package com.example.echolauncher;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import androidx.annotation.Nullable;

import java.util.List;
import java.util.Map;

public class HomeScreenService extends Service {
    @Override
    public int onStartCommand(Intent intent, int flags, int startID) {
        return onStartCommand(intent, flags, startID);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

//    public void add(int position, int screen, String identifier) {
//        storedItems.put(position,
//                new InstalledAppsManager.InstructionCollection(Instruction.ADD, identifier));
//    }

    private Map<Integer, InstalledAppsManager.InstructionCollection> storedItems;
}
