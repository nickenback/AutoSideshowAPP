package jp.techacademy.kenji.autosideshowapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.provider.MediaStore
import android.content.ContentUris
import kotlinx.android.synthetic.main.activity_main.*
import androidx.core.net.toUri

import android.os.Handler
import java.util.*

class MainActivity : AppCompatActivity() {

    private val PERMISSIONS_REQUEST_CODE = 100

    private var tTimer: Timer? = null

    private var thandler = Handler()




    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        var i:Int = 0
        var imagelistArray = arrayListOf<String>()




        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                imagelistArray = getContentsInfo()
            } else {
                requestPermissions(arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), PERMISSIONS_REQUEST_CODE)
            }
        } else {
            imagelistArray = getContentsInfo()
        }

        var im:Int = imagelistArray.size - 1


        startstop.setOnClickListener {
            if (imagelistArray.size > 0) {
                if (tTimer == null) {
                    startstop.text = "停止"
                    
                    tTimer = Timer()
                    tTimer!!.schedule(object : TimerTask() {
                        override fun run() {
                            thandler.post {
                                i = countup(i, im)
                                imageView.setImageURI(imagelistArray[i].toUri())
                            }
                        }
                    }, 2000, 2000)

                } else {
                    startstop.text = "開始"

                    tTimer!!.cancel()
                    tTimer = null
                }
            }
        }


        next.setOnClickListener {
            if(tTimer == null && imagelistArray.size > 0) {
                i = countup(i, im)
                imageView.setImageURI(imagelistArray[i].toUri())
            }
        }

        prev.setOnClickListener {
            if(tTimer == null && imagelistArray.size > 0) {
                i = countdown(i, im)
                imageView.setImageURI(imagelistArray[i].toUri())
            }
        }



    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        var imagelistArray = arrayListOf<String>()
        when (requestCode) {
            PERMISSIONS_REQUEST_CODE ->
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    imagelistArray = getContentsInfo()
                }
        }
    }

    private fun getContentsInfo():ArrayList<String> {
        val resolver = contentResolver
        val cursor = resolver.query(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            null,
            null,
            null,
            null
        )

        var imagelistArray = arrayListOf<String>()

        if (cursor!!.moveToFirst()) {
            do {
                val fieldIndex = cursor.getColumnIndex(MediaStore.Images.Media._ID)
                val id = cursor.getLong(fieldIndex)
                val imageUri = ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id)

                imagelistArray.add(imageUri.toString())

            } while (cursor.moveToNext())
        }
        if(imagelistArray.size > 1) {
            imageView.setImageURI(imagelistArray[0].toUri())
        }

        cursor.close()
        return(imagelistArray)
    }

    private fun countup(i:Int, im:Int):Int {
        var x = i
        var y = im
        if( x< y) {
            x++
        }else if(x == y){
            x = 0
        }
        return(x)
    }

    private fun countdown(i:Int, im:Int):Int{
        var x = i
        var y = im
        if(x > 0) {
            x--
        }else if(x == 0){
            x = y
        }
        return(x)
    }
}