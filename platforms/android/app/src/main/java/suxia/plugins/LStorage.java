package suxia.plugins;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteStatement;
import android.net.Uri;
import android.os.Build;
import android.util.Log;
import android.widget.EditText;
import android.widget.Toast;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaDialogsHelper;
import org.apache.cordova.CordovaPlugin;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.net.URL;
import java.util.Base64;
import java.util.HashMap;

import suxia.com.LStoragePlugin.browserActivity;

import suxia.com.LStoragePlugin.R;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;

public  class LStorage extends CordovaPlugin{
    private static final String LOG_TAG = "LStoragePlugin";
    static HashMap<String,SQLiteDatabase> dbMap=new HashMap<String,SQLiteDatabase>();
    private CallbackContext callbackContext;

    public LStorage() {

    }



    @Override
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) {
        this.callbackContext=callbackContext;

        //String dbname=https_slc08utm.us.oracle.com_16690.localstorage;

        //We try to clear data from container ,so the localstorage file name should be transfered .
        //Can't get the file from current url.
        try {
            JSONObject o = args.getJSONObject(0);

            if (action.equals("cleardata")) {
                String dbname=o.getString("name");
                String key=o.getString("keyword");
                String sql = "delete from ItemTable where Key = ?";
                Toast.makeText(cordova.getActivity(), "clear data", Toast.LENGTH_LONG).show();
                this.excuteSqlBatch(dbname, sql, key, callbackContext);

                return true;
            } else if (action.equals("showdata")) {
                String dbname=o.getString("name");
                Toast.makeText(cordova.getActivity(), "showdata", Toast.LENGTH_LONG).show();
                this.excuteSqlBatch(dbname, "select * from  ItemTable", null, callbackContext);
                return true;

            }else if(action.equals("openUrl")){
                String url=o.getString("url");

//                Intent intent=new Intent(Intent.ACTION_VIEW,Uri.parse(url));
//                cordova.getActivity().startActivity(intent);
                Intent intent=new Intent(cordova.getActivity(),browserActivity.class);
                intent.putExtra("url",url);
                cordova.getActivity().startActivityForResult(intent,1);


                return true;
            }

            return super.execute(action, args, callbackContext);
        }catch (JSONException e){
            e.printStackTrace();
            return false;
        }
    }


    private void openDatabase(String dbname) {
        if(this.getDatabase(dbname)!=null){
            this.closeDatabase(dbname);
        }
        String dbFilePath="/data/data/"+cordova.getActivity().getPackageName()+"/app_webview/Local Storage/"+dbname;
        File dbfile=new File(dbFilePath);
        if(dbfile.exists()){
            Log.v(LOG_TAG,"Open Sqlite :"+dbfile.getAbsolutePath());
            SQLiteDatabase db =SQLiteDatabase.openDatabase(dbFilePath,null,SQLiteDatabase.OPEN_READWRITE | SQLiteDatabase.NO_LOCALIZED_COLLATORS);
            dbMap.put(dbname,db);
        }else{
            Log.v(LOG_TAG,"can't find sqlite file :"+dbfile.getAbsolutePath());
        }


    }

    private void excuteSqlBatch(String dbName,String sql,String keyword,CallbackContext cbc){
        this.openDatabase(dbName);
        SQLiteDatabase db=this.getDatabase(dbName);

        if(db ==null) return;

        Boolean needRawQuery=true;
        String errorMessage="Unknown";

        JSONObject queryResult=new JSONObject();
        try {
            if (sql.toLowerCase().startsWith("delete")) {
                int rowsAffected = -1;
                SQLiteStatement myStatement = db.compileStatement(sql);

                //bind from 1
                myStatement.bindString(1, keyword);
                try {
                    rowsAffected = myStatement.executeUpdateDelete();
                    // Indicate valid results:
                    needRawQuery = false;
                    Log.e("SQLite", "----delete successfully----");
                } catch (SQLiteException e) {
                    e.printStackTrace();
                    needRawQuery = false;
                }
                if (rowsAffected != -1) {
                    queryResult.put("rowsAffected", rowsAffected);
                }
            }

            if (needRawQuery) {
                Cursor cur = db.rawQuery(sql, null);
                queryResult = this.getRowsResultFromCur(cur);
                cur.close();

            }
        }catch (JSONException e){
            e.printStackTrace();
            errorMessage = e.getMessage();
            Log.v("executeSqlBatch", "SQLitePlugin.executeSql[Batch](): Error=" +  errorMessage);
        }

        cbc.success(queryResult);

    }

    private JSONObject getRowsResultFromCur(Cursor cur){
        JSONObject rowsResult=new JSONObject();
        if(cur.moveToFirst()){
            JSONArray rowsArrayResult=new JSONArray();
            String key="";
            int colCount=cur.getColumnCount();
            do{
                try {
                    JSONObject row = new JSONObject();
                    for (int i = 0; i < colCount; i++) {
                        key = cur.getColumnName(i);
                        if (Build.VERSION.SDK_INT > 11) {
                            int curType=3;
                            try {
                                curType = cur.getType(i);
                                switch (curType) {
                                    case Cursor.FIELD_TYPE_NULL:
                                        row.put(key, JSONObject.NULL);
                                        break;
                                    case Cursor.FIELD_TYPE_INTEGER:
                                        row.put(key, cur.getInt(i));
                                        break;
                                    case Cursor.FIELD_TYPE_FLOAT:
                                        row.put(key, cur.getFloat(i));
                                        break;
                                    case Cursor.FIELD_TYPE_BLOB:
                                        row.put(key, cur.getBlob(i));
                                        break;
                                    case Cursor.FIELD_TYPE_STRING:
                                    default:
                                        row.put(key, cur.getString(i));
                                        break;


                                }
                            }catch (Exception e){
                                e.printStackTrace();
                            }

                        } else {
                            row.put(key, cur.getString(i));
                        }

                    }
                    rowsArrayResult.put(row);
                }catch (JSONException ex){
                    ex.printStackTrace();
                }


            }while (cur.moveToNext());
            try{
                rowsResult.put("rows",rowsArrayResult);
            }catch (JSONException e){
                e.printStackTrace();
            }
        }
        return  rowsResult;
    }


    private void closeDatabase(String dbname){
        SQLiteDatabase db=dbMap.get(dbname);
        if(db!=null){
            db.close();
            dbMap.remove(dbname);
        }
    }

    private SQLiteDatabase getDatabase(String dbname){
        return dbMap.get(dbname);
    }

    @Override
    public void onDestroy() {
        while (!dbMap.isEmpty()){
            String name=dbMap.keySet().iterator().next();
            this.closeDatabase(name);
            dbMap.remove(name);
        }
        super.onDestroy();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        if (resultCode == RESULT_OK) {
            Log.i("URL", requestCode + "");
        }
    }
}