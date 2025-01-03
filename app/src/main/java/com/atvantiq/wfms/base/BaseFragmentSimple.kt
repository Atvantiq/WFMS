package com.atvantiq.wfms.base

import android.app.Activity
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.location.LocationManager
import android.os.Bundle
import android.provider.Settings
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import com.atvantiq.wfms.R
import com.atvantiq.wfms.ui.dialogs.ProgressCircularDialog
import com.google.android.material.snackbar.Snackbar


open class BaseFragmentSimple : Fragment() {

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
	}

	fun showSnackbar(view: View, messsage: Int) {
		Snackbar.make(view, getString(messsage), Snackbar.LENGTH_SHORT).show()
	}
	
	fun showSnackbar(view: View, messsage: String) {
		Snackbar.make(view, messsage, Snackbar.LENGTH_SHORT).show()
	}
	
	
	fun alertDialogShow(context: Context, message: String) {
		val builder = AlertDialog.Builder(context)
		builder.setMessage(message)
		builder.setPositiveButton(getString(R.string.ok)) { dialogInterface, i -> dialogInterface.dismiss() }
		val alertDialog = builder.create()
		alertDialog.show()
	}
	
	fun alertDialogShow(context: Context, title: String, message: String) {
		val builder = AlertDialog.Builder(context)
		builder.setMessage(message)
		builder.setTitle(title)
		builder.setPositiveButton(getString(R.string.ok)) { dialogInterface, i -> dialogInterface.dismiss() }
		val alertDialog = builder.create()
		alertDialog.show()
	}
	
	fun alertDialogShow(
		context: Context,
		title: String,
		message: String,
		okLister: DialogInterface.OnClickListener
	) {
		val builder = AlertDialog.Builder(context)
		builder.setTitle(title)
		builder.setMessage(message)
		builder.setPositiveButton(getString(R.string.ok), okLister)
		val alertDialog = builder.create()
		alertDialog.show()
	}
	
	fun alertDialogShow(
		context: Context,
		message: String,
		okLister: DialogInterface.OnClickListener
	) {
		val builder = AlertDialog.Builder(context)
		builder.setMessage(message)
		builder.setPositiveButton(getString(R.string.ok), okLister)
		val alertDialog = builder.create()
		alertDialog.show()
	}
	
	fun alertDialogShow(
		context: Context,
		title: String,
		message: String,
		okButtonTitle: String,
		okLister: DialogInterface.OnClickListener
	) {
		val builder = AlertDialog.Builder(context)
		builder.setMessage(message)
		builder.setTitle(title)
		builder.setPositiveButton(okButtonTitle, okLister)
		val alertDialog = builder.create()
		alertDialog.show()
	}
	
	
	fun alertDialogShow(
		context: Context,
		title: String,
		message: String,
		okButtonTitle: String,
		okLister: DialogInterface.OnClickListener,
		canelLister: DialogInterface.OnClickListener
	) {
		val builder = AlertDialog.Builder(context)
		builder.setMessage(message)
		builder.setTitle(title)
		builder.setPositiveButton(okButtonTitle, okLister)
		builder.setNegativeButton(getString(R.string.cancel), canelLister)
		val alertDialog = builder.create()
		alertDialog.show()
	}
	
	fun showToast(context: Context, message: String) {
		Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
	}
	
	fun setItemsVisibility(menu: Menu, exception: MenuItem, visible: Boolean?) {
		for (i in 0 until menu.size()) {
			val item = menu.getItem(i)
			if (item !== exception) {
				item.isVisible = visible!!
			}
		}
	}
	
	fun hideKeyboard(activity: Activity) {
		val inputManager = activity
			.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
		
		// check if no view has focus:
		val currentFocusedView = activity.currentFocus
		if (currentFocusedView != null) {
			inputManager.hideSoftInputFromWindow(
				currentFocusedView.windowToken,
				InputMethodManager.HIDE_NOT_ALWAYS
			)
		}
	}
	
	lateinit var progressCirluarDialog: ProgressCircularDialog

	
	fun showCircularProgress() {
		progressCirluarDialog = ProgressCircularDialog()
		progressCirluarDialog.show(requireFragmentManager(), progressCirluarDialog.tag)
		
	}
	
	fun dismissCircularProgress() {
		if (progressCirluarDialog != null) {
			progressCirluarDialog.dismiss()
		}
	}
	
	fun isLifeCycleResumed() =
		viewLifecycleOwner.lifecycle.currentState == Lifecycle.State.RESUMED
}
