package com.nasahacker.nasacolorpicker.view

import android.annotation.SuppressLint
import android.content.ClipData
import android.content.ClipboardManager
import android.graphics.Bitmap
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.MotionEvent
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.graphics.blue
import androidx.core.graphics.green
import androidx.core.graphics.red
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.nasahacker.nasacolorpicker.R
import com.nasahacker.nasacolorpicker.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private val binding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }

    private var bitmap: Bitmap? = null

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val pickMedia =
            registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
                if (uri != null) {
                    bitmap = null
                    binding.ivSelected.setImageURI(uri)
                } else {
                    Log.d("PhotoPicker", "No media selected")
                }
            }

        binding.ivSelected.isDrawingCacheEnabled = true
        binding.ivSelected.buildDrawingCache(true)

        binding.btnPick.setOnClickListener {
            pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        }

        binding.ivSelected.setOnTouchListener { _, motionEvent ->
            if (motionEvent.action == MotionEvent.ACTION_UP || motionEvent.action == MotionEvent.ACTION_MOVE) {
                // Clear and rebuild the drawing cache
                binding.ivSelected.isDrawingCacheEnabled = false
                binding.ivSelected.isDrawingCacheEnabled = true

                // Get the new bitmap from the drawing cache
                bitmap = binding.ivSelected.drawingCache

                // Ensure bitmap is not null
                if (bitmap != null) {
                    val x = motionEvent.x.toInt()
                    val y = motionEvent.y.toInt()

                    // Check if the touch coordinates are within the bitmap bounds
                    if (x >= 0 && x < bitmap!!.width && y >= 0 && y < bitmap!!.height) {
                        // Get the pixel color at the touched location
                        val pixel = bitmap!!.getPixel(x, y)

                        val r = pixel.red
                        val g = pixel.green
                        val b = pixel.blue

                        // Update UI with the selected color
                        binding.tvSelectedColor.text = "#${Integer.toHexString(pixel)}"
                        binding.colorView.setBackgroundColor(Color.rgb(r, g, b))

                        // Update the circle in the custom image view
                        binding.ivSelected.updateCache(motionEvent.x, motionEvent.y)
                    } else {
                        Log.e("MainActivity", "Touch coordinates out of bitmap bounds")
                    }
                } else {
                    Log.e("MainActivity", "Bitmap is null")
                }
            }
            true
        }


        binding.btnCopy.setOnClickListener {
            val clipboardManager = getSystemService(CLIPBOARD_SERVICE) as ClipboardManager
            val clipData = ClipData.newPlainText("color", binding.tvSelectedColor.text.toString())
            clipboardManager.setPrimaryClip(clipData)
            Toast.makeText(this, "Copied to clipboard", Toast.LENGTH_SHORT).show()
        }


    }

}