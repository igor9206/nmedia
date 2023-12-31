package ru.netology.nmedia.activity

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.MenuProvider
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability
import com.google.android.material.snackbar.BaseTransientBottomBar.LENGTH_INDEFINITE
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.messaging.FirebaseMessaging
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import ru.netology.nmedia.R
import ru.netology.nmedia.activity.NewPostFragment.Companion.text
import ru.netology.nmedia.auth.AppAuth
import ru.netology.nmedia.databinding.ActivityAppBinding
import ru.netology.nmedia.dialog.AppDialog
import ru.netology.nmedia.viewmodel.AuthViewModel
import javax.inject.Inject

@AndroidEntryPoint
class AppActivity : AppCompatActivity() {
    private lateinit var navHostFragment: NavHostFragment
    private lateinit var navController: NavController
    private val authViewModel: AuthViewModel by viewModels()

    @Inject
    lateinit var appAuth: AppAuth

    @Inject
    lateinit var firebaseMessaging: FirebaseMessaging

    @Inject
    lateinit var googleApiAvailability: GoogleApiAvailability

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityAppBinding.inflate(layoutInflater)
        setContentView(binding.root)

        navHostFragment =
            supportFragmentManager.findFragmentById(R.id.navContainerFragment) as NavHostFragment
        navController = navHostFragment.navController
        intent?.let { intent ->
            if (intent.action != Intent.ACTION_SEND) {
                return@let
            }

            val text = intent.getStringExtra(Intent.EXTRA_TEXT)
            if (text.isNullOrBlank()) {
                Snackbar.make(binding.root, R.string.error_empty_content, LENGTH_INDEFINITE)
                    .setAction(android.R.string.ok) {
                        finish()
                    }
                    .show()
                return@let
            }
            // TODO: handle text


            navController.navigate(
                R.id.action_feedFragment_to_newPostFragment,
                Bundle().also { it.text = text }
            )
        }


        authViewModel.data.observe(this) {
            invalidateOptionsMenu()
        }

        addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.menu_main, menu)
            }

            override fun onPrepareMenu(menu: Menu) {
                menu.setGroupVisible(R.id.authenticated, authViewModel.authenticated)
                menu.setGroupVisible(R.id.unauthenticated, !authViewModel.authenticated)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                return when (menuItem.itemId) {
                    R.id.signin -> {
//                        AppAuth.getInstance().setAuth(5, "x-token")
                        navController.navigate(R.id.authorizationFragment)
                        true
                    }

                    R.id.signup -> {
                        navController.navigate(R.id.registrationFragment)
//                        AppAuth.getInstance().setAuth(5, "x-token")
                        true
                    }

                    R.id.signout -> {
                        val nowFragment = navController.currentDestination?.id
                        nowFragment ?: false
                        if (nowFragment == R.id.newPostFragment) {
                            AppDialog.dialogExit(binding.root, appAuth)

                        } else {
                            appAuth.removeAuth()
                        }
                        true
                    }

                    else -> false
                }
            }
        })

        requestNotificationsPermission()
        checkGoogleApiAvailability()
    }

    private fun requestNotificationsPermission() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
            return
        }

        val permission = Manifest.permission.POST_NOTIFICATIONS

        if (checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED) {
            return
        }

        requestPermissions(arrayOf(permission), 1)
    }

    private fun checkGoogleApiAvailability() {
        with(googleApiAvailability) {
            val code = isGooglePlayServicesAvailable(this@AppActivity)
            if (code == ConnectionResult.SUCCESS) {
                return@with
            }
            if (isUserResolvableError(code)) {
                getErrorDialog(this@AppActivity, code, 9000)?.show()
                return
            }
            Toast.makeText(this@AppActivity, R.string.google_play_unavailable, Toast.LENGTH_LONG)
                .show()
        }

        firebaseMessaging.token.addOnSuccessListener {
            println(it)
        }
    }

}