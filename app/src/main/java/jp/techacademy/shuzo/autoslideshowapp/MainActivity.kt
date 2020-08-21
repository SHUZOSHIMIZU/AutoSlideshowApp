package jp.techacademy.shuzo.autoslideshowapp
import android.Manifest
import android.content.ContentResolver
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import android.provider.MediaStore
import android.content.ContentUris
import android.database.Cursor
import android.os.Handler
import kotlinx.android.synthetic.main.activity_main.*
import java.lang.NullPointerException
import java.util.*
class MainActivity : AppCompatActivity() {
    private val PERMISSIONS_REQUEST_CODE = 100
    private val Time_Interval=2000.0



      var Position : Int =0
    private var mTimer: Timer? = null
    // タイマー用の時間のための変数
    private var mTimerSec = 0.0
    private var mHandler = Handler()



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        // Android 6.0以降の場合
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // パーミッションの許可状態を確認する
            if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                // 許可されている
                Position=getContentsInfo("0",0)
            } else {
                // 許可されていないので許可ダイアログを表示する
                requestPermissions(
                    arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                    PERMISSIONS_REQUEST_CODE
                )
            }
            // Android 5系以下の場合
        } else {
            Position=getContentsInfo("0",0)
        }


        btn1.setOnClickListener {
          Position=getContentsInfo("1",Position)
           }

        btn3.setOnClickListener {
            Position=getContentsInfo("2",Position)
        }

        btn2.setOnClickListener {
           if (mTimer==null) {
               btn2.text="■"
               btn1.setEnabled(false)
               btn3.setEnabled(false)
               Position=getContentsInfo("0",0)
               // タイマーの作成
               mTimer = Timer()

               mTimer!!.schedule(object : TimerTask() {
                   override fun run() {
                       mHandler.post {
                           // timer.text = String.format("%.1f", mTimerSec)
                           Position = getContentsInfo("1", Position)
                       }
                   }
               }, 2000, 2000)
           }else{
               mTimer!!.cancel()
               mTimer = null
               btn2.text="▶"
               btn1.setEnabled(true)
               btn3.setEnabled(true)

           }







        }












    }



    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            PERMISSIONS_REQUEST_CODE ->
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Position=getContentsInfo("0",0)
                }
        }





    }

    private fun getContentsInfo(sw:String,Po :Int):Int {
    //sw 1:送り　2 戻り　0　初期
        //return 位置　Long

        val resolver = contentResolver
        val cursor = resolver.query(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI, // データの種類
            null, // 項目(null = 全項目)
            null, // フィルタ条件(null = フィルタなし)
            null, // フィルタ用パラメータ
            null // ソート (null ソートなし)
        )
        Log.d("ANDROID", "SW : " + sw.toString())
        cursor.moveToPosition(Po)
        if (sw == "0") {
            cursor!!.moveToFirst()
        } else if (sw == "1") {
            if (cursor.moveToNext()==false) {
                 Log.d("ANDROID", "URI : >" )
                  cursor.moveToFirst()

            }
        } else if (sw == "2") {
            if (cursor.moveToPrevious()==false) {
                 Log.d("ANDROID", "URI : <")
                cursor.moveToLast()
            }
        }




            // indexからIDを取得し、そのIDから画像のURIを取得する
            var fieldIndex = cursor.getColumnIndex(MediaStore.Images.Media._ID)
            var id = cursor.getLong(fieldIndex)
            var imageUri =
                ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id)
             Log.d("ANDROID", "URI : " + imageUri.toString())
            imageView.setImageURI(imageUri)

            return cursor.getPosition()



        cursor.close()
    }
}


