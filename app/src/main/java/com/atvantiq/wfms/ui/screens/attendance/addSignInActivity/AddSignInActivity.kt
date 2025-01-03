package com.atvantiq.wfms.ui.screens.attendance.addSignInActivity

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.DialogInterface
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.atvantiq.wfms.R
import com.atvantiq.wfms.base.BaseActivity
import com.atvantiq.wfms.databinding.ActivityAddSignInBinding
import com.atvantiq.wfms.ui.screens.adapters.CustomArrayAdapter
import com.atvantiq.wfms.ui.screens.adapters.CustomAutoCompleteAdapter
import com.atvantiq.wfms.utils.DateUtils
import com.atvantiq.wfms.utils.PermissionUtils
import com.atvantiq.wfms.utils.Utils
import com.atvantiq.wfms.utils.files.PickMediaHelper
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import dagger.hilt.android.components.ActivityComponent

class AddSignInActivity : BaseActivity<ActivityAddSignInBinding, AddSignInVM>() {

    /*Local variables*/
    private var circleList = listOf("CG", "HP", "HR", "MH", "PB", "RJ", "DL", "UPE")
    private val projectList =
        listOf("DEPL-CG-BSNL-TOWER", "DEPL-CG-BSNL-EnodeB", "DEMO-PROJECT", "DEV-ENV-PROJECT")
    private val typeList =
        listOf("Tower Erection, Electrical, Tower Fencing", "Civil New Built", "MISC")
    private var siteIdList = listOf("445213", "438803", "893438", "343554", "098787")
    private val activityList = listOf("I&C", "ATP")
    private lateinit var circleAdapter: CustomAutoCompleteAdapter
    private lateinit var projectAdapter: CustomAutoCompleteAdapter
    private lateinit var typeAdapter: CustomAutoCompleteAdapter
    private lateinit var siteAdapter: CustomAutoCompleteAdapter
    private lateinit var activityAdapter: CustomAutoCompleteAdapter
    //---------------------------------------------------//

    /*Location API Variables*/
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    //--------------------------------------------------//

    // Image Picker Code
    private val cameraLauncher = registerForActivityResult(ActivityResultContracts.TakePicture()) { success ->
        pickMediaHelper.handleCameraResult(success)
    }

    private val galleryLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            pickMediaHelper.handleGalleryResult(result.data)
        }
    }

    private val permissionLauncher = registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
        pickMediaHelper.handlePermissionResult(permissions)
    }

    private lateinit var pickMediaHelper: PickMediaHelper
    //---------------------------------------------------------//


    override val bindingActivity: ActivityBinding
        get() = ActivityBinding(R.layout.activity_add_sign_in, AddSignInVM::class.java)

    override fun onCreateActivity(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        setUpToolbar()
        setUpPlaceLocations()
        setDateTimeAttendance()
        setImagePicker()
        setCircleList()
        setProjectList()
        setTypeList()
        setSiteList()
        setActivityList()
        initListeners()
    }

    private fun setUpPlaceLocations(){
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        requestCurrentLocation()
    }

    private fun setUpToolbar() {
        binding.addSignInToolbar.toolbarTitle.text = getString(R.string.add_login_activity)
        binding.addSignInToolbar.toolbarBackButton.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
    }

    private fun setDateTimeAttendance(){
        binding.dateString = DateUtils.getCurrentDate()
        binding.timeString = DateUtils.getCurrentTime()
    }

    private fun setImagePicker(){
        pickMediaHelper = PickMediaHelper(this, cameraLauncher, galleryLauncher, permissionLauncher, object : PickMediaHelper.Callback {
            override fun onImagePicked(path: String, request: Int) {
                if(!path.isNullOrBlank()){
                    binding.hasPreviewImage = true
                    var bitmap = pickMediaHelper.decodeBitmap(path)
                    binding.capturedImagePreview.setImageBitmap(bitmap)
                }
            }

            override fun onError(message: String) {
                binding.hasPreviewImage = false
            }
        })
    }

    private fun initListeners() {
        binding.circleEt.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                binding.circleEt.showDropDown()
            }
        }
        binding.circleEt.setOnClickListener {
            binding.circleEt.showDropDown()

        }

        binding.projectEt.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                binding.projectEt.showDropDown()
            }
        }
        binding.projectEt.setOnClickListener {
            binding.projectEt.showDropDown()

        }

        binding.typeEt.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                binding.typeEt.showDropDown()
            }
        }
        binding.typeEt.setOnClickListener {
            binding.typeEt.showDropDown()

        }

        binding.siteEt.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                binding.siteEt.showDropDown()
            }
        }
        binding.siteEt.setOnClickListener {
            binding.siteEt.showDropDown()

        }

        binding.activityEt.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                binding.activityEt.showDropDown()
            }
        }
        binding.activityEt.setOnClickListener {
            binding.activityEt.showDropDown()

        }
    }

    private fun setCircleList() {
        circleAdapter =
            CustomAutoCompleteAdapter(this, android.R.layout.simple_dropdown_item_1line, circleList)
        binding.circleEt.setAdapter(circleAdapter)
    }

    private fun setProjectList() {
        projectAdapter = CustomAutoCompleteAdapter(
            this,
            android.R.layout.simple_dropdown_item_1line,
            projectList
        )
        binding.projectEt.setAdapter(projectAdapter)
    }

    private fun setTypeList() {
        typeAdapter =
            CustomAutoCompleteAdapter(this, android.R.layout.simple_dropdown_item_1line, typeList)
        binding.typeEt.setAdapter(typeAdapter)
    }

    private fun setSiteList() {
        siteAdapter =
            CustomAutoCompleteAdapter(this, android.R.layout.simple_dropdown_item_1line, siteIdList)
        binding.siteEt.setAdapter(siteAdapter)
    }

    private fun setActivityList() {
        activityAdapter = CustomAutoCompleteAdapter(
            this,
            android.R.layout.simple_dropdown_item_1line,
            activityList
        )
        binding.activityEt.setAdapter(activityAdapter)
    }


    override fun subscribeToEvents(vm: AddSignInVM) {
        binding.vm = vm
        vm.clickEvents.observe(this) {
            when (it) {
                AddSignInClickEvents.ON_CAMERA_CLICK ->{
                    pickMediaHelper.showDialog()
                }
                AddSignInClickEvents.ON_SAVE_CLICK -> {
                    finish()
                }
                AddSignInClickEvents.ON_CANCEL_CLICK ->{
                    finish()
                }
            }
        }
    }

    @SuppressLint("MissingPermission")
    private fun requestCurrentLocation(){
        if( PermissionUtils.hasLocationPermissions(this)){
            fusedLocationClient.lastLocation
                .addOnSuccessListener { location: Location? ->
                    location?.let {
                       var locationString =  location?.latitude.toString() +" "+location?.longitude.toString()
                        binding.locationString = locationString
                    } ?: run {
                        Toast.makeText(this, "Unable to get location", Toast.LENGTH_SHORT).show()
                    }
                }

        }else{
            requestLocationPermission()
        }
    }

    private fun requestLocationPermission() {
        when {
            PermissionUtils.hasLocationPermissions(this) ->{
                requestCurrentLocation()
            }
            else -> {
                requestPermissionLauncher.launch(PermissionUtils.LOCATION_PERMISSIONS)
            }
        }
    }

    private val requestPermissionLauncher = registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
        permissions.entries.forEach { entry ->
            val permissionName = entry.key
            val isGranted = entry.value
            if (isGranted) {
                requestCurrentLocation()
            } else {
                showLocationPermissionDialog()
            }
        }
    }

    private fun showLocationPermissionDialog() {
        alertDialogShow(this,
            getString(R.string.warning_gps_needed),
            getString(R.string.warning_location_permission),
            getString(R.string.go_to_settings),
            okLister = DialogInterface.OnClickListener { p0, p1 ->
                Utils.openAppSettings(this)
            },
            canelLister = DialogInterface.OnClickListener { p0, p1 ->
            }
        )
    }

}
// Shikhar@Atvantiq