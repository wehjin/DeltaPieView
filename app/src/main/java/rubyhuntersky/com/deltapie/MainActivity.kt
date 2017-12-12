package rubyhuntersky.com.deltapie

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.widget.SeekBar
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        with(seekBar) {
            setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
                override fun onStartTrackingTouch(seekBar: SeekBar) = Unit
                override fun onStopTrackingTouch(seekBar: SeekBar) = Unit
                override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                    val delta = -(progress * 2 - 100) / 100f
                    Log.d(MainActivity::class.java.simpleName, "Delta $delta")
                    update(delta)
                }
            })
            progress = 45
        }
    }

    private fun update(delta: Float) {
        deltaPieView.delta = delta
    }
}
