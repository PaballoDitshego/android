package za.co.moxomo.activities


import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ProgressBar

import za.co.moxomo.R
import za.co.moxomo.databasehelpers.NotificationDatabaseHelper
import za.co.moxomo.helpers.ApplicationConstants

/**
 * The activity that handles pendingintents, also used as the webview activity
 */


class NotificationActivity : AppCompatActivity() {


    private val fm = supportFragmentManager


    var progressBar: ProgressBar? = null
      //  private set


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_notification)
        val toolbar = findViewById(R.id.toolbar) as Toolbar
        toolbar.setNavigationIcon(R.drawable.ic_action_back)
        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayShowTitleEnabled(false)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        progressBar = findViewById(R.id.progress_spinner) as ProgressBar
        progressBar!!.visibility = View.VISIBLE

        handleIntent(intent)


    }



    private fun handleIntent(intent: Intent) {

        if (intent.hasExtra("notification")) {
            val helper = NotificationDatabaseHelper(this)
            val row_id = intent.getLongExtra("row_id", 0)
            val cursor = helper.getNotification(row_id)
            val type = cursor!!.getString(cursor.getColumnIndexOrThrow(NotificationDatabaseHelper.TYPE))
            val action_string = cursor.getString(cursor.getColumnIndexOrThrow(NotificationDatabaseHelper.ACTION_STRING))
            cursor.close()

            when (type) {

                ApplicationConstants.ACTION_NEWS_ALERT -> {
                    helper.updateNotification(row_id, "read")
                   // loadInBrowser(action_string)
                }

                ApplicationConstants.ACTION_JOB_ALERT -> {
                    helper.updateNotification(row_id, "read")

                }

                else -> {
                }
            }

        } else {

            val url = intent.getStringExtra("url")
         //   loadInBrowser(url)
        }


    }




    public override fun onSaveInstanceState(savedInstanceState: Bundle) {
        super.onSaveInstanceState(savedInstanceState)
    }

    public override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)


    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_browser, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        val id = item.itemId


        return if (id == R.id.action_settings) {
            true
        } else super.onOptionsItemSelected(item)

    }


}