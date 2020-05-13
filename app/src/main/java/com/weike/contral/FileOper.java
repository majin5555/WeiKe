package com.weike.contral;

import android.util.Log;

import java.io.File;

public class FileOper {
//	public Bitmap[] getStrokeFilePaths(){
//		String strDir = getStrokeFilePath();
//		Bitmap[] strDirs = new Bitmap[100] ;
//		File file = new File(strDir);
//		File[] files = file.listFiles();
//		for(int i=0; i<files.length; i++){
//			if(files[i].exists()){
//				strDirs[i] = BitmapUtil.loadBitmapFromSDCard(files[i].getPath());
//			}
//		}
//		return strDirs;
//	}
	public String[] getStrokeFileNames(){
		String strDir = getStrokeFilePath();
		String[] strNames = new String[100];
		String[] filenames = null;
		File file = new File(strDir);
		File[] files = file.listFiles();
		for(int i=0; i<files.length; i++){
			if(files[i].exists()){
				Log.e("fileName:", files[i].getName());
				String str = files[i].getName();
				strNames[i] = str;
			}
		}
		filenames = new String[files.length];
		for(int i=0; i<filenames.length; i++){
			filenames[i] = strNames[i];
		}
		return filenames;
	}
	/*ȡ��ָ��Ŀ¼��·��*/
	public String getStrokeFilePath()
    {
        File sdcarddir = android.os.Environment.getExternalStorageDirectory();
        String strDir = sdcarddir.getPath() + "/CANVAS/";
        File file = new File(strDir);
        if (!file.exists())
        {
            file.mkdirs();
        }
        return strDir;
    }

}
