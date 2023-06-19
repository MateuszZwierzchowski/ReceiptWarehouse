package com.example.receiptwarehouse

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ObjectAnimator
import androidx.navigation.NavController
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import android.view.View
import android.view.animation.BounceInterpolator
import com.example.receiptwarehouse.databinding.ActivityMainBinding

class  MainActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding

    private var reciepeDetails: show_reciepe_fragment? = null
    private var reciepeList: MainPreviewFragment? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        this.reciepeDetails = show_reciepe_fragment()
        this.reciepeList = MainPreviewFragment()

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)


        val navController = findNavController(R.id.nav_host_fragment_content_main)


        binding.fabAdd.setOnClickListener {
            val fab = binding.fabAdd

            val bounceAnimator = ObjectAnimator.ofFloat(fab, View.TRANSLATION_Y, 0f, -50f, 0f)
            bounceAnimator.duration = 500
            bounceAnimator.interpolator = BounceInterpolator()

            bounceAnimator.addListener(object: AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator) {
                    super.onAnimationEnd(animation)
                    navController.navigateUp()
                    navController.navigate(R.id.CameraFragment)
                }
            })
            bounceAnimator.start()
        }

    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        return navController.navigateUp(appBarConfiguration)
                || super.onSupportNavigateUp()
    }

    fun getReciepeDetails(): show_reciepe_fragment? {
        return this.reciepeDetails
    }

    fun getReciepeList(): MainPreviewFragment? {
        return this.reciepeList
    }
}