package com.katrina.ui;

import java.util.ArrayList;

/**
 * Created by alatnet on 3/8/2015.
 */
public class ModuleFactory {
    private static ArrayList<KatrinaModule>tmpModList = new ArrayList<>();
    private static boolean isInit = false;
    private static MainUI mUI = null;

    synchronized public static void registerMod(KatrinaModule mod){
        if (!isInit)tmpModList.add(mod); //hold the mod until main ui is started.
        else { //main ui is started, add to the list.
            mUI.addModule(mod);
        }
    }

    synchronized public static void registerMainUI(MainUI _mUI){
        mUI = _mUI;
        isInit = true;

        for(int i=0;i<tmpModList.size();i++){ mUI.addModule(tmpModList.get(i)); }
        tmpModList.clear();
        tmpModList = null;
    }
}
