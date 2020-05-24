package com.louis.cloudmusic.skin.library;

import android.app.Application;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.util.Log;

import java.lang.reflect.Method;

public class SkinManager {

    public static SkinManager instance;
    private Application application;
    private Resources appResources;//app内置资源
    private Resources skinResources;//加载皮肤包资源
    private String skinPackageName;//皮肤包所在包名
    private boolean isDefaultSkin = true;//是否默认皮肤（app内置皮肤）
    private static final String ADD_ASSET_PATH = "addAssetPath";//方法名

    public SkinManager(Application application) {
        this.application = application;
        appResources = application.getResources();
    }

    public static void init(Application application) {
        if (instance == null) {
            synchronized (SkinManager.class) {
                if (instance == null) {
                    instance = new SkinManager(application);
                }
            }
        }
    }

    public static SkinManager getInstance() {
        return instance;
    }


    public void loaderSkinResources(String skinPath){
        try {
            //创建资源管理器
            AssetManager assetManager = AssetManager.class.newInstance();
            //被@hide限制，目前只能通过反射去拿
            Method addAssetPath = assetManager.getClass().getDeclaredMethod(ADD_ASSET_PATH, String.class);
            addAssetPath.setAccessible(true);
            addAssetPath.invoke(assetManager, skinPath);

            //创建加载外部皮肤包资源文件Resource
            //利用本应用的Resource初始化skinResource
            skinResources = new Resources(assetManager, appResources.getDisplayMetrics(), appResources.getConfiguration());

            //本剧皮肤包文件，获取皮肤包应用的包名
            skinPackageName = application.getPackageManager().getPackageArchiveInfo(skinPath, PackageManager.GET_ACTIVITIES).packageName;
            //如果无法获取皮肤包应用的包名，加载app内置资源
            isDefaultSkin = TextUtils.isEmpty(skinPackageName);

            Log.e("skinLibrary", "skinPackageName" + skinPackageName);
        } catch (Exception e) {
            e.printStackTrace();
            //可能发生异常
            isDefaultSkin = true;
        }
    }

    /*
        参考：resources.arsc资源映射
        通过ID值获取资源的Name和Type
        @param resourceId 资源的ID值
     */
    private int getSkinResourceIds(int resourceId) {
        String resourceEntryName = appResources.getResourceEntryName(resourceId);
        String resourceTypeName = appResources.getResourceTypeName(resourceId);//类型

        int skinResourceID = skinResources.getIdentifier(resourceEntryName, resourceTypeName, skinPackageName);
        isDefaultSkin = skinResourceID == 0;

        return skinResourceID == 0 ? resourceId : skinResourceID;
    }

    public boolean isDefaultSkin() {
        return isDefaultSkin;
    }

    //==============================================================================================

    public int getColor(int resourceId) {
        int ids = getSkinResourceIds(resourceId);
        return isDefaultSkin ? appResources.getColor(ids) : skinResources.getColor(ids);
    }

    public ColorStateList getColorStateList(int resourceId) {
        int ids = getSkinResourceIds(resourceId);
        return isDefaultSkin ? appResources.getColorStateList(ids) : skinResources.getColorStateList(ids);
    }

    // mipmap和drawable统一用法（待测）
    public Drawable getDrawableOrMipMap(int resourceId) {
        int ids = getSkinResourceIds(resourceId);
        return isDefaultSkin ? appResources.getDrawable(ids) : skinResources.getDrawable(ids);
    }

    public String getString(int resourceId) {
        int ids = getSkinResourceIds(resourceId);
        return isDefaultSkin ? appResources.getString(ids) : skinResources.getString(ids);
    }

    // 返回值特殊情况：可能是color / drawable / mipmap
    public Object getBackgroundOrSrc(int resourceId) {
        // 需要获取当前属性的类型名Resources.getResourceTypeName(resourceId)再判断
        String resourceTypeName = appResources.getResourceTypeName(resourceId);

        switch (resourceTypeName) {
            case "color":
                return getColor(resourceId);

            case "mipmap": // drawable / mipmap
            case "drawable":
                return getDrawableOrMipMap(resourceId);
        }
        return null;
    }

    // 获得字体
    public Typeface getTypeface(int resourceId) {
        // 通过资源ID获取资源path，参考：resources.arsc资源映射表
        String skinTypefacePath = getString(resourceId);
        // 路径为空，使用系统默认字体
        if (TextUtils.isEmpty(skinTypefacePath)) return Typeface.DEFAULT;
        return isDefaultSkin ? Typeface.createFromAsset(appResources.getAssets(), skinTypefacePath)
                : Typeface.createFromAsset(skinResources.getAssets(), skinTypefacePath);
    }
}
