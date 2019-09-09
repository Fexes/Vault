package com.example.encrypt.util;

import android.app.Activity;
import android.content.ComponentName;
import android.content.pm.PackageManager;

import java.util.List;

/**
 * Created by 10 on 12-01-2017.
 */

public class AppIconNameChanger {

    List<String> disableNames;
    String activeName;
    String packageName;
    private Activity activity;

    public AppIconNameChanger(Builder builder) {

        this.disableNames = builder.disableNames;
        this.activity = builder.activity;
        this.activeName = builder.activeName;
        this.packageName = builder.packageName;

    }

    public void setNow() {

        activity.getPackageManager().setComponentEnabledSetting(
                new ComponentName(packageName, activeName),
                PackageManager.COMPONENT_ENABLED_STATE_ENABLED, PackageManager.DONT_KILL_APP);

        for (int i = 0; i < disableNames.size(); i++) {
            try {
                activity.getPackageManager().setComponentEnabledSetting(
                        new ComponentName(packageName, disableNames.get(i)),
                        PackageManager.COMPONENT_ENABLED_STATE_DISABLED, PackageManager.DONT_KILL_APP);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static class Builder {

        List<String> disableNames;
        String activeName;
        String packageName;
        private Activity activity;

        public Builder(Activity activity) {
            this.activity = activity;
        }

        public Builder disableNames(List<String> disableNamesl) {
            this.disableNames = disableNamesl;
            return this;
        }

        public Builder activeName(String activeName) {
            this.activeName = activeName;
            return this;
        }

        public Builder packageName(String packageName) {
            this.packageName = packageName;
            return this;
        }

        public AppIconNameChanger build() {
            return new AppIconNameChanger(this);
        }

    }
}
