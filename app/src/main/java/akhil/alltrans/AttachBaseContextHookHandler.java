/*
 * Copyright 2017 Akhil Kedia
 * This file is part of AllTrans.
 *
 * AllTrans is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * AllTrans is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with AllTrans. If not, see <http://www.gnu.org/licenses/>.
 *
 */

package akhil.alltrans;

import android.app.Application;
import android.content.Context;

import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.util.HashMap;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;

class AttachBaseContextHookHandler extends XC_MethodHook {
    @Override
    protected void beforeHookedMethod(MethodHookParam methodHookParam) {
        XposedBridge.log("AllTrans: in attachBaseContext of ContextWrapper");
        alltrans.context = (Context) methodHookParam.args[0];
        utils.debugLog("Successfully got context!");

        if (PreferenceList.Caching) {
            if(alltrans.cache.isEmpty()) {
                try {
                    FileInputStream fileInputStream = alltrans.context.openFileInput("AllTransCache");
                    ObjectInputStream s = new ObjectInputStream(fileInputStream);
                    alltrans.cacheAccess.acquireUninterruptibly();
                    //noinspection unchecked
                    alltrans.cache = (HashMap<String, String>) s.readObject();
                    alltrans.cacheAccess.release();
                    utils.debugLog("Successfully read old cache");
                    s.close();
                } catch (Exception e) {
                    utils.debugLog("Could not read cache ");
                    utils.debugLog(e.toString());
                    alltrans.cacheAccess.acquireUninterruptibly();
                    alltrans.cache = new HashMap<>(10000);
                    alltrans.cache.put("ThisIsAPlaceHolderStringYouWillNeverEncounter", "ThisIsAPlaceHolderStringYouWillNeverEncounter");
                    alltrans.cacheAccess.release();
                }
            }
        }
    }

}