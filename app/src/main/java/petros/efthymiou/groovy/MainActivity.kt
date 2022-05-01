package petros.efthymiou.groovy

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import petros.efthymiou.groovy.playlist.PlayListFragment

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //NOTE: WRITE MINIMUM CODE POSSIBLE TO VERIFY UI LAYER !!!!!!
        //INSERT STARTING FRAGMENT - recommended architecture
        /////////////////////////////////////////////////
        if(savedInstanceState == null)
        {
            supportFragmentManager.beginTransaction()
                .add(R.id.container, PlayListFragment.newInstance()/* RECOMMENDED
                 WAY OF CREATING AN ANDROID FRAGMENT !!!!!*/)
                .commit()

        }
        ///////////////////////////////////////////////////
    }



}